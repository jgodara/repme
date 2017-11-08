package jgodara.repme.common.dataaccess;

import java.util.Iterator;
import java.util.Vector;

import org.hibernate.Session;
import org.hibernate.Transaction;

import jgodara.repme.Glob;

/**
 * 
 * This class provide the method to bind and unbind the session
 * <p>
 * The recommended manner to use this class is as follows. 
 * <p>
 * <pre>
 *  try {                                      
 *       CustomSessionContext.bindSession();     
 *       CustomSessionContext.beginTransaction();
 *       .....                                   
 *       CustomSessionContext.commit();          
 *  }catch(Exception ex){                       
 *       CustomSessionContext.rollback();        
 *       ....                                    
 *  }                                          
 *  finally {                                  
 *       CustomSessionContext.unBindSession(); 
 *  }                                          
 * </pre>
 * 
 * <p>
 * CustomSessionContext support nested session and transaction. It means even
 * the session and transaction has been started in the parent class or method,
 * the new session and transaction could be started by invoking method
 * bindSession(true).
 * 
 * @author J.Godara
 *
 */
public class CustomSessionContext {
	
	private static final ThreadLocal session = new ThreadLocal();
	
	/**
	 * Default constructor
	 *
	 */
	protected CustomSessionContext() {
		
	}
	
	/**
	 * This method is put in the filter to check if there is 
	 * a session leak in our code. If it detect a open session,
	 * this method will close that session and return true.
	 * 
	 * @return
	 *     true: at least one issue of session context for the 
	 *     current thread has been found.
	 *     false: the session context for the current thread has 
	 *     been close correctly.
	 */
	public static boolean destroy() {
		boolean suspiciousState = false;
		SessionHolderManager sessionHolderManager 
					= (SessionHolderManager) session.get();
		
		if (sessionHolderManager == null) return false;
		
		if (sessionHolderManager.getSessionHolders().size()<1) {
			session.set(null);
			return false;
		}
		
		for(Iterator iter=sessionHolderManager.getSessionHolders().iterator();
			iter.hasNext(); ) {
			SessionHolder sessionHolder = (SessionHolder)iter.next();
			if (sessionHolder != null) {
				if (sessionHolder.destroy()) suspiciousState = true;
			}
		}
		
		session.set(null);
		
		return suspiciousState;
	}
	
	/**
	 * Bind a session to the current thread.
	 * <p>
	 * If no session is in use, a new session will be created for the current
	 * thread. Otherwise, the thread will stay with the current session.
	 *
	 */
	public static void bindSession() {
		bindSession(false);
	}
	
	/**
	 * bind a session to the current Thread
	 * 
	 *@param isNewSession If true, the new session will be created for the current
	 *thread, and the old session will be back after this new session is unbind.
	 *If false, the current session will stay the same.
	 */
	public static void bindSession(boolean isNewSession)
	{	
		SessionHolder sessionHolder = null;
		if (isNewSession || !isBind()) {
			sessionHolder = createSessionHolder();
		} 
		else {
			sessionHolder = getSessionHolder();
		}
		sessionHolder.bindSession();
	}
	
	/**
	 * 
	 * @return
	 *   a new <code>SessionHolder</code> for the new session
	 */
	private static SessionHolder createSessionHolder() {
		SessionHolderManager sessionHolderManager = null;
		CustomSessionContext context = new CustomSessionContext();
		Object obj = session.get();
		if (obj == null) {
			sessionHolderManager = context.new SessionHolderManager();
			session.set(sessionHolderManager);
		}
		else
			sessionHolderManager = (SessionHolderManager) obj;
		
		SessionHolder sessionHolder = context.new SessionHolder();
		sessionHolderManager.add(sessionHolder);
		
		return sessionHolder;
	}
	
	/**
	 * 
	 * @return
	 * 	the current <code>SessionHolder</code>
	 */
	private static SessionHolder getSessionHolder() {
		
		SessionHolderManager sessionHolderManager = (SessionHolderManager) session.get();
		
		return sessionHolderManager!=null?
				sessionHolderManager.currentSessionHolder():null;
	}
	
	/**
	 * unbind a session from the current thread
	 * 
	 * This method assume the session has been binded. If not , the 
	 * <code>NullPointerException</code> will be thrown.
	 */
	public static void unbindSession()
	{	
		SessionHolderManager sessionHolderManager = (SessionHolderManager) session.get();
		//without bind, sessionHolderManager is null 
		
		if (sessionHolderManager.currentSessionHolder().unbindSession() &&
			sessionHolderManager.remove())
			session.set(null);
	}
	
	/**
	 * begin the transaction with binded session.
	 *
	 * only be used after session is binded to the thread
	 */
	public static void beginTransaction()
	{
		getSessionHolder().beginTransaction();
	}
	
	/**
	 * commit the transaction with binded session.
	 * 
	 * only be used after session is binded to the thread
	 */
	public static void commit()
	{
		getSessionHolder().commit();
	}
	
	/**
	 * rollback the transaction with binded session.
	 * 
	 * only be used after session is binded to the thread
	 */
	public static void rollback()
	{
		getSessionHolder().rollback();
	}
	
	/**
	 * check if the session is binded with this thread.
	 * @return
	 *  true, the session is binded. false, no session is binded
	 */
	public static boolean isBind()
	{
		Session sess = currentSession();
		
		return sess!=null && sess.isOpen();
	}
	
	/**
	 * retrieve the binded session.
	 * 
	 * @return
	 * the current session
	 */
	public static Session currentSession()
	{
		SessionHolder sessionHolder = getSessionHolder();
		return sessionHolder!=null?sessionHolder.currentSession():null;
	}
	
	/**
	 * Hold a session for nested session binded
	 * 
	 */
	private class SessionHolder {
		private int sessionLevel=-1;
		
		private int transactionLevel=-1;
		
		private Session session;
		
		private Transaction tx;
		
		/**
		 * used for session leak in the feature
		 */
		private long timestamp = 0;
		
		/**
		 * Only <code>transactionLevel</code> is 0, the transaction in
		 * this session will be started. otherwise, just ignore it.
		 *
		 */
		private void beginTransaction() {
			transactionLevel++;
			if (transactionLevel==0)
				tx = session.beginTransaction();
		}
		
		/**
		 * Only if <code>transactionLevel</code> is 0, will the transaction in
		 * this session be commited. otherwise, just ignore it.
		 *
		 */
		private void commit() {
			if (transactionLevel==0) {
				session.flush();
				tx.commit();
			}
			if (transactionLevel>=0)
				transactionLevel --;
		}
		
		private void rollback() {
			tx.rollback();
			transactionLevel = -1;
		}
		/**
		 * Only if <code>sessionLevel</code> is 0, will the session be open. 
		 * otherwise, just ignore it.
		 *
		 *@return 
		 *   the current session
		 */
		private Session bindSession() {
			sessionLevel++;
			if (sessionLevel==0) {
				session = Glob.getHibernateSessionFactory().openSession();
				timestamp = System.currentTimeMillis();
			}
			
			return session;
		}
		
		/**
		 * unbind the session if the current level equal the level
		 * in which the session is created.
		 * 
		 * @return
		 *    true, session is closed. false, session is still open.
		 */
		private boolean unbindSession()
		{	
			if (0==sessionLevel) {
				if (transactionLevel>-1) {//transaction not finished
					rollback();
					transactionLevel = -1;
				}
				if (session!=null) session.close();
			}
			
			sessionLevel--;
			
			return sessionLevel==-1;
		}
		private Session currentSession() {
			return session;
		}
		
		/**
		 * Close the session holder if it is not closed .
		 *   
		 * @return
		 *     true: at least one issue of session holder for the 
		 *     current session holder has been found.
		 *     false: this session holder for has been close correctly.
		 */
		private boolean destroy() {
			
			boolean suspiciousState = false;
			//transaction still open
			if (tx!=null && tx.isActive()) {
				tx.rollback();
				suspiciousState = true;
			}
			
			// session still open
			if (session!=null && session.isOpen()) {
				session.close();
				suspiciousState = true;
			}
			
			// session level doest not come back to initial state
			if (sessionLevel!= -1) {
				sessionLevel = -1;
				suspiciousState = true;
			}
			
			//transaction level does not come back to inital state
			if (transactionLevel!= -1) {
				transactionLevel = -1;
				suspiciousState = true;
			}
			
			return suspiciousState;
		}
	}
	
	/**
	 * Manage the multiple <code>SessionHolder</code>
	 * 
	 * When the {@link CustomSessionContext#bindSession(boolean)} is invoked,
	 * a new <code>SessionHolder</code> is added in if it is the new session 
	 * and it will be current session.
	 * When the {@link CustomSessionContext#unbindSession()} is invoked, 
	 * the current <code>SessionHolder</code> is removed if the current session
	 * has been closed.
	 * 
	 */
	private class SessionHolderManager {
		
		private Vector sessionHolders = new Vector();
		
		private int currentLevel = -1;
		
		public SessionHolder currentSessionHolder() {
			return (SessionHolder) sessionHolders.get(currentLevel);
		}
		
		/**
		 * remove the current <code>SessionHolder</code> from the
		 * <code>SessionHolderManager</code> 
		 * @return
		 *   <ul>
		 *   	<li>true - no more session holder in manager</li>
		 *      <li>false - SessionHolderManager still has SessionHolder</li>
		 *   </ul>
		 */
		private boolean remove() {
			sessionHolders.remove(currentLevel);
			currentLevel--;
			return currentLevel==-1;
		}
		
		private void add(SessionHolder sessionHolder) {
			currentLevel++;
			sessionHolders.add(currentLevel, sessionHolder);
		}
		
		private Vector getSessionHolders() {
			return sessionHolders;
		}
	}
	
}
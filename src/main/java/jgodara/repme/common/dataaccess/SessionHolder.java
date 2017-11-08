package jgodara.repme.common.dataaccess;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.Transaction;

import jgodara.repme.Glob;

public class SessionHolder {
	
	private static final Logger logger = Logger.getLogger(SessionHolder.class);
	
	private static Map sessionHolder = Collections.synchronizedMap(new HashMap());
	
	public static Session currentSession() {
		MySession mysession = 
			(MySession) sessionHolder.get(Thread.currentThread().getName());
		
		if (mysession != null && mysession.session.isOpen()) {
			mysession.accessCount ++;
			mysession.lastAcessTime = System.currentTimeMillis();
			return mysession.session;
		}
		else
			return createMySession();
	}
	
	public static void closeSession() {
		MySession mysession = 
			(MySession) sessionHolder.get(Thread.currentThread().getName());
		
		if (mysession != null) {
			sessionHolder.remove(mysession.threadName);			
			mysession.session.close();
		}
	}

	private static long timeout = 1000*60*5;
	
	public static void closeTimeOutSession() {
		long currTime = System.currentTimeMillis();
		try {
			Collection sessionInUse = sessionHolder.values();
			for (Iterator iter=sessionInUse.iterator(); iter.hasNext();) {
				MySession mysession = (MySession) iter.next();
				if (currTime - mysession.lastAcessTime > timeout) {
					
					Transaction tx = mysession.session.getTransaction();
					if (tx!=null && tx.isActive())
						tx.rollback();
					mysession.session.close();
					 
					sessionHolder.remove(mysession.threadName);
					
					StringBuffer out = new StringBuffer("TIME OUT: Terminate the Session!");
		
					out.append("  Create Time:").append(new Date(mysession.createTime));
					out.append("  Last Access Time:").append(new Date(mysession.lastAcessTime));
					out.append("  Access Count:").append(mysession.accessCount);
					out.append("  Hold By Thread:").append(mysession.threadName);
					
					logger.error(out.toString());
				}
			}
		}
		catch (Exception ex) {
			
		}
	}
	public static void showDetail() {
		try {
			logger.info("======================================Session Holder Detail==============================================");
			logger.info("= Index             Create Time                  Last Access Time      Count Status    Hold thread Name");
			int idx = 0;
			Collection sessionInUse = sessionHolder.values();
			for (Iterator iter=sessionInUse.iterator(); iter.hasNext();) {
				MySession mysession = (MySession) iter.next();
				idx ++;
				StringBuffer out = new StringBuffer();
				out.append("=   ").append(idx);
				out.append("    ").append(new Date(mysession.createTime));
				out.append("    ").append(new Date(mysession.lastAcessTime));
				out.append("    ").append(mysession.accessCount);
				out.append("    ").append((mysession.session.isOpen()?"OPEN":"CLOSE"));
				out.append("    ").append(mysession.threadName);
				
				logger.info(out.toString());
			}
			logger.info("=============================================End==========================================================");
		} catch (Exception ex) {
			
		}
	}
	
	private static Session createMySession() {
		SessionHolder sh = new SessionHolder();
		
		MySession mysession = sh.new MySession();
		mysession.createTime = System.currentTimeMillis();
		mysession.lastAcessTime = mysession.createTime;
		mysession.accessCount = 1;
		mysession.threadName = Thread.currentThread().getName();
		mysession.session = Glob.getHibernateSessionFactory().openSession();
		
		sessionHolder.put(mysession.threadName, mysession);
		
		return mysession.session;
	}
	
	private class MySession {
		private long createTime;
		
		private long accessCount;
		
		private Session session;
		
		private String threadName;
		
		private long lastAcessTime;
	}


}
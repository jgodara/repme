package jgodara.repme.common.dataaccess;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.context.internal.ManagedSessionContext;

import jgodara.repme.Glob;
import jgodara.repme.common.exceptions.DAOException;

public class DefaultDAO {
	/**
	 * Access point to hibernate session factory.
	 * 
	 * @return session object
	 * @throws HibernateException
	 *             error in getting session
	 */
	protected static Session getCurrentSession() throws HibernateException {

		SessionFactory sf = Glob.getHibernateSessionFactory();
		if (CustomSessionContext.isBind()) {
			return CustomSessionContext.currentSession();
		} else if (ManagedSessionContext.hasBind(sf)) {
			return sf.getCurrentSession();
		} else {
			return SessionHolder.currentSession();
		}
	}

	/**
	 * session.flush(); Close hibernate session. Method must invoke after any
	 * hibernate action. It works simular close() method of JDBC driver.
	 * 
	 * @throws HibernateException
	 *             error in session close
	 */
	protected static void closeSession() throws HibernateException {
		if (!ManagedSessionContext.hasBind(Glob.getHibernateSessionFactory()) && !CustomSessionContext.isBind()) {
			SessionHolder.closeSession();
		}
	}

	public Object load(Class domainClass, Serializable id) throws DAOException {

		Object result = null;
		try {
			if (id != null)
				result = getCurrentSession().load(domainClass, id);

		} catch (HibernateException e) {
			throw new DAOException(e);
		} finally {
			closeSession();
		}

		return result;
	}

	public List list(String listHqlClause) throws DAOException {

		try {
			Query query = getCurrentSession().createQuery(listHqlClause).setCacheable(true);
			return query.list();

		} catch (HibernateException e) {
			throw new DAOException(e);
		} finally {
			closeSession();
		}
	}

	public List listAndClose(String listHqlClause) throws DAOException {

		try {
			Query query = getCurrentSession().createQuery(listHqlClause).setCacheable(true);
			return query.list();

		} catch (HibernateException e) {
			throw new DAOException(e);
		} finally {
			closeSession();
		}
	}

	public List listAndClose(String listHqlClause, Map<String, Object> parameters) throws DAOException {
		try {
			Query query = getCurrentSession().createQuery(listHqlClause).setCacheable(true);
			for (Iterator<String> iterator = parameters.keySet().iterator(); iterator.hasNext();) {
				String name = iterator.next();
				Object value = parameters.get(name);
				query.setParameter(name, value);
			}
			return query.list();
		} catch (HibernateException e) {
			throw new DAOException(e);
		} finally {
			closeSession();
		}
	}

	public List list(String listHqlClause, int firstResult, int maxResults) throws DAOException {

		try {
			Query query = getCurrentSession().createQuery(listHqlClause).setCacheable(true);
			query.setFirstResult(firstResult);
			query.setMaxResults(maxResults);
			return query.list();
		} catch (HibernateException e) {
			throw new DAOException(e);
		} finally {
			closeSession();
		}
	}

	public List listAndClose(String listHqlClause, int firstResult, int maxResults) throws DAOException {

		try {
			Query query = getCurrentSession().createQuery(listHqlClause).setCacheable(true);
			query.setFirstResult(firstResult);
			query.setMaxResults(maxResults);
			return query.list();

		} catch (HibernateException e) {
			throw new DAOException(e);
		} finally {
			closeSession();
		}
	}

	public void save(Object domain) throws DAOException {
		Transaction tx = null;
		try {
			if (CustomSessionContext.isBind())
				CustomSessionContext.beginTransaction();
			else
				tx = getCurrentSession().beginTransaction();
			getCurrentSession().save(domain);
			getCurrentSession().flush();
			if (CustomSessionContext.isBind())
				CustomSessionContext.commit();
			else
				tx.commit();
		} catch (HibernateException e) {
			if (tx != null)
				tx.rollback();
			if (CustomSessionContext.isBind())
				CustomSessionContext.rollback();
			throw new DAOException(e);
		} finally {
			closeSession();
		}
	}

	public void excuteUpdate(String hql) throws DAOException {

		try {
			getCurrentSession().createQuery(hql).executeUpdate();
			getCurrentSession().flush();
		} catch (HibernateException e) {
			throw new DAOException(e);
		} finally {
			closeSession();
		}
	}

	public void update(Object domain) throws DAOException {
		Transaction tx = null;
		try {
			if (CustomSessionContext.isBind())
				CustomSessionContext.beginTransaction();
			else
				tx = getCurrentSession().beginTransaction();
			getCurrentSession().update(domain);
			getCurrentSession().flush();
			if (CustomSessionContext.isBind())
				CustomSessionContext.commit();
			else
				tx.commit();
		} catch (HibernateException e) {
			if (tx != null)
				tx.rollback();
			if (CustomSessionContext.isBind())
				CustomSessionContext.rollback();
			throw new DAOException(e);
		} finally {
			closeSession();
		}
	}

	public void saveOrUpdate(Object domain) throws DAOException {
		Transaction tx = null;
		try {
			if (CustomSessionContext.isBind())
				CustomSessionContext.beginTransaction();
			else
				tx = getCurrentSession().beginTransaction();
			getCurrentSession().saveOrUpdate(domain);
			getCurrentSession().flush();

			if (CustomSessionContext.isBind())
				CustomSessionContext.commit();
			else
				tx.commit();
		} catch (HibernateException e) {
			if (tx != null)
				tx.rollback();
			if (CustomSessionContext.isBind())
				CustomSessionContext.rollback();
			throw new DAOException(e);
		} finally {
			closeSession();
		}
	}

	public void delete(Object domain) throws DAOException {

		try {
			getCurrentSession().delete(domain);
			getCurrentSession().flush();
		} catch (HibernateException e) {
			throw new DAOException(e);
		} finally {
			closeSession();
		}
	}

	public List evictQuery(String sql) throws DAOException {

		try {
			Query query = getCurrentSession().createQuery(sql);
			List list = query.list();
			if (sql.trim().substring(0, 4).equalsIgnoreCase("from")) {
				for (int i = 0; list != null && i < list.size(); i++) {
					getCurrentSession().evict(list.get(i));
				}
			}
			return list;
		} catch (HibernateException e) {
			throw new DAOException(e);
		} finally {
			closeSession();
		}
	}

	/**
	 * A new method to prevent SQL inject
	 * 
	 * @param sql
	 * @param parmeters
	 * @return
	 * @throws DAOException
	 */
	public List evictQuery(String sql, Map<String, Object> parmeters) throws DAOException {
		try {
			Query query = getCurrentSession().createQuery(sql);
			if (parmeters != null) {
				for (String key : parmeters.keySet()) {
					query.setParameter(key, parmeters.get(key));
				}
			}

			List list = query.list();
			if (sql.trim().substring(0, 4).equalsIgnoreCase("from")) {
				for (int i = 0; list != null && i < list.size(); i++) {
					getCurrentSession().evict(list.get(i));
				}
			}
			return list;
		} catch (HibernateException e) {
			throw new DAOException(e);
		} finally {
			closeSession();
		}
	}

	public Object get(Class theClass, Serializable id) throws DAOException {

		try {
			return getCurrentSession().get(theClass, id);
		} catch (HibernateException e) {
			throw new DAOException(e);
		} finally {
			closeSession(); // close session
		}
	}

	public Object evictGet(Class theClass, Serializable id) throws DAOException {

		try {
			Object obj = getCurrentSession().get(theClass, id);
			if (obj != null)
				getCurrentSession().evict(obj);
			return obj;
		} catch (HibernateException e) {
			throw new DAOException(e);
		} finally {
			closeSession(); // close session
		}
	}

	public void beginwork() {
		Transaction tx = getCurrentSession().getTransaction();
		if (tx == null)
			tx = getCurrentSession().beginTransaction();
		else if (!tx.isActive()) {
			tx.begin();
		}
	}

	public void commitwork() {
		Transaction tx = getCurrentSession().getTransaction();
		if (tx != null)
			tx.commit();
		getCurrentSession().flush();
		closeSession();
	}

	public void rollback() {
		try {
			Transaction tx = getCurrentSession().getTransaction();
			if (tx != null)
				tx.rollback();
			closeSession();
		} catch (Exception e) {
		}
	}

	public Object merge(Object domain) throws DAOException {
		Transaction tx = null;
		Object o = null;
		try {
			tx = getCurrentSession().beginTransaction();
			o = getCurrentSession().merge(domain);
			tx.commit();
		} catch (HibernateException e) {
			throw new DAOException(e);
		} finally {
			closeSession(); // close session
		}
		return o;
	}

	public void batchInserts(List listDomains) throws DAOException {
		Transaction tx = null;
		if (CustomSessionContext.isBind())
			CustomSessionContext.beginTransaction();
		else
			tx = getCurrentSession().beginTransaction();

		for (int index = 0; index < listDomains.size(); index++) {
			Object domain = listDomains.get(index);
			getCurrentSession().save(domain);
		}
		try {
			getCurrentSession().flush();
			getCurrentSession().clear();
			if (CustomSessionContext.isBind())
				CustomSessionContext.commit();
			else
				tx.commit();
		} catch (HibernateException e) {
			if (tx != null)
				tx.rollback();
			if (CustomSessionContext.isBind())
				CustomSessionContext.rollback();
			throw new DAOException(e);
		} finally {
			closeSession();
		}
	}

}

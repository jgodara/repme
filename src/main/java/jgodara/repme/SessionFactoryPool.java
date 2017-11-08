package jgodara.repme;

import java.io.File;
import java.net.URL;
import java.util.Hashtable;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

/**
 * @author JGodara
 *
 */
public class SessionFactoryPool {
	
	private static final Log logger = LogFactory.getLog(SessionFactoryPool.class);
	
	private static SessionFactory liveSessionFactory = null;
	
	private static Map sessionFactoryReferenceCount = new Hashtable();
	
	public synchronized static SessionFactory getLiveSessionFactory() {
		
		try {
			
			if (liveSessionFactory == null) {
				logger.info("Building Hibernate LIVE Session Factory...");
				
				liveSessionFactory = buildSessionFactory(getHbmFiles(), Glob.dburl(), Glob.dbuser(), Glob.dbpass(), Glob.dbdriver(), null);
				
				logger.info("LIVE Hibernate Session Factory is created!"+liveSessionFactory);
			}
			
			addSessionFactoryReferenceCount(liveSessionFactory);
		} catch (Exception ex) {
			logger.error("TradeHelper SessionFactoryPool Error: "+ex.getMessage(), ex);
		}
		
		return liveSessionFactory;
	}
	
	/*
	 * =====================================
	 * 		BEGIN UTLITY FUNCTIONS
	 * =====================================	
	 */
	protected static SessionFactory buildSessionFactory(File[] hbmfiles, String dburl, String dbuser, String dbpassword, String dbdriver, String treecacheConfigFile) 
			throws Exception{
		for (int i=0; i<hbmfiles.length; i++)
			logger.info("Hibernate Mapping File Path:"+hbmfiles[i].getPath());
		logger.info("Hibernate Using JDBC, dburl:"+dburl+" dbuser:"+dbuser+" dbdriver:"+dbdriver);

		Configuration hibernateConfig = new Configuration();
		for (int i=0; i<hbmfiles.length; i++)
			hibernateConfig = addHbmFile(hibernateConfig, hbmfiles[i]);

//		TODO: Add the audit interceptors
//		AuditInterceptor auditInterceptor = new AuditInterceptor();
//		addDefaultInterceptor(hibernateConfig, auditInterceptor);
//		addEventListeners(hibernateConfig);
		
		if (treecacheConfigFile != null){
			hibernateConfig.setProperty("hibernate.cache.configuration_file", treecacheConfigFile);
			logger.info("hibernate.cache.configuration_file:"+treecacheConfigFile);
		}
		
		hibernateConfig.setProperty("hibernate.connection.url", dburl);
		hibernateConfig.setProperty("hibernate.connection.username", dbuser);
		hibernateConfig.setProperty("hibernate.connection.password", dbpassword);
		hibernateConfig.setProperty("hibernate.connection.driver_class", dbdriver);
		hibernateConfig.setProperty("dialect", "org.hibernate.dialect.MySQLDialect");
		
		
		SessionFactory sessionFactory = hibernateConfig.buildSessionFactory();
		//TODO:set a separate session factory
//		auditInterceptor.setSessionFactory(stagingSessionFactory);
		
		return sessionFactory;

	}
	
	private static Configuration addHbmFile(Configuration configuration, File f) 
			throws Exception {
		
		if (!f.isDirectory())
			throw new Exception("Hibernate Mapping File folder is missing!!"+f);
		
		File[] fs = f.listFiles();
		
		for (int i=0; i<fs.length; i++)
		{
			if (fs[i].isFile() && fs[i].getName().indexOf("hbm.xml")>0)		configuration.addFile(fs[i]);
		}
		
		return configuration;
	}
	
	protected static File[] getHbmFiles() throws Exception {
		
		URL url = SessionFactoryPool.class.getResource("/hibernate");		
		File f = new File(url.toURI());
		
		if (!f.isDirectory())
			throw new Exception("Hibernate mapping files cannot be found! "+f.getPath());
		
		return new File[]{f};
		
	}

	
	private static void addSessionFactoryReferenceCount(SessionFactory sf) {
		if (sf == null) return ;
		
		Integer count = (Integer) sessionFactoryReferenceCount.get(sf);
		
		if (count == null)
			sessionFactoryReferenceCount.put(sf, new Integer(1));
		else
			sessionFactoryReferenceCount.put(sf, new Integer(count.intValue()+1));
	}

}
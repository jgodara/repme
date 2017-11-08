package jgodara.repme;

import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.SessionFactory;

/**
 * 
 * @author Jay Godara
 * 
 * Glob holds all the static content TradeHelper needs to 
 * process stuff. If the content is not just loaded yet, the Glob
 * loads it and stores it for later uses.
 */
public class Glob {

	protected final static Log logger = LogFactory.getLog(Glob.class);	
	
	/*
	 * ====================================================
	 * 			GLOB VARIABLES
	 * ====================================================
	 */
	private static boolean notLoadedYet 				= true;
	private static final String sep						= java.io.File.separator;
	private static String fname							= null;
	
	/*
	 * ====================================================
	 * 			STATIC VARIABLES
	 * ====================================================
	 */
	private static String rootpath						= "";
	private static String dbhost						= "";
	private static String host							= "";
	private static String contenthost					= "";
	private static String serverprefix					= "";
	private static String appname						= "";
	private static String dburl							= "";
	private static String dbuser						= "";
	private static String dbpass						= "";
	private static String dbdriver						= "";
	private static String dataSource				= "";
	private static String defualtLanguageForAdmin		= "en";
	
	private static String steamOpenIDUrl 				= "http://steamcommunity.com/openid";
	private static String steamAPIUrl					= "http://api.steampowered.com";
	private static String steamAPIVersion				= "v0002";
	
	private static List<String> adminSupportLanguage	= new ArrayList<String>();	
	
	private static DataSource ds_live					= null;
	private static Context ctx							= null;
	/*
	 * ======================================================
	 * 			PUBLIC CONSTANTS
	 * ======================================================
	 */
	public static final String STEAM_API_KEY 			= "97CBBCF11EBE2E7783F63CE7A623DD49"; 
	
	/*
	 * =======================================================
	 * 			GLOB METHODS TO GET STATIC CONTENT
	 * =======================================================
	 */
	public static String rootpath() {
		checkLoadedYet();
		return rootpath;
	}
	
	public static String host() {
		checkLoadedYet();
		return host;
	}
	
	public static String contentHost() {
		checkLoadedYet();
		return contenthost;
	}
	
	public static String serverPrefix() {
		checkLoadedYet();
		return serverprefix;
	}
	
	public static String appname() {
		checkLoadedYet();
		return appname;
	}
	
	public static String dburl() {
		checkLoadedYet();
		return dburl;
	}
	
	public static String dbuser() {
		checkLoadedYet();
		return dbuser;
	}
	
	public static String dbpass() {
		checkLoadedYet();
		return dbpass;
	}
	
	public static String dbdriver() {
		checkLoadedYet();
		return dbdriver;
	}
	
	public static String fname() {
		checkLoadedYet();
		return fname;
	}
	
	public static String defaultLanguageForAdmin() {
		return defualtLanguageForAdmin;
	}
	
	public static String steamOpenIDURL() {
		checkLoadedYet();
		return steamOpenIDUrl;
	}
	
	public static String steamAPIUrl() {
		checkLoadedYet();
		return steamAPIUrl;
	}
	
	public static String steamAPIVersion() {
		checkLoadedYet();
		return steamAPIVersion;
	}
	
	public static DataSource getLiveDataSource() {
		checkLoadedYet();
		return ds_live;
	}
	
	public static List<String> adminSupportLanguage() {
		checkLoadedYet();
		return adminSupportLanguage;
	}
	
	public static String dataSource(){
		checkLoadedYet();
		return dataSource;
	}
	
	public static String dbhost() {
		checkLoadedYet();
		return dbhost;
	}
	
	/*
	 * ======================================================
	 * 			GLOB UTILITY MEHTODS
	 * ======================================================
	 */
	private static void checkLoadedYet() {
		if (notLoadedYet) {
			notLoadedYet = loadFromFile();
		}
	}
	
	private static boolean loadFromFile() {
		
		InputStream fstream = null;
		
		try {			
			Method method = null;
			method = Thread.class.getMethod("getContextClassLoader", new Class[]{});
			ClassLoader classLoader = (ClassLoader) method.invoke(Thread.currentThread(), new Object[]{});
			
			fstream = classLoader.getResourceAsStream("RepMe.properties");
			
			if (fstream != null)
				fname = "RepMe.properties";
			
			Properties properties = new Properties();
			properties.load(fstream);
			
			readApplicationProperties(properties);
			
			fstream.close();
			
			ctx = new InitialContext();
			
			try {
				ds_live = (DataSource) ctx.lookup(dataSource);
			} catch (Exception ex) {
				logger.warn("Live DataSource cannot be found!");
			}
			
		} catch (Exception ex) {
			logger.error("RM203: Error opening TradeHelper.properties!", ex);
		}
		
		return false;
	}
	
	private static void readApplicationProperties(Properties props) {
		
		logger.info("Start Reading Application Proeprties...");
		
		rootpath			= props.getProperty("rootpath", rootpath).trim();
		host				= props.getProperty("host", host).trim();
		contenthost			= props.getProperty("contenthost", contenthost).trim();
		serverprefix		= props.getProperty("serverprefix", serverprefix).trim();
		appname				= props.getProperty("appname", appname).trim();
		dburl				= props.getProperty("dburl", dburl).trim();
		dbuser				= props.getProperty("dbuser", dbuser).trim();
		dbpass				= props.getProperty("dbpass", dbpass).trim();
		dbdriver			= props.getProperty("dbdriver", dbdriver).trim();
		dataSource			= props.getProperty("dataSourceLive", dataSource).trim();
		
		steamOpenIDUrl		= props.getProperty("steamOpenIDUrl", steamOpenIDUrl).trim();
		steamAPIUrl			= props.getProperty("steamAPIUrl", steamAPIUrl);
		steamAPIVersion		= props.getProperty("steamAPIVersion", steamAPIVersion);
		
		dbhost				= props.getProperty("dbhost", dbhost);
		
		String [] supportedLanguages = props.getProperty("supportedLanguage", "en,es").split(",");
		for (String language : supportedLanguages) {
			adminSupportLanguage.add(language.trim());
		}
		
		logger.info("Done reading application properties...");
		
	}

	public static SessionFactory getHibernateSessionFactory() {	
		return SessionFactoryPool.getLiveSessionFactory();
	}
	
	public static String getHomeUrl() {
		return "ROOT".equals(appname()) ? "/" : "/" + appname() + "/";
	}
	
}
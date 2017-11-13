package jgodara.repme.key;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import jgodara.repme.Glob;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class KeyGenerator {
	protected static final Log logger = LogFactory.getLog(KeyGenerator.class);

	private static Map KeyMap = new HashMap();

	public static long nextval(String table) throws Exception {
		return getKeyClass(table.toUpperCase()).nextval();
	}

	protected static KeyClass getKeyClass(String table) {
		return (KeyClass) (KeyMap.get(table) == null ? initKeyClass(table)
				: KeyMap.get(table));
	}

	protected static synchronized KeyClass initKeyClass(String table) {
		KeyClass key = (KeyClass) KeyMap.get(table);

		if (key != null)
			return key;

		key = new KeyClass(table);

		KeyMap.put(table, key);

		return key;
	}

	/**
	 * initial key class and put it to key map, used in HibernateKeyGenerator
	 * class
	 * 
	 * @param table
	 * @param batchsize
	 * @param highLevelRatio
	 * @param selectMaxQuery
	 */
	public static void addKeyMap(String table, int batchsize,
			int highLevelRatio, String selectMaxQuery) {
		KeyMap.put(table, new KeyClass(table, batchsize, highLevelRatio,
				selectMaxQuery));
	}

	/**
	 * synchronize the primary key info between the primary key of each table of
	 * database and the base key of table KeyGenerator
	 */
	public static synchronized void syncKeyInfo() {
		Connection[] conns = new Connection[2];
		try {

			Class.forName(Glob.dbdriver()).newInstance();
			conns[0] = java.sql.DriverManager.getConnection(Glob.dburl(),
					Glob.dbuser(), Glob.dbpass());

			conns[0].setAutoCommit(false);

			if (Glob.getLiveDataSource() != null) {
				conns[1] = Glob.getLiveDataSource().getConnection();
				if (conns[1] == null && conns[1].isClosed()) {
					conns[1].setAutoCommit(false);
				}
			}

			syncKeyInfo(conns);
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			for (int i = 0; i < conns.length; i++) {
				if (conns[i] != null)
					try {
						conns[i].close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
			}
		}

	}

	/**
	 * synchronize the primary key info between the primary key of each table of
	 * database and the base key of table KeyGenerator
	 */
	public static synchronized void syncKeyInfo(String table) {
		Connection[] conns = new Connection[2];
		try {

			if (Glob.getLiveDataSource() != null) {
				conns[1] = Glob.getLiveDataSource().getConnection();
				if (conns[1] == null && conns[1].isClosed()) {
					conns[1].setAutoCommit(false);
				}
			}

			syncKeyInfo(conns, table);
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			for (int i = 0; i < conns.length; i++) {
				if (conns[i] != null)
					try {
						conns[i].close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
			}
		}

	}

	/**
	 * Recalculate the key of all tables
	 */
	public static void recalculateKey() {
		Connection[] conns = null;
		try {
			conns = getConnections();
			Iterator it = KeyMap.entrySet().iterator();

			while (it.hasNext()) {
				Entry entry = (Entry) it.next();

				KeyClass key = (KeyClass) entry.getValue();
				syncKeyInfo(conns, key);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			for (int i = 0; i < conns.length; i++) {
				if (conns[i] != null)
					try {
						conns[i].close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
			}
		}

	}

	/**
	 * Get Connections against both staging and live database
	 * 
	 * @return
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public static Connection[] getConnections() throws InstantiationException,
			IllegalAccessException, ClassNotFoundException, SQLException {
		Connection[] conns = new Connection[2];

		Class.forName(Glob.dbdriver()).newInstance();
		conns[0] = java.sql.DriverManager.getConnection(Glob.dburl(),
				Glob.dbuser(), Glob.dbpass());

		conns[0].setAutoCommit(false);

		if (Glob.getLiveDataSource() != null) {
			conns[1] = Glob.getLiveDataSource().getConnection();
			if (conns[1] == null && conns[1].isClosed()) {
				conns[1].setAutoCommit(false);
			}
		}

		return conns;
	}

	/**
	 * synchronize the primary key info between the primary key of each table of
	 * database and the base key of table KeyGenerator
	 * 
	 * @param conn
	 *            database connection of live database or staging database which
	 *            is used .
	 * @throws Exception
	 */
	public static synchronized void syncKeyInfo(Connection[] conns)
			throws Exception {
		Iterator it = KeyMap.entrySet().iterator();

		while (it.hasNext()) {
			Entry entry = (Entry) it.next();

			KeyClass key = (KeyClass) entry.getValue();

			if (!KeyGeneratorThread.retrieveKey(key, conns[0])) {
				long maxKey = getMaxKeyByCountRows(conns,
						key.getSelectMaxQuery());
				key.basekey = maxKey;
				KeyGeneratorThread.saveKey(key, conns[0]);
			}
		}
	}

	/**
	 * synchronize the primary key info between the primary key of each table of
	 * database and the base key of table KeyGenerator
	 * 
	 * @param conn
	 *            database connection of live database or staging database which
	 *            is used .
	 * @throws Exception
	 */
	public static synchronized void syncKeyInfo(Connection[] conns, String table)
			throws Exception {
		KeyClass key = (KeyClass) KeyMap.get(("`" + table + "`").toUpperCase());
		if (key == null)
			return;

		long maxKey = getMaxKeyByCountRows(conns, key.getSelectMaxQuery());
		key.basekey = maxKey;
		KeyGeneratorThread.updateKey(key, conns[0]);
	}

	public static synchronized void syncKeyInfo(Connection[] conns, KeyClass key)
			throws Exception {
		long maxKey = getMaxKeyByCountRows(conns, key.getSelectMaxQuery());
		key.basekey = maxKey;
		KeyGeneratorThread.updateKey(key, conns[0]);
	}

	protected static long getMaxKeyByCountRows(Connection[] conns, String query) {
		long maxKey = 1;

		for (int i = 0; i < conns.length; i++) {
			long tmp = 0;

			if (conns[i] == null)
				continue;
			PreparedStatement ps = null;
			try {
				ps = conns[i].prepareStatement(query);
				ResultSet rs = ps.executeQuery();
				if (rs != null) {
					if (rs.next())
						tmp = rs.getLong(1);

					rs.close();
				}
				ps.close();
			} catch (Exception ex) {
				if (ps != null)
					try {
						ps.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				logger.warn("Execute Query error:" + ex + " " + query);
			}
			if (tmp > maxKey)
				maxKey = tmp;
		}

		return maxKey;
	}

}
package jgodara.repme.key;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import jgodara.repme.Glob;

public class KeyGeneratorThread implements Runnable {

	/**
	 * create table KeyGenerator ( name varchar2(50), count numeric(4), level
	 * numeric(3), basekey numeric(30), primary key (name) );
	 * 
	 * create index keygenerator_idx on KeyGenerator(name);
	 */

	public static Connection keydbconn = null;

	public static final String KeyTableName = "keygenerator";

	private KeyClass key;

	/**
	 * update the database info of the key
	 * 
	 * @param key
	 */
	public KeyGeneratorThread(KeyClass key) {
		this.key = key;
	}

	public void run() {
		try {
			generateKey();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public Map initKeyMap() {
		return null;
	}

	public synchronized void generateKey() throws Exception {
		Connection conn = null;

		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = getConnection();
			ps = conn.prepareStatement(getKeyQueryWithLock(key.table));
			rs = ps.executeQuery();
			if (rs != null) {
				if (rs.next()) {
					key.operateKey(rs.getInt(1), rs.getInt(2), rs.getLong(3),
							KeyClass.setKey);
				} else {
					rs.close();
					ps.close();
					throw new SQLException(
							"can not found the key info of the table "
									+ key.table);
				}
				rs.close();
			} else {
				throw new SQLException(
						"can not found the key info of the table " + key.table);
			}
			ps.close();

		} catch (Exception ex) {
			key.setInProcess(false);
			if (conn != null)
				conn.rollback();
			if (ps != null)
				ps.close();
			if (conn != null)
				closeConnection(conn);
			throw ex;
		}

		try {
			if (key.count == 0)
				ps = conn
						.prepareStatement("UPDATE keygenerator SET basekey = basekey+1 WHERE name='"
								+ key.table + "'");
			else
				ps = conn
						.prepareStatement("UPDATE keygenerator SET basekey = basekey+count WHERE name='"
								+ key.table + "'");
			ps.executeUpdate();

			conn.commit();
		} catch (Exception ex) {
			conn.rollback();
			throw ex;
		} finally {
			key.setInProcess(false);
			ps.close();
			closeConnection(conn);
		}

	}

	private static Connection getConnection() throws InstantiationException,
			IllegalAccessException, ClassNotFoundException, SQLException {

		if (keydbconn == null || keydbconn.isClosed()) {
			Class.forName(Glob.dbdriver()).newInstance();
			keydbconn = java.sql.DriverManager.getConnection(Glob.dburl(),
					Glob.dbuser(), Glob.dbpass());
		}
		keydbconn.setAutoCommit(false);
		return keydbconn;
	}

	private static void closeConnection(Connection conn) throws SQLException {
		conn.close();
	}

	public static boolean retrieveKey(KeyClass key, Connection conn)
			throws Exception {
		boolean flag = true;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			ps = conn.prepareStatement(getKeyQueryWithLock(key.table));
			rs = ps.executeQuery();
			if (rs != null) {
				if (rs.next()) {
					key.count = rs.getInt(1);
					key.level = rs.getInt(2);
					key.basekey = rs.getLong(3);
				} else {
					flag = false;
				}
				rs.close();
			} else {
				flag = false;
			}
			ps.close();
			if (flag) {
				ps = conn
						.prepareStatement("UPDATE keygenerator SET basekey = basekey+count WHERE name='"
								+ key.table + "'");
				ps.executeUpdate();
			}
			conn.commit();
		} catch (Exception ex) {
			ex.printStackTrace();
			conn.rollback();
			throw ex;
		} finally {
			if (ps != null)
				ps.close();
		}

		return flag;
	}

	public static void saveKey(KeyClass key, Connection conn) throws Exception {

		PreparedStatement ps = null;
		try {
			ps = conn
					.prepareStatement("INSERT INTO keygenerator(name, count, ratio, basekey) VALUES( ?, ?, ?, ?)");
			ps.setString(1, key.table);
			ps.setInt(2, key.count);
			ps.setInt(3, key.level);
			ps.setLong(4, key.basekey + key.count);

			ps.executeUpdate();
			conn.commit();
		} catch (Exception ex) {
			conn.rollback();
			throw ex;
		} finally {
			if (ps != null)
				ps.close();
		}
	}

	/**
	 * Update key class against the database.
	 * 
	 * @param key
	 *            key class
	 * @param conn
	 *            staging connection
	 * @throws Exception
	 */
	public static void updateKey(KeyClass key, Connection conn)
			throws Exception {
		PreparedStatement ps = null;
		try {
			ps = conn
					.prepareStatement("UPDATE keygenerator SET basekey = ? WHERE name=?");
			ps.setString(2, key.table);
			ps.setLong(1, key.basekey + key.count);
			ps.executeUpdate();
			conn.commit();
		} catch (Exception ex) {
			conn.rollback();
			throw ex;
		} finally {
			if (ps != null)
				ps.close();
		}
	}

	protected static String getKeyQueryWithLock(String name) {
		return "SELECT count, ratio, basekey FROM keygenerator WHERE name='"
				+ name + "' for update";
	}

}

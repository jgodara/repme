package jgodara.repme.steam.businessobject;

import org.apache.log4j.Logger;

import jgodara.repme.common.dataaccess.DefaultDAO;
import jgodara.repme.common.exceptions.DAOException;
import jgodara.repme.steam.model.SteamUser;

public class SteamClientBO extends DefaultDAO {
	
	private static final Logger logger = Logger.getLogger(SteamClientBO.class);

	public SteamUser getUserBySteamid32(String steamid) {
		try {
			return (SteamUser) super.get(SteamUser.class, steamid);
		} catch (DAOException e) {
			logger.error("Cannot retrieve steam client", e);
			return null;
		}
	}

	public void saveOrUpdate(SteamUser user) {
		try {
			super.saveOrUpdate(user);
		} catch (Exception e) {
			logger.error("Cannot update steam client", e);
		}
	}

}

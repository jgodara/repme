package jgodara.repme.businessobject;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import jgodara.repme.common.dataaccess.DefaultDAO;
import jgodara.repme.common.exceptions.DAOException;
import jgodara.repme.model.Vouches;

public class VouchesBO extends DefaultDAO {
	
	private static final Logger logger = Logger.getLogger(VouchesBO.class);
	
	public List<Vouches> getRecentVouches() {
		try {
			return super.list("FROM " + Vouches.class.getName(), 0, 15);
		} catch (DAOException ex) {
			logger.error("Cannot get recent vouches from the database.", ex);
			return new ArrayList<Vouches>();
		}
	}
	
	public Vouches getVouchByVouchId(Long vouchId) {
		try {
			return (Vouches) super.get(Vouches.class, vouchId);
		} catch (DAOException ex) {
			logger.error("Cannot get vouch post " + vouchId + " from database", ex);
			return null;
		}
	}

}

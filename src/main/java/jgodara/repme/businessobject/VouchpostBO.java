package jgodara.repme.businessobject;

import java.util.List;

import org.apache.log4j.Logger;

import jgodara.repme.common.dataaccess.DefaultDAO;
import jgodara.repme.common.exceptions.DAOException;
import jgodara.repme.model.Vouchers;

public class VouchpostBO extends DefaultDAO {
	
	private static final Logger logger = Logger.getLogger(VouchpostBO.class);
	
	public List<Vouchers> getVouchersList(Long vouchPostId) {
		try {
			return list("FROM " + Vouchers.class.getName() + " WHERE vouchid=" + vouchPostId);
		} catch (DAOException ex) {
			logger.error("Cannot retreive voucher list", ex);
			return null;
		}
	}

}

package jgodara.repme.businessaction;

import java.util.List;

import org.apache.log4j.Logger;

import jgodara.repme.businessobject.VouchpostBO;
import jgodara.repme.datatransfer.VouchpostDTO;
import jgodara.repme.model.Vouchers;

public class VouchpostBA {

	private static final Logger logger = Logger.getLogger(VouchpostBA.class);
	
	private VouchpostBO vouchpostBO;

	public VouchpostDTO getVouchPost(Long vouchPostId) {
		List<Vouchers> vouchers = vouchpostBO.getVouchersList(vouchPostId);
		if (vouchers == null)
			return null;
		
		VouchpostDTO dto = new VouchpostDTO();
		
		for (Vouchers voucher : vouchers) {
			try {
				dto.setVouchedAmount(dto.getVouchedAmount() + voucher.getVouchAmount());
				dto.getVouchers().put(voucher.getVoucherSteamId(), voucher.getVouchAmount());
			} catch (Exception ex) {
				logger.error("Cannot create steam client.", ex);
			}
		}
		
		return dto;
	}

	public void setVouchpostBO(VouchpostBO vouchpostBO) {
		this.vouchpostBO = vouchpostBO;
	}

}

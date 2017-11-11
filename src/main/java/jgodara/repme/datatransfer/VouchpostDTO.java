package jgodara.repme.datatransfer;

import java.util.HashMap;
import java.util.Map;

public class VouchpostDTO {
	
	private Map<String, Float> vouchers = new HashMap<String, Float>();
	private float vouchedAmount = 0.0f;

	public Map<String, Float> getVouchers() {
		return vouchers;
	}

	public void setVouchers(Map<String, Float> vouchers) {
		this.vouchers = vouchers;
	}

	public float getVouchedAmount() {
		return vouchedAmount;
	}

	public void setVouchedAmount(float vouchedAmount) {
		this.vouchedAmount = vouchedAmount;
	}

}

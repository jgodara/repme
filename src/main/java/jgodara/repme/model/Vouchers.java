package jgodara.repme.model;

public class Vouchers {
	
	private long voucherid;
	private long vouchid;
	private String voucherSteamId;
	private float vouchAmount;

	public long getVoucherid() {
		return voucherid;
	}

	public void setVoucherid(long voucherid) {
		this.voucherid = voucherid;
	}

	public long getVouchid() {
		return vouchid;
	}

	public void setVouchid(long vouchid) {
		this.vouchid = vouchid;
	}

	public String getVoucherSteamId() {
		return voucherSteamId;
	}

	public void setVoucherSteamId(String voucherSteamId) {
		this.voucherSteamId = voucherSteamId;
	}

	public float getVouchAmount() {
		return vouchAmount;
	}

	public void setVouchAmount(float vouchAmount) {
		this.vouchAmount = vouchAmount;
	}

}

package jgodara.repme.model;

import java.util.Date;

public class Vouches {

	public static final int UOM_KEYS = 1;
	public static final int UOM_USD = 2;

	private long vouchid;
	private String ownerid;
	private String evictid;
	private int uom;
	private float amount;
	private Date createTime;

	public long getVouchid() {
		return vouchid;
	}

	public void setVouchid(long vouchid) {
		this.vouchid = vouchid;
	}

	public String getOwnerid() {
		return ownerid;
	}

	public void setOwnerid(String ownerid) {
		this.ownerid = ownerid;
	}

	public String getEvictid() {
		return evictid;
	}

	public void setEvictid(String evictid) {
		this.evictid = evictid;
	}

	public int getUom() {
		return uom;
	}

	public void setUom(int uom) {
		this.uom = uom;
	}

	public float getAmount() {
		return amount;
	}

	public void setAmount(float amount) {
		this.amount = amount;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

}

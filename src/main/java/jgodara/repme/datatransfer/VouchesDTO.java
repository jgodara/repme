package jgodara.repme.datatransfer;

import java.util.Date;

import jgodara.repme.steam.model.SteamUser;

public class VouchesDTO {

	private long vouchid;
	private SteamUser owner;
	private SteamUser evictor;
	private int uom;
	private float amount;
	private Date createTime;

	public long getVouchid() {
		return vouchid;
	}

	public void setVouchid(long vouchid) {
		this.vouchid = vouchid;
	}

	public SteamUser getOwner() {
		return owner;
	}

	public void setOwner(SteamUser owner) {
		this.owner = owner;
	}

	public SteamUser getEvictor() {
		return evictor;
	}

	public void setEvictor(SteamUser evictor) {
		this.evictor = evictor;
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

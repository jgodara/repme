package jgodara.repme.model;

public class Reps {

	private int repid;
	private String owner;
	private String evictor;
	private boolean positive;

	public int getRepid() {
		return repid;
	}

	public void setRepid(int repid) {
		this.repid = repid;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public String getEvictor() {
		return evictor;
	}

	public void setEvictor(String evictor) {
		this.evictor = evictor;
	}

	public boolean isPositive() {
		return positive;
	}

	public void setPositive(boolean positive) {
		this.positive = positive;
	}
	
}

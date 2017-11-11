package jgodara.repme.model;

public class Badges {
	
	private long badgeid;
	private String steamid32;
	private String code;
	private String name;

	public long getBadgeid() {
		return badgeid;
	}

	public void setBadgeid(long badgeid) {
		this.badgeid = badgeid;
	}

	public String getSteamid32() {
		return steamid32;
	}

	public void setSteamid32(String steamid32) {
		this.steamid32 = steamid32;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}

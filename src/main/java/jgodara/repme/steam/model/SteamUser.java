package jgodara.repme.steam.model;

public class SteamUser {

	private String steamid32;
	private String name;
	private String imageUrl;
	private int level;

	public String getSteamid32() {
		return steamid32;
	}

	public void setSteamid32(String steamid32) {
		this.steamid32 = steamid32;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

}

package jgodara.repme.steam.datatransfer;

import java.util.ArrayList;
import java.util.List;

import jgodara.repme.model.Badges;

public class SteamClientDTO {

	private String steamid32;
	private String name;
	private String imageUrl;
	private String fullImage;
	private String mediumImage;
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

	public String getFullImage() {
		return fullImage;
	}

	public void setFullImage(String fullImage) {
		this.fullImage = fullImage;
	}

	public String getMediumImage() {
		return mediumImage;
	}

	public void setMediumImage(String mediumImage) {
		this.mediumImage = mediumImage;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

}

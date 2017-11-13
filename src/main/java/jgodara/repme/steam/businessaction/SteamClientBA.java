package jgodara.repme.steam.businessaction;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.log4j.Logger;

import jgodara.repme.Glob;
import jgodara.repme.common.exceptions.DAOException;
import jgodara.repme.model.Badges;
import jgodara.repme.model.Reps;
import jgodara.repme.steam.businessobject.SteamClientBO;
import jgodara.repme.steam.datatransfer.SteamClientDTO;
import jgodara.repme.steam.model.SteamUser;
import net.sf.json.JSONObject;

public class SteamClientBA {

	private static final Logger logger = Logger.getLogger(SteamClientBA.class);

	public static final String STEAM_OWNER_STEAMID_REGEX = "%owner_steamid%";
	public static final String STEAM_ITEM_ASSETID_REGEX = "%assetid%";

	@SuppressWarnings("deprecation")
	private HttpClient client = new DefaultHttpClient();
	
	private SteamClientBO steamClientBO;

	public SteamClientDTO getSteamUser(String steamid) throws ClientProtocolException, IOException {

		SteamUser steamUser = null;
		try {
			steamUser = steamClientBO.getUserBySteamid32(steamid.substring(steamid.lastIndexOf('/') + 1));
			if (steamUser == null) {
				steamUser = new SteamUser();

				JSONObject userDetails = doRequest(Glob.steamAPIUrl() + "/ISteamUser/GetPlayerSummaries/"
						+ Glob.steamAPIVersion() + "/?key=" + Glob.STEAM_API_KEY + "&steamids=" + steamid);
				
				String name = ((JSONObject) userDetails.getJSONObject("response").getJSONArray("players").get(0))
						.getString("personaname");
				steamid = ((JSONObject) userDetails.getJSONObject("response").getJSONArray("players").get(0))
						.getString("steamid");
				String imageUrl = ((JSONObject) userDetails.getJSONObject("response").getJSONArray("players").get(0))
						.getString("avatar");

				steamUser.setImageUrl(imageUrl);
				steamUser.setSteamid32(steamid);
				steamUser.setName(name);
				
				userDetails = doRequest(Glob.steamAPIUrl() + "/IPlayerService/GetSteamLevel/v1"
						+ "/?key=" + Glob.STEAM_API_KEY + "&steamid=" + steamid);
				
				int level = ((JSONObject) userDetails.getJSONObject("response")).getInt("player_level");
				
				steamUser.setLevel(level);
				
				logger.info("User " + steamid + " was not found. Creating.");
				steamClientBO.saveOrUpdate(steamUser);
			}
		} catch (Exception e) {
			logger.error(e);
		}
		
		SteamClientDTO dto = new SteamClientDTO();
		dto.setSteamid32(steamUser.getSteamid32());
		dto.setLevel(steamUser.getLevel());
		dto.setName(steamUser.getName());
		dto.setFullImage(toImageUrl(steamUser.getImageUrl(), "full"));
		dto.setMediumImage(toImageUrl(steamUser.getImageUrl(), "medium"));

		return dto;
	}
	
	public long getScore(String steamid32) {
		long score = 0;
		List<Reps> reps = getReps(steamid32);
		for (Reps rep : reps) {
			if (rep.isPositive())
				score++;
			else
				score--;
		}
		return score;
	}
	
	@SuppressWarnings("unchecked")
	public List<Reps> getReps(String steamid32) {
		List<Reps> reps = new ArrayList<Reps>();
		try {
			reps = steamClientBO.list("FROM " + Reps.class.getName() + " WHERE evictor='" + steamid32 + "'");
		} catch (DAOException ex) {
			logger.error("Cannot get user reputation: " + steamid32, ex);
		}
		return reps;
	}
	
	@SuppressWarnings("unchecked")
	public List<Badges> getBadges(String steamid32) {
		List<Badges> badges = new ArrayList<Badges>();
		try {
			badges = steamClientBO.list("FROM " + Badges.class.getName() + " WHERE steamid32='" + steamid32 + "'");
		} catch (DAOException ex) {
			logger.error("Cannot get badges: " + steamid32, ex);
		}
		return badges;
	}
	
	public void addRep(String userid, String steamid32, boolean positive) throws DAOException {
		Reps rep = new Reps();
		rep.setOwner(userid);
		rep.setEvictor(steamid32);
		rep.setPositive(positive);
		
		steamClientBO.save(rep);
	}
	
	private String toImageUrl(String imageUrl, String ext) {
		return imageUrl.substring(0, imageUrl.lastIndexOf('.')) + "_" + ext + ".jpg";
	}

	private JSONObject doRequest(String url) throws ClientProtocolException, IOException {
		HttpGet request = new HttpGet(url);

		request.addHeader("Content-Type", "application/json; charset=utf-8");
		HttpResponse response = client.execute(request);

		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
		StringBuffer result = new StringBuffer();
		String line = "";
		while ((line = bufferedReader.readLine()) != null) {
			result.append(line);
		}

		JSONObject jsonObject = JSONObject.fromObject(result.toString());
		return jsonObject;
	}
	
	public void setSteamClientBO(SteamClientBO steamClientBO) {
		this.steamClientBO = steamClientBO;
	}

}

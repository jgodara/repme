package jgodara.repme.steam.businessaction;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.log4j.Logger;

import jgodara.repme.Glob;
import jgodara.repme.steam.businessobject.SteamClientBO;
import jgodara.repme.steam.model.SteamUser;
import net.sf.json.JSONObject;

public class SteamClientBA {

	private static final Logger logger = Logger.getLogger(SteamClientBA.class);

	public static final String STEAM_OWNER_STEAMID_REGEX = "%owner_steamid%";
	public static final String STEAM_ITEM_ASSETID_REGEX = "%assetid%";

	@SuppressWarnings("deprecation")
	private HttpClient client = new DefaultHttpClient();
	
	private SteamClientBO steamClientBO;

	public SteamUser getSteamUser(String steamid) throws ClientProtocolException, IOException {

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
		
		steamUser.setFullImage(toImageUrl(steamUser.getImageUrl(), "full"));
		steamUser.setMediumImage(toImageUrl(steamUser.getImageUrl(), "medium"));

		return steamUser;
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

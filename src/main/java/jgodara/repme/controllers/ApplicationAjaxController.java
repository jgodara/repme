package jgodara.repme.controllers;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jgodara.repme.SessionNameResolver;
import jgodara.repme.beans.SessionDetailsBean;
import jgodara.repme.businessaction.VouchesBA;
import jgodara.repme.businessaction.VouchpostBA;
import jgodara.repme.datatransfer.VouchesDTO;
import jgodara.repme.datatransfer.VouchpostDTO;
import jgodara.repme.model.Badges;
import jgodara.repme.model.Reps;
import jgodara.repme.model.Vouches;
import jgodara.repme.steam.businessaction.SteamClientBA;
import jgodara.repme.steam.datatransfer.SteamClientDTO;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class ApplicationAjaxController {
	
	private static final Logger logger = Logger.getLogger(ApplicationAjaxController.class);
	
	@Autowired
	private VouchpostBA vouchpostBA;
	
	@Autowired
	private VouchesBA vouchesBA;
	
	@Autowired
	private SteamClientBA steamClientBA;
	
	@RequestMapping(value = "/ajax/vouches/{vouchId}", method = RequestMethod.GET)
	public void asyncVouchPost(HttpServletRequest request, HttpServletResponse response, @PathVariable Long vouchId) throws IOException {
		
		JSONObject json = new JSONObject();
		
		try {
			VouchpostDTO dto = vouchpostBA.getVouchPost(vouchId);	
			json.accumulate("success", true);
			json.accumulate("amtDone", dto.getVouchedAmount());
			
			JSONArray vouchers = new JSONArray();
			for (String steamid32 : dto.getVouchers().keySet()) {
				JSONObject voucher = new JSONObject();
				SteamClientDTO steamUser = steamClientBA.getSteamUser(steamid32);
				voucher.accumulate("steamid32", steamUser.getSteamid32());
				voucher.accumulate("imageUrl", steamUser.getImageUrl());
				voucher.accumulate("name", steamUser.getName());
				voucher.accumulate("vouched", dto.getVouchers().get(steamid32));
				vouchers.add(voucher);
			}
			
			json.accumulate("vouchers", vouchers);
		} catch (Exception ex) {
			logger.error("Cannot process vouchpost " + vouchId, ex);
			
			json.accumulate("success", false);
		}
		
		response.setContentType("application/json");
		response.getWriter().write(json.toString());
	}
	
	@RequestMapping(value = "/ajax/user/{steamid32}", method = RequestMethod.GET)
	public void asyncUserService(HttpServletRequest request, HttpServletResponse response, @PathVariable String steamid32) throws IOException {
		
		JSONObject json = new JSONObject();
		
		try {
			SteamClientDTO steamUser = steamClientBA.getSteamUser(steamid32);
			json.accumulate("success", true);
			json.accumulate("steamid32", steamid32);
			json.accumulate("name", steamUser.getName());
			json.accumulate("level", steamUser.getLevel());
			json.accumulate("image", steamUser.getImageUrl());
			json.accumulate("largeimage", steamUser.getFullImage());
			json.accumulate("mediumimage", steamUser.getMediumImage());
			json.accumulate("score", steamClientBA.getScore(steamid32));
			
			JSONArray badges = new JSONArray();
			for (Badges badge : steamClientBA.getBadges(steamid32)) {
				JSONObject bObj = new JSONObject();
				bObj.accumulate("code", badge.getCode());
				bObj.accumulate("name", badge.getName());
				
				badges.add(bObj);
			}
			
			json.accumulate("badges", badges);
		} catch (Exception ex) {
			logger.error("Cannot process userservice " + steamid32, ex);
			
			json.accumulate("success", false);
		}
		
		response.setContentType("application/json");
		response.getWriter().write(json.toString());
	}
	
	@RequestMapping(value = "/ajax/vouches/post", method = RequestMethod.POST)
	public void submitVouch(HttpServletRequest request, HttpServletResponse response) throws IOException {
		
		JSONObject json = new JSONObject();
		
		Long vouchid = Long.parseLong(request.getParameter("vouchid"));
		try {
			SessionDetailsBean sdb = (SessionDetailsBean) request.getSession().getAttribute(SessionNameResolver.getSessionDetails());
			if (sdb == null) {				
				json.accumulate("success", false);
				json.accumulate("message", "You've been logged out. Please login again.");
			} else {
				if (vouchpostBA.alreadyVouched(sdb.getUserId(), vouchid)) {
					json.accumulate("success", false);
					json.accumulate("message", "You have already vouched for this post.");
				} else {
					Float vouchedFor = Float.parseFloat(request.getParameter("amt"));
					VouchesDTO v = vouchesBA.getVouchPost(vouchid);
					if (v.getEvictor().getSteamid32().equals(sdb.getUserId())) {
						json.accumulate("success", false);
						json.accumulate("message", "You cannot vouch for yourself.");
					} else {
						Float vouchAmt = v.getAmount();
						Float vouchedAmt = vouchpostBA.getVouchPost(vouchid).getVouchedAmount();
						Float required  = (vouchAmt - vouchedAmt);
						
						if (vouchedFor > required) {
							json.accumulate("success", false);
							json.accumulate("message", "You can only vouch till the extent of " + (v.getUom() == Vouches.UOM_KEYS ? required + " keys" : "$" + required) + ".");
						} else {
							vouchpostBA.processVouch(sdb.getUserId(), vouchid, vouchedFor);
							json.accumulate("success", true);
						}
					}
				}
			}
		} catch (Exception ex) {
			logger.error("Cannot process vouchservice " + vouchid, ex);
			
			json.accumulate("success", false);
			json.accumulate("message", "Internal Server Error.");
		}
		
		response.setContentType("application/json");
		response.getWriter().write(json.toString());
	}
	
	@RequestMapping(value = "/ajax/user/post", method = RequestMethod.POST)
	public void submitRep(HttpServletRequest request, HttpServletResponse response) throws IOException {
		
		JSONObject json = new JSONObject();
		
		String userid = request.getParameter("userid");
		try {
			SessionDetailsBean sdb = (SessionDetailsBean) request.getSession().getAttribute(SessionNameResolver.getSessionDetails());
			if (sdb == null) {				
				json.accumulate("success", false);
				json.accumulate("message", "You've been logged out. Please login again.");
			} else {
				if (userid.equals(sdb.getUserId())) {
					json.accumulate("success", false);
					json.accumulate("message", "You can't do this on your own profile.");
				} else {
					boolean repped = false;
					List<Reps> reps = steamClientBA.getReps(userid);
					for (Reps rep : reps) {
						if (rep.getEvictor().equals(userid)) {
							repped = true;
							break;
						}
					}
					
					if (repped) {
						json.accumulate("success", false);
						json.accumulate("message", "You already gave your feedback for this profile.");
					} else {
						Byte type = Byte.parseByte(request.getParameter("type"));
						steamClientBA.addRep(sdb.getUserId(), userid, type == 1);
						json.accumulate("success", true);
					}
				}
			}
		} catch (Exception ex) {
			logger.error("Cannot process repservice " + userid, ex);
			
			json.accumulate("success", false);
			json.accumulate("message", "Internal Server Error.");
		}
		
		response.setContentType("application/json");
		response.getWriter().write(json.toString());
	}

}

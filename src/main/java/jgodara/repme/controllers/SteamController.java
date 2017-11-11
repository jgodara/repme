package jgodara.repme.controllers;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import jgodara.repme.SessionNameResolver;
import jgodara.repme.beans.SessionDetailsBean;
import jgodara.repme.steam.businessaction.SteamClientBA;
import jgodara.repme.steam.datatransfer.SteamClientDTO;

@Controller
public class SteamController {

	private static final Logger logger = Logger.getLogger(SteamController.class);
	
	@Autowired
	private SteamClientBA steamClientBA;

	@RequestMapping(value = "/steamauthreturn", method = RequestMethod.GET)
	public ModelAndView steamAuthReturn(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView redirectView = new ModelAndView("steamAuthReturn");

		@SuppressWarnings("unchecked")
		Map<String, String> identifier = (Map<String, String>) request.getAttribute(SessionNameResolver.getIdentifier());
		try {
			if (identifier != null) {
				logger.debug("OpenID Verified: " + identifier);

				// Create session details from the SteamID
				SteamClientDTO steamUser = steamClientBA.getSteamUser(identifier.get("identity"));
				if (steamUser != null) {
					SessionDetailsBean sdb = new SessionDetailsBean();
					sdb.setLoggedIn(true);
					sdb.setUserName(steamUser.getName());
					sdb.setUserId(steamUser.getSteamid32());
					
					request.getSession().setAttribute(SessionNameResolver.getSessionDetails(), sdb);
				} else {
					throw new Exception("Steam login failed due to previous errors.");
				}

				logger.info("Session details bound.");
			}
		} catch (Exception ex) {
			logger.error("Error in OpenID Verification: " + ex.getMessage(), ex);
		}

		return redirectView;
	}
	
	@RequestMapping(value = "/steamLogout", method = RequestMethod.GET)
	public ModelAndView steamLogout(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView redirectView = new ModelAndView("redirect:/");
		
		request.getSession().setAttribute(SessionNameResolver.getSessionDetails(), null);
		
		return redirectView;
	}

}

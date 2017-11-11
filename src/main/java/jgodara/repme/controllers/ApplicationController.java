package jgodara.repme.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jgodara.repme.businessaction.VouchesBA;
import jgodara.repme.common.exceptions.PageNotFoundException;
import jgodara.repme.datatransfer.VouchesDTO;
import jgodara.repme.steam.businessaction.SteamClientBA;
import jgodara.repme.steam.datatransfer.SteamClientDTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ApplicationController {

	@Autowired
	private VouchesBA vouchesBA;

	@Autowired
	private SteamClientBA steamClientBA;

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public ModelAndView showHome(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new ModelAndView("index");

		mv.getModel().put("recentVouches", vouchesBA.getRecentVouces());

		return mv;
	}

	@RequestMapping(value = "/vouches/{vouchId}", method = RequestMethod.GET)
	public ModelAndView showVouchPage(HttpServletRequest request,
			HttpServletResponse response,
			@PathVariable(value = "vouchId") Long vouchId)
			throws PageNotFoundException {
		ModelAndView mv = new ModelAndView("vouch");

		VouchesDTO dto = vouchesBA.getVouchPost(vouchId);

		if (dto.getVouchid() == 0)
			throw new PageNotFoundException("The vouch post (" + vouchId
					+ ") doesn't exist.");

		mv.getModel().put("vouchPost", dto);

		return mv;
	}

	@RequestMapping(value = "/users/{steamid32}", method = RequestMethod.GET)
	public ModelAndView showUsersPage(HttpServletRequest request,
			HttpServletResponse response, @PathVariable String steamid32)
			throws PageNotFoundException {
		ModelAndView mv = new ModelAndView("user");

		SteamClientDTO steamClient = null;
		try {
			steamClient = steamClientBA.getSteamUser(steamid32);
		} catch (Exception ex) {
			throw new PageNotFoundException("The user (" + steamid32
					+ ") doesn't exist.");
		}

		mv.getModel().put("user", steamClient);

		return mv;
	}

}


package jgodara.repme.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jgodara.repme.businessaction.VouchesBA;

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

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public ModelAndView showHome(HttpServletRequest request,
			HttpServletResponse response) {
		ModelAndView mv = new ModelAndView("index");
		
		mv.getModel().put("recentVouches", vouchesBA.getRecentVouces());
		
		return mv;
	}
	
	@RequestMapping(value = "/vouches/{vouchId}", method = RequestMethod.GET)
	public ModelAndView showVouchPage(HttpServletRequest request, HttpServletResponse response, 
			@PathVariable(value="vouchId") Long vouchId) {
		ModelAndView mv = new ModelAndView("vouch");
		
		mv.getModel().put("vouchPost", vouchesBA.getVouchPost(vouchId));
		
		return mv;
	}

}

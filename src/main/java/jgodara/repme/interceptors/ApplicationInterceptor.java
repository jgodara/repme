package jgodara.repme.interceptors;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import jgodara.repme.SessionNameResolver;
import jgodara.repme.beans.SessionDetailsBean;

public class ApplicationInterceptor extends HandlerInterceptorAdapter {
	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		
		// Put session details in the model
		
		putSessionParametersInModel(
			modelAndView.getModel(), 
			request.getSession(), 
			SessionNameResolver.getSessionDetails()
		);
		
		super.postHandle(request, response, handler, modelAndView);
	}
	
	private void putSessionParametersInModel(Map model, HttpSession session, String ... paramNames) {
		for (String paramName : paramNames) {
			model.put("s_" + paramName, session.getAttribute(paramName));
		}
	}

}

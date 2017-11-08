package jgodara.repme.servlet;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.expressme.openid.Association;
import org.expressme.openid.Endpoint;
import org.expressme.openid.OpenIdException;
import org.expressme.openid.OpenIdManager;

import jgodara.repme.Glob;
import jgodara.repme.SessionNameResolver;
import jgodara.repme.util.StringUtil;

/**
 * @author JGodara
 */
public class OpenIDServlet extends HttpServlet {

	/**
	 * Private members.
	 */
	
	private static final long serialVersionUID 		= -5998885243419513055L;
	private static final String OPENID_ENDPOINT 	= "OPEndpoint";
	private static final String FORM_REDIRECTION	= "RedirectionJSP";
	private static final String AUTH_RETURN_URL		= "AuthReturnURL";
	
	private static final String ATTR_ALIAS = "openid_alias";
	
	private SteamOpenIdManager manager;
	
	private String opEndpoint = null;
	private String redirectionJsp = null;
	private String returnUrl = null;
	
	@Override
	public void init(ServletConfig config) throws ServletException {
		manager = new SteamOpenIdManager();
		
		//Load the parameters
		Enumeration<String> initParams = config.getInitParameterNames();
		while (initParams.hasMoreElements()) {
			String paramName = initParams.nextElement();
			if (OPENID_ENDPOINT.equals(paramName)) {
				opEndpoint = config.getInitParameter(paramName);
			} else if (FORM_REDIRECTION.equals(paramName)) {
				redirectionJsp = "/" + Glob.appname() + "/" + config.getInitParameter(paramName);
			} else if (AUTH_RETURN_URL.equals(paramName)) {
				returnUrl = "/" + config.getInitParameter(paramName);
			}
		}
		
		//Validate Data
		if (opEndpoint == null) {
			throw new ServletException(OPENID_ENDPOINT+" is a required parameter and connot be null.");
		}
		
		if (redirectionJsp == null) {
			throw new ServletException(FORM_REDIRECTION+" is a required parameter and cannot be null.");
		}
		
		if (returnUrl == null) {
			throw new ServletException(AUTH_RETURN_URL+" is a required parameter and cannot be null.");
		}
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		doPost(req, resp);
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		if ("true".equals(req.getParameter("is_return"))) {
			processReturn(req, resp);
		} else {
			authRequest(opEndpoint, req, resp);
		}
	}

	private void authRequest(String opEndpoint, HttpServletRequest req,
			HttpServletResponse resp) throws IOException {
		String realm = req.getRequestURL().toString();
		
		manager.setRealm(realm);
		manager.setReturnTo(StringUtil.appendUri(realm, "is_return=true"));
		
		Endpoint endpoint = manager.lookupEndpoint(opEndpoint);
		Association association = manager.lookupAssociation(endpoint);
		req.getSession().setAttribute(ATTR_ALIAS, endpoint.getAlias());
		String url = manager.getAuthenticationUrl(endpoint, association);
		resp.sendRedirect(url);
	}

	private void processReturn(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String alias = (String) req.getSession().getAttribute(ATTR_ALIAS);
		Map<String, String> authentication = manager.getAuthentication(req, alias);
		if (authentication == null) {
			req.getRequestDispatcher(StringUtil.appendUri(Glob.getHomeUrl(), "&message=Steam Authentication Failed!&messageType=err")).forward(req, resp);
		} else {
			req.setAttribute(SessionNameResolver.getIdentifier(), authentication);
			req.getRequestDispatcher(returnUrl).forward(req, resp);
		}
	}
	
	private class SteamOpenIdManager extends OpenIdManager {
		
		public Map<String, String> getAuthentication(HttpServletRequest request, String alias) {
			// verify:
			String identity = request.getParameter("openid.identity");
			if (identity == null)
				throw new OpenIdException("Missing 'openid.identity'.");
			String sig = request.getParameter("openid.sig");
			if (sig == null)
				throw new OpenIdException("Missing 'openid.sig'.");
			String signed = request.getParameter("openid.signed");
			if (signed == null)
				throw new OpenIdException("Missing 'openid.signed'.");
			
			String returnTo = StringUtil.appendUri(request.getRequestURL().toString(), "is_return=true");
			if (!returnTo.equals(request.getParameter("openid.return_to")))
				throw new OpenIdException("Bad 'openid.return_to'.");
			// check sig:
			String[] params = signed.split("[\\,]+");
			Map<String, String> authMap = new HashMap<String, String>();
			for (String param : params) {
				String value = request.getParameter("openid." + param);
				if (value != null)
					authMap.put(param, value);
			}
			
			return authMap;
		}
	}

}
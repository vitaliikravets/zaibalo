package ua.com.zaibalo.helper;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.hibernate.classic.Session;

import ua.com.zaibalo.constants.ZaibaloConstants;
import ua.com.zaibalo.db.DataAccessFactory;
import ua.com.zaibalo.db.HibernateUtils;
import ua.com.zaibalo.db.hibernate.HibernateUsersFacade;
import ua.com.zaibalo.model.User;

public class ServletHelper {
	public static String getClientIpAddr(HttpServletRequest request) {
		String ip = request.getHeader("X-Forwarded-For");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_CLIENT_IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_X_FORWARDED_FOR");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		return ip;
	}

	private static User getCookieUser(HttpServletRequest request, HttpServletResponse response){

		Cookie[] cookies = request.getCookies();
		if (cookies == null) {
			return null;
		}

		String userCookie = null;

		for (Cookie cookie : cookies) {
			if (cookie.getName().equals(ZaibaloConstants.ZAIBALO_USER_COOKIE_NAME)) {
				try {
					userCookie = URLDecoder.decode(cookie.getValue(), "UTF-8");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
					// NEVER GONNA HAPPEN)))
				}
			}
		}
		if (userCookie == null) {
			return null;
		}

		int colonIndex = userCookie.indexOf(":");
		if (colonIndex == -1) {
			response.addCookie(new Cookie(ZaibaloConstants.ZAIBALO_USER_COOKIE_NAME, ""));
			return null;
		}
		String userName = userCookie.substring(0, colonIndex);
		String userToken = userCookie.substring(colonIndex + 1);

		User cookieUser = null;
	
		Session sess = HibernateUtils.getSession();
		sess.beginTransaction();

		cookieUser = new HibernateUsersFacade(sess).getUserByName(userName);
		
		sess.getTransaction().commit();
		sess.close();

		if (cookieUser != null && cookieUser.getToken().equals(userToken)) {
			return cookieUser;
		}

		response.addCookie(new Cookie(ZaibaloConstants.ZAIBALO_USER_COOKIE_NAME, ""));
		return null;
	}

	public static User checkUserAuthorised(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		HttpSession session = request.getSession();

		User user = (User)session.getAttribute(ZaibaloConstants.USER_PARAM_NAME);
		if (user != null) {
			return user;
		}

		User cookieUser = ServletHelper.getCookieUser(request, response);

		if (cookieUser != null) {
			session.setAttribute(ZaibaloConstants.USER_PARAM_NAME, cookieUser);
			return cookieUser;
		}
		
		return null;
	}
	
	public static void logException(Exception exception, HttpServletRequest request){

		logMessage(exception.getMessage(), request);
		
		exception.printStackTrace();
	}
	
	public static void logMessage(String message, HttpServletRequest request){
		System.out.println();
		System.out.println("ERROR: " + message);
		System.out.println("Requested URL: " + request.getRequestURL() + ((request.getQueryString() == null) ? "" : "?" + request.getQueryString()));
		System.out.println("From IP: " + ServletHelper.getClientIpAddr(request));
		System.out.println("Parameters:");
		for(String paramKey : request.getParameterMap().keySet()){
			System.out.print(paramKey);
			System.out.print(": ");
			String[] par = request.getParameterMap().get(paramKey);
			for(String str: par){
				System.out.print(str);
			}
			System.out.println();
		}
		
		User user = (User)request.getSession().getAttribute(ZaibaloConstants.USER_PARAM_NAME);
		if(user != null){
			System.out.println("Logged in user: " + user.getLoginName() + " id: " + user.getId());
		}
	}
	
	public static void logShortErrorMessage(String message){
		System.out.println();
		System.out.println("ERROR: " + message);
	}
	
	public static void updateUnreadMessagesStatus(HttpServletRequest request){
		User user = (User)request.getSession().getAttribute(ZaibaloConstants.USER_PARAM_NAME);
		
		if(user == null){
			return;
		}
		
		DataAccessFactory factory = new DataAccessFactory(request);
		int count = factory.getMessageAccessInstance().getUnreadMessagesCount(user.getId());
		if(count != 0){			
			request.getSession().setAttribute("unreadMailCount", " [" + count + "]");
		}else{
			request.getSession().setAttribute("unreadMailCount", "");
		}
			
	}
}

package kr.co.cont.common.util;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;

public class SessionUtil {

	public static String getAttri(HttpServletRequest request, String key) {
		HttpSession session = request.getSession();
		return (String) session.getAttribute(key);
	}
	
	public static Object getAttribute(HttpServletRequest request, String key) {
		HttpSession session = request.getSession();
		return session.getAttribute(key);
	}
	
	public static void setAttribute(HttpServletRequest request, String key, Object value) {
		HttpSession session = request.getSession();
		session.setAttribute(key, value);
	}
	
	public static void removeAttribute(HttpServletRequest request, String key) {
		HttpSession session = request.getSession();
		session.removeAttribute(key);
	}
	
	public static void invalidate(HttpServletRequest request) {
		HttpSession session = request.getSession();
		session.invalidate();
	}
	
	public static boolean isLogin(HttpServletRequest request) {
		boolean isLogin = false;
		HttpSession session = request.getSession(false);
		if (session != null)
			isLogin = StringUtils.isNotEmpty((String) session.getAttribute("memberNo"));
		return isLogin;
	}
	
	public static String getId(HttpServletRequest request) {
		HttpSession session = request.getSession();
		return session.getId();
	}
	
}

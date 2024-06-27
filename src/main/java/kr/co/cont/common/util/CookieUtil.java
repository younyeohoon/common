package kr.co.cont.common.util;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import kr.co.cont.common.constants.ErrorCode;
import kr.co.cont.common.exception.BaseException;

public class CookieUtil {

	private static final String encoding = "UTF-8";
	private static final String path = "/";

	/**
	 * 특정 key의 쿠키값을 List로 반환한다.
	 * 
	 * @param request
	 * @param key 쿠키 이름
	 * @return
	 */
	public static List<String> getValueList(HttpServletRequest request, String key) {
		Cookie[] cookies = request.getCookies();
		String[] cookieValues = null;
		String value = "";
		List<String> list = null;

		// 특정 key의 쿠키값을 ","로 구분하여 String 배열에 담아준다.
		// ex) 쿠키의 key/value ---> key = "clickItems", value = "211, 223, 303"(상품 번호)
		if (cookies != null) {
			for (int i = 0; i < cookies.length; i++) {
				if (cookies[i].getName().equals(key)) {
					value = cookies[i].getValue();
					cookieValues = (decode(value, encoding)).split(",");
					break;
				}
			}
		}

		// String 배열에 담겼던 값들을 List로 다시 담는다.
		if (cookieValues != null) {
			list = new ArrayList<String>(Arrays.asList(cookieValues));

			if (list.size() > 20) { // 값이 20개를 초과하면, 최근 것 20개만 담는다.
				int start = list.size() - 20;
				List<String> copyList = new ArrayList<String>();
				for (int i = start; i < list.size(); i++) {
					copyList.add(list.get(i));
				}
				list = copyList;
			}
		}
		return list;
	}

	/**
	 * 키를 생성 혹은 업데이트한다.
	 * 
	 * @param request
	 * @param response
	 * @param key 쿠키 이름
	 * @param value 쿠키 이름과 짝을 이루는 값
	 * @param day 쿠키의 수명
	 */
	public static void setCookie(HttpServletRequest request, HttpServletResponse response, String key, String value, int day) {
		Cookie cookie = null;
		if (isExist(request, key)) {
			cookie = (Cookie) getValueMap(request).get(key);
			cookie.setValue(value);
		} else {
			cookie = new Cookie(key, encode(value, encoding));
		}
		
		cookie.setMaxAge(60 * 60 * 24 * day);
		cookie.setPath(path);
		response.addCookie(cookie);

	}
	
	/**
	 * 키를 생성 혹은 값을 추가한다.
	 * 
	 * @param request
	 * @param response
	 * @param key 쿠키 이름
	 * @param value 쿠키 이름과 짝을 이루는 값
	 * @param day 쿠키의 수명
	 */
	public static void addCookie(HttpServletRequest request, HttpServletResponse response, String key, String value, int day) {
		List<String> list = getValueList(request, key);
		String sumValue = "";
		int equalsValueCnt = 0;

		if (list != null) {
			for (int i = 0; i < list.size(); i++) {
				sumValue += list.get(i) + ",";
				if (list.get(i).equals(value)) {
					equalsValueCnt++;
				}
			}
			if (equalsValueCnt != 0) { // 같은 값을 넣으려고 할 때의 처리
				if (sumValue.substring(sumValue.length() - 1).equals(",")) {
					sumValue = sumValue.substring(0, sumValue.length() - 1);
				}
			} else {
				sumValue += value;
			}
		} else {
			sumValue = value;
		}

		if (!sumValue.equals("")) {
			Cookie cookie = new Cookie(key, encode(sumValue, encoding));
			cookie.setMaxAge(60 * 60 * 24 * day);
			cookie.setPath(path);
			response.addCookie(cookie);
		}
	}

	/**
	 * 쿠키값들 중 특정 값을 삭제한다.
	 * 
	 * @param request
	 * @param response
	 * @param key 쿠키 이름
	 * @param value 쿠키 이름과 짝을 이루는 값
	 */
	public static void deleteCookie(HttpServletRequest request, HttpServletResponse response, String key, String value) {
		List<String> list = getValueList(request, key);
		list.remove(value);

		String sumValue = "";
		if (list.size() != 0) {
			for (int i = 0; i < list.size(); i++) {
				sumValue += list.get(i) + ",";
			}
			if (sumValue.substring(sumValue.length() - 1).equals(",")) {
				sumValue = (sumValue.substring(0, sumValue.length() - 1)).replaceAll(" ", "");
			}
		}
		Cookie cookie = null;
		int time = 60 * 60 * 24 * 20;

		if (sumValue.equals("")) {
			time = 0;
		}

		cookie = new Cookie(key, encode(sumValue, encoding));
		cookie.setMaxAge(time);
		cookie.setPath(path);
		response.addCookie(cookie);
	}
	
	/**
	 * 모든 쿠키를 제거한다.
	 * 
	 * @param request
	 * @param response
	 */
	public static void removeCookie(HttpServletRequest request, HttpServletResponse response) {
		Cookie[] cookies = request.getCookies(); // 모든 쿠키 가져오기
		if (cookies != null) {
			for (int i = 0; i < cookies.length; i++) {
				cookies[i].setMaxAge(0); // 유효시간을 0으로 설정
				response.addCookie(cookies[i]); // 응답에 추가하여 만료시키기.
			}

		}
	}
	
	/**
	 * 특정 쿠리를 제거한다.
	 * 
	 * @param response
	 * @param key
	 */
	public static void removeCookie(HttpServletResponse response, String key) {
		Cookie cookie = new Cookie(key, null);
		cookie.setMaxAge(0);
		cookie.setPath(path);
		response.addCookie(cookie);
	}

	/**
	 * 일반적인 쿠키 생성
	 * 
	 * @param request
	 * @param response
	 * @param key 쿠키 이름
	 * @param value 쿠키 이름과 짝을 이루는 값
	 * @param day 쿠키의 수명
	 * @return
	 */
	public static Cookie createNewCookie(HttpServletRequest request, HttpServletResponse response
			, String key, String value, int day) {
		Cookie cookie = new Cookie(key, encode(value, encoding));
		cookie.setPath(path);
		cookie.setMaxAge(60 * 60 * 24 * day);
		response.addCookie(cookie);
		return cookie;
	}

	/**
	 * 쿠키들을 맵으로 반환한다.
	 * 
	 * @param request
	 * @return
	 */
	public static HashMap<String, Object> getValueMap(HttpServletRequest request) {
		HashMap<String, Object> cookieMap = new HashMap<>();

		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (int i = 0; i < cookies.length; i++) {
				cookieMap.put(cookies[i].getName(), cookies[i]);
			}
		}

		return cookieMap;
	}

	/**
	 * ","로 구분된 값이 아닌 단일 값으로 저장된 쿠키의 값을 반환한다.
	 * 
	 * @param request
	 * @param key 쿠키 이름
	 * @return
	 */
	public static String getValue(HttpServletRequest request, String key) {
		Cookie cookie = (Cookie) getValueMap(request).get(key);
		if (cookie == null)
			return null;
		return decode(cookie.getValue(), encoding);
	}

	/**
	 * 쿠키가 있는지 확인.
	 * 
	 * @param request
	 * @param key 쿠키 이름
	 * @return
	 */
	public static boolean isExist(HttpServletRequest request, String key) {
		return getValueMap(request).get(key) != null;
	}
	
	private static String encode(String str , String charset) {
		try {
			return URLEncoder.encode(str, charset);
		} catch (Exception e) {
			throw new BaseException(ErrorCode.REQUEST_FAILURE);
		}
	}
	
	private static String decode(String str , String charset) {
		try {
			return URLDecoder.decode(str, charset);
		} catch (Exception e) {
			throw new BaseException(ErrorCode.REQUEST_FAILURE);
		}
	}

}

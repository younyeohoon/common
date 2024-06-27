package kr.co.cont.common.filter;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;

import kr.co.cont.common.biz.base.model.BaseMap;
import kr.co.cont.common.biz.base.model.ResponseData;
import kr.co.cont.common.biz.base.service.BaseService;
import kr.co.cont.common.cache.BaseCache;
import kr.co.cont.common.constants.ErrorCode;
import kr.co.cont.common.exception.BaseException;
import kr.co.cont.common.util.DateUtil;
import kr.co.cont.common.util.SessionUtil;
import kr.co.cont.common.util.StringUtil;
import kr.co.cont.common.wrapper.BaseRequestWrapper;
import kr.co.cont.common.wrapper.BaseResponseWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
//@WebFilter(filterName = "baseFilter")
@Component("baseFilter")
public class BaseFilter implements Filter {
	
	private final BaseService transLogService;
	private final BaseCache baseCache;
	
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		if (log.isDebugEnabled()) log.debug("filterConfig = {}", filterConfig);
	}
	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;
		String uri = req.getRequestURI();
		String contextPath = req.getContextPath();
		String path = uri.substring(contextPath.length()).replaceAll("[/]+$", "/");
		String contentType = StringUtils.defaultIfBlank(req.getHeader("content-type"), "text/html");
		String responseType = StringUtils.defaultIfBlank(req.getHeader("response-type"), "");
		boolean isEncrypt = Boolean.parseBoolean(req.getHeader("isEncrypt")) ;
		
		MDC.put("path", path);
		MDC.put("memberId", SessionUtil.getAttri(req, "memberId"));
		MDC.put("memberNo", SessionUtil.getAttri(req, "memberNo"));
		MDC.put("reqTime", DateUtil.getCurrentDateString("yyyyMMddHHmmss") + StringUtil.random(3));
		MDC.put("memberIp", getRemoteAddr(req));
		
		req.setAttribute("ROOT_PATH", contextPath);
		req.setAttribute("cache", baseCache);
		
		if (log.isDebugEnabled()) log.debug("[Service Start]==========================================================================");
		
		try {
			// Wrapper
			BaseRequestWrapper requestWrapper = new BaseRequestWrapper(req);
			BaseResponseWrapper responseWrapper = new BaseResponseWrapper(res);
			
			
			boolean isLoginYn = false;
			boolean isLoggerYn = false;
			boolean isIplYn = false;
			boolean isUseYn = false;
			String transType = "view";
			
			if (contentType.contains("application/json") || "json".equals(responseType)) {
				transType = "json";
			}
			
			BaseMap transMap = baseCache.trans(path, transType);
			
			if (transMap != null) {
				
				isLoginYn = transMap.getBoolean("loginYn");
				isLoggerYn = transMap.getBoolean("loggerYn");
				isIplYn =transMap.getBoolean("iplYn");
				isUseYn =transMap.getBoolean("useYn");
				transType =transMap.getString("transType");
				
				// 거래재한 여부
				if (isIplYn) {
					
				}
				
				if (isUseYn) {
					// 사용여부
				}
				
				req.setAttribute("isAllowedPath", !isLoginYn);
				
				// request log
				if (isLoggerYn) requestLogger(requestWrapper);
				
				chain.doFilter(requestWrapper, responseWrapper);
				
				// response log
				if (isLoggerYn) responseLogger(responseWrapper);
				
				if (isEncrypt) {
					responseEncrypt(res, responseWrapper);
				}
				
				// 응답값 재사용
				responseWrapper.copyBodyToResponse();
			} else {
				String content = requestWrapper.requestContent();
				String params = StringUtil.toJsonString(requestWrapper.getParameterMap());
				log.error("\n[Not Found] URI = {} \n[Not Found] params = {} \n[Not Found] content= {}", path, params, content);
				
				this.responseError(req, res, ErrorCode.NOT_FOUND);
			}
		} catch (BaseException e) {
			e.printStackTrace();
			this.responseError(req, res, e.getErrorCode(), e.getWords());
		} catch (Exception e) {
			e.printStackTrace();
			this.responseError(req, res, ErrorCode.REQUEST_FAILURE);
		}
		
		if (log.isDebugEnabled()) log.debug("[Service End]==========================================================================\n\n");
	}
	
	@Override
	public void destroy() {
		
	}
	
	/**
	 * 오류시 응답값 전송
	 * 
	 * @param res
	 * @param errorCode
	 * @param contentType
	 * @param charsetName
	 * @throws IOException
	 */
	private void responseError(HttpServletRequest req, HttpServletResponse res, ErrorCode errorCode, Object... words) 
			throws IOException {
		if (log.isDebugEnabled()) log.debug("errorCode = {}", errorCode);
		
		String contentType = StringUtils.defaultIfBlank(req.getHeader("content-type"), "text/html");
		String responseType = StringUtils.defaultIfBlank(req.getHeader("response-type"), "");
		String charsetName = req.getCharacterEncoding();
		
		if ("json".equals(responseType)) {
			contentType = "application/json";
		}
		
		if (log.isDebugEnabled()) log.debug("contentType = {}, responseType = {}, charsetName = {}", contentType, responseType, charsetName);
		
		String responseMessage = StringUtil.toJsonString(new ResponseData(errorCode, words));
		byte[] responseMessageBytes = responseMessage.getBytes(charsetName);
		
		res.setStatus(errorCode.getStatus());
		res.setContentType(contentType);
		res.setContentLength(responseMessageBytes.length);
		res.getOutputStream().write(responseMessageBytes);
		res.flushBuffer();
	}

	/**
	 * 응답값을 암호화하여 보내준다.
	 * 
	 * @param response
	 * @param responseWrapper
	 * @throws IOException
	 */
	private void responseEncrypt(HttpServletResponse res, BaseResponseWrapper responseWrapper)
			throws IOException {
		
		String contentType = responseWrapper.getContentType();
		byte[] responseMessageBytes = responseWrapper.encryptContent();
		
		res.setContentType(contentType);
		res.setContentLength(responseMessageBytes.length);
		res.getOutputStream().write(responseMessageBytes);
		res.flushBuffer();
	}

	/**
	 * 요청 로그 기록
	 * 
	 * @param requestWrapper
	 */
	private void requestLogger(BaseRequestWrapper requestWrapper) {
		
		String contentType = requestWrapper.getContentType();
		String charsetName = requestWrapper.getCharacterEncoding();
		String requestContent = "";
		
		if (contentType != null && contentType.contains("application/json")) {
			BaseMap bodyMap = new BaseMap();
			try {
				bodyMap.putAll(StringUtil.toMap(requestWrapper.requestContent()));
			} catch (UnsupportedEncodingException e) {
				log.error("requestLogger 오류 :: ", e);
			}
			BaseMap contentMap = new BaseMap();
			contentMap.put("header", requestWrapper.requestHeader());
			contentMap.put("body", bodyMap);
			requestContent = StringUtil.toJsonString(contentMap);
			
		} else if (contentType != null && contentType.contains("multipart/form-data")){

			List<BaseMap> partList = new LinkedList<>();
			for (Part part : requestWrapper.getContentParts()) {
				if (part.getContentType().contains("application/json")) {
					try {
						String params = new String(part.getInputStream().readAllBytes(), charsetName);
						partList.add(new BaseMap(StringUtil.toMap(params)));
					} catch (IOException e) {
						log.error("requestLogger 오류 :: ", e);
					}
				} else {
					BaseMap partMap = new BaseMap();
					partMap.put("name", part.getName());
					partMap.put("fileName", part.getSubmittedFileName());
					partMap.put("size", part.getSize());
					partList.add(partMap);
				}
			}
			
			BaseMap contentMap = new BaseMap();
			contentMap.put("header", requestWrapper.requestHeader());
			contentMap.put("body", partList);
			requestContent = StringUtil.toJsonString(contentMap);
		} else {
			requestContent = StringUtil.toJsonString(requestWrapper.getParameterMap());
		}
		
		BaseMap  logMap = new BaseMap();
		logMap.put("contentType", contentType);
		logMap.put("transUri", MDC.get("path"));
		logMap.put("memberNo", MDC.get("memberNo"));
		logMap.put("remoteAddr", MDC.get("memberIp"));
		logMap.put("transLogNo", MDC.get("reqTime"));
		logMap.put("errorCode", "");
		logMap.put("errorMessage", "");
		logMap.put("transDiv", "req");
		logMap.put("transContent", requestContent);
		
		if (log.isDebugEnabled()) log.debug("[LOG] REQUEST = {}", logMap);
		
		transLogService.async(logMap);
	}
	
	/**
	 * 응답 로그 기록
	 * 
	 * @param responseWrapper
	 */
	private void responseLogger(BaseResponseWrapper responseWrapper) {
		String contentType = responseWrapper.getContentType();
		ErrorCode errorCode = responseWrapper.getErrorCode();
		String responseContent = "";
		if (contentType != null && contentType.contains("application/json")) {
			try {
				responseContent = responseWrapper.responseContent();
			} catch (UnsupportedEncodingException e) {
				log.error("responseLogger 오류 :: ", e);
			}
		} else {
			ModelAndView mav = responseWrapper.getModelAndView();
			if (mav != null) {
				responseContent = StringUtil.toJsonString(mav.getModel());
			}
		}
		
		BaseMap  logMap = new BaseMap();
		logMap.put("contentType", contentType);
		logMap.put("transUri", MDC.get("path"));
		logMap.put("memberNo", MDC.get("memberNo"));
		logMap.put("remoteAddr", MDC.get("memberIp"));
		logMap.put("transLogNo", MDC.get("reqTime"));
		logMap.put("errorCode", errorCode.getCode());
		logMap.put("errorMessage", errorCode.getMessage());
		logMap.put("transDiv", "res");
		logMap.put("transContent", responseContent);	
		
		if (log.isDebugEnabled()) log.debug("[LOG] RESPONSE = {}", logMap);
		
		transLogService.async(logMap);
	}
	
	/**
	 * 접속자 IP 가져오기
	 * 
	 * @param request
	 * @return
	 */
	private String getRemoteAddr(HttpServletRequest request) {
		String ipFromHeader = request.getHeader("X-FORWARDED-FOR");
		if (ipFromHeader != null && ipFromHeader.length() > 0) {
			log.debug("ip from proxy - X-FORWARDED-FOR : " + ipFromHeader);
			return ipFromHeader;
		}
		return request.getRemoteAddr();
	}
}

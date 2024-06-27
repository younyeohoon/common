package kr.co.cont.common.filter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.springframework.http.MediaType;

import com.fasterxml.jackson.databind.ObjectMapper;

import kr.co.cont.common.biz.base.model.ResponseData;
import kr.co.cont.common.constants.ErrorCode;
import kr.co.cont.common.util.SessionUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@WebFilter(filterName = "loginFilter", initParams = { @WebInitParam(name = "excludedExt", value = "/ws,/main,/sample,/login,/logout,/register")})
public class LoginFilter implements Filter {
	
	private static List<String> ALLOWED_PATHS ;
	
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		if (log.isDebugEnabled()) log.debug("filterConfig = {}", filterConfig);
		
		String excludedString = filterConfig.getInitParameter("excludedExt");
		if (log.isDebugEnabled()) log.debug("excludedString = {}", excludedString);
		
		if (excludedString != null) {
			ALLOWED_PATHS = Arrays.asList(excludedString.split(",", 0));
		} else {
			ALLOWED_PATHS = Collections.emptyList();
		}
		
		if(log.isDebugEnabled()) log.debug("ALLOWED_PATHS = {}", ALLOWED_PATHS);
		
	}
	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		// 1. 로그인된 클라이언트인지 확인( HttpSession 이 필요 => HttpServletRequest 가 필요)
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;
		
		String path = MDC.get("path");
		
		boolean isLogin = SessionUtil.isLogin(req);
//		boolean isAllowedPath = ALLOWED_PATHS.stream().filter(v -> path.startsWith(v) || "".equals(path)).findFirst().isPresent();
//		boolean isAllowedPath = ALLOWED_PATHS.stream().filter(v -> path.startsWith(v) ).findFirst().isPresent();
		boolean isAllowedPath = (boolean) req.getAttribute("isAllowedPath");
		
		if (isLogin || isAllowedPath) {
			chain.doFilter(request, response);
		} else {
			String contentType = StringUtils.defaultIfBlank(req.getHeader("content-type"), "");
			
			if (contentType.indexOf(MediaType.APPLICATION_JSON_VALUE) != -1) {
				ResponseData resData = new ResponseData(ErrorCode.LOGIN_ACCESS_DENIED);
				res.getWriter().write((new ObjectMapper()).writeValueAsString(resData));
				res.setContentType(contentType);
				res.setCharacterEncoding(req.getCharacterEncoding());
				res.setStatus(403);
				res.flushBuffer(); // marks response as committed
			} else {
				String params = req.getQueryString();
				if (params != null && !"".equals(params)) {
					path += "?" + params;
				}
				
				req.setAttribute("prePage", path);
				
				// forward
				RequestDispatcher rd = req.getRequestDispatcher("/login");
				rd.forward(req, res);
				
				// redirect
//				res.sendRedirect(req.getContextPath() + "/login");
				
			}
		}
	}

	@Override
	public void destroy() {
		
	}
}

package kr.co.cont.common.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.AsyncHandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class CommonInterceptor implements AsyncHandlerInterceptor {
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {

		if (log.isDebugEnabled())
			log.debug("[Start] preHandle");

		if (log.isDebugEnabled())
			log.debug("[End] preHandle");

		return true;
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			@Nullable ModelAndView modelAndView) throws Exception {
		if (log.isDebugEnabled())
			log.debug("[Start] postHandle");

		if (log.isDebugEnabled())
			log.debug("[End] postHandle");
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
			@Nullable Exception ex) throws Exception {
		if (log.isDebugEnabled())
			log.debug("[Start] afterCompletion");

		if (log.isDebugEnabled())
			log.debug("[End] afterCompletion");
	}

	@Override
	public void afterConcurrentHandlingStarted(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		if (log.isDebugEnabled())
			log.debug("[Start] afterConcurrentHandlingStarted");

		if (log.isDebugEnabled())
			log.debug("[End] afterConcurrentHandlingStarted");
	}

}

package kr.co.cont.common.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.lang.Nullable;
import org.springframework.web.servlet.AsyncHandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import kr.co.cont.common.wrapper.BaseResponseWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class BaseInterceptor implements AsyncHandlerInterceptor {
	
	/**
	 * 컨트롤러의 동작 이전에 가로채는 역할로 사용
	 */
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		
		return true;
	}

	/**
	 * 지정된 컨트롤러의 동작 이후에 처리하며,
	 * Spring MVC의 Front Controller인 DispatcherServlet이 화면을 처리하기 전에 동작
	 */
	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			@Nullable ModelAndView modelAndView) throws Exception {

		try {
			BaseResponseWrapper req = (BaseResponseWrapper) response;
			req.setModelAndView(modelAndView);
		} catch (Exception e) {
			log.error("postHandle error = {} ", e.getMessage());
		}
		
	}

	/**
	 * DispatcherSerlvet의 화면 처리(뷰)가 완료된 상태에서 처리
	 */
	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
			@Nullable Exception ex) throws Exception {
		
	}

	/**
	 * 비동기 요청 시 PostHandle과 afterCompletion이 수행되지 않고 afterConcurrentHandlingStarted가 수행
	 */
	@Override
	public void afterConcurrentHandlingStarted(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		if (log.isDebugEnabled()) log.debug("[Start] afterConcurrentHandlingStarted");

		if (log.isDebugEnabled()) log.debug("[End] afterConcurrentHandlingStarted");
	}

}

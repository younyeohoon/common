package kr.co.cont.common.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import kr.co.cont.common.interceptor.CommonInterceptor;

@Configuration
//@EnableWebMvc
public class WebMvcConfig implements WebMvcConfigurer {
	
	@Autowired
	private CommonInterceptor commonInterceptor;
	
	/**
	 * websocket 에서 오리진이 허용이 안되어 추가.
	 *
	 * @param registry
	 */
	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**")
				.allowedOrigins("http://dev.thecont.co.kr", "https://dev.thecont.co.kr");
	}	
	
//	@Override
//	public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
//		resolvers.add(new LoginMemberArgumentResolver());
//	}
	
//	@Override
//	public void addResourceHandlers(ResourceHandlerRegistry registry) {
//		registry.addResourceHandler("/spring/**")
//				.addResourceLocations("")
//				.setCacheControl(CacheControl.maxAge(10, TimeUnit.MINUTES))
//				.resourceChain(true)
//				.addResolver(null)
//				.addTransformer(null);
//	}
	
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		
		// 가로채는 경로 설정 가능
//		registry.addInterceptor(new BaseInterceptor())
//				.order(1);
////				.addPathPatterns("/**") // 모든 Path에 대해서 가로챌것이다.
////				.excludePathPatterns("/css/**","/*.ico","/error");
////				.excludePathPatterns("/sample"); // /sample 경로에 대해서는 Interceptor 가로채지 않을것이다.
//		
//		registry.addInterceptor(commonInterceptor())
//				.order(2)
//				.addPathPatterns("/**") // 모든 Path에 대해서 가로챌것이다.
//				.excludePathPatterns();
////				.addPathPatterns("/sample") // /sample경로에 대해서만 가로챌것이다.
////				.excludePathPatterns("/sample"); // /sample 경로에 대해서는 Interceptor 가로채지 않을것이다.
//		
//		
//		registry.addInterceptor(commonInterceptor()).addPathPatterns("/**");
		
//		registry.addInterceptor(baseInterceptor()).addPathPatterns("/**");
		registry.addInterceptor(commonInterceptor);
//		WebMvcConfigurer.super.addInterceptors(registry);
	}
	
}

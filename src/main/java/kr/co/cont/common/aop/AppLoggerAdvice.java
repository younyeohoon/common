package kr.co.cont.common.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import kr.co.cont.common.exception.BaseException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@Aspect
public class AppLoggerAdvice {
	
	
	@Pointcut("execution(public * kr.co.cont..web.*Controller.*(..))")
	private void cut() {}

	@Before("cut()")
	public void preLoging(JoinPoint joinPoint) {
//		// 전달되는 모든 파라미터를 Object의 배열로 가져온다.
//		log.debug("모든 파라미터 = {}", Arrays.toString(joinPoint.getArgs()));
//		
//		// 해당 Advice의 타입을 알아낸다.
//		log.debug("Advice 타입 = {}", joinPoint.getKind());
//		
//		// 실행하는 대상 객체의 메소드에 대한 정보를 알아낼 때 사용
//		log.debug("객체 메소드 = {}", joinPoint.getSignature().getName());
//		
//		// target 객체를 알아낼 때 사용
//		log.debug("target 객체 = {}", joinPoint.getTarget().toString());
//		
//		// Advice를 행하는 겍체를 알아낼 때 사용
//		log.debug("Advice를 행하는 겍체 = {}", joinPoint.getThis().toString());
	}
	
	@After("cut()")
	public void postLoging(JoinPoint joinPoint) {
//		// 전달되는 모든 파라미터를 Object의 배열로 가져온다.
//		log.debug("모든 파라미터 = {}", Arrays.toString(joinPoint.getArgs()));
//		
//		// 해당 Advice의 타입을 알아낸다.
//		log.debug("Advice 타입 = {}", joinPoint.getKind());
//		
//		// 실행하는 대상 객체의 메소드에 대한 정보를 알아낼 때 사용
//		log.debug("객체 메소드 = {}", joinPoint.getSignature().getName());
//		
//		// target 객체를 알아낼 때 사용
//		log.debug("target 객체 = {}", joinPoint.getTarget().toString());
//		
//		// Advice를 행하는 겍체를 알아낼 때 사용
//		log.debug("Advice를 행하는 겍체 = {}", joinPoint.getThis().toString());
		
	}
	
	@Around("cut()")
	public Object aroundLoging(ProceedingJoinPoint joinPoint) throws Throwable {
		long startTime = System.currentTimeMillis();
		Object responseObj = null;
		
		try {
			responseObj = joinPoint.proceed();
			
		} catch (BaseException e) {
			throw e;
		} catch (Throwable e) {
			throw e;
		} finally {
			if (log.isDebugEnabled()) log.debug("[LOG] 수행시간 : {} ms", System.currentTimeMillis() - startTime);
		}

		return responseObj;
	}


}

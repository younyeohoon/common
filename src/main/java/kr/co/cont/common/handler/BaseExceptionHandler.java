package kr.co.cont.common.handler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import kr.co.cont.common.biz.base.model.ResponseData;
import kr.co.cont.common.constants.ErrorCode;
import kr.co.cont.common.exception.BaseException;
import kr.co.cont.common.wrapper.BaseResponseWrapper;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ControllerAdvice(annotations = Controller.class)
public class BaseExceptionHandler {
	
	@ExceptionHandler(value = BaseException.class)
	public ModelAndView baseExceptionHandler(HttpServletRequest request, HttpServletResponse response, BaseException ex) {
		if (log.isDebugEnabled()) log.debug("BaseException :: {}", ex);
		
		BaseResponseWrapper res = (BaseResponseWrapper) response;
		res.setErrorCode(ex.getErrorCode());
		
		ResponseData responseData = new ResponseData(ex);
		
		ModelAndView mav = new ModelAndView();
		mav.addObject("responseData", responseData);
		
		switch (ex.getStatus()) {
		case 300:
			mav.setViewName("error/error_300");
			break;
		case 400:
			mav.setViewName("error/error_400");
			break;
		case 500:
			mav.setViewName("error/error_500");
			break;
		default:
			mav.setViewName("error/error");
			break;
		}
		
		return mav;
		
	}
	
	@ExceptionHandler(value = {MethodArgumentNotValidException.class})
	public ModelAndView processValidationError(HttpServletRequest request, HttpServletResponse response
			, MethodArgumentNotValidException  ex) {
		if (log.isDebugEnabled()) log.debug("Exception :: {} , {}", ex.getClass().getName(), ex.getMessage());

		BindingResult bindingResult = ex.getBindingResult();

		StringBuilder builder = new StringBuilder();
		for (FieldError fieldError : bindingResult.getFieldErrors()) {
			builder.append("[");
			builder.append(fieldError.getField());
			builder.append("](은)는 ");
			builder.append(fieldError.getDefaultMessage());
			builder.append(" 입력된 값: [");
			builder.append(fieldError.getRejectedValue());
			builder.append("]");
			builder.append("\n");
		}		
		
		return this.baseExceptionHandler(request, response, new BaseException(ErrorCode.INVALID_INPUT_VALUE, ex, builder.toString()));
	}
	
	@ExceptionHandler(value = {HttpRequestMethodNotSupportedException.class})
	public ModelAndView notSupportedExceptionHandler(HttpServletRequest request, HttpServletResponse response, Throwable ex) {
		if (log.isDebugEnabled()) log.debug("Exception :: {} , {}", ex.getClass().getName(), ex.getMessage());
		return this.baseExceptionHandler(request, response, new BaseException(ErrorCode.NOT_FOUND, ex));
	}
	
	@ExceptionHandler(value = Exception.class)
	public ModelAndView exceptionHandler(HttpServletRequest request, HttpServletResponse response, Throwable ex) {
		if (log.isDebugEnabled()) log.debug("Exception :: {} , {}", ex.getClass().getName(), ex.getMessage());
		
		return this.baseExceptionHandler(request, response, new BaseException(ErrorCode.REQUEST_FAILURE, ex));
	}
}

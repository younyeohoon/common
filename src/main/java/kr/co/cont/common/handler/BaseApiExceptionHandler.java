package kr.co.cont.common.handler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;

import kr.co.cont.common.biz.base.model.ResponseData;
import kr.co.cont.common.constants.ErrorCode;
import kr.co.cont.common.exception.BaseException;
import kr.co.cont.common.wrapper.BaseResponseWrapper;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE) 
@RestControllerAdvice(annotations = RestController.class)
public class BaseApiExceptionHandler {
	
	@ExceptionHandler(value = BaseException.class)
	public ResponseData baseExceptionHandler(HttpServletRequest request, HttpServletResponse response, BaseException ex) {
		if (log.isDebugEnabled()) log.debug("BaseException :: {}", ex);
		
		BaseResponseWrapper req = (BaseResponseWrapper) response;
		req.setErrorCode(ex.getErrorCode());
		
		return new ResponseData(ex);
		
	}
	
	@ExceptionHandler(value = {MethodArgumentNotValidException.class})
	public ResponseData processValidationError(HttpServletRequest request, HttpServletResponse response
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
	
	/**
	 * 파일 업로드 오류 관련
	 * 
	 * @param request
	 * @param response
	 * @param ex
	 * @return
	 */
	@ExceptionHandler(value = {MultipartException.class})
	public ResponseData multipartExceptionHandler(HttpServletRequest request, HttpServletResponse response, Throwable ex) {
		if (log.isDebugEnabled()) log.debug("Exception :: {} , {}", ex.getClass().getName(), ex.getMessage());
		
		BaseException baseException = new BaseException(ErrorCode.REQUEST_FAILURE, ex);
		
		if (ex instanceof MaxUploadSizeExceededException) {
			baseException = new BaseException(ErrorCode.FILE_UPLOAD_SIZE_ERROR, ex);
		}
		
		return this.baseExceptionHandler(request, response, baseException);
	}
	
	@ExceptionHandler(value = {HttpRequestMethodNotSupportedException.class})
	public ResponseData notSupportedExceptionHandler(HttpServletRequest request, HttpServletResponse response, Throwable ex) {
		if (log.isDebugEnabled()) log.debug("Exception :: {} , {}", ex.getClass().getName(), ex.getMessage());
		
		return this.baseExceptionHandler(request, response, new BaseException(ErrorCode.NOT_FOUND, ex));
	}
	
	@ExceptionHandler(value = Exception.class)
	public ResponseData exceptionHandler(HttpServletRequest request, HttpServletResponse response, Throwable ex) {
		if (log.isDebugEnabled()) log.debug("Exception :: {} , {}", ex.getClass().getName(), ex.getMessage());
		
		return this.baseExceptionHandler(request, response, new BaseException(ErrorCode.REQUEST_FAILURE, ex));
	}
}

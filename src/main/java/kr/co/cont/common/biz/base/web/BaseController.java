package kr.co.cont.common.biz.base.web;

import kr.co.cont.common.biz.base.model.BaseMap;
import kr.co.cont.common.biz.base.model.ResponseData;
import kr.co.cont.common.constants.ErrorCode;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BaseController {
	
	protected ResponseData setResponseData(ErrorCode errorCode) {
		if (log.isDebugEnabled()) log.debug("errorCode = {}", errorCode); 
		return new ResponseData(errorCode);
	}
	
	protected ResponseData setResponseData(ErrorCode errorCode, String... words) {
		if (log.isDebugEnabled()) log.debug("errorCode = {}", errorCode); 
		return new ResponseData(errorCode, words);
	}
	
	protected ResponseData setResponseData(ErrorCode errorCode, BaseMap body) {
		if (log.isDebugEnabled()) log.debug("errorCode = {}, body = {}", errorCode, body); 
		ResponseData responseData = new ResponseData(errorCode);
		responseData.setBody(body);
		return responseData;
	}
	
	protected ResponseData setResponseData(BaseMap body) {
		if (log.isDebugEnabled()) log.debug("body = {}", body);
		
		ErrorCode errorCode = body.getErrorCode();
		if (errorCode != null) {
			return new ResponseData(errorCode, body.getErrorMessage());
		} else {
			return new ResponseData(body);
		}
	}
	
}

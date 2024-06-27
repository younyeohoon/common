package kr.co.cont.common.handler;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.DefaultResponseErrorHandler;

import com.fasterxml.jackson.databind.ObjectMapper;

import kr.co.cont.common.biz.base.model.BaseMap;
import kr.co.cont.common.constants.ErrorCode;
import kr.co.cont.common.exception.BaseException;

@Component
public class RestTemplateResponseErrorHandler extends DefaultResponseErrorHandler {

	@Override
	public boolean hasError(ClientHttpResponse response) throws IOException {
//		int rawStatusCode = response.getRawStatusCode();
//		HttpStatus statusCode = HttpStatus.resolve(rawStatusCode);
//		return (statusCode != null ? hasError(statusCode) : hasError(rawStatusCode));
		
//		return response.getStatusCode().is4xxClientError() || response.getStatusCode().is5xxServerError();
		// 모든 요청을 전달한다.
		return false;
	}

	@Override
	public void handleError(ClientHttpResponse response) throws IOException {
		
		HttpStatus statusCode = response.getStatusCode();
		String statusText = response.getStatusText();
		
		StringBuilder message = new StringBuilder();
		message.append("[");
		message.append(statusCode.value());
		message.append("] ");
		message.append(statusText);
		
		ObjectMapper objectMapper = new ObjectMapper();
		BaseMap baseMap = objectMapper.readValue(getResponseBody(response), BaseMap.class);
		
		BaseException baseException =  null;
		if (statusCode.is5xxServerError()) {
			baseException =  new BaseException(ErrorCode.HTTP_INTERNAL_SERVER_ERROR, message.toString());
		} else if (statusCode.is4xxClientError()) {
			baseException =  new BaseException(ErrorCode.HTTP_BAD_REQUEST);
		}
		
		baseException.setBody(baseMap);
		
		throw baseException;
		
	}
	
}

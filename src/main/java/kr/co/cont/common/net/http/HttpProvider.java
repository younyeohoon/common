package kr.co.cont.common.net.http;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import kr.co.cont.common.biz.base.model.BaseMap;
import kr.co.cont.common.constants.ErrorCode;
import kr.co.cont.common.exception.BaseException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class HttpProvider {

	private final RestTemplate restTemplate;

	/**
	 * GET 방식으로 Content-Type 을 application/json.으로하여 정보를 요청한다.
	 * URL 인코딩 문제로 추가
	 * @param requestUrl 요청할 서버 주소
	 * @return BaseMap.class (응답결과)
	 */
	public BaseMap getSend(URL requestUrl) throws URISyntaxException {
		if(log.isDebugEnabled()) log.debug("requestUrl = {}", requestUrl);
		BaseMap header = new BaseMap();
		header.put("Content-Type", MediaType.APPLICATION_JSON.toString());
		return this.httpSend(requestUrl, HttpMethod.GET, header, null, BaseMap.class, false);
	}

	/**
	 * GET 방식으로 Content-Type 을 application/json.으로하여 정보를 요청한다.
	 *  
	 * @param requestUrl 요청할 서버 주소
	 * @return BaseMap.class (응답결과)  
	 */
	public BaseMap getSend(String requestUrl) {
		if(log.isDebugEnabled()) log.debug("requestUrl = {}", requestUrl);
		BaseMap header = new BaseMap();
		header.put("Content-Type", MediaType.APPLICATION_JSON.toString());
		return this.httpSend(requestUrl, HttpMethod.GET, header, null, BaseMap.class, false);
	}
	
	/**
	 * GET 방식으로 header를 사용자가 정의한 값으로 설정하여 정보를 요청한다.
	 * 
	 * @param requestUrl 요청할 서버 주소
	 * @param header 요청할 때 보낼 헤더
	 * @return BaseMap.class (응답결과)  
	 */
	public BaseMap getSend(String requestUrl, BaseMap header) {
		if(log.isDebugEnabled()) log.debug("requestUrl = {}", requestUrl);
		return this.httpSend(requestUrl, HttpMethod.GET, header, null, BaseMap.class, false);
	}
	
	
	/**
	 * GET 방식으로 header를 사용자가 정의한 값으로 설정하여 정보를 요청한다.
	 * 
	 * @param requestUrl 요청할 서버 주소
	 * @param header 요청할 때 보낼 헤더
	 * @param requestClazz 요청시 반환되는 데이터 타입
	 * @param HTTP Status Code 의 결과에 따라서 Exeption 처리 여부(true: Exception 미처리 )
	 * @return BaseMap.class (응답결과)  
	 */
	public <T> T getSend(String requestUrl, BaseMap header, Class<T> requestClazz, boolean skip) {
		if(log.isDebugEnabled()) log.debug("requestUrl = {}", requestUrl);
		return this.httpSend(requestUrl, HttpMethod.GET, header, null, requestClazz, skip);
	}
	
	/**
	 * POST 방식으로 Content-Type 을 application/json.으로하여 정보를 요청한다.
	 * 
	 * @param requestUrl 요청할 서버 주소
	 * @param data 요청할 때 보낼 데이터
	 * @return BaseMap.class (응답결과)
	 */
	public BaseMap postSend(String requestUrl, BaseMap data) {
		if(log.isDebugEnabled()) log.debug("requestUrl = {}", requestUrl);
		// Header 설정
		BaseMap header = new BaseMap();
		header.put("Content-Type", MediaType.APPLICATION_JSON.toString());
		return this.httpSend(requestUrl, HttpMethod.POST, header, data, BaseMap.class, false);
	}
	
	/**
	 * multipart/form-data 데이터 전송
	 * 
	 * @param requestUrl
	 * @param multipartFile
	 * @return
	 */
	public BaseMap postSend(String requestUrl, MultipartFile[] multipartFiles) {
		
		BaseMap header = new BaseMap();
		header.put("Content-Type", MediaType.MULTIPART_FORM_DATA);
		header.put("response-type", "json");
		
		MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
		
		int s = multipartFiles.length;
		for (int i = 0; i < s; s++) {
			MultipartFile multipartFile = multipartFiles[i];
			byte[] byteArray = null;
			try {
				byteArray = multipartFile.getBytes();
			} catch (IOException e) {
				log.error("파일 전송 byte 오류", e);
			}
			
			ByteArrayResource contentsAsResource = new ByteArrayResource(byteArray) {
				@Override
				public String getFilename() {
					return multipartFile.getOriginalFilename();
				}
			};
			
			body.add(multipartFile.getName(), contentsAsResource);
		}
		
		
		return this.httpSend(requestUrl, HttpMethod.POST, header, body, BaseMap.class, false);
	}
	
	/**
	 * POST 방식으로 header를 사용자가 정의한 값으로 설정하여 정보를 요청한다.
	 * 
	 * @param requestUrl 요청할 서버 주소
	 * @param header 요청할 때 보낼 헤더
	 * @param data 요청할 때 보낼 데이터
	 * @return BaseMap.class (응답결과) 
	 */
	public BaseMap postSend(String requestUrl, BaseMap header, BaseMap data) {
		if(log.isDebugEnabled()) log.debug("requestUrl = {}", requestUrl);
		return this.httpSend(requestUrl, HttpMethod.POST, header, data, BaseMap.class, false);
	}
	
	/**
	 * POST 방식으로 header를 사용자가 정의한 값으로 설정하여 정보를 요청한다.
	 * 
	 * @param requestUrl 요청할 서버 주소
	 * @param header 요청할 때 보낼 헤더
	 * @param data 요청할 때 보낼 데이터
	 * @param requestClazz 요청시 반환되는 데이터 타입
	 * @param skip HTTP Status Code 의 결과에 따라서 Exeption 처리 여부(true: Exception 미처리 ) 
	 * @return T.class (응답결과) 
	 */
	public <T> T postSend(String requestUrl, BaseMap header, Object data, Class<T> requestClazz, boolean skip) {
		if(log.isDebugEnabled()) log.debug("requestUrl = {}", requestUrl);
		return this.httpSend(requestUrl, HttpMethod.POST, header, data, requestClazz, skip);
	}
	
	/**
	 * PUT 방식으로 header를 사용자가 정의한 값으로 설정하여 정보를 요청한다.
	 * 
	 * @param <T>
	 * @param requestUrl 요청할 서버 주소
	 * @param header 요청할 때 보낼 헤더
	 * @param data 요청할 때 보낼 데이터
	 * @param requestClazz 요청시 반환되는 데이터 타입
	 * @param skip HTTP Status Code 의 결과에 따라서 Exeption 처리 여부(true: Exception 미처리 ) 
	 * @return T.class (응답결과) 
	 */
	public <T> T putSend(String requestUrl, BaseMap header, Object data, Class<T> requestClazz, boolean skip) {
		if(log.isDebugEnabled()) log.debug("requestUrl = {}", requestUrl);
		return this.httpSend(requestUrl, HttpMethod.PUT, header, data, requestClazz, skip);
	}
	
	/**
	 * DELETE 방식으로 header를 사용자가 정의한 값으로 설정하여 정보를 요청한다.
	 * 
	 * @param requestUrl 요청할 서버 주소
	 * @param header 요청할 때 보낼 헤더
	 * @param requestClazz 요청시 반환되는 데이터 타입
	 * @param skip HTTP Status Code 의 결과에 따라서 Exeption 처리 여부(true: Exception 미처리 ) 
	 * @return T.class (응답결과) 
	 */
	public <T> T deleteSend(String requestUrl, BaseMap header, Class<T> requestClazz, boolean skip) {
		if(log.isDebugEnabled()) log.debug("requestUrl = {}", requestUrl);
		return this.httpSend(requestUrl, HttpMethod.DELETE, header, null, requestClazz, skip);
	}
	
	/**
	 * RestTemplate 을 이용한 Http 통신
	 * 
	 * @param <T>
	 * @param requestUrl 요청할 서버 주소
	 * @param requestMethod 요청할 방식(POST/GET)
	 * @param header 요청할 때 보낼 헤더
	 * @param data 요청할 때 보낼 데이터
	 * @param requestClazz 요청시 반환되는 데이터 타입
	 * @param HTTP Status Code 의 결과에 따라서 Exeption 처리 여부(true: Exception 미처리 )
	 * @return
	 */
	private <T> T httpSend(String requestUrl, HttpMethod requestMethod, BaseMap header, Object data,
			Class<T> requestClazz, boolean skip) {
		if(log.isDebugEnabled()) log.debug("requestUrl = {}", requestUrl);
		
		// Header 설정
		HttpHeaders headers = new HttpHeaders();
		header.keySet().forEach( k -> {
			String key = String.valueOf(k);
			headers.add( key, header.getString(key));
		});
		
		// 요청할 때 보낼 데이터 및 헤더 값을 설정한다.
		HttpEntity<Object> requestEntity = null;
		if (data == null) {
			requestEntity = new HttpEntity<>(headers);
		} else {
			requestEntity = new HttpEntity<>(data, headers);
		}

		// 요청
		ResponseEntity<T> response = restTemplate.exchange(requestUrl, requestMethod, requestEntity, requestClazz);
//		int statusCode = response.getStatusCodeValue();
		HttpStatus httpStatus = response.getStatusCode();
		
		HttpHeaders responseHeader = response.getHeaders();
//		String contentType = responseHeader.getFirst("Content-Type");
		T responseBody = response.getBody();
		
		// HTTP 상태코드에 따른 Exception 분기 처리
		if (!skip) {
			if (httpStatus.is5xxServerError()) {
				// 서버 오류
				BaseException baseException = new BaseException(ErrorCode.HTTP_INTERNAL_SERVER_ERROR, responseBody);
				baseException.setBody(responseBody);
				throw baseException;
			} else if (httpStatus.is4xxClientError()) {
				// 요청 오류
				BaseException baseException = new BaseException(ErrorCode.HTTP_BAD_REQUEST);
				baseException.setBody(responseBody);
				throw baseException;
			} else if (httpStatus.is3xxRedirection()) {
				// 리다이렉션 완료
				// throw new BaseException(ErrorCode.HTTP_REQUEST_REDIRECT, responseBody);
			} else if (httpStatus.is2xxSuccessful()) {
				// 정상처리
			} else {
			}
		}
		
		if (log.isDebugEnabled()) {
			T body = null;
			String className = null;
			
			if (responseBody != null) {
				className = responseBody.getClass().getName();
				if (MediaType.APPLICATION_JSON.equalsTypeAndSubtype(responseHeader.getContentType())) {
					body = responseBody;
				}
			}

			StringBuilder sb = new StringBuilder();
			sb.append("\nstatusCode = ").append(httpStatus);
			sb.append("\nresponseHeader = ").append(responseHeader);
			sb.append("\nresponseBody = ").append(body);
			sb.append("\nClass Name = ").append(className);
			
			log.debug("{}", sb.toString());
		}
		
		return responseBody;
	}
	/**
	 * RestTemplate 을 이용한 Http 통신
	 *
	 * @param <T>
	 * @param requestUrl 요청할 서버 주소(URL)
	 * @param requestMethod 요청할 방식(POST/GET)
	 * @param header 요청할 때 보낼 헤더
	 * @param data 요청할 때 보낼 데이터
	 * @param requestClazz 요청시 반환되는 데이터 타입
	 * @param HTTP Status Code 의 결과에 따라서 Exeption 처리 여부(true: Exception 미처리 )
	 * @return
	 */
	private <T> T httpSend(URL requestUrl, HttpMethod requestMethod, BaseMap header, Object data,
						   Class<T> requestClazz, boolean skip) throws URISyntaxException {
		if(log.isDebugEnabled()) log.debug("requestUrl = {}", requestUrl);

		// Header 설정
		HttpHeaders headers = new HttpHeaders();
		header.keySet().forEach( k -> {
			String key = String.valueOf(k);
			headers.add( key, header.getString(key));
		});

		// 요청할 때 보낼 데이터 및 헤더 값을 설정한다.
		HttpEntity<Object> requestEntity = null;
		if (data == null) {
			requestEntity = new HttpEntity<>(headers);
		} else {
			requestEntity = new HttpEntity<>(data, headers);
		}

		// 요청
		ResponseEntity<T> response = restTemplate.exchange(requestUrl.toURI(), requestMethod, requestEntity, requestClazz);
		//		int statusCode = response.getStatusCodeValue();
		HttpStatus httpStatus = response.getStatusCode();

		HttpHeaders responseHeader = response.getHeaders();
		//		String contentType = responseHeader.getFirst("Content-Type");
		T responseBody = response.getBody();

		// HTTP 상태코드에 따른 Exception 분기 처리
		if (!skip) {
			if (httpStatus.is5xxServerError()) {
				// 서버 오류
				BaseException baseException = new BaseException(ErrorCode.HTTP_INTERNAL_SERVER_ERROR, responseBody);
				baseException.setBody(responseBody);
				throw baseException;
			} else if (httpStatus.is4xxClientError()) {
				// 요청 오류
				BaseException baseException = new BaseException(ErrorCode.HTTP_BAD_REQUEST);
				baseException.setBody(responseBody);
				throw baseException;
			} else if (httpStatus.is3xxRedirection()) {
				// 리다이렉션 완료
				// throw new BaseException(ErrorCode.HTTP_REQUEST_REDIRECT, responseBody);
			} else if (httpStatus.is2xxSuccessful()) {
				// 정상처리
			} else {
			}
		}

		if (log.isDebugEnabled()) {
			T body = null;
			String className = null;

			if (responseBody != null) {
				className = responseBody.getClass().getName();
				if (MediaType.APPLICATION_JSON.equalsTypeAndSubtype(responseHeader.getContentType())) {
					body = responseBody;
				}
			}

			StringBuilder sb = new StringBuilder();
			sb.append("\nstatusCode = ").append(httpStatus);
			sb.append("\nresponseHeader = ").append(responseHeader);
			sb.append("\nresponseBody = ").append(body);
			sb.append("\nClass Name = ").append(className);

			log.debug("{}", sb.toString());
		}

		return responseBody;
	}
}

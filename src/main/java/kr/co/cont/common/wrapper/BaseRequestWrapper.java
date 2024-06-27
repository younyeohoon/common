package kr.co.cont.common.wrapper;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.Collections;

import javax.servlet.ReadListener;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;

import org.springframework.web.util.ContentCachingRequestWrapper;

import kr.co.cont.common.biz.base.model.BaseMap;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BaseRequestWrapper extends ContentCachingRequestWrapper {
	
	/** 파일업로드시에 사용합니다. */
	private Collection<Part> parts;
	private Exception partsParseException;

	public BaseRequestWrapper(HttpServletRequest request) {
		super(request);
		setParts(request);
		setCachedContent();
	}
	
	public BaseRequestWrapper(HttpServletRequest request, int contentCacheLimit) {
		super(request);
		setParts(request);
		setCachedContent();
	}
	
	@Override
	public ServletInputStream getInputStream() throws IOException {
		final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(super.getContentAsByteArray());
		return new ServletInputStream() {
			
			@Override
			public int read() throws IOException {
				return byteArrayInputStream.read();
			}
			
			@Override
			public void setReadListener(ReadListener readListener) {
			}
			
			@Override
			public boolean isReady() {
				return false;
			}
			
			@Override
			public boolean isFinished() {
				return false;
			}
		};
	}
	
	/**
	 * 파일 업로드한 정보를 설정한다.
	 * 
	 * @param request
	 * @throws tException 
	 */
	public void setParts(HttpServletRequest request) {
		
		if (( request.getMethod().equalsIgnoreCase("POST") 
				|| request.getMethod().equalsIgnoreCase("PUT") )
				&& request.getContentType().startsWith("multipart/form-data")) {
			
			try {
				this.parts = request.getParts();
			} catch (IllegalStateException e) {
				log.error("파일 업로드 설정 오류 = {}, {}", e.getClass().getName(), e.getMessage());
				partsParseException = e;
			} catch (IOException e) {
				log.error("파일 업로드 설정 오류 = {}, {}", e.getClass().getName(), e.getMessage());
				partsParseException = e;
			} catch (ServletException e) {
				log.error("파일 업로드 설정 오류 = {}, {}", e.getClass().getName(), e.getMessage());
				partsParseException = e;
			}
		} else {
			request.getParameterMap();
		}
	}
	
	/**
	 * 파일 업로드한 정보를 전달한다.
	 */
	@Override
	public Collection<Part> getParts() throws IOException, ServletException {
		
		if (this.partsParseException != null) {
			if (this.partsParseException instanceof IOException) {
				throw (IOException) this.partsParseException;
			}

			if (this.partsParseException instanceof IllegalStateException) {
				throw (IllegalStateException) this.partsParseException;
			}

			if (this.partsParseException instanceof ServletException) {
				throw (ServletException) this.partsParseException;
			}
		}
		
		return this.parts;
	}
	
	/**
	 * 로그적재 용도로 사용하기 위해서 생성함.
	 * @return
	 */
	public Collection<Part> getContentParts() {
		if (this.parts == null) {
			return Collections.emptySet();
		} else {
			return this.parts;
		}
	}
	
	/**
	 * Header 정보를 문자열로 반환 한다.
	 * 
	 * @return
	 */
	public BaseMap requestHeader() {
		BaseMap headerMap = new BaseMap();
		getHeaderNames().asIterator().forEachRemaining( key -> {
			headerMap.put(key, getHeader(key));
		});
		return headerMap;
	}
	
	/**
	 * Ajax 로 전송한 정보를 가져온다.
	 * 
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public String requestContent() throws UnsupportedEncodingException {
		String charset = super.getCharacterEncoding();
		byte[] bytes = super.getContentAsByteArray();
		
		return new String(bytes, charset);
	}
	
	/**
	 * request.getInputStream() 에서 요청값을 가지고와 재사용 가능하도록 한다.
	 */
	private void setCachedContent() {
		ServletInputStream inputStream = null;
		try {
			inputStream = super.getInputStream();
			
			if (inputStream.read() != -1) {
				byte[] bytes = new byte[8000];
				inputStream.read(bytes, 1, bytes.length -1);
			}
		} catch (IOException e) {
			log.error("getInputStream() 작업중 오류 발생", e);
		} finally {
			if (inputStream != null) {
				try { inputStream.close(); } catch (Exception e) {}
			}
		}
	}
}

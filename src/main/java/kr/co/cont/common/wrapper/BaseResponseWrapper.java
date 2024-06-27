package kr.co.cont.common.wrapper;

import java.io.UnsupportedEncodingException;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.ContentCachingResponseWrapper;

import kr.co.cont.common.constants.ErrorCode;

public class BaseResponseWrapper extends ContentCachingResponseWrapper {
	
	private ErrorCode errorCode = ErrorCode.REQUEST_SUCCESS;
	private ModelAndView modelAndView;

	public BaseResponseWrapper(HttpServletResponse response) {
		super(response);
	}
	
	@Override
	public String getContentType() {
		return StringUtils.defaultIfBlank(super.getContentType(), "");
	}
	
	public void setErrorCode(ErrorCode errorCode) {
		this.errorCode = errorCode;
	}
	
	public ErrorCode getErrorCode() {
		return this.errorCode;
	}
	
	public void setModelAndView(ModelAndView modelAndView) {
		this.modelAndView = modelAndView;
	}
	
	public ModelAndView getModelAndView() {
		return this.modelAndView;
	}

	/**
	 * 
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public String responseContent() throws UnsupportedEncodingException {
		String charset = super.getCharacterEncoding();
		byte[] bytes = super.getContentAsByteArray();
		
		return new String(bytes, charset);
	}
	
	public byte[] encryptContent() throws UnsupportedEncodingException {
		String charset = super.getCharacterEncoding();
		byte[] bytes = super.getContentAsByteArray();
		String orgMessage = new String(bytes, charset);

		String encResponseMessage = orgMessage;
		byte[] responseMessageBytes = encResponseMessage.getBytes(charset);
		
		return responseMessageBytes;
	}
}

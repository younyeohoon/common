package kr.co.cont.common.exception;


import kr.co.cont.common.biz.base.model.BaseMap;
import kr.co.cont.common.constants.ErrorCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BaseException extends RuntimeException {

	/** */
	private static final long serialVersionUID = 420538488148343052L;
	
	private ErrorCode errorCode;
	private Object[] words;
	private BaseMap body;
	
	public BaseException(ErrorCode errorCode) {
		super(errorCode.getMessage());
		this.errorCode = errorCode;
	}
	
	public BaseException(ErrorCode errorCode, Object... words) {
		super(errorCode.getMessage());
		this.errorCode = errorCode;
		this.words = words;
	}
	
	public BaseException(ErrorCode errorCode, Throwable ex) {
		super(errorCode.getMessage(), ex);
		this.errorCode = errorCode;
	}
	
	public BaseException(ErrorCode errorCode, Throwable ex, Object... words) {
		super(errorCode.getMessage(), ex);
		this.errorCode = errorCode;
		this.words = words;
	}
	
	public String getCode() {
		return this.errorCode.getCode();
	}
//	
//	public void setCode(String code) {
//		this.code = code;
//	}
//	
	@Override
	public String getMessage() {
		return this.errorCode.getMessage();
	}
//	
//	public void setMessage(String message) {
//		this.message = message;
//	}
	
	public int getStatus() {
		return this.errorCode.getStatus();
	}
	
	public void setBody(Object body) {
		if (body instanceof BaseMap) {
			this.body = (BaseMap) body;
		}
	}
}

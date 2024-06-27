package kr.co.cont.common.biz.base.model;

import kr.co.cont.common.constants.ErrorCode;
import kr.co.cont.common.exception.BaseException;
import kr.co.cont.common.util.StringUtil;

public class ResponseData {

	/* 응답결과 */
	private BaseMap header;
	
	/* 응답값 */
	private BaseMap body;
	
	public ResponseData(ErrorCode errorCode) {
		this.header = new BaseMap();
		this.body = new BaseMap();
		
		this.setStatus(errorCode.getStatus());
		this.setCode(errorCode.getCode());
		this.setMessage(errorCode.getMessage());
		
	}
	
	public ResponseData(ErrorCode errorCode, Object[] words) {
		this.header = new BaseMap();
		this.body = new BaseMap();
		
		this.setStatus(errorCode.getStatus());
		this.setCode(errorCode.getCode());
		this.setMessage(StringUtil.replace(errorCode.getMessage(), "{@}", words));
	}
	
	public ResponseData(BaseException ex) {
		this.header = new BaseMap();
		
		if (ex.getBody() != null) this.body = ex.getBody();
		else this.body = new BaseMap();
		
		this.setStatus(ex.getStatus());
		this.setCode(ex.getCode());
		this.setMessage(StringUtil.replace(ex.getMessage(), "{@}", ex.getWords()));
	}
	
	public ResponseData(BaseMap body) {
		this.header = new BaseMap();
		this.body = body;
		
		this.setStatus(ErrorCode.REQUEST_SUCCESS.getStatus());
		this.setCode(ErrorCode.REQUEST_SUCCESS.getCode());
		this.setMessage(ErrorCode.REQUEST_SUCCESS.getMessage());
	}

	public BaseMap getHeader() {
		return header;
	}

	public void setHeader(BaseMap header) {
		this.header = header;
	}

	public BaseMap getBody() {
		return body;
	}

	public void setBody(BaseMap body) {
		this.body = body;
	}

	public int getStatus() {
		return this.header.getInt("status", 200);
	}

	public void setStatus(int status) {
		this.header.put("status", status);
	}
	
	public String getCode() {
		return this.header.getString("code");
	}

	public void setCode(String code) {
		this.header.put("code", code);
	}

	public String getMessage() {
		return this.header.getString("message");
	}

	public void setMessage(String message) {
		this.header.put("message", message);
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("header : ");
		sb.append(this.header.toString());
		sb.append(", body : ");
		sb.append(this.body.toString());
		return sb.toString();
	}
}

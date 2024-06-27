package kr.co.cont.common.push.mail;

import lombok.Data;

@Data
public class EmailUserData {
	
	private String emailAddr;
	private String name;
	
	public EmailUserData() {
	}
	
	public EmailUserData(String emailAddr) {
		this.emailAddr = emailAddr;
	}
	
	public EmailUserData(String emailAddr, String name) {
		this.emailAddr = emailAddr;
		this.name = name;
	}

}

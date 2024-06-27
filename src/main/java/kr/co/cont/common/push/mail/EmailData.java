package kr.co.cont.common.push.mail;

import java.util.LinkedList;
import java.util.List;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class EmailData {

	/** 발송자 이메일 주소 */
	private EmailUserData mailFrom = new EmailUserData();
	/** 수신자 이메일 주소 */
	private List<EmailUserData> mailTo = new LinkedList<EmailUserData>();
	/** 참조자 이메일 주소 */
	private List<EmailUserData> mailCc = new LinkedList<EmailUserData>();
	/** 숨은 참조자 이메일 주소 */
	private List<EmailUserData> mailBcc = new LinkedList<EmailUserData>();
	/** 이메일 제목 */
	private String mailSubject;
	/** 이메일 내용 */
	private String mailContent;
	/** 메일 형식이 HTML인지 여부(true, false) */
	private boolean isUseHtmlYn;
	/** 첨부파일 */
	private List<EmailFileData> files = new LinkedList<EmailFileData>();

	/** 멀티파트 데이터 */
	private MultipartFile[] multipartFiles;
	/** 멀티파트 데이터 여부 */
	private boolean isMultipart = false;
	/**
	 * 발송자 정보 변경
	 * 
	 * @param fromAddr 이메일 주소
	 */
	public void putMailFrom(String fromAddr) {
		mailFrom.setEmailAddr(fromAddr);
	}

	public void setIsMultipart(boolean isMultipart){
		this.isMultipart = isMultipart;
	}
	/**
	 * 발송자 정보 변경
	 * 
	 * @param fromAddr 발송자 이메일 주소
	 * @param name 발송자 이름
	 */
	public void putMailFrom(String fromAddr, String name) {
		mailFrom.setEmailAddr(fromAddr);
		mailFrom.setName(name);;
	}
	
	
	/**
	 * 수신자 이메일 추가
	 * 
	 * @param toAddr 수신자 이메일 주소
	 */
	public void addMailTo(String toAddr) {
		this.mailTo.add(new EmailUserData(toAddr));
	}
	
	/**
	 * 수신자 이메일 추가
	 * 
	 * @param toAddr 수신자 이메일 주소
	 * @param name 수신자 이름
	 */
	public void addMailTo(String toAddr, String name) {
		this.mailTo.add(new EmailUserData(toAddr, name));
	}
	
	/**
	 * 참조자 이메일 추가
	 * 
	 * @param ccAddr 참조자 이메일 주소
	 */
	public void addMailCc(String ccAddr) {
		this.mailCc.add(new EmailUserData(ccAddr));
	}
	
	/**
	 * 참조자 이메일 추가
	 * 
	 * @param ccAddr 참조자 이메일 주소
	 * @param name 참조자 이름
	 */
	public void addMailCc(String ccAddr, String name) {
		this.mailCc.add(new EmailUserData(ccAddr, name));
	}
	
	/**
	 * 숨은 참조자 이메일 추가
	 * 
	 * @param bccAddr 숨은 참조자 이메일 주소
	 */
	public void addMailBcc(String bccAddr) {
		this.mailBcc.add(new EmailUserData(bccAddr));
	}

	/**
	 * 숨은 참조자 이메일 추가
	 * 
	 * @param bccAddr 숨은 참조자 이메일 주소
	 * @param name 숨은 참조자 이름
	 */
	public void addMailBcc(String bccAddr, String name) {
		this.mailBcc.add(new EmailUserData(bccAddr, name));
	}
	
	/**
	 * 첨부파일 추가
	 * 
	 * @param filePath 파일명(Full 경로)
	 */
	public void addFile(String filePath) {
		this.files.add(new EmailFileData(filePath));
	}
	
	/**
	 * 첨부파일 추가
	 * 
	 * @param filePath 파일명(Full 경로)
	 * @param name 파일명
	 */
	public void addFile(String filePath, String name) {
		this.files.add(new EmailFileData(filePath, name));
	}
}

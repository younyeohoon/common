package kr.co.cont.common.push;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import kr.co.cont.common.push.mail.EmailData;
import kr.co.cont.common.push.mail.EmailSender;
import kr.co.cont.common.push.mail.EmailUserData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@Component
public class EmailProvider extends EmailSender {

	public EmailProvider(JavaMailSender mailSender) {
		super(mailSender);
	}
	
	/**
	 * 대표이메일을 이용하여 수신자에게 메일을 발송한다.
	 * 
	 * @param toAddr 수신자 이메일
	 * @param subject 이메일 제목
	 * @param content 이메일 내용
	 * @return 메일 발송 결과
	 */
	public boolean send(String toAddr, String subject, String content) {
		if (log.isDebugEnabled()) log.debug("");
		EmailData mailData = new EmailData();
		mailData.putMailFrom("info@cont.co.kr", "고객센터");
		mailData.addMailTo(toAddr);
		mailData.setMailSubject(subject);
		mailData.setMailContent(content);
		mailData.setUseHtmlYn(true);
		
		return this.send(mailData);
	}

	/**
	 * 대표이메일을 이용하여 수신자에게 메일을 일괄 발송한다.
	 *
	 * @param toAddrs 수신자 이메일 목록
	 * @param subject 이메일 제목
	 * @param content 이메일 내용
	 * @return 메일 발송 결과
	 */
	public boolean send(List<String> toAddrs, String subject, String content) {
		EmailData mailData = new EmailData();
		mailData.putMailFrom("info@cont.co.kr", "고객센터");
		toAddrs.forEach(toAddr -> { mailData.addMailTo(toAddr); });
		mailData.setMailSubject(subject);
		mailData.setMailContent(content);
		mailData.setUseHtmlYn(true);

		return this.send(mailData);
	}
	
	/**
	 * 발송자를 지정하여 이메일을 발송한다.
	 * 
	 * @param frData 발송자 정보(이메일, 이름)
	 * @param toAddr 수신자 이메일
	 * @param subject 이메일 제목
	 * @param content 이메일 내용
	 * @return 메일 발송 결과
	 */
	public boolean send(EmailUserData frData, String toAddr, String subject, String content) {
		if (log.isDebugEnabled()) log.debug("frData, toAddr, subject, content = {}", frData, toAddr, subject, content);
		EmailData mailData = new EmailData();
		mailData.putMailFrom(frData.getEmailAddr(), frData.getName());
		mailData.addMailTo(toAddr);
		mailData.setMailSubject(subject);
		mailData.setMailContent(content);
		mailData.setUseHtmlYn(true);

		return this.send(mailData);
	}
	
	/**
	 * 첨부파일을 지정하여 이메일을 발송한다.
	 * 
	 * @param frData 발송자 정보(이메일, 이름)
	 * @param toAddr 수신자 이메일
	 * @param subject 이메일 제목
	 * @param content 이메일 내용
	 * @param filePath 첨부파일 경로
	 * @return 메일 발송 결과
	 */
	public boolean send(EmailUserData frData, String toAddr, String subject, String content, String filePath) {
		if (log.isDebugEnabled()) 
			log.debug("frData, toAddr, subject, content = {} , filePath = {}", frData, toAddr, subject, content, filePath);
		EmailData mailData = new EmailData();
		mailData.putMailFrom(frData.getEmailAddr(), frData.getName());
		mailData.addMailTo(toAddr);
		mailData.setMailSubject(subject);
		mailData.setMailContent(content);
		mailData.setUseHtmlYn(true);
		mailData.addFile(filePath);
		
		return this.send(mailData);
	}

	/**
	 * 첨부파일을 받아서 이메일을 발송한다.
	 *
	 * @param frData 발송자 정보(이메일, 이름)
	 * @param toAddr 수신자 이메일
	 * @param subject 이메일 제목
	 * @param content 이메일 내용
	 * @param multipartFiles 첨부파일 데이터
	 * @return 메일 발송 결과
	 */
	public boolean send(EmailUserData frData, String toAddr, String subject, String content, MultipartFile[] multipartFiles) {
		if (log.isDebugEnabled())
			log.debug("frData, toAddr, subject, content = {} , multipartFiles is null = {}", frData, toAddr, subject, content, multipartFiles == null);
		EmailData mailData = new EmailData();
		mailData.putMailFrom(frData.getEmailAddr(), frData.getName());
		mailData.addMailTo(toAddr);
		mailData.setMailSubject(subject);
		mailData.setMailContent(content);
		mailData.setUseHtmlYn(true);
		mailData.setMultipartFiles(multipartFiles);
		mailData.setIsMultipart(true);
		return this.send(mailData);
	}
	
	/**
	 * 참조자를 지정하여 이메일을 발송한다.
	 * 
	 * @param frData 발송자 정보(이메일, 이름)
	 * @param toAddr 수신자 이메일
	 * @param subject 이메일 제목
	 * @param content 이메일 내용
	 * @param filePath 첨부파일 경로
	 * @param ccAddr 참조자 이메일
	 * @param bccAddr 숨은 참조자 이메일
	 * @return 메일 발송 결과
	 */
	public boolean send(EmailUserData frData, String toAddr, String subject, String content
			, String filePath, String ccAddr, String bccAddr) {
		if (log.isDebugEnabled()) 
			log.debug("frData, toAddr, subject, content = {} , filePath = {}", frData, toAddr, subject, content, filePath);
		EmailData mailData = new EmailData();
		mailData.putMailFrom(frData.getEmailAddr(), frData.getName());
		mailData.addMailTo(toAddr);
		mailData.setMailSubject(subject);
		mailData.setMailContent(content);
		mailData.setUseHtmlYn(true);
		mailData.addFile(filePath);
		mailData.addMailCc(ccAddr);
		mailData.addMailBcc(bccAddr);
		
		return this.send(mailData);
	}

	/**
	 * 수신자, 참조자, 숨은참조자 일괄 메일 발송한다.
	 *
	 * @param frData 발송자 정보(이메일, 이름)
	 * @param toAddrs 수신자 이메일 목록
	 * @param subject 이메일 제목
	 * @param content 이메일 내용
	 * @param filePath 첨부파일 경로
	 * @param ccAddrs 참조자 이메일 목록
	 * @param bccAddrs 숨은 참조자 이메일 목록
	 * @return 메일 발송 결과
	 */
	public boolean send(EmailUserData frData, List<String> toAddrs, String subject, String content
			, MultipartFile[] multipartFiles, List<String> ccAddrs, List<String> bccAddrs) {

		EmailData mailData = new EmailData();
		mailData.putMailFrom(frData.getEmailAddr(), frData.getName());
		toAddrs.forEach(toAddr -> {mailData.addMailTo(toAddr);});
		mailData.setMailSubject(subject);
		mailData.setMailContent(content);
		mailData.setUseHtmlYn(true);
		mailData.setMultipartFiles(multipartFiles);
		mailData.setIsMultipart(true);
		if (ccAddrs != null) ccAddrs.forEach(ccAddr -> { mailData.addMailCc(ccAddr); });
		if (bccAddrs != null) bccAddrs.forEach( bccAddr -> {mailData.addMailBcc(bccAddr);});

		return this.send(mailData);
	}
}

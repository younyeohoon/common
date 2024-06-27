package kr.co.cont.common.push.mail;

import java.io.UnsupportedEncodingException;
import java.util.List;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

/**
 * Email 발송 기능
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class EmailSender {

	final private JavaMailSender mailSender;
	
	/**
	 * 이메일을 발송한다.
	 * 
	 * @param mailData
	 * @return
	 */
	protected boolean send(EmailData mailData) {
		return this.sendEmail(mailData);
	}

	
	/**
	 * 이메일을 발송한다.
	 * 
	 * @param mailData
	 * @return
	 */
	private boolean sendEmail(EmailData mailData) {
		
		if (log.isDebugEnabled()) log.debug("mailData = {}", mailData);
		
		MimeMessage mimeMessage = mailSender.createMimeMessage();

		try {
			MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
			
			EmailUserData emailUserData = mailData.getMailFrom();
			String fromAddr = emailUserData.getEmailAddr();
			
			InternetAddress[] toAddress = listToArray(mailData.getMailTo(), "UTF-8");
			InternetAddress[] ccAddress = listToArray(mailData.getMailCc(), "UTF-8");
			InternetAddress[] bccAddress = listToArray(mailData.getMailBcc(), "UTF-8");

			helper.setSubject(MimeUtility.encodeText(mailData.getMailSubject(), "UTF-8", "B")); // Base64 encoding
			helper.setText(mailData.getMailContent(), mailData.isUseHtmlYn());
			helper.setFrom(new InternetAddress(fromAddr, emailUserData.getName(), "UTF-8"));
			helper.setTo(toAddress);
			helper.setCc(ccAddress);
			helper.setBcc(bccAddress);
			if(mailData.isMultipart()) {
				MultipartFile[] list = mailData.getMultipartFiles();
				if(list != null && list.length > 0) {
					for (MultipartFile file: list ) {
						String fileName = file.getOriginalFilename();
						try {
							helper.addAttachment(MimeUtility.encodeText(fileName, "UTF-8", "B"), file);
						} catch (UnsupportedEncodingException e) {
							log.error("첨부파일 추가 오류 :: {}, {}", fileName, e);
						} catch (MessagingException e) {
							log.error("첨부파일 추가 오류 :: {}, {}", fileName, e);
						}
					}
				}
			} else {
				mailData.getFiles().forEach( d -> {
					String fileName = d.getName();
					try {
						helper.addAttachment(MimeUtility.encodeText(fileName, "UTF-8", "B"), d.getFile());
					} catch (UnsupportedEncodingException e) {
						log.error("첨부파일 추가 오류 :: {}, {}", fileName, e);
					} catch (MessagingException e) {
						log.error("첨부파일 추가 오류 :: {}, {}", fileName, e);
					}
				});
			}

			mailSender.send(mimeMessage);
			
		} catch (MessagingException e) {
			log.error("이메일 발송 오류 :: ", e);
		} catch (UnsupportedEncodingException e) {
			log.error("이메일 발송 오류 :: ", e);
		}
		
		if (log.isDebugEnabled()) log.debug("[Done] mailData = {}", mailData);
		
		return true;

	}

	
	/**
	 * 이메일 수신자 List에서 이메일 주소와 이름을 추출하여
	 * InternetAddress 배열로 변환한다.
	 * 
	 * @param list
	 * @return
	 */
	private InternetAddress[] listToArray(List<EmailUserData> list,  String charset) {
		int arrListSize = list.size();
		
		InternetAddress[] internetAddresses = new InternetAddress[arrListSize];
		
		for (int i = 0 ; i < arrListSize; i++) {
			EmailUserData emailUserData = list.get(i);
			String emailAddr = emailUserData.getEmailAddr();
			String name = emailUserData.getName();
			try {
				internetAddresses[i] = new InternetAddress(emailAddr, name, charset);
			} catch (UnsupportedEncodingException e) {
				log.error("이메일 주소 설정 오류 :: {}[{}] , {}", emailAddr, name, e);
			}
		}
		return internetAddresses;
	}
}

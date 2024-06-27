package kr.co.cont.common.push;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;

import kr.co.cont.common.push.mail.EmailUserData;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ExtendWith(SpringExtension.class) // Junit4의 Runwith과 같은 기능을 하는 Junit5 어노테이션
@ContextConfiguration(locations = {"file:src/test/resources/config/spring/mvc-servlet.xml", "file:src/test/resources/config/spring/context-*.xml" })
@WebAppConfiguration
@TestMethodOrder(MethodOrderer.OrderAnnotation.class) // Order를 붙일 때 사용
class EmailProviderTest {

	@Autowired
	private EmailProvider emailProvider;
	
	@Test
	void testSendEmailData() {
		log.debug("이메일 테스트 입니다.");
		
		EmailUserData frData = new EmailUserData("chatterbox@cont.co.kr", "윤여훈");
		String toAddr = "younyeohoon@gmail.com";
		String subject = "This is Email test.";
		String content = "Learn how to send email using Spring.";
		String filePath = "C:\\Projects\\cont\\photo_web\\webapp\\webContents\\data\\file\\upload\\20230901\\1450001090320200012.png";
		String ccAddr = "john_vianney@daum.net";
		String bccAddr = "john_vianney@naver.com";
		
		StringBuilder sb = new StringBuilder();
		sb.append("<a href='http://localhost:8080/photo/main/auth?token=");
//		sb.append(token);
		sb.append("'>");
		sb.append("이메일 인증</a>");
		
		content = sb.toString();
		
//		emailProvider.send(toAddr, subject, content);
//		emailProvider.send(frData, toAddr, subject, content);
//		emailProvider.send(frData, toAddr, subject, content, filePath);
		emailProvider.send(frData, toAddr, subject, content, filePath, ccAddr, bccAddr);
	}

}

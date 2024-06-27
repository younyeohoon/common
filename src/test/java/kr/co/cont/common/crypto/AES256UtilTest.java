package kr.co.cont.common.crypto;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ExtendWith(SpringExtension.class) // Junit4의 Runwith과 같은 기능을 하는 Junit5 어노테이션
@ContextConfiguration(locations = {"file:src/test/resources/config/spring/mvc-servlet.xml", "file:src/test/resources/config/spring/context-*.xml" })
@WebAppConfiguration
@TestMethodOrder(MethodOrderer.OrderAnnotation.class) // Order를 붙일 때 사용
class AES256UtilTest {
	
	@Autowired
	private AES256Util aes256Util;

	@DisplayName("AES256 암호화")
	@Test
	void testEncrypt() {
		String planStr = "This is Test";
		String encStr = "";
		String decStr = "";
		
		// 암호화
		encStr = aes256Util.encrypt(planStr);
		// 복호화
		decStr = aes256Util.decrypt(encStr);
		
		log.debug("encStr = {}, decStr = {}", encStr, decStr);
		
		assertTrue(planStr.equals(decStr));
	}

	@DisplayName("AES256 복호화")
	@Test
	void testDecrypt() {
		String planStr = "This is Test";
		String encStr = "";
		String decStr = "";
		
		// 암호화
		encStr = aes256Util.encrypt(planStr);
		// 복호화
		decStr = aes256Util.decrypt(encStr);
		
		log.debug("encStr = {}, decStr = {}", encStr, decStr);
		
		assertTrue(planStr.equals(decStr));
	}
}

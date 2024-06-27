package kr.co.cont.common.crypto;

import static org.junit.jupiter.api.Assertions.*;

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
class CryptoTest {

	@Autowired
	Crypto crypto;
	
	@DisplayName("AES256 암복호화")
	@Test
	void testCrypto() {
		String plnStr = "This is Test";
		String encStr = "";
		String decStr = "";
		
		encStr = crypto.enc(plnStr);
		decStr = crypto.dec(encStr);
		
		log.debug("평문 = {}, 암호문 = {}, 복호문 = {}",plnStr , encStr, decStr);
		
		assertTrue(plnStr.equals(decStr));
	}

	@DisplayName("SHA256 암복호화")
	@Test
	void testCiper() {
		String plnStr = "This is Test";
		String saltStr = "12345";
		
		String encStr1 = crypto.ciper(plnStr, saltStr);
		String encStr2 = crypto.ciper(plnStr, saltStr);
		
		log.debug("평문 = {}, 솔트값 = {}, 암호문1= {}, 암호문2 = {}", plnStr, saltStr, encStr1, encStr2);
		
		assertTrue(encStr1.equals(encStr2));

	}

}

package kr.co.cont.common.token;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ExtendWith(SpringExtension.class) // Junit4의 Runwith과 같은 기능을 하는 Junit5 어노테이션
@ContextConfiguration(locations = {"file:src/test/resources/config/spring/mvc-servlet.xml", "file:src/test/resources/config/spring/context-*.xml" })
@WebAppConfiguration
@TestMethodOrder(MethodOrderer.OrderAnnotation.class) // Order를 붙일 때 사용
class JwtTokenProviderTest {

	@Autowired
	private JwtTokenProvider provider;
	
	@Test
	void testGetClaimsJwsOfClaimsString() {
		String token = provider.createToken("1d", "테스트 토큰", "user", null);
		log.debug("creat Token = {}", token);
		
		Jws<Claims> claims = provider.getClaims(token);
		log.debug("검증 = {}", claims);
		
		boolean isToken = provider.validateToken(claims);
		log.debug("check isToken = {}", isToken);
		
		String key = provider.getKey(claims);
		log.debug("token key = {}", key);
		
		
		Object email = provider.getClaims(claims, "email");
		log.debug("key email = {} , {}", email, email.getClass().getName());
		
		Object user = provider.getClaims(claims, "user");
		log.debug("key user = {} , {}", user, user.getClass().getName());
		
	}

}

package kr.co.cont.common.token;

import java.security.Key;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.jackson.io.JacksonDeserializer;
import io.jsonwebtoken.jackson.io.JacksonSerializer;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import kr.co.cont.common.biz.base.model.BaseMap;
import kr.co.cont.common.cache.BaseCache;
import kr.co.cont.common.constants.ErrorCode;
import kr.co.cont.common.exception.BaseException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JwtTokenProvider {

	// 토큰 발급자
	private String issuer;
	
	/** 암호화하기 위한 키 */ 
	private Key SECRET_KEY;
	
	@Autowired
	private BaseCache baseCache;
	
	@PostConstruct
	private void init() {
		this.issuer = baseCache.propertyValue("jwt.token.issuer");
		String secertKey = baseCache.propertyValue("jwt.token.secert.key");
		
		// key 생성
		if (log.isDebugEnabled()) log.debug("생성자 secertKey = {}", secertKey);
		byte[] secertByteKey = Decoders.BASE64.decode(secertKey);
		SECRET_KEY = Keys.hmacShaKeyFor(secertByteKey);
//		SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);
	}

	/**
	 * 토큰 생성 함수
	 * 
	 * @param expired 토큰만료기간 (숫자 + 구분자(d:일, h:시, m:분) ex) 1d, 2d, 3h, 30mm
	 * @param subject 토큰 제목
	 * @param baseMap 토큰 수신자/대상자
	 * @param baseMap 토큰에 추가할 값
	 * @return
	 */
	public String createToken(String expired, String subject, String audience, BaseMap baseMap) {
		// Claims을 생성
		Claims claims = Jwts.claims();
		// Payload 데이터 추가
		if (baseMap != null) {
			baseMap.keySet().forEach(k -> {
				String key = k.toString();
				claims.put(key, baseMap.getString(key));
			});
		}
		
		// 토큰 만료 시간
		long lng = Long.parseLong(expired.replaceAll("[^0-9]", ""));
		Date exp ;
		switch (expired.replaceAll("[0-9]", "")) {
			case "d":
				exp =Date.from(Instant.now().plus(Duration.ofDays(lng)));
				break;
			case "h":
				exp =Date.from(Instant.now().plus(Duration.ofHours(lng)));
				break;
			default:
				exp =Date.from(Instant.now().plus(Duration.ofMinutes(lng)));
				break;
		}
		
		
		// 토큰이 발급된 시간, 토큰의 age가 얼마나 되었는지 판단 가능
		Date iat = Date.from(Instant.now());
		// 토큰의 활성 날짜
		Date nbf = Date.from(Instant.now());
		//  JWT의 고유 식별자, 주로 중복 처리 방지를 위해 사용, 일회용 토큰에 사용하면 유용
		String jti = UUID.randomUUID().toString();
		
		// JWT 토큰을 만드는데, Payload 정보와 생성시간, 만료시간, 알고리즘 종류와 암호화 키를 넣어 암호화 함.
		return Jwts.builder()
				.serializeToJsonWith(new JacksonSerializer<>(new ObjectMapper()))
//				.setClaims(Map.of("email", email, "user", user))
				.setClaims(claims)
				.setIssuer(this.issuer)
				.setSubject(subject)
				.setAudience(audience)
				.setExpiration(exp)
				.setNotBefore(nbf)
				.setIssuedAt(iat)
				.setId(jti)
				.signWith(SECRET_KEY, SignatureAlgorithm.HS256)
				.compact();
	}
	
	/**
	 * 
	 * @param token
	 * @return
	 */
	public BaseMap checkToken(String jwsToken) {
		if(log.isDebugEnabled()) log.debug("jwsToken = {}", jwsToken);
		
		Jws<Claims> claims = getClaims(jwsToken);
		if(log.isDebugEnabled()) log.debug("검증 = {}", claims);
		
		boolean isToken = validateToken(claims);
		if (!isToken) {
			throw new BaseException(ErrorCode.TOKEN_UNAUTHORIZED);
		}
		log.debug("check isToken = {}", isToken);
		
		return new BaseMap(claims.getBody());
	}

	/**
	 * String으로 된 코드를 복호화한다.
	 * 
	 * @param jws
	 * @return
	 */
	public Jws<Claims> getClaims(String jws) {
		
		try {
			// 암호화 키로 복호화한다.
			// 즉 암호화 키가 다르면 에러가 발생한다.
			JwtParser jwtSubject = Jwts.parserBuilder()
					.deserializeJsonWith(new JacksonDeserializer<>(new ObjectMapper()))
					.setSigningKey(SECRET_KEY)
					.build();
			
			return jwtSubject.parseClaimsJws(jws);
		} catch (UnsupportedJwtException e) {
			// 예상하는 형식과 일치하지 않는 특정 형식이나 구성의 JWT일 때
			throw new BaseException(ErrorCode.TOKEN_BAD_REQUEST, e);
		} catch (MalformedJwtException e) {
			// JWT가 올바르게 구성되지 않았을 때
			throw new BaseException(ErrorCode.TOKEN_BAD_REQUEST, e);
		} catch (SignatureException e) {
			// JWT의 기존 서명을 확인하지 못했을 때
			throw new BaseException(ErrorCode.TOKEN_BAD_REQUEST, e);
		} catch (ExpiredJwtException e) {
			// JWT를 생성할 때 지정한 유효기간 초과할 때.
			throw new BaseException(ErrorCode.TOKEN_EXPIRED, e);
		} catch (IllegalArgumentException e) {
			//if the claimsJws string is null or empty or only whitespace
			throw new BaseException(ErrorCode.TOKEN_BAD_REQUEST, e);
		}
		
	}

	/**
	 * 토큰 만료 시간이 현재 시간을 지났는지 검증
	 * 
	 * @param claims
	 * @return
	 */
	public boolean validateToken(Jws<Claims> claims) {
		if(log.isDebugEnabled()) log.debug("getExpiration = {}", claims.getBody().getExpiration());
		return !claims.getBody().getExpiration().before(new Date());
	}

	/**
	 * 토큰을 통해 Payload의 ID를 취득
	 * 
	 * @param claims
	 * @return
	 */
	public String getKey(Jws<Claims> claims) {
		return claims.getBody().getId();
	}

	/**
	 * 토큰을 통해 Payload의 데이터를 취득
	 * 
	 * @param claims
	 * @param key
	 * @return
	 */
	public Object getClaims(Jws<Claims> claims, String key) {
		return claims.getBody().get(key);
	}

	/**
	 * 토큰 정보 취득
	 * 
	 * @param req
	 * @return
	 */
	public String resolveToken(HttpServletRequest req) {
		return StringUtils.defaultIfBlank(req.getHeader("X-AUTH-TOKEN"), "");
	}
}

package kr.co.cont.common.crypto;

import java.security.PrivateKey;

import org.springframework.stereotype.Component;

import kr.co.cont.common.biz.base.model.BaseMap;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class Crypto {

//	encText
//	decText	
//	평문(Plaintext) : 해독 가능한 형태의 메시지(암호화전 메시지)
//	암호문(Cipertext) : 해독 불가능한 형태의 메시지(암호화된 메시지)
//	암호화(Encryption) : 평문을 암호문으로 변환하는 과정
//	복호화(Decryption) : 암호문을 평문으로 변환하는 과정	
	

	private final AES256Util aes256Util;
	private final SHA256Util sha256Util;
	private final RSAUtil rsaUtil;
	
	/**
	 * AES256을 이용한 암호화
	 * 
	 * @param ecryptText
	 * @return
	 */
	public String enc(String ecryptText) {
		return aes256Util.encrypt(ecryptText);
	}
	
	/**
	 * AES256을 이용한 복호화
	 * 
	 * @param ecryptText
	 * @return
	 */
	public String dec(String ecryptText) {
		return aes256Util.decrypt(ecryptText);
	}
	
	/**
	 * SHA256을 이용한 암호화
	 * 
	 * @param ecryptText
	 * @param salt
	 * @return
	 */
	public String ciper(String ecryptText, String salt) {
		return sha256Util.encrypt(ecryptText, salt);
	}
	
	/**
	 * RSA 암호화 키 생성
	 * 
	 * @return
	 */
	public BaseMap createKey() {
		return rsaUtil.createRSA();
	}
	
	/**
	 * RAS를 이용한 암호화
	 *  
	 * @param publicKey
	 * @param ecryptText
	 * @return
	 */
	public String enc(PrivateKey publicKey, String ecryptText) {
		return rsaUtil.encrypt(publicKey, ecryptText);
	}
	
	/**
	 * RAS를 이용한 복호화
	 * 
	 * @param privateKey
	 * @param ecryptText
	 * @return
	 */
	public String dec(PrivateKey privateKey, String ecryptText) {
		return rsaUtil.decrypt(privateKey, ecryptText);
	}
}

package kr.co.cont.common.net.aws;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

import kr.co.cont.common.biz.base.model.BaseMap;
import kr.co.cont.common.cache.BaseCache;
import kr.co.cont.common.constants.BaseConstants;
import kr.co.cont.common.net.http.HttpProvider;
import kr.co.cont.common.util.DateUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.DeleteObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedDeleteObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

@Slf4j
@RequiredArgsConstructor
@Component
public class AwsS3Provider {

	// 액세스키
	private String accessKey;
	// 시크릿키
	private String secretKey;
	// 위치
	private Region region;

	private String bucket;
	private String cloudFrontName;
	private String imgRoot;
	private String docRoot;

	private S3Presigner presigner; 
	
	private final HttpProvider httpProvider;
	private final BaseCache baseCache;
	
	@PostConstruct
	public void  init() {
		
		// 액세스키
		this.region = Region.AP_NORTHEAST_2;
		this.accessKey = baseCache.propertyValue("aws.s3.photo.access.key");
		this.secretKey = baseCache.propertyValue("aws.s3.photo.secret.key");
		this.bucket = baseCache.propertyValue("aws.s3.photo.bucket");
		this.cloudFrontName = baseCache.propertyValue("aws.s3.photo.cloudfront.url"); 
		this.imgRoot = baseCache.propertyValue("aws.s3.photo.cloudfront.img.root");
		this.docRoot = baseCache.propertyValue("aws.s3.photo.cloudfront.doc.root"); // "doc/"
		
		presigner = S3Presigner.builder()
				.region(region)
				.credentialsProvider(() -> AwsBasicCredentials.create(accessKey, secretKey))
				.build();
			
	}
	
	// 미리 서명된 객체 가져오기
	public String getFile(String keyName) {
		if (log.isDebugEnabled()) log.debug("keyName = {}", keyName);
		
		GetObjectRequest objectRequest = GetObjectRequest.builder()
				.bucket(this.bucket)
				.key(keyName)
				.build();
		
		GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
				.signatureDuration(Duration.ofMinutes(60))
				.getObjectRequest(objectRequest)
				.build();
		
		PresignedGetObjectRequest presignedRequest = presigner.presignGetObject(presignRequest);
		
		String requstUrl = presignedRequest.url().toString();
		try {
			requstUrl = URLDecoder.decode(requstUrl, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			log.error("URLDecoder Error", e);
		}
		
//		String responseBody =  httpProvider.getSend(requstUrl, new BaseMap(), String.class, false);
		return requstUrl;
	}
	
	/**
	 * AWS S3에 파일을 등록한다.
	 * 미리 서명된 URL 생성 및 객체 업로드 
	 * 
	 * @param keyName 저장할 파일명
	 * @param ext 저장할 파일 확장자
	 * @param multipartFile 저장할 파일 
	 * @return 파일 저장 위치
	 */
	public BaseMap putFile(String fileSaveName, String ext, String contentType, byte[] fileByte) {
		
		BaseMap fileMap = new BaseMap();

		try {
			String linkRoot;
			
			if (contentType.startsWith("image")) {
				linkRoot = this.imgRoot + DateUtil.getCurrentDateString() + "/";
			} else {
				linkRoot = this.docRoot + DateUtil.getCurrentDateString() + "/";
			}
			
			// 파일의 메타정보 설정
			Map<String, String> metaMap = new HashMap<>();
//			metadata.put("author", "Mary Doe");
//			metadata.put("version", "1.0.0.0");
			
			PutObjectRequest objectRequest = PutObjectRequest.builder()
					.bucket(this.bucket).key(linkRoot + fileSaveName + "." + ext)
					.contentType(contentType).metadata(metaMap).build();

			PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
					.signatureDuration(Duration.ofMinutes(10)).putObjectRequest(objectRequest).build();
			
			PresignedPutObjectRequest presignedRequest = presigner.presignPutObject(presignRequest);
			
			// Upload content to the Amazon S3 bucket by using this URL.
			String requstUrl = URLDecoder.decode(presignedRequest.url().toString(), "UTF-8");
			
			// 헤더 설정
			BaseMap header = new BaseMap();
			header.put("Content-Type", contentType);
			for (String key : metaMap.keySet()) {
				header.put("x-amz-meta-" + key, metaMap.get(key));
			}
			
			// 파일 업로드 요청
			httpProvider.putSend(requstUrl, header, fileByte, Void.class, false);
			
			// 파일저장 경로
			fileMap.put("fileSavePath", cloudFrontName + linkRoot);
			fileMap.put("fileLinkRoot", linkRoot);
			fileMap.put("fileSaveYn", BaseConstants.USE_YES);
			
		} catch (Exception e) {
			log.error("파일 업로드 오류!!", e);
			fileMap.put("fileSaveYn", BaseConstants.USE_NO);
			fileMap.put("errorMessage", e.getCause().getMessage());
		} catch (Throwable e) {
			log.error("파일 업로드 오류!!", e);
			fileMap.put("fileSaveYn", BaseConstants.USE_NO);
			fileMap.put("errorMessage", e.getCause().getMessage());
		}
		
		return fileMap;
	}
	
	/**
	 * 파일 삭제
	 * 
	 * @param keyName
	 */
	public String deleteFile(String keyName) {
		if (log.isDebugEnabled()) log.debug("keyName = {}", keyName);
		
		DeleteObjectRequest objectRequest = DeleteObjectRequest.builder()
				.bucket(this.bucket)
				.key(keyName)
				.build();
		
		DeleteObjectPresignRequest presignRequest = DeleteObjectPresignRequest.builder()
				.signatureDuration(Duration.ofMinutes(60))
				.deleteObjectRequest(objectRequest)
				.build();
		
		PresignedDeleteObjectRequest presignedRequest = presigner.presignDeleteObject(presignRequest);
		
		String requstUrl = presignedRequest.url().toString();
		try {
			requstUrl = URLDecoder.decode(requstUrl, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			log.error("URLDecoder Error", e);
		}
		
		return httpProvider.deleteSend(requstUrl, new BaseMap(), String.class, false);
	}
}

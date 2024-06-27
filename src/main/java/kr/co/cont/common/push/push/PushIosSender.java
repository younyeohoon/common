package kr.co.cont.common.push.push;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ExecutionException;

import javax.net.ssl.SSLException;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ResourceUtils;

import com.eatthepath.pushy.apns.ApnsClient;
import com.eatthepath.pushy.apns.ApnsClientBuilder;
import com.eatthepath.pushy.apns.PushNotificationResponse;
import com.eatthepath.pushy.apns.auth.ApnsSigningKey;
import com.eatthepath.pushy.apns.util.SimpleApnsPayloadBuilder;
import com.eatthepath.pushy.apns.util.SimpleApnsPushNotification;
import com.eatthepath.pushy.apns.util.TokenUtil;
import com.eatthepath.pushy.apns.util.concurrent.PushNotificationFuture;

@Slf4j
public class PushIosSender {

	private InetAddress[] dnsresult;
	private File pkcs8File ;
	private String teamId;
	private String keyId;
	private String topic;


	public PushIosSender() {
		init();
	}
	
//	private File p12File;
//	private String p12Password;
	
//	@PostConstruct
	private void init() {

		try {
			pkcs8File = ResourceUtils.getFile("classpath:config/AuthKey_25XFYUG983.p8");
		} catch (FileNotFoundException e) {
			pkcs8File = new File("/Users/younyeohoon/Project/cont/photo/src/main/resources/config/AuthKey_25XFYUG983.p8");
			e.printStackTrace();
		}
		
		teamId = "8MR36DA472";
		keyId = "25XFYUG983";
		topic = "kr.co.cont.photo";
		
		// 
		try {
			dnsresult= InetAddress.getAllByName(ApnsClientBuilder.DEVELOPMENT_APNS_HOST);
		} catch (UnknownHostException e) {
			dnsresult = new InetAddress[0];
//			log.error("IOS Push 초기 설정 오류!!", e);
		}
	}

	public static void main(String[] args) throws InvalidKeyException, SSLException, NoSuchAlgorithmException, IOException, InterruptedException {
		PushIosSender app = new PushIosSender();
//		app.init();
		/// 내꺼
		app.send("72a6502dd7fcb4811c8d4411d12628cf540c8b97dc3e995543d857736f3e7634","보내는사람", "안녕하세요오" , "2023101700073");
//		app.send("c5e02ade7c9033f63e8ecaf1c24b8123b431305be816501ce5e25c58d085eda8","테스트플라이트 푸쉬 테스트입니다", "안녕하세요오" , "2023101700073");
	}
	
	public void send(String deviceToken, String title, String body, String chatNo) throws SSLException, IOException, InterruptedException, InvalidKeyException, NoSuchAlgorithmException {
	
		// push를 ip로 보내기 위해 push서버에서 ip를 가져온다
		for (int i = 0; i < dnsresult.length; i++) {
			if (log.isDebugEnabled()) log.debug("\n\n\n" + dnsresult[i].getHostAddress());

			final ApnsClient apnsClient = new ApnsClientBuilder()
//					.setApnsServer(StringUtils.substring(String.valueOf(dnsresult[i]),19),443)  // ip를 가져와서 파싱하고 port로 443을 넣어주었다
//					.setApnsServer(ApnsClientBuilder.DEVELOPMENT_APNS_HOST) // XXX : xcode 에서 설치할때..
					.setApnsServer(ApnsClientBuilder.PRODUCTION_APNS_HOST) // XXX : 위의 상황을 제외한..
					.setSigningKey(ApnsSigningKey.loadFromPkcs8File(pkcs8File, teamId, keyId))
//					.setClientCredentials(p12File, p12Password) //APNS서버를 만들때 발급받은 인증서파일과 인증서를 발급받을때 사용한 password를 입력한다.
					.build();

			// 이부분은 apple push에서 DATA부분에 들어갈 내용이다.
			// 이상태로 보내면 payload에 아래 내용으로 들어간다
			// {"aps":{"alert":{"body":"Example!","title":"Apple Push"}},"customProps":"customValue"}
			final String payload = new SimpleApnsPayloadBuilder()
					.setAlertTitle(title)
					.setAlertBody(body)
//					.setCategoryName("chat")
					.addCustomProperty("chatNo",chatNo)

					.build();

			// 아이폰에서 받은 토큰을 넣어준다
			final String token = TokenUtil.sanitizeTokenString(deviceToken);

			// 위에 만든 payload와 token, 그리고 topic까지 넣어서 noti 객체를 만들어준다
			final SimpleApnsPushNotification pushNotification = new SimpleApnsPushNotification(token, topic, payload);

			try {
				final PushNotificationFuture<SimpleApnsPushNotification, PushNotificationResponse<SimpleApnsPushNotification>> sendNotificationFuture 
					= apnsClient.sendNotification(pushNotification); // 메시지 전송해주는부분

				final PushNotificationResponse<SimpleApnsPushNotification> pushNotificationResponse 
					= sendNotificationFuture.get();
				
				// 보낸 메시지에대한 response를 받을 수 있다.
				if (pushNotificationResponse.isAccepted()) {
					// 성공했을때는 apns client를 종료하고 나감
					if (log.isDebugEnabled()) log.debug("Push notification accepted by APNs gateway.");
					apnsClient.close();
					break;
				} else {
					// 실패했을때는 apns client를 종료하고 다음 ip로 전송함
					if (log.isDebugEnabled()) log.debug("Notification rejected by the APNs gateway: {}", pushNotificationResponse.getRejectionReason());
					apnsClient.close();
					continue;

				}
			} catch (final ExecutionException e) {
				log.error("Failed to send push notification.");
				e.printStackTrace();
				apnsClient.close();
			}
		}
	}
}

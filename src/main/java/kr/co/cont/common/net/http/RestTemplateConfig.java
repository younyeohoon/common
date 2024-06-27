package kr.co.cont.common.net.http;

import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.apache.http.HeaderElement;
import org.apache.http.HeaderElementIterator;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeaderElementIterator;
import org.apache.http.pool.PoolStats;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.client.RestTemplate;

import kr.co.cont.common.cache.BaseCache;
import kr.co.cont.common.handler.RestTemplateResponseErrorHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@EnableScheduling
public class RestTemplateConfig {
	
	private int requestTimeout;	// 서버 연결을 맺을 때 타임아웃(ms)
	private int connectTimeout;	// 커넥션 풀로부터 꺼내올 때 타임아웃(ms)
	private int socketTimeout;	// 요청/응답 간 타임아웃 (read time out)(ms)
	private int idleTimeout;	// 
	private int maxTotal;	// 최대 오픈되는 커넥션 수
	private int maxPerRoute;	// 
	private int keepAliveTimeout;	// 20 * 1000
	private String instance = System.getProperty("instance");
	
	@Autowired
	private BaseCache baseCache;
	
	@Autowired
	private RestTemplateResponseErrorHandler restTemplateResponseErrorHandler;
	
	@PostConstruct
	private void init() {
		this.requestTimeout = baseCache.propertyInt("http.connection.request.timeout");
		this.connectTimeout = baseCache.propertyInt("http.connection.connect.timeout");
		this.socketTimeout = baseCache.propertyInt("http.connection.socket.timeout");
		this.keepAliveTimeout = baseCache.propertyInt("http.connection.keepAlive.timeout");
		this.idleTimeout = baseCache.propertyInt("http.connection.idle.timeout"); 
		this.maxTotal = baseCache.propertyInt("http.connection.pool.max.total");
		this.maxPerRoute = baseCache.propertyInt("http.connection.pool.max.per.route"); 
	}

	@Bean
	public PoolingHttpClientConnectionManager poolingHttpClientConnectionManager() {
		PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
		connectionManager.setMaxTotal(this.maxTotal); // 최대 오픈되는 커넥션 수
		// setMaxPerRoute는 경로를 미리 알고 있는 경우 사용
//		HttpRoute httpRoute = new HttpRoute(new HttpHost("locahost", 80));
//		connectionManager.setMaxPerRoute(httpRoute, 5);
		// setMaxPerRoute에 의해 경로가 지정되지 않은 호출에 대해 connection 갯수를 설정
		// 라우팅할 경로에 대한 커넥션
		connectionManager.setDefaultMaxPerRoute(this.maxPerRoute); // IP, 포트 1쌍에 대해 수행할 커넥션 수
		
		return connectionManager;
	}
	
	/**
	 * 헤더에 Keep-alive 응답이 없으면 HttpClient는 연결을 무기한 활성상태로 유지할 수 있다. 
	 * 연결이 재사용되기 전에 유휴 상태로 유지될 수 있는 기간을 설정할 수 있는 인터페이스이다.
	 * 헤더에 명시된 호스트의 keep-alive정책을 적용하려고 시도한 후 정보가 응답헤더에 없으면 20초간 연결을 유지한다. 
	 * 
	 * @return
	 */
	@Bean
	public ConnectionKeepAliveStrategy connectionKeepAliveStrategy() {
		return new ConnectionKeepAliveStrategy() {
			@Override
			public long getKeepAliveDuration(HttpResponse response, HttpContext context) {
				HeaderElementIterator it = new BasicHeaderElementIterator(
						response.headerIterator(HTTP.CONN_KEEP_ALIVE));
				while (it.hasNext()) {
					HeaderElement he = it.nextElement();
					String param = he.getName();
					String value = he.getValue();
					if (value != null && param.equalsIgnoreCase("timeout")) {
						return Long.parseLong(value) * 1000;
					}
				}
				return keepAliveTimeout;
			}
		};
	}
	
	@Bean
	public Runnable idleConnectionMonitor(final PoolingHttpClientConnectionManager connectionManager) {
		return new Runnable() {
			@Override
			@Scheduled(fixedDelay = 60 * 1000)
			public void run() {
				try {
					if (connectionManager != null) {
						if (!"l".equals(instance)) {
							for (HttpRoute httpRoute : connectionManager.getRoutes()) {
								try {
									log.info("\n[POOL] host name = {}, host count = {}", httpRoute.getTargetHost().toHostString(), httpRoute.getHopCount());
								} catch (Exception e) {
									log.info("\n[POOL] Host Route 오류 :: Exception Class = {}, Message = {}", e.getClass().getName(), e.getMessage());
								}
							}
							
							PoolStats poolStats = connectionManager.getTotalStats();
							log.info("\n[POOL] poolStats.getAvailable() = {}, poolStats.getMax() = {}", poolStats.getAvailable(), poolStats.getMax());
							log.info("\n[POOL] {} : 만료 또는 Idle 커넥션 종료.", Thread.currentThread().getName());
						}
						connectionManager.closeExpiredConnections();
						connectionManager.closeIdleConnections(idleTimeout, TimeUnit.MILLISECONDS);
						
					} else {
						log.info("{} : ConnectionManager가 없습니다.", Thread.currentThread().getName());
					}
				} catch (Exception e) {
					log.error(Thread.currentThread().getName() + " : 만료 또는 Idle 커넥션 종료 중 예외 발생.", e);
				}
			}
		};
	}

	@Bean
	public RequestConfig requestConfig() {
		return RequestConfig.custom()
				.setConnectionRequestTimeout(this.requestTimeout)
				.setConnectTimeout(this.connectTimeout)
				.setSocketTimeout(this.socketTimeout)
				.build();
	}
	
	@Bean
	public CloseableHttpClient httpClient(final PoolingHttpClientConnectionManager poolManager,
			final ConnectionKeepAliveStrategy strategy, final RequestConfig requestConfig) {
		return HttpClientBuilder.create()
				.setDefaultRequestConfig(requestConfig)
				.setConnectionManager(poolManager)
				.setKeepAliveStrategy(strategy)
				.build();
	}
	
	@Bean
	public RestTemplate restTemplate(final CloseableHttpClient httpClient) {
		HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
		factory.setHttpClient(httpClient);
		RestTemplate restTemplate = new RestTemplate(factory);
		restTemplate.setErrorHandler(restTemplateResponseErrorHandler);
		return restTemplate;
	}

}

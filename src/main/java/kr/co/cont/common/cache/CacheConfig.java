package kr.co.cont.common.cache;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.benmanes.caffeine.cache.Caffeine;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@EnableCaching
@Configuration
public class CacheConfig {
	
	@Bean
	public CacheManager cacheManager() {
		SimpleCacheManager cacheManager = new SimpleCacheManager();

		List<CaffeineCache> caches = Arrays.stream(CacheType.values())
				.map(cache -> new CaffeineCache(cache.getName(),
						Caffeine.newBuilder()
								.expireAfterAccess(cache.getExpireTime(), TimeUnit.SECONDS) // read  이후 특정 시간이 지나면 만료(expire) 한다
//								.expireAfterWrite(cache.getExpireTime(), TimeUnit.SECONDS)  // write 이후 특정 시간이 지나면 만료(expire) 한다.
//								.expireAfter(Expiry);	// 캐시가 생성되거나 마지막으로 업데이트된 후 지정된 시간 간격으로 캐시를 새로 고침합니다.
								.maximumSize(cache.getMaximumSize())	// 크기 기준으로 캐시를 제거하는 방식
								.evictionListener((key, value, cause) -> log.info("Key {} was evicted ({}): {}", key, cause, value))
								.removalListener((key, value, cause) -> log.info("Key {} was removed ({}): {}", key, cause, value))
								.recordStats()
								.build()))
				.collect(Collectors.toList());
		
		cacheManager.setCaches(caches);
		return cacheManager;
	}
}

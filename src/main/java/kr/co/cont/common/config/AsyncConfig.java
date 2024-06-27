package kr.co.cont.common.config;

import java.util.concurrent.Executor;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import kr.co.cont.common.cache.BaseCache;

@Configuration
@EnableAsync
public class AsyncConfig {
	
	private int corePoolSize;
	private int maxPoolSize;
	private int queueCapacity;
	
	@Autowired
	private BaseCache baseCache;
	
	@PostConstruct
	private void init() {
		this.corePoolSize = baseCache.propertyInt("thread.translog.corePoolSize");
		this.maxPoolSize = baseCache.propertyInt("thread.translog.maxPoolSize");
		this.queueCapacity = baseCache.propertyInt("thread.translog.queueCapacity");
	}

	@Bean(name = "transLogThreadPoolTaskExecutor")
	public Executor threadPoolTaskExecutor() {
		ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
		taskExecutor.setCorePoolSize(this.corePoolSize); // 기본 스레드 수
		taskExecutor.setMaxPoolSize(this.maxPoolSize); // 최대 스레드 수
		taskExecutor.setQueueCapacity(this.queueCapacity); // Queue 사이즈
		taskExecutor.setThreadNamePrefix("photo-executor-task-pool-");
		return taskExecutor;
	}
}

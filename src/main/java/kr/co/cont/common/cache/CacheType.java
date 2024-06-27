package kr.co.cont.common.cache;

import lombok.Getter;

@Getter
public enum CacheType {
	
	COMMON_COOE(NAME.COMMON_COOE),
	PROPERTY(NAME.PROPERTY),
	MENU(NAME.MENU),
	TRANS(NAME.TRANS),
	;

	private String name;
	private int expireTime;
	private int maximumSize;

	CacheType(String name) {
		this.name = name;
		this.expireTime = ConstConfig.DEFAULT_TTL_SEC;
		this.maximumSize = ConstConfig.DEFAULT_MAX_SIZE;
	}
	
	CacheType(String name, int sec, int size) {
		this.name = name;
		this.expireTime = sec;
		this.maximumSize = size;
	}
	
	static class NAME {
		static final String COMMON_COOE = "commonCode";
		static final String PROPERTY = "property";
		static final String MENU = "menu";
		static final String TRANS = "trans";
	}

	static class ConstConfig {
		static final int DEFAULT_TTL_SEC = 3000;
		static final int DEFAULT_MAX_SIZE = 10000;
	}

}

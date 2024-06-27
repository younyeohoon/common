package kr.co.cont.common.cache;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.stereotype.Component;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.stats.CacheStats;

import kr.co.cont.common.biz.base.model.BaseMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class BaseCache {
	
	private final CacheRepository cacheRepository;
	private final CacheManager cacheManager;
	private String instance = StringUtils.defaultIfEmpty(System.getProperty("instance"), "l");
	
	public String instance() {
		return instance;
	}
	
	/**
	 * 공통코드 목록을 가져온다.
	 * 
	 * @param codeGroup
	 * @return
	 */
	public List<BaseMap> codeList(String codeGroup) {
		return cacheRepository.applyCodeList(codeGroup);
		
	}
	
	/**
	 * 공통코드 정보를 가져온다.
	 * 
	 * @param codeGroup
	 * @param codeId
	 * @return
	 */
	public BaseMap code(String codeGroup, String codeId) {
		return codeList(codeGroup).stream()
								.filter(v -> codeId.equals(v.getString("codeId")))
								.findFirst()
								.orElse(new BaseMap());
	}
	
	/**
	 * 공통코드 출력명을 가져온다.
	 * 
	 * @param codeGroup
	 * @param codeId
	 * @return
	 */
	public String codeName(String codeGroup, String codeId) {
		return this.codeValue(codeGroup, codeId, "codeDisplayName");
	}

	/**
	 * 공통코드의 특정항목의 값을 가지고 온다.
	 * 
	 * @param codeGroup
	 * @param codeId
	 * @param targetName
	 * @return
	 */
	public String codeValue(String codeGroup, String codeId, String targetName) {
//		if(log.isDebugEnabled()) log.debug("codeGroup = {}, codeId = {}, targetName = {}", codeGroup, codeId, targetName);
		BaseMap codeInfo = this.code(codeGroup, codeId);
		if (codeInfo == null) return "";
		return codeInfo.getString(targetName);
	}
	
	/**
	 * 프로퍼티의 값을 문자형태로 반환한다.
	 * 
	 * @param propertyId
	 * @return String
	 */
	public String propertyValue(String propertyId) {
		BaseMap baseMap = cacheRepository.applyProperty(propertyId, instance);
		return baseMap.getString("propertyValue");
	}
	
	/**
	 * 프로퍼티의 값을 숫자 형태로 반환한다.
	 * 
	 * @param propertyId
	 * @return int
	 */
	public int propertyInt(String propertyId) {
		BaseMap baseMap = cacheRepository.applyProperty(propertyId, instance);
		return baseMap.getInt("propertyValue");
	}
	
	

	/**
	 * 사이트에 해당하는 메뉴목록을 가져온다.
	 * 
	 * @param menuSite
	 * @return
	 */
	public List<BaseMap> menus(String menuSite) {
		if (menuSite.isBlank()) return Collections.emptyList();
		return cacheRepository.applyMenuList(menuSite);
	}
	
	/**
	 * 
	 * @param uri
	 * @return
	 */
	public BaseMap trans(String uri, String transType) {
		List<BaseMap> list = cacheRepository.applyTrans(transType);
		return list.stream()
					.filter(v -> uri.matches(v.getString("transUri")))
					.findFirst()
					.orElse(null);
	}
	
	public BaseMap check() {
		BaseMap baseMap = new BaseMap();
		Collection<String> cacheNames = cacheManager.getCacheNames();
		for (String cacheName : cacheNames) {
			BaseMap map = new BaseMap();
			CaffeineCache cacheCaffeineCache = (CaffeineCache) cacheManager.getCache(cacheName);
			
			// 캐쉬내용
			Cache<Object, Object> cache = cacheCaffeineCache.getNativeCache();
			Map<Object, Object> cacheMap = cache.asMap();
			map.put("cacheMap", cacheMap);
			
			Map<Object, Object> cloneMap = new HashMap<Object, Object>();
			for (Object key : cacheMap.keySet()) {
				log.info("[cacheMap] key '{}' - value : {}" , key, cacheMap.get(key));
				cloneMap.put(key, cacheMap.get(key));
			}
			
			map.put("cacheMapSize", size(cloneMap));
			
			// 캐쉬 통계
			CacheStats stats = cache.stats();
			map.put("stats", stats);
			log.info("[stats] cache '{}' - stats : {}", cacheName, stats.toString());
			
			baseMap.put(cacheName, map);
		}
		
		return baseMap;
	}
	
	private int size(Map<Object, Object> map) {
		int s = 0;
		ByteArrayOutputStream baos = null;
		ObjectOutputStream oos = null;
		try {
			baos = new ByteArrayOutputStream();
			oos = new ObjectOutputStream(baos);
			oos.writeObject(map);
			s = baos.size();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (oos != null) try { oos.close(); } catch (IOException e) {}
			if (baos != null) try { baos.close(); } catch (IOException e) {}
		}
		
		return s;
	}
}

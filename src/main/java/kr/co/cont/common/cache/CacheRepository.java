package kr.co.cont.common.cache;

import java.util.List;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

import kr.co.cont.common.biz.base.dao.BaseDao;
import kr.co.cont.common.biz.base.model.BaseMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Repository("cacheRepository")
public class CacheRepository extends BaseDao {
	
	/**
	 * 공통 코드를 캐쉬에 적용한다.
	 * 캐쉬에 codeGroup 키가 존재하지 않으면 DB를 조회함.
	 * 
	 * @param codeGroup
	 * @return
	 */
	@Cacheable(cacheNames = CacheType.NAME.COMMON_COOE , key = "#codeGroup", unless = "#result==null or #result.isEmpty()", cacheManager = "cacheManager")
	public List<BaseMap> applyCodeList(String codeGroup) {
		if (log.isDebugEnabled()) log.debug("Apply codeGroup = {}", codeGroup);
		
		BaseMap parameter = new BaseMap();
		parameter.put("codeGroup", codeGroup);
		
		List<BaseMap> list = selectList("cache.selectCacheCommonCode", parameter);
		
		return list ;
	}
	
	/**
	 *  공통코드에서 codeGroup에 해당하는 KEY를 제거함.
	 * 
	 * @param codeGroup
	 */
	@CacheEvict(cacheNames = CacheType.NAME.COMMON_COOE, key = "#codeGroup", cacheManager = "cacheManager")
	public void removeCodeList(String codeGroup) {
		if (log.isDebugEnabled()) log.debug("remove codeGroup = {}", codeGroup);
	}
	
	/**
	 * 프로퍼티를 캐쉬에 적용한다.
	 * 캐쉬에 propertyId 키가 존재하지 않으면 DB를 조회함.
	 * 
	 * @param propertyId
	 * @return
	 */
	@Cacheable(cacheNames = CacheType.NAME.PROPERTY, key = "#propertyId", unless = "#result==null or #result.isEmpty()", cacheManager = "cacheManager")
	public BaseMap applyProperty(String propertyId, String propertyGroup) {
		if (log.isDebugEnabled()) log.debug("Apply propertyId = {}", propertyId);
		
		BaseMap parameter = new BaseMap();
		parameter.put("propertyId", propertyId);
		parameter.put("propertyGroup", propertyGroup);
		
		BaseMap baseMap = select("cache.selectCacheProperty", parameter);
		
		return baseMap;
	}
	
	/**
	 * 프로퍼티에서 propertyId에 해당하는 KEY를 제거함.
	 * 
	 * @param propertyId
	 */
	@CacheEvict(cacheNames = CacheType.NAME.PROPERTY, key = "#propertyId", cacheManager = "cacheManager")
	public void removePropery(String propertyId) {
		if (log.isDebugEnabled()) log.debug("remove propertyId = {}", propertyId);
	}
	
	/**
	 * 메뉴 목록을 캐쉬에 적용한다.
	 * 캐쉬에 menuSite 키가 존재하지 않으면 DB를 조회함.
	 * 
	 * @param menuSite
	 * @return
	 */
	@Cacheable(cacheNames = CacheType.NAME.MENU, key = "#menuSite", unless = "#result==null or #result.isEmpty()", cacheManager = "cacheManager")
	public List<BaseMap> applyMenuList(String menuSite) {
		if (log.isDebugEnabled()) log.debug("Apply menuSite = {}", menuSite);

		BaseMap parameter = new BaseMap();
		parameter.put("menuSite", menuSite);
		parameter.put("menuNo", 0);
		
		List<BaseMap> list = selectMenuList(parameter);
		
		return list;
	}
	
	/**
	 * 하위 메뉴가 존재하는 경우 하위 메뉴를 가져온다.
	 * 
	 * @param parameter
	 * @return
	 */
	private List<BaseMap> selectMenuList(BaseMap parameter) {
		List<BaseMap> list = selectList("cache.selectCacheMenuList", parameter);
		list.forEach( v -> v.put("list", selectMenuList(v)) );
		return list;
	}
	
	/**
	 * 메뉴에서 menuSite에 해당하는 KEY를 제거함.
	 * 
	 * @param menuSite
	 */
	@CacheEvict(cacheNames = CacheType.NAME.MENU, key = "#menuSite", cacheManager = "cacheManager")
	public void removeMenuList(String menuSite) {
		if (log.isDebugEnabled()) log.debug("remove menuSite = {}", menuSite);
	}

	@Cacheable(cacheNames = CacheType.NAME.TRANS, key = "#transType", unless = "#result==null or #result.isEmpty()", cacheManager = "cacheManager")
	public List<BaseMap> applyTrans(String transType) {
		BaseMap parameter = new BaseMap();
		parameter.put("transType", transType);
		
		return selectList("cache.selectCacheTrans", parameter);
	}
	
	@CacheEvict(cacheNames = CacheType.NAME.TRANS, key = "#transType", cacheManager = "cacheManager")
	public void removeTrans(String transType) {
		if (log.isDebugEnabled()) log.debug("remove transType = {}", transType);
	}
}

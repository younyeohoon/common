package kr.co.cont.common.biz.base.service;

import java.util.List;

import kr.co.cont.common.biz.base.model.BaseData;
import kr.co.cont.common.biz.base.model.BaseMap;

public abstract class BaseService {

	/**
	 * 비동기 호출을 위해서 추가함.
	 * 
	 * @param baseMap
	 */
	public abstract void async(BaseMap baseMap);
	
	/**
	 * 대용량 엑셀 다운로드를 위해서 추가함.
	 * 
	 * @param params
	 * @return
	 */
	public abstract List<BaseMap> excelList(BaseData params);
}

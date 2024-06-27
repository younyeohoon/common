package kr.co.cont.common.biz.base.model;

import java.util.Date;

import kr.co.cont.common.constants.BaseConstants;
import kr.co.cont.common.constants.DateConstants;
import kr.co.cont.common.util.DateUtil;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BaseData {

	/** 등록일시 */
	private Date regDate = DateUtil.getCurrentDate(DateConstants.DF_MILLISECOND);
	/** 등록자 */
	private String regUser = BaseConstants.DF_MEMBER_ID;;
	/** 수정일시 */
	private Date modDate = DateUtil.getCurrentDate(DateConstants.DF_MILLISECOND);
	/** 수정자 */
	private String modUser = BaseConstants.DF_MEMBER_ID;;
	
	/** 라인넘버 */
	private int num;
	/** 다음키 */
	private int nextKey = BaseConstants.DF_NEXT_KEY;
	/** 페이지당 행수 */
	private int perPage = BaseConstants.DF_PER_PAGE;
	
}

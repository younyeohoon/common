package kr.co.cont.common.file.excel;

import java.util.ArrayList;
import java.util.List;

import kr.co.cont.common.biz.base.model.BaseData;
import kr.co.cont.common.biz.base.service.BaseService;
import lombok.Data;

@Data
public class ExcelForm {
	
	/** 파일명 */
	private String fileName;
	/** 데이터 총 건수 */
	private long listSize;
	/** 목록을 가져올 서비스 */
	private BaseService service;
	/** 목록을 조회할 조건 */
	private BaseData params;
	/** 컬럼명 */
	private List<String> keys = new ArrayList<>();
	/** 엑셀 헤터명 */
	private List<String> headers = new ArrayList<>();
	/** 엑셀 셀의 넓이 */
	private List<String> widths = new ArrayList<>();
	/** 엑셀 셀의 텍스트 위치 */
	private List<String> aligns = new ArrayList<>();

	public void setSheetForm(String key, String header, String width, String align) {
		this.keys.add(key);
		this.headers.add(header);
		this.widths.add(width);
		this.aligns.add(align);
	}
}

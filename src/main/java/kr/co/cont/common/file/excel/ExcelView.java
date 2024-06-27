package kr.co.cont.common.file.excel;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.util.HSSFColor.HSSFColorPredefined;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.document.AbstractXlsxView;

import kr.co.cont.common.biz.base.model.BaseData;
import kr.co.cont.common.biz.base.model.BaseMap;
import kr.co.cont.common.biz.base.service.BaseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component("excelView")
public class ExcelView extends AbstractXlsxView {

	private static final int MAX_ROW = 1040000;

	private static final int PAGING_SIZE = 10000;
	
	/**
	 * This implementation creates an {@link XSSFWorkbook} for the XLSX format.
	 */
	@Override
	protected Workbook createWorkbook(Map<String, Object> model, HttpServletRequest request) {
		return new SXSSFWorkbook();
	}

	@Override
	protected void buildExcelDocument(Map<String, Object> model, Workbook workbook, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		ExcelForm excelForm = (ExcelForm) model.get("excelForm");
		
		BaseService service = excelForm.getService();
		BaseData params = excelForm.getParams();
		
		long listSize = excelForm.getListSize();

		List<BaseMap> dataList = null;
		for (int start = 0; start < listSize; start += PAGING_SIZE) {
			
			params.setNextKey(start);
			params.setPerPage(PAGING_SIZE);
			dataList = service.excelList(params);
			
			getWorkbook(excelForm, dataList, start, workbook);
			
			dataList.clear(); // 리스트 페이징 처리 및 메모리
		}
		
		String fileName = excelForm.getFileName();
		String userAgent = request.getHeader("User-Agent");
		String newFileName = fileName;
		if (userAgent.contains("Trident") || (userAgent.indexOf("MSIE") > -1)) {
			newFileName = URLEncoder.encode(fileName, "UTF-8").replaceAll("\\+", "%20");
		} else {
			newFileName = new String(fileName.getBytes("UTF-8"), "ISO-8859-1");
		}

		response.setHeader("Content-Disposition", "attachment;filename=" + newFileName + ".xlsx");
		response.setHeader("fileName", URLEncoder.encode(fileName, "UTF-8"));

	}

	private void getWorkbook(ExcelForm excelForm, List<BaseMap> dataList, int rowIdx, Workbook workbook) {
		
		List<String> keys = excelForm.getKeys();
		List<String> headers = excelForm.getHeaders();
		List<String> widths = excelForm.getWidths();
		List<String> aligns = excelForm.getAligns();

		// 최초 생성이면 SheetN 생성
		// 이어서 작성일 경우 SheetN에서 이어서
		String sheetName = "Sheet" + (rowIdx / MAX_ROW + 1); // 각 시트 당 1,040,000개씩
		boolean newSheet = ObjectUtils.isEmpty(workbook.getSheet(sheetName));
		Sheet sheet = ObjectUtils.isEmpty(workbook.getSheet(sheetName)) ? workbook.createSheet(sheetName)
				: workbook.getSheet(sheetName);

		CellStyle headerStyle = createHeaderStyle(workbook);
		CellStyle bodyStyleLeft = createBodyStyle(workbook, "LEFT");
		CellStyle bodyStyleRight = createBodyStyle(workbook, "RIGHT");
		CellStyle bodyStyleCenter = createBodyStyle(workbook, "CENTER");

		// \r\n을 통해 셀 내 개행
		// 개행을 위해 setWrapText 설정
		bodyStyleLeft.setWrapText(true);
		bodyStyleRight.setWrapText(true);
		bodyStyleCenter.setWrapText(true);

		int idx = 0;

		for (String width : widths) {
			sheet.setColumnWidth(idx++, Integer.parseInt(width) * 256);
		}

		Row row = null;
		Cell cell = null;

		// 매개변수로 받은 rowIdx % MAX_ROW 행부터 이어서 데이터
		int rowNo = rowIdx % MAX_ROW;

		if (newSheet) {
			row = sheet.createRow(rowNo);
			idx = 0;
			
			for (String columnName : headers) {
				cell = row.createCell(idx++);
				cell.setCellStyle(headerStyle);
				cell.setCellValue(columnName);
			}
		}

		for (BaseMap tempRow : dataList) {
			idx = 0;
			row = sheet.createRow(++rowNo);

			for (String key : keys) {
				if (StringUtils.isEmpty(key)) {
					continue;
				}

				cell = row.createCell(idx);
				
				if (ObjectUtils.isEmpty(aligns)) {
					// 디폴트 가운데 정렬
					cell.setCellStyle(bodyStyleCenter);
				} else {
					String hAlign = aligns.get(idx);

					if ("LEFT".equals(hAlign)) {
						cell.setCellStyle(bodyStyleLeft);
					} else if ("RIGHT".equals(hAlign)) {
						cell.setCellStyle(bodyStyleRight);
					} else {
						cell.setCellStyle(bodyStyleCenter);
					}
				}

				Object value = tempRow.get(key);

				if (value instanceof BigDecimal) {
					cell.setCellValue(((BigDecimal) value).toString());
				} else if (value instanceof Double) {
					cell.setCellValue(((Double) value).toString());
				} else if (value instanceof Long) {
					cell.setCellValue(((Long) value).toString());
				} else if (value instanceof Integer) {
					cell.setCellValue(((Integer) value).toString());
				} else {
					cell.setCellValue((String) value);
				}

				idx++;

				// 주기적인 flush 진행
				if (rowNo % 100 == 0) {
					try {
						((SXSSFSheet) sheet).flushRows(100);
					} catch (IOException e) {
						log.error("flushRows 오류발생!!", e);
						
					}
				}
			}
		}

//		return workbook;
	}

	/**
	 * 헤더에 사용할 셀 스타일을 설정한다.
	 *
	 * @param workbook
	 * @return
	 */
	private CellStyle createHeaderStyle(Workbook workbook) {
		CellStyle headerStyle = createBodyStyle(workbook, "");
		// 취향에 따라 설정 가능
		headerStyle.setFillForegroundColor(HSSFColorPredefined.LIGHT_YELLOW.getIndex());
		headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

		// 가로 세로 정렬 기준
		headerStyle.setAlignment(HorizontalAlignment.CENTER);
		headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);

		return headerStyle;
	}

	/**
	 * 셀의 스타일을 설정한다.
	 * 
	 * @param workbook
	 * @param align
	 * @return
	 */
	private CellStyle createBodyStyle(Workbook workbook, String align) {
		CellStyle bodyStyle = workbook.createCellStyle();
		// 취향에 따라 설정 가능
		bodyStyle.setBorderTop(BorderStyle.THIN);
		bodyStyle.setBorderBottom(BorderStyle.THIN);
		bodyStyle.setBorderLeft(BorderStyle.THIN);
		bodyStyle.setBorderRight(BorderStyle.THIN);
		bodyStyle.setVerticalAlignment(VerticalAlignment.CENTER);

		if (StringUtils.isEmpty(align) == false) {
			if ("LEFT".equals(align)) {
				bodyStyle.setAlignment(HorizontalAlignment.LEFT);
			} else if ("RIGHT".equals(align)) {
				bodyStyle.setAlignment(HorizontalAlignment.RIGHT);
			} else {
				bodyStyle.setAlignment(HorizontalAlignment.CENTER);
			}
			bodyStyle.setFont(createFontStyle(workbook, false));
		} else {
			bodyStyle.setFont(createFontStyle(workbook, true));
		}

		return bodyStyle;
	}
	
	private Font createFontStyle(Workbook workbook, boolean isHeader) {
		Font font = workbook.createFont();
		font.setFontName("나눔고딕");
		if (isHeader) {
			font.setFontHeight((short)300);
			font.setColor(IndexedColors.GREEN.getIndex());
			font.setBold(true);
		}
		
		return font;
	}
}

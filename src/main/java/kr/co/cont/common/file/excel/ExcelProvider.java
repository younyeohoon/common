package kr.co.cont.common.file.excel;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.jxls.common.Context;
import org.jxls.reader.ReaderBuilder;
import org.jxls.reader.XLSReader;
import org.jxls.util.JxlsHelper;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.SAXException;

import kr.co.cont.common.biz.base.model.BaseMap;
import kr.co.cont.common.constants.ErrorCode;
import kr.co.cont.common.exception.BaseException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ExcelProvider {
	
	/**
	 * 업로드한 엑셀 파일을 읽어서 데이터를 객체에 담는다.
	 * 
	 * @param template 엑셀파일 업로드 양식 XML 파일 위치
	 * @param excelFile 업로드한 엑셀파일
	 * @param baseMap 엑셀에서 추출한 데이터를 담을 객체
	 */
	public void read(String template, MultipartFile excelFile, BaseMap baseMap) {
		
		ClassPathResource classPathResource = new ClassPathResource(template);
		
		InputStream is = null;
		
		try {
			// 1. 미리 만들어둔 엑셀 템플릿 파일에 대한 InputStream 생성
			is = new BufferedInputStream(classPathResource.getInputStream());
			
			XLSReader reader = ReaderBuilder.buildFromXML(is);
			
			// 2. 데이터를 담을 객체 설정
			Map<String, Object> beans = new HashMap<>();
			for (Object key : baseMap.keySet()) {
				beans.put((String) key, baseMap.get(key));
			}
			
			// 3. 엑셀 파일에서 데이터를 읽어 온다.
			InputStream inExcel = null;
			try {
				inExcel = excelFile.getInputStream();
				reader.read(inExcel, beans);
			} catch (InvalidFormatException e) {
				log.error("엑셀 업로드 오류!!", e);
			} finally {
				if (inExcel != null) try {inExcel.close();} catch(Exception e) {log.error("templage error");}
			}
			
		} catch (IOException e) {
			log.error("엑셀 업로드 오류!!", e);
			throw new BaseException(ErrorCode.FILE_NOT_FOUND);
		} catch (SAXException e) {
			log.error("엑셀 업로드 오류!!", e);
		} finally {
			if (is != null) try {is.close();} catch(Exception e) {log.error("templage error");}
		}
	}
	
	/**
	 * JXLS 를 이용한 엑셀 다운로드
	 * 
	 * @param request
	 * @param response
	 * @param baseMap 엑셀에 표시할 데이터
	 * @param fileName 다운로드시 사용할 파일명
	 * @param template 엑셀 양식 위치
	 */
	public void download(HttpServletRequest request, HttpServletResponse response, BaseMap baseMap
			, String fileName, String template) {

		InputStream is = null;
		OutputStream os = null;
		
		try {
			
			// 1. 미리 만들어둔 엑셀 템플릿 파일에 대한 InputStream 생성
			is = new BufferedInputStream(new ClassPathResource(template).getInputStream());
			
			
			// 2. 응답객체로부터 OutPutStream 생성
			os = response.getOutputStream();
			
			// 3. 컨텍스트 객체 생성 및 세팅
			// 3-1. context 속성에 컨텍스트명과 엑셀에 쓰일 데이터를 Key & Value로 세팅
			// 3-2. 여기서 contextName("excelDataList")은 엑셀 템플릿파일에서 items="컨텍스트명"과 반드시 일치
			Context context = new Context();
			for (Object key : baseMap.keySet()) {
				context.putVar((String) key, baseMap.get(key));
			}
			
			// 4. 엑셀파일로 다운로드위한 response 객체 세팅
			String userAgent = request.getHeader("User-Agent");
			
			String newFileName = fileName;
			if (userAgent.contains("Trident") || (userAgent.indexOf("MSIE") > -1)) {
				newFileName = URLEncoder.encode(fileName, "UTF-8").replaceAll("\\+", "%20");
			} else {
				newFileName = new String(fileName.getBytes("UTF-8"), "ISO-8859-1");
			}
			
			response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
			response.setHeader("Content-Disposition", "attachment;filename=" + newFileName + ".xlsx");
			response.setHeader("fileName", URLEncoder.encode(fileName, "UTF-8"));
		
			
			// 5. templateStream 으로 부터 템플릿을 읽어 들인 후 context를 targetStream에 씀
			JxlsHelper.getInstance().setUseFastFormulaProcessor(false).processTemplate(is, os, context);
			
			os.flush();
		} catch (IOException e) {
			log.error("엑셀 다운로드 오류!!", e);
			throw new BaseException(ErrorCode.FILE_NOT_FOUND);
		} finally {
			if (os != null) try {os.close();} catch(Exception e) {log.error("엑셀다운로드 오류!!");}
			if (is != null) try {is.close();} catch(Exception e) {log.error("엑셀다운로드 오류!!");}
		}
	}

}

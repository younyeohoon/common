package kr.co.cont.common.util;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.Collections;
import java.util.List;

import javax.imageio.ImageIO;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import kr.co.cont.common.constants.ErrorCode;
import kr.co.cont.common.exception.BaseException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class FileUtil {

	/**
	 * 파일 읽기
	 * 
	 * @param fileName
	 * @return
	 */
	public static byte[] read(String fileName) {
		if(log.isDebugEnabled()) log.debug("fileName = {}", fileName);
		
		byte[] bytes;
		
		Path path = Paths.get(fileName);
		try {
			bytes = Files.readAllBytes(path);
		} catch (IOException e) {
			throw new BaseException(ErrorCode.FILE_SERVER_ERROR, e);
		}
		
		return bytes;

	}
	
	/**
	 * 파일을 라인 단위로 읽어서 리스트로 반환한다.
	 *  
	 * @param fileName
	 * @return
	 */
	public static List<String> readLine(String fileName) {
		if(log.isDebugEnabled()) log.debug("fileName = {}", fileName);

		List<String> list = null;
		Path path = Paths.get(fileName);
		try {
			list = Files.readAllLines(path);
		} catch (IOException e) {
			list = Collections.emptyList();
			e.printStackTrace();
		}

		return list;
	}

	/**
	 * 파일 쓰기
	 * 
	 * @param fileName
	 * @param bytes
	 */
	public static void write(String fileName, byte[] bytes) {
		if(log.isDebugEnabled()) log.debug("fileName = {}, context = {}", fileName, bytes.length);
		
		Path path = Paths.get(fileName);
		
		try {
			Files.createDirectories(path.getParent());
			/*
			 * StandardOpenOption.APPEND	파일 쓰기에서 뒤에 붙임
			 * StandardOpenOption.CREATE	파일이 없으면 생성
			 * StandardOpenOption.CREATE_NEW	파일이 있어도 생성
			 * StandardOpenOption.DELETE_ON_CLOSE	Files 사용하다가 close() 호출되면 삭제
			 * StandardOpenOption.READ	읽기 권한을 얻음
			 * StandardOpenOption.WRITE	쓰기 권한을 얻음
			 */
			Files.write(path, bytes, StandardOpenOption.CREATE, StandardOpenOption.CREATE_NEW);
		} catch (IOException e) {
			throw new BaseException(ErrorCode.FILE_SERVER_ERROR, e);
		}
		
	}
	
	/**
	 * 파일쓰기 
	 * 
	 * @param fileName
	 * @param context
	 */
	public static void write(String fileName, String context) {
		if(log.isDebugEnabled()) log.debug("fileName = {}, context = {}", fileName, context);

		Path path = Paths.get(fileName);

		try {
			Files.createDirectories(path.getParent());
			
			/*
			 * StandardOpenOption.APPEND	파일 쓰기에서 뒤에 붙임
			 * StandardOpenOption.CREATE	파일이 없으면 생성
			 * StandardOpenOption.CREATE_NEW	파일이 있어도 생성
			 * StandardOpenOption.DELETE_ON_CLOSE	Files 사용하다가 close() 호출되면 삭제
			 * StandardOpenOption.READ	읽기 권한을 얻음
			 * StandardOpenOption.WRITE	쓰기 권한을 얻음
			 */
			Charset charset = Charset.defaultCharset();
			Files.writeString(path, context, charset, StandardOpenOption.CREATE, StandardOpenOption.CREATE_NEW);
			
		} catch (IOException e) {
			throw new BaseException(ErrorCode.FILE_SERVER_ERROR, e);
		}
	}

	/**
	 * 파일 복사
	 * 
	 * @param formFileName
	 * @param toFileName
	 */
	public static void copy(String formFileName, String toFileName) {

		Path fromPath = Paths.get(formFileName);
		Path toPath = Paths.get(toFileName);

		try {
			Files.copy(fromPath, toPath, StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			throw new BaseException(ErrorCode.FILE_SERVER_ERROR, e);
		}

	}

	/**
	 * 디렉토리 생성
	 * 
	 * @param path
	 */
	public static void makeDir(Path path) {
		try {
			if (!Files.exists(path)) {
				Files.createDirectories(path);
			}
		} catch (IOException e) {
			log.error("{}", e);
		}
	}
	
	/**
	 * 파일 여부 확인
	 * 
	 * @param fileName
	 * @return
	 */
	public static boolean isFile(String fileName) {
		Path path = Paths.get(fileName);
		if (Files.isDirectory(path)) return false;
		else return Files.exists(path);
	}
	
	/**
	 * 파일 삭제
	 * 
	 * @param fileName
	 * @return
	 */
	public static boolean remvoe(String fileName) {
		try {
			Files.delete(Paths.get(fileName));
			return true;
		} catch (IOException e) {
			log.error("파일 삭제 오류 = {}", e);
			return false;
		}
	}

	/**
	 * 이미지 크기 줄이기
	 * 
	 * @param files
	 * @param formatName
	 * @return
	 * @throws IOException
	 */
	public static byte[] resizeImageFile(MultipartFile files, String formatName) {
		
		byte[] bytes = null;
		ByteArrayOutputStream baos = null;
		
		try {
			// 이미지 읽어 오기
			BufferedImage inputImage = ImageIO.read(files.getInputStream());
			
			// 일부 이미지(CMYK) 읽지 못하는 경우가 존재함.
			if (inputImage == null) return new byte[0];
			
			// 이미지 세로 가로 측정
			int originWidth = inputImage.getWidth();
			int originHeight = inputImage.getHeight();
			// 변경할 가로 길이
			int newWidth = 500;
			
			if (originWidth > newWidth) {
				// 기존 이미지 비율을 유지하여 세로 길이 설정
				int newHeight = (originHeight * newWidth) / originWidth;
				// 이미지 품질 설정
				// Image.SCALE_DEFAULT : 기본 이미지 스케일링 알고리즘 사용
				// Image.SCALE_FAST : 이미지 부드러움보다 속도 우선
				// Image.SCALE_REPLICATE : ReplicateScaleFilter 클래스로 구체화 된 이미지 크기 조절 알고리즘
				// Image.SCALE_SMOOTH : 속도보다 이미지 부드러움을 우선
				// Image.SCALE_AREA_AVERAGING : 평균 알고리즘 사용
				Image resizeImage = inputImage.getScaledInstance(newWidth, newHeight, Image.SCALE_FAST);
				BufferedImage newImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
				baos = new ByteArrayOutputStream();
				Graphics graphics = newImage.getGraphics();
				graphics.drawImage(resizeImage, 0, 0, null);
				graphics.dispose();
				ImageIO.write(newImage, formatName, baos);
				
				bytes = baos.toByteArray();
			} else {
				bytes = files.getBytes();
			}
		} catch(IOException e) {
			log.error("썸네일 생성 중 오류", e);
			bytes = new byte[0];
		} finally {
			if (baos != null) try { baos.close(); } catch (IOException e) {}
		}
		
		return bytes;
	}

}

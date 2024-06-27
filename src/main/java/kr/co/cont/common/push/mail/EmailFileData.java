package kr.co.cont.common.push.mail;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.core.io.FileSystemResource;

import lombok.Data;

@Data
public class EmailFileData {
	
	private String name;
	private FileSystemResource file;
	
	public EmailFileData(String filePath) {
		Path path = Paths.get(filePath);
		this.file = new FileSystemResource(path);
		this.name = path.getFileName().toString();
	}
	
	public EmailFileData(String filePath, String name) {
		Path path = Paths.get(filePath);
		this.file = new FileSystemResource(path);
		this.name = name;
	}

}

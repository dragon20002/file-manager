package com.haruu.filemanager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
//JAR 사용 시
@SpringBootApplication
public class FileManangerApplication {

	public static void main(String[] args) {
		SpringApplication.run(FileManangerApplication.class, args);
	}

}
 */

// WAR 사용 시
@SpringBootApplication
public class FileManangerApplication extends SpringBootServletInitializer {
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(FileManangerApplication.class);
	}
	public static void main(String[] args) {
		SpringApplication.run(FileManangerApplication.class, args);
	}
}
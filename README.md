# 드래그로 서버에 파일 업로드

![aa](https://postfiles.pstatic.net/MjAxOTAyMTZfNjYg/MDAxNTUwMjQ2ODMzNTQ3.mAv8TaiyFoFZhJKcP42rTEKQjfnSfVMbHywPI_4P0nkg.wV3_AAZ3F0gY-tf_SNyYXCpAFsJRpKqkzhN6miL0-d4g.PNG.dragon20002/SE-c48279ab-5c8e-4045-8a66-b8f6d7e59c32.png?type=w580)


## Dependencies

- Spring Boot 2.1.2
> spring-boot-starter-security<br>
> spring-boot-starter-web (tomcat9 embed)<br>

<p>

- Thymeleaf
> spring-boot-starter-thymeleaf<br>
> thymeleaf-layout-dialect

<p>

- Test
> spring-boot-starter-test<br>

<p>

## application.properties

```
spring.datasource.url=jdbc:mysql://localhost:3306/ils?serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=csedbadmin
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

spring.jpa.hibernate.ddl-auto=update
spring.jpa.properties.hibernate.current_session_context_class=org.springframework.orm.hibernate5.SpringSessionContext

server.port=80

### SSL settings ###
#server.ssl.enabled=true
#server.ssl.key-store=/home/ec2-user/ils_web/ils_web.jks
#server.ssl.key-store-password=csedbadmin
#server.ssl.key-password=csedbadmin
#server.ssl.key-alias=ils_web
#server.ssl.trust-store=/home/ec2-user/ils_web/ils_web.ts
#server.ssl.trust-store-password=csedbadmin
```

## pom.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
	<modelVersion>4.0.0</modelVersion>
	<parent>
		<groupId>org.springframework.boot</groupId>
		<artifactId>spring-boot-starter-parent</artifactId>
		<version>2.1.2.RELEASE</version>
		<relativePath /> <!-- lookup parent from repository -->
	</parent>
	<groupId>com.haruu</groupId>
	<artifactId>filemananger</artifactId>
	<version>0.0.1-SNAPSHOT</version>
	<name>file-mananger</name>
	<description>file manager back-end</description>

	<properties>
		<java.version>1.8</java.version>
	</properties>

	<packaging>war</packaging>

	<dependencies>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-web</artifactId>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-security</artifactId>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-thymeleaf</artifactId>
		</dependency>
		<dependency>
			<groupId>nz.net.ultraq.thymeleaf</groupId>
			<artifactId>thymeleaf-layout-dialect</artifactId>
		</dependency>


		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-test</artifactId>
			<scope>test</scope>
		</dependency>

	</dependencies>

	<build>
		<plugins>
			<plugin>
				<groupId>org.springframework.boot</groupId>
				<artifactId>spring-boot-maven-plugin</artifactId>
			</plugin>
		</plugins>
	</build>

</project>
```
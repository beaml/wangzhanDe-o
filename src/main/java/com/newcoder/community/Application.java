package com.newcoder.community;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
//这个注解所表示的类就是一个配置文件
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}

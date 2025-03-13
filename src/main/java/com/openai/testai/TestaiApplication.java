package com.openai.testai;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude={DataSourceAutoConfiguration.class})
public class TestaiApplication {

	public static void main(String[] args) {
		SpringApplication.run(TestaiApplication.class, args);
	}

}

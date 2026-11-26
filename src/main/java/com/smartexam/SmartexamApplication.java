package com.smartexam;

import com.smartexam.config.JwtProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableConfigurationProperties(JwtProperties.class)
@EnableScheduling
public class SmartexamApplication {

	public static void main(String[] args) {
		SpringApplication.run(SmartexamApplication.class, args);
        System.out.println(
                "======================================"
        );
        System.out.println(
                " SmartExam Backend Started Successfully"
        );
        System.out.println(
                "======================================"
        );
	}

}

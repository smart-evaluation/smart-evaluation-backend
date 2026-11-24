package com.smartexam;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
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

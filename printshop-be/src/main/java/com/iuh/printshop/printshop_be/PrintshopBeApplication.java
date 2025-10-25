package com.iuh.printshop.printshop_be;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class PrintshopBeApplication {

	public static void main(String[] args) {
		SpringApplication.run(PrintshopBeApplication.class, args);
	}

}

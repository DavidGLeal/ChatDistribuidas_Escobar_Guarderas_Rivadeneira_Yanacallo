package com.espe.chatsdb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class ChatsdbApplication {

	public static void main(String[] args) {
		SpringApplication.run(ChatsdbApplication.class, args);
	}

}

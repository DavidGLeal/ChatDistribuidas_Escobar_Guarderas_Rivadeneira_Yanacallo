package com.espe.socket_chat.socket_chat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class SocketChatApplication {

	public static void main(String[] args) {
		SpringApplication.run(SocketChatApplication.class, args);
	}

}

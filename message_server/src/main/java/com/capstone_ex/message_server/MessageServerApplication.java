package com.capstone_ex.message_server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class MessageServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(MessageServerApplication.class, args);
	}

}

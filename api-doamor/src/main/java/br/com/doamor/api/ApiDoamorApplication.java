package br.com.doamor.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.web.bind.annotation.RestController;


@EnableFeignClients
@SpringBootApplication
public class ApiDoamorApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApiDoamorApplication.class, args);
	}

}

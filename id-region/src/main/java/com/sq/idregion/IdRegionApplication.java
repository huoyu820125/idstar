package com.sq.idregion;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan({
		"com.sq.idregion",
		"com.sq.idstar.service",
})
public class IdRegionApplication {

	public static void main(String[] args) {
		SpringApplication.run(IdRegionApplication.class, args);
	}

}

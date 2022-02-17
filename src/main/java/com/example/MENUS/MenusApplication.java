package com.example.MENUS;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@SecurityScheme(name="check",scheme="basic",type= SecuritySchemeType.HTTP,in= SecuritySchemeIn.HEADER)
public class MenusApplication {

	public static void main(String[] args) {
		SpringApplication.run(MenusApplication.class, args);
	}

}

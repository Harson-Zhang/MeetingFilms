package com.harson.uidprovider;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("com.baidu.fsg.uid")
@SpringBootApplication
public class UidProviderApplication {

	public static void main(String[] args) {
		SpringApplication.run(UidProviderApplication.class, args);
	}

}

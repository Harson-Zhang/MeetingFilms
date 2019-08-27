package com.harson.uidprovider;

import com.alibaba.dubbo.config.spring.context.annotation.EnableDubbo;
import com.alibaba.dubbo.config.spring.context.annotation.EnableDubboConfig;
import com.alibaba.dubbo.spring.boot.annotation.EnableDubboConfiguration;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("com.baidu.fsg.uid")
@EnableDubbo
@SpringBootApplication
public class UidProviderApplication {

	public static void main(String[] args) {
		SpringApplication.run(UidProviderApplication.class, args);
	}

}

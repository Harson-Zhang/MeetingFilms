package com.stylefeng.guns;

import com.stylefeng.guns.rest.AlipayApplication;
import com.stylefeng.guns.rest.common.utils.FTPUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = AlipayApplication.class)
@EnableConfigurationProperties({com.stylefeng.guns.rest.common.utils.FTPUtils.class})
public class GunsRestApplicationTests {
	@Autowired
	private FTPUtils ftpUtils;

	@Test
	public void contextLoads() {
	}

	@Test
	public void testOrder() {
	}

	@Test
	public void testFTPUtils(){
		File file = new File("D:/FTPServer/qr-code/generate.png");
		System.out.println(ftpUtils.upload("qr1.png", file));
	}
}

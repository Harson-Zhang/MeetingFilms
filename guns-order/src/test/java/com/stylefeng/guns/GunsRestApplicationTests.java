package com.stylefeng.guns;

import com.stylefeng.guns.rest.OrderApplication;
import com.stylefeng.guns.rest.common.utils.FTPUtils;
import com.stylefeng.guns.rest.modular.order.service.DefaultOrderServiceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = OrderApplication.class)
@EnableConfigurationProperties({com.stylefeng.guns.rest.common.utils.FTPUtils.class})
public class GunsRestApplicationTests {
	@Autowired
	private FTPUtils ftpUtils;
	@Autowired
	DefaultOrderServiceImpl defaultOrderService;

	@Test
	public void contextLoads() {
	}

	@Test
	public void testOrder() {
		int[] fieldId = {7, 8};
		defaultOrderService.createOrder(1, fieldId, 2, "第四排没座了");
	}

	@Test
	public void testFTPUtils(){

	}
}
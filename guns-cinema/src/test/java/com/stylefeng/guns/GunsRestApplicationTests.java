package com.stylefeng.guns;

import com.stylefeng.guns.rest.CinemaApplication;
import com.stylefeng.guns.rest.modular.cinema.service.DefaultCinemaServiceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = CinemaApplication.class)
public class GunsRestApplicationTests {
	@Autowired
	DefaultCinemaServiceImpl defaultCinemaService;

	@Test
	public void contextLoads() {
	}

}

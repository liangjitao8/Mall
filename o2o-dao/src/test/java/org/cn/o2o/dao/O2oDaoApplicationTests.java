package org.cn.o2o.dao;
import javafx.application.Application;
import org.cn.o2o.model.Area;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest(classes={Application.class})
public class O2oDaoApplicationTests {
	@Autowired
	private AreaDao areaDao;
	@Test
	public void testBQueryArea() throws Exception {
		List<Area> areaList = areaDao.queryArea();
		assertEquals(3, areaList.size());
	}
}

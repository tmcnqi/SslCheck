package ab.monitor;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import ab.sslcheck.SslCheckApplication;
import ab.sslcheck.SslCheckValid;


@RunWith(SpringRunner.class)
@SpringBootTest(classes =  SslCheckApplication.class)
/*@RunWith(SpringRunner.class)
@SpringBootTest*/
public class SslCheckApplicationTests {
	
	@Autowired
	SslCheckValid sslCheckValid;

	@Test
	public void contextLoads() {
		
		try {
			sslCheckValid.validTest();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}

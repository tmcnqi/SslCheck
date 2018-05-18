package ab.sslcheck;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SslCheckApplication {
	
	@Autowired
	static Myconfig myConfig;
	
	public static void main(String[] args) {
		SpringApplication.run(SslCheckApplication.class, args);
	}
}

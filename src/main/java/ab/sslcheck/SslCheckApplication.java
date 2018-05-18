package ab.sslcheck;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import ab.sslcheck.bean.Myconfig;

@SpringBootApplication
public class SslCheckApplication {
	
	
	public static void main(String[] args) {
		SpringApplication.run(SslCheckApplication.class, args);
	}
}

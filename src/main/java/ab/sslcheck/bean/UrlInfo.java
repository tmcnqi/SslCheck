package ab.sslcheck.bean;

import org.springframework.stereotype.Service;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
@Service
public class UrlInfo {
	String url;
	int code;
	String comment;

}

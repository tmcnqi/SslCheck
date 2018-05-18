package ab.sslcheck;

import org.springframework.stereotype.Service;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
@Service
public class DomainInfo {
	String domain;
	String startTime;
	String endTime;
	String comment;
}

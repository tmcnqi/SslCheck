package ab.sslcheck;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 告警相关配置
 * @author chengqi
 *
 */
@Setter
@Getter
@ToString
@Service
public class Myconfig {
	
	@Value("${url_domain_list}")
	String url_list;
	
	@Value("${project_list}")
	String project_list;
}

package ab.sslcheck.utils;
/**
 *  httpclient请求返回的对象信息
 *  包括：返回码、响应时间、返回对象
 * @author chengqi
 *
 */
public class ResponseUtils {
	//返回状态
	int status; 
	//返回时间
	long spanTime;
	//返回信息
	String body ;
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public long getSpanTime() {
		return spanTime;
	}
	public void setSpanTime(long spanTime) {
		this.spanTime = spanTime;
	}
	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}
	
	
	
	

}

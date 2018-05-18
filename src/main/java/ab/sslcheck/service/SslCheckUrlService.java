package ab.sslcheck.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;

import ab.sslcheck.bean.Myconfig;
import ab.sslcheck.bean.UrlInfo;
import ab.sslcheck.utils.HttpClientUtils;
import ab.sslcheck.utils.ResponseUtils;
import lombok.Getter;
import lombok.Setter;

@Service("sslCheckUrl")
public class SslCheckUrlService {
	protected static Logger logger=LoggerFactory.getLogger(SslCheckValidService.class);  
	
	@Autowired
	 Myconfig myConfig;
	
	public List<UrlInfo> testUrlValid(){
		List<UrlInfo> list = new ArrayList<UrlInfo>();
		
		List<String> errorList = new ArrayList<String>();
		String project_str = myConfig.getProject_list();
		System.out.println("project_str:" + project_str);
		
		String[] project_list = project_str.trim().split(",");
		System.out.println("project_list:" + project_list.toString());
		
		String url = "";
		ResponseUtils   response = null;
		UrlInfo urlInfo = null;
		for(int i = 0 ; i < project_list.length ; i++ ){
			  urlInfo = new UrlInfo() ;
			  url = project_list[i];
			  
			  if(!url.contains("liveapi")){
				  response = HttpClientUtils.getForObject(url, "utf-8", 20000, 20000);
			  }else{
				  Map<String, String> params = new HashMap<String,String>();
				  params.put("bn", "0");
				  params.put("time", "");
				  params.put("abType", "1");
				  params.put("viewerId", "181022");
				  params.put("companyCode", "aaaa");
				  
//				  response = HttpClientUtils.postParametersForObject(url, params , null, 20000, 20000);
				  response = HttpClientUtils.postParametersForObject(url,JSONObject.toJSONString(params));
			  }
			  if(response!=null ) {
				  urlInfo.setCode(response.getStatus());
				  urlInfo.setUrl(url);
				  if(url.contains("im-http") && response.getStatus() == 403 ){
					  urlInfo.setComment("测试通过");
				  }else if (response.getStatus()  == 200 ) {
					  urlInfo.setComment("测试通过");
				  }else{
					  urlInfo.setComment("测试不通过"); 
				  }
			  }else{
				  urlInfo.setUrl(url);
				  urlInfo.setComment("请求失败");
			  }
			  list.add(urlInfo);
		}
		return list;
		
	}
	
	
	public static void main(String[] args) {
		 Map<String, String> params = new HashMap<String,String>();
		  params.put("bn", "0");
		  params.put("time", "");
		  params.put("abType", "1");
		  params.put("viewerId", "181022");
		  params.put("companyCode", "aaaa");
		  
		  System.out.println("params:" + params.toString());
		  ResponseUtils response = HttpClientUtils.postParametersForObject("https://liveapi.bangcommunity.com/program/livelist",JSONObject.toJSONString(params));
	
	}
	
}

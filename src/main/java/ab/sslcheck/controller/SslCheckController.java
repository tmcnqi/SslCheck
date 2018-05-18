package ab.sslcheck.controller;


import java.util.ArrayList;
import java.util.List;

import javax.xml.ws.RequestWrapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import ab.sslcheck.DomainInfo;
import ab.sslcheck.SslCheckUrl;
import ab.sslcheck.SslCheckValid;
import ab.sslcheck.UrlInfo;
import lombok.Getter;
import lombok.Setter;

@Controller
public class SslCheckController {
	
	@Autowired
	SslCheckValid sslCheckValid;
	
	
	@Autowired
	SslCheckUrl sslCheckUrl;

	@Setter
	@Getter
	static String not_access_url_list  = ""; 
	
	@RequestMapping(value="/list",method=RequestMethod.GET)
    public String loginWeb(Model model){
		String ret = "";
		 //往页面上返回对象和信息
        try {
			ret = sslCheckValid.validTestForResult();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        model.addAttribute("https_check_result",ret);
        return "index";
    }
	
	@RequestMapping(value="/",method=RequestMethod.GET)
    public String list(Model model){
		List<DomainInfo> list = null;
		String not_access_url = "";
		String ret = "";
		 //往页面上返回对象和信息
        try {
			list = sslCheckValid.validTestForList();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        not_access_url = sslCheckValid.getNot_check_url();
        System.out.println("not_access_url:" +not_access_url);
        
        List<UrlInfo> project_list = null;
        
        try {
        	project_list = sslCheckUrl.testUrlValid();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        
        //返回https验证信息
        model.addAttribute("list",list);
        model.addAttribute("project_list",project_list);
        model.addAttribute("not_access_url",not_access_url);
        return "index";
    }
}

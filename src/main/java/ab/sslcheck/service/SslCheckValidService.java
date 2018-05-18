package ab.sslcheck.service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ab.sslcheck.bean.DomainInfo;
import ab.sslcheck.bean.Myconfig;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


@Service("sslCheckValid")
public class SslCheckValidService {
	protected static Logger logger=LoggerFactory.getLogger(SslCheckValidService.class);  
	
	@Setter
	@Getter
	static String not_check_url ; 
	
	
	@Autowired
	 Myconfig myConfig;
	
	static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	
	public static void main(String[] args) throws Exception {
		
		String url_domain = "https://im.bangcommunity.com";
		 URL url = new URL(url_domain);
	        HttpsURLConnection conn = (HttpsURLConnection)url.openConnection();
	        conn.connect();
	        Certificate[] certs = conn.getServerCertificates();    //会拿到完整的证书链
	        X509Certificate cert = (X509Certificate)certs[0];    //cert[0]是证书链的最下层
	        boolean flag = false;
	        Date date = new Date(2018,5,27);
	        if(!cert.getNotAfter().after(date)){  //如果日期大于之前有效日期
	        	flag = true;
	        }
	        System.out.println("域名:" + url_domain + "起始时间：" + sdf.format( cert.getNotBefore()) + " ;过期时间：" + sdf.format( cert.getNotAfter()) + ((flag==true)?" 校验通过":"校验不通过")  );
	       
	        /* System.out.println("序号：" + cert.getSerialNumber());
	        System.out.println("颁发给：" + cert.getSubjectDN().getName());
	        System.out.println("颁发者：" + cert.getIssuerDN().getName());
	        System.out.println("起始时间：" + sdf.format( cert.getNotBefore()));
	        System.out.println("过期时间：" + sdf.format( cert.getNotAfter()));
	        System.out.println("算法：" + cert.getSigAlgName());
	        System.out.println("指纹：" + getThumbPrint(cert));*/
	        conn.disconnect();
	}
	
	public void validTest()  {
		
		List<String> errorList = new ArrayList<String>();
		String url_list = myConfig.getUrl_list();
		System.out.println("url_list:" + myConfig.getUrl_list());
		
		String[] url_str_list = url_list.trim().split(",");
		System.out.println("list:" + url_str_list.toString());
		 boolean flag = false;
		for(int i = 0 ; i <url_str_list.length ; i++ ) {
			flag = false;
			String url_domain = "https://" + url_str_list[i];
			try{
			 URL url = new URL(url_domain);
		        HttpsURLConnection conn = (HttpsURLConnection)url.openConnection();
		        conn.connect();
		        Certificate[] certs = conn.getServerCertificates();    //会拿到完整的证书链
		        X509Certificate cert = (X509Certificate)certs[0];    //cert[0]是证书链的最下层
//		        System.out.println("起始时间：" + sdf.format( cert.getNotBefore()));
//		        System.out.println("过期时间：" + sdf.format( cert.getNotAfter()));
		        
		        Calendar calendar = Calendar.getInstance();
				calendar.set(Calendar.YEAR, 2018);
				calendar.set(Calendar.MONTH, 4);
				calendar.set(Calendar.DAY_OF_MONTH, 27);
//		        System.out.println("calendar: " + sdf.format( calendar.getTime() ));
		        
		        if(cert.getNotAfter().getTime()> calendar.getTimeInMillis()){  //如果日期大于之前有效日期
		        	flag = true;
//		        	logger.info("域名:" + url_domain + " ;起始时间：" + sdf.format( cert.getNotBefore()) + " ;过期时间：" + sdf.format( cert.getNotAfter()) + " 校验通过"  );
		        	System.out.println("域名:" + url_domain + ";\t起始时间：" + sdf.format( cert.getNotBefore()) + ";\t过期时间：" + sdf.format( cert.getNotAfter()) + ";\t校验通过"  );

		        }else{
		        	flag = false;
//		        	logger.error("域名:" + url_domain + " ;起始时间：" + sdf.format( cert.getNotBefore()) + " ;过期时间：" + sdf.format( cert.getNotAfter()) + " 校验不通过"  );
		        	System.err.println("域名:" + url_domain + ";\t起始时间：" + sdf.format( cert.getNotBefore()) + ";\t过期时间：" + sdf.format( cert.getNotAfter()) + ";\t校验不通过"  );
		        	errorList.add(url_domain);
		        }
//		        System.out.println("flag:"+flag);
		        
//		        System.out.println("域名:" + url_domain + " ;起始时间：" + sdf.format( cert.getNotBefore()) + " ;过期时间：" + sdf.format( cert.getNotAfter()) + ((flag==true)?" 校验通过":"校验不通过")  );
		        conn.disconnect();
			}catch(Exception e){
				e.printStackTrace();
				continue;
			}
		}
		System.err.println("校验不通过的域名:" + errorList.toString());
		System.out.println("执行结束");
	       
	}
	
	
	public String validTestForResult()  {
		String result = "";
		
		List<String> errorList = new ArrayList<String>();
		String url_list = myConfig.getUrl_list();
		System.out.println("url_list:" + myConfig.getUrl_list());
		
		String[] url_str_list = url_list.trim().split(",");
		System.out.println("list:" + url_str_list.toString());
		 boolean flag = false;
		for(int i = 0 ; i <url_str_list.length ; i++ ) {
			flag = false;
			String url_domain = "https://" + url_str_list[i];
			try{
			 URL url = new URL(url_domain);
		        HttpsURLConnection conn = (HttpsURLConnection)url.openConnection();
		        conn.connect();
		        Certificate[] certs = conn.getServerCertificates();    //会拿到完整的证书链
		        X509Certificate cert = (X509Certificate)certs[0];    //cert[0]是证书链的最下层
//		        System.out.println("起始时间：" + sdf.format( cert.getNotBefore()));
//		        System.out.println("过期时间：" + sdf.format( cert.getNotAfter()));
		        
		        Calendar calendar = Calendar.getInstance();
				calendar.set(Calendar.YEAR, 2018);
				calendar.set(Calendar.MONTH, 4);
				calendar.set(Calendar.DAY_OF_MONTH, 27);
//		        System.out.println("calendar: " + sdf.format( calendar.getTime() ));
		        
		        if(cert.getNotAfter().getTime()> calendar.getTimeInMillis()){  //如果日期大于之前有效日期
		        	flag = true;
//		        	logger.info("域名:" + url_domain + " ;起始时间：" + sdf.format( cert.getNotBefore()) + " ;过期时间：" + sdf.format( cert.getNotAfter()) + " 校验通过"  );
		        	System.out.println("域名:" + url_domain + ";\t起始时间：" + sdf.format( cert.getNotBefore()) + ";\t过期时间：" + sdf.format( cert.getNotAfter()) + ";\t校验通过"  );
		        	result += "域名:" + url_domain + ";\t起始时间：" + sdf.format( cert.getNotBefore()) + ";\t过期时间：" + sdf.format( cert.getNotAfter()) + ";\t校验通过\r\n";
		        }else{
		        	flag = false;
//		        	logger.error("域名:" + url_domain + " ;起始时间：" + sdf.format( cert.getNotBefore()) + " ;过期时间：" + sdf.format( cert.getNotAfter()) + " 校验不通过"  );
		        	System.err.println("域名:" + url_domain + ";\t起始时间：" + sdf.format( cert.getNotBefore()) + ";\t过期时间：" + sdf.format( cert.getNotAfter()) + ";\t校验不通过"  );
		        	result += "域名:" + url_domain + ";\t起始时间：" + sdf.format( cert.getNotBefore()) + ";\t过期时间：" + sdf.format( cert.getNotAfter()) + ";\t校验不\r\n";
		        	errorList.add(url_domain);
		        	
		        }
		        
//		        System.out.println("flag:"+flag);
		        
//		        System.out.println("域名:" + url_domain + " ;起始时间：" + sdf.format( cert.getNotBefore()) + " ;过期时间：" + sdf.format( cert.getNotAfter()) + ((flag==true)?" 校验通过":"校验不通过")  );
		        conn.disconnect();
			}catch(Exception e){
				e.printStackTrace();
				continue;
			}
		}
		not_check_url = "校验不通过域名:" + errorList.toString();
		result += "校验不通过域名:" + errorList.toString();
		System.err.println("校验不通过的域名:" + errorList.toString());
		System.out.println("执行结束");
		
		return result;
	       
	}
	
	
	public List<DomainInfo> validTestForList()  {
		List<DomainInfo> list = new ArrayList<DomainInfo>();
		String result="";
		
		List<String> errorList = new ArrayList<String>();
		String url_list = myConfig.getUrl_list();
		System.out.println("url_list:" + myConfig.getUrl_list());
		
		String[] url_str_list = url_list.trim().split(",");
		System.out.println("list:" + url_str_list.toString());
		DomainInfo domainInfo = new DomainInfo();
		 boolean flag = false;
		for(int i = 0 ; i <url_str_list.length ; i++ ) {
			 domainInfo = new DomainInfo();
			flag = false;
			String url_domain = "https://" + url_str_list[i];
			try{
			 URL url = new URL(url_domain);
		        HttpsURLConnection conn = (HttpsURLConnection)url.openConnection();
		        conn.connect();
		        Certificate[] certs = conn.getServerCertificates();    //会拿到完整的证书链
		        X509Certificate cert = (X509Certificate)certs[0];    //cert[0]是证书链的最下层
//		        System.out.println("起始时间：" + sdf.format( cert.getNotBefore()));
//		        System.out.println("过期时间：" + sdf.format( cert.getNotAfter()));
		        
		        Calendar calendar = Calendar.getInstance();
				calendar.set(Calendar.YEAR, 2018);
				calendar.set(Calendar.MONTH, 4);
				calendar.set(Calendar.DAY_OF_MONTH, 28);
				calendar.set(Calendar.HOUR_OF_DAY, 0);
				calendar.set(Calendar.MINUTE, 0);
				calendar.set(Calendar.SECOND, 0);
//		        System.out.println("calendar: " + sdf.format( calendar.getTime() ));
		        
		        if(cert.getNotAfter().getTime()>= calendar.getTimeInMillis()){  //如果日期大于之前有效日期
		        	flag = true;
//		        	logger.info("域名:" + url_domain + " ;起始时间：" + sdf.format( cert.getNotBefore()) + " ;过期时间：" + sdf.format( cert.getNotAfter()) + " 校验通过"  );
		        	System.out.println("域名:" + url_domain + ";\t起始时间：" + sdf.format( cert.getNotBefore()) + ";\t过期时间：" + sdf.format( cert.getNotAfter()) + ";\t校验通过"  );
		        	result += "域名:" + url_domain + ";\t起始时间：" + sdf.format( cert.getNotBefore()) + ";\t过期时间：" + sdf.format( cert.getNotAfter()) + ";\t校验通过\r\n";
		        	domainInfo = new DomainInfo();
		        	domainInfo.setDomain(url_domain);
		        	domainInfo.setStartTime(sdf.format( cert.getNotBefore()));
		        	domainInfo.setEndTime(sdf.format( cert.getNotAfter()));
		        	domainInfo.setComment("校验通过");
		        	list.add(domainInfo);
		        
		        }else{
		        	flag = false;
//		        	logger.error("域名:" + url_domain + " ;起始时间：" + sdf.format( cert.getNotBefore()) + " ;过期时间：" + sdf.format( cert.getNotAfter()) + " 校验不通过"  );
		        	System.err.println("域名:" + url_domain + ";\t起始时间：" + sdf.format( cert.getNotBefore()) + ";\t过期时间：" + sdf.format( cert.getNotAfter()) + ";\t校验不通过"  );
		        	result += "域名:" + url_domain + ";\t起始时间：" + sdf.format( cert.getNotBefore()) + ";\t过期时间：" + sdf.format( cert.getNotAfter()) + ";\t校验不\r\n";
		        	errorList.add(url_domain);
		        	domainInfo = new DomainInfo();
		        	domainInfo.setDomain(url_domain);
		        	domainInfo.setStartTime(sdf.format( cert.getNotBefore()));
		        	domainInfo.setEndTime(sdf.format( cert.getNotAfter()));
		        	domainInfo.setComment("校验不通过");
		        	list.add(domainInfo);
		        	
		        }
		        
//		        System.out.println("flag:"+flag);
		        
//		        System.out.println("域名:" + url_domain + " ;起始时间：" + sdf.format( cert.getNotBefore()) + " ;过期时间：" + sdf.format( cert.getNotAfter()) + ((flag==true)?" 校验通过":"校验不通过")  );
		        conn.disconnect();
			}catch(Exception e){
				e.printStackTrace();
				continue;
			}
		}
//		domainInfo = new DomainInfo();
		result += "校验不通过域名:" + errorList.toString();
		not_check_url ="校验不通过域名:" + errorList.toString();
		/*domainInfo.setDomain("校验不通过");
		domainInfo.setStartTime( errorList.toString());
    	domainInfo.setEndTime("");
    	list.add(domainInfo);*/
		
		System.err.println("校验不通过的域名:" + errorList.toString());
		System.out.println("执行结束");
		
		return list;
	       
	}
	
	
	
	
	private static String getThumbPrint(X509Certificate cert) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        byte[] der = cert.getEncoded();
        md.update(der);
        byte[] digest = md.digest();
        return bytesToHexString(digest);
    }
    
    private static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

}

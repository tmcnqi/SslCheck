package ab.sslcheck.utils;


import java.io.IOException;
import java.net.SocketTimeoutException;
import java.security.GeneralSecurityException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.config.RequestConfig.Builder;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;

import com.alibaba.fastjson.JSONObject;


/**
 *  依赖的jar包有：commons-lang-2.6.jar、httpclient-4.3.2.jar、httpcore-4.3.1.jar、commons-io-2.4.jar
 * @author chengqi
 * 
 *   Http请求工具包
 *
 */
public class HttpClientUtils {

    public static final int connTimeout=10000;
    public static final int readTimeout=10000;
    public static final String charset="UTF-8";
    private static HttpClient client = null;
    
    
    
    static {
        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
        cm.setMaxTotal(128);
        cm.setDefaultMaxPerRoute(128);
        client = HttpClients.custom().setConnectionManager(cm).build();
    }
   
    public static ResponseUtils postParametersForObject(String url, String parameterStr) {
        return postForObject(url,parameterStr,"application/json",charset,connTimeout,readTimeout);
    }
    
    public static String postParameters(String url, String parameterStr) throws ConnectTimeoutException, SocketTimeoutException, Exception{
        return post(url,parameterStr,"application/x-www-form-urlencoded",charset,connTimeout,readTimeout);
    }
    
    public static String postParameters(String url, String parameterStr,String charset, Integer connTimeout, Integer readTimeout) throws ConnectTimeoutException, SocketTimeoutException, Exception{
        return post(url,parameterStr,"application/x-www-form-urlencoded",charset,connTimeout,readTimeout);
    }
    
    public static String postParameters(String url, Map<String, String> params) throws ConnectTimeoutException,  
     SocketTimeoutException, Exception {
         return postForm(url, params, null, connTimeout, readTimeout);
     }
    
    public static String postParameters(String url, Map<String, String> params, Integer connTimeout,Integer readTimeout) throws ConnectTimeoutException,  
    SocketTimeoutException, Exception {
         return postForm(url, params, null, connTimeout, readTimeout);
     }
      
    public static String get(String url) throws Exception {  
        return get(url, charset, null, null);  
    }
    
    public static String get(String url, String charset) throws Exception {  
        return get(url, charset, connTimeout, readTimeout);  
    } 
    
    
    /** 
     * 发送一个 Post 请求, 使用指定的字符集编码. 
     *  
     * @param url 
     * @param body RequestBody 
     * @param mimeType 例如 application/xml "application/x-www-form-urlencoded" a=1&b=2&c=3
     * @param charset 编码 
     * @param connTimeout 建立链接超时时间,毫秒. 
     * @param readTimeout 响应超时时间,毫秒. 
     * @return ResponseBody, 使用指定的字符集编码. 
     * @throws ConnectTimeoutException 建立链接超时异常 
     * @throws SocketTimeoutException  响应超时 
     * @throws Exception 
     */  
    public static ResponseUtils postForObject(String url, String body, String mimeType,String charset, Integer connTimeout, Integer readTimeout) 
           {
    	
    	//返回对象信息：响应时间、响应信息、响应码
    	ResponseUtils responseUtils = new ResponseUtils();
        HttpClient client = null;
        HttpPost post = new HttpPost(url);
        long startTime = System.currentTimeMillis();
        String result = "";
        try {
            if (StringUtils.isNotBlank(body)) {
                HttpEntity entity = new StringEntity(body, ContentType.create(mimeType, charset));
                post.setEntity(entity);
            }
            // 设置参数
            Builder customReqConf = RequestConfig.custom();
            if (connTimeout != null) {
                customReqConf.setConnectTimeout(connTimeout);
            }
            if (readTimeout != null) {
                customReqConf.setSocketTimeout(readTimeout);
            }
            post.setConfig(customReqConf.build());

            HttpResponse res;
            if (url.startsWith("https")) {
                // 执行 Https 请求.
                client = createSSLInsecureClient();
                res = client.execute(post);
            } else {
                // 执行 Http 请求.
                client = HttpClientUtils.client;
                res = client.execute(post);
            }
            result = IOUtils.toString(res.getEntity().getContent(), charset);
            long endTime = System.currentTimeMillis();
//            responseUtils.setBody(IOUtils.toString(res.getEntity().getContent(), "UTF-8"));
            responseUtils.setStatus(res.getStatusLine().getStatusCode());
            responseUtils.setSpanTime(endTime-startTime);
            System.out.println("请求HttpClient返回:" + JSONObject.toJSONString(responseUtils));
            
        }catch (ConnectTimeoutException e) {  //连接超时
            // TODO Auto-generated catch block
        	responseUtils.setBody(e.getMessage() +	JSONObject.toJSONString(e.getStackTrace()));
        
	            responseUtils.setStatus(999);   //超时返回状态:999
	            responseUtils.setSpanTime(connTimeout);
	            System.out.println("请求HttpClient返回:" + JSONObject.toJSONString(responseUtils));
//	           e.printStackTrace();
        } catch (SocketTimeoutException e) {  //网络超时
            // TODO Auto-generated catch block
        	responseUtils.setBody(e.getMessage() +	JSONObject.toJSONString(e.getStackTrace()));
	            responseUtils.setStatus(998);   //网络超时状态:998
	            responseUtils.setSpanTime(connTimeout);
	            System.out.println("请求HttpClient返回:" + JSONObject.toJSONString(responseUtils));
//	            e.printStackTrace();
        } catch (Exception e) {  //其他异常
            // TODO Auto-generated catch block
        	responseUtils.setBody(e.getMessage() +	JSONObject.toJSONString(e.getStackTrace()));
	            responseUtils.setStatus(997);   //其他异常：997
	            responseUtils.setSpanTime(connTimeout);
	            System.out.println("请求HttpClient返回:" + JSONObject.toJSONString(responseUtils));
//	            e.printStackTrace();
        } finally {
            post.releaseConnection();
            if (url.startsWith("https") && client != null&& client instanceof CloseableHttpClient) {
                try {
					((CloseableHttpClient) client).close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
        }
        return responseUtils;
    }  
    

    /** 
     * 发送一个 Post 请求, 使用指定的字符集编码. 
     *  
     * @param url 
     * @param body RequestBody 
     * @param mimeType 例如 application/xml "application/x-www-form-urlencoded" a=1&b=2&c=3
     * @param charset 编码 
     * @param connTimeout 建立链接超时时间,毫秒. 
     * @param readTimeout 响应超时时间,毫秒. 
     * @return ResponseBody, 使用指定的字符集编码. 
     * @throws ConnectTimeoutException 建立链接超时异常 
     * @throws SocketTimeoutException  响应超时 
     * @throws Exception 
     */  
    public static String post(String url, String body, String mimeType,String charset, Integer connTimeout, Integer readTimeout) 
            throws ConnectTimeoutException, SocketTimeoutException, Exception {
        HttpClient client = null;
        HttpPost post = new HttpPost(url);
        String result = "";
        try {
            if (StringUtils.isNotBlank(body)) {
                HttpEntity entity = new StringEntity(body, ContentType.create(mimeType, charset));
                post.setEntity(entity);
            }
            // 设置参数
            Builder customReqConf = RequestConfig.custom();
            if (connTimeout != null) {
                customReqConf.setConnectTimeout(connTimeout);
            }
            if (readTimeout != null) {
                customReqConf.setSocketTimeout(readTimeout);
            }
            post.setConfig(customReqConf.build());

            HttpResponse res;
            if (url.startsWith("https")) {
                // 执行 Https 请求.
                client = createSSLInsecureClient();
                res = client.execute(post);
            } else {
                // 执行 Http 请求.
                client = HttpClientUtils.client;
                res = client.execute(post);
            }
            result = IOUtils.toString(res.getEntity().getContent(), charset);
        } finally {
            post.releaseConnection();
            if (url.startsWith("https") && client != null&& client instanceof CloseableHttpClient) {
                ((CloseableHttpClient) client).close();
            }
        }
        return result;
    }  
    
    
    /** 
     * 提交form表单 
     *  
     * @param url 
     * @param params 
     * @param connTimeout 
     * @param readTimeout 
     * @return 
     * @throws ConnectTimeoutException 
     * @throws SocketTimeoutException 
     * @throws Exception 
     */  
    public static String postForm(String url, Map<String, String> params, Map<String, String> headers, Integer connTimeout,Integer readTimeout) throws ConnectTimeoutException,  
            SocketTimeoutException, Exception {  
  
        HttpClient client = null;  
        HttpPost post = new HttpPost(url);  
        try {  
            if (params != null && !params.isEmpty()) {  
                List<NameValuePair> formParams = new ArrayList<org.apache.http.NameValuePair>();  
                Set<Entry<String, String>> entrySet = params.entrySet();  
                for (Entry<String, String> entry : entrySet) {  
                    formParams.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));  
                }  
                UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formParams, Consts.UTF_8);  
                post.setEntity(entity);  
            }
            
            if (headers != null && !headers.isEmpty()) {  
                for (Entry<String, String> entry : headers.entrySet()) {  
                    post.addHeader(entry.getKey(), entry.getValue());  
                }  
            }  
            // 设置参数  
            Builder customReqConf = RequestConfig.custom();  
            if (connTimeout != null) {  
                customReqConf.setConnectTimeout(connTimeout);  
            }  
            if (readTimeout != null) {  
                customReqConf.setSocketTimeout(readTimeout);  
            }  
            post.setConfig(customReqConf.build());  
            HttpResponse res = null;  
            if (url.startsWith("https")) {  
                // 执行 Https 请求.  
                client = createSSLInsecureClient();  
                res = client.execute(post);  
            } else {  
                // 执行 Http 请求.  
                client = HttpClientUtils.client;  
                res = client.execute(post);  
            }  
            System.out.println("httpclient返回码：" + res.getStatusLine().getStatusCode());
            return IOUtils.toString(res.getEntity().getContent(), "UTF-8");  
        } finally {  
            post.releaseConnection();  
            if (url.startsWith("https") && client != null  
                    && client instanceof CloseableHttpClient) {  
                ((CloseableHttpClient) client).close();  
            }  
        }  
    } 
    
    /** 
     * 提交form表单 返回response对象：返回码、响应时间、返回信息
     *  
     * @param url 
     * @param params 
     * @param connTimeout 
     * @param readTimeout 
     * @return 
     * @throws ConnectTimeoutException 
     * @throws SocketTimeoutException 
     * @throws Exception 
     */  
    public static ResponseUtils postFormForObject(String url, Map<String, String> params, Map<String, String> headers, Integer connTimeout,Integer readTimeout) /*throws ConnectTimeoutException,  
            SocketTimeoutException, Exception*/ {  
    	//返回对象信息：响应时间、响应信息、响应码
    	ResponseUtils responseUtils = new ResponseUtils();
        HttpClient client = null;  
        HttpPost post = new HttpPost(url);  
        long startTime = System.currentTimeMillis();
        try {  
            if (params != null && !params.isEmpty()) {  
                List<NameValuePair> formParams = new ArrayList<org.apache.http.NameValuePair>();  
                Set<Entry<String, String>> entrySet = params.entrySet();  
                for (Entry<String, String> entry : entrySet) {  
                    formParams.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));  
                }  
                UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formParams, Consts.UTF_8);  
                post.setEntity(entity);  
            }
            
            if (headers != null && !headers.isEmpty()) {  
                for (Entry<String, String> entry : headers.entrySet()) {  
                    post.addHeader(entry.getKey(), entry.getValue());  
                }  
            }  
            // 设置参数  
            Builder customReqConf = RequestConfig.custom();  
            if (connTimeout != null) {  
                customReqConf.setConnectTimeout(connTimeout);  
            }  
            if (readTimeout != null) {  
                customReqConf.setSocketTimeout(readTimeout);  
            }  
            post.setConfig(customReqConf.build());  
            HttpResponse res = null;  
            
            
            try{
            
	            if (url.startsWith("https")) {  
	                // 执行 Https 请求.  
	                client = createSSLInsecureClient();  
	                res = client.execute(post);  
	            } else {  
	                // 执行 Http 请求.  
	                client = HttpClientUtils.client;  
	                res = client.execute(post);  
	            }  
	            long endTime = System.currentTimeMillis();
	            responseUtils.setBody(IOUtils.toString(res.getEntity().getContent(), "UTF-8"));
	            responseUtils.setStatus(res.getStatusLine().getStatusCode());
	            responseUtils.setSpanTime(endTime-startTime);
	            System.out.println("请求HttpClient返回:" + JSONObject.toJSONString(responseUtils));
	            
            }catch (ConnectTimeoutException e) {  //连接超时
                // TODO Auto-generated catch block
            	responseUtils.setBody(e.getMessage() +	JSONObject.toJSONString(e.getStackTrace()));
            
 	            responseUtils.setStatus(999);   //超时返回状态:999
 	            responseUtils.setSpanTime(connTimeout);
 	            System.out.println("请求HttpClient返回:" + JSONObject.toJSONString(responseUtils));
// 	           e.printStackTrace();
            } catch (SocketTimeoutException e) {  //网络超时
                // TODO Auto-generated catch block
            	responseUtils.setBody(e.getMessage() +	JSONObject.toJSONString(e.getStackTrace()));
 	            responseUtils.setStatus(998);   //网络超时状态:998
 	            responseUtils.setSpanTime(connTimeout);
 	            System.out.println("请求HttpClient返回:" + JSONObject.toJSONString(responseUtils));
// 	            e.printStackTrace();
            } catch (Exception e) {  //其他异常
                // TODO Auto-generated catch block
            	responseUtils.setBody(e.getMessage() +	JSONObject.toJSONString(e.getStackTrace()));
  	            responseUtils.setStatus(997);   //其他异常：997
  	            responseUtils.setSpanTime(connTimeout);
  	            System.out.println("请求HttpClient返回:" + JSONObject.toJSONString(responseUtils));
//  	            e.printStackTrace();
            }
            
            return responseUtils;  
            
        } finally {  
            post.releaseConnection();  
            if (url.startsWith("https") && client != null  
                    && client instanceof CloseableHttpClient) {  
                try {
					((CloseableHttpClient) client).close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}  
            }  
        }  
    } 
    
    
    
    
    /** 
     * 发送一个 GET 请求 
     *  
     * @param url 
     * @param charset 
     * @param connTimeout  建立链接超时时间,毫秒. 
     * @param readTimeout  响应超时时间,毫秒. 
     * @return 
     * @throws ConnectTimeoutException   建立链接超时 
     * @throws SocketTimeoutException   响应超时 
     * @throws Exception 
     */  
    public static String get(String url, String charset, Integer connTimeout,Integer readTimeout) 
            throws ConnectTimeoutException,SocketTimeoutException, Exception { 
        
        HttpClient client = null;  
        HttpGet get = new HttpGet(url);  
        String result = "";  
        try {  
            // 设置参数  
            Builder customReqConf = RequestConfig.custom();  
            if (connTimeout != null) {  
                customReqConf.setConnectTimeout(connTimeout);  
            }  
            if (readTimeout != null) {  
                customReqConf.setSocketTimeout(readTimeout);  
            }  
            get.setConfig(customReqConf.build());  
  
            HttpResponse res = null;  
  
            if (url.startsWith("https")) {  
                // 执行 Https 请求.  
                client = createSSLInsecureClient();  
                res = client.execute(get);  
            } else {  
                // 执行 Http 请求.  
                client = HttpClientUtils.client;  
                res = client.execute(get);  
            }  
  
            result = IOUtils.toString(res.getEntity().getContent(), charset);  
        } finally {  
            get.releaseConnection();  
            if (url.startsWith("https") && client != null && client instanceof CloseableHttpClient) {  
                ((CloseableHttpClient) client).close();  
            }  
        }  
        return result;  
    }  
    
    
    public static ResponseUtils getForObject(String url, String charset, Integer connTimeout,Integer readTimeout){ 
        //返回对象
    	ResponseUtils responseUtils = new ResponseUtils();
    	
        HttpClient client = null;  
        HttpGet get = new HttpGet(url);  
        String result = "";  
        try {  
            // 设置参数  
            Builder customReqConf = RequestConfig.custom();  
            if (connTimeout != null) {  
                customReqConf.setConnectTimeout(connTimeout);  
            }  
            if (readTimeout != null) {  
                customReqConf.setSocketTimeout(readTimeout);  
            }  
            get.setConfig(customReqConf.build());  
  
            HttpResponse res = null;  
            long startTime = System.currentTimeMillis();
            try{
	            if (url.startsWith("https")) {  
	                // 执行 Https 请求.  
	                client = createSSLInsecureClient();  
	                res = client.execute(get);  
	            } else {  
	                // 执行 Http 请求.  
	                client = HttpClientUtils.client;  
	                res = client.execute(get);  
	            }  
	            result = IOUtils.toString(res.getEntity().getContent(), charset);  
	            long endTime = System.currentTimeMillis();
	            responseUtils.setBody(result);
	            responseUtils.setStatus(res.getStatusLine().getStatusCode());
	            responseUtils.setSpanTime(endTime-startTime);
	            System.out.println("请求HttpClient返回:" + JSONObject.toJSONString(responseUtils));
            }catch (ConnectTimeoutException e) {  //连接超时
                // TODO Auto-generated catch block
            	responseUtils.setBody(e.getMessage());
 	            responseUtils.setStatus(999);   //超时返回状态:999
 	            responseUtils.setSpanTime(connTimeout);
 	            System.out.println("请求HttpClient返回:" + JSONObject.toJSONString(responseUtils));
 	           e.printStackTrace();
            } catch (SocketTimeoutException e) {  //网络超时
                // TODO Auto-generated catch block
                responseUtils.setBody(e.getMessage());
 	            responseUtils.setStatus(998);   //网络超时状态:998
 	            responseUtils.setSpanTime(connTimeout);
 	            System.out.println("请求HttpClient返回:" + JSONObject.toJSONString(responseUtils));
 	            e.printStackTrace();
            } catch (Exception e) {  //其他异常
                // TODO Auto-generated catch block
            	 responseUtils.setBody(e.getMessage());
  	            responseUtils.setStatus(997);   //其他异常：997
  	            responseUtils.setSpanTime(connTimeout);
  	            System.out.println("请求HttpClient返回:" + JSONObject.toJSONString(responseUtils));
  	            e.printStackTrace();
            }
            
        } finally {  
            get.releaseConnection();  
            if (url.startsWith("https") && client != null && client instanceof CloseableHttpClient) {  
                try {
					((CloseableHttpClient) client).close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}  
            }  
        }  
        return responseUtils;  
    }  
    
    
    /** 
     * 从 response 里获取 charset 
     *  
     * @param ressponse 
     * @return 
     */  
    @SuppressWarnings("unused")  
    private static String getCharsetFromResponse(HttpResponse ressponse) {  
        // Content-Type:text/html; charset=GBK  
        if (ressponse.getEntity() != null  && ressponse.getEntity().getContentType() != null && ressponse.getEntity().getContentType().getValue() != null) {  
            String contentType = ressponse.getEntity().getContentType().getValue();  
            if (contentType.contains("charset=")) {  
                return contentType.substring(contentType.indexOf("charset=") + 8);  
            }  
        }  
        return null;  
    }  
    
    
    
    /**
     * 创建 SSL连接
     * @return
     * @throws GeneralSecurityException
     */
    private static CloseableHttpClient createSSLInsecureClient() throws GeneralSecurityException {
        try {
            SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
                        public boolean isTrusted(X509Certificate[] chain,String authType) throws CertificateException {
                            return true;
                        }
                    }).build();
            
            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext, new X509HostnameVerifier() {

                        @Override
                        public boolean verify(String arg0, SSLSession arg1) {
                            return true;
                        }

                        @Override
                        public void verify(String host, SSLSocket ssl)
                                throws IOException {
                        }

                        @Override
                        public void verify(String host, X509Certificate cert)
                                throws SSLException {
                        }

                        @Override
                        public void verify(String host, String[] cns,
                                String[] subjectAlts) throws SSLException {
                        }

                    });
            
            return HttpClients.custom().setSSLSocketFactory(sslsf).build();
            
        } catch (GeneralSecurityException e) {
            throw e;
        }
    }
    
    public static void main(String[] args) {  
//            String str= post("https://localhost:443/ssl/test.shtml","name=12&page=34","application/x-www-form-urlencoded", "UTF-8", 10000, 10000);
//            String str= get("https://localhost:443/ssl/test.shtml?name=12&page=34","GBK");
            /*Map<String,String> map = new HashMap<String,String>();
            map.put("name", "111");
            map.put("page", "222");
            String str= postForm("https://localhost:443/ssl/test.shtml",map,null, 1, 1);
            System.out.println(str);*/
    		/*Map<String,Object> map1 = new HashMap<String,Object>();
    		String str = "{\"Content-Type\":\"application/json; charset=utf-8\"}";
    		map1 =  JSONObject.parseObject(str, Map.class);
    		System.out.println("map1:" + map1 );*/
    	
        /*	Map<String,String> map = new HashMap<String,String>();
            map.put("name", "111");
            map.put("page", "222");
            ResponseUtils responseUtils= postFormForObject("https://localhost:443/ssl/test.shtml",map,null, 1, 1);
            System.out.println("ResponseUtils:" + JSONObject.toJSONString(responseUtils));*/
            
            String str1 = "{\"Content-Type\":\"application/json; charset=utf-8\"}";
            Map<String,String> header =  JSONObject.parseObject(str1, Map.class);
            Map<String,String> params = new HashMap<String,String>();
            /*{"bn":"0","time":"","abType":"1","viewerId":"18583","companyCode":""}*/
            params.put("bn", "0");
            params.put("time", "");
            params.put("abType","1");
            params.put("viewerId", "18583");
            params.put("companyCode", "");
            System.out.println("请求头:" + header );
            System.out.println("请求参数:" + params );
            ResponseUtils response= postFormForObject("https://uliveapi.bangcommunity.com/program/livelist",params,header, 10000, 10000);
            System.out.println("ResponseUtils:" + JSONObject.toJSONString(response));
    }
    
    
    
}
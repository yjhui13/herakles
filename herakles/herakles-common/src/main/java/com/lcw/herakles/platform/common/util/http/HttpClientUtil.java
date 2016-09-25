package com.lcw.herakles.platform.common.util.http;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.lcw.herakles.platform.common.constant.ApplicationConstant;
import com.lcw.herakles.platform.common.util.http.pool.HttpClientPoolConnectionManager;

/**
 * HTTP client util
 * 
 * @author chenwulou
 *
 */
public class HttpClientUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpClientUtil.class);

    public static void main(String[] args) {
        String url = "http://127.0.0.1:8081/herakles-web/web/product/delete";
        Map<String, String> params = new HashMap<String, String>();
        params.put("id", "4b74866f2e8e42578f3a32a6f9bf8324");
        HttpClientUtil.post(url, params);
    }

    /**
     * 发送 post请求
     * @param url
     * @param params
     * @return
     */
    public static String post(String url, Map<String, String> params) {
        String result = null;
        CloseableHttpClient httpclient = HttpClientPoolConnectionManager.getInstance().getHttpClient();
        HttpPost httppost = new HttpPost(url);
        List<NameValuePair> formparams = Lists.newArrayList();
        CloseableHttpResponse response = null;

        Set<String> keySet = params.keySet();
        for (String key : keySet) {
            formparams.add(new BasicNameValuePair(key, params.get(key)));
        }
        try {
            UrlEncodedFormEntity uefEntity = new UrlEncodedFormEntity(formparams, ApplicationConstant.UTF_8);
            httppost.setEntity(uefEntity);
            response = httpclient.execute(httppost);

            HttpEntity entity = response.getEntity();
            if (entity != null) {
                result = EntityUtils.toString(entity, ApplicationConstant.UTF_8);
                EntityUtils.consume(entity);
            }

        } catch (IOException e) {
            httppost.abort();
            LOGGER.error("HttpClientUtil.post,{}", e);
        } finally {
            if (response != null) {
                try {
                    response.close();
                    // httpclient.close();
                } catch (IOException e) {
                    httppost.abort();
                    LOGGER.error("HttpClientUtil.post,{}", e);
                }
            }
            httppost.releaseConnection();
        }
        return result;
    }

    /**
     * 发送 get请求
     * 
     * @param url
     * @param map
     * @return
     */
    public static String get(String url) {
        String result = null;
        CloseableHttpClient httpclient = HttpClientPoolConnectionManager.getInstance().getHttpClient();
        CloseableHttpResponse response = null;
        HttpGet httpGet = new HttpGet(url);
        try {
            response = httpclient.execute(httpGet);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                result = EntityUtils.toString(entity, ApplicationConstant.UTF_8);
                EntityUtils.consume(entity);
            }
        } catch (IOException e) {
            httpGet.abort();
            LOGGER.error("HttpClientUtil.get,{}", e);
        } finally {
            if (response != null) {
                try {
                    response.close();
                    // httpclient.close();
                } catch (IOException e) {
                    httpGet.abort();
                    LOGGER.error("HttpClientUtil.get,{}", e);
                }
            }
            httpGet.releaseConnection();
        }
        return result;
    }

}
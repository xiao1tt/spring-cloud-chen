package com.chen.nozdormu.core.util;

import com.chen.nozdormu.core.biz.model.ReturnT;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.*;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.cert.X509Certificate;

/**
 * @author xuxueli 2018-11-25 00:55:31
 */
public class JobRemotingUtil {

    private static Logger logger = LoggerFactory.getLogger(JobRemotingUtil.class);
    public static final String JOB_ACCESS_TOKEN = "JOB-ACCESS-TOKEN";


    /**
     * trust-https start
     *
     * @param connection
     */
    private static void trustAllHosts(HttpsURLConnection connection) {
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, TRUST_ALL_CERTS, new java.security.SecureRandom());
            SSLSocketFactory newFactory = sc.getSocketFactory();

            connection.setSSLSocketFactory(newFactory);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        connection.setHostnameVerifier((hostname, session) -> true);
    }

    private static final TrustManager[] TRUST_ALL_CERTS = new TrustManager[]{new X509TrustManager() {
        @Override
        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
            return new java.security.cert.X509Certificate[]{};
        }

        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) {
        }
    }};
    // trust-https end

    /**
     * post
     */
    public static <T> ReturnT<T> postBody(String url, String accessToken, int timeout, Object requestObj,
                                          Class<T> returnClass) {
        HttpURLConnection connection = null;
        BufferedReader bufferedReader = null;
        try {
            // connection
            URL realUrl = new URL(url);
            connection = (HttpURLConnection) realUrl.openConnection();

            // trust-https
            boolean useHttps = url.startsWith("https");
            if (useHttps) {
                HttpsURLConnection https = (HttpsURLConnection) connection;
                trustAllHosts(https);
            }

            // connection setting
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setUseCaches(false);
            connection.setReadTimeout(timeout * 1000);
            connection.setConnectTimeout(3 * 1000);
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
            connection.setRequestProperty("Accept-Charset", "application/json;charset=UTF-8");

            if (accessToken != null && accessToken.trim().length() > 0) {
                connection.setRequestProperty(JOB_ACCESS_TOKEN, accessToken);
            }

            // do connection
            connection.connect();

            // write requestBody
            if (requestObj != null) {
                String requestBody = GsonTool.toJson(requestObj);

                DataOutputStream dataOutputStream = new DataOutputStream(connection.getOutputStream());
                dataOutputStream.write(requestBody.getBytes(StandardCharsets.UTF_8));
                dataOutputStream.flush();
                dataOutputStream.close();
            }

            // valid StatusCode
            int statusCode = connection.getResponseCode();
            if (statusCode != 200) {
                return new ReturnT<>(ReturnT.FAIL_CODE, "remoting fail, StatusCode(" + statusCode + ") invalid. for url:" + url);
            }

            // result
            bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder result = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                result.append(line);
            }
            String resultJson = result.toString();

            // parse returnT
            try {
                return GsonTool.fromJson(resultJson, ReturnT.class, returnClass);
            } catch (Exception e) {
                logger.error("remoting response content invalid. url:{}, result:{}", url, resultJson, e);
                return new ReturnT<>(ReturnT.FAIL_CODE,
                        "    remoting (url=" + url + ") response content invalid(" + resultJson + ").");
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return new ReturnT<>(ReturnT.FAIL_CODE, "remoting error(" + e.getMessage() + "), for url : " + url);
        } finally {
            try {
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
                if (connection != null) {
                    connection.disconnect();
                }
            } catch (Exception e2) {
                logger.error(e2.getMessage(), e2);
            }
        }
    }
}

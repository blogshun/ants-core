package com.ants.common.utils;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

/**
 * @author MrShun
 * @version 1.0
 * Date 2017-06-21
 */
public class HttpUtil {

    private static final Logger LOG = LoggerFactory.getLogger(HttpUtil.class);

    /**
     * @param path    请求路径
     * @param charset 编码
     * @return 文本内容
     */
    public static String sendGet(String path, Map<String, String> headers, String charset) {
        try {
            URL url = new URL(path.trim());
            //打开连接
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            // 提交模式
            urlConnection.setRequestMethod("GET");
            //设置请求头属性
            if (headers != null) {
                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    urlConnection.setRequestProperty(entry.getKey(), entry.getValue());
                }
            }
            if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                urlConnection.connect();
                //得到输入流
                InputStream is = urlConnection.getInputStream();
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int len = 0;
                while (-1 != (len = is.read(buffer))) {
                    byteArrayOutputStream.write(buffer, 0, len);
                    byteArrayOutputStream.flush();
                }
                return byteArrayOutputStream.toString(charset == null ? "utf-8" : charset);
            }
        } catch (IOException e) {
            e.printStackTrace();
            LOG.error(e.getMessage());
        }

        return null;
    }

    public static String sendGet(String path) {
        return sendGet(path, null, null);
    }

    public static String sendGet(String path, Map<String, String> headers) {
        return sendGet(path, headers, null);
    }

    /**
     * POST请求获取数据
     *
     * @param path    路径
     * @param body    请求体
     * @param params  参数
     * @param headers 头部信息
     * @param charset 编码
     * @return 文本内容
     */
    public static String sendPost(String path, String body, Map<String, String> params, Map<String, String> headers, String charset) {
        URL url = null;
        try {
            url = new URL(path);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            // 提交模式
            httpURLConnection.setRequestMethod("POST");
            // 发送POST请求必须设置如下两行
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);
            //设置请求头属性
            if (headers != null) {
                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    httpURLConnection.setRequestProperty(entry.getKey(), entry.getValue());
                }
            }
            httpURLConnection.connect();
            // 获取URLConnection对象对应的输出流
            PrintWriter printWriter = new PrintWriter(httpURLConnection.getOutputStream());
            if (params != null) {
                // 发送请求参数
                StringBuffer postParams = new StringBuffer();
                int i = 0;
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    /** postParams.append(entry.getKey() + "=" + URLEncoder.encode(entry.getValue(), charset == null ? "utf-8" : charset));*/
                    postParams.append(entry.getKey() + "=" + entry.getValue());
                    if (i != params.size() - 1) {
                        postParams.append("&");
                    }
                    i++;
                }
                //post的参数 xx=xx&yy=yy
                printWriter.write(postParams.toString());
            }
            if (body != null) {
                printWriter.write(body);
            }
            // flush输出流的缓冲
            printWriter.flush();
            if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                //开始获取数据
                BufferedInputStream bis = new BufferedInputStream(httpURLConnection.getInputStream());
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                int len;
                byte[] arr = new byte[1024];
                while ((len = bis.read(arr)) != -1) {
                    bos.write(arr, 0, len);
                    bos.flush();
                }
                bos.close();
                return bos.toString(charset == null ? "utf-8" : charset);
            }
        } catch (Exception e) {
            e.printStackTrace();
            LOG.error(e.getMessage());
        }
        return null;
    }


    public static String sendPost(String path, Map<String, String> params, Map<String, String> headers) {
        return sendPost(path, null, params, headers, null);
    }

    public static String sendPost(String path, Map<String, String> params) {
        return sendPost(path, null, params, null, null);
    }

    public static String sendPost(String path, String body, Map<String, String> headers) {
        return sendPost(path, body, null, headers, null);
    }

    public static String sendPost(String path, String body) {
        return sendPost(path, body, null, null, null);
    }

}

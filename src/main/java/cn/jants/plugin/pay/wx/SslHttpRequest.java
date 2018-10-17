package cn.jants.plugin.pay.wx;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.SSLContext;
import java.io.*;
import java.nio.charset.Charset;
import java.security.*;
import java.security.cert.CertificateException;

/**
 * SSL Wx请求
 *
 * @author MrShun
 * @version 1.0
 */
public class SslHttpRequest {

    /**
     * p12证书获取数据
     *
     * @param url
     * @param data
     * @param certPath
     * @param machId
     * @return
     */
    public static String p12Send(String url, String data, String certPath, String machId) {
        StringBuffer sb = new StringBuffer();
        try {
            KeyStore keyStore = KeyStore.getInstance("PKCS12");
            File file = new File(certPath);
            FileInputStream instream = new FileInputStream(file);
            try {
                keyStore.load(instream, machId.toCharArray());
            } finally {
                instream.close();
            }

            // Trust own CA and all self-signed certs
            SSLContext.getDefault();
            SSLContext sslcontext = SSLContexts.custom()
                    .loadKeyMaterial(keyStore, machId.toCharArray())
                    .build();
            // Allow TLSv1 protocol only
            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
                    sslcontext,
                    new String[]{"TLSv1"},
                    null,
                    SSLConnectionSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
            CloseableHttpClient httpclient = HttpClients.custom()
                    .setSSLSocketFactory(sslsf)
                    .build();
            try {

                HttpPost httpPost = new HttpPost(url);
                httpPost.setEntity(new StringEntity(data, Charset.forName("UTF-8")));

                CloseableHttpResponse response = httpclient.execute(httpPost);

                try {
                    HttpEntity entity = response.getEntity();

                    if (entity != null) {
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(entity.getContent()));
                        String text;
                        while ((text = bufferedReader.readLine()) != null) {
                            sb.append(text);
                        }

                    }
                    EntityUtils.consume(entity);
                } finally {
                    response.close();
                }
            } finally {
                httpclient.close();
            }
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnrecoverableKeyException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }
}
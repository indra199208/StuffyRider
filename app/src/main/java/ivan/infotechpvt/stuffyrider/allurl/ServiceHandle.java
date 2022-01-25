package ivan.infotechpvt.stuffyrider.allurl;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;

public class ServiceHandle {
    public static final int GET = 1;
    public static final int POST = 2;
    static String response;

    static {
        response = null;
    }

    public String makeServiceCall(String url, int method) {
        return makeServiceCall(url, method, null);
    }

    public String makeServiceCall(String url, int method, List<NameValuePair> params) {
        try {



//            HostnameVerifier hostnameVerifier = org.apache.http.conn.ssl.SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER;
//
//            DefaultHttpClient client = new DefaultHttpClient();
//
//            SchemeRegistry registry = new SchemeRegistry();
//            SSLSocketFactory socketFactory = SSLSocketFactory.getSocketFactory();
//            socketFactory.setHostnameVerifier((X509HostnameVerifier) hostnameVerifier);
//            registry.register(new Scheme("https", socketFactory, 443));
//            SingleClientConnManager mgr = new SingleClientConnManager(client.getParams(), registry);
//            DefaultHttpClient httpClient = new DefaultHttpClient(mgr, client.getParams());
//
//            // Set verifier
//            HttpsURLConnection.setDefaultHostnameVerifier(hostnameVerifier);


            HostnameVerifier hostnameVerifier = org.apache.http.conn.ssl.SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER;

            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpResponse httpResponse = null;
            DefaultHttpClient client = new DefaultHttpClient();
            SchemeRegistry registry = new SchemeRegistry();
            SSLSocketFactory socketFactory = SSLSocketFactory.getSocketFactory();
            socketFactory.setHostnameVerifier((X509HostnameVerifier) hostnameVerifier);
            registry.register(new Scheme("https", socketFactory, 443));
            SingleClientConnManager mgr = new SingleClientConnManager(client.getParams(), registry);
            httpClient = new DefaultHttpClient(mgr, client.getParams());
            // Set verifier
            HttpsURLConnection.setDefaultHostnameVerifier(hostnameVerifier);

            if (method == POST) {
                HttpPost httpPost = new HttpPost(url);
                if (params != null) {
                    httpPost.setEntity(new UrlEncodedFormEntity(params));
                }
                httpResponse = httpClient.execute(httpPost);
            } else if (method == GET) {
                if (params != null) {
                    url = new StringBuilder(String.valueOf(url)).append("?").append(URLEncodedUtils.format(params, "utf-8")).toString();
                    System.out.println("Url  "+ url);

                }
                httpResponse = httpClient.execute(new HttpGet(url));
                System.out.println(httpResponse);
            }
            response = EntityUtils.toString(httpResponse.getEntity());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e2) {
            e2.printStackTrace();
        } catch (IOException e3) {
            e3.printStackTrace();
        }
        return response;
    }

}

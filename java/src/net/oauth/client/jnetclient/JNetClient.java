/*
* $Id: $
*
* (C) Copyright 2013 Jaxo Inc.  All rights reserved.
* This work contains confidential trade secrets of Jaxo Inc.
* Use, examination, copying, transfer and disclosure to others
* are prohibited, except with the express written agreement of Jaxo.
*
* Author:  Pierre G. Richard
* Written: 1/4/2013
*/
package net.oauth.client.jnetclient;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.util.Map;

import net.oauth.client.ExcerptInputStream;
import net.oauth.http.HttpMessage;
import net.oauth.http.HttpResponseMessage;

/*-- class JNetClient --+
*//**
* Utility methods for an OAuth client based on a standard Java Net Client
*
* @author  Pierre G. Richard
* @version $Id: $
*/
public class JNetClient implements net.oauth.http.HttpClient
{
   /*-----------------------------------------------------------------execute-+
   *//**
   *//*
   +-------------------------------------------------------------------------*/
   public HttpResponseMessage execute(
      HttpMessage request,
      Map<String, Object> parameters
   ) throws IOException {
System.err.println("-------------- execute");
System.err.println("MTH: " + request.method);
System.err.println("URL: " + request.url.toExternalForm());
       if (!request.method.equalsIgnoreCase(POST)) {
          throw new IOException("Only POST is implemented");
       }
       byte[] excerpt = null;
       HttpURLConnection conn = (HttpURLConnection)request.url.openConnection();
       for (Map.Entry<String, String> header : request.headers) {
          conn.addRequestProperty(header.getKey(), header.getValue());
       }
       for (Map.Entry<String, Object> p : parameters.entrySet()) {
          String name = p.getKey();
          String value = p.getValue().toString();
//        if (FOLLOW_REDIRECTS.equals(name)) {
//           HttpURLConnection.setFollowRedirects(Boolean.parseBoolean(value));
//        }else
          if (READ_TIMEOUT.equals(name)) {
System.err.println("Read Timeout: " + Integer.parseInt(value));
             conn.setReadTimeout(Integer.parseInt(value));
          }else if (CONNECT_TIMEOUT.equals(name)) {
System.err.println("Connect Timeout: " + Integer.parseInt(value));
             conn.setConnectTimeout(Integer.parseInt(value));
          }
       }
       conn.setDoOutput(true);
       conn.setRequestMethod("POST");
       OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
       InputStream body = request.getBody();
       if (body != null) {
          ExcerptInputStream input = new ExcerptInputStream(body);
          excerpt = input.getExcerpt();
          int ch;
          while ((ch = input.read()) != -1) writer.write(ch);
          input.close();
       }
       writer.close();
       return new JNetResponseMessage(request, excerpt, conn);
   }
/*
    private static final HttpClientPool SHARED_CLIENT = new SingleClient();

    private static final boolean isGaeUsed = true; // DEBUG DEBUG DEBUG

    private static class SingleClient implements HttpClientPool {
        SingleClient() {
           HttpClient client;
           HttpParams params;
           if (isGaeUsed) {
              client = new DefaultHttpClient();
              params = client.getParams();
              ClientConnectionManager connectionManager = new GAEConnectionManager();
              client = new DefaultHttpClient(connectionManager, params);

//            ClientConnectionManager connectionManager = new GAEConnectionManager();
//            params = new BasicHttpParams();
//            HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
//            HttpProtocolParams.setContentCharset(params, HTTP.DEFAULT_CONTENT_CHARSET);
//            HttpProtocolParams.setUseExpectContinue(params, true);
//            HttpConnectionParams.setTcpNoDelay(params, true);
//            HttpConnectionParams.setSocketBufferSize(params, 8192);
//            client = new DefaultHttpClient(connectionManager, params);
           }else {
              client = new DefaultHttpClient();
              ClientConnectionManager mgr = client.getConnectionManager();
              if (!(mgr instanceof ThreadSafeClientConnManager)) {
                 params = client.getParams();
                 client = new DefaultHttpClient(
                    new ThreadSafeClientConnManager(
                       params, mgr.getSchemeRegistry()
                    ),
                    params
                 );
              }
           }
           this.client = client;
        }

        private final HttpClient client;

        public HttpClient getHttpClient(URL server) {
            return client;
        }
    }
*/
}

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
             conn.setReadTimeout(Integer.parseInt(value));
          }else if (CONNECT_TIMEOUT.equals(name)) {
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
}
/*===========================================================================*/

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
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.util.Map;

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
//*/  if (request != null) { dontExecute(request, parameters); return null; }
      if (request.method.equalsIgnoreCase(POST)) {
         InputStream body = request.getBody();
         if (body != null) {
            request.removeHeaders(HttpMessage.CONTENT_LENGTH);
         }
         HttpURLConnection conn = (HttpURLConnection)request.url.openConnection();
         for (Map.Entry<String, String> header : request.headers) {
            conn.addRequestProperty(header.getKey(), header.getValue());
         }
         for (Map.Entry<String, Object> p : parameters.entrySet()) {
            String name = p.getKey();
            String value = p.getValue().toString();
            if (FOLLOW_REDIRECTS.equals(name)) {
               conn.setInstanceFollowRedirects(Boolean.parseBoolean(value));
            }else if (READ_TIMEOUT.equals(name)) {
               conn.setReadTimeout(Integer.parseInt(value));
            }else if (CONNECT_TIMEOUT.equals(name)) {
               conn.setConnectTimeout(Integer.parseInt(value));
            }
         }
         conn.setChunkedStreamingMode(2048);
         conn.addRequestProperty("Expect", "100-continue");
         conn.setDoOutput(true);
         conn.setRequestMethod("POST");
         OutputStream output = conn.getOutputStream();
         if (body != null) {
            int ch;
            while ((ch = body.read()) != -1) output.write(ch);
            body.close();
         }
         output.close();
         return new JNetResponseMessage(request, conn);

      }else if (request.method.equalsIgnoreCase(GET)) {
         HttpURLConnection conn = (HttpURLConnection)request.url.openConnection();
         for (Map.Entry<String, String> header : request.headers) {
            conn.addRequestProperty(header.getKey(), header.getValue());
         }
         for (Map.Entry<String, Object> p : parameters.entrySet()) {
            String name = p.getKey();
            String value = p.getValue().toString();
            if (FOLLOW_REDIRECTS.equals(name)) {
               conn.setInstanceFollowRedirects(Boolean.parseBoolean(value));
            }else if (READ_TIMEOUT.equals(name)) {
               conn.setReadTimeout(Integer.parseInt(value));
            }else if (CONNECT_TIMEOUT.equals(name)) {
               conn.setConnectTimeout(Integer.parseInt(value));
            }
         }
         conn.setRequestMethod("GET");
         return new JNetResponseMessage(request, conn);

      }else {
         throw new IOException("Handling of " + request.method + " not implemented");
      }
   }


//*/    public void dontExecute(
//*/       HttpMessage request,
//*/       Map<String, Object> parameters
//*/    ) throws IOException {
//*/       if (request.method.equalsIgnoreCase(POST)) {
//*/          InputStream body = request.getBody();
//*/          if (body != null) {
//*/             request.removeHeaders(HttpMessage.CONTENT_LENGTH);
//*/          }
//*/          System.out.println("HttpURLConnection: " + request.url);
//*/ //       HttpURLConnection conn = (HttpURLConnection)request.url.openConnection();
//*/          System.out.println("Property:");
//*/          for (Map.Entry<String, String> header : request.headers) {
//*/             System.out.println("\t\"" + header.getKey() + "\"\t\"" + header.getValue() + "\"");
//*/ //          conn.addRequestProperty(header.getKey(), header.getValue());
//*/          }
//*/          System.out.println("\t\"Expect\"\t\"100-continue\"");
//*/          for (Map.Entry<String, Object> p : parameters.entrySet()) {
//*/             String name = p.getKey();
//*/             String value = p.getValue().toString();
//*/             if (FOLLOW_REDIRECTS.equals(name)) {
//*/ //             conn.setInstanceFollowRedirects(Boolean.parseBoolean(value));
//*/                System.out.println("FollowRedirects: " + value);
//*/             }else if (READ_TIMEOUT.equals(name)) {
//*/ //             conn.setReadTimeout(Integer.parseInt(value));
//*/                System.out.println("ReadTimeOut: " + value);
//*/             }else if (CONNECT_TIMEOUT.equals(name)) {
//*/ //             conn.setConnectTimeout(Integer.parseInt(value));
//*/                System.out.println("ConnectTimeOut: " + value);
//*/             }
//*/          }
//*/          System.out.println("ChunkedStreamingMode: 2048");
//*/ //       conn.setChunkedStreamingMode(2048);
//*/ //       conn.addRequestProperty("Expect", "100-continue");
//*/ //       conn.setDoOutput(true);
//*/          System.out.println("RequestMethod: POST");
//*/ //       conn.setRequestMethod("POST");
//*/          System.out.println("BODY");
//*/ //       OutputStream output = conn.getOutputStream();
//*/          if (body != null) {
//*/             int ch;
//*/             while ((ch = body.read()) != -1) System.out.write(ch);
//*/ //          while ((ch = body.read()) != -1) output.write(ch);
//*/             body.close();
//*/          }
//*/ //       output.close();
//*/ //       return new JNetResponseMessage(request, conn);
//*/
//*/ //    }else if (request.method.equalsIgnoreCase(GET)) {
//*/ //       HttpURLConnection conn = (HttpURLConnection)request.url.openConnection();
//*/ //       for (Map.Entry<String, String> header : request.headers) {
//*/ //          conn.addRequestProperty(header.getKey(), header.getValue());
//*/ //       }
//*/ //       for (Map.Entry<String, Object> p : parameters.entrySet()) {
//*/ //          String name = p.getKey();
//*/ //          String value = p.getValue().toString();
//*/ //          if (FOLLOW_REDIRECTS.equals(name)) {
//*/ //             conn.setInstanceFollowRedirects(Boolean.parseBoolean(value));
//*/ //          }else if (READ_TIMEOUT.equals(name)) {
//*/ //             conn.setReadTimeout(Integer.parseInt(value));
//*/ //          }else if (CONNECT_TIMEOUT.equals(name)) {
//*/ //             conn.setConnectTimeout(Integer.parseInt(value));
//*/ //          }
//*/ //       }
//*/ //       conn.setRequestMethod("GET");
//*/ //       return new JNetResponseMessage(request, conn);
//*/
//*/       }else {
//*/          throw new IOException("Handling of " + request.method + " not implemented");
//*/       }
//*/    }
}
/*===========================================================================*/

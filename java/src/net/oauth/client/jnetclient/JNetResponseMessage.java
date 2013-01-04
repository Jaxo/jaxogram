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
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.oauth.OAuth;
import net.oauth.client.ExcerptInputStream;
import net.oauth.http.HttpMessage;
import net.oauth.http.HttpResponseMessage;

/*-- class JNetResponseMessage --+
*//**
*
* @author  Pierre G. Richard
* @version $Id: $
*/
public class JNetResponseMessage extends HttpResponseMessage
{
   private HttpURLConnection m_conn;
   private HttpMessage m_request;
   private String m_requestBody;

   /*-----------------------------------------------------JNetResponseMessage-+
   *//**
   * Constructor
   *//*
   +-------------------------------------------------------------------------*/
   public JNetResponseMessage(
      HttpMessage request, byte[] excerpt, HttpURLConnection conn
   ) {
      super(request.method, request.url);
      m_request = request;
      m_conn = conn;
      headers.addAll(getHeaders());
      if (excerpt != null) {
         try {
            m_requestBody = new String(excerpt, request.getContentCharset());
         }catch (Exception e) {
            m_requestBody = new String(excerpt);
         }
      }
   }

   @Override
   public int getStatusCode() throws IOException {
      return m_conn.getResponseCode();
   }

   @Override
   public InputStream openBody() throws IOException {
      return m_conn.getInputStream();
   }

   // Return a complete description of the HTTP exchange.
   @Override
   public void dump(Map<String, Object> into) throws IOException {
      super.dump(into);
      // into.put(REQUEST, getStringizedRequest());
      into.put(RESPONSE, getStringizedResponse());
   }

   private String getStringizedRequest()
   {
      StringBuilder sb = new StringBuilder();
      sb.append(m_request.method);
      sb.append(" ");
      sb.append(m_request.url.getPath());
      String query = m_request.url.getQuery();
      if ((query != null) && (query.length() > 0)) {
         sb.append("?").append(query);
      }
      sb.append(EOL);
      Map<String, List<String>> properties = m_conn.getRequestProperties();
      for (Map.Entry<String, List<String>> entry : properties.entrySet()) {
         String key = entry.getKey();
         for (String value : entry.getValue()) {
             sb.append(key).append(": ").append(value).append(EOL);
         }
      }
//    int contentLength = m_conn.getRequestProperty("Content-Length");
//    if (contentLength >= 0) {
//       sb.append("Content-Length: ").append(contentLength).append(EOL);
//    }
      if (m_requestBody != null) sb.append(EOL).append(m_requestBody).append(EOL);
      return sb.toString();
   }

   private String getStringizedResponse() throws IOException
   {
      StringBuilder sb = new StringBuilder();
      sb.append(m_conn.getResponseMessage()).append(EOL);
      Map<String, List<String>> headerFields = m_conn.getHeaderFields();
      for (Map.Entry<String, List<String>> entry : headerFields.entrySet()) {
         String key = entry.getKey();
         for (String value : entry.getValue()) {
             sb.append(key).append(": ").append(value).append(EOL);
         }
      }
      sb.append(EOL);
      if (body != null) {
         sb.append(
            new String(
              ((ExcerptInputStream)body).getExcerpt(), m_conn.getContentEncoding()
            )
         );
      }
      return sb.toString();
   }

   private List<Map.Entry<String, String>> getHeaders() {
      List<Map.Entry<String, String>> headers = new ArrayList<Map.Entry<String, String>>();
      Map<String, List<String>> headerFields = m_conn.getHeaderFields();
      for (String key : headerFields.keySet()) {
         headers.add(new OAuth.Parameter(key, m_conn.getHeaderField(key)));
      }
//    // What if same header, multiple times?
//    for (Map.Entry<String, List<String>> entry : headerFields.entrySet()) {
//       String key = entry.getKey();
//       for (String value : entry.getValue()) {
//           headers.add(new OAuth.Parameter(key, value));
//       }
//    }
      return headers;
   }
}
/*==========================================================================*/

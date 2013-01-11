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

   /*-----------------------------------------------------JNetResponseMessage-+
   *//**
   * Constructor
   *//*
   +-------------------------------------------------------------------------*/
   public JNetResponseMessage(
      HttpMessage request, HttpURLConnection conn
   ) {
      super(request.method, request.url);
      m_request = request;
      m_conn = conn;
      headers.addAll(getHeaders());
   }

   @Override
   public int getStatusCode() throws IOException {
      return m_conn.getResponseCode();
   }

   @Override
   public InputStream openBody() throws IOException {
      return m_conn.getInputStream();
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

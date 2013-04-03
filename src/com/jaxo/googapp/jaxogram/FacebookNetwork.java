/*
* $Id: $
*
* (C) Copyright 2013 Jaxo Inc.  All rights reserved.
* This work contains confidential trade secrets of Jaxo Inc.
* Use, examination, copying, transfer and disclosure to others
* are prohibited, except with the express written agreement of Jaxo.
*
* Author:  Pierre G. Richard
* Written: 3/12/2013
*/
package com.jaxo.googapp.jaxogram;

import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
//*/ import java.util.logging.Logger;

import net.oauth.OAuth;
import net.oauth.OAuthAccessor;
import net.oauth.OAuthMessage;
import net.oauth.http.HttpMessage;
import net.oauth.http.HttpResponseMessage;
import net.oauth.http.MultipartEntity;

import org.apache.commons.io.IOUtils;

/*-- class FacebookNetwork --+
*//**
*
* @author  Pierre G. Richard
* @version $Id: $
*/
public class FacebookNetwork extends OAuthorizer implements Network
{
   //*/ private static Logger logger = Logger.getLogger("com.jaxo.googapp.jaxogram.FacebookNetwork");
   private static final String CONSUMER_KEY = "139995292843954";
   private static final String CONSUMER_SECRET = "10b0780013a8bd1de859e28f531faebf";
   private static final String OAUTH_REQUEST_URL = "https://graph.facebook.com";
   private static final String OAUTH_AUTHORIZATION_URL = "http://www.facebook.com/dialog/oauth";
   private static final String OAUTH_ACCESS_URL = "https://graph.facebook.com/oauth/access_token";

   private static final String ACCESS_TOKEN_NAME = "access_token=";

   /*----------------------------------------------------------FacebookNetwork-+
   *//**
   *//*
   +-------------------------------------------------------------------------*/
   public FacebookNetwork() throws Exception {
      super(
         CONSUMER_KEY,
         CONSUMER_SECRET,
         OAUTH_REQUEST_URL,
         OAUTH_AUTHORIZATION_URL,
         OAUTH_ACCESS_URL
      );
   }

   /*----------------------------------------------------------FacebookNetwork-+
   *//**
   *//*
   +-------------------------------------------------------------------------*/
   public FacebookNetwork(String accessPass) throws Exception {
      this();
      accessor.accessToken = accessPass;
   }

   /*----------------------------------------------------------requestAuthURL-+
   *//**
   *//*
   +-------------------------------------------------------------------------*/
   public String requestAuthURL(String callbackUrl) throws Exception
   {
      accessor.tokenSecret = URLEncoder.encode(callbackUrl,"UTF-8");
      //*/ logger.info("request auth URL");
      return (
         OAUTH_AUTHORIZATION_URL +
         "?client_id=" + consumer.consumerKey +
         "&redirect_uri=" + accessor.tokenSecret +
         "&scope=publish_stream,user_photos"
      );
   }

   /*------------------------------------------------------------authenticate-+
   *//**
   *//*
   +-------------------------------------------------------------------------*/
   public String authenticate(String verifier, OAuthAccessor givenAccessor)
   throws Exception
   {
      String body;
      String userName = null;
      String accessToken = null;
      HttpResponseMessage response;
      response = client.getHttpClient().execute(
         new HttpMessage(
            OAuthMessage.GET,
            new URL(
               OAUTH_ACCESS_URL +
               "?client_id=" + consumer.consumerKey +
               "&redirect_uri=" + givenAccessor.tokenSecret +
               "&client_secret=" + consumer.consumerSecret +
               "&code=" + verifier
            )
         ),
         client.getHttpParameters()
      );
      body = IOUtils.toString(response.getBody(), response.getContentCharset());
      if (response.getStatusCode() == 200) {
         int beg = body.indexOf(ACCESS_TOKEN_NAME);
         if (beg >= 0) {
            beg += ACCESS_TOKEN_NAME.length();
            int end = body.indexOf('&', beg);
            if (end == -1) end = body.length();
            accessToken = body.substring(beg, end);
         }
      }
      if (accessToken != null) {
         response = client.getHttpClient().execute(
            new HttpMessage(
               OAuthMessage.GET,
               new URL(
                  OAUTH_REQUEST_URL +
                  "/me?fields=name&access_token=" + accessToken
               )
            ),
            client.getHttpParameters()
         );
         if (response.getStatusCode() == 200) {
            userName = JsonIterator.get(
               IOUtils.toString(response.getBody(), response.getContentCharset()),
               "name"
            );
         }
      }
      //*/ logger.info("authenticate: \n\turi:\"" + givenAccessor.tokenSecret + "\"\n\tresponse code: " + response.getStatusCode() + "\n\taccess token: \"" + ((accessToken == null)?"[NONE]" : accessToken) + "\"\n\tuser name: \"" + ((userName == null)?"[NONE]" : userName) + "\"\n\tresponse value: \"" + body + "\"" );
      if ((accessToken == null) || (userName == null)) {
         throw new Exception("RC=" + response.getStatusCode() + "\n" + body);
      }
      return (
         new StringBuilder().
         append("{ \"accessPass\":\"").
         append(accessToken).
         append("\", \"userName\":\"").
         append(userName).
         append("\" }")
      ).toString();
   }

   /*-------------------------------------------------------------whoIsAsJson-+
   *//**
   *//*
   +-------------------------------------------------------------------------*/
   public String whoIsAsJson(String id) throws Exception {
      // We do not take "id" into account (we suppose it's "me")
      JsonIterator it = new JsonIterator(get("/me", "fields=name,gender"));
      StringBuilder sb = new StringBuilder();
      sb.append("{\"name\":{\"givenName\":\"").
      append(it.next("name")).
      append("\",\"familyName\":\" \"},").
//    append("\"birthday\":\"\",").
      append("\"gender\":\"").
      append(it.next("gender")).
      append("\"}");
      return sb.toString();
   }

   /*--------------------------------------------------------listAlbumsAsJson-+
   *//**
   * @see
   *//*
   +-------------------------------------------------------------------------*/
   public String listAlbumsAsJson() throws Exception {
      JsonIterator it = new JsonIterator(
         get("/me/albums", "fields=id,name,cover_photo")
      );
      String title;
      String id;
      StringBuilder sb = new StringBuilder();
      sb.append('[');
      boolean first = true;
      while (
         ((id = it.next("id")) != null) &&
         ((title = it.next("name")) != null)
      ) {
         String url;
         if (first) {
            first = false;
         }else {
            sb.append(',');
         }
         sb.append("{\"id\":\"").append(id).
         append("\",\"title\":\"").append(title);
         if (
            ((url = it.next("cover_photo")) != null) &&
            ((url = JsonIterator.get(get("/"+url, "fields=source"), "source")) != null)
         ) {
            sb.append("\",\"thumbnailUrl\":\"").append(url);
         }
         sb.append("\"}");
      }
      sb.append(']');
      return sb.toString();
   }

   /*-------------------------------------------------------------createAlbum-+
   *//**
   *//*
   +-------------------------------------------------------------------------*/
   public void createAlbum(String title, String desc) throws Exception {
      MultipartEntity entity = new MultipartEntity();
      entity.addField("name", title, "UTF-8");
      entity.addField("message", desc, "UTF-8");
      post("/me/albums", null, entity);
      // return post("/me/albums", null, entity);
   }

   /*-------------------------------------------------------------uploadPhoto-+
   * See: https://developers.facebook.com/blog/post/498/
   *//**
   *//*
   +-------------------------------------------------------------------------*/
   public String uploadPhoto(
      String albumId,
      String title,
      String text,
      byte[] image,
      String type // png, gif, or jpg.
   )
   throws Exception
   {
      MultipartEntity entity = new MultipartEntity();
      entity.addField("message", title, "UTF-8");
      entity.addFile("file", "tmpfile." + type, "image/" + type, image);
      return post("/" + albumId + "/photos", null, entity);
   }

   /*--------------------------------------------------------------------post-+
   *//**
   *//*
   +-------------------------------------------------------------------------*/
   private String post(String path, String query, MultipartEntity entity)
   throws Exception
   {
      List<OAuth.Parameter> headers = new ArrayList<OAuth.Parameter>();
      StringBuilder sb = new StringBuilder(OAUTH_REQUEST_URL);
      sb.append(path);
      sb.append('?');
      if ((query != null) && (query.length() > 0)) {
         sb.append(query);
         sb.append('&');
      }
      sb.append("access_token=");
      sb.append(accessor.accessToken);
      headers.add(
         new OAuth.Parameter(
            net.oauth.http.HttpMessage.CONTENT_TYPE,
            entity.getContentType()
         )
      );
      HttpMessage request = new HttpMessage(
         OAuthMessage.POST, new URL(sb.toString()), entity.getBody()
      );
      request.headers.addAll(headers);
      HttpResponseMessage response = client.getHttpClient().execute(
         request,
         client.getHttpParameters()
      );
      String body = IOUtils.toString(
         response.getBody(), response.getContentCharset()
      );
      if (response.getStatusCode() != 200) {
         throw new Exception("RC=" + response.getStatusCode() + "\n" + body);
      }
      return body;
   }

   /*---------------------------------------------------------------------get-+
   *//**
   *//*
   +-------------------------------------------------------------------------*/
   private String get(String path, String query) throws Exception
   {
      StringBuilder sb = new StringBuilder(OAUTH_REQUEST_URL);
      sb.append(path);
      sb.append('?');
      if ((query != null) && (query.length() > 0)) {
         sb.append(query);
         sb.append('&');
      }
      sb.append("access_token=");
      sb.append(accessor.accessToken);
      HttpResponseMessage response= client.getHttpClient().execute(
         new HttpMessage(OAuthMessage.GET, new URL(sb.toString())),
         client.getHttpParameters()
      );
      String body = IOUtils.toString(
         response.getBody(), response.getContentCharset()
      );
      //*/ logger.info("GET " + path + " " + query + " RC:" + response.getStatusCode() + "\n\t\"" + body + "\"");
      if (response.getStatusCode() != 200) {
         throw new Exception("RC=" + response.getStatusCode() + "\n" + body);
      }
      return body;
   }
}
/*===========================================================================*/

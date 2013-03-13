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
/**/ import java.util.logging.Logger;

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
   /**/ private static Logger logger = Logger.getLogger("com.jaxo.googapp.jaxogram.FacebookNetwork");
   private static final String CONSUMER_KEY = "139995292843954";
   private static final String CONSUMER_SECRET = "10b0780013a8bd1de859e28f531faebf";
   private static final String OAUTH_REQUEST_URL = "https://graph.facebook.com";
   private static final String OAUTH_AUTHORIZATION_URL = "http://www.facebook.com/dialog/oauth";
   private static final String OAUTH_ACCESS_URL = "https://graph.facebook.com/oauth/access_token";

   private static final String ACCESS_TOKEN_NAME = "access_token=";
   private static final String USERNAME_NAME = "\"name\"";

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
      /**/ logger.info("request auth URL");
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
   public String[] authenticate(String verifier, OAuthAccessor givenAccessor)
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
         body = IOUtils.toString(response.getBody(), response.getContentCharset());
         if (response.getStatusCode() == 200) {
            int beg = body.indexOf(USERNAME_NAME);
            if (beg >= 0) {
               beg = body.indexOf('"', beg+USERNAME_NAME.length()+1) + 1;
               userName = body.substring(beg, body.indexOf('"', beg));
            }
         }
      }
      /**/ logger.info("authenticate: \n\turi:\"" + givenAccessor.tokenSecret + "\"\n\tresponse code: " + response.getStatusCode() + "\n\taccess token: \"" + ((accessToken == null)?"[NONE]" : accessToken) + "\"\n\tuser name: \"" + ((userName == null)?"[NONE]" : userName) + "\"\n\tresponse value: \"" + body + "\"" );
      if ((accessToken == null) || (userName == null)) {
         throw new Exception("RC=" + response.getStatusCode() + "\n" + body);
      }
      return new String[] { accessToken, userName };
   }

   /*-------------------------------------------------------------whoIsAsJson-+
   *//**
   *//*
   +-------------------------------------------------------------------------*/
   public String whoIsAsJson(String id) throws Exception {
      // We do not take "id" into account (we suppose it's "me")
      HttpResponseMessage response= client.getHttpClient().execute(
         new HttpMessage(
            OAuthMessage.GET,
            new URL(
               OAUTH_REQUEST_URL +
               "/me?access_token=" +  accessor.accessToken
//             "/me/albums?access_token=" +  accessor.accessToken
            )
         ),
         client.getHttpParameters()
      );
      String foo = IOUtils.toString(response.getBody(), response.getContentCharset());
      /**/ logger.info("whoIs:" + foo);
      return foo;
//    StringBuilder sb = new StringBuilder();
//    sb.append("{\"name\":{\"givenName\":\"").
//    append("Not Yet Implemented").
//    append("\",\"familyName\":\" \"},").
//    append("\"birthday\":\"\",").
//    append("\"gender\":\"\"}");
//    return sb.toString();
   }

   /*--------------------------------------------------------listAlbumsAsJson-+
   *//**
   * @see
   *//*
   +-------------------------------------------------------------------------*/
   public String listAlbumsAsJson() throws Exception {
      StringBuilder sb = new StringBuilder();
      sb.append("[{\"id\":\"").
      append("15799571").
      append("\",\"title\":\"").append("Dummy").
      append("\",\"description\":\"").
      append("Unused for Facebook").
      append("\",\"thumbnailUrl\":\"").append("").
      append("\"}]");
      return sb.toString();
   }

   /*-------------------------------------------------------------createAlbum-+
   *//**
   *//*
   +-------------------------------------------------------------------------*/
   public void createAlbum(String title, String desc) throws Exception {
      // TODO
   }

   /*-------------------------------------------------------------uploadPhoto-+
   * See: https://developers.facebook.com/blog/post/498/
   *//**
   *//*
   +-------------------------------------------------------------------------*/
   public String uploadPhoto(
      String albumId,
      String title,
      byte[] image,
      String type // png, gif, or jpg.
   )
   throws Exception
   {
      MultipartEntity entity = new MultipartEntity();
      List<OAuth.Parameter> headers = new ArrayList<OAuth.Parameter>();
      entity.addField("message", "A acat", "UTF-8");
      entity.addFile("file", "tmpfile." + type, "image/" + type, image);
      headers.add(
         new OAuth.Parameter(
            net.oauth.http.HttpMessage.CONTENT_TYPE,
            entity.getContentType()
         )
      );
      HttpMessage request = new HttpMessage(
         OAuthMessage.POST,
         new URL(
            OAUTH_REQUEST_URL + "/me/photos" +
//          "?access_token=AAABZCUzGzP7IBAFyPzYNPVnWSE8pGHpOk9ZBqIXuLVNQfZAo3FPzaZB8wIuO6mXghq3IO7e7yvlABkfqB6mjDvIKB3ZBdNAEKpyGJ8VhNzQZDZD"
            "?access_token=AAABZCUzGzP7IBAFyPzYNPVnWSE8pGHpOk9ZBqIXuLVNQfZAo3FPzaZB8wIuO6mXghq3IO7e7yvlABkfqB6mjDvIKB3ZBdNAEKpyGJ8VhNzQZDZD"
         ),
         entity.getBody()
      );
      request.headers.addAll(headers);
      HttpResponseMessage response = client.getHttpClient().execute(
         request,
         client.getHttpParameters()
      );
      String body = IOUtils.toString(response.getBody(), response.getContentCharset());
      /**/ logger.info("upld: response: \"" + body + "\"");
      // {"id":"10151604445814623","post_id":"588959622_10151604443959623"}
      return body;
   }
}
/*===========================================================================*/

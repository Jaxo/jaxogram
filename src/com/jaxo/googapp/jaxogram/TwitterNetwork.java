/*
* $Id: $
*
* (C) Copyright 2013 Jaxo Inc.  All rights reserved.
* This work contains confidential trade secrets of Jaxo Inc.
* Use, examination, copying, transfer and disclosure to others
* are prohibited, except with the express written agreement of Jaxo.
*
* Author:  Pierre G. Richard
* Written: 3/10/2013
*/
package com.jaxo.googapp.jaxogram;

import java.io.ByteArrayInputStream;
import java.net.URL;
import java.util.List;
import java.util.Map;

import net.oauth.OAuth;
import net.oauth.OAuthAccessor;
import net.oauth.OAuthMessage;
import net.oauth.http.HttpMessage;
import net.oauth.http.HttpResponseMessage;
import net.oauth.http.MultipartEntity;

import org.apache.commons.io.IOUtils;
//*/ import java.util.logging.Logger;

/*-- class TwitterNetwork --+
*//**
*
* @author  Pierre G. Richard
* @version $Id: $
*/
public class TwitterNetwork extends OAuthorizer implements Network
{
   //*/ private static Logger logger = Logger.getLogger("com.jaxo.googapp.jaxogram.TwitterNetwork");
   private static final String CONSUMER_KEY = "taYsxDzNvPrx6tFemKe6Zw";
   private static final String CONSUMER_SECRET = "0SW9ZzuyGXOB4ienpBCcWyd0r0V2zOAOQRgVLjdYww";
   private static final String OAUTH_REQUEST_URL = "https://api.twitter.com/oauth/request_token";
   private static final String OAUTH_AUTHORIZATION_URL = "https://api.twitter.com/oauth/authorize";
   private static final String OAUTH_ACCESS_URL = "https://api.twitter.com/oauth/access_token";
   private static final String UPLOAD_URL = "https://api.twitter.com/1.1/statuses/update_with_media.json";
   private static final String OLD_UPLOAD_URL = "https://upload.twitter.com/1/statuses/update_with_media.json";


   /*----------------------------------------------------------TwitterNetwork-+
   *//**
   *//*
   +-------------------------------------------------------------------------*/
   public TwitterNetwork() throws Exception {
      super(
         CONSUMER_KEY,
         CONSUMER_SECRET,
         OAUTH_REQUEST_URL,
         OAUTH_AUTHORIZATION_URL,
         OAUTH_ACCESS_URL
      );
   }

   /*----------------------------------------------------------TwitterNetwork-+
   *//**
   *//*
   +-------------------------------------------------------------------------*/
   public TwitterNetwork(String accessPass) throws Exception {
      this();
      String[] p = accessPass.split(" ");
      if (p.length != 2) {
         throw new Exception(
            "Access pass does not have correct format (token and secret)"
         );
      }
      accessor.accessToken = p[0];
      accessor.tokenSecret = p[1];
   }

   /*----------------------------------------------------------requestAuthURL-+
   *//**
   *//*
   +-------------------------------------------------------------------------*/
   public String requestAuthURL(String callbackUrl) throws Exception
   {
      List<OAuth.Parameter> callback = OAuth.newList(
         OAuth.OAUTH_CALLBACK, callbackUrl
      );
      OAuthMessage response = client.getRequestTokenResponse(
         accessor, null, callback
      );
      String authorizationURL = OAuth.addParameters(
         accessor.consumer.serviceProvider.userAuthorizationURL,
         OAuth.OAUTH_TOKEN, accessor.requestToken
      );
      if (response.getParameter(OAuth.OAUTH_CALLBACK_CONFIRMED) == null) {
         authorizationURL = OAuth.addParameters(authorizationURL, callback);
      }else {
         authorizationURL = OAuth.addParameters(
            accessor.consumer.serviceProvider.userAuthorizationURL,
            OAuth.OAUTH_TOKEN, accessor.requestToken
         );
      }
      return authorizationURL;
   }


   /*------------------------------------------------------------authenticate-+
   *//**
   *//*
   +-------------------------------------------------------------------------*/
   public String[] authenticate(String verifier, OAuthAccessor givenAccessor)
   throws Exception
   {
      OAuthMessage response = client.getAccessToken(
         givenAccessor, null,
         OAuth.newList(OAuth.OAUTH_VERIFIER, verifier)
      );
      return new String[] {
         givenAccessor.accessToken + " " + givenAccessor.tokenSecret,
         response.getParameter("screen_name")
      };
   }

   /*-------------------------------------------------------------whoIsAsJson-+
   *//**
   *//*
   +-------------------------------------------------------------------------*/
   public String whoIsAsJson(String id) throws Exception {
      // TODO
      StringBuilder sb = new StringBuilder();
      sb.append("{\"name\":{\"givenName\":\"").
      append("Not Yet Implemented").
      append("\",\"familyName\":\" \"},").
//    append("\"birthday\":\"\",").
      append("\"gender\":\"\"}");
      return sb.toString();
   }

   /*--------------------------------------------------------listAlbumsAsJson-+
   *//**
   * @see
   *//*
   +-------------------------------------------------------------------------*/
   public String listAlbumsAsJson() throws Exception {
      // TODO
      StringBuilder sb = new StringBuilder();
      sb.append("[{\"id\":\"").
      append("15799571").
      append("\",\"title\":\"").append("Dummy").
      append("\",\"description\":\"").
      append("Unused for Twitter").
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
   *//**
   *//*
   +-------------------------------------------------------------------------*/
   public String uploadPhoto(
      String albumId,
      String title,
      byte[] image,
      String type // png, gif, or jpg.  see: ~/fxos/orkut/java/src/com/google/orkut/client/api/UploadPhotoTx.java
   )
   throws Exception
   {
      MultipartEntity entity = new MultipartEntity();
      entity.addField(
         "status", "This picture was sent by Jaxogram", "ISO-8859-1"
      );
      entity.addStream(
         "media[]", "tmpfile.jpg", "image/jpeg", new ByteArrayInputStream(image)
      );
      HttpMessage httpRequest = new HttpMessage(
         "POST", new URL(UPLOAD_URL), entity.getBody()
      );
      List<Map.Entry<String, String>> headers = httpRequest.headers;
      headers.add(new OAuth.Parameter("Authorization", getAuthHeader(UPLOAD_URL)));
      headers.add(new OAuth.Parameter("Content-Type", entity.getContentType()));
      // headers.add(new OAuth.Parameter(CONTENT_LENGTH, Integer.toString(form.length)));

      HttpResponseMessage httpResponse = client.getHttpClient().execute(
         httpRequest,
         client.getHttpParameters()
      );
      return IOUtils.toString(
         httpResponse.getBody(),
         httpResponse.getContentCharset()
      );
      // Example of JSON responses
      // {"request":
      //    "\/1\/statuses\/update_with_media.json",
      //    "error":"Missing or invalid url parameter."
      // }
      // {"errors":
      //    [
      //       {
      //          "message": "Bad Authentication data",
      //          "code":215
      //        }
      //    ]
      // }
   }
}
/*===========================================================================*/

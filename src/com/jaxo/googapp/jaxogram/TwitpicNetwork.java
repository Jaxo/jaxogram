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

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

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
   private static final String CONSUMER_KEY = "1GuuuKkVr2VqZrQRJJ3pkw";
   private static final String CONSUMER_SECRET = "aWyTrSFTXYBb2ddL67tdb1i8xRGJnNF67c3LkGadFs";
   private static final String OAUTH_REQUEST_URL = "https://api.twitter.com/oauth/request_token";
   private static final String OAUTH_AUTHORIZATION_URL = "https://api.twitter.com/oauth/authorize";
   private static final String OAUTH_ACCESS_URL = "https://api.twitter.com/oauth/access_token";

   private static final String API_KEY = "7348580b4263e5703fc3cb97e7ba3283"; // TWitPic
   private static final String UPLOAD_URL = "http://api.twitpic.com/2/upload.json";
   private static final String CREDENTIALS_URL = "https://api.twitter.com/1/account/verify_credentials.json";
   private static final String REALM_URL = "http://api.twitter.com";

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
      List<OAuth.Parameter> headers = new ArrayList<OAuth.Parameter>();
      MultipartEntity entity = new MultipartEntity();
      entity.addField("key", API_KEY, "UTF-8");
      entity.addField("message", title, "UTF-8");
      entity.addFile("media", "tmpfile." + type, "image/" + type, image);
      headers.add(
         new OAuth.Parameter(
            "X-Verify-Credentials-Authorization",
            accessor.newRequestMessage(
               OAuthMessage.GET, CREDENTIALS_URL, null, null
            ).getAuthorizationHeader(REALM_URL)
         )
      );
      headers.add(
         new OAuth.Parameter(
            "X-Auth-Service-Provider",
            CREDENTIALS_URL
         )
      );
      headers.add(
         new OAuth.Parameter(
            net.oauth.http.HttpMessage.CONTENT_TYPE,
            entity.getContentType()
         )
      );
      HttpMessage httpRequest = new HttpMessage(
         OAuthMessage.POST,
         new URL(UPLOAD_URL),
         entity.getBody()
      );
      httpRequest.headers.addAll(headers);
      HttpResponseMessage response = client.getHttpClient().execute(
         httpRequest,
         client.getHttpParameters()
      );
      return IOUtils.toString(response.getBody(), response.getContentCharset());
      // Example of JSON responses
      // {
      //    "id":"cag6rj",
      //    "text":"Jaxogram Test",
      //    "url":"http:\/\/twitpic.com\/cag6rj",
      //    "width":100,
      //    "height":100,
      //    "size":5045,
      //    "type":"png",
      //    "timestamp":"Mon, 11 Mar 2013 11:10:16 +0000",
      //    "user":{
      //       "id":3186471,
      //       "screen_name": "jaxosystems"
      //    }
      // }
      // { "errors":
      //    [
      //       {
      //         "code":401,
      //         "message":"Could not authenticate you (invalid application key)."
      //       }
      //    ]
      // }
   }
}
/*===========================================================================*/

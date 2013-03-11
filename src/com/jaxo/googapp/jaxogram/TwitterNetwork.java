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

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import net.oauth.OAuth;
import net.oauth.OAuthMessage;
import net.oauth.http.HttpMessage;
import net.oauth.http.HttpResponseMessage;
import net.oauth.http.MultipartEntity;


// import com.google.orkut.client.api.DefaultOrkutAdapter;
// import com.google.orkut.client.api.OrkutAdapterDebugListener;

/*-- class TwitterNetwork --+
*//**
*
* @author  Pierre G. Richard
* @version $Id: $
*/
public class TwitterNetwork extends TwitterAdapter implements Network
{
   static final boolean IS_DEBUG = true;
   static Logger logger = Logger.getLogger("com.jaxo.googapp.jaxogram.TwitterNetwork");
   private static final String consumerKey = "1GuuuKkVr2VqZrQRJJ3pkw";
   private static final String consumerSecret = "aWyTrSFTXYBb2ddL67tdb1i8xRGJnNF67c3LkGadFs";
   private static final String UPLOAD_URL = "http://api.twitpic.com/2/upload.json";
   private static final String CREDENTIALS_URL = "https://api.twitter.com/1/account/verify_credentials.json";
   private static final String REALM_URL = "http://api.twitter.com";
   /*----------------------------------------------------------TwitterNetwork-+
   *//**
   *//*
   +-------------------------------------------------------------------------*/
   public TwitterNetwork() throws Exception {
      super(consumerKey, consumerSecret);
   }

   /*----------------------------------------------------------TwitterNetwork-+
   *//**
   *//*
   +-------------------------------------------------------------------------*/
   public TwitterNetwork(String accessPass) throws Exception {
      super(consumerKey, consumerSecret);
      setAccessPass(accessPass);
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
      OAuthMessage request = accessor.newRequestMessage(
         OAuthMessage.GET, CREDENTIALS_URL, null, null

      );
      List<OAuth.Parameter> headers;
      MultipartEntity entity = new MultipartEntity();
      entity.addField("key", "7348580b4263e5703fc3cb97e7ba3283", "UTF-8"); // from.getBodyEncoding()
      entity.addField("message", "Jaxogram Test", "UTF-8");
      entity.addFile("media", "tmpfile.jpg", "image/jpeg", image);
      headers = new ArrayList<OAuth.Parameter>();
      headers.add(
         new OAuth.Parameter(
            "X-Verify-Credentials-Authorization",
            request.getAuthorizationHeader(REALM_URL)
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

      // return response.readBodyAsString();
      String foo = toString(
         response.getBody(), response.getContentCharset()
      );
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
      return foo;
   }

   /*----------------------------------------------------------------toString-+
   *//**
   *//*
   +-------------------------------------------------------------------------*/
   public static String toString(InputStream in, String encoding)
   throws IOException
   {
      if (in == null) {
         return null;
      }else {
         try {
            StringBuilder sb = new StringBuilder();
            InputStreamReader r = new InputStreamReader(in, encoding);
            char[] s = new char[512];
            for (int n; (n = r.read(s)) > 0; sb.append(s, 0, n));
            return sb.toString();
         }finally {
             in.close();
         }
      }
   }

   /*---------------------------------------------------------------------say-+
   *//**
   *//*
   +-------------------------------------------------------------------------*/
   protected void say(String s) {
      if (IS_DEBUG) logger.info(s);
   }
}
/*===========================================================================*/

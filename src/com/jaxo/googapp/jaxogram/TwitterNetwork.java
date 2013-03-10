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
import java.util.ArrayList;
import java.util.Map;
import java.util.logging.Logger;

import net.oauth.OAuth;
import net.oauth.OAuthMessage;
import net.oauth.client.OAuthClient;

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
      consumer.setProperty(
         OAuthClient.PARAMETER_STYLE,
         net.oauth.ParameterStyle.FLICKR_PHOTO_UPLOAD
      );
      ArrayList<Map.Entry<String, String>> params;
      params = new ArrayList<Map.Entry<String, String>>();
      params.add(new OAuth.Parameter("title", title));
      params.add(new OAuth.Parameter("description", "Jaxogram")); // FIXME
      OAuthMessage response = client.invoke(
         accessor,
         OAuthMessage.POST,
         UPLOAD_URL,
         params,
         new ByteArrayInputStream(image)
      );
      String foo = response.readBodyAsString();
      // Example of JSON response
      // {
      //    "id" : "1lad07",
      //    "text" : "test",
      //    "url" : "http:\/\/twitpic.com\/1lacuz",
      //    "width" : 220,
      //    "height" : 84,
      //    "size" : 8722,
      //    "type" : "png",
      //    "timestamp" : "Wed, 05 May 2010 16:11:48 +0000",
      //    "user" : {
      //       "id" : 12345,
      //       "screen_name" : "twitpicuser"
      //    }
      // }
      return foo;
      // return response.readBodyAsString();
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

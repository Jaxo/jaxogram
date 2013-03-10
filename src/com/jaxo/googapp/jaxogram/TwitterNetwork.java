/*
* $Id: $
*
* (C) Copyright 2013 Jaxo Inc.  All rights reserved.
* This work contains confidential trade secrets of Jaxo Inc.
* Use, examination, copying, transfer and disclosure to others
* are prohibited, except with the express written agreement of Jaxo.
*
* Author:  Pierre G. Richard
* Written: 1/6/2013
*/
package com.jaxo.googapp.jaxogram;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Map;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
   private static final String UPLOAD_URL = "http://api.twitter.com/services/upload";

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
      append("234565432").
      append("\",\"title\":\"").append("Dummy").
      append("\",\"description\":\"").
      append("Unused for FlickR").
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
//    Collection<String> tags = ...;
//    if (tags != null) {
//       params.add(new OAuth.Parameter("tags", StringUtilities.join(tags, " ")));
//    }
      params.add(new OAuth.Parameter("is_public", "0"));
      params.add(new OAuth.Parameter("is_family", "1"));
      params.add(new OAuth.Parameter("is_friend", "1"));
//    params.add(new OAuth.Parameter("safety_level", "1" : "2" : "3"));
      params.add(new OAuth.Parameter("content_type", "1"));
//    params.add(new OAuth.Parameter("hidden", foo? "1" : "2"));
      OAuthMessage response = client.invoke(
         accessor,
         OAuthMessage.POST,
         UPLOAD_URL,
         params,
         new ByteArrayInputStream(image)
      );
      return extractPayload(response.getBodyAsStream());
   }

   /*----------------------------------------------------------extractPayload-+
   *//**
   * Kind of simplimistic XML Parser.
   *//*
   +-------------------------------------------------------------------------*/
   static final Pattern status = Pattern.compile(".*\\<rsp.*stat=\"(.*?)\".*?>\\s*(.*?)\\</rsp>", Pattern.DOTALL);
   static final Pattern errCode = Pattern.compile(".*\\<err.*code=\"(.*?)\"", Pattern.DOTALL);
   static final Pattern errMsg = Pattern.compile(".*\\<err.*msg=\"(.*?)\"", Pattern.DOTALL);
   static String extractPayload(InputStream in) throws Exception
   {
      Matcher matcher;
      String statusVal = "fail";  // "ok" or "fail"
      String errCodeVal = "?";
      String errMsgVal = "?";
      String payload = "";
      StringBuilder sb = new StringBuilder();
      int ch;
      while ((ch = in.read()) != -1) sb.append((char)ch);
      matcher = status.matcher(sb.toString());
      if (matcher.lookingAt() && (matcher.groupCount() > 0)) {
         statusVal = matcher.group(1);
         if (matcher.groupCount() > 1) {
            payload = matcher.group(2);
            if (!statusVal.equals("ok")) {  // assume fail
               matcher = errCode.matcher(payload);
               if (matcher.lookingAt() && (matcher.groupCount() > 0)) {
                  errCodeVal = matcher.group(1);
               }
               matcher = errMsg.matcher(payload);
               if (matcher.lookingAt() && (matcher.groupCount() > 0)) {
                  errMsgVal = matcher.group(1);
               }
            }
         }
      }
      if (!statusVal.equals("ok")) {  // assume fail
         throw new Exception("RC=" + errCodeVal + " (" + errMsgVal + ")");
      }
      return payload.trim();
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

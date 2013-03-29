/*
* $Id: $
*
* (C) Copyright 2013 Jaxo Inc.  All rights reserved.
* This work contains confidential trade secrets of Jaxo Inc.
* Use, examination, copying, transfer and disclosure to others
* are prohibited, except with the express written agreement of Jaxo.
*
* Author:  Pierre G. Richard
* Written: 3/8/2013
*/
package com.jaxo.googapp.jaxogram;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.oauth.OAuth;
import net.oauth.OAuthAccessor;
import net.oauth.OAuthMessage;
import net.oauth.client.OAuthClient;
//*/ import java.util.logging.Logger;

// import com.google.orkut.client.api.DefaultOrkutAdapter;
// import com.google.orkut.client.api.OrkutAdapterDebugListener;

/*-- class FlickrNetwork --+
*//**
*
* @author  Pierre G. Richard
* @version $Id: $
*/
public class FlickrNetwork extends OAuthorizer implements Network
{
   //*/ static Logger logger = Logger.getLogger("com.jaxo.googapp.jaxogram.FlickrNetwork");
   private static final String CONSUMER_KEY = "10ffe23ba7c418592998e1d17a34ec59";
   private static final String CONSUMER_SECRET = "9c3a11c7aeb7fd00";
   private static final String OAUTH_REQUEST_URL = "http://www.flickr.com/services/oauth/request_token";
   private static final String OAUTH_AUTHORIZATION_URL = "http://www.flickr.com/services/oauth/authorize";
   private static final String OAUTH_ACCESS_URL = "http://www.flickr.com/services/oauth/access_token";

   private static final String UPLOAD_URL = "http://api.flickr.com/services/upload";
   private static final String SERVER_URL = "http://api.flickr.com/services/rest";

   /*-----------------------------------------------------------FlickrNetwork-+
   *//*
   *//*
   +-------------------------------------------------------------------------*/
   public FlickrNetwork() throws Exception {
      super(
         CONSUMER_KEY,
         CONSUMER_SECRET,
         OAUTH_REQUEST_URL,
         OAUTH_AUTHORIZATION_URL,
         OAUTH_ACCESS_URL
      );
   }

   /*-----------------------------------------------------------FlickrNetwork-+
   *//**
   *//*
   +-------------------------------------------------------------------------*/
   public FlickrNetwork(String accessPass) throws Exception {
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
   public String authenticate(String verifier, OAuthAccessor givenAccessor)
   throws Exception
   {
      OAuthMessage response = client.getAccessToken(
         givenAccessor, null,
         OAuth.newList(OAuth.OAUTH_VERIFIER, verifier)
      );
      return (
         new StringBuilder().
         append("{ \"accessPass\":\"").
         append(givenAccessor.accessToken).
         append(' ').
         append(givenAccessor.tokenSecret).
         append("\", \"userName\":\"").
         append(response.getParameter("fullname")).
         append("\" }")
      ).toString();
   }

   /*------------------------------------------------------------------fooBar-+
   *//**
   *//*
   +-------------------------------------------------------------------------*/
   public void fooBar() throws Exception {
      consumer.setProperty(
         OAuthClient.PARAMETER_STYLE,
         net.oauth.ParameterStyle.QUERY_STRING
      );
      // ArrayList params = new ArrayList();
      ArrayList<Map.Entry<String, String>> params;
      params = new ArrayList<Map.Entry<String, String>>();
      params.add(new OAuth.Parameter("format", "json"));
      params.add(new OAuth.Parameter("method", "flickr.test.login"));
      params.add(new OAuth.Parameter("nojsoncallback", "1"));
      OAuthMessage response = client.invoke(accessor, SERVER_URL, params);
      if (response == null) {
         throw new Exception("Who cares?");
      }
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
      return extractPayload(response.readBodyAsString());
   }

   /*----------------------------------------------------------extractPayload-+
   *//**
   * Kind of simplimistic XML Parser.
   *//*
   +-------------------------------------------------------------------------*/
   static final Pattern status = Pattern.compile(".*\\<rsp.*stat=\"(.*?)\".*?>\\s*(.*?)\\</rsp>", Pattern.DOTALL);
   static final Pattern errCode = Pattern.compile(".*\\<err.*code=\"(.*?)\"", Pattern.DOTALL);
   static final Pattern errMsg = Pattern.compile(".*\\<err.*msg=\"(.*?)\"", Pattern.DOTALL);
   static String extractPayload(String response) throws Exception
   {
      Matcher matcher;
      String statusVal = "fail";  // "ok" or "fail"
      String errCodeVal = "?";
      String errMsgVal = "?";
      String payload = "";
      matcher = status.matcher(response);
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

   // SIMPLIFIED, getting read of all oauth.client bullshit (invented by Orkut?)
// public String uploadPhoto(
//    String albumId,
//    String title,
//    byte[] image,
//    String type // png, gif, or jpg.  see: ~/fxos/orkut/java/src/com/google/orkut/client/api/UploadPhotoTx.java
// )
// throws Exception
// {
//    ArrayList<Map.Entry<String, String>> params;
//    params = new ArrayList<Map.Entry<String, String>>();
//    OAuthMessage request = accessor.newRequestMessage(
//       OAuthMessage.POST,
//       UPLOAD_URL,
//       params,
//       new ByteArrayInputStream(image)
//    );
//    HttpMessage httpRequest = makeNewRequest(request);
//    HttpResponseMessage httpResponse = client.getHttpClient().execute(
//       httpRequest,
//       client.getHttpParameters()
//    );
//    String foo = IOUtils.toString(
//       httpResponse.getBody(),
//       httpResponse.getContentCharset()
//    );
//    return foo;
//    // Example of JSON responses
//    // {"errors":
//    //    [
//    //       {
//    //          "message": "Bad Authentication data",
//    //          "code":215
//    //        }
//    //    ]
//    // }
// }
//
// // see net.oauth.http.HttpMessage.newRequest()
// public static HttpMessage makeNewRequest(OAuthMessage request) throws Exception
// {
//    List<Map.Entry<String, String>> headers;
//    InputStream body = request.getBodyAsStream();
//    String url = request.URL;
//    headers = new ArrayList<Map.Entry<String, String>>(request.getHeaders());
//    if (body == null) throw new Exception("no BODY");
//    {
//       headers.add(new OAuth.Parameter("Authorization", request.getAuthorizationHeader(null)));
//       // Write the non-OAuth parameters in the body
//       MultipartEntity entity = new MultipartEntity();
//       for (Map.Entry<String, String> parameter : request.getParameters()) {
//          if (!parameter.getKey().startsWith("oauth_")) {
//             entity.addField(
//                parameter.getKey(),   // not OAuth.percentEncode
//                parameter.getValue(),
//                request.getBodyEncoding()
//             );
//          }
//       }
//       entity.addStream("photo", "tmpfile.jpg", "image/jpeg", body);
//       headers.add(new OAuth.Parameter("Content-Type", entity.getContentType()));
//       // headers.add(new OAuth.Parameter(CONTENT_LENGTH, Integer.toString(form.length)));
//       body = entity.getBody();
//    }
//    HttpMessage httpRequest = new HttpMessage(
//       request.method, new URL(url), body
//    );
//    httpRequest.headers.addAll(headers);
//    return httpRequest;
// }
}
/*===========================================================================*/

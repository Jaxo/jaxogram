/*
* $Id: $
*
* (C) Copyright 2013 Jaxo Inc.  All rights reserved.
* This work contains confidential trade secrets of Jaxo Inc.
* Use, examination, copying, transfer and disclosure to others
* are prohibited, except with the express written agreement of Jaxo.
*
* Author:  Pierre G. Richard
* Written: 3/9/2013
*/
package com.jaxo.googapp.jaxogram;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.oauth.OAuth;
import net.oauth.OAuthAccessor;
import net.oauth.OAuthConsumer;
import net.oauth.OAuthMessage;
import net.oauth.OAuthServiceProvider;
import net.oauth.OAuthorizer;
import net.oauth.client.OAuthClient;
import net.oauth.client.jnetclient.JNetClient;

/*-- class FlickrAdapter --+
*//**
*
* @author  Pierre G. Richard
* @version $Id: $
*/
public class FlickrAdapter implements OAuthorizer {
   OAuthConsumer consumer;
   OAuthAccessor accessor;
   OAuthClient client;
   private String requestURL = "";

   private static final String OAUTH_REQUEST_URL = (
      "http://www.flickr.com/services/oauth/request_token"
   );
   private static final String OAUTH_AUTHORIZATION_URL = (
      "http://www.flickr.com/services/oauth/authorize"
   );
   private static final String OAUTH_ACCESS_URL = (
      "http://www.flickr.com/services/oauth/access_token"
   );
   private static final String SERVER_URL = (
      "http://api.flickr.com/services/rest"
   );

   /*-----------------------------------------------------------FlickrAdapter-+
   *//*
   * @param consumerKey Your OAuth consumer key.
   * @param consumerSecret Your OAuth consumer secret.
   *//*
   +-------------------------------------------------------------------------*/
   public FlickrAdapter(String consumerKey, String consumerSecret)
   throws Exception {
      this.requestURL = SERVER_URL;
      say("Init OAuth.");
      say("Consumer key:    " + consumerKey);
      say("Consumer secret: <not shown>");
      say("Request URL is:  " + requestURL);
      try {
         say("Setting up oauth consumer.");
         consumer = new OAuthConsumer(
            null,
            consumerKey,
            consumerSecret,
            new OAuthServiceProvider(
               OAUTH_REQUEST_URL,
               OAUTH_AUTHORIZATION_URL,
               OAUTH_ACCESS_URL
            )
         );
         consumer.setProperty(
            OAuthClient.PARAMETER_STYLE,
            net.oauth.ParameterStyle.QUERY_STRING
         );
         say("Setting up oauth accessor and client.");
         accessor = new OAuthAccessor(consumer);
         client = new OAuthClient(new JNetClient());
      }catch (Exception ex) {
         throw new Exception(
            "OrkutAdapter: Failed to initialize OAuth.", ex
         );
      }
   }

   /*----------------------------------------------------------requestAuthURL-+
   *//**
   *//*
   +-------------------------------------------------------------------------*/
   public String requestAuthURL(String callbackUrl) throws Exception {
      try {
         say("Getting oauth request token.");
         List<OAuth.Parameter> callback = OAuth.newList(
            OAuth.OAUTH_CALLBACK, callbackUrl
         );
         OAuthMessage response = client.getRequestTokenResponse(
            accessor, null, callback
         );
         say("Response obtained.");
         String authorizationURL = OAuth.addParameters(
            accessor.consumer.serviceProvider.userAuthorizationURL,
            OAuth.OAUTH_TOKEN, accessor.requestToken
         );

         if (response.getParameter(OAuth.OAUTH_CALLBACK_CONFIRMED) == null) {
            say("No callback confirm - service provider is using 1.0, not 1.0a.");
            say("Adding callback as bare parameter.");
            authorizationURL = OAuth.addParameters(authorizationURL, callback);
         }else {
            authorizationURL = OAuth.addParameters(
               accessor.consumer.serviceProvider.userAuthorizationURL,
               OAuth.OAUTH_TOKEN, accessor.requestToken
            );
         }
         say("Request token: " + accessor.requestToken);
         say("Authorization URL: " + authorizationURL);
         return authorizationURL;
      }catch (Exception e) {
         e.printStackTrace();
         throw new Exception(
            "Error requesting OAuth authorization URL", e
         );
      }
   }

   /*-------------------------------------------------------------getAccessor-+
   *//**
   *//*
   +-------------------------------------------------------------------------*/
   public OAuthAccessor getAccessor() {
      return accessor;
   }

   /*-----------------------------------------------------------getAccessPass-+
   *//**
   *//*
   +-------------------------------------------------------------------------*/
   public String getAccessPass() {
      return accessor.accessToken + " " + accessor.tokenSecret;
   }

   /*-----------------------------------------------------------setAccessPass-+
   *//**
   *//*
   +-------------------------------------------------------------------------*/
   public void setAccessPass(String accessPass) throws Exception {
      say("Access pass provided: '" + accessPass + "'");
      String[] p = accessPass.split(" ");
      if (p.length != 2) {
         throw new Exception(
            "Access pass does not have correct format (token and secret)"
         );
      }
      say("Literal access token and secret supplied:");
      say("Access token  : " + p[0]);
      say("Token secret  : " + p[1]);
      accessor.accessToken = p[0];
      accessor.tokenSecret = p[1];
   }

   /*------------------------------------------------------------authenticate-+
   *//**
   *//*
   +-------------------------------------------------------------------------*/
   public String[] authenticate(String verifier, OAuthAccessor givenAccessor)
   throws Exception
   {
      say("Verifier and Accessor provided: " + verifier);
      say("Obtaining access token...");
      OAuthMessage response = client.getAccessToken(
         givenAccessor, null,
         OAuth.newList(OAuth.OAUTH_VERIFIER, verifier)
      );
      say("Got access token   : " + givenAccessor.accessToken);
      say("Access token secret: " + givenAccessor.tokenSecret);
      return new String[] {
         givenAccessor.accessToken + " " + givenAccessor.tokenSecret,
         response.getParameter("fullname")
      };
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

   /*---------------------------------------------------------------------say-+
   *//**
   *//*
   +-------------------------------------------------------------------------*/
   protected void say(String s) {}
}
/*===========================================================================*/


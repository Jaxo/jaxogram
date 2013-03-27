/*
* $Id: $
*
* (C) Copyright 2011 Jaxo Inc.  All rights reserved.
* This work contains confidential trade secrets of Jaxo Inc.
* Use, examination, copying, transfer and disclosure to others
* are prohibited, except with the express written agreement of Jaxo.
*
* Author:  Pierre G. Richard
* Written: 3/10/2013
*/
package com.jaxo.googapp.jaxogram;

import net.oauth.OAuthAccessor;
import net.oauth.OAuthConsumer;
import net.oauth.OAuthMessage;
import net.oauth.OAuthServiceProvider;
import net.oauth.client.OAuthClient;
import net.oauth.client.jnetclient.JNetClient;

/*-- class OAuthorizer --+
*//**
*
* @author  Pierre G. Richard
* @version $Id: $
*/
public abstract class OAuthorizer
{
   OAuthConsumer consumer;
   OAuthAccessor accessor;
   OAuthClient client;

   public abstract String requestAuthURL(String callbackUrl) throws Exception;
   public abstract String[] authenticate(String verifier, OAuthAccessor accessor) throws Exception;

   /*-------------------------------------------------------------OAuthorizer-+
   *//**
   *//*
   +-------------------------------------------------------------------------*/
   public OAuthorizer(
      String consumerKey,
      String consumerSecret,
      String oauthRequestUrl,
      String oauthAuthorizationUrl,
      String oauthAccessUrl
   )
   throws Exception
   {
      consumer = new OAuthConsumer(
         null,
         consumerKey,
         consumerSecret,
         new OAuthServiceProvider(
            oauthRequestUrl,
            oauthAuthorizationUrl,
            oauthAccessUrl
         )
      );
      consumer.setProperty(
         OAuthClient.PARAMETER_STYLE,
         net.oauth.ParameterStyle.QUERY_STRING
      );
      accessor = new OAuthAccessor(consumer);
      client = new OAuthClient(new JNetClient());
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

   /*-----------------------------------------------------------getAuthHeader-+
   *//**
   *//*
   +-------------------------------------------------------------------------*/
   public String getAuthHeader(String url) throws Exception
   {
      OAuthMessage msg = new OAuthMessage("POST", url, null, null);
      msg.addRequiredParameters(accessor);
      return msg.getAuthorizationHeader(null);
   }
}

/*===========================================================================*/

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
package net.oauth;

/*-- interface OAuthorizer --+
*//**
*
* @author  Pierre G. Richard
* @version $Id: $
*/
public interface OAuthorizer {
   OAuthAccessor getAccessor();
   String requestAuthURL(String callbackUrl) throws Exception;
   // void setAccessPass(String accessPass) throws Exception;
   String[] authenticate(String verifier, OAuthAccessor accessor) throws Exception;
}

/*===========================================================================*/

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

import java.util.logging.Logger;

// import com.google.orkut.client.api.DefaultOrkutAdapter;
// import com.google.orkut.client.api.OrkutAdapterDebugListener;

/*-- class FlickrNetwork --+
*//**
*
* @author  Pierre G. Richard
* @version $Id: $
*/
// public class FlickrNetwork extends DefaultOrkutAdapter implements Network
public class FlickrNetwork extends FlickrAdapter implements Network
{
   public String listAlbumsAsJson() throws Exception {
      return "???";
   }
   public void createAlbum(String title, String desc) throws Exception {
   }
   public void uploadPhoto(
      String albumId, String title, byte[] image, String type
   ) throws Exception {
   }

   static final boolean IS_DEBUG = true;
   static Logger logger = Logger.getLogger("com.jaxo.googapp.jaxogram.FlickrNetwork");
   static final String consumerKey = "10ffe23ba7c418592998e1d17a34ec59";
   static final String consumerSecret = "9c3a11c7aeb7fd00";

   public FlickrNetwork() throws Exception {
      super(consumerKey, consumerSecret);
   }

   public FlickrNetwork(String accessPass) throws Exception {
      super(consumerKey, consumerSecret);
      setAccessPass(accessPass);
   }

   public String whoIsAsJson(String id) throws Exception {
      return "???";
   }

   protected void say(String s) {
      if (IS_DEBUG) logger.info(s);
   }
}
/*===========================================================================*/

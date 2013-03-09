/*
* $Id: $
*
* (C) Copyright 2011 Jaxo Inc.  All rights reserved.
* This work contains confidential trade secrets of Jaxo Inc.
* Use, examination, copying, transfer and disclosure to others
* are prohibited, except with the express written agreement of Jaxo.
*
* Author:  Pierre G. Richard
* Written: 3/6/2013
*/
package com.jaxo.googapp.jaxogram;

/*-- interface Network --+
*//**
*
* @author  Pierre G. Richard
* @version $Id: $
*/
interface Network {
   String whoIsAsJson(String id) throws Exception;
   String listAlbumsAsJson() throws Exception;
   void createAlbum(String title, String desc) throws Exception;
   void uploadPhoto(
      String albumId, String title, byte[] image, String type
   ) throws Exception;
}
/*===========================================================================*/

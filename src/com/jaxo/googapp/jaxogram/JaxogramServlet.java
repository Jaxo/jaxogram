/*
* $Id: $
*
* (C) Copyright 2013 Jaxo Inc.  All rights reserved.
* This work contains confidential trade secrets of Jaxo Inc.
* Use, examination, copying, transfer and disclosure to others
* are prohibited, except with the express written agreement of Jaxo.
*
* Author:  Pierre G. Richard
* Written: 1/4/2013
*/
package com.jaxo.googapp.jaxogram;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Enumeration;
//*/ import java.util.logging.Level;
//*/ import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.util.Streams;
import org.apache.commons.io.IOUtils;

@SuppressWarnings("serial")
/*-- class JaxogramServlet --+
*//**
*
* @author  Pierre G. Richard
* @version $Id: $
*/
public class JaxogramServlet extends HttpServlet
{
   /*-------------------------------------------------------------------doGet-+
   *//**
   *//*
   +-------------------------------------------------------------------------*/
   public void doGet(HttpServletRequest request, HttpServletResponse response)
   throws IOException {
      doPost(request, response);
   }

   /*------------------------------------------------------------------doPost-+
   *//**
   *//*
   +-------------------------------------------------------------------------*/
   public void doPost(HttpServletRequest req, HttpServletResponse resp)
   throws IOException
   {
//*/  Logger logger = Logger.getLogger(
//*/     "com.jaxo.googapp.jaxogram.JaxogramServlet"
//*/  );
      String op = req.getParameter("OP");
//*/  logger.info("OP: " + op);
      resp.setContentType("text/plain");
      PrintWriter writer = resp.getWriter();

      try {
         if (op.equals("whoAmI")) {
            writer.println(new OrkutNetwork().whoAmI());

         }else if (op.equals("listAlbums")) {
            writer.println(new OrkutNetwork().listAlbums());

         }else if (op.startsWith("postImage")) {
            String imgType = "jpg";
            String imgTitle = "(No Title)";
            String albumId = "5830280253747333482";
            byte[] image = null;
            if (op.equals("postImageData")) {
               for (
                  @SuppressWarnings("unchecked")
                  Enumeration<String> names = req.getParameterNames();
                  names.hasMoreElements();
               ) {
                  String name = names.nextElement();
                  String values = req.getParameterValues(name)[0];
                  if (name.equals("IMG")) {
                     imgType = values;
                  }else if (name.equals("TIT")) {
                     imgTitle = values;
                  }else if (name.equals("AID")) {
                     albumId = values;
                  }
               }
               InputStream in = req.getInputStream();
               image = IOUtils.toByteArray(in);
               IOUtils.closeQuietly(in);
            }else { // (op.equals("postImageFile")) {
               ServletFileUpload upload = new ServletFileUpload();
               upload.setSizeMax(500000);
               // upload.setSizeMax(120000);
               FileItemIterator iterator = upload.getItemIterator(req);
               while (iterator.hasNext()) {
                  FileItemStream item = iterator.next();
                  if (item.isFormField()) {
                     String name = item.getFieldName();
                     String values = Streams.asString(item.openStream());
                     if (name.equals("IMG")) {
                        imgType = values;
                     }else if (name.equals("TIT")) {
                        imgTitle = values;
                     }else if (name.equals("AID")) {
                        albumId = values;
                     }
                  }else {
                     InputStream in = item.openStream();
                     image = IOUtils.toByteArray(in);
                     IOUtils.closeQuietly(in);
                  }
               }
            }
            if (image != null) {
               OrkutNetwork orknet = new OrkutNetwork();
               orknet.uploadPhoto(albumId, imgTitle, image, imgType);
               writer.println("Successfully uploaded to album #" + albumId);
            }else {
               resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
               writer.println("Error:\nImage data not found");
            }
         }
      }catch (Exception e) {
         resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
         e.printStackTrace();
         writer.println("Error:\n" + e.getMessage());
      }
   }

// static class LogFormatter extends java.util.logging.Formatter {
//    public String format(LogRecord rec) {
//       StringBuilder sb = new StringBuilder();
//    }
// }

}
/*===========================================================================*/

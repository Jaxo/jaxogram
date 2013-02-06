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
import java.net.URLEncoder;
import java.util.Enumeration;
//*/ import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.oauth.OAuthAccessor;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.util.Streams;
import org.apache.commons.io.IOUtils;

import com.google.appengine.api.memcache.Expiration;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;

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
         if (op.equals("backCall")) {
            /*
            | Call back from Orkut:
            | regular WEB app:
            |    pass the oauth_verifier back to origin (redirect to the referer page)
            | packaged app
            |    store the verifier in device local storage
            |    return an iframe contents
            */
            String referer = req.getParameter("referer");
            if (referer != null) {
               String redirect = (
                  req.getParameter("referer") +
                  "?OP=backCall" +
                  "&verifier=" +
                  URLEncoder.encode(req.getParameter("oauth_verifier"), "UTF-8")
               );
//*/           logger.info("Callback from orkut => proxy to " + redirect);
               resp.setStatus(HttpServletResponse.SC_SEE_OTHER);
               resp.setHeader("Location", redirect);
            }else {
               MemcacheService memcache = MemcacheServiceFactory.getMemcacheService();
               String memKey = req.getParameter("JXK");
               memcache.put(
                  memKey,
                  URLEncoder.encode(req.getParameter("oauth_verifier"), "UTF-8"),
                  Expiration.byDeltaSeconds(1200)  // TODO 300 is enough
               );
               resp.setStatus(HttpServletResponse.SC_CREATED);
            }
         }else if (op.equals("backCallTest")) {
            MemcacheService memcache = MemcacheServiceFactory.getMemcacheService();
            String memKey = req.getParameter("JXK");
            memcache.put(
               memKey,
               URLEncoder.encode(req.getParameter("oauth_verifier"), "UTF-8"),
               Expiration.byDeltaSeconds(1200)  // TODO 300 is enough
            );
            resp.setContentType("text/html");
            writer.print(
               backCallTestResponse(
                  URLEncoder.encode(req.getParameter("oauth_verifier"), "UTF-8")
               )
            );
         }else {  // Cross Origin Resource Sharing
            if (req.getHeader("origin") != null) {
               resp.setHeader("Access-Control-Allow-Origin", req.getHeader("origin"));
//          }else {
//             throw new Exception("Invalid CORS header (no origin)");
            }
            // need cookies for session id's:
            resp.setHeader("Access-Control-Allow-Credentials", "true");
            // resp.setHeader("Access-Control-Expose-Headers", "FooBar");

            if (op.equals("getVerifier")) {
               MemcacheService memcache = MemcacheServiceFactory.getMemcacheService();
               String memKey = req.getParameter("JXK");
               String val = (String)memcache.get(memKey);
               if (val == null) {
                  val = "???";
               }else {
                  memcache.delete(memKey);
               }
               writer.print(val);

            }else if (op.equals("postAccPss")) {
               InputStream in = req.getInputStream();
               req.getSession(true).setAttribute("accesspass", IOUtils.toString(in));
//*/           logger.info("Access Pass: " + req.getSession(true).getAttribute("accesspass"));
               IOUtils.closeQuietly(in);

            }else if (op.equals("getUrl")) {
               HttpSession session = req.getSession(true);
               OrkutNetwork orknet = makeNullOrkutNetwork(req);
               String authorizeUrl = orknet.requestAuthURL();
               session.setAttribute("accessor", orknet.getAccessor());
               writer.print(authorizeUrl);

            }else if (op.equals("getAccPss")) {
               HttpSession session = req.getSession(true);
               OrkutNetwork orknet = makeNullOrkutNetwork(req);
               String ap =  orknet.authenticate(
                  req.getParameter("verifier"),
                  (OAuthAccessor)session.getAttribute("accessor")
               );
               session.setAttribute("accesspass", ap);
               writer.printf(
                  "{ \"accessPass\":\"%s\", \"userName\":\"%s\" }",    // JSON
                  URLEncoder.encode(ap, "UTF-8").replace("+", "%20"),  // arghhhh
                  URLEncoder.encode(makeOrkutNetwork(req).whoAmI(), "UTF-8").
                  replace("+", "%20")
               );

            }else if (op.equals("whoAmI")) {
               writer.println(makeOrkutNetwork(req).whoIsAsJson(null));

            }else if (op.equals("listAlbums")) {
               writer.println(makeOrkutNetwork(req).listAlbumsAsJson());

            }else if (op.equals("createAlbum")) {
               String title = req.getParameter("title");
               String descr = req.getParameter("descr");
               writer.println(
                  makeOrkutNetwork(req).createAlbumAsJson(
                     (title == null)? "No title" : title,
                     (descr == null)? "" : descr
                  )
               );
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
               if (image == null) {
                  throw new Exception("Image data not found");
               }
               makeOrkutNetwork(req).uploadPhoto(
                  albumId, imgTitle, image, imgType
               );
//*/           writer.println("Successfully uploaded to album #" + albumId);
            }
         }
      }catch (Exception e) {
         resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
//*/     e.printStackTrace();
         writer.print(e.getMessage());
      }
   }

   /*--------------------------------------------------------makeOrkutNetwork-+
   *//**
   *//*
   +-------------------------------------------------------------------------*/
   public static OrkutNetwork makeOrkutNetwork(HttpServletRequest req)
   throws Exception {
      HttpSession session = req.getSession(true);
      return new OrkutNetwork(
         (String)session.getAttribute("accesspass"),
         getBaseUrl(req) + "/jaxogram?OP=backCall"    // callback URL
      );
   }

   /*----------------------------------------------------makeNullOrkutNetwork-+
   *//**
   *//*
   +-------------------------------------------------------------------------*/
   public static OrkutNetwork makeNullOrkutNetwork(HttpServletRequest req)
   throws Exception {
      String referer = req.getHeader("referer");
      if (referer != null) {
         referer = "&referer=" +  URLEncoder.encode(referer, "UTF-8");
      }else if ((referer = req.getParameter("JXK")) != null) {
         referer = "&JXK=" + referer;
      }else {
         referer = "";
//       throw new Exception("Invalid CORS header (null referer)");
      }
      return new OrkutNetwork(
         null,                                        // no access password
         getBaseUrl(req) + "/jaxogram?OP=backCall" +  // callback URL
         referer
      );
   }

   /*--------------------------------------------------------------getBaseUrl-+
   *//**
   *//*
   +-------------------------------------------------------------------------*/
   public static String getBaseUrl(HttpServletRequest req) {
      int port = req.getServerPort();
      StringBuilder sb = new StringBuilder(50);
      sb.append(req.getScheme()).append("://").append(req.getServerName());
      if ((port != 80) && (port != 443)) sb.append(':').append(port);
      return sb.toString();
   }

   private static String backCallTestResponse(String verifier) {
      String f = (
         "<HTML><HEAD>" +
         "\n<SCRIPT type='text/javascript'>" +
         "\nfunction setVerifier() {" +
         "\n  document.getElementById('vrf').innerHTML = (" +
            "\n'<SMALL>" + verifier + "</SMALL>'" +
         "\n  );" +
         "\n}" +
         "\n</SCRIPT></HEAD>" +
         "\n<BODY><H1>Back Call Test</H1>" +
         "\n<BUTTON onclick='setVerifier();'>Set Verifier</BUTTON>" +
         "\n<DIV id='vrf'>&nbsp;</DIV>" +
         "\n</BODY></HTML>"
      );
      return f;
   }
}
/*===========================================================================*/

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
//*/ import java.util.logging.Logger;

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
//*/  logger.info("OP:" + op);

      resp.setContentType("text/plain");
      PrintWriter writer = resp.getWriter();

      try {
         if (op.equals("backCall")) {
            /*
            | Call back from Orkut, Twitter, etc...
            | regular WEB app:
            |    pass the oauth_verifier back to origin (redirect to the referer page)
            | packaged app
            |    store the verifier in device local storage
            |    return an iframe contents
            */
            String referer = req.getParameter("referer");
            String net = req.getParameter("NET");
            String verifier = req.getParameter("oauth_verifier");
            if (verifier == null) { // Facebook!
               verifier = req.getParameter("code");
            }
            if (referer != null) {
               String redirect = (
                  req.getParameter("referer") +
                  "?OP=backCall" + "&NET=" + net +
                  "&VRF=" + URLEncoder.encode(verifier, "UTF-8")
               );
//*/           logger.info("Callback from orkut => proxy to " + redirect);
               resp.setStatus(HttpServletResponse.SC_SEE_OTHER);
               resp.setHeader("Location", redirect);
            }else {
               MemcacheService memcache = MemcacheServiceFactory.getMemcacheService();
               String memKey = req.getParameter("JXK");
               memcache.put(
                  memKey,
                  "{\"VRF\":\"" + verifier + "\", \"NET\":\"" + net + "\"}",
                  Expiration.byDeltaSeconds(300) // 5 minutes
               );
               resp.setStatus(HttpServletResponse.SC_CREATED);
            }
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
               // issued repeatedly by packaged application
               MemcacheService cache = MemcacheServiceFactory.getMemcacheService();
               String key = req.getParameter("JXK");
               String val;
               for (int i=0; ((val=(String)cache.get(key))==null)&&(i < 20); ++i) {
                  try { Thread.sleep(1000); }catch (InterruptedException e) {}
               }
               if (val == null) {
                  val = "???";
               }else {
                  cache.delete(key);
               }
               writer.print(val); // { "VRF":"(verifier)", "NET":"(facebook)" }

            }else if (op.equals("postAccPss")) {
               InputStream in = req.getInputStream();
               req.getSession(true).setAttribute("accesspass", IOUtils.toString(in));
//*/           logger.info("Access Pass: " + req.getSession(true).getAttribute("accesspass"));
               IOUtils.closeQuietly(in);

            }else if (op.equals("checkAccPss")) {
               InputStream in = req.getInputStream();
               String loginPasswd = IOUtils.toString(in);
               IOUtils.closeQuietly(in);
               writer.print(new PicasaNetwork(loginPasswd).whoAmI());
               req.getSession(true).setAttribute("accesspass", loginPasswd);

            }else if (op.equals("getUrl")) {
               HttpSession session = req.getSession(true);
               String net = req.getParameter("NET");
               String authorizeUrl;
               String referer = req.getHeader("referer");
               if (referer != null) {
                  referer = "&referer=" +  URLEncoder.encode(referer, "UTF-8");
               }else if ((referer = req.getParameter("JXK")) != null) {
                  referer = "&JXK=" + referer;
               }else {
                  referer = "";
         //       throw new Exception("Invalid CORS header (null referer)");
               }
               String callbackUrl = (
                  getBaseUrl(req) +  // callback URL
                  "/jaxogram?OP=backCall&NET=" + net + referer
               );
               OAuthorizer authorizer = makeAuthorizer(net);
               authorizeUrl = authorizer.requestAuthURL(callbackUrl);
               session.setAttribute("accessor", authorizer.getAccessor());
               writer.print(authorizeUrl);

            }else if (op.equals("getAccPss")) {
               HttpSession session = req.getSession(true);
               OAuthorizer authorizer = makeAuthorizer(req.getParameter("NET"));
               String[] vals = authorizer.authenticate(
                  req.getParameter("VRF"),
                  (OAuthAccessor)session.getAttribute("accessor")
               );
               String accessPass = vals[0];
               String userName = vals[1];
               session.setAttribute("accesspass", accessPass);
               writer.printf(
                  "{ \"accessPass\":\"%s\", \"userName\":\"%s\" }",    // JSON
                  URLEncoder.encode(accessPass, "UTF-8").replace("+", "%20"),  // arghhhh
                  URLEncoder.encode(userName, "UTF-8").replace("+", "%20")
               );

            }else {
               Network network = makeNetwork(
                  req.getParameter("NET"),
                  (String)req.getSession(true).getAttribute("accesspass")
               );
               if (op.equals("whoAmI")) {
                  writer.println(network.whoIsAsJson(null));

               }else if (op.equals("listAlbums")) {
                  writer.println(network.listAlbumsAsJson());

               }else if (op.equals("createAlbum")) {
                  String title = req.getParameter("title");
                  String descr = req.getParameter("descr");
                  network.createAlbum(
                     (title == null)? "No title" : title,
                     (descr == null)? "" : descr
                  );
                  writer.println(network.listAlbumsAsJson());

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
                  network.uploadPhoto(albumId, imgTitle, image, imgType);
//*/              writer.println("Successfully uploaded to album #" + albumId);
               }
            }
         }
      }catch (Exception e) {
         resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
//*/     e.printStackTrace();
         writer.print(e.getMessage());
      }
   }

   /*-------------------------------------------------------------makeNetwork-+
   *//**
   *//*
   +-------------------------------------------------------------------------*/
   public static Network makeNetwork(String net, String accessPass)
   throws Exception
   {
      if (net.equals("facebook")) {
         return new FacebookNetwork(accessPass);
      }else if (net.equals("twitter")) {
         return new TwitterNetwork(accessPass);
      }else if (net.equals("flickr")) {
         return new FlickrNetwork(accessPass);
      }else if (net.equals("picasa")) {
         return new PicasaNetwork(accessPass);
      }else if (net.equals("orkut")){
         return new OrkutNetwork(accessPass);
      }else {
         throw new Exception("Unknown Network");
      }
   }

   /*----------------------------------------------------------makeAuthorizer-+
   *//**
   *//*
   +-------------------------------------------------------------------------*/
   public static OAuthorizer makeAuthorizer(String net) throws Exception
   {
      if (net.equals("facebook")) {
         return new FacebookNetwork();
      }else if (net.equals("twitter")) {
         return new TwitterNetwork();
      }else if (net.equals("flickr")) {
         return new FlickrNetwork();
      }else if (net.equals("orkut")){
         return new OrkutNetwork();
      }else {
         throw new Exception("Unknown Authorizer");
      }
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
}
/*===========================================================================*/

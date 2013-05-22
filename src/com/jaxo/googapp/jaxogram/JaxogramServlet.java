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

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
/**/ import java.util.logging.Logger;
/**/ import java.util.logging.Level;

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
/**/  Logger logger = Logger.getLogger(
/**/     "com.jaxo.googapp.jaxogram.JaxogramServlet"
/**/  );
      String op = req.getParameter("OP");
      int restVersion = (req.getParameter("V") == null)? 0 : Integer.parseInt(req.getParameter("V"));
//*/  logger.info("OP:" + op);

      resp.setContentType("text/plain");
      PrintWriter writer = resp.getWriter();

      try {
         if (op.equals("backCall")) {
            if (restVersion == 0) {
               /*
               | Call back from Orkut
               | regular WEB app:
               |    pass the oauth_verifier back to origin (redirect to the referer page)
               | packaged app
               |    store the verifier in device local storage
               |    return an iframe contents
               */
               String referer = req.getParameter("referer");
               String net = req.getParameter("NET");
               String verifier = req.getParameter("oauth_verifier");
               if (referer != null) {
                  String redirect = (
                     req.getParameter("referer") +
                     "?OP=backCall" + "&NET=" + net +
                     "&verifier=" + URLEncoder.encode(verifier, "UTF-8")
                  );
//*/              logger.info("Callback from orkut => proxy to " + redirect);
                  resp.setStatus(HttpServletResponse.SC_SEE_OTHER);
                  resp.setHeader("Location", redirect);
               }else {
                  putVerifierInStore(req.getParameter("JXK"), verifier);
                  resp.setStatus(HttpServletResponse.SC_CREATED);
               }
            }else {
               // OAuth callback: store the verifier in appspot storage
               String net = req.getParameter("NET");
               String verifier = req.getParameter("oauth_verifier");
               if (verifier == null) { // Facebook!
                  verifier = req.getParameter("code");
               }
               putVerifierInStore(
                  req.getParameter("JXK"),
                  "{\"VRF\":\"" + verifier + "\", \"NET\":\"" + net + "\"}"
               );
               resp.setStatus(HttpServletResponse.SC_CREATED);
            }
         }else if (op.equals("blob")) {
            String action = req.getParameter("ACT");
            if (action.equals("store")) {
               ServletFileUpload upload = new ServletFileUpload();
               FileItemIterator iterator = upload.getItemIterator(req);
               String contents = null;
               String fileName = null;
               String mimeType = null;
               while (iterator.hasNext()) {
                  FileItemStream item = iterator.next();
                  if (item.isFormField()) {
                     String name = item.getFieldName();
                     String values = Streams.asString(item.openStream());
                     if (name.equals("CNT")) {
                        contents = values;
                     }else if (name.equals("FN")) {
                        fileName = values;
                     }else if (name.equals("MT")) {
                        mimeType = values;
                     }
                  }
               }
               BlobFileSystem.write(contents, fileName, mimeType);
            }else if (action.equals("load")) {
               BlobFileSystem.read(req.getParameter("FN"), resp);
            }else {  // dir
               writer.print(BlobFileSystem.dir(req.getParameter("MT")));
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
               writer.print(getVerifierFromStore(req.getParameter("JXK")));

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
               String net;
               String callbackUrl;
               if (restVersion == 0) {
                  String referer = req.getHeader("referer");
                  if (referer != null) {
                     referer = "&referer=" +  URLEncoder.encode(referer, "UTF-8");
                  }else if ((referer = req.getParameter("JXK")) != null) {
                     referer = "&JXK=" + referer;
                  }else {
                     referer = "";
            //       throw new Exception("Invalid CORS header (null referer)");
                  }
                  net = "orkut";
                  callbackUrl = (
                     getBaseUrl(req) +  // callback URL
                     "/jaxogram?OP=backCall&V=" + restVersion +
                     "&NET=" + net + referer
                  );
               }else {
                  net = req.getParameter("NET");
                  callbackUrl = (
                     getBaseUrl(req) +  // callback URL
                     "/jaxogram?OP=backCall&V=" + restVersion +
                     "&NET=" + net +
                     "&JXK=" + req.getParameter("JXK")
                  );
               }
               OAuthorizer authorizer = makeAuthorizer(net);
               String authorizeUrl = authorizer.requestAuthURL(callbackUrl);
               session.setAttribute("accessor", authorizer.getAccessor());
               writer.print(authorizeUrl);

            }else if (op.equals("getAccPss")) {
               HttpSession session = req.getSession(true);
               OAuthorizer authorizer = makeAuthorizer(
                  (restVersion == 0)? "orkut" : req.getParameter("NET")
               );
               String data = authorizer.authenticate(
                  req.getParameter((restVersion == 0)? "verifier" : "VRF"),
                  (OAuthAccessor)session.getAttribute("accessor")
               );
               session.setAttribute(
                  "accesspass",
                  JsonIterator.get(data, "accessPass")
               );
               writer.printf(data);

            }else if (op.equals("publish")) {
//             resp.setContentType("application/octet-stream");
               resp.setHeader(
                  "Content-Disposition",
                  "attachment; filename=\"filter.jxf\""
               );
               writer.print(req.getParameter("data"));
            }else {
               Network network = makeNetwork(
                  (restVersion == 0)? "orkut" : req.getParameter("NET"),
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
                  String albumId = "";
                  byte[] image = null;
                  if (op.equals("postImageData")) {     // restVersion 0 only
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
                     upload.setSizeMax(1000000);
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
                  writer.println(
                     network.uploadPhoto(albumId, imgTitle, image, imgType)
                  );
//*/              writer.println("Successfully uploaded to album #" + albumId);
               }
            }
         }
      }catch (Exception e) {
         resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
/**/     logger.log(Level.SEVERE, "in \"" + op + "\"\n" + e.toString());
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
      }else if (net.equals("twitpic")) {
         return new TwitpicNetwork(accessPass);
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
      }else if (net.equals("twitpic")) {
         return new TwitpicNetwork();
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

   /*------------------------------------------------------putVerifierInStore-+
   *//**
   *//*
   +-------------------------------------------------------------------------*/
   private void putVerifierInStore(String jxk, String val) {
      Entity verif = new Entity(KeyFactory.createKey("Verif", jxk));
      verif.setProperty("val", val);
      DatastoreServiceFactory.getDatastoreService().put(verif);
   }

   /*----------------------------------------------------getVerifierFromStore-+
   *//**
   *//*
   +-------------------------------------------------------------------------*/
   private String getVerifierFromStore(String jxk) {
      String val = "???";
      Key key = KeyFactory.createKey("Verif", jxk);
      DatastoreService store = DatastoreServiceFactory.getDatastoreService();
      for (int i=0; (i < 20); ++i) {
         try {
            val = store.get(key).getProperty("val").toString();
            store.delete(key);
            break;
         }catch (EntityNotFoundException e2) {
            try { Thread.sleep(1000); }catch (InterruptedException e1) {}
         }
      }
      return val;
   }

   /*------------------------------------------------------putVerifierInCache-+
   *//**
   *//*
   +-------------------------------------------------------------------------*/
// public  void putVerifierInCache(String jxk, String val) {
//    MemcacheServiceFactory.getMemcacheService().put(
//       jxk, val, Expiration.byDeltaSeconds(300) // 5 minutes
//    );
// }

   /*----------------------------------------------------getVerifierFromCache-+
   *//**
   *//*
   +-------------------------------------------------------------------------*/
// public  String getVerifierFromCache(String jxk) {
//    String val = null;
//    MemcacheService cache = MemcacheServiceFactory.getMemcacheService();
//    for (int i=0; ((val=(String)cache.get(jxk))==null) && (i < 20); ++i) {
//       try { Thread.sleep(1000); }catch (InterruptedException e) {}
//    }
//    if (val == null) {
//       return "???";
//    }else {
//       cache.delete(jxk);
//       return val;
//    }
// }
}

/*===========================================================================*/

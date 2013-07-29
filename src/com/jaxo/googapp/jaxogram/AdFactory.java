/*
* $Id: $
*
* (C) Copyright 2013 Jaxo Inc.  All rights reserved.
* This work contains confidential trade secrets of Jaxo Inc.
* Use, examination, copying, transfer and disclosure to others
* are prohibited, except with the express written agreement of Jaxo.
*
* Author:  Pierre G. Richard
* Written: 7/28/2013
*/
package com.jaxo.googapp.jaxogram;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;

public class AdFactory {
   public static Ad makeAd(HttpServletRequest req) throws Exception {
      HttpURLConnection conn = null;
      String cid = (String)req.getSession(true).getAttribute("iaad_cid");
      try {
         URL iaAdUrl = new URL(
            "http://m2m1.inner-active.mobi/simpleM2M/requestEnhancedHtmlAd" +
            "?aid=Jaxo_Jaxogram_other" +
            "&v=Sm2m-2.0.1" +
            "&ua=" + URLEncoder.encode(req.getHeader("user-agent"), "UTF-8") +
            "&cip=" + URLEncoder.encode(req.getRemoteAddr(), "UTF-8") +
            "&a=" + req.getParameter("a") +
            "&g=" + req.getParameter("g") +
            "&k=" + URLEncoder.encode(req.getParameter("k"), "UTF-8") +
            "&l=" + URLEncoder.encode(req.getParameter("l"), "UTF-8") +
            "&lg=" + URLEncoder.encode(req.getParameter("lg"), "UTF-8") +
            "&f=20" + // HTMLserving Ads
            ((cid != null)? ("&cid=" + cid) : "") +
            "&w=" + req.getParameter("w") +
            "&h=" + req.getParameter("h") +
            "&mnc=" + req.getParameter("mnc") +
            "&mcc=" + req.getParameter("mcc") +
            "&nt=" + req.getParameter("nt") +
            "&po=" + req.getParameter("po") +
            "&rw=" + req.getParameter("rw") +
            "&rh=" + req.getParameter("rh")
         );
         conn = (HttpURLConnection)iaAdUrl.openConnection();
         conn.setRequestMethod("GET");
         conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
         @SuppressWarnings("unchecked")
         Enumeration<String> headerNames = req.getHeaderNames();
         while (headerNames.hasMoreElements()) {
            String name = headerNames.nextElement();
            String value = req.getHeader(name);
            if (!name.equals("user-agent") && !name.equals("accept")) {
               conn.setRequestProperty("iA-" + name, value);
            }
         }
         conn.setDoInput(true);
         Ad ad = new Ad(
            new BufferedReader(
               new InputStreamReader(conn.getInputStream(), "UTF-8")
            )
         );
         if (ad.cid != null) {
            req.getSession(true).setAttribute("iaad_cid", ad.cid);
         }
         return ad;
      }finally {
         if (conn != null) conn.disconnect();
      }
   }

   static public class Ad
   {
      public String html;
      public String cid;
      public String error;
      public String adType;
      public String adWidth;
      public String adHeight;
      public String adNetwork;
      Ad(BufferedReader in) throws Exception { // Arghhh...
StringBuilder dbgBuf = new StringBuilder();
         StringBuilder buf = new StringBuilder();
         String line;
         String trackingPixelSrc = null;
         boolean isInScript = false;
         int beg;
         while ((line=in.readLine()) != null) {
dbgBuf.append(line);
dbgBuf.append("\n");
            line = line.trim();
            if (line.length() > 0) {
               if (isInScript) {
                  if (line.startsWith("</script")) {
                     isInScript = false;
                  }else if (trackingPixelSrc == null) {
                     beg = line.indexOf("iaImpressionTrackingPixelSrc");
                     if ((beg >= 0) && ((beg=line.indexOf("\"", beg)+1) > 0)) {
                        trackingPixelSrc = line.substring(beg, line.indexOf("\"", beg));
                     }
                  }
               }else if (line.startsWith("<script")) {
                  isInScript = true;
               }else if (line.startsWith("<input")) {
                  String id;
                  String value;
                  beg = line.indexOf("id=") + 4;
                  id = line.substring(beg, line.indexOf("\"", beg));
                  beg = line.indexOf("value=") + 7;
                  value = line.substring(beg, line.indexOf("\"", beg));
                  if (id.equals("inneractive-cid")) {
                     cid = value;
                  }else if (id.equals("inneractive-error")) {
                     error = value;
                  }else if (id.equals("inneractive-ad-type")) {
                     adType = value;
                  }else if (id.equals("inneractive-ad-width")) {
                     adWidth = value;
                  }else if (id.equals("inneractive-ad-height" )) {
                     adHeight = value;
                  }else if (id.equals("inneractive-ad-network")) {
                     adNetwork = value;
                  }
               }else if (
                  !line.startsWith("<img") ||
                  (line.indexOf("iaImpressionTrackingPixel") == -1)
               ) {
                  buf.append(line);
               }
            }
         }
         if (trackingPixelSrc != null) {
            buf.append("<img id='iaImpressionTrackingPixel' alt='' style='width:1px;height:1px' src='");
            buf.append(trackingPixelSrc);
            buf.append("'/>");
         }
String dbgStr = dbgBuf.toString();
if (dbgStr.startsWith("Roudoudou")) {
   error = "Riquiqui";
}
         html = buf.toString();
      }
   }
}
/*===========================================================================*/

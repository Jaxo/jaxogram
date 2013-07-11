/*
* $Id: $
*
* (C) Copyright 2013 Jaxo Inc.
*
* Mozilla Public License 2.0
*
* Author:  Pierre G. Richard
* Written: 7/10/2013
*/
package com.jaxo.googapp.jaxogram;
import java.io.OutputStream;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
/**/ import java.util.logging.Logger;

import org.apache.commons.io.IOUtils;

/*-- class ReceiptVerifier --+
*//**
*
* @author  Pierre G. Richard
* @version $Id: $
*/
class ReceiptVerifier {
   static String appTst = "{\"manifest\":{\"name\":\"Jaxogram\",\"version\":\"12.4\",\"description\":\"Jaxo's Photo Sharing App\",\"launch_path\":\"/index.html\",\"locales\":{\"en\":{\"description\":\"Jaxo's Photo Sharing App\"},\"fr\":{\"description\":\"App. Jaxo de partage de photos\"},\"pt\":{\"description\":\"App Jaxo de compartilhamento de foto\"},\"es\":{\"description\":\"App Jaxo de uso compartido de fotos\"},\"pl\":{\"description\":\"App Jaxo udostepniania zdjec\"}},\"developer\":{\"name\":\"Jaxo, Inc.\",\"url\":\"http://www.jaxo.com\"},\"default_locale\":\"en\",\"icons\":{\"60\":\"/applogos/logo60.png\",\"32\":\"/applogos/logo32.png\",\"64\":\"/applogos/logo64.png\",\"128\":\"/applogos/logo128.png\"},\"installs_allowed_from\":[\"*\"],\"type\":\"privileged\",\"permissions\":{\"systemXHR\":{\"description\":\"Access Jaxo's app server and OAuth\"},\"contacts\":{\"description\":\"Import Orkut friends as contacts\",\"access\":\"readcreate\"},\"device-storage:pictures\":{\"description\":\"Share photos\",\"access\":\"readcreate\"},\"storage\":{\"description\":\"Use local storage for access tokens\"}},\"activities\":{\"share\":{\"filters\":{\"type\":\"image/*\"},\"disposition\":\"window\",\"href\":\"/index.html?OP=share\"}}},\"updateManifest\":null,\"manifestURL\":\"app://95b97326-cb84-47d1-a104-38c3099ff679/manifest.webapp\",\"receipts\":[\"eyJhbGciOiAiUlMyNTYiLCAidHlwIjogIkpXVCIsICJqa3UiOiAiaHR0cHM6Ly9tYXJrZXRwbGFjZS5jZG4ubW96aWxsYS5uZXQvcHVibGljX2tleXMvbWFya2V0cGxhY2Utcm9vdC1wdWIta2V5Lmp3ayJ9.eyJpc3MiOiAiaHR0cHM6Ly9tYXJrZXRwbGFjZS5jZG4ubW96aWxsYS5uZXQvcHVibGljX2tleXMvbWFya2V0cGxhY2Utcm9vdC1wdWIta2V5Lmp3ayIsICJwcmljZV9saW1pdCI6IDEwMCwgImp3ayI6IFt7ImFsZyI6ICJSU0EiLCAibW9kIjogIkFNWUtaNFVyLVkxa2tTTXJUemtzVUtiRG1FOXdjaWlkV0FzTVd4R3F2bEFGSTZpbFp2ZXR4X2E4cWZoYnZxRnhTUW96cTBIRU9GRVVkdVJjSUtkV0Q0M2JtWmxaMHNnQVo0M0h1clc5QjIzX3QxbnhNeWc4QjFiV1AzWnMyUjVtZ0tZM0VxazlrZ2JKNGZRbkhWQkdvdnNPRDhiSy1WbFNXb0liaG5FR2JtYndvS1NHS0g1V2tTd2Zxb1ZFczNTNjhrNUdQX0gwMTFNNFJZSWhTbFM5amd2NlFUeDlwMU92ZFo4VmJUVnlZMGpZUHZLcjZoT2NqSXJBbGNsUHNBUl8zZFY0VG1BTmpaWHlLaFpyVnB5Wkp5MUhZcDlRLTZxQ21hZXJHaVhYdnVDMW5ycGN2RUNnWklMbW4zcHlLX2pwZHB6eXVlMjJpZDBDN0xfQW9JMkpYdHMiLCAiZXhwIjogIkFRQUIiLCAia2lkIjogImFwcHN0b3JlLm1vemlsbGEuY29tLTIwMTMtMDctMDMifV0sICJleHAiOiAxMzc0MDg0MDAxLCAiaWF0IjogMTM3Mjg3NDQwMSwgInR5cCI6ICJjZXJ0aWZpZWQta2V5IiwgIm5iZiI6IDEzNzI4NzQ0MDF9.LfxQPMqT316zzFgocaXbegU5eRhJjuuKGftb5JsVHlFwHh8jt7uXT2KdgYd15OP7VbukEQuiz2WFMTkLlEcCSGy-EbxWx_w_WzIJ2j2uZBeCtI8Z76nFitU4DN-KlWPyShZiZFjk1Y1YAhl6riVbuflpDhDInCktdlb6hc54imsz9rWqIjoumii502qG3uNADWDbYiH3sDoBQffqx9QBplt5bphTR7fTQ8OI96IuROaGQvdw4OZxgs6h4MJI9pC6r6Cu_lf9y-yqBgTO1lEqT7PNNeLVw9pHinDWecz7h5F8Of-YQNmP_b-a7bqY4UjrJ5he1NoPzYATKPOjn2xtlg~eyJqa3UiOiAiaHR0cHM6Ly9tYXJrZXRwbGFjZS5jZG4ubW96aWxsYS5uZXQvcHVibGljX2tleXMvbWFya2V0cGxhY2Utcm9vdC1wdWIta2V5Lmp3ayIsICJ0eXAiOiAiSldUIiwgImFsZyI6ICJSUzI1NiJ9.eyJwcm9kdWN0IjogeyJ1cmwiOiAiaHR0cHM6Ly85NWI5NzMyNi1jYjg0LTQ3ZDEtYTEwNC0zOGMzMDk5ZmY2Nzkuc2ltdWxhdG9yIiwgInN0b3JlZGF0YSI6ICJpZD0wIn0sICJpc3MiOiAiaHR0cHM6Ly9tYXJrZXRwbGFjZS5maXJlZm94LmNvbSIsICJ2ZXJpZnkiOiAiaHR0cHM6Ly9tYXJrZXRwbGFjZS5maXJlZm94LmNvbS9kZXZlbG9wZXJzL3Rlc3QvcmVjZWlwdHMvdmVyaWZ5L29rLyIsICJkZXRhaWwiOiAiaHR0cHM6Ly9tYXJrZXRwbGFjZS5maXJlZm94LmNvbS9kZXZlbG9wZXJzL3Rlc3QvcmVjZWlwdHMvZGV0YWlscy8iLCAicmVpc3N1ZSI6ICJodHRwczovL21hcmtldHBsYWNlLmZpcmVmb3guY29tL2RldmVsb3BlcnMvdGVzdC9yZWNlaXB0cy9kZXRhaWxzLyIsICJ1c2VyIjogeyJ0eXBlIjogImRpcmVjdGVkLWlkZW50aWZpZXIiLCAidmFsdWUiOiAibm9uZSJ9LCAiZXhwIjogMTM3MzUzODg5MiwgImlhdCI6IDEzNzM0NTI0OTIsICJ0eXAiOiAidGVzdC1yZWNlaXB0IiwgIm5iZiI6IDEzNzM0NTI0OTJ9.AZD4SxVUj4ZP5NVtv3syqZY7bhGPkam33EsANaBVzFFTt4C582un23mSbMBdjQohQse5j-wnN_NKxFkh_ZF7ONagzyT0XvqFcxE-AD1050bDGEhY-C9vWik05zFI9AYkLR3fqeYpaExtxsZ_oU162kTHfGzFGc6B50keMIV4p-8At1-soi5AHDdfRv-KxGwVUUlnueXgLfdcj9nrYaFLe9BUOYYTFBPXY9F4jwHa1SWnUm8Qi4N_6R46jmUK8XhHoGNPKRJKQKdPR1vfNt9I6VBROXKL7W3PSbF2yBrBAyOdMh1uXksbvy_byLArvmhMuHEiB2kZIcHJMYxdxoRftg\"],\"origin\":\"app://95b97326-cb84-47d1-a104-38c3099ff679\",\"installOrigin\":\"app://95b97326-cb84-47d1-a104-38c3099ff679\",\"installTime\":1373452501960,\"removable\":true,\"progress\":null,\"installState\":\"installed\",\"onprogress\":null,\"lastUpdateCheck\":1373452520902,\"updateTime\":1373452501960,\"downloadAvailable\":false,\"downloading\":false,\"readyToApplyDownload\":false,\"downloadSize\":0,\"downloadError\":{},\"ondownloadsuccess\":null,\"ondownloaderror\":null,\"ondownloadavailable\":null,\"ondownloadapplied\":null}";
   static void test() {
      try {
        verify(appTst);
      }catch (Exception e) {
         e.printStackTrace();
      }
   }

   /*------------------------------------------------------------------verify-+
   *//**
   *//*
   +-------------------------------------------------------------------------*/
   static void verify(String app) throws Exception {
/**/  Logger logger = Logger.getLogger(
/**/     "com.jaxo.googapp.jaxogram.ReceiptVerifier"
/**/  );
      Json.Root appRoot = Json.parse(new StringReader(app));
      for (Json.Member appMbr : ((Json.Object)appRoot).members) {
         if (appMbr.getKey().equals("receipts")) {
            for (Object value : ((Json.Array)appMbr.getValue()).values) {
               checkReceipt((String)value);
            }
         }
      }
   }

   /*------------------------------------------------------------checkReceipt-+
   *//**
   *//*
   +-------------------------------------------------------------------------*/
   static private boolean checkReceipt(String jwt) throws Exception
   {
      // split up the JWT to get the part that contains the receipt
      int ix;
      ix = jwt.indexOf('~');
      if (ix == -1) {
         throw new Exception("Invalid JWT");
      }
      String subJwt = jwt.substring(1+jwt.indexOf('.', ix+1));
      ix = subJwt.indexOf('.');
      if (ix == -1) {
         ix = subJwt.indexOf('~');
         if (ix == -1) {
            ix = subJwt.length();
         }
      }
      Json.Root rcpRoot = Json.parse(
         new StringReader(Base64.Url.decode(subJwt.substring(0, ix)))
      );
      ReceiptType rcpType = null;
      String storeUrl = null;
      Date issuedAt = null;
      String verifierUrl = null;
      for (Json.Member rcpMbr : ((Json.Object)rcpRoot).members) {
         String key = rcpMbr.getKey();
         if (key.equals("typ")) {
            rcpType = ReceiptType.make((String)rcpMbr.getValue());
         }else if (key.equals("iss")) {
            storeUrl = (String)rcpMbr.getValue();
         }else if (key.equals("iat")) {
            // currently, we do nothing with it
            issuedAt = new Date((Long)rcpMbr.getValue());
         }else if (key.equals("verify")) {
            verifierUrl = (String)rcpMbr.getValue();
         }
      }
      if (
         (rcpType == null) ||
         !rcpType.isAllowed() ||
         (issuedAt == null) ||
         (storeUrl == null) ||
         (verifierUrl == null) ||
         !isSubdomain(storeUrl, verifierUrl)
      ) {
         return false;
      }else {
         String response = post(
            new URL(verifierUrl), jwt.getBytes("UTF-8")
         );
         String status = null;
         Json.Root rspRoot = Json.parse(new StringReader(response));
         for (Json.Member rspMbr : ((Json.Object)rspRoot).members) {
            String key = rspMbr.getKey();
            if (key.equals("status")) {
               status = (String)rspMbr.getValue();
               break;
            }
         }
         if (status.equals("ok")) {
            return true;
         }
      }
      return false;
//    logger.info(
//       "\nReceipt Type:\t" + rcpType +
//       "\nIssuer store:\t" + storeUrl +
//       "\nIssued At: \t" + issuedAt +
//       "\nVerifier URL:\t" + verifierUrl +
//       "\nVerifier Response:\n\"" +
//       response + "\""
//    );
   }

   /*-------------------------------------------------------------isSubdomain-+
   *//**
   * Returns true if subdomain is the same as base, or a subdomain of it,
   * irregardless of protocol or port
   *//*
   +-------------------------------------------------------------------------*/
   private static boolean isSubdomain(String base, String subdomain) {
      String re1 = "^https?:\\/\\/"; // to remove the protocol
      String re2 = "[:\\/].*";       // to remove the path
      String sb1 = base.replace(re1, "").replace(re2, "");
      String sb2 = subdomain.replace(re1, "").replace(re2, "");
      int ofs = sb2.length() - sb1.length();
      return ((ofs >= 0) && sb1.equalsIgnoreCase(sb2.substring(ofs)));
   }

   /*--------------------------------------------------------------------post-+
   *//**
   * Post the full JWT to the verifier URL and retunrs the responce
   *//*
   +-------------------------------------------------------------------------*/
   private static String post(URL verifierUrl, byte[] jwt) throws Exception {
      HttpURLConnection conn = null;
      try {
         conn = (HttpURLConnection)verifierUrl.openConnection();
         conn.setRequestMethod("POST");
         conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
         conn.setRequestProperty("Content-Length", Integer.toString(jwt.length));
         conn.setDoInput(true);
         conn.setDoOutput(true);
         OutputStream out = conn.getOutputStream();
         out.write(jwt);
         out.close();
         return IOUtils.toString(conn.getInputStream());
      }finally {
         if (conn != null) conn.disconnect();
      }
   }

   /*------------------------------------------------------ enum ReceiptType -+
   *//**
   *//*
   +-------------------------------------------------------------------------*/
   static enum ReceiptType {
      PURCHASE("purchase-receipt", true),
      DEVELOPER("developer-receipt", true),
      REVIEWER("reviewer-receipt", true),
      TEST("test-receipt", true);

      private String m_name;
      private boolean m_isAllowed;
      private static Map<String, ReceiptType> m_fromName = new HashMap<String, ReceiptType>();
      static {
         for (ReceiptType t : values()) m_fromName.put(t.m_name, t);
      }
      private ReceiptType(String name, boolean isAllowed) {
         m_name = name;
         m_isAllowed = isAllowed;
      }
      public String toString() {
         return super.toString() + " => " + m_isAllowed;
      }
      public boolean isAllowed() {
         return m_isAllowed;
      }
      public static ReceiptType make(String name) {
         return m_fromName.get(name);
      }
   }
}
/*===========================================================================*/

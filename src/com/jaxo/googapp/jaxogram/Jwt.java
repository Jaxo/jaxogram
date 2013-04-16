/*
* $Id: $
*
* (C) Copyright 2013 Jaxo Inc.  All rights reserved.
* This work contains confidential trade secrets of Jaxo Inc.
* Use, examination, copying, transfer and disclosure to others
* are prohibited, except with the express written agreement of Jaxo.
*
* Author:  Pierre G. Richard
* Written: 4/12/2013
*/
package com.jaxo.googapp.jaxogram;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/*-- class Jwt --+
*//**
*
* @author  Pierre G. Richard
* @version $Id: $
*/
class Jwt {
   private static final String ENC = "UTF-8";
   private static final String ALGO = "HmacSHA256";
   private static final String HDR_SHA_256 = "{\"typ\":\"JWT\",\"alg\":\"HS256\"}";

   static String payKey = "?";
   static String paySecret = "?";
   static final String payKeySimulated = "18d91518-2fd1-42a8-8a3c-54dfccb2e6e4";
   static final String paySecretSimulated = "86c001ce38c23aca8a729fb6e4723e232fb726c1d3d3e609a6d02ea7787946916e4f1c90fc3c3b92ab9046329937d810";
   static final String productName = "Jaxogram";
   static final String productId = "f97e7b94-56a7-4184-b4f1-e02a1be2886d";
   static final String productDescr = "Jaxo\'s Photo Sharing App";
   static final int productPrice = 1;
   static final long TEN_YEARS_AS_SECS = 10 * 366 * 24 * 60 * 60;

   /*----------------------------------------------------------------base64Url-+
   *//**
   *//*
   +-------------------------------------------------------------------------*/
   static private String base64Url(String in) throws Exception {
      return base64Url(in.getBytes(ENC));
   }

   /*----------------------------------------------------------------base64Url-+
   *//**
   *//*
   +-------------------------------------------------------------------------*/
   static private String hashKey(String in) throws Exception {
      Mac mac = Mac.getInstance(ALGO);
      mac.init(new SecretKeySpec(paySecret.getBytes(ENC), ALGO));
      return base64Url(mac.doFinal(in.getBytes(ENC)));
   }

   /*----------------------------------------------------------------base64Url-+
   *//**
   *//*
   +-------------------------------------------------------------------------*/
   static private String base64Url(byte[] inBuf) throws Exception {
      return new String(Base64.Url.encode(inBuf), ENC);
   }

   /*------------------------------------------------------------------encode-+
   *//**
   *//*
   +-------------------------------------------------------------------------*/
   static private String encode(String claim) throws Exception {
      StringBuilder sb = new StringBuilder();
      sb.append(base64Url(HDR_SHA_256)).append('.').append(base64Url(claim));
      String sig = hashKey(sb.toString());
      return sb.append('.').append(sig).toString();
   }

   /*------------------------------------------------------------------decode-+
   *//**
   *//*
   +-------------------------------------------------------------------------*/
   static private String decode(String response) throws Exception {
      String[] ar = response.split("\\.", 3);
      if (!ar[2].equals(new StringBuilder().append(ar[0]).append('.').append(ar[1].toString()))) {
         throw new Exception("Invalid Signature");
      }
      return new String(Base64.Url.decode(ar[1].getBytes(ENC)), ENC);
   }

   /*-------------------------------------------------------makePurchaseOrder-+
   *//**
   *//*
   +-------------------------------------------------------------------------*/
   static public String makePurchaseOrder(
      String userId,
      String backUrl,
      String test
   ) throws Exception {
      String simulate = "";
      String extra = "";
      if (test != null) {
         payKey = payKeySimulated;
         paySecret = paySecretSimulated;
         StringBuilder sb = new StringBuilder("\n  ,\"simulate\": { \"result\": \"");
         if (test.charAt(1) == '1') {      // 0:denied, 1:granted,
            sb.append("postback");
         }else {
            sb.append("chargeback").append("\", \"reason\": \"").append("some reason");
         }
         sb.append("\" }");
         simulate = sb.toString();
         if (test.charAt(2) == '0') {      // 0:no answer, 2:late answer
            extra = "&A=N";
         }else if (test.charAt(2) == '2') {
            extra = "&A=L";
         }
      }
      return encode(
         "{\"iss\": \"" + payKey + "\"" +
         "\n,\"aud\": \"marketplace.firefox.com\"" +
         "\n,\"typ\": \"mozilla/payments/pay/v1\"" +
         "\n,\"iat\": " + (System.currentTimeMillis() / 1000L) +
         "\n,\"exp\": " + (TEN_YEARS_AS_SECS + (System.currentTimeMillis() / 1000L)) +
         "\n,\"request\": " +
         "\n  {\"id\": \"" + productId + "\"" +
         "\n  ,\"pricePoint\":" + productPrice +
         "\n  ,\"name\": \"" + productName + "\"" +
         "\n  ,\"description\": \"" + productDescr + "\"" +
         "\n  ,\"productData\": \"" + userId + "\"" +
         "\n  ,\"postbackURL\": \"" + backUrl + "YES" + extra + "\"" +
         "\n  ,\"chargebackURL\": \"" + backUrl + "NO" + extra + "\"" +
         simulate +
         "\n  }" +
         "\n}"
      );
   }

   /*--------------------------------------------------------getPaymentNotice-+
   *//**
   *//*
   +-------------------------------------------------------------------------*/
   static public String getPaymentNotice(String notice) throws Exception {
      return decode(notice);
   }
}

/*==========================================================================*/

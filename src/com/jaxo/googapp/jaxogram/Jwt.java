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

   /*----------------------------------------------------------------toBase64-+
   *//**
   *//*
   +-------------------------------------------------------------------------*/
   static private String toBase64(String in) throws Exception {
      return toBase64(in.getBytes(ENC));
   }

   /*----------------------------------------------------------------toBase64-+
   *//**
   *//*
   +-------------------------------------------------------------------------*/
   static private String toBase64(byte[] in) throws Exception {
      byte[] out = Base64.encode(in);
      int len = out.length;
      while ((--len > 0) && (out[len] == '='));
      return new String(out, 0, len+1, ENC);
   }

   /*------------------------------------------------------------------encode-+
   *//**
   *//*
   +-------------------------------------------------------------------------*/
   static private String encode(String claim) throws Exception {
      Mac mac = Mac.getInstance(ALGO);
      mac.init(new SecretKeySpec(paySecret.getBytes(ENC), ALGO));
      StringBuilder sb = new StringBuilder();
      sb.append(toBase64(HDR_SHA_256)).append('.').append(toBase64(claim));
      String sig = toBase64(mac.doFinal(sb.toString().getBytes(ENC)));
      sb.append('.').append(sig);
      return sb.toString();
   }

   /*------------------------------------------------------------------decode-+
   *//**
   *//*
   +-------------------------------------------------------------------------*/
   static private String decode(String response) throws Exception {
      Mac mac = Mac.getInstance(ALGO);
      mac.init(new SecretKeySpec(paySecret.getBytes(ENC), ALGO));
      String[] vals = response.split("\\.", 3);
      StringBuilder sb = new StringBuilder();
      sb.append(vals[0]).append('.').append(vals[1]);
      String sig1 = toBase64(mac.doFinal(sb.toString().getBytes(ENC)));
      String sig2 = vals[2].replace('-', '+').replace('_','/');
      if (!sig2.equals(sig1)) {
         throw new Exception("Invalid Signature");
      }
      return new String(Base64.decode(vals[1].getBytes(ENC)), ENC);
   }

   static final String payKey = "18d91518-2fd1-42a8-8a3c-54dfccb2e6e4";
   static final String paySecret = "86c001ce38c23aca8a729fb6e4723e232fb726c1d3d3e609a6d02ea7787946916e4f1c90fc3c3b92ab9046329937d810";
   static final String productName = "Jaxogram";
   static final String productId = "f97e7b94-56a7-4184-b4f1-e02a1be2886d";
   static final String productDescr = "Jaxo\'s Photo Sharing App";
   static final int productPrice = 1;
   static final long TEN_YEARS_AS_SECS = 10 * 366 * 24 * 60 * 60;

   /*-------------------------------------------------------makePurchaseOrder-+
   *//**
   *//*
   +-------------------------------------------------------------------------*/
   static public String makePurchaseOrder(
      String userId,
      String backUrl
   ) throws Exception {
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
         "\n  ,\"postbackURL\": \"" + backUrl + "YES\"" +
         "\n  ,\"chargebackURL\": \"" + backUrl + "NO\"" +
         "\n  ,\"simulate\": { \"result\": \"postback\" }" +
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

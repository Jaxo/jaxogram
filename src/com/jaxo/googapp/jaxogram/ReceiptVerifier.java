/*
* (C) Copyright 2013 Jaxo Inc.
*
* This Source Code Form is subject to the terms of the Mozilla Public
* License, v. 2.0.  If a copy of the MPL was not distributed with this
* file, you can obtain one at http://mozilla.org/MPL/2.0/.
*
* Author:  Pierre G. Richard
* Written: 7/10/2013
*/
package com.jaxo.googapp.jaxogram;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*-- class ReceiptVerifier --+
*//**
*
* @author  Pierre G. Richard
* @version $Id: $
*/
public class ReceiptVerifier
{
   /*------------------------------------------------------------------verify-+
   *//**
   * Verify whether an Application has the proper payment receipt.
   * The String <code>app</code> is a JSON stringified Mozilla App object.
   *
   * For example, <code>window.navigator.mozApps.getSelf()</code> calls
   * <code>onsuccess</code> passing such an <code>App<code> object
   * in the <code>result</code> field of the <code>DomRequest</code> argument.
   *
   * @param app JSON stringified Mozilla App Object to check the receipt of
   * @return a ReceiptVerifier.Status enumeration value
   *//*
   +-------------------------------------------------------------------------*/
   static public Status verify(String app)
   {
      if (app.length() == 0) {
         return Status.EMPTY;
      }else {
         Status status = Status.ERROR;
         try {
            Json.Root appRoot = Json.parse(new StringReader(app));
            for (Json.Member appMbr : ((Json.Object)appRoot).members) {
               if (appMbr.getKey().equals("receipts")) {
                  for (Object value : ((Json.Array)appMbr.getValue()).values) {
                     status = Status.getBestOf(
                        checkReceipt((String)value), status
                     );
                     if (status == Status.OK) return status;
                  }
                  break;
               }
            }
         }catch (Exception e) {}
         return status;
      }
   }

   /*------------------------------------------------------------checkReceipt-+
   *//**
   *//*
   +-------------------------------------------------------------------------*/
   static private Status checkReceipt(String jwt)
   {
      try {
         // split up the JWT to get the part that contains the receipt
         int ix;
         ix = jwt.indexOf('~');
         if (ix == -1) {
            return Status.BAD_JWT;
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
            return Status.BAD_FIELDS;
         }else {
            Status status = getVerifierStatus(
               post(new URL(verifierUrl), jwt.getBytes("UTF-8"))
            );
            if ((status == Status.OK) && (rcpType == ReceiptType.TEST)) {
               return Status.TESTONLY;
            }else {
               return status;
            }
         }
      }catch (Exception e) {
         return Status.ERROR;
      }
   }

   /*---------------------------------------------------------------getDomain-+
   *//**
   *//*
   +-------------------------------------------------------------------------*/
   static Pattern pat = Pattern.compile("^https?:\\/\\/(.*?)(([:\\/].*)|$)");
   private static String getDomain(String url) {
      Matcher matcher = pat.matcher(url);
      matcher.matches();
      return matcher.group(1).toLowerCase();
   }

   /*-------------------------------------------------------------isSubdomain-+
   *//**
   * Returns true if url2 is the same as url1, or a subdomain of it,
   * irregardless of http/https protocol, and/or port
   *
   * @param url1 main url
   * @param url2 url the domain or subdomain of which
   *             must match the domain of <code>url1</code>
   * @return true if url2 is the same as url1, or a subdomain of it
   * @exception IllegalStateException or IndexOutOfBoundsException
   *//*
   +-------------------------------------------------------------------------*/
   private static boolean isSubdomain(String url1, String url2) {
      return getDomain(url2).endsWith(getDomain(url1));
   }

   /*--------------------------------------------------------------------post-+
   *//**
   * Post the full JWT to the verifier URL and returns the JSON response
   *//*
   +-------------------------------------------------------------------------*/
   private static Json.Root post(URL verifierUrl, byte[] jwt) throws Exception
   {
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
         return Json.parse(
            new InputStreamReader(conn.getInputStream(), "UTF-8")
         );
      }finally {
         if (conn != null) conn.disconnect();
      }
   }

   /*-------------------------------------------------------getVerifierStatus-+
   *//**
   *//*
   +-------------------------------------------------------------------------*/
   static private Status getVerifierStatus(Json.Root root)
   throws Exception
   {
      for (Json.Member mbr : ((Json.Object)root).members) {
         if (mbr.getKey().equals("status")) {
            return Status.make((String)mbr.getValue());
         }
      }
      return Status.UNKNOWN;
   }

   /*------------------------------------------------------ enum ReceiptType -+
   *//**
   *//*
   +-------------------------------------------------------------------------*/
   private static enum ReceiptType {
      PURCHASE("purchase-receipt", true),
      DEVELOPER("developer-receipt", true),
      REVIEWER("reviewer-receipt", true),
      TEST("test-receipt", true),
      UNKNOWN(null, false);

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
      public boolean isAllowed() {
         return m_isAllowed;
      }
      public static ReceiptType make(String name) {
         ReceiptType type = m_fromName.get(name);
         return (type == null)? UNKNOWN : type;
      }
   }

   /*----------------------------------------------------------- enum Status -+
   *//**
   *//*
   +-------------------------------------------------------------------------*/
   public static enum Status {
      OK(
         "ok",
         new String[] {
            "Receipt validated",
            "Récépissé validé",
            "Recibo validado",
            "Recibo validado",
            "Rebut validat",
            "Otrzymanie zatwierdzony"
         }
      ),
      PENDING(
         "pending",
         new String[] {
            "Payment in progress",
            "Paiement en cours",
            "Pagamento em andamento",
            "Pago en curso",
            "Pagament en curs",
            "Płatność w toku",
         }
      ),
      REFUNDED(
         "refunded",
         new String[] {
            "Purchase refunded",
            "Achat remboursé",
            "Dinheiro devolvido",
            "El dinero reembolsado",
            "Els diners reemborsat",
            "Pieniądze zwracane"
         }
      ),
      EXPIRED(
         "expired",
         new String[] {
            "Expiry date has passed",
            "Date de validation expirée",
            "Prazo de validade",
            "Fecha de caducidad ha pasado",
            "Data de caducitat ha passat",
            "Termin ważności upłynął"
         }
      ),
      TESTONLY(
         "",
         new String[] {
            "Test receipt",
            "Récépissé d'essai",
            "Certificado de Teste",
            "Certificado de prueba",
            "Certificat de prova",
            "Test Certyfikat"
         }
      ),
      INVALID(
         "invalid",
         new String[] {
            "Invalid receipt",
            "Récépissé non valide",
            "O certificado não é válido",
            "Certificado no válido",
            "Certificat no vàlid",
            "Certyfikat nieważny"
         }
      ),
      EMPTY(
         "",
         new String[] {
            "No receipts found",
            "Absence de récépissés",
            "Não há recibos",
            "No hay recibos",
            "No hi ha rebuts",
            "Brak otrzymane"
         }
      ),
      BAD_FIELDS(
         "",
         new String[] {
            "Improper receipt",
            "Récépissé non conforme",
            "Certificado impróprio",
            "Certificado incorrecto",
            "Certificat incorrecte",
            "Niewłaściwe Certyfikat"
         }
      ),
      BAD_JWT(
         "",
         new String[] {
            "Unreadable receipt",
            "Récépissé illisible",
            "Certificado ilegível",
            "Certificado ilegible",
            "Certificat il · legible",
            "Nieczytelne certyfikat"
         }
      ),
      ERROR(
         "",
         new String[] {
            "Processing errors",
            "Erreurs au traitement",
            "Erros de processamento",
            "Errores de procesamiento",
            "Errors de processament",
            "Błędy przetwarzania"
         }
      ),
      UNKNOWN(
         "",
         new String[] {
            "Unrecognized validation",
            "Validation non reconnue",
            "Validação não reconhecido",
            "Validación no reconocido",
            "Validació no reconegut",
            "Nierozpoznany walidacji"
         }
      );
      static final String[] i18nLang = { "en", "fr", "pt", "es", "ca", "pl" };
      private String m_name;
      private String[] m_msgs;
      private static Map<String, Status> m_fromName = new HashMap<String, Status>();
      static {
         for (Status t : values()) {
            if (t.m_name.length() > 0) m_fromName.put(t.m_name, t);
         }
      }
      private Status(String name, String[] msgs) {
         m_name = name;
         m_msgs = msgs;
      }
      public static Status make(String name) {
         Status status = m_fromName.get(name);
         return (status == null)? UNKNOWN : status;
      }
      public static Status getBestOf(Status status1, Status status2) {
         return (status1.ordinal() < status2.ordinal())? status1 : status2;
      }
      public String toString() {
         return toString("en");
      }
      public String toString(String iso639) {
         int ix = 0;
         // no time to implement a ResourceBundle...
         for (int i=0, max=i18nLang.length; i < max; ++i) {
            if (iso639.startsWith(i18nLang[i])) {
               ix = i;
               break;
            }
         }
         return toCharRef(m_msgs[ix]) + " (" + ordinal() + ")";
      }
      public static String toCharRef(String msg) {
         StringBuilder sb = new StringBuilder();
         for (int i=0, max = msg.length(); i < max; ++i) {
            int ch = msg.charAt(i);
            if (ch < 0x80) {
               sb.append((char)ch);
            }else {
               sb.append(String.format("&#x%04x;", ch));
            }
         }
         return sb.toString();
      }
   }
}
/*===========================================================================*/

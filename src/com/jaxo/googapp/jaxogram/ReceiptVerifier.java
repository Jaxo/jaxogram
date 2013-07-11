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
import java.io.StringReader;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
/**/ import java.util.logging.Logger;
/**/ import java.util.logging.Level;

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

   static void verify(String app) throws Exception {
/**/  Logger logger = Logger.getLogger(
/**/     "com.jaxo.googapp.jaxogram.ReceiptVerifier"
/**/  );
      Json.Root appRoot = Json.parse(new StringReader(app));
      for (Json.Member appMbr : ((Json.Object)appRoot).members) {
         if (appMbr.getKey().equals("receipts")) {
            for (Object value : ((Json.Array)appMbr.getValue()).values) {
               String jwt = (String)value;
               // split up the JWT to get the part that contains the jwt
               int ix;
               ix = jwt.indexOf('~');
               if (ix == -1) {
                  throw new Exception("Invalid JWT");
               }
               jwt = jwt.substring(1+jwt.indexOf('.', ix+1));
               ix = jwt.indexOf('.');
               if (ix == -1) {
                  ix = jwt.indexOf('~');
                  if (ix == -1) {
                     ix = jwt.length();
                  }
               }
               Json.Root rcpRoot = Json.parse(
                  new StringReader(Base64.Url.decode(jwt.substring(0, ix)))
               );
               ReceiptType rcpType = null;
               String issuerStore = null;
               Date issuedAt = null;
               URL verifierUrl = null;
               for (Json.Member rcpMbr : ((Json.Object)rcpRoot).members) {
                  String key = rcpMbr.getKey();
                  if (key.equals("typ")) {
                     rcpType = ReceiptType.make((String)rcpMbr.getValue());
                  }else if (key.equals("iss")) {
                     issuerStore = (String)rcpMbr.getValue();
                  }else if (key.equals("iat")) {
                     issuedAt = new Date((Long)rcpMbr.getValue());
                  }else if (key.equals("verify")) {
                     verifierUrl = new URL((String)rcpMbr.getValue());
                  }
               }
               logger.info(
                  "\nReceipt Type:\t" + rcpType +
                  "\nIssuer store:\t" + issuerStore +
                  "\nIssued At: \t" + issuedAt +
                  "\nVerifier URL:\t" + verifierUrl
               );
            }
         }
      }
   }

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
      public static ReceiptType make(String name) {
         return m_fromName.get(name);
      }
   }

}
/*
  var receipts = JSON.parse(app).receipts;
  if ((!receipts) || (!receipts.length)) {
    if (receipts === undefined) {
      alert("ERROR: the receipts property of the app object is undefined");
    }else {
       alert("No receipts were found or installed");
    }
    return;
  }
  foreach(
    receipts,
    function(receipt) {
    }

  );
  var parsed;
  try {
    parsed = JSON.parse(receipt);
  }catch (e) {
    alert("Error decoding JSON: " + e);
    return;
  }
  var typ = parsed.typ;
  if (this.typsAllowed.indexOf(typ) < 0) {
    alert("Wrong receipt type: " + typ);
    callback();
    return;
  }
  var iss = parsed.iss;
  if (! iss) {
    this._addReceiptError(receipt, new this.errors.ReceiptFormatError("No (or empty) iss field"), {parsed: parsed});
    callback();
    return;
  }
  // FIXME: somewhat crude checking, case-sensitive:
  if (this.installs_allowed_from && _forceIndexOf(this.installs_allowed_from, iss) == -1 && _forceIndexOf(this.installs_allowed_from, "*") == -1) {
    this._addReceiptError(receipt, new this.errors.InvalidReceiptIssuer("Issuer (iss) of receipt is not a valid installer: " + iss, {iss: iss, installs_allowed_from: this.installs_allowed_from}));
    callback();
    return;
  }
  var verify = parsed.verify;
  if (! verify) {
    this._addReceiptError(receipt, new this.errors.ReceiptFormatError("No (or empty) verify field"), {parsed: parsed});
    callback();
    return;
  }
  if (! isSubdomain(parsed.iss, verify)) {
    this._addReceiptError(receipt, new this.errors.VerifyDomainMismatch("Verifier domain is not the same (or a subdomain) of the issuer domain", {parsed: parsed, iss: parsed.iss, verify: parsed.verify}));
    callback();
    return;
  }
  // Node.js
  if (typeof XMLHttpRequest === "undefined") {
    XMLHttpRequest = require("xmlhttprequest").XMLHttpRequest;
  }
  var req = new XMLHttpRequest();
  var self = this;
  var timeout = null;
  this.log(this.levels.INFO, "POSTing to " + verify);
  req.open("POST", verify);
  req.onreadystatechange = function () {
    if (req.readyState != 4) {
      return;
    }
    self.log(self.levels.INFO, "Request to " + verify + " completed with status: " + req.status);
    if (timeout) {
      clearTimeout(timeout);
      timeout = null;
    }
    if (req.status === 0) {
      self._addReceiptError(
        receipt,
        new self.errors.ConnectionError("Server could not be contacted", {request: req, url: verify}));
      callback();
      return;
    }
    if (req.status == 404) {
      self._addReceiptError(
        receipt,
        new self.errors.ServerStatusError("Server responded with 404 to " + verify,
                                          {request: req, status: req.status, url: verify}));
      callback();
      return;
    }
    if (req.status != 200) {
      self._addReceiptError(
        receipt,
        new self.errors.ServerStatusError("Server responded with non-200 status: " + req.status,
        {request: req, status: req.status, url: verify}));
      callback();
      return;
    }
    var result;
    try {
      result = JSON.parse(req.responseText);
    } catch (e) {
      self._addReceiptError(receipt, new self.errors.InvalidServerResponse("Invalid JSON from server", {request: req, text: req.responseText}));
      callback();
      return;
    }
    if (typeof result != "object" || result === null) {
      self._addReceiptError(receipt, new self.errors.InvalidServerResponse("Server did not respond with a JSON object (" + JSON.stringify(result) + ")", {request: req, text: req.responseText}));
      callback();
      return;
    }
    self.log(self.levels.INFO, "Receipt (" + receipt.substr(0, 4) + "...) completed with result: " + JSON.stringify(result));
    if (result.status == "ok" || result.status == "pending") {
      // FIXME: should represent pending better:
      self._addReceiptVerification(receipt, result);
      if (result.status == "ok") {
        // FIXME: maybe pending should be saved too, in case of future network error
        self._saveResults(receipt, parsed, result);
      }
      callback();
      return;
    }
    if (result.status == "refunded") {
      self._addReceiptError(receipt, new self.errors.Refunded("Application payment was refunded", {result: result}));
      callback();
      return;
    }
    if (result.status == "expired") {
      self._addReceiptError(receipt, new self.errors.ReceiptExpired("Receipt expired", {result: result}));
      // FIXME: sometimes an error, sometimes not?  Accumulate separately?
      self._addReceiptVerification(receipt, result);
      callback();
      return;
    }
    if (result.status == "invalid") {
      self._addReceiptError(receipt, new self.errors.InvalidFromStore("The store reports the receipt is invalid", {result: result}));
      callback();
      return;
    }
    self._addReceiptError(receipt, new self.errors.InvalidServerResponse("Store replied with unknown status: " + result.status, {result: result}));
    callback();
  };
  req.send(receipt);
  if (this.requestTimeout) {
    timeout = setTimeout(function () {
      req.abort();
      self.log(self.levels.ERROR, "Request to " + verify + " timed out");
      self._addReceiptError(
        receipt,
        new self.errors.RequestTimeout(
          "The request timed out after " + self.requestTimeout + " milliseconds",
          {request: req, url: verify})
      );
      callback();
    }, this.requestTimeout);
  }
};
*/

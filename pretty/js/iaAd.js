//      Phones                         Tablets
//
//  168 x  28 (M)                  468 x  60 (XXXL)
//  216 x  36 (L)                  728 x  90 (XXXXL)
//  300 x  50 (XL)                1024 x  90 (XXXXXL)
//  300 x 250 (Rectangle)         1024 x 768 (Full Screen L)
//  320 x  50 (XXL)                900 x 500 (Full Screen M)
//  320 x 480 (Fulll Screen)      1280 x 800 (Full Screen XL)


create_iaAd = function(container, onNavigate) {
   var eltSection = container;
   var eltFrame = document.createElement("iframe");
   var eltBtnHide = document.createElement("button");
   var opts = {
      APP_ID: "Jaxo_Jaxogram_other", // "Mozilla_AppTest_other",
      PORTAL: 642,
      BANNER_REQUIRED_WIDTH: 300,    // if you change it, keep iaAd.css in synch!
      BANNER_REQUIRED_HEIGHT: 50,
      SPLASH_REQUIRED_WIDTH: 320,
      SPLASH_REQUIRED_HEIGHT: 480,
      IS_MOBILE_WEB: true,
      IS_ORMMA_SUPPORT: false,
      IS_MRAID_SUPPORT: false,
      IS_INTERSTITIAL_AD: false,
      CATEGORY: "Music",
      AGE: "",
      GENDER: "",
      KEYWORDS: "Rock,Pop,Jazz,Blues",
      DEFAULT_GPS_COORDINATES: "40.41694,-3.70081",
      LOCATION: "",
      MOBILE_NETWORK_CODE: "",
      MOBILE_COUNTRY_CODE: "",
      NETWORK: "",                   // 3G or WIFI
      FAILOVER: "",
      REFRESH_RATE: 0
   };
   var params = (
      "?v=" + (
         (opts.IS_ORMMA_SUPPORT)? (
            (opts.IS_MRAID_SUPPORT)? "Stag-2.1.0&f=116" : "Stag-2.1.0&f=52"
         ) : (
            (opts.IS_MRAID_SUPPORT)? "Stag-2.1.0&f=84" : "Stag-2.0.1&f=20"
         )
      ) +
      "&fs=" + ((opts.IS_INTERSTITIAL_AD)? "true" : "false") +
      "&aid=" + opts.APP_ID +
      "&po=" + opts.PORTAL +
      "&c=" + opts.CATEGORY +
      "&k=" + encodeURIComponent(opts.KEYWORDS) + (
         (opts.FAILOVER)?
         "&noadstring=" + encodeURIComponent(opts.FAILOVER) :
         "&test=true"
      ) +
      "&l=" + encodeURIComponent(opts.LOCATION) +
      "&mw=" + ((opts.IS_MOBILE_WEB) ? "true" : "false") +
      "&iemd=" +  // IMEI_MD5
      "&iesha=" + // IMEI_SHA1
      "&mmd=" +   // MAC_MD5
      "&msha=" +  // MAC_SHA1
      "&dmd=" +   // UDID_MD5
      "&dsha=" +  // UDID_SHA1
      "&ismd=" +  // IMSI_MD5
      "&issha=" + // IMSI_SHA1
      "&amd=" +   // ANDROID_ID_MD5
      "&asha=" +  // ANDROID_ID_SHA1
      "&idfa=" +  // IDFA
      "&idfv=" +  // IDFV
      "&a=" + opts.AGE +
      "&g=" + opts.GENDER +
      "&w=" + window.innerWidth +
      "&h=" + window.innerHeight +
      "&mnc=" + opts.MOBILE_NETWORK_CODE +
      "&mcc=" + opts.MOBILE_COUNTRY_CODE +
      "&nt=" + opts.NETWORK +
      "&ow=" +    // OPTIONAL_WIDTH +
      "&oh="      // OPTIONAL_HEIGHT
   );

   /*
   | We cannot use "target='_blank' b/c it would violate the same-origin
   | policy, and the sandbox attribute doesn't work, etc.
   | So let's go "base='_self', and stay in the iframe?
   | Alas, the "_self" frame size is appropriate for a banner, not for a splash.
   | As well as the burden for keeping a "back" button all the way...
   |
   | The only valid possibility is to post a message to the top browser window,
   | and let him do the navigation (overriding the <A> onclick event handler of
   | this cross-origin frame)
   |
   | When a navigation is launched, the iaAd instance calls the "onNavigate"
   | passed as an argument to the create_iaAd method.  "onNavigate" takes
   | one argument: the URL to navigate to.
   */
   var htmlProlog = (
      "<html><head>" + (
        (opts.REFRESH_RATE && (opts.REFRESH_RATE > 0))?
        "<meta http-equiv='refresh' content='" + opts.REFRESH_RATE + "'/>" : ""
      ) +
      "<base target='_self' />" +
      "</head>" +
      "<body" +
      " style='padding:0;margin:0;overflow:hidden'" +
      " onclick='location.reload();'>" +
      "<script language='javascript' " +
      "src='http://ad-tag.inner-active.mobi/simpleM2M/RequestTagAd" +
      params
   );
   var htmlEpilog = (
      "'><\/script>" +
      "<script language='javascript'>" +
         "var daddy = parent;" +
         "document.body.removeAttribute('onclick');" +
         "document.getElementById('iaAdContainerDiv').querySelector('a').onclick = function() { " +
            "daddy.postMessage('iaAd' + this.href, '*');" +
            "return false;" +
         "};" +
      "<\/script>" +
      "</body></html>"
   );
   var gpsCoordinates = encodeURIComponent(DEFAULT_GPS_COORDINATES);
   var bannerSrc = (
      "data:text/html;charset=utf-8," +
      htmlProlog +
      "&lg=" + gpsCoordinates +
      "&rw=" + opts.BANNER_REQUIRED_WIDTH +
      "&rh=" + opts.BANNER_REQUIRED_HEIGHT +
      htmlEpilog
   );
   var splashSrc = (
      "data:text/html;charset=utf-8," +
      htmlProlog +
      "&lg=" + gpsCoordinates +
      "&rw=" + opts.SPLASH_REQUIRED_WIDTH +
      "&rh=" + opts.SPLASH_REQUIRED_HEIGHT +
      htmlEpilog
   );

   var hide = function() {
      eltSection.style.display = "none";
      eltBtnHide.style.display = "none";
   };

   var show = function(role, src) {
      eltFrame.style.visibility = "hidden";
      eltFrame.style.opacity = "0";
      eltFrame.onload = function() {
         this.style.visibility = "";
         this.style.opacity = "1";
         eltBtnHide.style.opacity = "1";
      }
      eltFrame.setAttribute("role", role);
      eltSection.style.display="block";
      eltFrame.src = src;
   }

   var doNavigate = onNavigate;

   addEventListener(
      "message",
      function(e) {
         if (doNavigate) {
            var message = e.data;
            if (message.substr(0, 4) == "iaAd") {
               doNavigate(message.substr(4));
            }
         }
      },
      false
   );
   eltBtnHide.textContent = "\u00d7";
   eltBtnHide.onclick = hide;
   eltSection.appendChild(eltFrame);
   eltSection.appendChild(eltBtnHide);
   return {
      hide: hide,
      showBanner: function() {
         eltBtnHide.style.display = "none";
         show("banner", bannerSrc);
      },
      showSplash: function() {
         eltBtnHide.style.opacity = "0";
         eltBtnHide.style.display = "block";
         show("complementary", splashSrc);
      },
      setGpsCoordinates(latitude, longitude) {
         gpsCoordinates = encodeURIComponent(latitude + "," + longitude);
      }
   };
}



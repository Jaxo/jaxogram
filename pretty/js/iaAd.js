//      Phones                         Tablets
//
//  168 x  28 (M)                  468 x  60 (XXXL)
//  216 x  36 (L)                  728 x  90 (XXXXL)
//  300 x  50 (XL)                1024 x  90 (XXXXXL)
//  300 x 250 (Rectangle)         1024 x 768 (Full Screen L)
//  320 x  50 (XXL)                900 x 500 (Full Screen M)
//  320 x 480 (Fulll Screen)      1280 x 800 (Full Screen XL)

var iaAdOptions = {
   APP_ID: "Jaxo_Jaxogram_other",  // "Mozilla_AppTest_other",
   PORTAL: 642,
   BANNER_REQUIRED_WIDTH: 300,     // if you change it, keep iaAd.css in synch!
   BANNER_REQUIRED_HEIGHT: 50,
   SPLASH_REQUIRED_WIDTH: 320,
   SPLASH_REQUIRED_HEIGHT: 480,
   IS_MOBILE_WEB: true,
   IS_ORMMA_SUPPORT: false,
   IS_MRAID_SUPPORT: false,
   IS_INTERSTITIAL_AD: false,
   IMEI_MD5: "",
   IMEI_SHA1: "",
   MAC_MD5: "",
   MAC_SHA1: "",
   UDID_MD5: "",
   UDID_SHA1: "",
   IMSI_MD5: "",
   IMSI_SHA1: "",
   ANDROID_ID_MD5: "",
   ANDROID_ID_SHA1: "",
   IDFA: "",
   IDFV: "",
   CATEGORY: "Music",
   AGE: "",
   GENDER: "",
   KEYWORDS: "Rock,Pop,Jazz,Blues",
   LOCATION: "",
   GPS_COORDINATES: "",
   MOBILE_NETWORK_CODE: "",
   MOBILE_COUNTRY_CODE: "",
   NETWORK: "",                       // values are 3G and WIFI,
   OPTIONAL_WIDTH: "",                // optional ad width,
   OPTIONAL_HEIGHT: "",               // optional ad height,
   FAILOVER: "",
   REFRESH_RATE: 0
};

function iaAd(opts) {
   var params = (
      "?v=" + (
         (opts.IS_ORMMA_SUPPORT)? (
            (opts.IS_MRAID_SUPPORT)? "Stag-2.1.0&f=116" : "Stag-2.1.0&f=52"
         ) : (
            (opts.IS_MRAID_SUPPORT)? "Stag-2.1.0&f=84" : "Stag-2.0.1&f=20"
         )
      ) + (
         (opts.IS_INTERSTITIAL_AD)? "&fs=true" : "&fs=false"
      ) +
      "&aid=" + opts.APP_ID +
      "&po=" + opts.PORTAL +
      "&c=" + opts.CATEGORY +
      "&k=" + encodeURIComponent(opts.KEYWORDS) + (
         (opts.FAILOVER)?
         "&noadstring=" + encodeURIComponent(opts.FAILOVER) :
         "&test=true"
      ) +
      "&lg=" + encodeURIComponent(opts.GPS_COORDINATES) +
      "&l=" + encodeURIComponent(opts.LOCATION) +
      "&mw=" + ((opts.IS_MOBILE_WEB) ? "true" : "false") +
      "&iemd=" + opts.IMEI_MD5 +
      "&iesha=" + opts.IMEI_SHA1 +
      "&mmd=" + opts.MAC_MD5 +
      "&msha=" + opts.MAC_SHA1 +
      "&dmd=" + opts.UDID_MD5 +
      "&dsha=" + opts.UDID_SHA1 +
      "&ismd=" + opts.IMSI_MD5 +
      "&issha=" + opts.IMSI_SHA1 +
      "&amd=" + opts.ANDROID_ID_MD5 +
      "&asha=" + opts.ANDROID_ID_SHA1 +
      "&idfa=" + opts.IDFA +
      "&idfv=" + opts.IDFV +
      "&a=" + opts.AGE +
      "&g=" + opts.GENDER +
      "&w=" + window.innerWidth +
      "&h=" + window.innerHeight +
      "&mnc=" + opts.MOBILE_NETWORK_CODE +
      "&mcc=" + opts.MOBILE_COUNTRY_CODE +
      "&nt=" + opts.NETWORK +
      "&ow=" + opts.OPTIONAL_WIDTH +
      "&oh=" + opts.OPTIONAL_HEIGHT
   );

   var htmlProlog = (
      "<html><head>" + (
        (opts.REFRESH_RATE && (opts.REFRESH_RATE > 0))?
        "<meta http-equiv='refresh' content='" + opts.REFRESH_RATE + "'/>" : ""
      ) +
      "<base target='_blank' />" +
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
      "</body></html>"
   );
   this.bannerSrc = (
      "data:text/html;charset=utf-8," +
      htmlProlog +
      "&rw=" + opts.BANNER_REQUIRED_WIDTH +
      "&rh=" + opts.BANNER_REQUIRED_HEIGHT +
      htmlEpilog
   );
   this.splashSrc = (
      "data:text/html;charset=utf-8," +
      htmlProlog +
      "&rw=" + opts.SPLASH_REQUIRED_WIDTH +
      "&rh=" + opts.SPLASH_REQUIRED_HEIGHT +
      htmlEpilog
   );
}

iaAd.prototype = {
   makeBannerFrame: function() {
      var frame = document.createElement("iframe");
      frame.src = this.bannerSrc;
      frame.setAttribute("role", "banner");
      frame.setAttribute("scrolling", "no");
      return frame;
   },
   makeSplashFrame: function() {
      var frame = document.createElement("iframe");
      frame.src = this.splashSrc;
      frame.setAttribute("role", "complementary");
      frame.setAttribute("scrolling", "no");
      return frame;
   },
// refresh: function() {
//    frame.src = frame.src;
// }
};


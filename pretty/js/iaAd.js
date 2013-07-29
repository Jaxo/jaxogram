//      Phones                         Tablets
//
//  168 x  28 (M)                  468 x  60 (XXXL)
//  216 x  36 (L)                  728 x  90 (XXXXL)
//  300 x  50 (XL)                1024 x  90 (XXXXXL)
//  300 x 250 (Rectangle)         1024 x 768 (Full Screen L)
//  320 x  50 (XXL)                900 x 500 (Full Screen M)
//  320 x 480 (Fulll Screen)      1280 x 800 (Full Screen XL)

create_iaAd = function(container, onNavigate) {
   /*
   | `onNavigate' is a function which is called when the frame window.location
   | changes.  The argument passed is an url -- e.g.: onNavigate(url).
   | The url can be an empty string if the user just closed the frame (really
   | hidden, not closed).
   */
   var timeoutId;
   var doNavigate = onNavigate;
   var eltSection = container;
   var eltFrame = document.createElement("div");
   var eltBtnHide = document.createElement("button");
   var opts = {
      PORTAL: 642,              // FIXME: that's for iPhone/iPod
      BANNER_RQD_WIDTH: 300,    // if you change it, keep iaAd.css in synch!
      BANNER_RQD_HEIGHT: 50,
      SPLASH_RQD_WIDTH: 320,
      SPLASH_RQD_HEIGHT: 480,
      IS_MOBILE_WEB: true,
      IS_ORMMA_SUPPORT: false,
      IS_MRAID_SUPPORT: false,
      IS_INTERSTITIAL_AD: false,
      CATEGORY: "Music",
      AGE: "",
      GENDER: "",
      KEYWORDS: "Rock,Pop,Jazz,Blues",
      DEFAULT_GPS_COORDINATES: "40.41694,-3.70081",  // Madrid
      DEFAULT_LOCATION: "ES",                        // Spain
      MOBILE_NETWORK_CODE: "",
      MOBILE_COUNTRY_CODE: "",
      NETWORK: ""                                    // 3G or WIFI
   };
   var params = (
      "&fs=" + ((opts.IS_INTERSTITIAL_AD)? "true" : "false") +
      "&po=" + opts.PORTAL +
      "&c=" + opts.CATEGORY +
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
      "&k=" + encodeURIComponent(opts.KEYWORDS) +
      "&w=" + window.innerWidth +
      "&h=" + window.innerHeight +
      "&mnc=" + opts.MOBILE_NETWORK_CODE +
      "&mcc=" + opts.MOBILE_COUNTRY_CODE +
      "&nt=" + opts.NETWORK
   );
   var gpsCoordinates = encodeURIComponent(opts.DEFAULT_GPS_COORDINATES);
   var location = encodeURIComponent(opts.DEFAULT_LOCATION);

   var getIaAd = function(targetElt, width, height) {
      issueRequest(
         "GET", "getIaAd",
         params +
         "&l=" + location +
         "&lg=" + gpsCoordinates +
         "&rw=" + width +
         "&rh=" + height,
         function(htmlAd) {
            targetElt.innerHTML = htmlAd;
            if (doNavigate) {
               targetElt.querySelector('a').onclick = function() {
                  hide();
                  doNavigate(this.href);
                  return false;
               };
            }
         }
      );
   };

   var show = function(width, height, role) {
//Fx1 eltFrame.style.visibility = "hidden";
//Fx1 eltFrame.style.opacity = "0";
      eltFrame.style.opacity = "1";    //Fx1
      eltBtnHide.style.opacity = "1";  //Fx1
//Fx1 eltFrame.onload = function() {
//Fx1    this.style.visibility = "";
//Fx1    this.style.opacity = "1";
//Fx1    eltBtnHide.style.opacity = "1";
//Fx1 }
      eltFrame.setAttribute("role", role);
      eltSection.style.display="block";
      getIaAd(eltFrame, width, height);
   };

   var hide = function() {
      eltSection.style.display = "none";
      eltBtnHide.style.display = "none";
   };

   eltBtnHide.onclick = function() {
      hide();
      if (doNavigate) doNavigate("");
   };
   eltSection.appendChild(eltFrame);
   eltSection.appendChild(eltBtnHide);

   if (navigator.geolocation) {
      navigator.geolocation.getCurrentPosition(
         function(position) {   // OK!
            var coords = position['coords'];
            var latitude = coords['latitude'];
            var longitude = coords['longitude'];
            gpsCoordinates = encodeURIComponent(latitude + "," + longitude);
            location = ""; // better to let the sever guessit from the coords.
            var xhr = new XMLHttpRequest();
            xhr.open(
               "GET",
               "http://maps.googleapis.com/maps/api/geocode/json?latlng=" +
               latitude + "," + longitude + "&sensor=true",
               true
            );
            xhr.onreadystatechange = function () {
               if (
                  (this.readyState === 4) &&
                  ((this.status === 200) || (this.status === 0))
               ) {
                  var loc = "";
                  for (
                     var i=0,
                     c=JSON.parse(this.responseText).results[0]['address_components'],
                     max=c.length;
                     i < max;
                     ++i
                  ) {
                     var component = c[i];
                     for (
                        var j=0, t=component.types, maxJ=t.length;
                        j < maxJ;
                        ++j
                     ) {
                        var type = t[j];
                        if ((type === "locality") || (type === "country")) {
                           if (loc.length > 0) loc += ",";
                           loc += component['long_name'];
                           break;
                        }
                     }
                  }
                  location = encodeURIComponent(loc);
               }
            };
            xhr.send();
         },
         function(error) {
//*/        alert("Error " + error.code + ": " + error.message);
         },
         {
            enableHighAccuracy: false,
            timeout: 300000,       // allow 5 minutes for searching
            maximumAge: 86400000   // position found last day is ok
         }
      );
   }
   return {
      hide: hide,
      showBanner: function() {
         eltBtnHide.style.display = "none";
         show(opts.BANNER_RQD_WIDTH, opts.BANNER_RQD_HEIGHT, "banner");
         timeoutId = setTimeout(hide, 15000);
      },
      showSplash: function() {
         clearTimeout(timeoutId);
         eltBtnHide.style.opacity = "0";
         eltBtnHide.style.display = "block";
         show(opts.SPLASH_RQD_WIDTH, opts.SPLASH_RQD_HEIGHT, "complementary");
      },
      refreshBannerIn: function(customEltFrame) {
         getIaAd(customEltFrame, opts.BANNER_RQD_WIDTH, opts.BANNER_RQD_HEIGHT);
      }
   };
};

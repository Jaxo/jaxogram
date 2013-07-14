/*
| States:
| 0 IDLE
| 1 INSTALLED
| 2 FORIOS
| 3 FAILED
| 4 UNSUPPORTED
*/

function setInstallButton(buttonId) {
   var buttonElt = document.getElementById(buttonId);
   dispatcher.on(
      "z_install_changed",
      function action(state, info) {
         buttonElt.style.display = (
            (state == 0 /* IDLE */)? "table-cell" : "none"
         );
         if (state == 2 /* FORIOS */) {
            alert(i18n("z_safariInstall"));
            // navigator.platform.toLowerCase());
         }
         if (state == 3 /* FAILED */) {
            alert(
              i18n("z_installFailure", info.name) +
              (info.message? ("\n" + info.message) : "")
            );
         }
      }
   );
   var install = new Install();
   buttonElt.addEventListener("click", function() { install.doIt(); });
}

function Install() {
   this.doIt = function() {};
   if (navigator.mozApps) {
      var request = navigator.mozApps.getSelf();
      var that = this;
      request.onsuccess = function () {
         if (
            request.result
            // work-around for a bug in Firefox OS Desktop
            || (
               (window.location.protocol === "http:") &&
               !window.locationbar.visible
            )
         ) {
            // if  we already run as OWA, then nothing to be done
            dispatcher.post("z_install_changed", 1 /* INSTALLED */, request.result);
         }else {
            // we are not running as OWA.
            // Don't bother.  Assume this app was not installed.
            that.doIt = mozInstall;
            dispatcher.post("z_install_changed", 0 /* IDLE */);
         }
      };
      request.onerror = function() {
         dispatcher.post("z_install_changed", 3 /* FAILED */, req1.error);
      };
   }else if ((typeof chrome !== "undefined") && chrome.webstore && chrome.app) {
      if (chrome.app.isInstalled) {
         dispatcher.post("z_install_changed", 1 /* INSTALLED */);
      }else {
         this.doIt = function() {
            chrome.webstore.install(
               null,
               function() {
                  dispatcher.post("z_install_changed", 1 /* INSTALLED */);
               },
               function(error) {
                  dispatcher.post("z_install_changed", 3 /* FAILED */, error);
               }
            );
         };
         dispatcher.post("z_install_changed", 0 /* IDLE */);
      }
   }else if (typeof window.navigator.standalone !== "undefined") {
      if (window.navigator.standalone) {
         dispatcher.post("z_install_changed", 1 /* INSTALLED */);
      }else {
         /*
         | Right now, just asks that something show a UI element mentioning
         | how to install using Safari's "Add to Home Screen" button.
         */
         this.doIt = function() {
            dispatcher.post("z_install_changed", 2 /* FORIOS */);
         };
         dispatcher.post("z_install_changed", 0 /* IDLE */);
      }
   }else {
      dispatcher.post("z_install_changed", 4 /* UNSUPPORTED */);
   }

   function mozInstall() {
      try {
         /*
         | we are not running as OWA, but this does NOT mean that the app
         | (or another app from same origin) is not installed...
         | we do not want REINSTALL_FORBIDDEN
         */
         var here = window.location;
         var there = document.createElement('a');
         var req2 = navigator.mozApps.getInstalled();
         req2.onsuccess = function() {
            var max = req2.result.length;
            var apps = req2.result;
            for (var i=0; i < max; ++i) {
               there.href = apps[i].manifestURL;
               if (
                  (here.hostname == there.hostname) &&
                  (here.port == there.port) &&
                  (here.protocol == there.protocol)
               ) {
                  var manifest = apps[i].manifest;
                  dispatcher.post(
                     "z_install_changed", 3 /* FAILED */,
                     {
                        "name": "SAME_ORIGIN_CONFLICT",
                        "message": i18n(
                           "z_installConflict",
                           manifest.name,
                           manifest.version,
                           manifest.description,
                           manifest.developer.name,
                           i18nDate(apps[i].installTime),
                           i18nDate(apps[i].lastUpdateCheck)
                        )
                     }
                  );
                  /*
                  Instructions for removing an App on Ubuntu:
                  cd ~/.local/share/applications
                  rm owa-http\;localhost\;8888.desktop
                  cd ~/.mozilla/firefox/jxyjgc2f.default/webapps
                  rm -r \{98b7d4fa-7b08-43ec-8ac7-db0794611942}
                  ** and remove the entry in webapps.json **
                  cd ~
                  rm -r  .http\;localhost\;8888/
                  */

                  // var req4 = apps[i].checkForUpdate();
                  // req4.onsuccess = function() { alert("Success!"); };
                  // req4.onerror = function() { alert("Failure " + req3.error.name); }
                  return;
               }
            }
            // OK.  This app is brand new.  Try installing it.
            var req3 = navigator.mozApps.install(
               here.href.substring(0, here.href.lastIndexOf("/")) +
               "/hosted_manifest.webapp"
            );
            req3.onsuccess = function() {
               dispatcher.post("z_install_changed", 1 /* INSTALLED */, req3.result);
            };
            req3.onerror = function() {
               dispatcher.post("z_install_changed", 3 /* FAILED */, req3.error);
            };
         };
         req2.onerror = function() {
             dispatcher.post("z_install_changed", 3 /* FAILED */, req2.error);
         };
      }catch (error) {
         dispatcher.post("z_install_changed", 3 /* FAILED */, error);
      }
   }
}


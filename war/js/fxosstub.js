function Install() {
   if (navigator.mozApps) {
      var that = this;
      var request = navigator.mozApps.getSelf();
      request.onsuccess = function () {
         if (this.result) {
            dispatcher.post(
               "z_install_changed", "z_installed", this.result.manifest.version
            );
         }else {
            that.installUrl = (
               location.href.substring(0, location.href.lastIndexOf("/")) +
               "/hosted_manifest.webapp"
            );
            that.doIt = function() {
               //*/ alert("Install from " + that.installUrl);
               try {
                  var req2 = navigator.mozApps.install(that.installUrl);
                  req2.onsuccess = function(data) {
                     dispatcher.post(
                        "z_install_changed",
                        "z_installed",
                        req2.result.manifest.version
                     );
                     //*/ alert("Bingo!");
                  };
                  req2.onerror = function() {
                     dispatcher.post("z_install_changed", "z_failed", this.error);
                  };
               }catch (error) {
                  dispatcher.post("z_install_changed", "z_failed", error);
               }
            };
            dispatcher.post("z_install_changed", "z_uninstalled");
         }
      };
      request.onerror = function(error) {
         dispatcher.post("z_install_changed", "z_failed", error);
      };
   }else if ((typeof chrome !== "undefined") && chrome.webstore && chrome.app) {
      if (chrome.app.isInstalled) {
         dispatcher.post("z_install_changed", "z_installed");
      }else {
         this.doIt = function() {
            chrome.webstore.install(
               null,
               function() {
                  dispatcher.post("z_install_changed", "z_installed");
               },
               function(error) {
                  dispatcher.post("z_install_changed", "z_failed", error);
               }
            );
         };
         dispatcher.post("z_install_changed", "z_uninstalled");
      }
   }else if (typeof window.navigator.standalone !== "undefined") {
      if (window.navigator.standalone) {
         dispatcher.post("z_install_changed", "z_installed");
      }else {
         /*
         | Right now, just asks that something show a UI element mentioning
         | how to install using Safari's "Add to Home Screen" button.
         */
         this.doIt = function() {
            dispatcher.post("z_install_forIOS", navigator.platform.toLowerCase());
         };
         dispatcher.post("z_install_changed", "z_uninstalled");
      }
   }else {
      dispatcher.post("z_install_changed", "z_unsupported");
   }
   return this;
}

function setInstallButton(buttonId) {
   var buttonElt = document.getElementById(buttonId);
   dispatcher.on(
      "z_install_changed",
      function action(state) {
         buttonElt.style.display = (
            (state == "z_uninstalled")? "table-cell" : "none"
         );
         if (state == "z_failed") {
            alert(i18n("z_installFailure"));
         }
      }
   );
   dispatcher.on(
      "z_install_forIOS",
      function action(state) {
         buttonElt.style.display = "none";
         alert(i18n("z_safariInstall"));
      }
   );
   var install = new Install();
   buttonElt.addEventListener(
      "click", function() { install.doIt(); }
   );
}

function toggleSidebarView() {
   expandSidebarView(0);
}

function expandSidebarView(v) { // -1: collapse, +1: expand, 0: toggle
   var btn = document.getElementById('p01');
   var drawer = document.querySelector(".drawer");
   var sidebar = document.querySelector(".sidebar");
   var expanded = (sidebar.getAttribute("aria-expanded") === "true");
   if (!expanded && (v >= 0)) {
      btn.setAttribute("role", "back");
      drawer.setAttribute("aria-shrunk", "true");
      sidebar.setAttribute("aria-expanded", "true");
   }else if (v <= 0) {
      btn.setAttribute("role", "menu");
      drawer.setAttribute("aria-shrunk", "false");
      sidebar.setAttribute("aria-expanded", "false");
   }
}

function resetSidebarButton() {
   var btn = document.getElementById('p01');
   if (document.querySelector(".sidebar").getAttribute("aria-expanded") === "true") {
      btn.setAttribute("role", "back");
   }else {
      btn.setAttribute("role", "menu");
   }
}

function getAttribute(node, attrName) {  // helper
   var attrs = node.attributes;
   if ((attrs != null) && (attrs[attrName] != null)) {
      return attrs[attrName].value;
   }else {
      return null;
   }
}

function menuListClicked(event) {
   var liElt = getRealTarget(event);
   if (liElt == null) return;
   if (liElt.getAttribute("role") == "listbox") {
      var ulChildElt = liElt.getElementsByTagName("UL")[0];
      if (ulChildElt != null) {     // defense!
         var afterTransed;
         if (liElt.getAttribute("aria-expanded") == "true") {
            afterTransed = function() {
               liElt.removeAttribute("aria-expanded");
            }
            ulChildElt.style.height = "0rem";
         }else {
            afterTransed = function() {
               liElt.setAttribute("aria-expanded", "true");
            }
            ulChildElt.style.height = (
               ulChildElt.firstElementChild.offsetHeight *
               ulChildElt.childElementCount
            ) + "px";
         }
         ulChildElt.addEventListener(
            "transitionend",
            function() {
               afterTransed();
               this.removeEventListener("transitionend", arguments.callee, true);
            },
            true
         );
      }
   }else if (getAttribute(liElt.parentNode, "role") !=="radiogroup") {
      if (liElt.getAttribute("aria-selected") == "true") {
         // however for lambda lists, it deselects the item
         liElt.removeAttribute("aria-selected");
      }else {
         liElt.setAttribute("aria-selected", "true");
         ownedId = getAttribute(liElt, "aria-owns");
         if (ownedId != null) {
            document.getElementById(ownedId).setAttribute("aria-expanded", "true");
         }
      }
   }
}

function radioClicked(event) {
   var liElt = getRealTarget(event);
   // selecting a selected item in a radiogroup does nothing..
   if ((liElt == null) || (liElt.getAttribute("aria-selected") == "true")) return;
   // selecting a new item in a radiogroup deselects its siblings
   var siblings = liElt.parentNode.childNodes;
   for (var i=0; i < siblings.length; ++i) {
      var sib = siblings[i];
      if (getAttribute(sib, "aria-selected") == "true") {
         ownedId = getAttribute(sib, "aria-owns");
         if (ownedId != null) {
            document.getElementById(ownedId).setAttribute("aria-expanded", "false");
         }
         sib.attributes.removeNamedItem("aria-selected");
      }
   }
   liElt.setAttribute("aria-selected", "true");
   ownedId = getAttribute(liElt, "aria-owns");
   if (ownedId != null) {
      document.getElementById(ownedId).setAttribute("aria-expanded", "true");
   }
}

function getRealTarget(clickedEvent) {
   /*
   | TEMPORARY fix?
   | I am interested in list items, direct children of UL's, or TR's),
   | The list items descendants (IMG, SPAN, etc...) are phony.
   | I should probably add "role=listitem" for all such items.
   */
   var elt = clickedEvent.target;      // the item that was clicked
   while ((elt != null) && (elt.nodeName != "TD") && (elt.nodeName != "LI")) {
      elt = elt.parentNode;
   }
   return elt;
}

function expandPage(id) {
   var elt = document.getElementById(id);
   var siblings = elt.parentNode.children;
   for (var i=0; i < siblings.length; ++i) {
      var sib = siblings[i];
      if (sib != elt) {
         sib.setAttribute("aria-expanded", "false");
      }
   }
   elt.setAttribute("aria-expanded", "true");
}

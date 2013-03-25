function whenShareLoaded() {
   var loc = window.location;
   if (loc.protocol !== "app:") {
      var host = loc.host;
      if (host.indexOf("appspot") >= 0) {              // appspot default, or versioned
         server_url = "http://" + host + "/jaxogram";
      }else {                                          // "http://localhost:8888/", or
         server_url = "http://localhost:8888/jaxogram" // "http://ottokar/jaxogram/index.html"
      }
   }
   if (server_url !== "http://jaxogram.appspot.com/jaxogram") {
      simpleMsg("warning", i18n("testMode", server_url));
   }
   createDispatcher();
   users = new JgUsers();

   navigator.mozApps.getSelf().onsuccess = function() {
      if (this.result) {
         var version = this.result.manifest.version;
         if (version) {
            document.querySelector("header h1 small").textContent = version;
         }
      }
   }
   setNavigateButton = setUploadFromShareButton;

   // Listeners
   document.getElementById("sidebarMenu").onclick = menuListClicked;
   document.getElementById("jgUsersAid").onclick = listAlbums;
   document.getElementById("changeLanguage").onclick = changeLanguage;

   var dfltLocale = navigator.language || navigator.userLanguage;
   document.getElementById('usedLang').textContent = i18n(dfltLocale);
   document.getElementById(dfltLocale).setAttribute("aria-selected", "true");
   translateBody(dfltLocale);
   document.getElementById("jgUsersIn").style.display = "none";
   document.getElementById("jgUsersAid").style.display = "none";
   document.getElementById("footerTable").parentElement.style.display = "none";

   try {
      navigator.mozSetMessageHandler(
         "activity",
         function(issuer) {
            if (!users.hasSome()) {
               authorize();
            }else {
               formatUsersList(true);
            }
            document.getElementById("btnMain").onclick = function() {
               issuer.postResult("shared");
            }
            document.getElementById("uploadFromShare").onclick = function(event) {
               event.stopPropagation();
               uploadFromShare(issuer);
            }
         }
      );
   }catch (error) {
      simpleMsg("Error", error);
   }
};

function setUploadFromShareButton() {   // aka setNavigateButton
   var elt = document.getElementById("uploadFromShare");
   if (!users.hasSome() || networks.every(
         function(network) {
            if (network.name !== users.getNet()) {
               return true;    // pursue...
            }else {
               elt.textContent = i18n(elt.id, network.name);
               elt.style.display = "";
               return false;
            }
         }
      )
   ) {
      elt.style.display = "none";
   }
}

function uploadFromShare(issuer) {
   if (!users.hasSome()) {
      formatUsersList(true);
   }else {
      var albumId = isAlbumIdRequired()? users.getAlbumId() : "NoNeedFor";
      if (!albumId) {
         dispatcher.on(
            "albumsListed",
            function action(albumsCount) {
               dispatcher.off("albumsListed", action);
               if (albumsCount > 0) {
                  simpleMsg("warning", i18n("selectOrCreateAlbum"));
               }else {
                  createAlbum(false);
               }
            }
         );
         // show the appropriate panel for selecting the default album
         document.getElementById("jgUsersAid").click();
      }else {
         uploadShared(issuer, albumId);
      }
   }
}

function uploadShared(issuer, albumId) {
   var data = issuer.source.data;
   var blobs = data.blobs;
   var filenames = data.filenames;
   blobs.forEach(
      function(blob, index) {
         // var filename = filenames[index];
         issueRequest(
            "POST",
            "postImageData&NET=" + users.getNet() +
            "&AID=" + albumId,
            blob,
            function(val) {     // whenDone
               issuer.postResult("shared");
            },
            function(rc, val) { // whenFailed
               simpleMsg("error", "RC: " + rc);
               // issuer.postResult("shared");
            },
            "image/jpeg"
         );
      }
   );
}

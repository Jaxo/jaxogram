var pendingPhotos = [];  // array of blobs or files
var upldPhotosCount = 0;
var users;
// var server_url = "http://jaxogram.appspot.com/jaxogram";
// -- only for our internal testing --
var server_url = "http://12.jaxogram.appspot.com/jaxogram"; // FIXME
// var server_url = "http://localhost:8888/jaxogram";
var oauthNetwork = {
   name: "oauth",
   url: null,
   win: null,
   key: "0",
   timer: null
}
var networks = [
   {
      name: "orkut",
      url: "http://www.orkut.com",
      win: null
   },{
      name: "flickr",
      url: "http://www.flickr.com",
      win: null
   },{
      name: "picasa",
      url: "http://picasa.goggle.com",
      win: null
   },{
      name: "twitter",
      url: "http://www.twitter.com",
      win: null
   },{
      name: "twitpic",
      url: "http://mobile.twitpic.com",
      win: null
   },{
      name: "facebook",
      url: "http://www.facebook.com",
      win: null
   }
];

window.onload = function() {
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
   // users.cleanUp();
   // users.destroy();
   var params = getQueryParams();

   if (params.OP === "backCall") {
      /*
      | this occurs for non-packaged applications only:
      | b/c packaged app have no origin, can not appear as referer...
      | so there is no way to call them back from the external world.
      */
      registerUser(params.VRF, params.NET);
   }
   dispatcher.on(
      "install_changed",
      function action(state, version) {
         if (
            (state === "uninstalled") && !users.hasSome() &&
            (params.OP !== "backCall") &&
            (params.OP !== "share")
         ) {
            confirmMsg(
               i18n('betterInstall'),
               function() { document.getElementById("btnInstall").click(); }
            );
         }else if ((state === "installed") && version) {
            document.querySelector("header h1 small").textContent = version;
         }
      }
   );

   setInstallButton("btnInstall");
   window.addEventListener("resize", fitImages, false);
   fitImages();

   // Listeners
   document.getElementById("btnMain").onclick = toggleSidebarView;
   setBuyButton();
   document.querySelector(".menuList").onclick = menuListClicked;
   document.getElementById("mn_albums").onclick = listAlbums;
   document.getElementById("changeLanguage").onclick = changeLanguage;
   // document.getElementById("footerTable").onclick = function() { expandSidebarView(-1); };
   document.getElementById("pickPhoto").onclick = pickPhoto;
   document.getElementById("uploadPhoto").onclick = uploadPhoto;
   document.getElementById("cancelPhoto").onclick = function() {
      pendingPhotos.shift();
      uploadPhotos();
   };
   document.getElementById("logins").onclick = function(event) {
      changeLogin(this, event);
   };
   // document.getElementById("mn_albums").style.display = "none";
   document.getElementById("footerRow2").style.display = "none";
   var dfltLocale = navigator.language || navigator.userLanguage;
   translateBody(dfltLocale);
   formatUsersList(true);
   document.getElementById('usedLang').textContent = i18n(dfltLocale);
   document.getElementById(dfltLocale).setAttribute("aria-selected", "true");

   var eltMain = document.getElementById("corepane");
   new GestureDetector(eltMain).startDetecting();
   eltMain.addEventListener(
      "swipe",
      function(e) {
         // alert("Swipe:" + "\n start: " + detail.start + "\n end: " + detail.end + "\n dx: " + detail.dx + "\n dy: " + detail.dy + "\n dt: " + detail.dt + "\n vx: " + detail.vx + "\n vy: " + detail.vy + "\n direction: " + detail.direction + "\n angle: " + detail.angle);
         var direction = e.detail.direction;
         if (direction === 'right') {
            expandSidebarView(1);
         }else if (direction === 'left') {
            expandSidebarView(-1);
         }
      }
   );
   if (navigator.mozSetMessageHandler) {
      navigator.mozSetMessageHandler(
         "activity",
         function(issuer) {
            blobs = issuer.source.data.blobs;
            for (var i=0; i < blobs.length; ++i) {
               pendingPhotos.push(blobs[i]);
            }
            uploadPhotos();
         }
      );
   }
};

function onNetworkChange()
{
   if (users.hasSome()) {
      var networkName = users.getNet();
      var netImage = "../images/" + networkName + "Logo.png";
      var connectTo = document.getElementById("connectTo");
      for (var i=0, max = networks.length; i < max; ++i) {
         var network = networks[i];
         if (network.name === networkName) {
            connectTo.onclick = function(event) {
               event.stopPropagation();
               browseTo(network);
            };
            break;
         }
      }
      connectTo.style.display = "";
      document.getElementById("mn_userName").textContent = users.getUserName();
      document.getElementById("p2_userName").textContent = users.getUserName();
      document.getElementById("p2_userImage").src = users.getImageUrl();
      document.getElementById("mn_netImage").src = netImage;
      document.getElementById("p0_netImage").src = netImage;
      document.getElementById("p2_netImage").src = netImage;
      document.getElementById("p2_userScreenName").textContent = users.getScreenName();
   }else {
      document.getElementById("mn_user").style.display = "none";
      document.getElementById("p2_userName").textContent = i18n("noNetwork");
      document.getElementById("p2_userImage").src = "";
      document.getElementById("p0_netImage").src = "";
      document.getElementById("p2_netImage").src = "";
      document.getElementById("connectTo").style.display = "none";
   }
}

function formatUsersList(isUserRequired) {
   resetAlbumsList();
   if (users.hasSome()) {
      var elt;
      var itmElt;
      var ulElt = document.getElementById("logins");
      while (ulElt.hasChildNodes()) {
         ulElt.removeChild(ulElt.lastChild);
      }
      itmElt = document.createElement("LI");
      itmElt.className = "i18n";
      itmElt.id = "newLogin";
      itmElt.onclick = function(event) {
         authorize();
         event.stopPropagation();
      };
      itmElt.appendChild(document.createTextNode(i18n("newLogin")));
      ulElt.appendChild(itmElt);
      users.forEach(
         function(name, pass, albumTitle, isSelected, net) {
            var imgElt = document.createElement("IMG");
            itmElt = document.createElement("LI");
            var spanElt = document.createElement("SPAN");
            if (isSelected) itmElt.setAttribute("aria-selected", "true");
            imgElt.src = "../images/" + net + "SmallLogo.png";
            spanElt.setAttribute("role", "trasher");
            itmElt.appendChild(spanElt);
            itmElt.appendChild(imgElt);
            itmElt.appendChild(document.createTextNode(name));
            ulElt.appendChild(itmElt);
         }
      );
      elt = document.getElementById("mn_user");
      elt.setAttribute("aria-expanded", "false");
      elt.appendChild(ulElt);
      elt.style.display = "";
      tellAccessPass();
   }else {
      document.getElementById("mn_user").style.display = "none";
      if (isUserRequired) {
         authorize();
      }
   }
   onNetworkChange();
}

function isAlbumIdRequired() {
   return (
      users.hasSome() &&
      (users.getNet() !== "twitter") &&
      (users.getNet() !== "twitpic") &&
      (users.getNet() !== "flickr")
   );
}

function resetAlbumsList() {
   var albums = document.getElementById("mn_albums");
   while (albums.hasChildNodes()) albums.removeChild(albums.lastChild);
   if (!isAlbumIdRequired()) {
      albums.style.display = "none";
      albums.setAttribute("aria-expanded", "false");
   }else {
      var elt;
      var smallElt;
      var italicElt;
      var ulElt;

      smallElt = document.createElement("SMALL");
      smallElt.className = "i18n";
      smallElt.id = "photoAlbum";
      smallElt.appendChild(document.createTextNode(i18n("photoAlbum")));
      italicElt = document.createElement("I");
      italicElt.id = "albumTitle";
      if (users.getAlbumTitle()) {
         italicElt.textContent = users.getAlbumTitle();
      }else {
         italicElt.className = "i18n";
         italicElt.appendChild(document.createTextNode(i18n("albumTitle")));
      }
      ulElt = document.createElement("UL");
      ulElt.id = "albumList";
      ulElt.setAttribute("role", "radiogroup");
      ulElt.onclick = function(event) { changeAlbum(this, event); };

      albums.appendChild(smallElt);
      albums.appendChild(document.createElement("BR"));
      albums.appendChild(italicElt);
      albums.appendChild(ulElt);
      albums.style.display = "";
   }
   if (users.hasSome()) uploadPhotos(); // if any photo are queued
}

function formatAlbumsList(albums, elt) {  // elt is the UL id='albumList'
   var selAlbumId = users.getAlbumId();
   var selAlbumTitle = users.getAlbumTitle();
   var isSelAlbumOK = false;
   var liElt;

   while (elt.hasChildNodes()) {
      elt.removeChild(elt.lastChild);
   }
   liElt = document.createElement("LI");
   liElt.className = "i18n";
   liElt.id = "newAlbum";
   liElt.onclick = function(event) {
      createAlbum(true);
      event.stopPropagation();
   };
   liElt.appendChild(document.createTextNode(i18n('newAlbum')));
   elt.appendChild(liElt);
   for (var i=0, max=albums.length; i < max; ++i) {
      var album = albums[i];
      var title = album.title;
      var description = album.description;
      if (!title || (title.length === 0)) title = "no title";
      if (!description || (description.length === 0)) description = "";

      var imgElt = document.createElement("IMG");
      if (album.thumbnailUrl && (album.thumbnailUrl.length !== 0)) {
         imgElt.src = album.thumbnailUrl;
      }else {
         imgElt.src = "../images/unknSmall.png";
      }
      var spanElt = document.createElement("SPAN");
      spanElt.appendChild(document.createTextNode(title));
      var smallElt = document.createElement("SMALL");
      smallElt.appendChild(document.createTextNode(description));
      var divElt = document.createElement("DIV");
      divElt.appendChild(spanElt);
      divElt.appendChild(document.createElement("BR"));
      divElt.appendChild(smallElt);

      liElt = document.createElement("LI");
      liElt.id = album.id;
      if (selAlbumId === album.id) {
         liElt.setAttribute("aria-selected", "true");
         isSelAlbumOK = true;
      }
      liElt.appendChild(imgElt);
      liElt.appendChild(divElt);
      elt.appendChild(liElt);
   }
   var albumTitleElt = document.getElementById('albumTitle');
   if (isSelAlbumOK) {
      albumTitleElt.textContent = selAlbumTitle;
      albumTitleElt.removeAttribute("class");  // no more i18n'ed
   }else {
      /*
      | Sanity check.
      | We didn't find the saved album ID in the list...
      | May be was it removed?
      */
      // 1) Tell JgUsers that its album id is no more alive,
      users.setAlbum(null, null);
      // 2) Reflect this fact in the Photo Album title
      albumTitleElt.className = "i18n";
      albumTitleElt.textContent = i18n("albumTitle");
   }
   // uploadPhotos();
}

function changeAlbum(elt, event) {
   var albumTitleElt = document.getElementById('albumTitle');
   var liElt = getRealTarget(event);
   var albumTitle = liElt.getElementsByTagName("SPAN")[0].textContent;
   users.setAlbum(liElt.id, albumTitle);
   albumTitleElt.textContent = albumTitle;
   albumTitleElt.removeAttribute("class"); // no more i18n'ed  (except if 'none')
   uploadPhotos();
}

function changeLogin(elt, event) {
   var liElt = getRealTarget(event);
   if (liElt) {
      var index = -1;
      while (liElt=liElt.previousSibling) ++index;
      if (event.target.getAttribute("role") === "trasher") {
         var img = event.target.nextSibling.src;
         confirmMsg(
            i18n(
               "revokeAccess",
               event.target.nextSibling.nextSibling.textContent,
               img.substring(1+img.lastIndexOf("/"), img.lastIndexOf("SmallLogo"))
            ),
            function() {
               users.deleteUserAt(index);
               formatUsersList(false);
            }
         );
      }else {
         users.selectUserAt(index);
         resetAlbumsList();
         onNetworkChange();
         tellAccessPass();
      }
   }
}

function changeLanguage(event) {
   var clicked = event.target;
   translateBody(clicked.id);
   document.getElementById('usedLang').textContent = clicked.textContent;
}

function fitImages() {
   // workaround, until "object-fit:contain;" gets implemented
   var images = document.querySelectorAll(".imgbox img");
   for (var i=0; i < images.length; ++i) {
      var img = images[i];
      img.onload = function() { fitImage(this); }
      fitImage(img);
   }
}

function fitImage(img) {
   var elt = img.parentNode;
   if ((elt.offsetHeight * img.offsetWidth)<(img.offsetHeight * elt.offsetWidth)) {
      img.style.width = "";
      img.style.height = "100%";
   }else {
      img.style.width = "100%";
      img.style.height = "";
   }
}

function authorize() {
   var eltContainer = document.createElement("DIV");
   networks.forEach(
      function(network) {
         var name = network.name;
         var elt = document.createElement("IMG");
         elt.className = "buttonLike";
         elt.src= "images/" + name + "Logo.png";
         if (name === "picasa") {
            elt.onclick = authorizePicasa;
         }else {
            elt.onclick = function() { authorizeThruOAuth(name); }
         }
         eltContainer.appendChild(elt);
      }
   );
   showMsg("chooseNetwork", [eltContainer]);
}

function authorizePicasa() {
   hideMsg();
   var eltInp1 = makeInputField("login");
   var eltInp2 = makeInputField("passwd", "password");
   showMsg(
      "picasaLogin",
      [getInputFieldContainer(eltInp1), getInputFieldContainer(eltInp2)],
      function() {     // whenDone
         if ((eltInp1.value.length === 0) || (eltInp2.value.length === 0)) {
            shakeMsg();
         }else {
            hideMsg();
            var passwd = eltInp1.value + " " + eltInp2.value;
            issueRequest(
               "POST", "checkAccPss", passwd,
               function(userName) { // whenDone
                  hideMsg();
                  users.addUser(userName, passwd, "picasa");
                  formatUsersList(false);
               },
               function(rc, val) {  // whenFailed
                  simpleMsg("error", i18n("badLogin"));
               }
            );
         }
      }
   );
}

function authorizeThruOAuth(net) {
   hideMsg();
   // make a pseudo-random key )between 100000 and 200000
   oauthNetwork.key = (Math.floor(Math.random() * 100000) + 100000).toString();
   // obtain the URL at which the user will grant us access
   issueRequest(
      "GET", "getUrl", "&JXK=" + oauthNetwork.key + "&NET=" + net,
      function(oauthUrl) {        // whenDone
         oauthNetwork.url = oauthUrl;
//*/     console.error("browseTo..." + oauthNetwork.url);
         browseTo(oauthNetwork);  // open a new browser window
//*/     console.error("got a window " + oauthNetwork.win);
         oauthNetwork.win.addEventListener(
            "close",
            function(event) {
               oauthNetwork.win = null;
               clearTimeout(oauthNetwork.timer);
            },
            false
         );
         getVerifier();           // wait 'til it returns with the verifier
      },
      function(rc, val) {         // whenFailed
         simpleMsg("error", "authorize RC:" + rc + "\n" + val);
      }
   );
}

function browseTo(network) {
   if (!network.win || network.win.closed) {
//*/  console.error("attempt to open window...");
      network.win = window.open(network.url, network.name);
   }else {
      network.win.focus();
   }
}

/*
| wait until appspot obtained the verifier in its cache
*/
function getVerifier() {
   issueRequest(
      "GET", "getVerifier", "&JXK=" + oauthNetwork.key,
      function(val) {     // whenDone
         var win = oauthNetwork.win;
         if (win && !win.closed) {
            if (val === "???") {
               oauthNetwork.timer = setTimeout(getVerifier, 1000);
            }else {
               win.close();
               var obj = JSON.parse(val);
               registerUser(obj.VRF, obj.NET);
               formatUsersList(false);
            }
         }
      },
      function(rc, val) { // whenFailed
         var win = oauthNetwork.win;
         if (win && !win.closed) win.close();
         simpleMsg("error", "getVerifier RC:" + rc);
      }
   );
   document.getElementById("progresspane").style.visibility='hidden';
}

function registerUser(verifier, net) {
   issueRequest(
      "GET", "getAccPss",
      "&VRF=" + encodeURIComponent(verifier) + "&NET=" + net,
      function(val) {     // whenDone
         var obj = JSON.parse(val);
         // alert(dump(obj));
         users.addUser(
            obj.userName,
            obj.accessPass,
            net,
            obj.imageUrl,
            obj.screenName
         );
         formatUsersList(false);
      },
      function(rc, val) { // whenFailed
         simpleMsg("error", i18n("authDenied", val));
      }
   );
}

function tellAccessPass()
{
   issueRequest(
      "POST", "postAccPss", users.getAccessPass(),
      function(val) {},   // whenDone
      function(rc, val) { // whenFailed
         simpleMsg("error", "tellAccess RC: " + rc + "\n" + val);
      }
   );
}

function listAlbums(event) {
   if (this.getAttribute("aria-expanded") === "true") {
      if (!event.isTrusted) {    // a programatic click forces list refresh
         event.stopPropagation();
      }else {
         return;
      }
   }
   var ulChildElt = this.getElementsByTagName("UL")[0];
   if (ulChildElt !== null) {     // defense!
      issueRequestStd(
         'listAlbums',
         function(albums) {
            formatAlbumsList(albums, ulChildElt);
         }
      );
   }
}

function createAlbum(isDirect) {
   var eltInp1 = makeInputField("title");
   var eltInp2 = makeInputField("description");
   showMsg(
      isDirect? "createAlbumProlog1" : "createAlbumProlog2",
      [getInputFieldContainer(eltInp1), getInputFieldContainer(eltInp2)],
      function() {
         hideMsg();
         var what = (
            "createAlbum" +
            "&title=" + eltInp1.value.replace(/^\s+|\s+$/g,'') +
            "&descr=" + eltInp2.value.replace(/^\s+|\s+$/g,'')
         );
         issueRequestStd(
            what,
            function(albums) {
               var newAlbum = albums[0];
               users.setAlbum(newAlbum.id, newAlbum.title);
               formatAlbumsList(
                  albums,
                  document.getElementById("mn_albums").getElementsByTagName("UL")[0]
               );
            }
         );
      }
   );
}

function pickPhoto(event) {
   if (typeof MozActivity !== "undefined") {
      var a = new MozActivity({ name: "pick", data: {type: "image/jpeg"}});
      a.onsuccess = function(e) {
         pendingPhotos.push(a.result.blob);
         uploadPhotos();
      };
      a.onerror = function() {
         simpleMsg("error", i18n('pickImageError'));
      };
   }else {
      var elt = document.getElementById('upldFile');
      elt.onchange = function() {
         if (!this.files) {
            simpleMsg("error", i18n("noFileApiProp"));
         }else if (!this.files[0]) {
            simpleMsg("error", i18n("noFileSelected"));
         }else {
            for (var i=0; i < this.files.length; ++i) {
               pendingPhotos.push(this.files[i]);
            }
            uploadPhotos();
         }
      }
      elt.click();
   }
}

function finishUpload() {
   expandPage("p0"); // stop p2!
   document.getElementById("footerRow2").style.display = "none";
   document.getElementById("footerRow1").style.display = "";
   if (upldPhotosCount > 0) {
      simpleMsg("info", i18n('photosUploaded', upldPhotosCount));
      upldPhotosCount = 0;
   }
   // FIXME: if (issuer) issuer.postResult({result: "ok"});
}

function uploadPhotos() {
   if (pendingPhotos.length == 0) {
      finishUpload();
   }else {
      document.getElementById("footerRow1").style.display = "none";
      document.getElementById("footerRow2").style.display = "";

      // document.querySelector(".p2_user").classList.add("active");
      var countElt = document.getElementById("p2_msgCount");
      var textElt = document.getElementById("p2_msgText");
      textElt.value = "";
      var setCounter = function(event) {
         var remain = 116 - textElt.value.length;
         if (remain < 0) {
            textElt.value = textElt.value.substring(0, 116);
            remain = 116 - textElt.value.length;
         }
         countElt.textContent = remain;
         if (remain > 20) {
            countElt.style.color = "#9b9b9b";
         }else {
            countElt.style.color = "#C80000";
         }
         if (event && (event.keyCode === 13)) this.blur();
      };
      var imgData = pendingPhotos[0];
      var imgElt = document.createElement("IMG");
      var clrElt = document.getElementById("p2_clear");
      imgElt.src = URL.createObjectURL(imgData);
      // FIXME: imgElt.onload = function() { URL.revokeObjectURL(this.src); };
      imgElt.id = "p2_picture";
      var oldImgElt = document.getElementById("p2_picture");
      oldImgElt.parentNode.replaceChild(imgElt, oldImgElt);
      fitImage(imgElt);
      clrElt.style.visibility = "visible";
      clrElt.onclick = function() {
         imgElt.style.visibility = "hidden";
         this.style.visibility = "hidden";
      };
      textElt.addEventListener("keyup", setCounter, false);
      setCounter();
      expandPage("p2");
      isUploadable();
   }
}

function isUploadable() {
   if (!users.hasSome()) {
      formatUsersList(true);
      return false;
   }else if (isAlbumIdRequired() && (users.getAlbumId() == null)) {
      // show the appropriate panel for selecting an album
      var albumsPane = document.getElementById("mn_albums");
      expandSidebarView(1);
      if (albumsPane.getAttribute("aria-expanded") !== "true") {
         albumsPane.click();
      }else {
         var albums = document.getElementById("albumList").childNodes;
         var albumsCount = 0;
         for (var i=0, max=albums.length; i < max; ++i) {
            if (albums[i].nodeType != 3) ++albumsCount;
         }
         if (albumsCount > 0) {
            simpleMsg("warning", i18n("selectOrCreateAlbum"));
         }else {
            createAlbum(false);
         }
      }
      return false;
   }else {
      expandSidebarView(-1);
      return true;
   }
}

function uploadPhoto() {
   if (isUploadable()) {
      var imgData = pendingPhotos.shift();
      var formData = new FormData();
      formData.append("MAX_FILE_SIZE", "1000000");
   // formData.append("IMG", file.name.substr(-3));
      if (isAlbumIdRequired()) formData.append("AID", users.getAlbumId());
      formData.append("TIT", document.getElementById("p2_msgText").value);
      formData.append("upldFile", imgData);
      issueRequest(
         "POST",
         "postImageFile&NET=" + users.getNet(),
         formData,
         function(val) {        // whenDone
            try {
               var media = JSON.parse(val).entities.media[0];
               var idStr = media.id_str;
               var expandedUrl = media.expanded_url;
            }catch (error) {
            }
            /*
            Response:
               var res = JSON.parse(xhr.responseText);
               var id_str = res.entities.media[0].id_str;
               var ex_url = res.entities.media[0].expanded_url;
            */
            ++upldPhotosCount;
            uploadPhotos();
         },
         function(rc, val) {    // whenFailed
            finishUpload();
            // FIXME: if (issuer) issuer.postError(message);
         }
      );
   }
}

function issueRequestStd(what, whenDone) {
   if (!users.hasSome()) {
      formatUsersList(true);
   }else {
      issueRequest(
         "GET", what, "&NET=" + users.getNet(),
         function(val) {     // whenDone
            whenDone(JSON.parse(val));
         },
         function(rc, val) { // whenFailed
            dispatcher.clean();
            simpleMsg("error", what + " RC: " + rc + "\n" + val);
         }
      );
   }
}

function issueRequest(method, op, values, whenDone, whenFailed) {
   var query = "?OP=" + op + "&V=1";    // REST version #1
   if (method === "GET") query += values;
   var xhr = new XMLHttpRequest({mozSystem: true});
   if (xhr.withCredentials === undefined) {
      simpleMsg("error", "Sorry: can't do cross-site requests");
      return;
   }
   xhr.withCredentials = true;
   xhr.open(method, server_url + query, true);
   xhr.onreadystatechange = function () {
      if (this.readyState === 4) {
         document.getElementById("progresspane").style.visibility='hidden';
         if ((this.status === 200) || (this.status === 0)) {
            whenDone(this.responseText);
         }else {
            whenFailed(this.status, this.responseText);
         }
      }
   };
   document.getElementById("progresspane").style.visibility='visible';
   if (method === "GET") {
      xhr.send();
   }else {
      xhr.send(values);
   }
}

/*========== PURCHASE (beta) ============*/

function setBuyButton() {
        /*
        | DEBUG only.
        | For production:
        |    remove the setBuyButton function
        |    rename setRealBuyButton into setBuyButton
        |    (or change slash-start-star-slash to slash-slash-star-slash)
        |
        */
/**/    if (navigator.mozPay === "toto") { // undefined) {   // FIXME
/**/       // "granted", "1366018659077", "agpzfmpheG9ncmFtcgkLEgNQYXkYBgw"
/**/       document.getElementById("btnBuy").style.display = "none";
/**/    }else {
/**/       document.querySelector("header").insertAdjacentHTML(
/**/          "afterEnd",
/**/          "<DIV id='BT_Test' style='padding:1rem;margin:3rem;background-color:rgba(255, 255, 127, 0.8);position:absolute;z-index:100;font-size:1.5rem'>" +
/**/          "<DIV style='margin-bottom:2rem;font-size:1.8rem;padding:1rem;text-align:center;background-color:rgba(60,20,20,0.5);color:white'>Internal Test of the Buy Process</DIV>" +
/**/          "<SPAN id='BT_CurPay' style='color:red;font-size:1rem'></SPAN>" +
/**/          "<BR/>Issue new(?) User's Payment<BR/>" +
/**/          "<FORM name='BT_Form'>" +
/**/          "<INPUT type='radio' name='g1' value='1' checked>Granted</INPUT><BR/>" +
/**/          "<INPUT type='radio' name='g1' value='0'>Denied</INPUT><BR/>" +
/**/          "<HR/>" +
/**/          "Payment Provider... <BR/>" +
/**/          "<INPUT type='radio' name='g2' value='1' checked>answers &lt; 1mn</INPUT><BR/>" +
/**/          "<INPUT type='radio' name='g2' value='2'>does answer late</INPUT><BR/>" +
/**/          "<INPUT type='radio' name='g2' value='0'>does not answer</INPUT><BR/>" +
/**/          "<HR/>" +
/**/          "<BUTTON id='BT_Apply' style='width:8rem;margin:1rem;float:left'>Apply</BUTTON>" +
/**/          "<BUTTON id='BT_Skip' style='width:8rem;margin:1rem;float:right'>Skip</BUTTON>" +
/**/          "</FORM>" +
/**/          "</DIV>"
/**/       );
/**/       document.getElementById("BT_CurPay").innerHTML = (
/**/          users.getPayState() + " " +
/**/          i18nDate(users.getPayTime()) + "<BR>" +
/**/          users.getPayKey()
/**/       );
/**/       document.BT_Form.onsubmit = function() {
/**/          document.getElementById("BT_Test").style.visibility = "hidden";
/**/          return false;
/**/       };
/**/       document.getElementById("BT_Apply").onclick = function() {
/**/          var answered;
/**/          var granted;
/**/          users.deletePayment();
/**/          var g1 = document.BT_Form.g1;
/**/          for (var i=0; i < g1.length; ++i) {
/**/             if (g1[i].checked == true) {
/**/                granted = g1[i].value;
/**/                break;
/**/             }
/**/          }
/**/          var g2 = document.BT_Form.g2;
/**/          for (var i=0; i < g2.length; ++i) {
/**/             if (g2[i].checked == true) {
/**/                answered = g2[i].value;
/**/                break;
/**/             }
/**/          }
/**/          var elt = document.getElementById("btnBuy");
/**/          elt.style.display = "";
/**/          elt.onclick = function() {
/**/             purchase("&test=1"+ granted + answered);
/**/             onclick = setRealBuyButton();
/**/          }
/**/       }
/**/       document.getElementById("BT_Skip").onclick = setRealBuyButton;
/**/    }
/**/ }
/**/
/**/ function setRealBuyButton() {
   if ((navigator.mozPay !== undefined) && (users.getPayState() !== "granted")) {
      var elt = document.getElementById("btnBuy");
      elt.style.display = "";
      elt.onclick = function() { purchase("&test=111"); };
      // for marketplace, just do: elt.onclick = purchase;
   }
}

function purchase(test) {
   var elt = document.getElementById("btnBuy");
   elt.style.display = "none";
   if (!test) test = "";
   if (users.getPayState() === "pending") {
      getPayment(elt, users.getPayKey());
   }else {
      issueRequest(
         "GET", "purchase", test,
         function(jwt) { // whenDone
            /*
            | extract the productData from the returned JWT.
            | It is the stringized key (keyToString)
            | of the "Pay" kind entity in Google App Datastore
            */
            var ix = 1+jwt.indexOf('.');
var rfa = jwt.substring(ix, jwt.indexOf('.', ix));
var sgb = atob(rfa.replace('+','-').replace('/','_'));
var thc = JSON.parse(sgb);
var uid = thc.request.productData;
//          var uid = JSON.parse(
//             atob(
//                jwt.substring(ix, jwt.indexOf('.', ix)).
//                replace('+','-').replace('/','_')
//             )
//          ).request.productData;
            var req = navigator.mozPay([jwt]);
            req.onsuccess = function() { getPayment(elt, uid); };
            req.onerror = function() {  // mozPay failed
               simpleMsg("error", "Pay process" + this.error.name);
               elt.style.display = "";
            }
         },
         function(rc, val) {  // whenFailed (e.g.: we didn't generate the JWT)
            simpleMsg("error", "Payment request: server error\nRC:" + rc);
            elt.style.display = "";
         }
      );
   }
}

function getPayment(elt, uid) {
   issueRequest(
      "GET", "getPayment", "&PYK=" + uid,
      function(val) {     // whenDone
         pay = JSON.parse(val);
         var msg;
         if (pay.state === "granted") {
            msg = i18n("grantedPay");
         }else {
            elt.style.display = "";
            if (pay.state == "denied") {
               msg = i18n("deniedPay");
            }else {
               // assume pay.state is "pending"
               if (users.getPayState() === "pending") { // for the 2nd time
                  confirmMsg(
                     i18n("cancelPay", i18nDate(new Date(users.getPayTime()))),
                     function() { cancelPayment(elt, uid); }
                  );
                  return;
               }else {
                  msg = i18n("pendingPay");
               }
            }
         }
         users.writePayment(uid, pay);
         simpleMsg("info", msg);
      },
      function(rc, val) { // whenFailed
         simpleMsg("error", "Payment response: Server error\nRC:" + rc);
         elt.style.display = "";
      }
   );
   document.getElementById("progresspane").style.visibility='hidden';
}

function cancelPayment(elt, uid) {
   issueRequest(
      "GET", "cancelPayment", "&PYK=" + uid,
      function(val) {     // whenDone
         pay = JSON.parse(val);
         var msg;
         if (pay.state === "granted") {  // payment granted in the meanwhile
            msg = i18n("grantedPay");
         }else {
            elt.style.display = "";
            if (pay.state == "denied") { // the regular case
               msg = i18n("deniedPay");
            }else {
               msg = i18n("pendingPay"); // should not occur
            }
         }
         users.writePayment(uid, pay);
         simpleMsg("info", msg);
      },
      function(rc, val) { // whenFailed
         simpleMsg("error", "Payment not cancelled\nRC:" + rc);
         elt.style.display = "";
      }
   );
   document.getElementById("progresspane").style.visibility='hidden';
}

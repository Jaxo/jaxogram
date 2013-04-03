var pendingPhotos = [];  // array of blobs or files
var users;
var server_url = "http://jaxogram.appspot.com/jaxogram";
// -- only for our internal testing --
// var server_url = "http://11.jaxogram.appspot.com/jaxogram";
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
   document.querySelector(".menuList").onclick = menuListClicked;
   document.getElementById("jgUsersAid").onclick = listAlbums;
   document.getElementById("changeLanguage").onclick = changeLanguage;
   document.getElementById("footerTable").onclick = function() { expandSidebarView(-1); };
   document.getElementById("pickPhoto").onclick = pickPhoto;
   // document.getElementById("jgUsersAid").style.display = "none";
   document.getElementById("footerRow2").style.display = "none";

   var dfltLocale = navigator.language || navigator.userLanguage;
   translateBody(dfltLocale);
   formatUsersList(true);
   document.getElementById('usedLang').textContent = i18n(dfltLocale);
   document.getElementById(dfltLocale).setAttribute("aria-selected", "true");
// window.onerror = function(msg, url, linenumber){
//    alert('Error: ' + msg + '\nURL: ' + url + '\n@ line: ' + linenumber);
//    return true;
// }
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
         function() {
            blobs = issuer.source.data.blobs;
            for (var i=0; i < blobs.length; ++i) {
               pendingPhotos.push(blobs[i]);
            }
            startUpload();
         }
      );
   }
};

function setNetworkButtons() {
   var eltButton = document.getElementById("connectTo");
   if (!users.hasSome() || networks.every(
         function(network) {
            if (network.name !== users.getNet()) {
               return true;    // pursue...
            }else {
               var imgElt = document.createElement("IMG");
               imgElt.src = "images/" + network.name + "Logo.png";
               imgElt.style.width = "7rem";
               while (eltButton.hasChildNodes()) {
                  eltButton.removeChild(eltButton.lastChild);
               }
               eltButton.appendChild(imgElt);
               eltButton.onclick = function(event) {
                  event.stopPropagation();
                  browseTo(network);
               }
               eltButton.style.display = "";
               return false;
            }
         }
      )
   ) {
      eltButton.style.display = "none";
   }
}

function formatUsersList(isUserRequired) {
   resetAlbumsList();
   if (users.hasSome()) {
      var elt;
      var liElt;
      var smallElt;
      var imgNetElt;
      var italicElt;
      var ulElt = document.createElement("UL");
      ulElt.setAttribute("role", "radiogroup");
      ulElt.onclick = function(event) { changeLogin(this, event); };

      liElt = document.createElement("LI");
      liElt.className = "i18n";
      liElt.id = "newLogin";
      liElt.onclick = function(event) {
         authorize();
         event.stopPropagation();
      };
      liElt.appendChild(document.createTextNode(i18n("newLogin")));
      ulElt.appendChild(liElt);
      users.forEach(
         function(name, pass, albumTitle, isSelected, net) {
            var imgElt = document.createElement("IMG");
            var itmElt = document.createElement("LI");
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
      elt = document.getElementById("jgUsersIn");
      while (elt.hasChildNodes()) {
         elt.removeChild(elt.lastChild);
      }
      elt.setAttribute("aria-expanded", "false");
      smallElt = document.createElement("SMALL");
      smallElt.className = "i18n";
      smallElt.id = "loginAs";
      smallElt.appendChild(document.createTextNode(i18n("loginAs")));
      italicElt = document.createElement("I");
      italicElt.id = "jgUserName";
      italicElt.appendChild(document.createTextNode(users.getUserName()));
      imgNetElt = document.createElement("IMG");
      imgNetElt.id = "jgUserNet";
      imgNetElt.src = "../images/" + users.getNet() + "Logo.png";
      elt.appendChild(smallElt);
      elt.appendChild(document.createElement("BR"));
      elt.appendChild(imgNetElt);
      elt.appendChild(italicElt);
      elt.appendChild(ulElt);
      elt.style.display = "";
      tellAccessPass();
   }else {
      document.getElementById("jgUsersIn").style.display = "none";
      if (isUserRequired) {
         authorize();
      }
   }
   setNetworkButtons();
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
   var albums = document.getElementById("jgUsersAid");
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
   if (users.hasSome()) startUpload(); // if any photo are queued
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
   // startUpload();
}

function changeAlbum(elt, event) {
   var albumTitleElt = document.getElementById('albumTitle');
   var liElt = getRealTarget(event);
   var albumTitle = liElt.getElementsByTagName("SPAN")[0].textContent;
   users.setAlbum(liElt.id, albumTitle);
   albumTitleElt.textContent = albumTitle;
   albumTitleElt.removeAttribute("class"); // no more i18n'ed  (except if 'none')
   startUpload();
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
         document.getElementById('jgUserName').textContent = users.getUserName();
         document.getElementById('jgUserNet').src = "../images/" + users.getNet() + "Logo.png";
         resetAlbumsList();
         setNetworkButtons();
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
                  document.getElementById("jgUsersAid").getElementsByTagName("UL")[0]
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
         startUpload();
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
            startUpload();
         }
      }
      elt.click();
   }
}

function startUpload() {
   if (pendingPhotos.length == 0) {
      finishUpload();
   }else {
      document.getElementById("footerRow1").style.display = "none";
      document.getElementById("uploadPhoto").style.display = "none";
      document.getElementById("cancel").onclick = function() {
         pendingPhotos.shift();
         startUpload();
      }
      document.getElementById("footerRow2").style.display = "";
      if (!users.hasSome()) {
         formatUsersList(true);
      }else if (isAlbumIdRequired() && (users.getAlbumId() == null)) {
         // show the appropriate panel for selecting an album
         var albumsPane = document.getElementById("jgUsersAid");
         expandSidebarView(1);
         if (albumsPane.getAttribute("aria-expanded") !== "true") {
            albumsPane.click();
         }else {
            var albums = document.getElementById("albumList").childNodes;
            var albumsCount = 0;
            for (var i=0, max=albums.length; i < max; ++i)
            {
               if (albums[i].nodeType != 3) ++albumsCount;
            }
            if (albumsCount > 0) {
               simpleMsg("warning", i18n("selectOrCreateAlbum"));
            }else {
               createAlbum(false);
            }
         }
      }else {
         expandSidebarView(-1);
         document.getElementById("uploadPhoto").style.display = "";
         uploadPhotos();
      }
   }
}

function finishUpload() {
   expandPage("p0"); // stop p2!
   document.getElementById("footerRow2").style.display = "none";
   document.getElementById("footerRow1").style.display = "";
}

function uploadPhotos() {
   if (pendingPhotos.length == 0) {
      finishUpload();
      if (users.getAlbumId()) {
         simpleMsg("info", i18n('imageUploadedIn', users.getAlbumTitle()));
      }else {
         simpleMsg("info", i18n('imageUploaded'));
      }
      // if (issuer) issuer.postResult({result: "ok"});
   }else {
      document.getElementById("p2_userImage").src = users.getImageUrl();
      document.getElementById("p2_netImage").src = "images/" + users.getNet() + "Logo.png";
      document.getElementById("p2_userName").textContent = users.getUserName();
      document.getElementById("p2_userScreenName").textContent = "@" + users.getScreenName();
      // document.querySelector(".p2_user").classList.add("active");
      var countElt = document.getElementById("p2_msgCount");
      var textElt = document.getElementById("p2_msgText");
      var setCounter = function() {
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
      textElt.addEventListener("keyup", function() { setCounter(); }, false);
      setCounter();
      expandPage("p2");
      var uploadBtn = document.getElementById("uploadPhoto");
      uploadBtn.style.display = "";
      uploadBtn.onclick = function() {
         pendingPhotos.shift();
         var formData = new FormData();
         formData.append("MAX_FILE_SIZE", "1000000");
      // formData.append("IMG", file.name.substr(-3));
         if (isAlbumIdRequired()) formData.append("AID", users.getAlbumId());
      // formData.append("TIT", "my title");
      // formData.append("TXT", textElt.value);
         formData.append("upldFile", imgData);
         issueRequest(
            "POST",
            "postImageFile&NET=" + users.getNet(),
            formData,
            function(val) {        // whenDone
               uploadPhotos();
            },
            function(rc, val) {    // whenFailed
               finishUpload();
               // if (issuer) issuer.postError(message);
            }
         );
      }
      /*
      Response:
         var res = JSON.parse(xhr.responseText);
         var id_str = res.entities.media[0].id_str;
         var ex_url = res.entities.media[0].expanded_url;
      */
   };
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
      simpleMsg("error", "Sorry: your browser doesn't handle cross-site requests");
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


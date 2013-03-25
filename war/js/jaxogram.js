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
      url: "http://mobile.twitpic.com",
      win: null
   },{
      name: "facebook",
      url: "http://www.facebook.com",
      win: null
   }
];
var setNavigateButton = function() { alert("no navigation button"); };

function whenIndexLoaded() {
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
            (params.OP !== "backCall")
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
   fitImage(document.getElementById('photoImage'));
   window.addEventListener("resize", fitImages, false);
   setNavigateButton = setGoForItButton;

   // Listeners
   document.getElementById("btnMain").onclick = toggleSidebarView;
   document.getElementById("sidebarMenu").onclick = menuListClicked;
   document.getElementById("jgUsersAid").onclick = listAlbums;
   document.getElementById("changeLanguage").onclick = changeLanguage;
   document.getElementById("footerTable").onclick = function() { expandSidebarView(-1); };
   document.getElementById("pickAndUpload").onclick = pickAndUpload;
   // document.getElementById("whoAmI").onclick = whoAmI;

   var dfltLocale = navigator.language || navigator.userLanguage;
   formatUsersList(false);
   translateBody(dfltLocale);
   document.getElementById('usedLang').textContent = i18n(dfltLocale);
   document.getElementById(dfltLocale).setAttribute("aria-selected", "true");
// window.onerror = function(msg, url, linenumber){
//    alert('Error: ' + msg + '\nURL: ' + url + '\n@ line: ' + linenumber);
//    return true;
// }
   var eltMain = document.getElementById("main");
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
};

function setGoForItButton() {   // aka setNavigateButton
   var elt = document.getElementById("go4it");
   if (!users.hasSome() || networks.every(
         function(network) {
            if (network.name !== users.getNet()) {
               return true;    // pursue...
            }else {
               var imgElt = document.createElement("IMG");
               imgElt.src = "images/" + network.name + "Logo.png";
               imgElt.style.width = "7rem";
               while (elt.hasChildNodes()) {
                  elt.removeChild(elt.lastChild);
               }
               elt.appendChild(imgElt);
               elt.onclick = function(event) {
                  event.stopPropagation();
                  browseTo(network);
               }
               elt.style.display = "";
               return false;
            }
         }
      )
   ) {
      elt.style.display = "none";
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
      document.getElementById("footerTable").parentElement.style.display = "";
      tellAccessPass();
   }else {
      if (isUserRequired) {
         confirmMsg(
            i18n("requestAuth"),
            function() { authorize(); }
         );
      }else {
         var elt = document.getElementById("jgUsersIn");
         var btnElt = document.createElement("BUTTON");
         var spanElt = document.createElement("SPAN");
         btnElt.onclick = function(event) {
            authorize();
            event.stopPropagation();
         }
         spanElt.className = "i18n";
         spanElt.id = "newLogin";
         spanElt.appendChild(document.createTextNode(i18n("newLogin")));
         btnElt.appendChild(spanElt);

         while (elt.hasChildNodes()) {
            elt.removeChild(elt.lastChild);
         }
         elt.appendChild(btnElt);
      }
   }
   setNavigateButton();
}

function isAlbumIdRequired() {
   return (
      users.hasSome() &&
      (users.getNet() !== "twitter") &&
      (users.getNet() !== "flickr")
   );
}

function resetAlbumsList() {
   if (!isAlbumIdRequired()) {
      document.getElementById("jgUsersAid").style.display = "none";
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

      elt = document.getElementById("jgUsersAid");
      while (elt.hasChildNodes()) {
         elt.removeChild(elt.lastChild);
      }
      elt.appendChild(smallElt);
      elt.appendChild(document.createElement("BR"));
      elt.appendChild(italicElt);
      elt.appendChild(ulElt);
      elt.style.display = "";
   }
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
}

function changeAlbum(elt, event) {
   var albumTitleElt = document.getElementById('albumTitle');
   var liElt = getRealTarget(event);
   var albumTitle = liElt.getElementsByTagName("SPAN")[0].textContent;
   users.setAlbum(liElt.id, albumTitle);
   albumTitleElt.textContent = albumTitle;
   albumTitleElt.removeAttribute("class"); // no more i18n'ed  (except if 'none')
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
         setNavigateButton();
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
   // fitImage(document.getElementById('barImageIn'));
   fitImage(document.getElementById('photoImage'));
}
function fitImage(img) {
   var s;
   var elt = document.getElementById("p1");
   if ((elt.offsetHeight * img.offsetWidth)<(img.offsetHeight * elt.offsetWidth)) {
      s = "height:100%";
   }else {
      s = "width:100%";
   }
   img.setAttribute("style", s);
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
         browseTo(oauthNetwork);  // open a new browser window
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
            decodeURIComponent(obj.userName),
            decodeURIComponent(obj.accessPass),
            net
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

function whoAmI() {
   issueRequestStd(
      'whoAmI',
       function(person) {
          var value;
          value = person.name.givenName;
          if (!value) value = "?";
          document.getElementById("p2_givenName").textContent = value;
          value = person.name.familyName;
          if (!value) value = "?";
          document.getElementById("p2_familyName").textContent = value;
//        document.getElementById("p2_thumbnail").setAttribute(
//           "src", person.thumbnailUrl
//        );
          value = person.gender;
          if (!value) value = "?";
          document.getElementById("p2_gender").textContent = value;
          value = person.birthday;
          if (!value) {
             value = "?";
          }else {
             var date = new Date(value);
             value = date.getDate() + " " + i18n('months')[date.getMonth()];
          }
          document.getElementById("p2_birthday").textContent = value;
          expandPage("p2");
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
            dispatcher.post("albumsListed", albums.length);
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

function pickAndUpload(event) {
   if (!users.hasSome()) {
      formatUsersList(true);
   }else {
      var albumId = isAlbumIdRequired()? users.getAlbumId() : "NoNeedFor";
      if (!albumId) {
         event.stopPropagation();
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
         expandSidebarView(1);
      }else {
         try {
            uploadPick(albumId);
         }catch (err) {
            uploadFile(albumId);
         }
      }
   }
}

function uploadPick(albumId) {
   var a = new MozActivity({ name: "pick", data: {type: "image/jpeg"}});
   a.onsuccess = function(e) {
      issueRequest(
         "POST",
         "postImageData&NET=" + users.getNet() + "&AID=" + albumId,
         a.result.blob,
         function(val) {     // whenDone
            expandPage("p1");
            simpleMsg("info", i18n('imageUploaded', users.getAlbumTitle()));
         },
         function(rc, val) { // whenFailed
            simpleMsg("error", "RC: " + rc);
         },
         "image/jpeg"
      );
      try {
         var url = URL.createObjectURL(a.result.blob);
         var img = document.getElementById('photoImage');
         img.src = url;
         img.onload = function() { URL.revokeObjectURL(url); };
      }catch (error) {
         simpleMsg("error", "Local error: " + error);
      }
   };
   a.onerror = function() { simpleMsg("error", i18n('pickImageError')); };
}

function uploadFile(albumId) {
   var elt = document.getElementById('upldFile');
   elt.onchange = function() {
      if (typeof window.FileReader !== 'function') {
         simpleMsg("error", i18n("noFileApi"));
      }else if (!this.files) {
         simpleMsg("error", i18n("noFileApiProp"));
      }else if (!this.files[0]) {
         simpleMsg("error", i18n("noFileSelected"));
      }else {
         var file = this.files[0];
         var formData = new FormData();
         formData.append("MAX_FILE_SIZE", "1000000");
         formData.append("IMG", file.name.substr(-3));
         formData.append("AID", albumId);
//       formData.append("TIT", "my title");
         formData.append("upldFile", file);
         issueRequest(
            "POST", "postImageFile&NET=" + users.getNet(), formData,
            function(val) {     // whenDone
               expandPage("p1");
               // FIXME (albumId may be null)
               simpleMsg("info", i18n("imageUploaded", users.getAlbumTitle()));
            },
            function(rc, val) { // whenFailed
               simpleMsg("error", "RC: " + rc);
            }
         );
         var reader = new FileReader();
         reader.onload = function (event) {
            document.getElementById("photoImage").src = event.target.result;
         };
         reader.readAsDataURL(file);
      }
   };
   elt.click();
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

function issueRequest(method, op, values, whenDone, whenFailed, contentType) {
   var query = "?OP=" + op + "&V=1";    // REST version #1
   if (method === "GET") query += values;
   var xhr = new XMLHttpRequest({mozSystem: true});
   if (xhr.withCredentials === undefined) {
      simpleMsg("error", "Sorry: your browser doesn't handle cross-site requests");
      return;
   }
   xhr.withCredentials = true;
   xhr.open(method, server_url + query, true);
   if (contentType) xhr.setRequestHeader("Content-Type", contentType);
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

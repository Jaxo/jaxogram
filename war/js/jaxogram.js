var server_url = "http://13.jaxogram.appspot.com/jaxogram";
// -- only for our internal testing --
// var server_url = "http://11.jaxogram.appspot.com/jaxogram";
// var server_url = "http://localhost:8888/jaxogram";

var svgHeader = "<svg version='1.1' xmlns='http://www.w3.org/2000/svg' xmlns:xlink='http://www.w3.org/1999/xlink'";
var pendingPhotos = [];  // array of blobs or files
var upldPhotosCount = 0;
var tempFilterChoice = 0;
var filterChoice = 0;
var thumbMaxWidth = 0;
var thumbMaxHeight = 0;
var filters = [
   {
      name: "raw",
      img: new Image(),
      src: "",
      thumbImg: ""
   }, {
      name: "Nichole1",
      value:"<feComponentTransfer><feFuncR type='table' tableValues='0.0471, 0.1255, 0.251, 0.3765, 0.502, 0.6274, 0.7529, 0.8784, 1'/><feFuncG type='table' tableValues='0, 0.1255, 0.251, 0.3765, 0.4902, 0.6274, 0.7804, 0.9294, 1'/><feFuncB type='table' tableValues='0.0863, 0.1922, 0.251, 0.3765, 0.502, 0.6274, 0.7529, 0.8784, 1'/></feComponentTransfer><feColorMatrix type='matrix' values=' 0.7875 0.1930 0.0194 0.0000 0.0000 0.0575 0.9230 0.0194 0.0000 0.0000 0.0575 0.1930 0.7494 0.0000 0.0000 0.0000 0.0000 0.0000 1.0000 0.0000'/><feComponentTransfer><feFuncR type='linear' slope='1.0309' intercept='-0.0155'/><feFuncG type='linear' slope='1.0309' intercept='-0.0155'/><feFuncB type='linear' slope='1.0309' intercept='-0.0155'/></feComponentTransfer>",
      vignette: {},
      src: "",
      thumbImg: ""
   }, {
      name: "Nichole2",
      value:"<feComponentTransfer><feFuncR type='table' tableValues='0, 0.1255, 0.251, 0.3765, 0.502, 0.6274, 0.7529, 0.8784, 1'/><feFuncG type='table' tableValues='0, 0.1255, 0.251, 0.3765, 0.502, 0.6274, 0.7529, 0.8784, 1'/><feFuncB type='table' tableValues='0, 0.1255, 0.251, 0.3765, 0.502, 0.6274, 0.7529, 0.8784, 1'/></feComponentTransfer><feComponentTransfer><feFuncR type='linear' slope='1.0695' intercept='-0.0348'/><feFuncG type='linear' slope='1.0695' intercept='-0.0348'/><feFuncB type='linear' slope='1.0695' intercept='-0.0348'/></feComponentTransfer>",
      vignette: {},
      src: "",
      thumbImg: ""
   }, {
      name: "Vignette",
      value:"",
      vignette: { radius: 65, bright: 60},
      src: "",
      thumbImg: ""
   },{
      name: "f4",
      value: "<feColorMatrix type=\"matrix\" values=\"0.6666 0.6666 0.6666 0 0 0.3333 0.3333 0.3333 0 0 0.3333 0.3333 0.3333 0 0 0 0 0 1 0\"/>",
      vignette: {},
      src: "",
      thumbImg: ""
   },{
      name: "f5",
      value: "<feColorMatrix type=\"matrix\" values=\"-0.0257 1.2426 -0.0402 0.0000 0.0000 0.3113 0.0074 0.1600 0.0000 0.0000 0.8248 0.1325 -1.1995 0.0000 0.0000 0.0000 0.0000 0.0000 1.0000 0.0000\"/>",
      vignette: {},
      src: "",
      thumbImg: ""
   }
];
var users;
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
// },{
//    name: "twitpic",
//    url: "http://mobile.twitpic.com",
//    win: null
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
   var radioGroupNodes = document.querySelectorAll("[role=radiogroup]");
   for (var i=0, max=radioGroupNodes.length; i < max; ++i) {
      radioGroupNodes[i].addEventListener("click", radioClicked);
   }
   document.querySelector(".menuList").onclick = menuListClicked;
   document.getElementById("mn_albums").onclick = listAlbums;
   document.getElementById("changeLanguage").addEventListener("click", changeLanguage);
   // document.getElementById("footerTable").onclick = function() { expandSidebarView(-1); };
   document.getElementById("pickPhoto").onclick = pickPhoto;
   document.getElementById("uploadPhoto").onclick = tryUploadPhoto;
   document.getElementById("editPhoto").onclick = editPhoto;
   document.getElementById("cancelPhoto").onclick = function() {
      pendingPhotos.shift();
      uploadPhotos();
   };
   document.getElementById("cancelEdit").onclick = cancelEditPhoto;
   document.getElementById("validateEdit").onclick = validateEditPhoto;
   document.getElementById("logins").onclick = changeLogin;
   document.getElementById("p2_msgText").addEventListener(
      "input", onTextEntered, false
   );
   document.getElementById("p2_msgText").addEventListener(
      "keydown", onTextEntered, false
   );
   // document.getElementById("mn_albums").style.display = "none";
   var dfltLocale = navigator.language || navigator.userLanguage;
   translateBody(dfltLocale);
   formatUsersList(false);
   formatNetworkChoices();
   document.getElementById('usedLang').textContent = i18n(dfltLocale);
   document.getElementById(dfltLocale).setAttribute("aria-selected", "true");
   var elt = document.getElementById("imgFilters");
   for (var i=0, max=filters.length; i < max; ++i) {
      var tdElt = document.createElement("TD");
      var imgElt = document.createElement("IMG");
      imgElt.onload = function() {
        // no longer need to read the blob so it's revoked
        if (this.src) URL.revokeObjectURL(this.src);
      };
      filters[i].thumbImg = imgElt;
      tdElt.setAttribute("align", "center");
      tdElt.appendChild(imgElt);
      elt.appendChild(tdElt);
   }
   showToolbar(2);
   thumbMaxWidth = (elt.cells)[0].offsetWidth;
   thumbMaxHeight = (elt.cells)[0].offsetHeight;
   showToolbar(0);

   elt.addEventListener("click", changeFilter);
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

function onTextEntered(event) {
   if (event && (event.keyCode === 13)) {
      event.stopPropagation();
      this.blur();
   }else {
      var countElt = document.getElementById("p2_msgCount");
      if (this.value.length == 0) {
         countElt.textContent = "";
      }else {
         var remain = 116 - this.value.length;
         if (remain > 20) {
            countElt.style.color = "";
         }else {
            countElt.style.color = "#C80000";
            if (remain < 0) {
               this.value = this.value.substring(0, 116);
               remain = 0;
            }
         }
         countElt.textContent = remain;
         this.style.height = "1.7rem";
         if (this.scrollHeight > this.clientHeight) {
            this.style.height = this.scrollHeight + "px";
         }
      }
   }
}

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
      document.getElementById("initLogin").style.visibility = "";
      document.getElementById("mn_userName").textContent = users.getUserName();
      document.getElementById("p0_userName").innerHTML = users.getUserName();
      document.getElementById("p0_userImage").src = users.getImageUrl();
      document.getElementById("mn_netImage").src = netImage;
      document.getElementById("p0_netImage").src = netImage;
      document.getElementById("p0_userScreenName").textContent = users.getScreenName();
   }else {
      var elt = document.getElementById("initLogin");
      elt.style.visibility = "visible";
      elt.onclick = authorize;
      document.getElementById("mn_user").style.display = "none";
      document.getElementById("p0_userName").innerHTML = i18n("noNetwork");
      document.getElementById("p0_userImage").src = "../images/none.png";
      document.getElementById("mn_netImage").src = "../images/none.png";
      document.getElementById("p0_netImage").src = "../images/none.png";
      document.getElementById("p0_userScreenName").textContent = "";
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
      ulElt.addEventListener("click", radioClicked);
      ulElt.addEventListener("click", function(event) { changeAlbum(this, event); });

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
   document.getElementById("p2_userScreenName").textContent = users.getScreenName();
   uploadPhotos();
}

function changeLogin(event) {
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
      img.addEventListener("load", function() { fitImage(this); });
      fitImage(img);
   }
}

function fitImage(img) {
   var elt = img.parentNode;
   if ((elt.offsetHeight * img.offsetWidth)<(img.offsetHeight * elt.offsetWidth)) {
      img.style.top = "0";
      img.style.width = "";
      img.style.height = "100%";
   }else {
      img.style.top = ((elt.offsetHeight - img.offsetHeight) / 2) + "px";
      img.style.width = "100%";
      img.style.height = "";
   }
}

function formatNetworkChoices() {
   var eltContainer = document.getElementById("choices");
   var btnElt;
   networks.forEach(
      function(network) {
         var name = network.name;
         var imgElt = document.createElement("IMG");
         imgElt.src= "images/" + name + "Logo.png";
         btnElt = document.createElement("BUTTON");
         if (name === "picasa") {
            btnElt.onclick = authorizePicasa;
         }else {
            btnElt.onclick = function() { authorizeThruOAuth(name); }
         }
         btnElt.appendChild(imgElt);
         eltContainer.appendChild(btnElt);
      }
   );
   btnElt = document.createElement("BUTTON");
   btnElt.textContent = i18n("cancel");
   btnElt.onclick = function() {
      document.querySelector(".menuaction").style.display="none";
   }
   var divElt = document.createElement("DIV");
   divElt.appendChild(btnElt);
   eltContainer.parentNode.appendChild(divElt);
// eltContainer.appendChild(btnElt);
}

function authorize() {
   expandSidebarView(-1);
   document.querySelector(".menuaction").style.display="block";
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
            menuListClicked(event);
         }
      );
      event.stopPropagation();
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
   showToolbar(0);
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
      showToolbar(1);
      showNewPhoto();
      document.getElementById("p2_msgText").value = "";
      document.getElementById("p2_msgCount").textContent = "";
      expandPage("p2");
      // ?? isUploadable();
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

function tryUploadPhoto() {
   if (isUploadable()) {
      filterAndUploadPhoto(pendingPhotos.shift());
   }
}

function doFilter(w, h, imgUrl, filter) {
   var fId = filter.name;
   var data = (
     "<g><image preserveAspectRatio='xMinYMin meet'" +
     " width='" + w + "' height='" + h + "' xlink:href='" + imgUrl
   );
   if (filter.value) {
      data += (
        "' filter='url(data:image/svg+xml," +
        escape(
           "<svg xmlns='http://www.w3.org/2000/svg'><filter id='" + fId + "'>" +
           filter.value +
           "</filter></svg>"
        ) +
        "#" + fId + ")"
      );
   }
   data += (
     "'></image></g>"
   );
   if (filter.vignette.radius) { // if (Object.keys(filter.vignette) !== 0)
      data = vignetize(data, w, h, imgUrl, filter);
   }else {
      data = svgHeader + " width='" + w + "' height='" + h + "' >" + data + "</svg>";
   }
   return new Blob([data], {type:"image/svg+xml"});
}

function vignetize(data, w, h, imgUrl, filter) {
   var f2 = filter.name + "_2";
   var f3 = filter.name + "_3";
   var f4 = filter.name + "_4";
   var r = filter.vignette.radius / 100;
   var b = filter.vignette.bright / 100;
   var m, t, u, p, q, cx, cy;
   if (h < w) {
      m = h;
      t = (h-w)/2;
      u = 0;
      p = 1.0;
      q = w/h;
   }else {
      m = w;
      t = 0;
      u = (w-h)/2;
      p = h/w;
      q = 1.0;
   }
   var border = (m * 0.0214) | 0;
   if (border < 2) border = 2;
   return (
     svgHeader + " width='" + m + "' height='" + m +
       "'><g transform='translate(" + t + "," + u + ")'>" +
         data +
         "<g>" +
            "<image xlink:href='" + imgUrl +
            "' width='" + w + "' height='" + h +
            "' filter='url(data:image/svg+xml," +
            escape(
               "<svg xmlns='http://www.w3.org/2000/svg'><filter id='" + f2 + "'>" +
               "<feGaussianBlur stdDeviation='2'/>" +
               "<feColorMatrix type='matrix' values='" + b + " 0 0 0 0 0 " + b + " 0 0 0 0 0 " + b + " 0 0 0 0 0 1 0'/>" +
               "</filter></svg>"
            ) +
            "#" + f2 +
            ")' mask ='url(data:image/svg+xml," +
            escape(
               "<svg xmlns='http://www.w3.org/2000/svg'>" +
               "<mask id='" + f3 +
               "' maskUnits='objectBoundingBox' maskContentUnits='objectBoundingBox'>" +
               "<rect x='0' y='0' width='1' height='1' stroke-width='0'" +
               " fill='url(data:image/svg+xml," +
               escape(
                  "<svg xmlns='http://www.w3.org/2000/svg'><radialGradient id='" + f3 + "'" +
                     " gradientUnits='objectBoundingBox' cx='" + (1/(2*p)) + "' cy='" + (1/(2*q)) + "' r='" + r + "'" +
                     " gradientTransform='scale(" + p + "," + q + ")'" +
                  ">" +
                     "<stop offset='0.4' stop-color='#ffffff' stop-opacity='0'/>" +
                     "<stop offset='0.6' stop-color='#ffffff' stop-opacity='1'/>" +
                  "</radialGradient></svg>"
               ) +
               "#" + f3 + ")'/>" +
               "</mask></svg>"
            ) +
            "#" + f3 + ")'></image>" +
         "</g>" +
         "<g fill='none' stroke='white' stroke-width='" + border + "' >" +
           "<rect x='" + ((border/2)-t) + "' y='" + ((border/2)-u) + "' width='" + (m-border) + "' height='" + (m-border) + "'/>" +
         "</g>" +
       "</g>" +
     "</svg>"
   );
}

function filterAndUploadPhoto(imgRawBlob)
{
   if (filterChoice === 0) {
      uploadPhoto(imgRawBlob);
   }else {
      var sentImg = new Image();
      document.getElementById("progresspane").style.visibility="visible";
      sentImg.onload = function() {
         var canvas = document.createElement("CANVAS");
         canvas.width = sentImg.width;
         canvas.height = sentImg.height;
         var ctx = canvas.getContext('2d');
         ctx.drawImage(sentImg, 0, 0);
         // see https://developer.mozilla.org/en-US/docs/DOM/HTMLCanvasElement#Example.3A_Getting_a_file_representing_the_canvas
         canvas.toBlob(
            function(imgFilteredBlob) { uploadPhoto(imgFilteredBlob); },
            "image/jpeg", 0.95
         );
      };
      sentImg.src = document.getElementById("p2_picture").src;
   }
}

function uploadPhoto(imgBlob) {
   var formData = new FormData();
   formData.append("MAX_FILE_SIZE", "2000000");
// formData.append("IMG", file.name.substr(-3));
   if (isAlbumIdRequired()) formData.append("AID", users.getAlbumId());
   formData.append("TIT", document.getElementById("p2_msgText").value);
   formData.append("upldFile", imgBlob);
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

function showToolbar(barNo) {
   var elt = document.getElementById("footerTable");
   var prevBarNo = -1;
   elt.parentNode.style.display = "none";
   for (var rows=elt.rows, max=rows.length, i=0; i < max; ++i) {
      var row = rows[i];
      if (!row.style.display) prevBarNo = i;
      if (i == barNo) {
         elt.parentNode.style.display = "";
         row.style.display = "";
      }else {
         row.style.display = "none";
      }
   }
   return prevBarNo;
}

function showNewPhoto() {
   // see https://developer.mozilla.org/en-US/docs/DOM/HTMLCanvasElement#Example.3A_Getting_a_file_representing_the_canvas
   var imgRawElt = filters[0].img;
   imgRawElt.onload = function() {
      if (filters[0].src) URL.revokeObjectURL(filters[0].src);
      filters[0].src = imgRawElt.src;
      // 0) Set the filters
      for (var i=1, max=filters.length; i < max; ++i) {
         var filter = filters[i];
         if (filter.src) URL.revokeObjectURL(filter.src);
         filter.src = URL.createObjectURL(
            doFilter(imgRawElt.width, imgRawElt.height, imgRawElt.src, filter)
         );
      }
      // 1) Compute the thumbnails size
      var w = thumbMaxHeight / filters[0].img.height;
      var h = thumbMaxWidth / filters[0].img.width;
      if (w < h) {
         w = Math.round(filters[0].img.width * w);
         h = thumbMaxHeight;
      }else {
         w = thumbMaxWidth;
         h = Math.round(filters[0].img.height * h);
      }
      // 2) draw the raw thumbnail
      var canvas = document.createElement("CANVAS");
      canvas.setAttribute("width", w);
      canvas.setAttribute("height", h);
      canvas.getContext('2d').drawImage(imgRawElt, 0, 0, w, h);
      // 3) derive the filtered thumbnails
      canvas.toBlob(
         function(thumbBlob) {
            var rawThumb = URL.createObjectURL(thumbBlob);
            filters[0].thumbImg.src = rawThumb;
            for (var i=1, max=filters.length; i < max; ++i) {
               filters[i].thumbImg.src = URL.createObjectURL(
                  doFilter(w, h, rawThumb, filters[i])
               );
            }
         },
         "image/jpeg", 0.95
      );
      document.getElementById("p2_picture").src = filters[filterChoice].src;
   };
   imgRawElt.src = URL.createObjectURL(pendingPhotos[0]);
}

function editPhoto() {
   showToolbar(2);
   tempFilterChoice = filterChoice;
   expandSidebarView(-1);
   document.getElementById("p3_picture").src = filters[filterChoice].src;
   expandPage("p3");
}

function validateEditPhoto() {
   filterChoice = tempFilterChoice;
   document.getElementById("p2_picture").src = filters[filterChoice].src;
   cancelEditPhoto();
}

function cancelEditPhoto() {
   showToolbar(1);
   expandPage("p2");
}

function changeFilter(event) {
   if (event) tempFilterChoice = getRealTarget(event).cellIndex;
   document.getElementById("p3_picture").src = filters[tempFilterChoice].src;
}


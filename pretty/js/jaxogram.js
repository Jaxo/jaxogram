var server_url = "http://jaxogram.appspot.com/jaxogram";
// -- only for our internal testing --
// var server_url = "http://11.jaxogram.appspot.com/jaxogram";
// var server_url = "http://localhost:8888/jaxogram";
var appRecord = null;
var svgHeader = "<svg version='1.1' xmlns='http://www.w3.org/2000/svg' xmlns:xlink='http://www.w3.org/1999/xlink'";
var pendingPhotos = [];  // array of blobs or files
var upldPhotosCount = 0;
var tempFilterChoice = 0;
var filterChoice = 0;
var thumbMaxWidth = 0;
var thumbMaxHeight = 0;
var advertizer;
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
   },{
      name: "Impressions",
      value: "<feMorphology operator=\"dilate\" radius=\"5\"/><feMorphology operator=\"erode\" radius=\"7\"/>",
      vignette: {},
      src: "",
      thumbImg: ""
   },{
      name: "f7",
      value: "<feColorMatrix type=\"luminanceToAlpha\"/><feDiffuseLighting diffuseConstant=\"1\" surfaceScale=\"10\" result=\"diffuse3\"><feDistantLight elevation=\"28\" azimuth=\"90\"/></feDiffuseLighting><feComposite operator=\"in\" in2=\"diffuse3\"/>",
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
      simpleMsg("z_warning", i18n("z_testMode", server_url));
   }
   users = new JgUsers();
   // users.cleanUp();
   // users.destroy();
   advertizer = create_iaAd(
      document.getElementById("adPane"),
      function(url) { window.open(url, "iaAd"); }
   );

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
      "z_install_changed",
      function action(state, app) {
         if (state === 0 /* IDLE */) {
            if (
               !users.hasSome() &&
               (params.OP !== "backCall") && (params.OP !== "share")
            ) {
               confirmMsg(
                  i18n('z_betterInstall'),
                  function() { document.getElementById("z_btnInstall").click(); }
               );
            }
         }else if ((state === 1 /* INSTALLED */) && app) {
            document.querySelector("header h1 small").textContent = (
               app.manifest.version
            );
         }
         checkCredentials(app, state);
      }
   );
   setInstallButton("z_btnInstall");

   window.addEventListener("resize", fitImages, false);
   fitImages();

   // Listeners
   document.getElementById('p01').onclick = toggleSidebarView;
   var radioGroupNodes = document.querySelectorAll("[role=radiogroup]");
   for (var i=0, max=radioGroupNodes.length; i < max; ++i) {
      radioGroupNodes[i].addEventListener("click", radioClicked);
   }
   document.querySelector(".menuList").onclick = menuListClicked;
   document.getElementById("mn5").onclick = listAlbums;
   document.getElementById("ft1").onclick = pickPhoto;
   document.getElementById("ft3").onclick = uploadFilteredPhoto;
   document.getElementById("ft4").onclick = editPhoto;
   document.getElementById("ft5").onclick = function() {
      pendingPhotos.shift();
      uploadPhotos();
   };
   document.getElementById("p31").onclick = cancelEditPhoto;
   document.getElementById("p32").onclick = validateEditPhoto;
   document.getElementById("mn4").onclick = changeLogin;
   document.getElementById("z_enterTweet").addEventListener(
      "input", onTextEntered, false
   );
   document.getElementById("z_enterTweet").addEventListener(
      "keydown", onTextEntered, false
   );
   // document.getElementById("mn5").style.display = "none";
   formatLanguageList();
   formatUsersList(false);
   formatNetworkChoices();
   var elt = document.getElementById("ft6");
   for (var i=0, max=filters.length; i < max; ++i) {
      var liElt = document.createElement("LI");
      var imgElt = document.createElement("IMG");
      imgElt.onload = function() {
         // no longer need to read the blob so it's revoked
         if (this.src) URL.revokeObjectURL(this.src);
      };
      filters[i].thumbImg = imgElt;
      liElt.setAttribute("align", "center");
      liElt.appendChild(imgElt);
      elt.appendChild(liElt);
   }
   showToolbar(2);
   thumbMaxWidth = elt.firstElementChild.offsetWidth;
   thumbMaxHeight = elt.firstElementChild.offsetHeight;
   showToolbar(0);

   elt.addEventListener("click", changeFilter);
   var principal = document.querySelector(".principal");
   new GestureDetector(principal).startDetecting();
   principal.addEventListener(
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
   // FIXME (iaAd test)
   setGpsCoordinates();
   document.getElementById("adBanner").onclick = advertizer.showBanner;
   document.getElementById("adSplash").onclick = advertizer.showSplash;
};

function onTextEntered(event) {
   if (event && (event.keyCode === 13)) {
      event.stopPropagation();
      this.blur();
   }else {
      var countElt = document.getElementById("p21");
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
      var netImage = "images/" + networkName + "Logo.png";
      var connectTo = document.getElementById("ft2");
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
      document.getElementById("z_initLogin").style.visibility = "";
      document.getElementById("mn3").textContent = users.getUserName();
      document.getElementById("p13").innerHTML = users.getUserName();
      document.getElementById("p11").src = users.getImageUrl();
      document.getElementById("mn2").src = netImage;
      document.getElementById("p12").src = netImage;
      document.getElementById("p14").textContent = users.getScreenName();
   }else {
      var elt = document.getElementById("z_initLogin");
      elt.style.visibility = "visible";
      elt.onclick = authorize;
      document.getElementById("mn1").style.display = "none";
      document.getElementById("p13").innerHTML = (
         "<SPAN class='i18n' id='z_noNetwork'>" +
         i18n('z_noNetwork') +
         "</SPAN>"
      );
      document.getElementById("p11").src = "images/none.png";
      document.getElementById("mn2").src = "images/none.png";
      document.getElementById("p12").src = "images/none.png";
      document.getElementById("p14").textContent = "";
      document.getElementById("ft2").style.display = "none";
   }
}

function formatLanguageList() {
   var listElt = document.getElementById("mn6");
   setLocale(users.getLocale());
   forEachLanguage(
      function(language, selected) {
         var itmElt = document.createElement("LI");
         itmElt.className = "i18n";
         itmElt.id = language;
         if (selected) itmElt.setAttribute("aria-selected", "true");
         listElt.appendChild(itmElt);
      }
   );
   listElt.addEventListener(
      "click",
      function(event) {
         var iso639 = event.target.id;
         users.setLocale(iso639);
         translateBody(iso639);
      }
   );
   translateBody(users.getLocale());
}

function formatUsersList(isUserRequired) {
   resetAlbumsList();
   if (users.hasSome()) {
      var elt;
      var itmElt;
      var ulElt = document.getElementById("mn4");
      while (ulElt.hasChildNodes()) {
         ulElt.removeChild(ulElt.lastChild);
      }
      itmElt = document.createElement("LI");
      itmElt.className = "i18n addItem";
      itmElt.id = "z_newLogin";
      itmElt.onclick = function(event) {
         authorize();
         event.stopPropagation();
      };
      itmElt.appendChild(document.createTextNode(i18n("z_newLogin")));
      ulElt.appendChild(itmElt);
      users.forEach(
         function(name, pass, albumTitle, isSelected, net) {
            var imgElt = document.createElement("IMG");
            itmElt = document.createElement("LI");
            var spanElt = document.createElement("SPAN");
            if (isSelected) itmElt.setAttribute("aria-selected", "true");
            imgElt.src = "images/" + net + "SmallLogo.png";
            spanElt.setAttribute("role", "trasher");
            itmElt.appendChild(spanElt);
            itmElt.appendChild(imgElt);
            itmElt.appendChild(document.createTextNode(name));
            ulElt.appendChild(itmElt);
         }
      );
      elt = document.getElementById("mn1");
      elt.setAttribute("aria-expanded", "false");
      elt.appendChild(ulElt);
      elt.style.display = "";
      tellAccessPass();
   }else {
      document.getElementById("mn1").style.display = "none";
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
   var albums = document.getElementById("mn5");
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
      smallElt.id = "z_photoAlbum";
      smallElt.appendChild(document.createTextNode(i18n("z_photoAlbum")));
      italicElt = document.createElement("I");
      italicElt.id = "z_albumTitle";
      if (users.getAlbumTitle()) {
         italicElt.textContent = users.getAlbumTitle();
      }else {
         italicElt.className = "i18n";
         italicElt.appendChild(document.createTextNode(i18n("z_albumTitle")));
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
   liElt.className = "i18n addItem";
   liElt.id = "z_newAlbum";
   liElt.onclick = function(event) {
      createAlbum(true);
      event.stopPropagation();
   };
   liElt.appendChild(document.createTextNode(i18n('z_newAlbum')));
   elt.appendChild(liElt);
   for (var i=0, max=albums.length; i < max; ++i) {
      var album = albums[i];
      var title = album['title'];
      var description = album['description'];
      var thumbUrl = album['thumbnailUrl'];
      if (!title || (title.length === 0)) title = "no title";
      if (!description || (description.length === 0)) description = "";
      if (!thumbUrl || (thumbUrl.length === 0)) thumbUrl = "images/unknSmall.png";
      var imgElt = document.createElement("IMG");
      imgElt.src = thumbUrl;
      var spanElt = document.createElement("SPAN");
      spanElt.appendChild(document.createTextNode(title));
      var smallElt = document.createElement("SMALL");
      smallElt.appendChild(document.createTextNode(description));
      var divElt = document.createElement("DIV");
      divElt.appendChild(spanElt);
      divElt.appendChild(document.createElement("BR"));
      divElt.appendChild(smallElt);

      liElt = document.createElement("LI");
      liElt.id = album['id'];
      if (selAlbumId === liElt.id) {
         liElt.setAttribute("aria-selected", "true");
         isSelAlbumOK = true;
      }
      liElt.appendChild(imgElt);
      liElt.appendChild(divElt);
      elt.appendChild(liElt);
   }
   var albumTitleElt = document.getElementById('z_albumTitle');
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
      albumTitleElt.textContent = i18n("z_albumTitle");
   }
   // uploadPhotos();
}

function changeAlbum(elt, event) {
   var albumTitleElt = document.getElementById('z_albumTitle');
   var liElt = getRealTarget(event);
   var albumTitle = liElt.getElementsByTagName("SPAN")[0].textContent;
   users.setAlbum(liElt.id, albumTitle);
   albumTitleElt.textContent = albumTitle;
   albumTitleElt.removeAttribute("class"); // no more i18n'ed  (except if 'none')
   document.getElementById("p14").textContent = users.getScreenName();
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
               "z_revokeAccess",
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
   var eltContainer = document.getElementById("ma1");
   var btnElt;
   networks.forEach(
      function(network) {
         var name = network.name;
         var itmElt = document.createElement("menuitem");
         itmElt.style.backgroundImage = "url(\"images/" + name + "Logo.png\")";
         if (name === "picasa") {
            itmElt.onclick = authorizePicasa;
         }else {
            itmElt.onclick = function() { authorizeThruOAuth(name); }
         }
         eltContainer.appendChild(itmElt);
      }
   );
   document.getElementById("z_cancel").onclick = hideActionMenu;
}

function authorize() {
   // expandSidebarView(-1);
   showActionMenu();
}

function authorizePicasa() {
   hideActionMenu();
   var eltInp1 = makeInputField("z_login");
   var eltInp2 = makeInputField("z_passwd", "password");
   showMsg(
      "z_picasaLogin",
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
                  // hideMsg(); why?
                  users.addUser(userName, passwd, "picasa");
                  formatUsersList(false);
               },
               function(rc, val) {  // whenFailed
                  simpleMsg("z_error", i18n("z_badLogin"));
               }
            );
         }
      }
   );
}

function authorizeThruOAuth(net) {
   hideActionMenu();
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
         simpleMsg("z_error", "authorize RC:" + rc + "\n" + val);
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
               registerUser(obj['VRF'], obj['NET']);
               formatUsersList(false);
            }
         }
      },
      function(rc, val) { // whenFailed
         var win = oauthNetwork.win;
         if (win && !win.closed) win.close();
         simpleMsg("z_error", "getVerifier RC:" + rc);
      }
   );
   showProgress(false);
}

function registerUser(verifier, net) {
   issueRequest(
      "GET", "getAccPss",
      "&VRF=" + encodeURIComponent(verifier) + "&NET=" + net,
      function(val) {     // whenDone
         var obj = JSON.parse(val);
         // alert(dump(obj));
         users.addUser(
            obj['userName'],
            obj['accessPass'],
            net,
            obj['imageUrl'],
            obj['screenName']
         );
         formatUsersList(false);
      },
      function(rc, val) { // whenFailed
         simpleMsg("z_error", i18n("z_authDenied", val));
      }
   );
}

function tellAccessPass()
{
   issueRequest(
      "POST", "postAccPss", users.getAccessPass(),
      function(val) {},   // whenDone
      function(rc, val) { // whenFailed
         simpleMsg("z_error", "tellAccess RC: " + rc + "\n" + val);
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
   var eltInp1 = makeInputField("z_title");
   var eltInp2 = makeInputField("z_description");
   showMsg(
      isDirect? "z_createAlbumProlog1" : "z_createAlbumProlog2",
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
               users.setAlbum(newAlbum['id'], newAlbum['title']);
               formatAlbumsList(
                  albums,
                  document.getElementById("mn5").getElementsByTagName("UL")[0]
               );
            }
         );
      }
   );
}

function pickPhoto(event) {
   document.getElementById("z_enterTweet").value = "";
   document.getElementById("p21").textContent = "";
   if (typeof MozActivity !== "undefined") {
      var a = new MozActivity({ name: "pick", data: {type: "image/jpeg"}});
      a.onsuccess = function(e) {
         pendingPhotos.push(a.result.blob);
         uploadPhotos();
      };
      a.onerror = function() {
         simpleMsg("z_error", i18n('z_pickImageError'));
      };
   }else {
      var elt = document.createElement("INPUT");
      elt.type = "file";
      elt.setAttribute("multiple", "true");
      elt.setAttribute("accept", "image/");
      elt.onchange = function() {
         if (!this.files) {
            simpleMsg("z_error", i18n("z_noFileApiProp"));
         }else if (!this.files[0]) {
            simpleMsg("z_error", i18n("z_noFileSelected"));
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
   revealPage("p1"); // stop p2!
   showToolbar(0);
   if (upldPhotosCount > 0) {
      simpleMsg("z_info", i18n('z_photosUploaded', upldPhotosCount));
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
      revealPage("p2");
      // ?? isUploadable();
   }
}

function isUploadable() {
   if (!users.hasSome()) {
      formatUsersList(true);
      return false;
   }else if (isAlbumIdRequired() && (users.getAlbumId() == null)) {
      // show the appropriate panel for selecting an album
      var albumsPane = document.getElementById("mn5");
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
            simpleMsg("z_warning", i18n("z_selectOrCreateAlbum"));
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

function uploadFilteredPhoto(imgRawBlob)
{
   if (isUploadable()) {
      var imgRawBlob = pendingPhotos.shift();
      if (filterChoice === 0) {
         uploadPhoto(imgRawBlob);
      }else {
         var sentImg = new Image();
         showProgress(true);
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
         sentImg.src = document.getElementById("p22").src;
      }
   }
}

function uploadPhoto(imgBlob) {
   var formData = new FormData();
   formData.append("MAX_FILE_SIZE", "2000000");
// formData.append("IMG", file.name.substr(-3));
   if (isAlbumIdRequired()) formData.append("AID", users.getAlbumId());
   formData.append("TIT", document.getElementById("z_enterTweet").value);
   formData.append("upldFile", imgBlob);
   issueRequest(
      "POST",
      "postImageFile&NET=" + users.getNet(),
      formData,
      function(val) {        // whenDone
         /*
         Response:
            var res = JSON.parse(xhr.responseText);
            var id_str = res.entities.media[0].id_str;
            var ex_url = res.entities.media[0].expanded_url;
         *//*
         try {
            var media = JSON.parse(val)['entities']['media'][0];
            var idStr = media.id_str;
            var expandedUrl = media['expanded_url'];
         }catch (error) {
         }
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

function checkCredentials(app, state) {
   var appRecord = "";
   if (app) {
      var appObject = {};
      for (var name in app) appObject[name] = app[name];
      appRecord = JSON.stringify(appObject);
   }
   issueRequest(
      "POST",
      "appCred&stt=" + state + "&loc=" + users.getLocale(),
      appRecord,
      function(val) {     // whenDone
         var ad = JSON.parse(val)["ad"];
         if (ad) simpleMsg("z_info", ad, true);
      },
      function(rc, val) { // whenFailed
         if (rc == 402) { // Payment Required
            var obj = JSON.parse(val);
            var ad = obj["ad"];
            simpleMsg(
               "z_warning",
               obj["msg"] + "<HR/>" + i18n("z_noReceipts"),
               true // don't erase
            );
         }else {
            simpleMsg("z_error", "Server RC: " + rc + "\n" + val);
         }
         if (ad) simpleMsg("z_info", ad, true);
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
            simpleMsg("z_error", what + " RC: " + rc + "\n" + val);
         }
      );
   }
}

function issueRequest(method, op, values, whenDone, whenFailed) {
   var query = "?OP=" + op + "&V=2";    // REST version #2 (postAccPss)
   if (method === "GET") query += values;
   var xhr = new XMLHttpRequest({'mozSystem': true});
   if (xhr.withCredentials === undefined) {
      simpleMsg("z_error", "Sorry: can't do cross-site requests");
      return;
   }
   xhr.withCredentials = true;
   xhr.open(method, server_url + query, true);
   xhr.onreadystatechange = function () {
      if (this.readyState === 4) {
         showProgress(false);
         if ((this.status === 200) || (this.status === 0)) {
            whenDone(this.responseText);
         }else {
            whenFailed(this.status, this.responseText);
         }
      }
   };
   showProgress(true);
   if (method === "GET") {
      xhr.send();
   }else {
      xhr.send(values);
   }
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
      document.getElementById("p22").src = filters[filterChoice].src;
   };
   imgRawElt.src = URL.createObjectURL(pendingPhotos[0]);
}

function editPhoto() {
   showToolbar(2);
   tempFilterChoice = filterChoice;
   expandSidebarView(-1);
   document.getElementById("p33").src = filters[filterChoice].src;
   revealPage("p3");
}

function validateEditPhoto() {
   filterChoice = tempFilterChoice;
   document.getElementById("p22").src = filters[filterChoice].src;
   cancelEditPhoto();
}

function cancelEditPhoto() {
   showToolbar(1);
   revealPage("p2");
}

function changeFilter(event) {
   tempFilterChoice = getIndex(event);
   document.getElementById("p33").src = filters[tempFilterChoice].src;
}

function getQueryParams() {
   var query = window.location.search.substr(1).split('&');
   if (query === "") return {};
   var params = {};
   for (var i=0; i < query.length; ++i) {
       var param = query[i].split('=');
       if (param.length === 2) {
          params[param[0]] = decodeURIComponent(param[1].replace(/\+/g, " "));
       }
   }
   return params;
}

function setGpsCoordinates() {
   var geoloc = navigator.geolocation;
   if (geoloc) {
      geoloc.getCurrentPosition(
         function(position) {   // OK!
            var coords = position.coords;
            advertizer.setGpsCoordinates(coords.latitude, coords.longitude);
         },
         function(error) {
         },
         {
            enableHighAccuracy: false,
            timeout: 300000,       // allow 5 minutes for searching
            maximumAge: 86400000   // position found last day is ok
         }
      );
   }
}

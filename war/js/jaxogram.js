var users;
var authKey;
var isPackaged = true;
var server_url = "http://jaxogram.appspot.com/jaxogram";
// -- only for our internal testing --
// var server_url = "http://5.jaxogram.appspot.com/jaxogram";
// var server_url = "http://localhost:8888/jaxogram";

window.onload = function() {
   var loc = window.location;
   if (loc.protocol !== "app:") {
      isPackaged = false;
      var host = loc.host;
      if (host.indexOf("appspot") >= 0) {              // appspot default, or versioned
         server_url = "http://" + host + "/jaxogram";
      }else {                                          // "http://localhost:8888/", or
         server_url = "http://localhost:8888/jaxogram" // "http://ottokar/jaxogram/index.html"
      }
   }
   //OT-LH*/ isPackaged = true;  // testing on ottokar/localhost
   if (server_url !== "http://jaxogram.appspot.com/jaxogram") {
      alert("Warning: test version\nServer at\n" + server_url);
   }
   createDispatcher();
   users = new JgUsers();
   // users.cleanUp();
   // users.destroy();
   var params = getQueryParams();
   if (params.OP === "backCall") {
      /*
      | this occurs for non-packaged application only:
      | packaged app have no origin, can not appear as referer...
      | no way to call them back from the external world.
      */
      registerUser(params.verifier);
   }
   dispatcher.on(
      "install_changed",
      function action(state, version) {
         if (
            (state === "uninstalled") && !users.hasSome() &&
            (params.OP !== "backCall") && confirm(i18n('betterInstall'))
         ) {
            document.getElementById("btnInstall").click();
         }else if ((state === "installed") && version) {
            document.querySelector("header h1 small").textContent = version;
         }
      }
   );

   setInstallButton("btnInstall");
   fitImage(document.getElementById('photoImage'));
   window.addEventListener("resize", fitImages, false);

   // Listeners
   document.getElementById("btnMain").onclick = toggleSidebarView;
   document.getElementById("sidebarMenu").onclick = menuListClicked;
   document.getElementById("jgUsersAid").onclick = listAlbums;
   document.getElementById("changeLanguage").onclick = changeLanguage;
   document.getElementById("footerTable").onclick = function() { expandSidebarView(-1); };
   document.getElementById("pickAndUpload").onclick = pickAndUpload;
   document.getElementById("whoAmI").onclick = whoAmI;

// document.getElementById("p1").addEventListener(
//    'transitionend',
//    function(event) {
//       p1Expanded(event.target.attributes["aria-expanded"].value == "true");
//    },
//    false
// );

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
   eltMain.addEventListener("swipe", swipeHandler);
};

// function p1Expanded(isExpanded) {
//    var style = document.getElementById("btnSecond").style;
//    if (isExpanded) {
//       style.display ="";
//    }else {
//       style.display = "none";
//    }
// }

function swipeHandler(e) {
   var direction = e.detail.direction;
   if (direction === 'right') {
      expandSidebarView(1);
   }else if (direction === 'left') {
      expandSidebarView(-1);
   }
// alert(
//    "Swipe:" +
//    "\n start: " + detail.start +
//    "\n end: " + detail.end +
//    "\n dx: " + detail.dx +
//    "\n dy: " + detail.dy +
//    "\n dt: " + detail.dt +
//    "\n vx: " + detail.vx +
//    "\n vy: " + detail.vy +
//    "\n direction: " + detail.direction +
//    "\n angle: " + detail.angle
// );
}

function formatUsersList(isUserRequired) {
   document.getElementById("jgUsersAid").innerHTML = (
      "<SMALL class='i18n' id='photoAlbum'>" +
      i18n("photoAlbum") +
      "</SMALL><BR/><i class='i18n' id='albumTitle'>" + i18n("albumTitle") + "</i>" +
      "<UL role='radiogroup' id='albumList' onclick='changeAlbum(this, event)'></UL>"
   );
   if (users.hasSome()) {
      var html = "";
      var selName = null;
      var selAlbumTitle = null;
      users.forEach(
         function(name, pass, albumTitle, isSelected, net) {
            html += "<LI";
            if (isSelected) {
               selName = name;
               selAlbumTitle = albumTitle;
               html += " aria-selected='true'";
            }
            html += "><span role='trasher'>&#xF018;</span>" + name + "</LI>";
         }
      );
      if (selAlbumTitle) {
         var albumTitleElt = document.getElementById('albumTitle');
         albumTitleElt.textContent = selAlbumTitle;
         albumTitleElt.removeAttribute("class");  // no more i18n'ed
      }
      document.getElementById("jgUsersIn").innerHTML = (
         "<SMALL class='i18n' id='loginAs'>" +
         i18n('loginAs') +
         "</SMALL><BR/><i id='jgUserName'>" +
         selName +
         "</i><UL role='radiogroup' onclick='changeLogin(this, event);'>" +
         "<LI class='i18n' id='newLogin'" +
         " onclick='authorize();event.stopPropagation()'>" +
         i18n('newLogin') + "</SPAN></LI>" + html + "</UL>"
      );
      tellAccessPass();
   }else {
      if (isUserRequired && confirm(i18n("requestAuth"))) {
         authorize();
      }else {
         document.getElementById("jgUsersIn").innerHTML = (
            "<BUTTON onclick='authorize();event.stopPropagation();' >" +
            "<SPAN class='i18n' id='newLogin'>" + i18n('newLogin') +
            "</SPAN></BUTTON>"
         );
      }
   }
}

function formatAlbumsList(albums, elt) {
   var html = "";
   var selAlbumId = users.getAlbumId();
   var selAlbumTitle = users.getAlbumTitle();
   var isSelAlbumOK = false;
   for (var i=0, max=albums.length; i < max; ++i) {
      var album = albums[i];
      var title = album.title;
      var description = album.description;
      if (!title || (title.length === 0)) title = "no title";
      if (!description || (description.length === 0)) description = "&nbsp;";
      html += "<LI id='" + album.id;
      if (selAlbumId === album.id) {
         html += "' aria-selected='true";
         isSelAlbumOK = true;
      }
      html += (
         "'><IMG src='" + album.thumbnailUrl + "'/><DIV><SPAN>" + title +
         "</SPAN><BR/><SMALL>" + description + "</SMALL></DIV></LI>"
      );
   }
   elt.innerHTML = (
      "<LI class='i18n' id='newAlbum'" +
      " onclick='createAlbum(true);event.stopPropagation();'>" +
      i18n('newAlbum') + "</SPAN></LI>" + html + "</UL>"
   );
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
      albumTitleElt.setAttribute("class", "i18n");
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
         if (confirm(i18n("revokeAccess", event.target.nextSibling.textContent))) {
            users.deleteUserAt(index);
            formatUsersList(false);
         }
      }else {
         users.selectUserAt(index);
         document.getElementById('jgUserName').textContent = users.getUserName();
         var albumTitle = users.getAlbumTitle();
         document.getElementById("jgUsersAid").innerHTML = (
            "<SMALL class='i18n' id='photoAlbum'>" +
            i18n('photoAlbum') +
            "</SMALL><BR/>" + (
                albumTitle?
                ("<i id='albumTitle'>" + albumTitle + "</i>") :
                ("<i class='i18n' id='albumTitle'>" + i18n("albumTitle") + "</i>")
             ) +
            "<UL role='radiogroup' id='albumList' onclick='changeAlbum(this, event)'></UL>"
         );
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

function makeCorsRequest(method, query) {
   var xhr = new XMLHttpRequest({mozSystem: true});
   if (xhr.withCredentials === undefined) {
      alert("Sorry: your browser doesn't handle cross-site requests");
      return;
   }
   xhr.withCredentials = true;
   xhr.open(method, server_url + query, true);
   return xhr;
}

function authorize() {
   // make a pseudo-random key )between 100000 and 200000
   authKey = (Math.floor(Math.random() * 100000) + 100000).toString();
   // obtain the URL at which the user will grant us access
   var xhr = makeCorsRequest("GET", "?OP=getUrl&JXK=" + authKey);
   xhr.onreadystatechange = function() {
      switch (xhr.readyState) {
      case 1: // OPENED
         document.getElementById("progresspane").style.visibility='visible';
         break;
      case 4:
         document.getElementById("progresspane").style.visibility='hidden';
         if (this.status === 200 || this.status === 0) {         // navigate to it as a top browser window
            if (isPackaged) {               // if packaged, do NOT leave the app!
               browseTo(xhr.responseText);  // use a mozbrowser, instead
            }else {                         // if not packaged, we can leave the page
               window.location.href = xhr.responseText;
            }
         }else {
            alert("authorize RC:" + this.status + "\n" + this.responseText);
         }
         break;
      }
   };
   xhr.send();
}

function browseTo(targetUrl) {
   var pane = document.getElementById("browserpane");
   var browserFrame = document.createElement('iframe');
   browserFrame.setAttribute('mozbrowser', 'true');
   browserFrame.classList.add('iframebox');
   pane.appendChild(browserFrame);
   pane.style.visibility = "visible";

   //OT-LH*/ browserFrame.src = server_url + "?OP=backCallTest&JXK=" + authKey " "&oauth_verifier=tombouctou";
   browserFrame.src = targetUrl;

   document.querySelector("footer").style.visibility="hidden";
   document.getElementById("btnMainImage").style.backgroundImage = "url(style/images/close.png)";
   document.getElementById("btnMain").onclick = browseQuit;
   getVerifier();
}

function browseQuit() {
   var pane = document.getElementById("browserpane");
   pane.innerHTML = "";
   pane.style.visibility = "hidden";
   document.querySelector("footer").style.visibility = "visible";
   document.getElementById("btnMainImage").style.backgroundImage = "url(style/images/menu.png)";
   document.getElementById("btnMain").onclick = toggleSidebarView;
}

/*
| for packaged application, this is the way appspot tells us the verifier
*/
function getVerifier() {
   var xhr=makeCorsRequest("GET", "?OP=getVerifier&JXK=" + authKey);
   xhr.onreadystatechange = function() {
      if (xhr.readyState == "4") {
         if (xhr.status != '200' && xhr.status != '0') {
            alert("getVerifier RC:" + xhr.status);
         }else {
            var verifier = this.responseText;
            if (verifier === "???") {
               setTimeout(getVerifier, 1000);
            }else {
               browseQuit();
               //OT-LH*/ alert("Bingo!\nVerifier is: " + verifier);
               registerUser(verifier);
               formatUsersList(false);
            }
         }
      }
   }
   xhr.send();
}

function registerUser(verifier) {
   var xhr = makeCorsRequest(
      "GET",
      "?OP=getAccPss&verifier=" + encodeURIComponent(verifier)
   );
   xhr.onreadystatechange = function () {
      switch (this.readyState) {
      case 1: // OPENED
         document.getElementById("progresspane").style.visibility='visible';
         break;
      case 4: // DONE
         document.getElementById("progresspane").style.visibility='hidden';
         if (this.status !== 200 && this.status !== 0) {
            alert(i18n("authDenied", xhr.responseText));
         }else {
            var val = JSON.parse(xhr.responseText);
            // alert(dump(val));
            users.addUser(
               decodeURIComponent(val.userName),
               decodeURIComponent(val.accessPass),
               "orkut"
            );
            formatUsersList(false);
         }
      }
   };
   xhr.send();
}

function tellAccessPass()
{
   var xhr = makeCorsRequest("POST", "?OP=postAccPss");
   xhr.onreadystatechange = function () {
      if (xhr.readyState === 4) {
         if (this.status !== 200 && this.status !== 0) {
            alert('tellAccess RC: ' + this.status + "\n" + this.responseText);
         }
      }
   };
   xhr.send(users.getAccessPass());
}

function whoAmI() {
   queryFor(
      'whoAmI',
       function(person) {
          var value;
          value = person.name.givenName;
          if (value === null) value = "?";
          document.getElementById("p2_givenName").textContent = value;
          value = person.name.familyName;
          if (value === null) value = "?";
          document.getElementById("p2_familyName").textContent = value;
//        document.getElementById("p2_thumbnail").setAttribute(
//           "src", person.thumbnailUrl
//        );
          value = person.gender;
          if (value === null) value = "?";
          document.getElementById("p2_gender").textContent = value;
          value = person.birthday;
          if (value === null) {
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
      queryFor(
         'listAlbums',
         function(albums) {
            formatAlbumsList(albums, ulChildElt);
            dispatcher.post("albumsListed", albums.length);
         }
      );
   }
}

function createAlbum(isDirect) {
   var resp = prompt(
      i18n(isDirect? 'createAlbumProlog1' : 'createAlbumProlog2') +
      i18n('createAlbum'),
      'Jaxogram / ' + i18n('albumDescr')
   );
   if (resp) {
      var ix = resp.indexOf('/');
      var what = "createAlbum";
      if (ix === -1) {
         what += "&title=" + resp.replace(/^\s+|\s+$/g,'');
      }else {
         what += (
            "&title=" + resp.substr(0, ix).replace(/^\s+|\s+$/g,'') +
            "&descr=" + resp.substr(ix+1).replace(/^\s+|\s+$/g,'')
         );
      }
      queryFor(
         what,
         function(val) {
            users.setAlbum(val.new.id, val.new.title);
            formatAlbumsList(
               val.list,
               document.getElementById("jgUsersAid").
               getElementsByTagName("UL")[0]
            );
         }
      );
   }
}

function queryFor(what, whenDone) {
   if (!users.hasSome()) {
      formatUsersList(true);
      return;
   }
   var xhr = makeCorsRequest("GET", "?OP=" + what);
   xhr.onreadystatechange = function() {
      switch (this.readyState) {
      case 1: // OPENED
         document.getElementById("progresspane").style.visibility='visible';
         break;
      case 4: // DONE
         document.getElementById("progresspane").style.visibility='hidden';
         if (this.status === 200 || this.status === 0) {
            var val = JSON.parse(this.responseText);
            whenDone(val);
         }else {
            dispatcher.clean();
            alert(what + " RC: " + this.status + "\n" + this.responseText);
         }
         break;
      }
   };
   xhr.send();
}

function pickAndUpload(event) {
   if (!users.hasSome()) {
      formatUsersList(true);
   }else {
      var albumId = users.getAlbumId();
      if (!albumId) {
         event.stopPropagation();
         dispatcher.on(
            "albumsListed",
            function action(albumsCount) {
               dispatcher.off("albumsListed", action);
               if (albumsCount > 0) {
                  alert(i18n("selectOrCreateAlbum"));
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
      var xhr = makeCorsRequest("POST", "?OP=postImageData&AID="+albumId);
      xhr.setRequestHeader("Content-Type", 'image/jpeg');
      xhr.onreadystatechange = whenRequestStateChanged;
      xhr.send(a.result.blob);
      try {
         var url = URL.createObjectURL(a.result.blob);
         var img = document.getElementById('photoImage');
         img.src = url;
         img.onload = function() { URL.revokeObjectURL(url); };
      }catch (error) {
         alert("Local error: " + error);
      }
   };
   a.onerror = function() { alert(i18n('pickImageError')); };
}

function uploadFile(albumId) {
   var elt = document.getElementById('upldFile');
   elt.onchange = function() {
      if (typeof window.FileReader !== 'function') {
         alert(i18n("noFileApi"));
      }else if (!this.files) {
         alert(i18n("noFileApiProp"));
      }else if (!this.files[0]) {
         alert("No file selected");
      }else {
         var file = this.files[0];
         var formData = new FormData();
         formData.append("MAX_FILE_SIZE", "1000000");
         formData.append("IMG", file.name.substr(-3));
         formData.append("AID", albumId);
//       formData.append("TIT", "my title");
         formData.append("upldFile", file);
         var xhr = makeCorsRequest("POST", "?OP=postImageFile");
         xhr.onreadystatechange = whenRequestStateChanged;
         xhr.send(formData);
         var reader = new FileReader();
         reader.onload = function (event) {
            document.getElementById("photoImage").src = event.target.result;
         };
         reader.readAsDataURL(file);
      }
   };
   elt.click();
}

function whenRequestStateChanged() {
   switch (this.readyState) {
   case 1: // OPENED
      document.getElementById("progresspane").style.visibility='visible';
      break;
   case 4: // DONE
      document.getElementById("progresspane").style.visibility='hidden';
      if (this.status === 200 || this.status === 0) {
         expandPage("p1");
         alert(i18n('imageUploaded', users.getAlbumTitle()));
      }else {
         alert(this.responseText);
      }
      break;
   }
}

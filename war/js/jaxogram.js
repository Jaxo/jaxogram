var users;

window.onload = function() {
   createDispatcher();
   users = new JgUsers();
   // users.cleanUp();
   // users.destroy();
   var params = getQueryParams();
   if (params.OP === "granted") {
      users.addUser(params.u, params.ap, "orkut");
   }else if (params.OP === "denied") {
      alert(i18n("authDenied") + "\n\n(" + params.msg + ")");
   }
   dispatcher.on(
      "install_changed",
      function action(state) {
         if (
            (state === "uninstalled") && !users.hasSome() &&
            (params.OP !== "denied") && confirm(i18n('betterInstall'))
         ) {
            document.getElementById("btnInstall").click();
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

function authorize() {
   var request = new XMLHttpRequest({mozSystem: true});
   // obtain the URL at which the user will grant us access
   request.open("GET", "http://8.jaxogram.appspot.com/jaxogram?OP=getUrl", true);
   request.onreadystatechange = function() {
      if (request.readyState === 4) {
         if (this.status === 200) {
            // navigate to it...
            window.location.href = request.responseText;
//          window.open(
//             request.responseText,
//             'popUpWindow',
//             'resizable=yes,scrollbars=yes,toolbar=yes,menubar=no,location=no,directories=no, status=yes'
//          );
         }else {
            alert(this.responseText);
         }
      }
   };
   request.send();
}

function tellAccessPass()
{
   var request = new XMLHttpRequest({mozSystem: true});
   request.open("POST", "http://8.jaxogram.appspot.com/jaxogram?OP=postAccPss", true);
   request.onreadystatechange = function () {
      if (request.readyState === 4) {
         if (this.status !== 200) {
            alert('HTTP error ' + this.status);
         }
      }
   };
   request.send(users.getAccessPass());
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
             value = date.getDay() + " " + i18n('months')[date.getMonth()];
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
   var request = new XMLHttpRequest({mozSystem: true});
   request.onreadystatechange = function() {
      switch (this.readyState) {
      case 1: // OPENED
         document.getElementById("progresspane").style.visibility='visible';
         break;
      case 4: // DONE
         document.getElementById("progresspane").style.visibility='hidden';
         if (this.status === 200) {
            var val = JSON.parse(this.responseText);
            whenDone(val);
         }else {
            dispatcher.clean();
            alert(this.responseText);
         }
         break;
      }
   };
   request.open("GET", "http://8.jaxogram.appspot.com/jaxogram?OP=" + what, true);  // ???
   request.send();
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
      var request = new XMLHttpRequest({mozSystem: true});
      request.open(
         "POST",
         "http://8.jaxogram.appspot.com/jaxogram?OP=postImageData&AID="+albumId,
         true
      );
      request.setRequestHeader("Content-Type", 'image/jpeg');
      request.onreadystatechange = whenRequestStateChanged;
      request.send(a.result.blob);
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
   var formElt = document.getElementById('upldForm');
   var elt = formElt.firstChild;
   elt.onchange= function() {
      if (typeof window.FileReader !== 'function') {
         alert(i18n("noFileApi"));
      }else if (!this.files) {
         alert(i18n("noFileApiProp"));
      }else if (!this.files[0]) {
         alert("No file selected");
      }else {
         var file = this.files[0];
         var formData = new FormData(formElt);
         formData.append("MAX_FILE_SIZE", "1000000");
         formData.append("IMG", file.name.substr(-3));
         formData.append("AID", albumId);
//       formData.append("TIT", "a title");
         var request = new XMLHttpRequest({mozSystem: true});
         request.onreadystatechange = whenRequestStateChanged;
         request.open("POST", "http://8.jaxogram.appspot.com/jaxogram?OP=postImageFile", true);
         request.send(formData);
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
      if (this.status === 200) {
         expandPage("p1");
         alert(i18n('imageUploaded', users.getAlbumTitle()));
      }else {
         alert(this.responseText);
      }
      break;
   }
}

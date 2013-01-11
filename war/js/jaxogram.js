var users;

function init() {
   setInstallButton("btnInstall");
// document.getElementById('fdflt').click();
   fitImage(document.getElementById('imageOut'));
   window.addEventListener("resize", fitImages, false);
   document.getElementById("p1").addEventListener(
      'transitionend',
      function(event) {
         p1Expanded(event.target.attributes["aria-expanded"].value == "true");
      },
      false
   );

   users = new JgUsers();
   users.cleanUp(); // TEMP???
   var params = getQueryParams();
   if (params.OP === "granted") {
      var name = prompt(i18n('enterUserName'), i18n("defaultUserName"));
      users.addUser(name, params.ap, "orkut");
   }else if (params.OP === "denied") {
      alert(i18n("authDenied") + "\n\n(" + params.msg + ")");
   }
   if (users.hasSome()) {
      var html = "";
      var selName = null;
      users.forEach(
         function(name, pass, net, selected) {
            html += "<LI";
            if (selected) {
               selName = name;
               html += " aria-selected='true'";
            }
            html += ">" + name + "</LI>"
         }
      );
      document.getElementById("jgUsersIn").innerHTML = (
         "<SPAN class='i18n' id='loginAs'></SPAN>: <i id='jgUserName'>" +
         selName +
         "</i><UL role='radiogroup' onclick='changeLogin(this, event);'>" +
         "<LI onclick='authorize();event.stopPropagation()'>" +
         "<SPAN class='i18n' id='newLogin'></SPAN></LI>" + html + "</UL>"
      );
      tellAccessPass();
   }else {
      if (confirm(i18n("requestAuth"))) {
         authorize();
      }else {
         elt.innerHTML = (
            "<BUTTON onclick='authorize();event.stopPropagation();' >" +
            "<SPAN class='i18n' id='newLogin'></SPAN></BUTTON>"
         );
      }
   }
   var dfltLocale = navigator.language || navigator.userLanguage;
   dfltLocale = "pt_BR"; // FIXME (remove)
   translateBody(dfltLocale);
   document.getElementById('usedLang').innerHTML = i18n(dfltLocale);
}

function changeLogin(elt, event) {
   var clicked = event.target;
   document.getElementById('jgUserName').innerHTML = clicked.innerHTML;
   var index = -1;
   while (clicked=clicked.previousSibling) ++index;
   users.selectUserAt(index);
   tellAccessPass();
}

function changeLanguage(elt, event) {
   var clicked = event.target;
   translateBody(clicked.id);
   document.getElementById('usedLang').innerHTML = clicked.innerHTML;
}

// document.getElementById("p1").setAttribute("aria-expanded", "true");
// window.onerror = function(msg, url, linenumber){
//    alert('Error: ' + msg + '\nURL: ' + url + '\n@ line: ' + linenumber);
//    return true;
// }

function p1Expanded(isExpanded) {
   // var style = document.getElementById("btnSecond").style;
   // if (isExpanded) {
   //    style.display ="";
   // }else {
   //    style.display = "none";
   // }
}

function fitImages(img) {
   // workaround, until "object-fit:contain;" gets implemented
   // fitImage(document.getElementById('barImageIn'));
   fitImage(document.getElementById('imageOut'));
}
function fitImage(img) {
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
   if (query == "") return {};
   var params = {};
   for (var i=0; i < query.length; ++i) {
       var param = query[i].split('=');
       if (param.length == 2) {
          params[param[0]] = decodeURIComponent(param[1].replace(/\+/g, " "));
       }
   }
   return params;
}

function authorize() {
   var request = new XMLHttpRequest();
   request.onreadystatechange = whenRequestStateChanged;
   // obtain the URL at which the user will grant us access
   request.open("GET", "jaxogram?OP=getUrl", true);
   request.onreadystatechange = function() {
      if (request.readyState == 4) {
         if (this.status == 200) {
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
   }
   request.send();
}

function tellAccessPass()
{
   var request = new XMLHttpRequest();
   request.open("POST", "jaxogram?OP=postAccPss", true);
   request.onreadystatechange = function () {
      if (request.readyState == 4) {
         if (this.status != 200) {
            alert('HTTP error ' + this.status);
         }
      }
   }
   request.send(users.getAccessPass());
}

function queryWhoAmI() {
   queryFor('whoAmI');
}
function queryListAlbums() {
   queryFor('listAlbums');
}

function queryFor(what) {
   var request = new XMLHttpRequest();
   request.onreadystatechange = function() {
      switch (this.readyState) {
      case 1: // OPENED
         document.getElementById("progresspane").style.visibility='visible';
         break;
      case 4: // DONE
         document.getElementById("progresspane").style.visibility='hidden';
         if (this.status == 200) {
            var val = JSON.parse(this.responseText)
            console.log(val);
            alert(dump(val));
         }else {
            alert(this.responseText);
         }
         break;
      }
   }
   request.open("GET", "jaxogram?OP=" + what, true);  // ???
   request.send();
}

function uploadFile() {
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
         var request = new XMLHttpRequest();
         request.onreadystatechange = whenRequestStateChanged;
//       request.open("POST", "decodeFile", true);
         request.open("POST", "jaxogram?OP=postImageFile", true);
         request.send(formData);
         var reader = new FileReader();
         reader.onload = function (event) {
            document.getElementById("imageOut").src = event.target.result;
         };
         reader.readAsDataURL(file);
      }
   }
   elt.click();
}

function pickAndUploadImage()
{
   try {
      var a = new MozActivity({ name: "pick", data: {type: "image/jpeg"}});
      a.onsuccess = function(e) {
        var request = new XMLHttpRequest();
        request.open(
           "POST",
           "jaxogram?OP=postImageData",
           true
        );
        request.setRequestHeader("Content-Type", 'image/jpeg');
        request.onreadystatechange = whenRequestStateChanged;
        request.send(a.result.blob);
        var url = URL.createObjectURL(a.result.blob);
        var img = document.getElementById('imageOut');
        img.src = url;
        img.onload = function() { URL.revokeObjectURL(url); };
      };
      a.onerror = function() { alert(i18n('pickImageError')); };
   }catch (err) {
      uploadFile();
      return;
   }
}

function whenRequestStateChanged() {
   switch (this.readyState) {
   case 1: // OPENED
      document.getElementById("progresspane").style.visibility='visible';
      break;
   case 4: // DONE
      document.getElementById("progresspane").style.visibility='hidden';
//    if (this.status == 200) {
//       document.getElementById('barDataOut').innerHTML = this.responseText;
//    }else {
         alert(this.responseText);
//    }
      break;
   }
}

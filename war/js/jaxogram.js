function init() {
   createLocalStorageIfNeeded();
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
   var params = getQueryParams();
   if (params.OP === "backCall") {
      alert("Access Pass: \"" + params.ap);
   }
// document.getElementById("p1").setAttribute("aria-expanded", "true");
// window.onerror = function(msg, url, linenumber){
//    alert('Error: ' + msg + '\nURL: ' + url + '\n@ line: ' + linenumber);
//    return true;
// }
}

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
   // 1st is to get the URL to which the user will grant our access
   request.open("GET", "jaxogram?OP=getUrl", true);
   request.onreadystatechange = function() {
      if (request.readyState == 4) {
         if (this.status == 200) {
            // window.open(
            //    request.responseText,
            //    'popUpWindow',
            //    'resizable=yes,scrollbars=yes,toolbar=yes,menubar=no,location=no,directories=no, status=yes'
            // );
            window.location.href = request.responseText;
         }else {
            alert(this.responseText);
         }
      }
   }
   request.send();
}

function queryFor(what) {
   var request = new XMLHttpRequest();
   request.onreadystatechange = whenRequestStateChanged;
   request.open("GET", "jaxogram?OP=" + what, true);  // ???
   request.send();
}

function uploadFile() {
   var formElt = document.getElementById('upldForm');
   var elt = formElt.firstChild;
   elt.onchange= function() {
      if (typeof window.FileReader !== 'function') {
         alert("The file API isn't supported on this browser yet.");
      }else if (!this.files) {
         alert(
           "Your browser doesn't seem to support" +
           " the `files` property of file inputs."
         );
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
      a.onerror = function() { alert('Failure at picking an image'); };
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

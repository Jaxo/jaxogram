// FIXME Need a protection mechanism.
// see: https://developers.google.com/appengine/docs/java/users/overview
var localFilesList;
var serverFilesList;
var imagesList = [
   {
      name: "Cirques",
      src: "Cirques.jpg"
   },{
      name: "Clown Fishes",
      src: "ClownFishes.jpg"
   },{
      name: "La Licorne",
      src: "Licorne.png"
   }
];

window.onload = function() {
   /*
   avoid this:
   "Sorry.\n" +
   "For security reasons, \"vignettization\" cannot be applied\n"+
   "to image data created outside of the browser.\n\n" +
   "Please, use an image that has been obtained by pressing\n" +
   "the \"Load additional images\" button."
   */
   var count = 0;
   for (var i=0; i < imagesList.length; ++i) {
      fileToBlob(
         imagesList[i],
         function() { if (++count == imagesList.length) doOnLoad();}
      );
   }
}

function doOnLoad() {
   initFilters();
   createFilesManager();
   localFilesList = new FilesList("localFilesList");
   document.getElementById('filtersList').onchange = function() {
      var file = filesmanager.open(this.selectedIndex - 1);
      currentFilter.restore(file? file.data : null);
      filterImage();
   };
   localFilesList.addButton("Remove").onclick = function() {
      alert("Sorry: to be implemented");
//    var filterIndeces = localFilesList.getSelectedRows();              // OUH
//    localFilesList.clear();                                            // OUH
//    if (filterIndeces.length > 0) {                                    // OUH
//       var file = filesmanager.open(filterIndeces[0]);                 // OUH
//       filterImage();                                                  // OUH
//    }                                                                  // OUH
   };
   localFilesList.addButton("Rename").onclick = function() {
      alert("Sorry: to be implemented");
   };
   serverFilesList = new FilesList("serverFilesList");
   serverFilesList.addButton("Import").onclick = function() {
      var fileNames = [];
      serverFilesList.getSelectedRows().forEach(
         function(rowNo) {
            fileNames.push(serverFilesList.getCellValue(rowNo, 0));
         }
      );
      serverFilesList.clear();
      filesmanager.serverImport(fileNames);
   };

   document.getElementById("manageFilters").onclick = function() {
      filesmanager.dir(localFilesList);   // populate
   }
   document.getElementById("saveFilter").onclick = function() {
      filesmanager.save(JSON.stringify(currentFilter));
      buildFiltersList();
   };
   document.getElementById("saveFilterAs").onclick = function() {
      filesmanager.save(
         JSON.stringify(currentFilter), true
      ).name;
      buildFiltersList();
   };
   document.getElementById("importFilter").onclick = function() {
      filesmanager.serverDir(                  // populate
         "application/json;jaxo_type=jxf",
         serverFilesList
      );
      // var input = document.getElementById("filterFileInput");
      // input.onchange = function() { filesmanager.localImport(this.files); }
      // input.click();
   }
   document.getElementById("exportFilter").onclick = function() {
      // filesmanager.localExport(JSON.stringify(currentFilter));
      var fileName = prompt(
         "Warning" +
         "\n\nFor this version of the Filter Factory, " +
         "\nexported filters definitively replace any" +
         "\nexisting ones on the Server." +
         "\n\nPlease, make sure this is what you want," +
         "\nor use a unique name." +
         "\n\nExport as:",
         filesmanager.currentFileName()
      );
      if (fileName) {
         filesmanager.serverExport(
            JSON.stringify(currentFilter),
            fileName,
            "application/json;jaxo_type=jxf"
         );
      }
   };
   document.getElementById('upldFile').onchange = function() {
      if (this.files && this.files[0]) {
         var curLen = imagesList.length;
         for (var i=0; i < this.files.length; ++i) {
            imagesList.push({
               name: this.files[i].name,
               src: URL.createObjectURL(this.files[i])
            });
         }
         if (curLen < imagesList.length) { // next new picture
            buildImagesList(curLen);
         }
      }
   }
   document.getElementById("originalImage").onload = function() {
      filterImage();
   }
   document.getElementById('imagesList').onchange = function() {
      var workIndex = this.selectedIndex;
      if ((workIndex+1) >= this.options.length) {
         document.getElementById("upldNext").setAttribute("disabled", "true");
      }else {
         document.getElementById("upldNext").removeAttribute("disabled");
      }
      if (workIndex <= 0) {
         document.getElementById("upldPrev").setAttribute("disabled", "true");
      }else {
         document.getElementById("upldPrev").removeAttribute("disabled");
      }
      if (workIndex >= 0) {
         this.removeAttribute("disabled");
         document.getElementById("originalImage").src = imagesList[workIndex].src;
      }else {
         this.setAttribute("disabled", "true");
      }
   }
   document.getElementById("upldBtn").onclick = function() {
      document.getElementById("upldFile").click();
   }
   document.getElementById("upldPrev").onclick = function() {
      var selElt = document.getElementById('imagesList');
      --selElt.selectedIndex;
      selElt.onchange();
   }
   document.getElementById("upldNext").onclick = function() {
      var selElt = document.getElementById('imagesList');
      ++selElt.selectedIndex;
      selElt.onchange();
   }
   buildImagesList();
   buildFiltersList();
}

function buildImagesList(selectedIndex) {
   var selElt = document.getElementById('imagesList');
   var options = selElt.options;
   var workIndex = selectedIndex? selectedIndex : selElt.selectedIndex;
   options.length = 0;
   if (workIndex == -1) workIndex = 0;
   imagesList.forEach(
      function(image, index) {
         ++options.length;
         options[index].text = image.name;
      }
   );
   while (workIndex >= options.length) --workIndex;
   selElt.selectedIndex = workIndex;
   selElt.onchange();
}

function buildFiltersList() {
   var selElt = document.getElementById('filtersList');
   var options = selElt.options;
   // var workIndex = selectedIndex? selectedIndex : selElt.selectedIndex;
   options.length = 1;
   options[0].text = "[New Filter]";
   // options[0].setAttribute("disabled", "true");
   // if (workIndex == -1) workIndex = 0;
   filesmanager.dir({
      populate: function(files) {
         files.forEach(
            function(file) { options[options.length++].text = file.name; }
         );
      }
   });
   options[1+filesmanager.currentSelection()].setAttribute("selected", "true");
// selElt.onchange();   OUH
}

function fileToBlob(image, whenDone) {
   // see https://developer.mozilla.org/en-US/docs/DOM/HTMLCanvasElement#Example.3A_Getting_a_file_representing_the_canvas
   var img = new Image();
   img.onload = function() {
      var canvas = document.createElement("CANVAS");
      canvas.width = img.width;
      canvas.height = img.height;
      canvas.getContext('2d').drawImage(img, 0, 0);
      canvas.toBlob(
         function(blob) {
            image.src = URL.createObjectURL(blob);
            whenDone();
         }
      );
   }
   img.src = image.src;
}

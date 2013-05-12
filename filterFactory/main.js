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
         i,
         function() { if (++count == imagesList.length) doOnLoad();}
      );
   }
}
function onFilterOpened(name, data) {
   document.getElementById("filterName").textContent = name;
   alert(name + " => \"" + data + "\""); // <== parse the data!
}
function getFilterData(name) {
   document.getElementById("filterName").textContent = name;
   return document.getElementById("filterValue");
}
function doOnLoad() {
   initFilters();
   createFilesManager();
   document.getElementById("loadFilter").appendChild(
      filesmanager.openList(onFilterOpened)
   );
   document.getElementById("saveFilter").onclick = function() {
      filesmanager.save(getFilterData);
   };
   document.getElementById("saveFilterAs").onclick = function() {
      filesmanager.save(getFilterData, true);
   };
   document.getElementById("importFilter").onclick = function() {
      var input = document.getElementById("filterFileInput");
      input.onchange = function() { filesmanager.import(this.files); }
      input.click();
   }
   document.getElementById("exportFilter").onclick = function() {
      filesmanager.export(document.getElementById("filterValue"));
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
}

function fileToBlob(i, whenDone) {
   // see https://developer.mozilla.org/en-US/docs/DOM/HTMLCanvasElement#Example.3A_Getting_a_file_representing_the_canvas
   var image = imagesList[i];
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

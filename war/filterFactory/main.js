// FIXME Need a protection mechanism.
// see: https://developers.google.com/appengine/docs/java/users/overview
var filtersMarkableTable;
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

function doOnLoad() {
   initFilters();
   initFiltersImport();
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
      filesmanager.serverDir(
         "application/json;jaxo_type=jxf", populateFiltersImport
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
}

function onFilterOpened(name, data) {
   document.getElementById("filterName").textContent = name;
   currentFilter.restore(data);
   filterImage();
}
function getFilterData(name) {
   document.getElementById("filterName").textContent = name;
   // return document.getElementById("filterValue").textContent;
   return JSON.stringify(currentFilter);
}

function initFiltersImport() {
   var filtersTable = document.getElementById("filtersTable");
   filtersMarkableTable = new MarkableTable(filtersTable);
   filtersTable.onclick = function(event) {
      var elt = event.target;      // the item that was clicked
      while ((elt != null) && (elt.nodeName != "TR")) {
         elt = elt.parentNode;
      }
      if (elt && (elt.className === "file")) {
         event.preventDefault();
         event.stopPropagation();
         filtersMarkableTable.clicked(event, elt.rowIndex);
      }
   }
}

function populateFiltersImport(result) {
   var row;
   var cell0;
   var cell1;
   var cell2;
   var cell3;
   var date;
   var tblBody = (
      document.getElementById(
         "filtersTable"
      ).getElementsByTagName("TBODY")
   )[0];
   tblBody.innerHTML = "";
   result.forEach(
      function(entry) {
         row = document.createElement("TR");
         row.className = "file";
         cell0 = document.createElement("TD");
         cell1 = document.createElement("TD");
         cell2 = document.createElement("TD");
         cell3 = document.createElement("TD");
         date = entry.creation.split(' ');
         cell0.textContent = entry.name;
         cell1.textContent = entry.size;
         cell2.textContent = date[0]+" "+date[1]+" "+date[2]+", "+date[5];
         cell3.textContent = date[3]+" "+date[4];
         row.appendChild(cell0);
         row.appendChild(cell1);
         row.appendChild(cell2);
         row.appendChild(cell3);
         tblBody.appendChild(row);
      }
   );
   var btn0 = document.createElement("BUTTON");
   var btn1 = document.createElement("BUTTON");
   btn0.textContent = "Cancel";
   btn0.style = "margin-right:1rem";
   btn0.onclick = resetFiltersImport;
   btn1.textContent = "Import";
   btn1.onclick = function() {
      var fileNames = filtersMarkableTable.getSelected(0);
      resetFiltersImport();
      filesmanager.serverImport(fileNames);
   }
   cell0 = document.createElement("TD");
   cell0.setAttribute("colspan", "4");
   cell0.style = "text-align:right";
   cell0.appendChild(btn0);
   cell0.appendChild(btn1);
   row = document.createElement("TR");
   row.appendChild(cell0);
   tblBody.appendChild(row);
   document.getElementById("filtersTblCtnr").style.visibility = "visible";
}

function resetFiltersImport() {
   document.getElementById("filtersTblCtnr").style.visibility = "hidden";
   filtersMarkableTable.reset();
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

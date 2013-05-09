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
   initFilters();

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
      document.getElementById("filteredImage").src = this.src;
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



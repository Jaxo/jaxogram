window.onload = function() {
   console.log("share: loading...");
   try {
      navigator.mozSetMessageHandler(
         "activity",
         function(issuer) {
            document.getElementById("OK").onclick = function() {
               console.log("share: message processed.");
               issuer.postResult("shared");
            }
            var log = (
               "share: message received!\n\t" +
               "activity: " + issuer + "\n\t" +
               "name:     " + issuer.source.name + "\n\t" +
               "images:   " + issuer.source.data.blobs.length + "\n"
            );
            var data = issuer.source.data;
            var blobs = data.blobs;
            var filenames = data.filenames;
            blobs.forEach(
               function(blob, index) {
//                var url = URL.createObjectURL(blob);
//                var img = document.createElement("IMG");
//                img.style.width = "100px";
//                img.src = url;
//                img.onload = function() { URL.revokeObjectURL(url); };
//                document.body.appendChild(img);
//                var label = document.createElement("SPAN");
//                label.textContent = filenames[index];
//                document.body.appendChild(label);
                  log += filenames[index] + "\n";
               }
            );
            document.getElementById("log").textContent = log;
            if (issuer.source.name === "share") {
               // addImages(issuer.source.data);
            }
         }
      );
   }catch (error) {
      alert("Error: " + error);
   }
   console.log("share: loaded.");
};

function addImages(data) {
   var blobs = data.blobs;
   var filenames = data.filenames;
   blobs.forEach(function(blob, index) {
      var url = URL.createObjectURL(blob);
      var img = document.createElement("IMG");
      img.style.width = "100px";
      img.src = url;
      img.onload = function() {
         URL.revokeObjectURL(url); };
         document.body.appendChild(img);
         var label = document.createElement("SPAN");
         label.textContent = filenames[index];
         document.body.appendChild(label);
      }
   );
}

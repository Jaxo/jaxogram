var photosDB;

function initPhotosDB() {
   var scanning = 0;
   photosDB = MediaDB.init(
      'pictures',
      null, // metadataParsers.imageMetadataParser,
      { mimeTypes: ['image/jpeg', 'image/png'], version: 2 }
   );
   photosDB.onunavailable = function(event) {
      var why = event.detail;
      if (why === MediaDB.NOCARD) {
         alert("No SD card");
      }else if (why === MediaDB.UNMOUNTED) {
         alert("SD card unmounted");
      }
   };
   photosDB.onready = function() {
      alert("Photo DB is ready to serve!");
   };
   photosDB.onscanstart = function onscanstart() {
      if (scanning++ == 1) {
         // Show the scanning indicator
      }
   };
   photosDB.onscanend = function onscanend() {
      if (--scanning == 0) {
         // Hide the scanning indicator
      }
   };
   photosDB.oncreated = function(event) {
      // event.detail.forEach(fileCreated);
   };
   photosDB.ondeleted = function(event) {
      // event.detail.forEach(fileDeleted);
   };
   // photosDB.addFile(filename, blob);   <<<<<<<<<<<<< BINGO!
}

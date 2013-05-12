function createFilesManager() {
// localStorage.removeItem("jaxo_filters");
// localStorage.setItem(
//    "jaxo_filters",
//    JSON.stringify([
//       {
//          name: "Aurora",
//          data: "AaAaAaAa"
//       }, {
//          name: "Nicole #1",
//          data: "BbBbBbBb"
//       }, {
//          name: "Nicole #2",
//          data: "CcCcCcCc"
//       }
//    ])
// );
   if (!window.filesmanager) {
      Object.defineProperty(
         window,
         "filesmanager",
         new(
            function() {
               var filesmanager = {};
               var files = [];
               var index = -1;
               var opList;
               init();
               this.get = function() {
                  return filesmanager;
               };
               this.configurable = false;
               this.enumerable = true;
               //------------------------------------------
               Object.defineProperty(
                  filesmanager,
                  "openList",
                  {
                     value: function(whenFileOpened) {
                        opList.onclick = function(event) {
                           var elt = event.target;
                           if (elt.id && elt.id.startsWith("fileOp")) {
                              index = elt.id.substr(6);
                              var file = files[index];
                              whenFileOpened(file.name, file.data);
                           }
                        }
                        return opList;
                     },
                     configurable: false,
                     enumerable: false
                  }
               );
               //------------------------------------------
               Object.defineProperty(
                  filesmanager,
                  "save",
                  {
                     value: function(getFileData, asCopy) {
                        if ((index == -1) || asCopy) {
                           var name = prompt("Filter name:", "");
                           if (!name) {
                              return;
                           }else {
                              for (var i=0; ; ++i) {
                                 if (i == files.length) {
                                    var item;
                                    index = i;
                                    files.push({ name: name });
                                    item = document.createElement("LI");
                                    item.textContent = name;
                                    item.id = "fileOp" + i;
                                    opList.appendChild(item);
                                    break;
                                 }else if (files[i].name === name) {
                                    if (!confirm(name +" already exits.\nReplace?")) return;
                                    index = i;
                                    break;
                                 }
                              }
                           }
                        }
                        files[index].data = getFileData(files[index].name);
                        localSave();
                        alert(files[index].name + " saved.");
                     },
                     configurable: false,
                     enumerable: false
                  }
               );
               //------------------------------------------
               Object.defineProperty(
                  filesmanager,
                  "import",
                  {
                     value: function(newFiles) {
                        var self = this;
                        if (newFiles) {
                           var cnt = newFiles.length;
                           for (var i=0, max=cnt; i < max; ++i) {
                              doImport(
                                 newFiles[i],
                                 function() {
                                    if (--cnt == 0) {
                                       localSave.call(self);
                                    }
                                 }
                              );
                           }
                        }
                     },
                     configurable: false,
                     enumerable: false
                  }
               );
               //------------------------------------------
               Object.defineProperty(
                  filesmanager,
                  "export",
                  {
                     value: function(data, whenDone, whenFailed) {
                        doExport(data, whenDone, whenFailed)
                     },
                     configurable: false,
                     enumerable: false
                  }
               );
               //------------------------------------------
               function init() {
                  var item;
                  opList = document.createElement("UL");
                  var temp = localStorage.getItem("jaxo_filters");
                  if (temp) {
                     files = JSON.parse(temp);
                     for (var i=0; i < files.length; ++i) {
                        item = document.createElement("LI");
                        item.textContent = files[i].name;
                        item.id = "fileOp" + i;
                        opList.appendChild(item);
                     }
                  }
               }
               //------------------------------------------
               function doImport(file, whenDone) {
                  var name = file.name;
                  var reader = new FileReader();
                  reader.onload = function(evt) {
                     var ix = files.length;
                     files.push({
                        name: name,
                        data: evt.target.result
                     });
                     item = document.createElement("LI");
                     item.textContent = name;
                     item.id = "fileOp" + ix;
                     opList.appendChild(item);
                     whenDone();
                  };
                  reader.readAsBinaryString(file);
               }
               //------------------------------------------
               function localSave() {
                  localStorage.setItem("jaxo_filters", JSON.stringify(files));
               }
               //------------------------------------------
               function doExport(data) {
                  var form = document.createElement("FORM");
                  var input = document.createElement("INPUT");
                  input.setAttribute("type", "hidden");
                  input.setAttribute("name", "data");
                  input.setAttribute("value", data);
                  form.setAttribute(
                     "action",
                     "http://jaxogram.appspot.com/jaxogram?OP=publish"
                  );
                  form.setAttribute("method", "POST");
                  form.style.display = "none";
                  form.appendChild(input);
                  document.body.appendChild(form);
                  form.submit();
                  document.body.removeChild(form);
               }
               //------------------------------------------
            }
         )()
      );
   }
}

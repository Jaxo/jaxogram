function createFilesManager() {
   localStorage.removeItem("jaxo_filters");
   localStorage.setItem(
      "jaxo_filters",
      JSON.stringify([
         {
            name: "Aurora",
            data: "AaAaAaAa"
         }, {
            name: "Nicole #1",
            data: "BbBbBbBb"
         }, {
            name: "Nicole #2",
            data: "CcCcCcCc"
         }
      ])
   );
   if (!window.filesmanager) {
      Object.defineProperty(
         window,
         "filesmanager",
         new(
            function() {
               var filesmanager = {};
               var files = [];
               var opList;
               var svList;
               init();
               this.get = function() {
                  return filesmanager;
               };
               this.configurable = false;
               this.enumerable = true;

               function init() {
                  var item;
                  opList = document.createElement("UL");
                  svList = document.createElement("UL");
                  item = document.createElement("LI");
                  item.textContent = "New...";
                  item.id = "fileNew";
                  svList.appendChild(item);
                  var temp = localStorage.getItem("jaxo_filters");
                  if (temp) {
                     files = JSON.parse(temp);
                     for (var i=0; i < files.length; ++i) {
                        if (i==0) {
                           item = document.createElement("LI");
                           item.className = "sepa";
                           svList.appendChild(item);
                        }
                        var name = files[i].name;
                        item = document.createElement("LI");
                        item.textContent = name;
                        item.id = "fileSv" + i;
                        item.textContent = name;
                        svList.appendChild(item);
                        item = document.createElement("LI");
                        item.textContent = name;
                        item.id = "fileOp" + i;
                        item.textContent = name;
                        opList.appendChild(item);
                     }
                  }else {
                     // opList.setAttribute("disabled", "true");
                     files = [];
                  }
               }
               //------------------------------------------
               Object.defineProperty(
                  filesmanager,
                  "openList",
                  {
                     value: function(whenFileOpened) {
                        opList.onclick = function(event) {
                           var elt = event.target;
                           if (elt.id && elt.id.startsWith("fileOp")) {
                              var file = files[elt.id.substr(6)];
                              whenFileOpened(file.name, file.data);
                           }
                           event.stopPropagation();
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
                  "saveList",
                  {
                     value: function(getFileData) {
                        svList.onclick = function(event) {
                           var elt = event.target;
                           var index = -1;
                           if (elt.id) {
                              if (elt.id.startsWith("fileSv")) {
                                 index = elt.id.substr(6);
                              }else if (elt.id === "fileNew") {
                                 var name = prompt("Filter name:", "");
                                 if (name) {
                                    for (var i=0; ; ++i) {
                                       if (i == files.length) {
                                          var item;
                                          index = i;
                                          files.push({ name: name });
                                          item = document.createElement("LI");
                                          item.textContent = name;
                                          item.id = "fileSv" + i;
                                          this.appendChild(item);
                                          item = document.createElement("LI");
                                          item.textContent = name;
                                          item.id = "fileOp" + i;
                                          opList.appendChild(item);
                                          // opList.removeAttribute("disabled");
                                          break;
                                       }else if (files[i].name === name) {
                                          if (confirm("Replace?")) index = i;
                                          break;
                                       }
                                    }
                                 }
                              }
                              if (index >= 0) {
                                 files[index].data = getFileData(
                                    files[index].name
                                 );
                              }
                           }
                           event.stopPropagation();
                        }
                        return svList;
                     },
                     configurable: false,
                     enumerable: false
                  }
               );
               //------------------------------------------
//             Object.defineProperty(
//                filesmanager,
//                "saveCurrent",
//                {
//                   value: function(getFileData) {
//                      svList.onclick = function(event) {
//                         var elt = event.target;
//                         var index = -1;
//                         if (elt.id) {
//                            if (elt.id.startsWith("fileSv")) {
//                               index = elt.id.substr(6);
//                            }else if (elt.id === "fileNew") {
//                               var name = prompt("Filter name:", "");
//                               if (name) {
//                                  for (var i=0; ; ++i) {
//                                     if (i == files.length) {
//                                        var item;
//                                        index = i;
//                                        files.push({ name: name });
//                                        item = document.createElement("LI");
//                                        item.textContent = name;
//                                        item.id = "fileSv" + i;
//                                        this.appendChild(item);
//                                        item = document.createElement("LI");
//                                        item.textContent = name;
//                                        item.id = "fileOp" + i;
//                                        opList.appendChild(item);
//                                        // opList.removeAttribute("disabled");
//                                        break;
//                                     }else if (files[i].name === name) {
//                                        if (confirm("Replace?")) index = i;
//                                        break;
//                                     }
//                                  }
//                               }
//                            }
//                            if (index >= 0) {
//                               files[index].data = getFileData(
//                                  files[index].name
//                               );
//                            }
//                         }
//                         event.stopPropagation();
//                      }
//                      return svList;
//                   },
//                   configurable: false,
//                   enumerable: false
//                }
//             );
//             //------------------------------------------
            }
         )()
      );
   }
}

function createFilesManager() {
   // localStorage.removeItem("jaxo_filters");
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
               //--------------------------------------------------------------
               Object.defineProperty(
                  filesmanager,
                  "currentFileName",
                  {
                     value: function() {
                        return (index==-1)? "" : files[index].name;
                     },
                     configurable: false,
                     enumerable: false
                  }
               );
               //--------------------------------------------------------------
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
               //--------------------------------------------------------------
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
                                    if (!confirm(name +" already exits.\nReplace?")) {
                                       return;
                                    }
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
               //--------------------------------------------------------------
               Object.defineProperty(
                  filesmanager,
                  "localImport",
                  {
                     value: function(newFiles) {
                        var self = this;
                        if (newFiles) {
                           var cnt = newFiles.length;
                           for (var i=0, max=cnt; i < max; ++i) {
                              readFromClientStorage(
                                 newFiles[i],
                                 function() {
                                    if (--cnt == 0) localSave.call(self);
                                 }
                              );
                           }
                        }
                     },
                     configurable: false,
                     enumerable: false
                  }
               );
               //--------------------------------------------------------------
               Object.defineProperty(
                  filesmanager,
                  "localExport",
                  {
                     value: function(data) { writeToClientStorage(data); },
                     configurable: false,
                     enumerable: false
                  }
               );
               //--------------------------------------------------------------
               Object.defineProperty(
                  filesmanager,
                  "serverImport",
                  {
                     value: function(filePaths) {
                        var self = this;
                        if (filePaths) {
                           var cnt = filePaths.length;
                           for (var i=0, max=cnt; i < max; ++i) {
                              readFromBlobStore(
                                 filePaths[i],
                                 function() {
                                    if (--cnt == 0) localSave.call(self);
                                 }
                              );
                           }
                        }
                     },
                     configurable: false,
                     enumerable: false
                  }
               );
               //--------------------------------------------------------------
               Object.defineProperty(
                  filesmanager,
                  "serverExport",
                  {
                     value: function(data, filePath, mimeType) {
                        writeToBlobStore(data, filePath, mimeType);
                     },
                     configurable: false,
                     enumerable: false
                  }
               );
               //--------------------------------------------------------------
               Object.defineProperty(
                  filesmanager,
                  "serverDir",
                  {
                     value: function(mimeType, whenDone) {
                        getBlobStoreDir(mimeType, whenDone);
                     },
                     configurable: false,
                     enumerable: false
                  }
               );
               //--------------------------------------------------------------
               function init() {
                  var item;
/**/              var reqdFilterNames = ["Nichole1", "Nichole2"];
                  opList = document.createElement("UL");
                  var temp = localStorage.getItem("jaxo_filters");
                  if (temp) {
                     files = JSON.parse(temp);
                     for (var i=0; i < files.length; ++i) {
                        item = document.createElement("LI");
                        item.textContent = files[i].name;
/**/                    reqdFilterNames.forEach(
/**/                       function(name, index) {
/**/                          if (name === files[i].name) {
/**/                             reqdFilterNames[index] = undefined;
/**/                          }
/**/                       }
/**/                    );
                        item.id = "fileOp" + i;
                        opList.appendChild(item);
                     }
                  }
/**/              reqdFilterNames.forEach(
/**/                 function(name) {
/**/                    if (name) {
/**/                       readJsonFileFromServer(
/**/                          name,
/**/                          function(data) {
/**/                             files.push({ name: name, data: data});
/**/                             item = document.createElement("LI");
/**/                             item.textContent = name;
/**/                             item.id = "fileOp" + (files.length-1);
/**/                             opList.appendChild(item);
/**/                          }
/**/                       )
/**/                    }
/**/                 }
/**/              );
               }
               //--------------------------------------------------------------
               function localSave() {
                  localStorage.setItem("jaxo_filters", JSON.stringify(files));
               }
               //--------------------------------------------------------------
               function readFromClientStorage(file, whenDone) {
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
               //--------------------------------------------------------------
               function writeToClientStorage(data) {
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
               //--------------------------------------------------------------
               function readFromBlobStore(filePath, whenDone) {
                  var xhr = new XMLHttpRequest();
                  var url = "../jaxogram?OP=blob&ACT=load&FN=" + filePath;
                  xhr.open("GET", url, true);
                  xhr.onreadystatechange = function () {
                     if (this.readyState === 4) {
                        if ((this.status === 200) || (this.status === 0)) {
                           var ix = files.length;
                           files.push({
                              name: filePath,
                              data: this.responseText
                           });
                           item = document.createElement("LI");
                           item.textContent = filePath;
                           item.id = "fileOp" + ix;
                           opList.appendChild(item);
                           whenDone();
                        }else {
                           alert("Can't load \"" + filePath + "\" RC:" + this.status);
                        }
                     }
                  };
                  xhr.send();
               }
               //--------------------------------------------------------------
               function writeToBlobStore(contents, filePath, mimeType)
               {
                  var form = new FormData();
                  form.append("FN", filePath);
                  form.append("MT", mimeType);
                  form.append("CNT", contents);
                  var xhr = new XMLHttpRequest();
                  xhr.open("POST", "../jaxogram?OP=blob&ACT=store", true);
                  xhr.onreadystatechange = function () {
                     if (this.readyState === 4) {
                        if ((this.status !== 200) && (this.status !== 0)) {
                           alert("Can't store \"" + filePath + "\" RC:" + this.status);
                        }
                     }
                  };
                  xhr.send(form);
               }
               //--------------------------------------------------------------
               function getBlobStoreDir(mimeType, whenDone) {
                  var xhr = new XMLHttpRequest();
                  var url = "../jaxogram?OP=blob&ACT=dir&MT=" + mimeType;
                  xhr.open("GET", url, true);
                  xhr.onreadystatechange = function () {
                     if (this.readyState === 4) {
                        if ((this.status === 200) || (this.status === 0)) {
                           whenDone(JSON.parse(this.responseText));
                        }else {
                           alert("Can't dir \"" + filePath + "\" RC:" + this.status);
                        }
                     }
                  };
                  xhr.send();
               }
               //--------------------------------------------------------------
               function readJsonFileFromServer(filePath, whenDone) {
                  var xhr = new XMLHttpRequest();
                  xhr.open("GET", filePath, true);
                  xhr.overrideMimeType("application/json");
                  xhr.onreadystatechange = function () {
                     if (this.readyState === 4) {
                        if ((this.status === 200) || (this.status === 0)) {
                           whenDone(this.responseText);
                        }else {
                           alert("Can't read \"" + filePath + "\" RC:" + this.status);
                        }
                     }
                  };
                  xhr.send();
               }
               //--------------------------------------------------------------
            }
         )()
      );
   }
}


function createFilesManager() {
   localStorage.removeItem("jaxo_filters");
   if (!window.filesmanager) {
      Object.defineProperty(
         window,
         "filesmanager",
         new(
            function() {
               var filesmanager = {};
               var files = [];
               var index = -1;
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
                  "open",
                  {
                     value: function(fileNo) {
                        index = fileNo;
                        return files[fileNo];
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
                     value: function(data, asCopy) {
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
                        files[index].data = data;
                        files[index].creation =  new Date().toUTCString();
                        files[index].size =  data.length;
                        localSave();
                        alert(files[index].name + " saved in your workspace.");
                        return files[index];
                     },
                     configurable: false,
                     enumerable: false
                  }
               );
               //--------------------------------------------------------------
               Object.defineProperty(
                  filesmanager,
                  "dir",
                  {
                     value: function(fileList) {
                        fileList.populate.call(fileList, files);
                     },
                     configurable: false,
                     enumerable: false
                  }
               );

               //--------------------------------------------------------------
               Object.defineProperty(
                  filesmanager,
                  "clientImport",
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
                  "clientExport",
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
                                    if (--cnt == 0) {
                                       localSave.call(self);
                                       alert("Files imported to your workspace");
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
                     value: function(mimeType, fileList) {
                        getBlobStoreDir(mimeType, fileList);
                     },
                     configurable: false,
                     enumerable: false
                  }
               );
               //--------------------------------------------------------------
               function init() {
                  var item;
/**/              var reqdFilterNames = ["Nichole1", "Nichole2"];
/**/              var count = reqdFilterNames.length;
                  var temp = localStorage.getItem("jaxo_filters");
                  if (temp) {
                     files = JSON.parse(temp);
/**/                 for (var i=0, max=files.length; i < max; ++i) {
/**/                    for (var j=0, max=reqdFilterNames.length; j < max; ++j) {
/**/                       var name = reqdFilterNames[j];
/**/                       if (name === files[i].name) {
/**/                          --count;
/**/                          reqdFilterNames[index] = undefined;
/**/                       }
/**/                    }
/**/                 }
                  }
/**/ var fules = files;
/**/              if (count) {
/**/                 for (var j=0, max=reqdFilterNames.length; j < max; ++j) {
/**/                    var name = reqdFilterNames[j];
/**/                    if (name) {
/**/ alert("Glou: " + name);
/**/                       readJsonFileFromServer(
/**/                          name,
/**/                          function(entry) {
/**/                             files.push(entry);
/**/                             if (--count == 0) localSave();
/**/                          }
/**/                       )
/**/                    }
/**/                 }
/**/              }else
                  localSave();
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
                              creation: this.getResponseHeader("Last-Modified"),
                              size: this.getResponseHeader("Content-Length"),
                              data: this.responseText
                           });
                           whenDone();
                        }else {
                           alert("Can't load \"" + filePath + "\" RC:" + this.status);
                        }
                     }
                  };
                  xhr.send();
               }
               // Wed May 15 13:14:37 CEST 2013  <= Blob
               // Mon, 03 Jul 2006 21:44:38 GMT  <= JS toUTCString
               // Mon, 13 May 2013 11:18:49 GMT  <= HTTP GET (Last-Modified)
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
               function getBlobStoreDir(mimeType, fileList) {
                  var xhr = new XMLHttpRequest();
                  var url = "../jaxogram?OP=blob&ACT=dir&MT=" + mimeType;
                  xhr.open("GET", url, true);
                  xhr.onreadystatechange = function () {
                     if (this.readyState === 4) {
                        if ((this.status === 200) || (this.status === 0)) {
                           fileList.populate.call(
                              fileList, JSON.parse(this.responseText)
                           );
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
                           whenDone({
                              name: filePath,
                              creation: this.getResponseHeader("Last-Modified"),
                              size: this.getResponseHeader("Content-Length"),
                              data: this.responseText
                           });
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


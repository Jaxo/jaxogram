function FilesList(replacedId, title) {
   var tsegment;
   var td;
   var tr;
   var btn;
   var that = this;
   var elt = document.getElementById(replacedId);
   tsegment = document.createElement("THEAD");
   tr = document.createElement("TR");
   td = document.createElement("TH");
   td.setAttribute("colspan", "4");
   td.innerHTML = elt.innerHTML;
   tr.appendChild(td);
   tsegment.appendChild(tr);
   tr = document.createElement("TR");
   td = document.createElement("TH");
   td.setAttribute("colspan", "4");
   td.appendChild(document.createElement("HR"));
   tr.appendChild(td);
   tsegment.appendChild(tr);
   tr = document.createElement("TR");
   td = document.createElement("TH");
   td.textContent = "Name";
   tr.appendChild(td);
   td = document.createElement("TH");
   td.textContent = "Size";
   tr.appendChild(td);
   td = document.createElement("TH");
   td.textContent = "Last Modified";
   td.setAttribute("colspan", "2");
   tr.appendChild(td);
   tsegment.appendChild(tr);

   this.tableInner = document.createElement("TABLE");
   this.tableInner.className = "fl_TableInner";
   this.tableInner.appendChild(tsegment);  // THEAD
   this.tableBody = document.createElement("TBODY");
   this.tableInner.appendChild(this.tableBody);

   tsegment = document.createElement("TFOOT");
   tr = document.createElement("TR");
   td = document.createElement("TD");
   td.setAttribute("colspan", "4");
   this.buttons = td;
   tr.appendChild(td);
   tsegment.appendChild(tr);
   this.tableInner.appendChild(tsegment);

   this.addButton("Cancel").onclick = function() { that.clear(); }
   this.tableInner.onclick = function(event) {
      var elt = event.target;      // the item that was clicked
      while ((elt != null) && (elt.nodeName != "TR")) {
         elt = elt.parentNode;
      }
      if (elt && (elt.className === "file")) {
         event.preventDefault();
         event.stopPropagation();
         that.clicked(event, elt.rowIndex);
      }
   }

   tr = document.createElement("TR");
   td = document.createElement("TD");
   td.setAttribute("align", "center");
   td.appendChild(this.tableInner);
   tr.appendChild(td);
   tsegment = document.createElement("TBODY");
   tsegment.appendChild(tr);

   this.tableOuter = document.createElement("TABLE");
   this.tableOuter.className = "fl_TableContainer";
   this.tableOuter.appendChild(tsegment);

   var dad = elt.parentNode;
   var sib = elt.nextSibling;
   dad.removeChild(elt);
   dad.insertBefore(this.tableOuter, sib);

   // Selections...
   // this.bkgds = new Array("#eaeaea", "#ffffff", "#fbb7ec", "#fad3f2");
   this.bkgds = new Array("transparent", "transparent", "#f07746", "#f07746");
   this.fggds = new Array("black", "black", "white", "white");

   this.firstRow = -1;
   this.lastRow = -1;
   this.plusRows = [];
   this.minusRows = [];
}

FilesList.prototype = {
   addButton: function(name) {
      btn = document.createElement("BUTTON");
      btn.style = "margin-left:1rem";
      btn.textContent = name;
      this.buttons.appendChild(btn);
      return btn;
   },
   populate: function(result) {
      var row;
      var cell0;
      var cell1;
      var cell2;
      var cell3;
      var date;
      var split;
      var tblBody = this.tableBody;
      tblBody.innerHTML = "";
      result.forEach(
         function(entry) {
            row = document.createElement("TR");
            row.className = "file";
            cell0 = document.createElement("TD");
            cell1 = document.createElement("TD");
            cell2 = document.createElement("TD");
            cell3 = document.createElement("TD");
            date = entry.creation;
            split = date.lastIndexOf(' ', date.lastIndexOf(' ')-1)
            cell0.textContent = entry.name;
            cell1.textContent = entry.size;
            // Date is deemed to be as in: "Wed, 01 May 2013 20:14:37 GMT"
            cell2.textContent = date.substr(0, split);
            cell3.textContent = date.substr(split+1);
            row.appendChild(cell0);
            row.appendChild(cell1);
            row.appendChild(cell2);
            row.appendChild(cell3);
            tblBody.appendChild(row);
         }
      );
      this.tableOuter.style.visibility = "visible";
   },
   clear: function() {
      this.tableOuter.style.visibility = "hidden";
      this.reset();
   },
   /* Selections... */
   clicked: function(ev, newRow) {
      if (ev.ctrlKey) {
         if (this.firstRow < 0) {
            this.firstRow = newRow;
            this.setBkgd(newRow, 2);
         }else if (
            (newRow == this.firstRow) || (
               (this.lastRow >= 0) && (
                  ((newRow >= this.firstRow) && (newRow <= this.lastRow)) ||
                  ((newRow >= this.lastRow) && (newRow <= this.firstRow))
               )
            )
         ) {
            ix = this.minusRows.indexOf(newRow);
            if (ix >= 0) {
               this.setBkgd(this.minusRows[ix], 2);
               this.minusRows.splice(ix, 1);
            }else {
               this.setBkgd(newRow, 0);
               this.minusRows.push(newRow);
            }
         }else {
            ix = this.plusRows.indexOf(newRow);
            if (ix >= 0) {
               this.setBkgd(this.plusRows[ix], 0);
               this.plusRows.splice(ix, 1);
            }else {
               this.setBkgd(newRow, 2);
               this.plusRows.push(newRow);
            }
         }
      }else if (ev.shiftKey) {
         this.clearExtraRows();
         if (this.firstRow != newRow) {
            if (this.firstRow < 0) {
               this.setBkgd(newRow, 2);
               this.firstRow = newRow;
            }else {
               this.setBkgd(this.firstRow, 2);
               this.fillRows(0);
               this.lastRow = newRow;
               this.fillRows(2);
            }
         }
      }else {
         this.clearExtraRows();
         if (this.firstRow >= 0) {
            this.setBkgd(this.firstRow, 0);
            this.fillRows(0);
            this.lastRow = -1;
         }
         this.firstRow = newRow;
         this.setBkgd(newRow, 2);
      }
      return false;
   },
   reset: function() {
      this.clearExtraRows();
      if (this.firstRow >= 0) {
         this.setBkgd(this.firstRow, 0);
         this.fillRows(0);
      }
      this.firstRow = -1;
      this.lastRow = -1;
   },
   clearExtraRows: function() {
      this.minusRows.length = 0;
      var len = this.plusRows.length;
      for (ix=0; ix < len; ++ix) {
         this.setBkgd(this.plusRows[ix], 0);
      }
      this.plusRows.length = 0;
   },
   fillRows: function(ixBkgd) {
      // assert: this.firstRow >= 0
      if (this.lastRow >= 0) {
         if (this.lastRow >= this.firstRow ) {
            i = this.firstRow;
            while (++i <= this.lastRow) {
               this.setBkgd(i, ixBkgd);
            }
         }else {
            i = this.firstRow;
            while (--i >= this.lastRow) {
               this.setBkgd(i, ixBkgd);
            }
         }
      }
   },
   getSelectedRows: function() {
      // 3 is the # of rows in the THEAD.  Yes, it's a bit hacky.
      var result = [];
      if (this.firstRow >= 0) {
         if (this.minusRows.indexOf(this.firstRow) == -1) {
            result.push(this.firstRow - 3);
         }
         if (this.lastRow >= 0) {
            if (this.lastRow >= this.firstRow ) {
               i = this.firstRow;
               while (++i <= this.lastRow) {
                  if (this.minusRows.indexOf(i) == -1) {
                     result.push(i-3);
                  }
               }
            }else {
               i = this.firstRow;
               while (--i >= this.lastRow) {
                  if (this.minusRows.indexOf(i) == -1) {
                     result.push(i-3);
                  }
               }
            }
         }
      }
      for (i=0; i < this.plusRows.length; ++i) {
         result.push(this.plusRows[i-3]);
      }
      return result;
   },
   getCellValue: function(rowNo, cellNo) {
      // 3 is the # of rows in the THEAD.  Yes, it's a bit hacky.
      return this.tableInner.rows[rowNo+3].cells[cellNo].innerHTML;
   },
   setBkgd: function(rowNo, ixBkgd) {
      if ((rowNo & 1) > 0) ++ixBkgd;
      // this.tableInner.rows[rowNo].background = this.bkgds[ixBkgd];
      var style = this.tableInner.rows[rowNo].style;
      style.background = this.bkgds[ixBkgd];
      style.color = this.fggds[ixBkgd];
   }
}


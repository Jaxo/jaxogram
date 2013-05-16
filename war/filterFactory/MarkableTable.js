function MarkableTable(table) {
   // this.bkgds = new Array("#eaeaea", "#ffffff", "#fbb7ec", "#fad3f2");
   this.bkgds = new Array("transparent", "transparent", "#f07746", "#f07746");
   this.fggds = new Array("black", "black", "white", "white");

   this.firstRow = -1;
   this.lastRow = -1;
   this.plusRows = [];
   this.minusRows = [];
   this.table = table;

   this.clicked = function(ev, newRow) {
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
   }

   this.reset = function() {
      this.clearExtraRows();
      if (this.firstRow >= 0) {
         this.setBkgd(this.firstRow, 0);
         this.fillRows(0);
      }
      this.firstRow = -1;
      this.lastRow = -1;
   }

   this.clearExtraRows = function() {
      this.minusRows.length = 0;
      var len = this.plusRows.length;
      for (ix=0; ix < len; ++ix) {
         this.setBkgd(this.plusRows[ix], 0);
      }
      this.plusRows.length = 0;
   }

   this.fillRows = function(ixBkgd) {
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
   }

   this.getSelected = function(cellNo) {
      var result = [];
      if (this.firstRow >= 0) {
         if (this.minusRows.indexOf(this.firstRow) == -1) {
            result.push(this.solve(this.firstRow, cellNo));
         }
         if (this.lastRow >= 0) {
            if (this.lastRow >= this.firstRow ) {
               i = this.firstRow;
               while (++i <= this.lastRow) {
                  if (this.minusRows.indexOf(i) == -1) {
                     result.push(this.solve(i, cellNo));
                  }
               }
            }else {
               i = this.firstRow;
               while (--i >= this.lastRow) {
                  if (this.minusRows.indexOf(i) == -1) {
                     result.push(this.solve(i, cellNo));
                  }
               }
            }
         }
      }
      for (i=0; i < this.plusRows.length; ++i) {
         result.push(this.solve(this.plusRows[i], cellNo));
      }
      return result;
   }

   this.setBkgd = function(rowNo, ixBkgd) {
      if ((rowNo & 1) > 0) ++ixBkgd;
      // this.table.rows[rowNo].background = this.bkgds[ixBkgd];
      var style = this.table.rows[rowNo].style;
      style.background = this.bkgds[ixBkgd];
      style.color = this.fggds[ixBkgd];
   }

   this.solve = function(rowNo, cellNo) {
      return this.table.rows[rowNo].cells[cellNo].innerHTML;
   }
}

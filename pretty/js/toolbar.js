// window.addEventListener(
//    "load",
//    function() {
//       var revealers = document.querySelectorAll(
//          "footer.toolbar > menu > li[aria-controls]"
//       );
//       for (var i=0, max=revealers.length; i < max; ++i) {
//          revealers[i].addEventListener(
//             "click",
//             function() { revealPage(this.getAttribute("aria-controls")); }
//          );
//       }
//    }
// );
function showToolbar(barNo) {
   var menus = document.querySelectorAll("footer.toolbar > menu");
   // var prevBarId = undefined;
   var container = menus[0].parentNode;
   container.display = "none";
   for (max=menus.length, i=0; i < max; ++i) {
      var menu = menus[i];
      // if (menu.getAttribute("aria-expanded") == "true") prevBarId = menu.id;
      if (i === barNo) {
         container.style.display = "";
         menu.setAttribute("aria-expanded", "true");
      }else {
         menu.setAttribute("aria-expanded", "false");
      }
   }
   // return prevBarId;
}

// function showToolbar(barNo) {
//    var elt = document.querySelector("footer > table");
//    var prevBarNo = -1;
//    elt.parentNode.style.display = "none";
//    for (var rows=elt.rows, max=rows.length, i=0; i < max; ++i) {
//       var row = rows[i];
//       if (!row.style.display) prevBarNo = i;
//       if (i == barNo) {
//          elt.parentNode.style.display = "";
//          row.style.display = "";
//       }else {
//          row.style.display = "none";
//       }
//    }
//    return prevBarNo;
// }
//

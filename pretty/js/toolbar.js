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

function toggleSidebarView() {
   expandSidebarView(0);
}

function expandSidebarView(v) { // -1: collapse, +1: expand, 0: toggle
   var trigger = document.getElementById('p01');
   var principal = document.querySelector(".principal");
   var sidebar = document.querySelector(".sidebar");
   var expanded = (sidebar.getAttribute("aria-expanded") === "true");
   if (!expanded && (v >= 0)) {
      trigger.className = "icon back";
      principal.setAttribute("aria-expanded", "false");
      sidebar.setAttribute("aria-expanded", "true");
   }else if (v <= 0) {
      trigger.className = "icon menu";
      principal.setAttribute("aria-expanded", "true");
      sidebar.setAttribute("aria-expanded", "false");
   }
}

function resetSidebarButton() {
   var btn = document.getElementById('p01');
   if (document.querySelector(".sidebar").getAttribute("aria-expanded") === "true") {
      btn.className = "icon back";
   }else {
      btn.className = "icon menu";
   }
}


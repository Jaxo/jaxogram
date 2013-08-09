function menuListClicked(event) {
   var liElt = getRealTarget(event);
   if (liElt == null) return;
   if (liElt.getAttribute("role") == "listbox") {
      var ulChildElt = liElt.getElementsByTagName("UL")[0];
      if (ulChildElt != null) {     // defense!
         var afterTransed;
         if (liElt.getAttribute("aria-expanded") == "true") {
            afterTransed = function() {
               liElt.removeAttribute("aria-expanded");
            }
            ulChildElt.style.height = "0rem";
         }else {
            afterTransed = function() {
               liElt.setAttribute("aria-expanded", "true");
            }
            ulChildElt.style.height = (
               ulChildElt.firstElementChild.offsetHeight *
               ulChildElt.childElementCount
            ) + "px";
         }
         ulChildElt.addEventListener(
            "transitionend",
            function() {
               afterTransed();
               this.removeEventListener("transitionend", arguments.callee, true);
            },
            true
         );
         ulChildElt.addEventListener(
            "webkitTransitionEnd",
            function() {
               afterTransed();
               this.removeEventListener("webkitTransitionEnd", arguments.callee, true);
            },
            true
         );
      }
   }else if (liElt.parentNode.getAttribute("role") !=="radiogroup") {
      // for lambda lists, select toggles the selected state
      if (liElt.getAttribute("aria-selected") == "true") {
         liElt.removeAttribute("aria-selected");
      }else {
         liElt.setAttribute("aria-selected", "true");
      }
   }
}

function radioClicked(event) {
   var liElt = getRealTarget(event);
   // selecting a selected item in a radiogroup does nothing..
   // selecting a new item in a radiogroup deselects its siblings
   if (liElt && (liElt.getAttribute("aria-selected") !== "true")) {
      var siblings = liElt.parentNode.children;
      for (var i=0, max=siblings.length; i < max; ++i) {
         var sib = siblings[i];
         if (sib.getAttribute("aria-selected")) {
            sib.removeAttribute("aria-selected");
         }
      }
      liElt.setAttribute("aria-selected", "true");
   }
}

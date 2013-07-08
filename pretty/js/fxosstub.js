function getAttribute(node, attrName) {  // helper
   var attrs = node.attributes;
   if ((attrs != null) && (attrs[attrName] != null)) {
      return attrs[attrName].value;
   }else {
      return null;
   }
}

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
      }
   }else if (getAttribute(liElt.parentNode, "role") !=="radiogroup") {
      if (liElt.getAttribute("aria-selected") == "true") {
         // however for lambda lists, it deselects the item
         liElt.removeAttribute("aria-selected");
      }else {
         liElt.setAttribute("aria-selected", "true");
         ownedId = getAttribute(liElt, "aria-owns");
         if (ownedId != null) {
            document.getElementById(ownedId).setAttribute("aria-expanded", "true");
         }
      }
   }
}

function radioClicked(event) {
   var liElt = getRealTarget(event);
   // selecting a selected item in a radiogroup does nothing..
   if ((liElt == null) || (liElt.getAttribute("aria-selected") == "true")) return;
   // selecting a new item in a radiogroup deselects its siblings
   var siblings = liElt.parentNode.childNodes;
   for (var i=0; i < siblings.length; ++i) {
      var sib = siblings[i];
      if (getAttribute(sib, "aria-selected") == "true") {
         ownedId = getAttribute(sib, "aria-owns");
         if (ownedId != null) {
            document.getElementById(ownedId).setAttribute("aria-expanded", "false");
         }
         sib.attributes.removeNamedItem("aria-selected");
      }
   }
   liElt.setAttribute("aria-selected", "true");
   ownedId = getAttribute(liElt, "aria-owns");
   if (ownedId != null) {
      document.getElementById(ownedId).setAttribute("aria-expanded", "true");
   }
}

function getRealTarget(clickedEvent) {
   /*
   | TEMPORARY fix?
   | I am interested in list items, direct children of UL's, or TR's),
   | The list items descendants (IMG, SPAN, etc...) are phony.
   | I should probably add "role=listitem" for all such items.
   */
   var elt = clickedEvent.target;      // the item that was clicked
   while ((elt != null) && (elt.nodeName != "TD") && (elt.nodeName != "LI")) {
      elt = elt.parentNode;
   }
   return elt;
}

function getIndex(clickedEvent) {
   var elt = clickedEvent.target;      // the item that was clicked
   while ((elt != null) && (elt.nodeName != "LI")) {
      elt = elt.parentNode;
   }
   var index = 0;
   for (var l=elt.parentNode.firstElementChild; l && (l != elt); l=l.nextElementSibling) {
      ++index;
   }
   return index;
}

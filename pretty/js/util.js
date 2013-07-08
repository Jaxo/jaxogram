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

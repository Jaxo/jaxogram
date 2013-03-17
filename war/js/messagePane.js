function hideMessagePane() {
   document.getElementById("messagepane").style.top = '-100%';
}

function showMessagePane(idTitle, eltContents, whenDone) {
   var eltCell;
   var eltMsg = document.getElementById("messagepane");
   var eltRows = eltMsg.querySelectorAll(
      "div:first-child > div:first-child > table tr"
   );
   var row = eltRows[0];
   row.cells[0].onclick = hideMessagePane;
   eltCell = eltRows[0].cells[2];
   eltCell.className = "i18n";
   eltCell.id = idTitle;
   eltCell.childNodes[0].textContent = i18n(idTitle);
   eltMsg.style.top='0px';
   if (whenDone) {
      row.cells[3].style.visibility = "visible";
      row.cells[4].style.visibility = "visible";
      row.cells[4].onclick = whenDone;
   }else {
      row.cells[3].style.visibility = "hidden";
      row.cells[4].style.visibility = "hidden";
   }
   eltCell = eltRows[1].cells[0];
   // eltCell.replaceChild(eltContents, eltCell.childNodes[0]);
   while (eltCell.hasChildNodes()) {
      eltCell.removeChild(eltCell.lastChild);
   }
   eltContents.forEach(
      function(elt) { eltCell.appendChild(elt); }
   );
}

function makeInputField(name, type) {
   var eltLegend = document.createElement("LEGEND");
   eltLegend.className = "i18n";
   eltLegend.id = name;
   eltLegend.appendChild(document.createTextNode(i18n(name)));
   var eltInput = document.createElement("INPUT");
   if (type) eltInput.type = type;
   var eltField = document.createElement("FIELDSET");
   eltField.appendChild(eltLegend);
   eltField.appendChild(eltInput);
   var eltDiv = document.createElement("DIV");
   eltDiv.style.margin = "2rem auto";
   eltDiv.appendChild(eltField);
   return eltInput;
}

function getInputFieldContainer(inputField) {
   return inputField.parentNode.parentNode;
}


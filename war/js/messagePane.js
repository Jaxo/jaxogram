function showMsg(idTitle, eltContents, whenDone) {
   hideMsg();
   var elt = buildMessagePane(idTitle, eltContents, whenDone);
   elt.style.transitionProperty = "top";
   elt.style.transitionDuration = "0.6s";
   elt.style.opacity = "1.0";
   elt.style.top='0px';
}

function alertMsg(idTitle, eltContents, whenDone) {
   hideMsg();
   var elt = buildMessagePane(idTitle, eltContents, whenDone);
   elt.style.transitionProperty = "opacity";
   elt.style.transitionDuration = "3s";
   elt.style.top = "0";
   setTimeout(
      function() {
         elt.style.opacity = "0";
         elt.addEventListener(
            "transitionend",
            function() {
               elt.removeEventListener("transitionend", arguments.callee, true);
               elt.style.transitionProperty = "none";
               elt.style.transitionDuration = "0";
               elt.style.top = "-100%";
               elt.style.opacity = "1.0";
            },
            true
         );
      }, 1000
   );
}

function confirmMsg(text, whenDone) {
   var eltDiv = document.createElement("DIV");
   eltDiv.style.padding = "2rem";
   eltDiv.style.textAlign = "left";
   eltDiv.textContent = text;
   showMsg("confirm", [eltDiv], function() { hideMsg(); whenDone(); });
}

function simpleMsg(idTitle, text) {
   var eltDiv = document.createElement("DIV");
   eltDiv.style.padding = "2rem";
   eltDiv.style.textAlign = "left";
   eltDiv.textContent = text;
   alertMsg(idTitle, [eltDiv]);
}

function hideMsg() {
   var elt = document.getElementById("messagepane");
   elt.style.top = '-100%';
   elt.style.opacity = "1.0";
}

function buildMessagePane(idTitle, eltContents, whenDone) {
   var eltCell;
   var eltMsg = document.getElementById("messagepane");
   var eltRows = eltMsg.querySelectorAll(
      "div:first-child > div:first-child > table tr"
   );
   var row = eltRows[0];
   row.cells[0].onclick = hideMsg;
   eltCell = eltRows[0].cells[2];
   eltCell.className = "i18n";
   eltCell.id = idTitle;
   eltCell.childNodes[0].textContent = i18n(idTitle);
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
   return eltMsg;
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


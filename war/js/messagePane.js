function showMsg(idTitle, eltContents, whenDone) {
   execute(
      function(elt, args) {
         buildMessagePane(elt, args);
         elt.style.transitionProperty = "top";
         elt.style.transitionDuration = "0.6s";
         elt.style.opacity = "1.0";
         elt.style.top = "0";
      },
      arguments
   );
}

function alertMsg(idTitle, eltContents, whenDone) {
   execute(
      function(elt, args) {
         buildMessagePane(elt, args);
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
                     hideMsg();
                  },
                  true
               );
            }, 1000
         );
      },
      arguments
   );
}

function confirmMsg(text, whenDone) {
   showMsg("confirm", [makeTextField(text)], function() { hideMsg(); whenDone(); });
}

function simpleMsg(idTitle, text) {
   alertMsg(idTitle, [makeTextField(text)]);
}

function execute(fct, args)
{
   var elt = document.getElementById("messagepane");
   if (elt.promises === undefined) {
      elt.promises = [];
      new GestureDetector(elt).startDetecting();
      elt.addEventListener(
         "swipe",
         function(e) {
            var direction = e.detail.direction;
            if (e.detail.direction === 'up') {
               hideMsg();
            }
         }
      );
   }
   if (elt.staged) {
      var obj = new Object();
      obj.fct = fct;
      obj.args = args;
      elt.promises.push(obj);
   }else {
      elt.staged = "1";
      fct(elt, args);
   }
}

function hideMsg() {
   var elt = document.getElementById("messagepane");
   elt.style.top = '-150%';
   elt.style.opacity = "1.0";
   elt.staged = elt.promises.shift();
   if (elt.staged) {
      elt.staged.fct(elt, elt.staged.args);
   }
}

function shakeMsg() {
   var elt = document.getElementById("messagepane");
   var count = 0;
   var timer = setInterval(
      function() {
         var delta;
         if (++count > 17) {
            delta = 0;
            clearInterval(timer);
         }else {
            switch (count % 5) {
            case 0: delta = 1; break;
            case 1: delta = 0; break;
            case 2: delta = -1; break;
            case 4: delta = 0; break;
            }
         }
         elt.style.transform = "translateX(" + delta + "rem)";
      },
      32
   );
}

function buildMessagePane(elt, args) {
   var eltCell;
   var eltRows = elt.querySelectorAll(
      "div:first-child > div:first-child > table tr"
   );
   var row = eltRows[0];
   row.cells[0].onclick = hideMsg;
   eltCell = eltRows[0].cells[2];
   eltCell.className = "i18n";
   eltCell.id = args[0];                      // idTitle
   eltCell.childNodes[0].textContent = i18n(args[0]);
   if (args[2]) {                             // whenDone
      row.cells[3].style.visibility = "visible";
      row.cells[4].style.visibility = "visible";
      row.cells[4].onclick = args[2];
   }else {
      row.cells[3].style.visibility = "hidden";
      row.cells[4].style.visibility = "hidden";
   }
   eltCell = eltRows[1].cells[0];
   while (eltCell.hasChildNodes()) {
      eltCell.removeChild(eltCell.lastChild);
   }
   args[1].forEach(                           // eltContents
      function(eltItem) { eltCell.appendChild(eltItem); }
   );
   return elt;
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

function makeTextField(text) {
   var eltDiv = document.createElement("DIV");
   eltDiv.style.padding = "2rem";
   eltDiv.style.textAlign = "left";
   eltDiv.textContent = text;
   return eltDiv;
}


create_carousel = function(
   container, width, height, nbFigures, isVertical
) {
   container.style.width = width + "px";
   container.style.height = height + "px";
   var rotateXform = isVertical? "rotateX" : "rotateY";
   var carousel = document.createElement("div");
   var thetaIncrt = 360 / nbFigures;
   var theta = 0;
   var zPx = isVertical? height : width;

   zPx = (zPx / (2 * Math.tan(Math.PI / nbFigures))) + "px";
   carousel.style['transform'] = "translateZ(-" + zPx + ")";
   for (var i=0; i < nbFigures; ++i) {
      var eltFigure = document.createElement("figure");
      // eltFigure.style['top'] = figTop + "px";
      eltFigure.style['height'] = height + "px";
      eltFigure.style['width'] = width + "px";
      eltFigure.style['lineHeight'] = height + "px";
      eltFigure.style['fontSize'] = (height * 0.6) + "px";
      eltFigure.style['transform'] = (
         rotateXform + "(" + theta +
         "deg) translateZ(" + zPx + ")" //  + " scale(.9999)"
      );
      carousel.appendChild(eltFigure);
      theta += thetaIncrt;
   }
   var figNo = 0;
   var rotate = function(val) {
      figNo = (figNo + val) % nbFigures;
      theta -= thetaIncrt * val;
      carousel.style["transform"] = (
         "translateZ(-" + zPx + ") " + rotateXform + "(" + theta + "deg)"
      );
   };
   var interval;
   container.appendChild(carousel);
   return {
      rotateEach: function(millis, whenRotating) {
         if (millis == 0) {
            if (interval) clearInterval(interval);
         }else {
            interval = setInterval(
               function() {
                  if (whenRotating) whenRotating(figNo);
                  rotate(1);
               },
               millis
            );
         }
      },
      setFigureContents: function(elt, figNo) {
         var figureElt = carousel.children[figNo];
         while (figureElt.hasChildNodes()) {
            figureElt.removeChild(figureElt.lastChild);
         }
         figureElt.appendChild(elt);
      },
      getFigureContents: function(figNo) {
         return carousel.children[figNo].children[0];
      }
   }
};

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
   var rotate = function(val) {
      theta += thetaIncrt * val;
      carousel.style["transform"] = (
         "translateZ(-" + zPx + ") " + rotateXform + "(" + theta + "deg)"
      );
      // if (theta == -360) theta = 0;
   };
   var interval;
   container.appendChild(carousel);
   return {
      rotateEach: function(ms) {
         if (ms == 0) {
            if (interval) clearInterval(interval);
         }else {
            interval = setInterval(function() { rotate(-1); }, ms);
         }
      },
      setInnerHTML: function(html, figNo) {
         carousel.children[figNo].innerHTML = html;
      }
   }
};

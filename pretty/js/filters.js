function initFilters() {
   var container = document.createElement("DIV");
   container.innerHTML = (
      "<svg xmlns:xlink='http://www.w3.org/1999/xlink'><defs>" +
      "<filter id='Nichole1'>" +
         "<feComponentTransfer>" +
         "<feFuncR type='table' tableValues='0.0471, 0.1255, 0.251, 0.3765, 0.502, 0.6274, 0.7529, 0.8784, 1'/>" +
         "<feFuncG type='table' tableValues='0, 0.1255, 0.251, 0.3765, 0.4902, 0.6274, 0.7804, 0.9294, 1'/>" +
         "<feFuncB type='table' tableValues='0.0863, 0.1922, 0.251, 0.3765, 0.502, 0.6274, 0.7529, 0.8784, 1'/>" +
         "</feComponentTransfer>" +
//       "<feColorMatrix type='matrix' values=' 0.7875 0.1930 0.0194 0.0000 0.0000 0.0575 0.9230 0.0194 0.0000 0.0000 0.0575 0.1930 0.7494 0.0000 0.0000 0.0000 0.0000 0.0000 1.0000 0.0000'/>" +
         "<feColorMatrix type='matrix' values='.79 .19 .02 0 0 .06 .92 .02 0 0 .06 .19 .75 0 0 0   0   0   1 0'/>" +
         "<feComponentTransfer>" +
           "<feFuncR type='linear' slope='1.0309' intercept='-0.0155'/>" +
           "<feFuncG type='linear' slope='1.0309' intercept='-0.0155'/>" +
           "<feFuncB type='linear' slope='1.0309' intercept='-0.0155'/>" +
         "</feComponentTransfer>" +
      "</filter>" +
      "<filter id='Nichole2'>" +
         "<feComponentTransfer>" +
         "<feFuncR type='table' tableValues='0, 0.1255, 0.251, 0.3765, 0.502, 0.6274, 0.7529, 0.8784, 1'/>" +
         "<feFuncG type='table' tableValues='0, 0.1255, 0.251, 0.3765, 0.502, 0.6274, 0.7529, 0.8784, 1'/>" +
         "<feFuncB type='table' tableValues='0, 0.1255, 0.251, 0.3765, 0.502, 0.6274, 0.7529, 0.8784, 1'/>" +
         "</feComponentTransfer>" +
         "<feComponentTransfer>" +
         "<feFuncR type='linear' slope='1.0695' intercept='-0.0348'/>" +
         "<feFuncG type='linear' slope='1.0695' intercept='-0.0348'/>" +
         "<feFuncB type='linear' slope='1.0695' intercept='-0.0348'/>" +
         "</feComponentTransfer>" +
      "</filter>" +
      "<filter id='f4'><feColorMatrix type=\"matrix\" values=\"0.6666 0.6666 0.6666 0 0 0.3333 0.3333 0.3333 0 0 0.3333 0.3333 0.3333 0 0 0 0 0 1 0\"/></filter>" +
      "<filter id='f5'><feColorMatrix type=\"matrix\" values=\"-0.0257 1.2426 -0.0402 0.0000 0.0000 0.3113 0.0074 0.1600 0.0000 0.0000 0.8248 0.1325 -1.1995 0.0000 0.0000 0.0000 0.0000 0.0000 1.0000 0.0000\"/></filter>" +
      "</defs></svg>"
   );
   document.body.insertBefore(container, document.body.firstChild);
}

function makeSvgImage(imgSrc, filter, w, h) {
   var filtered;
   if (filter.value) {
      filtered = "' filter='url(#" + filter.name + ")";
   }else {
      filtered = "";
   }
   return (
      "<image width='" + w + "' height='" + h +
      "' preserveAspectRatio='xMinYMin meet' xlink:href='" + imgSrc + filtered +
      "'></image>"
   );
}

function doVignette(w, h, imgSrc, filter) {
   var f2 = filter.name + "_2";
   var f3 = filter.name + "_3";
   var f4 = filter.name + "_4";
   var r = filter.vignette.radius / 100;
   var b = filter.vignette.bright / 100;
   var m, t, u, p, q, cx, cy;
   if (h < w) {
      m = h;
      t = (h-w)/2;
      u = 0;
      p = 1.0;
      q = w/h;
   }else {
      m = w;
      t = 0;
      u = (w-h)/2;
      p = h/w;
      q = 1.0;
   }
   var border = (m * 0.0214) | 0;
   if (border < 2) border = 2;
   return (
     "<svg width='" + m + "' height='" + m + "'>" +
     "<g transform='translate(" + t + "," + u + ")'>" +
       makeSvgImage(imgSrc, filter, w, h) +
       "<g>" +
         "<filter id='" + f2 + "'>" +
           "<feGaussianBlur stdDeviation='2'/>" +
           "<feColorMatrix type='matrix' values='" + b + " 0 0 0 0 0 " + b + " 0 0 0 0 0 " + b + " 0 0 0 0 0 1 0'/>" +
         "</filter>" +
         "<radialGradient id='" + f3 +
         "' gradientUnits='objectBoundingBox' cx='" + (1/(2*p)) +
         "' cy='" + (1/(2*q)) + "' r='" + r +
         "' gradientTransform='scale(" + p + "," + q + ")'>" +
           "<stop offset='0.4' stop-color='#ffffff' stop-opacity='0'/>" +
           "<stop offset='0.6' stop-color='#ffffff' stop-opacity='1'/>" +
         "</radialGradient>" +
         "<mask id='" + f4 +
         "' maskUnits='objectBoundingBox' maskContentUnits='objectBoundingBox'>" +
           "<rect x='0' y='0' width='1' height='1' stroke-width='0" +
           "' fill='url(#" + f3 + ")'/>" +
         "</mask>" +
         "<image width='" + w + "' height='" + h +
         "' xlink:href='" + imgSrc +
         "' filter='url(#" + f2 + ")" +
         "' mask='url(#" + f4 + ")'>" +
         "</image>" +
       "</g>" +
       "<g fill='none' stroke='white' stroke-width='" + border + "' >" +
         "<rect x='" + ((border/2)-t) +
         "' y='" + ((border/2)-u) +
         "' width='" + (m-border) +
         "' height='" + (m-border) + "'/>" +
       "</g>" +
     "</g>"
   );
}



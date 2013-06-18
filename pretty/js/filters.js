var svgHeader = "<svg version='1.1' xmlns='http://www.w3.org/2000/svg' xmlns:xlink='http://www.w3.org/1999/xlink";

function makeSvg(imgSrc, filter, w, h, isSelfContained) {
   if (filter.vignette.radius) { // if (Object.keys(filter.vignette) !== 0)
      return vignettize(imgSrc, filter, w, h, isSelfContained);
   }else {
      return (
         svgHeader + "' width='" + w + "' height='" + h + "'>" +
         filterImage(imgSrc, filter, w, h, isSelfContained) + "</svg>"
      );
   }
}

function filterImage(imgSrc, filter, w, h, isSelfContained) {
   if (filter.value) {
      if (isSelfContained) {
         return declareImage(
            imgSrc, w, h,
            dataUrlize("filter", declareFilter(filter), filter.name)
         );
      }else {
         return (
            declareFilter(filter) +
            declareImage(imgSrc, w, h, "' filter='url(#" + filter.name + ")")
         );
      }
   }else {
      return declareImage(imgSrc, w, h, "");
   }
}

function vignettize(imgSrc, filter, w, h, isSelfContained) {
   var f1 = filter.name + "_2";
   var f2 = filter.name + "_3";
   var f3 = filter.name + "_4";
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
   var thick = (m * 0.0214) | 0;
   if (thick < 2) thick = 2;

   var vignette;
   if (isSelfContained) {
      vignette = declareImage(
         imgSrc, w, h,
         dataUrlize("filter", declareBlur(f1, b), f1) +
         dataUrlize(
            "mask",
            declareMask(
               f3,
               dataUrlize("fill", declareFill(f2, p, q, r), f2)
            ), f3
         )
      );
   }else {
      vignette = (
         declareBlur(f1, b) +
         declareFill(f2, p, q, r) +
         declareMask(f3, "' fill='url(#" + f2 + ")") +
         declareImage(
            imgSrc, w, h,
            "' filter='url(#" + f1 + ")' mask='url(#" + f3 + ")"
         )
      );
   }
   return (
      svgHeader + "' width='" + m + "' height='" + m +
      "'><g transform='translate(" + t + "," + u + ")'>" +
      filterImage(imgSrc, filter, w, h, isSelfContained) +
      "<g>" + vignette + "</g>" + doBorder(thick, m, t, u) + "</g></svg>"
   );
}

function declareFilter(filter) {
   return "<filter id='" + filter.name + "'>" + filter.value + "</filter>";
}

function declareImage(imgSrc, w, h, attrs) {
   return (
      "<image xlink:href='" + imgSrc +
      "' width='" + w +
      "' height='" + h +
      attrs +
      "'></image>"
   );
}

function declareBlur(f, b) {
   return (
      "<filter id='" + f + "'>" +
      "<feGaussianBlur stdDeviation='2'/>" +
      "<feColorMatrix type='matrix' values='" + b + " 0 0 0 0 0 " + b + " 0 0 0 0 0 " + b + " 0 0 0 0 0 1 0'/>" +
      "</filter>"
   );
}

function declareFill(f, p, q, r) {
   return (
      "<radialGradient id='" + f +
      "' gradientUnits='objectBoundingBox' cx='" + (1/(2*p)) +
      "' cy='" + (1/(2*q)) + "' r='" + r +
      "' gradientTransform='scale(" + p + "," + q + ")'>" +
        "<stop offset='0.4' stop-color='#ffffff' stop-opacity='0'/>" +
        "<stop offset='0.6' stop-color='#ffffff' stop-opacity='1'/>" +
      "</radialGradient>"
   );
}

function declareMask(f, value) {
   return (
      "<mask id='" + f +
      "' maskUnits='objectBoundingBox' maskContentUnits='objectBoundingBox'>" +
      "<rect x='0' y='0' width='1' height='1' stroke-width='0" +
      value + "'/></mask>"
   );
}

function doBorder(thick, m, t, u) {
   return (
      "<g fill='none' stroke='white' stroke-width='" + thick + "' >" +
        "<rect x='" + ((thick/2)-t) +
        "' y='" + ((thick/2)-u) +
        "' width='" + (m-thick) +
        "' height='" + (m-thick) + "'/>" +
      "</g>"
   );
}

function dataUrlize(attname, svgVal, f) {
   return(
      "' " + attname + "='url(data:image/svg+xml," +
      escape("<svg xmlns='http://www.w3.org/2000/svg'>" + svgVal + "</svg>") +
      "#" + f + ")"
   );
}

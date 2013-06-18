var svgHeader = "<svg version='1.1' xmlns='http://www.w3.org/2000/svg' xmlns:xlink='http://www.w3.org/1999/xlink";

function makeSvg(imgSrc, filter, w, h) {
   if (filter.vignette.radius) { // if (Object.keys(filter.vignette) !== 0)
      return vignettize(imgSrc, filter, w, h, false);
   }else {
      return (
         svgHeader + "' width='" + w + "' height='" + h + "'>" +
         makeSvgFiltered(imgSrc, filter, w, h) + "</svg>"
      );
   }
}

function makeSelf(imgSrc, filter, w, h) {   // self-contained
   if (filter.vignette.radius) { // if (Object.keys(filter.vignette) !== 0)
      return vignettize(imgSrc, filter, w, h, true);
   }else {
      return (
         svgHeader + "' width='" + w + "' height='" + h + "'>" +
         makeSelfFiltered(imgSrc, filter, w, h) + "</svg>"
      );
   }
}

function makeSelfFiltered(imgSrc, filter, w, h) {
   if (filter.value) {
      return (
         "<g>" +
         declareImage(imgSrc, w, h) +
         "' filter='url(data:image/svg+xml," +
         escape("<svg xmlns='http://www.w3.org/2000/svg'>" + declareFilter(filter) + "</svg>") +
         "#" + filter.name + ")'></image></g>"
      );
   }else {
      return "<g>" + declareImage(imgSrc, w, h) + "'></image></g>"
   }
}

function makeSvgFiltered(imgSrc, filter, w, h) {
   if (filter.value) {
      return (
         declareFilter(filter) +
         declareImage(imgSrc, w, h) +
         "' filter='url(#" + filter.name + ")'></image>"
      );
   }else {
      return declareImage(imgSrc, w, h) + "'></image>"
   }
}

function vignettize(imgSrc, filter, w, h, selfContained) {
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
   var border = (m * 0.0214) | 0;
   if (border < 2) border = 2;
   if (selfContained) {
      return (
         svgHeader + "' width='" + m + "' height='" + m +
         "'><g transform='translate(" + t + "," + u + ")'>" +
         makeSelfFiltered(imgSrc, filter, w, h) +
         "<g>" +
         z0(imgSrc, w, h, zz1(f1) + zz3(f3, zz2(f2, p, q, r))) +
         "</g>" + z5(border, m, t, u) +
         "</g></svg>"
      );
   }else {
      return (
         svgHeader + "' width='" + m + "' height='" + m +
         "'><g transform='translate(" + t + "," + u + ")'>" +
         makeSvgFiltered(imgSrc, filter, w, h) +
         "<g>" +
         z1(f1, b) +
         z2(f2, p, q, r) +
         z3(f3, "' fill='url(#" + f2 + ")") +
         z0(imgSrc, w, h, "' filter='url(#" + f1 + ")' mask='url(#" + f3 + ")") +
         "</g>" + z5(border, m, t, u) +
         "</g></svg>"
      );
   }
}

function declareImage(imgSrc, w, h) {
   return (
      "<image width='" + w + "' height='" + h +
      "' preserveAspectRatio='xMinYMin meet' xlink:href='" + imgSrc
   );
}
function declareFilter(filter) {
   return "<filter id='" + filter.name + "'>" + filter.value + "</filter>";
}

function z0(imgSrc, w, h, attrs) {
   return (
      "<image xlink:href='" + imgSrc +
      "' width='" + w +
      "' height='" + h +
      attrs +
      "'></image>"
   );
}

function z1(f, b) {
   return (
      "<filter id='" + f + "'>" +
      "<feGaussianBlur stdDeviation='2'/>" +
      "<feColorMatrix type='matrix' values='" + b + " 0 0 0 0 0 " + b + " 0 0 0 0 0 " + b + " 0 0 0 0 0 1 0'/>" +
      "</filter>"
   );
}
function zz1(f, b) {
   return (
      "' filter='url(data:image/svg+xml," +
      escape("<svg xmlns='http://www.w3.org/2000/svg'>" + z1(f, b) + "</svg>") +
      "#" + f + ")"
   );
}
function z2(f, p, q, r) {
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
function zz2(f, p, q, r) {
   return (
      "' fill='url(data:image/svg+xml," +
      escape("<svg xmlns='http://www.w3.org/2000/svg'>" + z2(f, p, q, r) + "</svg>") +
      "#" + f + ")"
   );
}
function z3(f, value) {
   return (
      "<mask id='" + f +
      "' maskUnits='objectBoundingBox' maskContentUnits='objectBoundingBox'>" +
      "<rect x='0' y='0' width='1' height='1' stroke-width='0" +
      value + "'/></mask>"
   );
}
function zz3(f, value) {
   return (
      "' mask ='url(data:image/svg+xml," +
      escape("<svg xmlns='http://www.w3.org/2000/svg'>" + z3(f, value)  + "</svg>") +
      "#" + f + ")"
   );
}
function z5(border, m, t, u) {
   return (
      "<g fill='none' stroke='white' stroke-width='" + border + "' >" +
        "<rect x='" + ((border/2)-t) +
        "' y='" + ((border/2)-u) +
        "' width='" + (m-border) +
        "' height='" + (m-border) + "'/>" +
      "</g>"
   );
}

// var elt = document.getElementById("ft6");     OBSO
// for (var i=0, max=filters.length; i < max; ++i) {
//    var tdElt = document.createElement("TD");
//    var imgElt = document.createElement("IMG");
//    imgElt.onload = function() {
//      // no longer need to read the blob so it's revoked
//    };
//    filters[i].thumbImg = imgElt;
//    tdElt.setAttribute("align", "center");
//    tdElt.appendChild(imgElt);
//    elt.appendChild(tdElt);
// }

// var thumbMaxWidth = 0;
// var thumbMaxHeight = 0;
// showToolbar(2);
// thumbMaxWidth = (elt.cells)[0].offsetWidth;
// thumbMaxHeight = (elt.cells)[0].offsetHeight;
// showToolbar(0);

//             var rawThumb = URL.createObjectURL(thumbBlob);
//             filters[0].thumbImg.src = rawThumb;
//             for (var i=1, max=filters.length; i < max; ++i) {
//                filters[i].thumbImg.src = URL.createObjectURL(
//                   doFilter(w, h, rawThumb, filters[i])
//                );
//             }
//
// function fitImages() {
//    // workaround, until "object-fit:contain;" gets implemented
//    var images = document.querySelectorAll(".imgbox img");
//    for (var i=0; i < images.length; ++i) {
//       var img = images[i];
//       img.addEventListener("load", function() { fitImage(this); });
//       fitImage(img);
//    }
// }
//
// function fitImage(img) {
//    var elt = img.parentNode;
//    if ((elt.offsetHeight * img.offsetWidth)<(img.offsetHeight * elt.offsetWidth)) {
//       img.style.top = "0";
//       img.style.width = "";
//       img.style.height = "100%";
//    }else {
//       img.style.top = ((elt.offsetHeight - img.offsetHeight) / 2) + "px";
//       img.style.width = "100%";
//       img.style.height = "";
//    }
// }
//
//    // 0) Set the filters
//    for (var i=1, max=filters.length; i < max; ++i) {
//       var filter = filters[i];
//       if (filter.src) URL.revokeObjectURL(filter.src);
//       filter.src = URL.createObjectURL(
//          doFilter(img.width, img.height, img.src, filter)
//       );
//   }


var currentFilter;
var xRW = 0.3086;
var xGW = 0.6094;
var xBW = 0.0820;
var RW = 0.213;
var GW = 0.715;
var BW = 0.072;
var RX = 0.787;  // 1-RW
var GX = 0.285;  // 1-GW
var BX = 0.928;  // 1-BW
var RA = 0.143;
var GA = 0.140;
var BA = 0.283;

var filters = [
   "brightness", "saturation", "rotateHue",
   "contrast", "blur", "sepia", "dusk", "sharpen", "sharpenChk",
   "extra2", "extra3", "noir", "moat",
   "vignetteChk", "vigRadius", "vigBright"

];

function initFilters() {
   filters.forEach(
      function(name) {
         var elt = document.getElementById(name);
         if (elt.type === "checkbox") {
            elt.addEventListener('change', filterImage);
         }else if (elt.type === "range") {
            elt.addEventListener('input', filterImage);
         }
      }
   );
   var vertVal = document.getElementById("vertVal");
   document.querySelector(".vsliders").onmouseout = function() {
      vertVal.style.display = "none";
   }
   var vertSliders = document.querySelectorAll("input[orient='vertical']");
   for (var i=0, max=vertSliders.length; i < max; ++i) {
      var elt = vertSliders[i];
      elt.onmouseover = function() {
         vertVal.style.display = "";
         vertVal.textContent = this.value;
      }
      elt.setAttribute("ix", i);
      elt.oninput = function() {
         vertVal.textContent = this.value;
         currentFilter.updateColorsLevels(this.getAttribute("ix"), this.value);
         filterImage();
      }
   }
   currentFilter = new Filter();
}

function multiply(a, b) {
   var t = 0;
   var res = new Float64Array(20);
   for (var i=0, t=0; t < 20; i += 5) {
      for (var j=0; j < 5; ++j, ++t) {
         res[t] = 0;
         for (var k=0, l=i, m=j; k < 4; ++k, ++l, m += 5) {
            res[t] += a[l] * b[m];
         }
      }
      res[t-1] += a[i+4];
   }
   return res;
}

function saturate(s, vals) {
   if (s == 100) {
      return vals;
   }else {
      s /= 100;
      return multiply(
         vals,
         [
            (((1.0-s)*RW) + s), (((1.0-s)*GW)), (((1.0-s)*BW)), 0, 0,
            (((1.0-s)*RW)), (((1.0-s)*GW) + s), (((1.0-s)*BW)), 0, 0,
            (((1.0-s)*RW)), (((1.0-s)*GW)), (((1.0-s)*BW) + s), 0, 0,
            0, 0, 0, 1, 0
         ]
      );
   }
}

function rotateHue(d, vals) {
   var rad = (d * Math.PI) / 180;
   var cos = Math.cos(rad);
   var sin = Math.sin(rad);
   return multiply(
      vals,
      [
         RW+(cos*RX)-(sin*RW), GW-(cos*GW)-(sin*GW), BW-(cos*BW)+(sin*BX), 0, 0,
         RW-(cos*RW)+(sin*RA), GW+(cos*GX)+(sin*GA), BW-(cos*BW)-(sin*BA), 0, 0,
         RW-(cos*RW)-(sin*RX), GW-(cos*GW)+(sin*GW), BW+(cos*BX)+(sin*BW), 0, 0,
         0, 0, 0, 1, 0
      ]
   );
}
function doExtra3(d, vals) {
   if (d != 100) {
      d /= 100;
      vals[0] *= (1-RW) * d;
      vals[6] *= (1-GW) * d;
      vals[11] *= (1-BW) * d;
   }
   return vals;
}

function doDusk(dusk, vals) {
   if (!dusk) {
      return vals;
   }else {
      return multiply(
         vals,
         [
            1.0, 0, 0, 0, 0,
            0, 0.2, 0, 0, 0,
            0, 0, 0.2, 0, 0,
            0, 0, 0, 1, 0
         ]
      );
   }
}

function doSepia(sepia, vals) {
   if (!sepia) {
      return vals;
   }else {
      return multiply(
         vals,
         [
            0.280, 0.450, 0.05, 0, 0,
            0.140, 0.390, 0.04, 0, 0,
            0.080, 0.280, 0.03, 0, 0,
            0, 0, 0, 1, 0
         ]
      );
   }
}

function makeGaussianBlur() {
   var blur = currentFilter.blur;
   if (blur == 0) {
      return "";
   }else {
      return "<feGaussianBlur stdDeviation='" + (blur/10) + "'/>";
   }
}

function makeFilterNoir() {
   if (currentFilter.noir) {
      return (
         "<feGaussianBlur stdDeviation='1.5'/>" +
         "<feComponentTransfer>" +
           "<feFuncR type='discrete' tableValues='0 .5 1 1'/>" +
           "<feFuncG type='discrete' tableValues='0 .5 1'/>" +
           "<feFuncB type='discrete' tableValues='0'/>" +
         "</feComponentTransfer>"
      );
   }else {
      return "";
   }
}

function makeFilterMoat() {
   if (currentFilter.moat) {
      return (
         "<feConvolveMatrix order='5'" +
         " kernelMatrix='" +
         "1  1    1     1   1 " +
         "1 -1.2 -1.2  -1.2 1 " +
         "1 -1.2  -3   -1.2 1 " +
         "1 -1.2 -1.2  -1.2 1 " +
         "1  1  1  1  1'/>"
      );
   }else {
      return "";
   }
}

function makeSharpen() {
   if (currentFilter.isSharpened) {
      return (
         "<feConvolveMatrix order='3'" +
         " kernelMatrix='" +
         " 1 -1  1 " +
         "-1 " + -(currentFilter.sharpen/100) + " -1 " +
         " 1 -1  1'/>"
      );
   }else {
      return "";
   }
}

function makeContrast() {
   var contrast = (currentFilter.contrast/2) - 50;
   var slope;
   var intercept;
   if (contrast == 0) {
      return "";
   }else if (contrast < 0) {
      contrast = -contrast;
      contrast /= 100;
      intercept = contrast;
      slope = 1 - (2*contrast);
   }else {
      contrast /= 200;
      slope = 1 / (1 - (2*contrast));
      intercept = (1 - slope) / 2;
   }
   slope = slope.toFixed(4);
   intercept = intercept.toFixed(4);
   return (
      "<feComponentTransfer>" +
      "<feFuncR type='linear' slope='" + slope +
      "' intercept='" + intercept + "'/>" +
      "<feFuncG type='linear' slope='" + slope +
      "' intercept='" + intercept + "'/>" +
      "<feFuncB type='linear' slope='" + slope +
      "' intercept='" + intercept + "'/>" +
      "</feComponentTransfer>"
   );
}

function makeColorsLevels() {
   var levels;
   var value = "";
   levels = currentFilter.redLevels();
   if (levels !== "") {
      value += "<feFuncR type='table' tableValues='" + levels + "'/>";
   }
   levels = currentFilter.greenLevels();
   if (levels !== "") {
      value += "<feFuncG type='table' tableValues='" + levels + "'/>";
   }
   levels = currentFilter.blueLevels();
   if (levels !== "") {
      value += "<feFuncB type='table' tableValues='" + levels + "'/>";
   }
   if (value !== "") {
      value = "<feComponentTransfer>" + value + "</feComponentTransfer>"
   }
   return value;
}

function makeColorMatrix() {
   var brightness = currentFilter.brightness / 100;
   var vals = [
      brightness, 0, 0, 0, 0,
      0, brightness, 0, 0, 0,
      0, 0, brightness, 0, 0,
      0, 0, 0, 1, 0
   ];
   vals = saturate(currentFilter.saturation, vals);
   vals = rotateHue(currentFilter.hueRot, vals);
   vals = doExtra3(currentFilter.extra3, vals);
   vals = doSepia(currentFilter.sepia, vals);
   vals = doDusk(currentFilter.dusk, vals);
   var isIdentity = true;
   var filterValue = "";
   for (var i=0, j=-1; i < 20; ++i) {
      var v = vals[i];
      if ((i%5) == 0) ++j;
      if ((i%5) == j) { // diagonal
         if (v != 1) isIdentity = false;
      }else {
         if (v != 0) isIdentity = false;
      }
      filterValue += " " + vals[i].toFixed(4);
   }
   if (!isIdentity) {
      return "<feColorMatrix type='matrix' values='" +  filterValue + "'/>"
   }else {
      return ""; // no need for.
   }
}

function filterImage() {
   currentFilter.refresh();
   // alert(dump(currentFilter));
   // alert(JSON.stringify(currentFilter));
   var filterValue = "";
   var colorsLevels = makeColorsLevels();
   var colorMatrix = makeColorMatrix();
   var contrast = makeContrast();
   var gaussianBlur = makeGaussianBlur();
   var filterNoir = makeFilterNoir();
   var filterMoat = makeFilterMoat();
   var sharpen = makeSharpen();

   filterValue = colorsLevels;
   if (colorMatrix !== "") {
      if (filterValue !== "") filterValue += "\r\n";
      filterValue += colorMatrix;
   }
   if (contrast != "") {
      if (filterValue !== "") filterValue += "\r\n";
      filterValue += contrast;
   }
   if (gaussianBlur != "") {
      if (filterValue !== "") filterValue += "\r\n";
      filterValue += gaussianBlur;
   }
   if (filterNoir != "") {
      if (filterValue !== "") filterValue += "\r\n";
      filterValue += filterNoir;
   }
   if (filterMoat != "") {
      if (filterValue !== "") filterValue += "\r\n";
      filterValue += filterMoat;
   }
   if (sharpen != "") {
      if (filterValue !== "") filterValue += "\r\n";
      filterValue += sharpen;
   }
   if (currentFilter.isVignetted) {
      if (filterValue !== "") filterValue += "\r\n";
      filterValue += "<feVignette radius='" + currentFilter.vigRadius + "' bright='" + currentFilter.vigBright + "'/>";
   }
   // document.getElementById("filterValue").innerHTML = filterValue;

   var originalImage = document.getElementById("originalImage");
   var filteredImage = document.getElementById("filteredImage");
   if (filterValue === "") {
      filteredImage.src = originalImage.src;
   }else {
      var w = originalImage.width;    // 450
      var h = originalImage.height;   // 281
      var baseFilter = (
        "<g>" +
           "<image xlink:href='" + originalImage.src +
           "' width='" + w +
           "' height='" + h +
           "' filter='url(data:image/svg+xml," +
           escape(
              "<svg xmlns='http://www.w3.org/2000/svg'><filter id='f1'>" +
              colorsLevels +
              colorMatrix +
              contrast +
              gaussianBlur +
              filterNoir +
              filterMoat +
              sharpen +
              "</filter></svg>"
           ) +
           "#f1)'></image>" +
        "</g>"
      );
      var svg;
      if (!currentFilter.isVignetted) {
         svg = (
            "<svg width='" + w + "' height='" + h + "' xmlns='http://www.w3.org/2000/svg' xmlns:xlink='http://www.w3.org/1999/xlink'>" +
            baseFilter +
            "</svg>"
         );
      }else {
         svg = vignetize(baseFilter, originalImage, currentFilter.vigRadius/100, currentFilter.vigBright/100);
      }
      filteredImage.src = URL.createObjectURL(
         new Blob([svg], {type:"image/svg+xml"})
      );
   }
}

function vignetize(baseFilter, originalImage, r, b)
{
   var f2 = "f2";
   var f3 = "f3";
   var f4 = "f4";
   // var border = 6;
   var w = originalImage.width;    // 450
   var h = originalImage.height;   // 281
   var m, t, u, p, q, cx, cy;
   if (h < w) {
      m = h;
      t = (h-w)/2;       // -84
      u = 0;             // 0
      p = 1.0;
      q = w/h;           // 1.6014
   }else {
      m = w;
      t = 0;
      u = (w-h)/2;
      p = h/w;
      q = 1.0;
   }
   var border = (m * 0.0214) | 0;
   return (
     "<svg width='" + m + "' height='" + m + "' xmlns='http://www.w3.org/2000/svg' xmlns:xlink='http://www.w3.org/1999/xlink'>" +
       "<g transform='translate(" + t + "," + u + ")'>" +
         baseFilter +
         "<g>" +
            "<image xlink:href='" + originalImage.src +
            "' width='" + w + "' height='" + h +
            "' filter='url(data:image/svg+xml," +
            escape(
               "<svg xmlns='http://www.w3.org/2000/svg'><filter id='" + f2 + "'>" +
               "<feGaussianBlur stdDeviation='2'/>" +
               "<feColorMatrix type='matrix' values='" + b + " 0 0 0 0 0 " + b + " 0 0 0 0 0 " + b + " 0 0 0 0 0 1 0'/>" +
               "</filter></svg>"
            ) +
            "#" + f2 +
            ")' mask ='url(data:image/svg+xml," +
            escape(
               "<svg xmlns='http://www.w3.org/2000/svg'>" +
               "<mask id='" + f3 +
               "' maskUnits='objectBoundingBox' maskContentUnits='objectBoundingBox'>" +
               "<rect x='0' y='0' width='1' height='1' stroke-width='0'" +
               " fill='url(data:image/svg+xml," +
               escape(
                  "<svg xmlns='http://www.w3.org/2000/svg'><radialGradient id='" + f3 + "'" +
                     " gradientUnits='objectBoundingBox' cx='" + (1/(2*p)) + "' cy='" + (1/(2*q)) + "' r='" + r + "'" +
                     " gradientTransform='scale(" + p + "," + q + ")'" +
                  ">" +
                     "<stop offset='0.4' stop-color='#ffffff' stop-opacity='0'/>" +
                     "<stop offset='0.6' stop-color='#ffffff' stop-opacity='1'/>" +
                  "</radialGradient></svg>"
               ) +
               "#" + f3 + ")'/>" +
               "</mask></svg>"
            ) +
            "#" + f3 + ")'></image>" +
         "</g>" +
         "<g fill='none' stroke='white' stroke-width='" + border + "' >" +
           "<rect x='" + ((border/2)-t) + "' y='" + ((border/2)-u) + "' width='" + (m-border) + "' height='" + (m-border) + "'/>" +
         "</g>" +
       "</g>" +
     "</svg>"
   );
}


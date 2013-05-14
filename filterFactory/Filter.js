function Filter() {
   this.refresh();
   this.colorsLevels = [
      Filter.colorsLevelsBase.slice(0),
      Filter.colorsLevelsBase.slice(0),
      Filter.colorsLevelsBase.slice(0)
   ];
}
Filter.colorsLevelsBase = [
   0, 0.125, 0.250, 0.375, 0.500, 0.625, 0.750, 0.875, 1.0000
];
Filter.prototype = {
   refresh: function() {
      this.blur = parseInt(document.getElementById("blur").value);
      this.brightness = parseInt(document.getElementById("brightness").value);
      // this.colorsLevels = colorsLevels;
      this.contrast = parseInt(document.getElementById("contrast").value);
      this.dusk = document.getElementById("dusk").checked;
      this.extra3 = parseInt(document.getElementById("extra3").value);
      this.hueRot = parseInt(document.getElementById("rotateHue").value);
      this.isSharpened = document.getElementById("sharpenChk").checked;
      this.isVignetted = document.getElementById("vignetteChk").checked;
      this.moat = document.getElementById("moat").checked;
      this.noir = document.getElementById("noir").checked;
      this.saturation = parseInt(document.getElementById("saturation").value);
      this.sepia = document.getElementById("sepia").checked;
      this.sharpen = parseInt(document.getElementById("sharpen").value);
      this.vigBright = parseInt(document.getElementById("vigBright").value);
      this.vigRadius = parseInt(document.getElementById("vigRadius").value);
      this.setDisplayedValues();
   },
   restore: function(value) {
      var src = JSON.parse(value);
      this.blur = document.getElementById("blur").value = src.blur;
      this.brightness = document.getElementById("brightness").value = src.brightness;
      this.contrast = document.getElementById("contrast").value = src.contrast;
      this.dusk = document.getElementById("dusk").checked = src.dusk;
      this.extra3 = document.getElementById("extra3").value = src.extra3;
      this.hueRot = document.getElementById("rotateHue").value = src.hueRot;
      this.isSharpened = document.getElementById("sharpenChk").checked = src.isSharpened;
      this.isVignetted = document.getElementById("vignetteChk").checked = src.isVignetted;
      this.moat = document.getElementById("moat").checked = src.moat;
      this.noir = document.getElementById("noir").checked = src.noir;
      this.saturation = document.getElementById("saturation").value = src.saturation;
      this.sepia = document.getElementById("sepia").checked = src.sepia;
      this.sharpen = document.getElementById("sharpen").value = src.sharpen;
      this.vigBright = document.getElementById("vigBright").value = src.vigBright;
      this.vigRadius = document.getElementById("vigRadius").value = src.vigRadius;
      this.colorsLevels = src.colorsLevels;
      var prefixes = ["red", "green", "blue"];
      for (var i=0; i < 3; ++i) {
         var row = this.colorsLevels[i];
         var prefix = prefixes[i];
         for (var j=0; j < 9; ++j) {
            document.getElementById(prefix + j).value = (row[j] * 255);
         }
      }
      this.setDisplayedValues();
   },
   setDisplayedValues: function() {
      document.getElementById("blurVal").textContent =  (this.blur/10).toFixed(2);
      document.getElementById("brightnessVal").textContent =  this.brightness.toFixed(0);
      document.getElementById("contrastVal").textContent =  this.contrast.toFixed(0);
      document.getElementById("extra3Val").textContent =  this.extra3.toFixed(0);
      document.getElementById("rotateHueVal").textContent =  this.hueRot.toFixed(0);
      document.getElementById("saturationVal").textContent =  this.saturation.toFixed(0);
      document.getElementById("sharpenVal").textContent =  this.sharpen.toFixed(0);
      document.getElementById("vigBrightVal").textContent = this.vigBright;
      document.getElementById("vigRadiusVal").textContent = this.vigRadius;
   },
   updateColorsLevels: function(index, value) {
      var ix = parseInt(index);
      this.colorsLevels[(ix/9)|0][ix%9] = parseFloat((value/255).toFixed(4));
   },
   getColorsLevels: function(index) {
      var levels = "";
      var row = this.colorsLevels[index];
      var identity = (row[0] === Filter.colorsLevelsBase[0]);
      levels += row[0];
      for (var i=1; i < 9; ++i) {
         if (identity) identity = (row[i] === Filter.colorsLevelsBase[i]);
         levels += ", " + row[i];
      }
      return identity? "" : levels;
   },
   redLevels: function() {
      return this.getColorsLevels(0);
   },
   greenLevels: function() {
      return this.getColorsLevels(1);
   },
   blueLevels: function() {
      return this.getColorsLevels(2);
   }
}

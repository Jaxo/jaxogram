function JgUsers() {
   var selectedIndex = 0;
   var that = this;
   var users = [];  // n x JgUserItem
   var payment;     // JgPayment
   readPayment();
   readUsers();

   this.getAccessPass = function() {
      return users[selectedIndex]["p"];
   }
   this.getUserName = function() {
      return users[selectedIndex]["n"];
   }
   this.getNet = function() {
      return users[selectedIndex]["t"];
   }
   this.getImageUrl = function() {
      return users[selectedIndex]["i"] || "images/none.png";
   }
   this.getScreenName = function() {
      var val = users[selectedIndex]["s"];
      if (val) {
         return "@" + val;
      }else {
         val = users[selectedIndex]["tt"];
         if (val) {
            return val;
         }else {
            return "";
         }
      }
   }
   this.getAlbumTitle = function() {
      if (users.length != 0) {
         return users[selectedIndex]["tt"];
      }else {
         return null;
      }
   }
   this.getAlbumId = function() {
      return users[selectedIndex]["ai"];
   }
   this.getLocale = function() {
      var iso639 = localStorage.getItem("jgLocale");
      if (!iso639) {
         iso639 = navigator.language;
         localStorage.setItem("jgLocale", iso639);
      }
      return iso639;
   }
   this.setLocale = function(iso639) {
      localStorage.setItem("jgLocale", iso639);
   }
   this.getPayState = function() {
      if (payment == null) {
         return "none";
      }else {
         return payment['s'];
      }
   }
   this.getPayTime = function() {
      if (payment == null) {
         return 0;
      }else {
         return payment['d'];
      }
   }
   this.getPayKey = function() {
      if (payment == null) {
         return 0;
      }else {
         return payment['pk'];
      }
   }
   this.setAlbum = function(albumId, albumTitle) {
      users[selectedIndex]["ai"] = albumId;
      users[selectedIndex]["tt"] = albumTitle;
      rewriteUsers();
   }
   this.hasSome = function() {
      return users.length != 0;
   }
   this.addUser = function(userName, userPass, userNet, usrImg, userScreen) {
      if (userName != null) {
         selectedIndex = users.length;
         users.push(
            new JgUserItem(userName, userPass, userNet, usrImg, userScreen)
         );
         rewriteUsers();
      }
   }
   this.deleteUser = function(userName, userPass, userNet) {
      removeIf(
         function(user, at, users) {
            return (
               ((userName == null)||(userName === "")||(userName === user["n"])) &&
               ((userPass == null)||(userPass === "")||(userPass === user["p"])) &&
               ((userNet == null)||(userNet === "")||(userNet === user["t"]))
            );
         }
      );
   }
   this.deleteUserAt = function(userAt) {
      removeIf(function(user, at, users) { return (userAt == at); });
   }
   this.selectUserAt = function(index) {
      rewriteSelectedIndex(index);
   }
   this.forEach = function(
      doFunction /* (name, pass, albumTitle, isSelected, network) */
   ) {
      users.forEach(
         function(user, at, users) {
            doFunction(user["n"], user["p"], user["tt"], at == selectedIndex, user["t"]);
         }
      );
   }
   this.cleanUp = function() {
      removeIf(
         function(user, at, users) {
            return ((user["n"] == null) || (user["p"] == null));
         }
      );
   }
   this.destroy = function() {
      localStorage.removeItem("jgUsers");
      localStorage.removeItem("jgPayment");
      localStorage.removeItem("jgLocale");
      localStorage.removeItem("jgSelectAt");
      readUsers();
   }
   function removeIf(ifFunction) {
      var oldCount = users.length;
      newUsers = [];
      users.forEach(
         function(user, at, users) {
            if (!ifFunction(user, at, users)) newUsers.push(user);
         }
      );
      if (oldCount != newUsers.length) {
         users = newUsers;
         rewriteUsers();
         rewriteSelectedIndex(selectedIndex);
      }
   };
   function readUsers() {
      var value = localStorage.getItem("jgUsers");
      if (value == null) {
         users = [];
      }else {
         users = JSON.parse(value);
      }
      rewriteSelectedIndex(localStorage.getItem("jgSelectAt"));
   }
   function rewriteUsers() {
      localStorage.setItem("jgUsers", JSON.stringify(users));
   }
   function rewriteSelectedIndex(index) {
      if ((index == null) || (index < 0) || (index >= users.length)) {
         index = 0;
      }
      localStorage.setItem("jgSelectAt", index);
      selectedIndex = index;
   }
   function readPayment() {
      var value = localStorage.getItem("jgPayment");
      if (value == null) {
         payment = null;
      }else {
         payment = JSON.parse(value);
      }
   }
   this.writePayment = function(payKey, pay) {
      payment = new JgPayment(payKey, pay);
      localStorage.setItem("jgPayment", JSON.stringify(payment));
   }
   this.deletePayment = function() {
      localStorage.removeItem("jgPayment");
      payment = null;
   }
}

function JgUserItem(userName, userPass, userNet, userImg, userScreen) {
   this["n"] = userName;
   this["p"] = userPass;
   this["t"] = userNet;
   this["i"] = userImg; // image url
   this["s"] = userScreen; // screen name
   this["ai"] = null;   // album id
   this["tt"] = null;   // album title
}

function JgPayment(payKey, pay) {
   this['pk'] = payKey;
   this['s'] = pay['state'];
   this['d'] = pay['date'];
}

function JgUsers() {
   createLocalStorageIfNeeded();
   var selectedIndex = 0;
   var that = this;
   var users = [];
   readUsers();

   this.getAccessPass = function() {
      return users[selectedIndex].p;
   }
   this.getUserName = function() {
      return users[selectedIndex].n;
   }
   this.hasSome = function() {
      return users.length != 0;
   }
   this.addUser = function(userName, userPass, userNet) {
      if (userName != null) {
         selectedIndex = users.length;
         users.push(new JgUserItem(userName, userPass, userNet));
         rewriteUsers();
      }
   };
   this.deleteUser = function(userName, userPass, userNet) {
      removeIf(
         function(user, at, users) {
            return (
               ((userName == null)||(userName === "")||(userName === user.n)) &&
               ((userPass == null)||(userPass === "")||(userPass === user.p)) &&
               ((userNet == null)||(userNet === "")||(userNet === user.t))
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
   this.forEach = function(doFunction/*(userName, userPass, userNet)*/) {
      users.forEach(
         function(user, at, users) {
            doFunction(user.n, user.p, user.t, at == selectedIndex);
         }
      );
   }
   this.cleanUp = function() {
      removeIf(
         function(user, at, users) {
            return ((user.n == null) || (user.p == null));
         }
      );
   }
   this.destroy = function() {
      localStorage.removeItem("jgUsers");
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
}

function JgUserItem(userName, userPass, userNet) {
   this.n = userName;
   this.p = userPass;
   this.t = userNet;
}




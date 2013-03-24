window.onload = function() {
   var loc = window.location;
   if (loc.protocol !== "app:") {
      var host = loc.host;
      if (host.indexOf("appspot") >= 0) {              // appspot default, or versioned
         server_url = "http://" + host + "/jaxogram";
      }else {                                          // "http://localhost:8888/", or
         server_url = "http://localhost:8888/jaxogram" // "http://ottokar/jaxogram/index.html"
      }
   }
   if (server_url !== "http://jaxogram.appspot.com/jaxogram") {
      simpleMsg("warning", i18n("testMode", server_url));
   }
   createDispatcher();
   users = new JgUsers();
   // users.cleanUp();
   // users.destroy();
   var params = getQueryParams();

   if (params.OP === "backCall") {
      /*
      | this occurs for non-packaged applications only:
      | b/c packaged app have no origin, can not appear as referer...
      | so there is no way to call them back from the external world.
      */
      registerUser(params.VRF, params.NET);
   }

   dispatcher.on(
      "install_changed",
      function action(state, version) {
         if (
            (state === "uninstalled") && !users.hasSome() &&
            (params.OP !== "backCall")
         ) {
            confirmMsg(
               i18n('betterInstall'),
               function() { document.getElementById("btnInstall").click(); }
            );
         }else if ((state === "installed") && version) {
            document.querySelector("header h1 small").textContent = version;
         }
      }
   );

   setInstallButton("btnInstall");
   fitImage(document.getElementById('photoImage'));
   window.addEventListener("resize", fitImages, false);

   // Listeners
   document.getElementById("btnMain").onclick = toggleSidebarView;
   document.getElementById("sidebarMenu").onclick = menuListClicked;
   document.getElementById("jgUsersAid").onclick = listAlbums;
   document.getElementById("changeLanguage").onclick = changeLanguage;
   document.getElementById("footerTable").onclick = function() { expandSidebarView(-1); };
   document.getElementById("pickAndUpload").onclick = pickAndUpload;
   // document.getElementById("whoAmI").onclick = whoAmI;

   var dfltLocale = navigator.language || navigator.userLanguage;
   formatUsersList(false);
   translateBody(dfltLocale);
   document.getElementById('usedLang').textContent = i18n(dfltLocale);
   document.getElementById(dfltLocale).setAttribute("aria-selected", "true");
// window.onerror = function(msg, url, linenumber){
//    alert('Error: ' + msg + '\nURL: ' + url + '\n@ line: ' + linenumber);
//    return true;
// }
   var eltMain = document.getElementById("main");
   new GestureDetector(eltMain).startDetecting();
   eltMain.addEventListener(
      "swipe",
      function(e) {
         // alert("Swipe:" + "\n start: " + detail.start + "\n end: " + detail.end + "\n dx: " + detail.dx + "\n dy: " + detail.dy + "\n dt: " + detail.dt + "\n vx: " + detail.vx + "\n vy: " + detail.vy + "\n direction: " + detail.direction + "\n angle: " + detail.angle);
         var direction = e.detail.direction;
         if (direction === 'right') {
            expandSidebarView(1);
         }else if (direction === 'left') {
            expandSidebarView(-1);
         }
      }
   );
};


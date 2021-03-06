jaxogram
========

Basically, Jaxogram is a "Photo Sharing" application, working on social networks such as Orkut.
In the spirit of targeting the largest audience, it runs on Firefox OS.


Market Place
============

Jaxo's Mozilla Marketplace id is jaxo@jaxo.com (the password is the standard one).

To test the packaging of Jaxogram for the marketplace:

1. For safety...
<UL><LI>Remove the Firefox OS Simulator from Firefox
<UL><LI>do <CODE>Firefox->Tools->Add Ons->Extensions</CODE>
</LI><LI>select Firefox OS Simulator,
</LI><LI>press the Remove button
</LI></UL>
</LI><LI>Reinstall the Firefox OS Simulator from https://addons.mozilla.org/en-US/firefox/addon/firefox-os-simulator
</LI></UL><BR/>

2. Start the Firefox OS Simulator and turn on Developer Mode, which is a setting buried deep in<BR/><CODE>Settings->Device Information->More Information->Developer</CODE>

3. Run the <code>pkgMe</code> shell script.
<BR/>First time use? *Modify <CODE>pkgMe</CODE>, so it knows your URL server address, and the top directory of your local server files*
<BR/>PkgMe does:
<UL><LI>create a jaxogram.zip packaged app from jaxogram/war:<BR/><CODE>zip jaxogram -x */.DS_Store -r manifest.webapp index.html favicon.ico js style applogos images</CODE>
</LI><LI>create an install.html and package.manifest
</LI><LI>all these files being saved into the top level directory of your local Web server files
</LI></UL><BR/>

4. Install the jaxogram app by entering the following in Firefox in the Firefox OS Simulator: <BR/><CODE>http://&lt;URL server address&gt;/install.html</CODE>

5. With jaxogram successfully installed, start jaxogram and try <CODE>Who am I?</CODE>

Good links:

1. Robert Nymam's packaged app, derived from fxosstub: https://github.com/robnyman/Firefox-OS-Boilerplate-App/tree/packaged-app

2. Sample XHR, crossdomainrequest.js, https://gist.github.com/4401599

3. Firefox OS permission matrix, https://docs.google.com/spreadsheet/ccc?key=0Akyz_Bqjgf5pdENVekxYRjBTX0dCXzItMnRyUU1RQ0E#gid=0


OAuth Scenario
==============

Characters:
----------

- User (Apache2 server on Ottokar, or Firefox Mobile Device);
- Jaxo (localhost:8888/jaxogram, or jaxogram.appspot.com);
- Orkut.

When Registering
----------------

    User -> Jaxo (OP=getURL): I want to pass the auth procedure
    Jaxo -> Orkt: Point me to your URL for auth registration, and call me back
                 when the user is done with
                 (CB with OP=backCall, and User address, aka referer)
                 | Except for packaged app:
                 | referer contains a random key ("authKey"), because
                 | the user address doesn't exist
                 Jaxo stores the "accessor" into a session variable
    Orkt -> Jaxo: here is the auth URL where User must navigate to
    Jaxo -> User: window.location = the auth URL
                 | Except for packaged app:
                 |  we do *not* leave the app, or we would never return to it,
                 |  What is done:
                 |   - Open a mozbrowser as an iframe [requires: browser perm]
                 |   - Point the iframe at the auth URL
    ---------
    [No valid Session ID here]
    User -> Orkt: I authorize Jaxo to issue requests on my behalf (or I deny access)
    Orkt -> Jaxo (OP=backCall): User said OK, here is your "verifier"
                 Jaxo:
                 - redirects the response to User (the referer)
                 - passes the oauth "verifier" in the parameters (and OP=backCall)
                 | Except for packaged app, where Jaxo:
                 | - stores the oauth "verifier" in the memcache, under the key
                 |   "authKey"
                 | - the "User" has provided a watch-dog ("getVerifier"),
                 |   waiting for this action

    Jaxo -> User: interprets the OP backcall as an XHR
                 | Except for packaged app:
                 | Jaxo's dog wakes up, get the "verifier" from the memcache,
                 | and kills the mozbrowser
    [Session ID again valid]
    ---------
    User -> Jaxo: (OP=getAccPss) obtain the "access password", given the "accessor" and the
                  "verifier"
                  save the "access password" in a session variable
    Jaxo -> User: here is your access pass
                  User saves it in Permanent Local Storage

After Registration
------------------

From the Permanent Locale Storage, User gets the "access password", and tells Jaxo to use it: OP=postAccessPass


Means For Testing
==================

1) Simulate a basic exchange:
-----------------------------
Eclipse, localhost:8888, Server and Jaxo on GAE simulator:
<PRE>
      in "jaxogram.js", make sure server_url is set to "http://localhost:8888/jaxogram"
      start JaxoGram on localhost:8888
</PRE>

<i>Never underestimate JavaScript problems (typos may kill you),
even if the following step is a pain in the neck **DO IT**.</i>

2) Simulate Jaxo separated from Server
--------------------------------------
Eclipse Server at localhost:8888, Jaxo on Ottokar:
<PRE>
   in "jaxogram.js",
      [line numbering may change... look for lines starting with "//OT-LH*/"]
      make sure server_url is set to "http://localhost:8888/jaxogram"
      line 19: uncomment (that is, replace the 2nd slash with a star):
         //OT-LH*/ isPackaged = true; // testing on ottokar/localhost
      line 312...313
         uncomment:
         //OT-LH*/ browserFrame.src = server_url + "?OP=backCallTest&JXK=bougnoul&oauth_verifier=tombouctou"
         comment out the next line: "browserFrame.src = targetUrl;"
      line 345...346
         uncomment
         "//OT-LH*/ alert("Bingo!\nVerifier is: " + verifier);"
         comment out the next line: "registerUser(verifier)"
   from ~/jaxoapps/jaxogram enter
         "pkgMe"
   from /home/pgr/.gvfs/build on ottokar/sites/jaxogram
         "unzip jaxogram.zip"
   start Jaxogram on ottokar/jaxogram
</PRE>
Don't forget to clean up your changes!

3) Firefox OS simulator
-----------------------
See "Market Place" above
<PRE>
   from ~/jaxoapps/jaxogram enter "pkgMe"
   install from "http://ottokar/jaxogram/install.html"
</PRE>

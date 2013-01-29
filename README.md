jaxogram
========

Basically, Jaxogram is a "Photo Sharing" application, working on social networks such as Orkut.
In the spirit of targeting the largest audience, it runs on Firefox OS.


Market Place
============

Jaxo's Mozilla Marketplace id is jaxo@jaxo.com (the password is the standard one).

To test the packaging of Jaxogram for the marketplace:

1. Remove the Firefox OS Simulator from Firefox (Firefox->Tools->Add Ons->Extensions, select Firefox OS Simulator, press the Remove button)

2. Reinstall the Firefox OS Simulator from https://addons.mozilla.org/en-US/firefox/addon/firefox-os-simulator

3. Start the Firefox OS Simulator and turn on Developer Mode, which is a setting buried deep in Settings->Device Information->More Information->Developer

4. Create a jaxogram.zip packaged app from jaxogram/war: zip jaxogram -x */.DS_Store -r manifest.webapp index.html favicon.ico js style applogos images

5. Copy jaxogram.zip and jaxogram/war/packaged-app/install.html and jaxogram/war/packaged-app/package.manifest into the top level directory on a local Web server (BE SURE TO CHANGE THE URL'S ADDRESS IN BOTH OF THE packaged-app FILES ACCORDINGLY)

6. Install the jaxogram app by entering the following in Firefox in the Firefox OS Simulator: http://<local-web-server>/install.html

7. With jaxogram successfully installed, start jaxogram and try Who am I?

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
                 Jaxo stores the "accessor" into a session variable
    Orkt -> Jaxo: here is the auth URL where User must navigate to
    Jaxo -> User: window.location = the auth URL
    ---------
    [No valid Session ID here]
    User -> Orkt: I authorize Jaxo to issue requests on my behalf (or I deny access)
    Orkt -> Jaxo (OP=backCall): User said OK, here is your "verifier"
                 Jaxo:
                 - redirects the response to User (the referer)
                 - passes the oauth "verifier" in the parameters (and OP=backCall)
    Jaxo -> User: interprets the OP backcall as an XHR
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







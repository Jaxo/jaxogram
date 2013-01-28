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

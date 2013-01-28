Jaxo's Mozilla Marketplace id is jaxo@jaxo.com (the password is the standard one).

To package Jaxogram for the marketplace:

    cd jaxogram/war
    rm -f jaxogram.zip
    zip jaxogram -x */.DS_Store -r manifest.webapp index.html favicon.ico js style applogos images

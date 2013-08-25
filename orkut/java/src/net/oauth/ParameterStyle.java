package net.oauth;

/**
 * Where to place OAuth parameters in an HTTP message. The alternatives are
 * summarized in OAuth Core section 5.2.
 */
public enum ParameterStyle {
    /**
     * Send parameters whose names begin with "oauth_" in an HTTP header, and
     * other parameters (whose names don't begin with "oauth_") in either the
     * message body or URL query string. The header formats are specified by
     * OAuth Core section 5.4.
     */
    AUTHORIZATION_HEADER,

   /**
    * Flickr specific to upload photos.
    * - the body initially contains the raw data of the photo to be uploaded
    * - after being processed by net.oauth.http.HttpMessage.newRequest()
    *   - the content type is set to multipart/form
    *   - oauth parameters goes to the authorization header
    *   - non-auth parameters will appear as parts in the multipart body of
    * the POST, followed by the photo raw data
    */
    FLICKR_PHOTO_UPLOAD,

    /**
     * Send all parameters in the message body, with a Content-Type of
     * application/x-www-form-urlencoded.
     */
    BODY,

    /** Send all parameters in the query string part of the URL. */
    QUERY_STRING;
}

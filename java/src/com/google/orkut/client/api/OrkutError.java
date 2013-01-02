/* EXPERIMENTAL (really) */
/* Copyright (c) 2009 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.orkut.client.api;

import org.json.me.JSONObject;

/**
 * A error reported by a failed {@link Transaction}.
 *
 * <p>Once a transaction is executed and there is an error detected, the method
 * {@link Transaction#getError()} would return an OrkutError object instance.
 * Use this object to find out what error occurred and handle it appropriately.
 * </p>
 * 
 * Here is a description of the error codes returned:<br><br>
 * 400: Bad request, possibly malformed syntax or invalid signature<br>
 * 401: Unauthorized request or OAuth token expired<br>
 * 403: No permission or usage limits exceeded<br>
 * 500: Server error or server not responding<br>
 * -32000: Custom error<br>
 * 
 *
 * <p>Below is a sample code.</p>
 *
 * <pre>
 *   FetchScrapTx scrapTx = factory.fetchScrap();
 *   execute(scrapTx);
 *
 *   if (scrapTx.hasError()) [
 *     OrkutError error = scrapTx.getError();
 *     if (error.isAuthenticationFailure()) {
 *       // handle auth error.
 *     } else if (error.isClientError()) {
 *       // handle client error.
 *     }
 *   }
 * </pre>
 *
 * @author Sachin Shenoy
 * @author Shishir Birmiwal
 */
public class OrkutError {
  static class ErrorCode {
    static final int BAD_REQUEST = 400;
    static final int NOT_AUTHORIZED = 401;
    static final int USAGE_EXCEEDED = 403;
    static final int NO_PERMISSION = 403;
    static final int SERVER_ERROR = 500;
    static final int CUSTOM_ERROR = -32000;
  }

  static class ErrorType {
    static final String ALBUM_DOES_NOT_EXIST = "albumDoesNotExist";
    static final String ALBUM_PHOTO_LIMIT_EXCEEDED = "albumPhotoLimitExceeded";
    static final String API_LIMIT_EXCEEDED = "apiLimitExceeded";
    static final String REQUIRES_CAPTCHA = "requiresCaptcha";
    static final String INVALID_PHONE_NUMBER = "invalidPhoneNumber";
    static final String INVALID_USER = "invalidUser";
    static final String MAX_FRIENDS_EXCEEDED = "maxFriendsExceeded";
    static final String NO_PERMISSION = "noPermissions";
    static final String PHOTO_SIZE_LIMIT_EXCEEDED = "photoSizeLimitExceeded";
    static final String SMS_QUOTA_EXCEEDED = "smsQuotaExceeded";
    static final String TOS_NOT_ACCEPTED = "tosNotAccepted";
    static final String USER_PHOTO_LIMIT_EXCEEDED = "userPhotoLimitExceeded";
  }

  private final JSONObject json;

  final int code;
  private final String message;

  static final String ERROR_TYPE_NOT_PRESENT = "errorTypeNotPresent";

  OrkutError(JSONObject json) {
    this.json = json;
    code = json.optInt(ResponseFields.CODE);
    message = json.optString(Params.MESSAGE);
  }

  /**
   * Returns true if this is an authentication failure.
   *
   * <p>To recover from this error, get fresh authentication credentials,
   * which may involve asking the user to login again.<p>
   */
  public boolean isAuthenticationFailure() {
    return code == OrkutError.ErrorCode.NOT_AUTHORIZED && !hasErrorType();
  }

  /**
   * Returns true if this is a Captcha error. The client needs to resend
   * this request with a captcha answer.
   *
   * <p>To recover from this error retry the request with answer to the
   * captcha given by the url {@link #captchaUrl()} </p>
   */
  public boolean isCaptchaError() {
    return code == OrkutError.ErrorCode.NOT_AUTHORIZED &&
        OrkutError.ErrorType.REQUIRES_CAPTCHA.equals(errorType());
  }

  /**
   * Returns the captcha url challenge. Only present if this is a captcha error.
   */
  public String captchaUrl() {
    return getDataField(Params.CAPTCHA_URL);
  }

  /**
   * Returns the token associated with captcha challenge.
   */
  public String captchaToken() {
    return getDataField(Params.CAPTCHA_TOKEN);
  }
  /**
   * Returns true if the logged in user has not accepted Orkut ToS (Terms Of
   * Service).
   *
   * <p>To recover from this error, ask the user to login to
   * http://www.orkut.com and accept the Terms Of Service.</p>
   */
  public boolean isTosNotAcceptedByUser() {
    return code == OrkutError.ErrorCode.NOT_AUTHORIZED &&
        OrkutError.ErrorType.TOS_NOT_ACCEPTED.equals(errorType());
  }

  /**
   * Returns true if api call limit has reached.
   *
   * <p>To recover from this error, try again after waiting for sometime. To
   * prevent this error from occurring reduce the number of calls that are being
   * made.</p>
   */
  public boolean isApiLimitExceeded() {
    return code == OrkutError.ErrorCode.USAGE_EXCEEDED &&
        OrkutError.ErrorType.API_LIMIT_EXCEEDED.equals(errorType());
  }

  /**
   * Returns true if the user could not be found or mapped to an invlaid user.
   *
   * <p>There is no way to recover from this error. Prompt the user to login
   * with some other user id, as most probably the original account got deleted
   * or got locked out.</p>
   */
  public boolean isInvalidUser() {
    return code == OrkutError.ErrorCode.NOT_AUTHORIZED &&
        OrkutError.ErrorType.INVALID_USER.equals(errorType());
  }

  /**
   * Returns true if the app/user does not have permission/access rights to
   * perform the requested operation.
   *
   * <p>To recover from this error, you will have to get the required
   * permission/access. This would be dependent on the operation that is
   * being performed.</p>
   */
  public boolean isNoPermission() {
    return code == OrkutError.ErrorCode.NO_PERMISSION &&
        OrkutError.ErrorType.NO_PERMISSION.equals(errorType());
  }

  /**
   * Returns true if the album being referenced cold not be found.
   *
   * <p> This is a client error, or perhaps te album was deleted by someone
   * while the client had a cache wiht it. Refersh the album id and try the
   * request again. </p>
   */
  public boolean isAlbumDoesNotExist() {
    return code == OrkutError.ErrorCode.CUSTOM_ERROR &&
        OrkutError.ErrorType.ALBUM_DOES_NOT_EXIST.equals(errorType());
  }

  /**
   * Returns true if the phone number given was invalid.
   *
   * <p>This is a client error, where wrongly formatted phone number was given
   * along with an api. Try the request again with the right phone number </p>
   */
  public boolean isInvalidPhoneNumber() {
    return code == OrkutError.ErrorCode.CUSTOM_ERROR &&
        OrkutError.ErrorType.INVALID_PHONE_NUMBER.equals(errorType());
  }

  public String getPhoneNumber() {
    return null;
  }

  /**
   * Returns true if there was a client side error.
   */
  public boolean isClientError() {
    return code >= 400 && code < 500;
  }

  /**
   * Returns true if there was a server side error.
   */
  public boolean isServerError() {
    return code >= 500 && code < 600;
  }

  /**
   * Returns the error message string (this is beyond the other error params).
   */
  String getMessage() {
    return message;
  }

  /**
   * Returns a string representation of this error.
   */
  public String toString() {
    return "error " + code + " (" + errorType() + ") " + message;
  }

  /**
   * Returns true if the errorType is present.
   */
  private boolean hasErrorType() {
    JSONObject data = data();
    return (data == null) ? false : data.has(Fields.ERROR_TYPE);
  }

  String errorType() {
    JSONObject data = data();
    return (data == null) ? OrkutError.ERROR_TYPE_NOT_PRESENT : data.optString(Fields.ERROR_TYPE);
  }

  private String getDataField(String dataField) {
    JSONObject data = data();
    return (data == null) ? "" : data.optString(dataField);
  }

  private JSONObject data() {
    JSONObject data = json.optJSONObject(ResponseFields.RESULT_KEY);
    if (data == null) {
      data = json.optJSONObject(ResponseFields.RESULT_KEY_DATA);
    }
    return data;
  }
}

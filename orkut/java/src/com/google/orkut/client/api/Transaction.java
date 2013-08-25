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
 * Represents a transaction. A transaction constitutes of a request and a reply.
 *
 * @author Sachin Shenoy
 * @author Shishir Birmiwal
 */
public class Transaction  {
  protected OrkutError orkutError;
  protected OrkutRequest request;

  Transaction(String method) {
    request = new OrkutRequest(method);
  }

  Transaction(String id, String method) {
    request = new OrkutRequest(id, method);
  }

  /**
   * Returns the ID of this transaction.
   *
   * @return ID of the transaction.
   */
  String getId() {
    return request.getId();
  }

  /**
   * Returns <code>true</code> if the response in the transaction had error set.
   *
   * <p>
   * If this method returns true, then {@code #getError()} will return the
   * error object that will have error detials.
   * </p>
   *
   * @return true if the response had error set.
   */
  public boolean hasError() {
    return (orkutError != null);
  }

  /**
   * {@link OrkutError} with details of the error.
   *
   * @return error object with details of error.
   */
  public OrkutError getError() {
    return orkutError;
  }

  /**
   * Returns the string representation of json request.
   *
   * @return string representation of the json request.
   */
  JSONObject getRequestAsJson() {
    return request.getJSONObject();
  }

  /**
   * Returns the name of the param where the body should be placed in multipart
   */
  String getParamName() {
    return null;
  }

  void setResponse(JSONObject response) {
    JSONObject error = response.optJSONObject(ResponseFields.ERROR_KEY);
    if (error != null) {
      // set error fields
      orkutError = new OrkutError(error);
      return;
    }

    // no errors
    JSONObject data = getData(response);
    if (data == null) {
      throw new RuntimeException("both 'data' and 'error' are missing!");
    }

    setResponseData(data);
  }

  protected JSONObject getData(JSONObject response) {
    JSONObject data = response.optJSONObject(ResponseFields.RESULT_KEY);
    if (data == null) {
      data = response.optJSONObject(ResponseFields.RESULT_KEY_DATA);
    }
    return data;
  }

  OrkutRequest getOrkutRequest() {
    return request;
  }

  /**
   * Invoked to set the response data to the request.
   *
   * <p>
   * This will be only invoked if there was no error, either in network, or in
   * the json response. Hence, it is guaranteed that response object has valid
   * value.
   * </p>
   * @param data data response from the server
   */
  protected void setResponseData(JSONObject data) {}
}
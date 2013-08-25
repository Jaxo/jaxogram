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

import org.json.me.JSONArray;
import org.json.me.JSONException;
import org.json.me.JSONObject;

/**
 * Builder to construct a JSON-RPC request to be sent to Orkut servers.
 *
 * <pre>
 * {@code
 * OrkutRequest request = new OrkutRequest("m32", "people.get");
 * request.addParameter("userId", "@me")
 *        .addParameter("groupId", "@self")
 *        .addField("id")
 *        .addField("age")
 *        .toString();
 * }
 * </pre>
 * Returns the following string value of following json.
 * <pre>
 * {
 *   "id": "m32",
 *   "method": "people.get",
 *   "params": {
 *     "userId": "@me",
 *     "groupId": "@self",
 *     "fields": ["id", "age"],
 *    }
 * }
 * </pre>
 *
 * @author Kartick Vaddadi
 */
class OrkutRequest {
  /** Used to generate a unique id for each request that is sent */
  private static int nextInt = 0;

  /** "id" of the request. */
  private final String id;

  /** Method of the request */
  private final String method;

  /** "params" field of the request */
  private final JSONObject params ;

  /** "fields" parameter of the request */
  private final JSONArray fields;

  OrkutRequest(String method) {
    this(method, method);
  }

  /**
   * Initializes a request. Clients should not use this directly. They should
   * use methods from a {@link Transaction} to create and initialize OrkutRequest.
   *
   * @param id the id used to identify a request within a batch. It is also used
   *     to identify the method invoked. This information is used while parsing
   *     the response for the request.
   * @param method the json-rpc method that is invoked by the request
   */
  OrkutRequest(String id, String method) {
    this.method = method;
    this.id = nextInt++ + "-" + id;
    params  = new JSONObject();
    fields = new JSONArray();
  }

  /**
   * Add a json array parameter to the request.
   *
   * @param name name of the parameter
   * @param value value of the parameter
   * @return this object for chaining
   */
  OrkutRequest addParameter(String name, Object value) {
    try {
      params.put(name, value);
    } catch (JSONException e) {
      // rethrow as runtime exception
      throw new RuntimeException("A JSONException should never happen!", e);
    }
    return this;
  }

  /**
   * Add a field to the fields list.
   *
   * @param name the field name
   * @return this to facilitate chaining
   */
  OrkutRequest addField(String name) {
    fields.put(name);
    return this;
  }

  /**
   * Adds 'fieldName' to 'fields' if not already present
   */
  OrkutRequest addFieldIfNotPresent(String fieldName) {
    int fieldsLength = fields.length();
    try {
      for (int i = 0; i < fieldsLength; i++) {
        if (fieldName.equals(fields.get(i))) {
          return this;
        }
      }
      fields.put(fieldName);
      return this;
    } catch (JSONException jse) {
      throw new RuntimeException(Util.getRuntimeErrorMessage("OrkutRequest.addIfNotPresent"),
          jse);
    }
  }

  /**
   * Sets value of "userId" parameter.
   *
   * @param userId value of userId parameter
   * @return this to facilitate chaining
   * @throws JSONException
   */
  OrkutRequest setUserId(String userId) {
    addParameter(Params.USER_ID, userId);
    return this;
  }

  /**
   * Sets value of "groupId" parameter.
   *
   * @param groupId value of groupId parameter
   * @return this to facilitate chaining
   */
  OrkutRequest setGroupId(String groupId) {
    addParameter(Params.GROUP_ID, groupId);
    return this;
  }

  /**
   * Sets the value of "startIndex" parameter.
   *
   * @param startIndex value of startIndex parameter
   * @return this to facilitate chaining
   */
  OrkutRequest setStartIndex(int startIndex) {
    addParameter(Params.START_INDEX, startIndex);
    return this;
  }

  /**
   * Sets the value of "count" parameter.
   *
   * @param count value of count parameter
   * @return this to facilitate chaining.
   */
  OrkutRequest setCount(int count) {
    addParameter(Params.COUNT, count);
    return this;
  }

  OrkutRequest setAlbumId(String albumId) {
    addParameter(Params.ALBUM_ID, albumId);
    return this;
  }

  OrkutRequest setMediaItemId(String mediaItemId) {
    addParameter(Params.MEDIA_ITEM_ID, mediaItemId);
    return this;
  }

  /**
   * Sets the value of "sortyBy" parameter.
   *
   * @param sortBy value of sortyBy parameter
   * @return this to facilitate chaining
   */
  OrkutRequest setSortBy(String sortBy) {
    addParameter(Params.SORT_BY, sortBy);
    return this;
  }

  /**
   * Builds the json request string.
   *
   * <p>
   * Clients should call this function to get the request string if they intend
   * to send a single rpc. In case you want a batch request use
   * {@link BatchTransaction} to build a batched request.
   * </p>
   */
  public String toString() {
    return getJSONObject().toString();
  }

  /**
   * Returns id of the request.
   *
   * @return id of the request.
   */
  String getId() {
    return id;
  }

  /**
   * Returns the method of the request.
   *
   * @return method of the json request.
   */
  String getMethod() {
    return method;
  }

  JSONObject getParameters() {
    return params;
  }

  JSONArray getFields() {
    return fields;
  }

  JSONObject getJSONObject() {
    try {
      JSONObject obj = new JSONObject();
      obj.put(Params.METHOD, method);
      obj.put(Fields.ID, id);
      if (fields.length() > 0) {
        params.put(Params.FIELDS, fields);
      }
      if (params.length() > 0) {
        obj.put(Params.PARAMS, params);
      }
      return obj;
    } catch (JSONException jse) {
      throw new RuntimeException(Util.getRuntimeErrorMessage("OrkutRequest.toJSONObject"),
          jse);
    }
  }

  int getStartIndex() {
    return params.optInt(Params.START_INDEX);
  }

  String getUserId() {
    return params.optString(Params.USER_ID, Constants.USERID_ME);
  }

  int getCount() {
    return params.optInt(Params.COUNT);
  }
}

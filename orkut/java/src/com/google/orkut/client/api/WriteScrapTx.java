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

import com.google.orkut.client.api.InternalConstants.Values;

import org.json.me.JSONObject;

/**
 * A {@link Transaction} to write a scrap on orkut.
 *
 * @author Sachin Shenoy
 */
public class WriteScrapTx extends Transaction {

  private final JSONObject message = new JSONObject();

  WriteScrapTx(String personId, String body) {
    super(RequestIds.SCRAPS_WRITE, MethodNames.MESSAGES_CREATE);
    request.setUserId(personId)
           .setGroupId(Group.SELF)
           .addParameter(Params.MESSAGE_TYPE, Values.PUBLIC_MESSAGE)
           .addParameter(Params.MESSAGE, message);
    setBody(body);
  }

  WriteScrapTx(String personId, String body, String scrapId) {
    this(personId, body);
    request.addParameter(Params.MSG_ID, scrapId);
  }

  WriteScrapTx setBody(String body) {
    Util.putJsonValue(message, Fields.BODY, body);
    return this;
  }
}

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
 * A {@link Transaction} that posts an activity on orkut.
 *
 * @author Sachin Shenoy
 */
public class PostActivityTx extends Transaction {
  private final JSONObject activity = new JSONObject();

  PostActivityTx(String title, String body) {
    super(RequestIds.ACTIVITIES_CREATE, MethodNames.CREATE_ACTIVITIES);
    // TODO(birmiwal) change this to USERID_ME when support for this
    // is pushed in backend
    request.setUserId("@viewer")
           .setGroupId(Group.SELF)
           .addParameter(Params.ACTIVITY, activity)
           .addParameter(Fields.APP_ID, InternalConstants.Values.APP);
    Util.putJsonValue(activity, Fields.TITLE, title);
    Util.putJsonValue(activity, Fields.BODY, body);
  }
}

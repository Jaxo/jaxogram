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
 * An {@link ActivityEntry} for a status message change on orkut.
 *
 * @author Sachin Shenoy
 */
public class StatusMessageActivity extends ActivityEntry {

  StatusMessageActivity(JSONObject json) {
    super(json);
  }

  public String getType() {
    return ActivityEntry.ActivityType.STATUS_MSG;
  }

  public String getBody() {
    return json.optString(Fields.BODY);
  }
}

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
 * Represents an {@link ActivityEntry} for an app related activity.
 *
 * @author Sachin Shenoy
 */
public class MakamakaActivity extends ActivityEntry {

  MakamakaActivity(JSONObject json) {
    super(json);
  }

  public String getType() {
    return ActivityEntry.ActivityType.MAKAMAKA;
  }

  public String getTitle() {
    return json.optString(Fields.TITLE);
  }

  public String getBody() {
    return json.optString(Fields.BODY);
  }
}

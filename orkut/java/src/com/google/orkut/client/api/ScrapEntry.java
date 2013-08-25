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
 * A scrap on orkut.
 *
 * @author Sachin Shenoy
 */
public class ScrapEntry {
  private final JSONObject json;

  ScrapEntry(JSONObject json) {
    this.json = json;
  }

  public String getId() {
    return json.optString(Fields.ID);
  }

  public String getFromUserId() {
    return json.optString(Fields.FROM_USER_ID);
  }

  public String getBody() {
    return json.optString(Fields.BODY);
  }

  public boolean hasTime() {
    return json.has(Fields.TIME);
  }

  public long getTime() {
    return json.optLong(Fields.TIME);
  }

  public boolean hasFromUserProfile() {
    return json.has(Fields.FROM_USER_PROFILE);
  }

  public OrkutPerson getFromUserProfile() {
    return new OrkutPerson(json.optJSONObject(Fields.FROM_USER_PROFILE));
  }

  public String toString() {
    return getBody();
  }
}

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
 * Represents a comments entry.
 *
 * @author Sachin Shenoy
 */
public class CommentEntry {
  private static final String FROM_USER_PROFILE = "fromUserProfile";
  private static final String PARENT_ID = "parentId";
  private static final String TEXT = "text";
  private static final String CREATED_TIME = "created";
  private static final String AUTHOR_ID = "authorId";
  private static final String ID = "id";

  private final JSONObject json;

  CommentEntry(JSONObject json) {
    this.json = json;
  }

  /** Returns a unique ID for the comment */
  public String getId() {
    return json.optString(ID);
  }

  /** Returns the user id of the author */
  public String getAuthorId() {
    return json.optString(AUTHOR_ID);
  }

  /** Returns the time when the comment was posted */
  public int getCreatedTime() {
    return json.optInt(CREATED_TIME);
  }

  /** Returns the comment text */
  public String getText() {
    return json.optString(TEXT);
  }

  /** Returns the id of the parent object */
  public String parentId() {
    return json.optString(PARENT_ID);
  }

  /** Returns true if the author profile is available */
  public boolean hasAuthorProfile() {
    return json.has(FROM_USER_PROFILE);
  }

  /** Returns the author profile if present, null otherwise */
  public OrkutPerson getAuthorProfile() {
    if (!hasAuthorProfile()) {
      return null;
    }
    return new OrkutPerson(json.optJSONObject(FROM_USER_PROFILE));
  }
}

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
 * Represents a comment.
 *
 * @author Sachin Shenoy
 */
public class Comment {

  private final JSONObject json;

  Comment(JSONObject json) {
    if (json == null) {
      throw new CreationException("JSON cannot be null!");
    }
    this.json = json;
  }

  /** Returns the text of the comment */
  public String getText() {
    return json.optString(Fields.TEXT);
  }

  /** Returns the created time of the comment */
  public Long getCreatedTime() {
    return json.optLong(Fields.CREATED);
  }

  /** Returns the user id of the author of the comment */
  public String getAuthorId() {
    return json.optString(Fields.AUTHOR_ID);
  }
}

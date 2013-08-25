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

import com.google.orkut.client.api.Constants.MediaItemType;

import org.json.me.JSONObject;

/**
 * A media item in an Activity related to a photo or a video.
 * TODO(birmiwal): replace this with {@link Photo}.
 *
 * @author Sachin Shenoy
 * @author Shishir Birmiwal
 */
public class MediaItem {

  private final JSONObject json;

  MediaItem(JSONObject json) {
    if (json == null
        || json.optString(Fields.TYPE) == null
        || json.optString(Fields.ID) == null) {
      throw new CreationException("JSON/ID/TYPE cannot be null!");
    }
    this.json = json;
  }

  public String getId() {
    return json.optString(Fields.ID);
  }

  public String getPageUrl() {
    return json.optString(Fields.PAGE_URL);
  }

  public String getUrl() {
    return json.optString(Fields.URL);
  }

  public String getType() {
    return json.optString(Fields.TYPE);
  }

  /**
   * Returns the you-tube url for a shared video.
   * Note: this only makes sense when {@link #getType()} is {@link MediaItemType#VIDEO}
   */
  public String getYoutubeUrl() {
    return json.optString(Fields.YOUTUBE_URL);
  }
}

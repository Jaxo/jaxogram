/* EXPERIMENTAL (really) */
/* Copyright (c) 2010 Google Inc.
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
 * A video in orkut.
 *
 * @author Prashant Tiwari
 */
public class Video {
  private final JSONObject json;

  Video(JSONObject json) {
    this.json = json;
  }

  /**
   * @return the ID of the video
   */
  public String getId() {
    return json.optString(Fields.ID);
  }
  
  /**
   * @return the length of the video in seconds
   */
  public long getDuration() {
    return json.optLong(Fields.VIDEO_DURATION);
  }

  /**
   * @return the video title
   */
  public String getTitle() {
    return json.optString(Fields.TITLE);
  }
  
  /**
   * @return the video description
   */
  public String getDescription() {
    return json.optString(Fields.DESCRIPTION);
  }
  
  /**
   * @return the url to the thumbnail of the video
   */
  public String getThumbnailUrl() {
    return json.optString(Fields.THUMBNAIL_URL);
  }
  
  /**
   * @return the url of the source video
   */
  public String getURL() {
    return json.optString(Fields.URL);
  }

  @Override
  public String toString() {
    StringBuilder videoDesc = new StringBuilder();
    videoDesc.append("title: " + getTitle());
    videoDesc.append("\nthumbnail: " + getThumbnailUrl());
    videoDesc.append("\nduration: " + getDuration() + " seconds");
    videoDesc.append("\ndescription: " + getDescription());
    return videoDesc.toString();
  }
}

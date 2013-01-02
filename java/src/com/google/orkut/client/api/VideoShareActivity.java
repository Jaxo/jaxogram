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

import java.util.Vector;

/**
 * An {@link ActivityEntry} which represents a video being shared on orkut.
 *
 * @author Sachin Shenoy
 */
public class VideoShareActivity extends ActivityEntry {

  private Vector mediaItems;

  VideoShareActivity(JSONObject json) {
    super(json);
    parse(json);
  }

  private void parse(JSONObject json) {
    mediaItems = Util.forEachItemInList(json, Fields.MEDIA_ITEMS, new Converter() {
          Object convert(JSONObject json) {
            return new MediaItem(json);
          }
        });
  }

  public String getType() {
    return ActivityEntry.ActivityType.VIDEO;
  }

  /**
   * Returns the number of videos in this video-share-activity.
   */
  public int getMediaItemCount() {
    return mediaItems.size();
  }

  /**
   * Returns the video at the given index in the video share activity.
   *
   * @param index video index - valid values from {@code 0} to {@link #getMediaItemCount()}{@code -1}
   */
  public MediaItem getMediaItem(int index) {
    return (MediaItem) mediaItems.get(index);
  }
}
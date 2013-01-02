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
 * Represents PhotoShare activity.
 *
 * <p>
 * This activity is created when someone uploads photo into orkut. We provide a
 * coalesced (merged) view of the activity, where if a user uploads multiple
 * photos, rather than providing multiple activity stream entry, we provide a
 * single entry with details of all photos. This detail is provided through
 * {@link MediaItem}.
 * </p>
 *
 * @author Sachin Shenoy
 */
public class PhotoShareActivity extends ActivityEntry {

  private Vector mediaItems;

  PhotoShareActivity(JSONObject json) {
    super(json);
    parse(json);
  }

  private void parse(JSONObject json) {
    mediaItems = Util.forEachItemInList(json, Fields.MEDIA_ITEMS,
        new Converter() {
          Object convert(JSONObject json) {
            return new MediaItem(json);
          }
        });
  }

  public String getType() {
    return ActivityEntry.ActivityType.PHOTO;
  }

  /** Returns number of media items present in the activity */
  public int getMediaItemCount() {
    return mediaItems.size();
  }

  /** Returns a requested media item from the activity */
  public MediaItem getMediaItem(int index) {
    return (MediaItem) mediaItems.get(index);
  }
}

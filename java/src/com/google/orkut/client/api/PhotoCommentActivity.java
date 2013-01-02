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
 * An {@link ActivityEntry} generated when a photo is commented on.
 *
 * @author Sachin Shenoy
 */
public class PhotoCommentActivity extends ActivityEntry {

  private Vector mediaItems;
  private Vector comments;

  PhotoCommentActivity(JSONObject json) {
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
    comments = Util.forEachItemInList(json, Fields.COMMENTS,
        new Converter() {
          Object convert(JSONObject json) {
            return new Comment(json);
          }
        });
  }

  public String getType() {
    return ActivityEntry.ActivityType.PHOTO_COMMENT;
  }

  /** Returns the id of the album on which the comment is written */
  public String getAlbumId() {
    JSONObject templateParams = json.optJSONObject(Fields.TEMPLATE_PARAMS);
    if (templateParams == null) {
      return "";
    }
    return templateParams.optString(Fields.ALBUM_ID);
  }

  /** Returns the title of the album on which the comment is written */
  public String getAlbumTitle() {
    JSONObject templateParams = json.optJSONObject(Fields.TEMPLATE_PARAMS);
    if (templateParams == null) {
      return "";
    }
    return templateParams.optString(Fields.ALBUM_TITLE);
  }

  /** Returns the profile of the owner of the photo */
  public OrkutPerson getPhotoOwnerProfile() {
    return getOwnerProfile();
  }

  /** Returns a requested photo on which the comments were written */
  public MediaItem getMediaItem() {
    if (mediaItems.size() == 0) {
      return null;
    } else {
      return (MediaItem) mediaItems.get(0);
    }
  }

  /** Returns number of comments on the photo */
  public int getCommentsCount() {
    return mediaItems.size();
  }

  /** Returns a comment given index */
  public Comment getComment(int index) {
    return (Comment) comments.get(index);
  }
}

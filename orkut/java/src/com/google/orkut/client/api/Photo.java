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
 * A photo object entity. Represents a photo on orkut.
 *
 * @author Shishir Birmiwal
 */
public class Photo {
  private final JSONObject json;

  public Photo(JSONObject json) {
    if (json == null) {
      throw new RuntimeException("cannot create a photo out of a null json");
    }
    this.json = json;
  }

  Photo(String ownerId, String albumId, String photoId) {
    json = new JSONObject();
    setOwnerId(ownerId);
    setAlbumId(albumId);
    setId(photoId);
  }

  public String getId() {
    return json.optString(Fields.ID);
  }

  public String getTitle() {
    return json.optString(Fields.TITLE);
  }

  public String getThumbnailUrl() {
    return json.optString(Fields.THUMBNAIL_URL);
  }

  public String getAlbumId() {
    return json.optString(Fields.ALBUM_ID);
  }

  public String getOwnerId() {
    return json.optString(Params.OWNER_ID);
  }

  public String getUrl() {
    return json.optString(Fields.URL);
  }

  public void setTitle(String title) {
    Util.putJsonValue(json, Fields.TITLE, title);
  }

  JSONObject getJson() {
    return json;
  }

  void setOwnerId(String ownerId) {
    Util.putJsonValue(json, Params.OWNER_ID, ownerId);
  }

  void setAlbumId(String albumId) {
    Util.putJsonValue(json, Params.ALBUM_ID, albumId);
  }

  void setId(String id) {
    Util.putJsonValue(json, Fields.ID, id);
  }

  void setThumbnailUrl(String thumbnailUrl) {
    Util.putJsonValue(json, Fields.THUMBNAIL_URL, thumbnailUrl);
  }

  void setUrl(String url) {
    Util.putJsonValue(json, Fields.URL, url);
  }
}

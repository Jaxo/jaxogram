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
 * A {@link Transaction} to get one or more photos from orkut.
 *
 * @author Shishir Birmiwal
 */
public class GetPhotosTx extends Transaction {
  private static final int DEFAULT_NUM_PHOTOS = 20;

  private Vector photos;
  private int totalResults;

  /**
   * Creates a request to fetch photos for a user. The request can either fetch a
   * photo for a given {@code photoId} or photos in an album.
   *
   * @param userId the id of the owner of the album from which to fetch photo(s)
   * @param albumId the id of the album from which to fetch photo(s)
   * @param photoId the id of the photo. if this is null, photos are fetched from the album,
   *     as specified by {@code startIndex} and {@code maxCount}
   */
  GetPhotosTx(String userId, String albumId, String photoId) {
    super(RequestIds.MEDIAITEMS_GET, MethodNames.MEDIAITEMS_GET);
    request.setUserId(userId);
    request.setGroupId(Group.SELF);
    request.addParameter(Params.ALBUM_ID, albumId);
    if (photoId == null) {
      request.setCount(DEFAULT_NUM_PHOTOS);
    } else {
      request.addParameter(Params.MEDIA_ITEM_ID, photoId);
    }
  }

  GetPhotosTx(String userId, String albumId) {
    this(userId, albumId, null);
  }

  /**
   * Get next set of photos given a previous {@link GetPhotosTx}.
   *
   * @param prev the previous get photos transaction
   */
  GetPhotosTx(GetPhotosTx prev) {
    this(prev.getUserId(), prev.getAlbumId());
    request.setStartIndex(prev.getStartIndex() + prev.getItemsCount());
    setCount(prev.getRequestCount());
  }

  /**
   * Sets the maximum number of photos to fetch.
   */
  public GetPhotosTx setCount(int count) {
    request.setCount(count);
    return this;
  }

  /**
   * Returns the number of {@link Photo}s fetched in this request.
   */
  public int getPhotoCount() {
    return photos.size();
  }

  /**
   * Returns the {@link Photo} at {@code index}. Valid values for {@code index} are
   * from {@code 0} to {@link #getPhotoCount()}{code -1}. Use this to get the {@link Photo}s
   * returned from the transaction generated using {@link PhotosTxFactory#getPhotos(Album)} or
   * {@link PhotosTxFactory#getPhotos(String, String)}.
   */
  public Photo getPhoto(int index) {
    return (Photo) photos.get(index);
  }

  /**
   * The {@link Photo} for the given photo-id in query.
   *
   * @return an orkut {@link Photo}
   */
  public Photo getPhoto() {
    if (photos.size() != 1) {
      throw new RuntimeException(
          "your request returned multiple/no photos."
          + "should you be using getAllPhotos() instead?");
    }

    return (Photo) photos.get(0);
  }

  /**
   * Returns <code>true</code> if more {@link Photo}s can be fetched using
   * {@link PhotosTxFactory#getNextPhotos(GetPhotosTx)}.
   */
  public boolean canGetMorePhotos() {
    return totalResults - getStartIndex() - getItemsCount() > 0;
  }

  protected void setResponseData(JSONObject data) {
    totalResults = data.optInt(Fields.TOTAL_RESULTS);
    photos = Util.forEachItemInList(data, ResponseFields.LIST_KEY, new Converter() {
          Object convert(JSONObject json) {
            Photo photo = new Photo(json);
            photo.setOwnerId(request.getParameters().optString(Params.USER_ID));
            return photo;
          }
        });
  }

  private int getItemsCount() {
    return getPhotoCount();
  }

  private int getStartIndex() {
    return request.getStartIndex();
  }

  private String getAlbumId() {
    return request.getParameters().optString(Params.ALBUM_ID);
  }

  private String getUserId() {
    return request.getUserId();
  }

  private int getRequestCount() {
    return request.getCount();
  }
}

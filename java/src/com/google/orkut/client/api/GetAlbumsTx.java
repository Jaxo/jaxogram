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
 * A {@link Transaction} to get one (or multiple) albums from orkut.
 *
 * <p>Note: The albums returned for a given user may vary with
 * the permission-settings.
 *
 * @author Shishir Birmiwal
 */
public class GetAlbumsTx extends Transaction {
  private static final int DEFAULT_NUM_ALBUMS = 20;

  Vector albums;
  private int totalResults;

  GetAlbumsTx(String userId, String albumId) {
    super(RequestIds.ALBUMS_GET, MethodNames.ALBUMS_GET);
    request.setUserId(userId).setGroupId(Group.SELF);
    if (albumId == null) {
      request.setCount(DEFAULT_NUM_ALBUMS);
    } else {
      request.addParameter(Params.ALBUM_ID, albumId);
    }
  }

  GetAlbumsTx(GetAlbumsTx prev) {
    this(prev.getUserId(), null);
    request.setStartIndex(prev.getStartIndex() + prev.getAlbumCount());
    request.setCount(prev.getRequestCount());
  }

  /**
   * Sets the number of {@link Album}s to fetch.
   */
  public GetAlbumsTx setCount(int count) {
    request.setCount(count);
    return this;
  }

  /**
   * Returns the number of {@link Album}s fetched in this request.
   */
  public int getAlbumCount() {
    return albums.size();
  }

  /**
   * Returns the {@link Album} at {@code index}. Valid values for {@code index} are
   * from {@code 0} to {@link #getAlbumCount()}{code -1}. Use this to get the {@link Album}s
   * returned from the transaction generated using {@link AlbumsTxFactory#getAlbums(String)}.
   */
  public Album getAlbum(int index) {
    return (Album) albums.get(index);
  }

  /**
   * Get the album requested using {@link AlbumsTxFactory#getAlbum(String, String)}.
   *
   * @return the album returned for the request
   */
  public Album getAlbum() {
    if (albums.size() != 1) {
      throw new RuntimeException(
          "your request returned multiple/no albums."
          + "should you be using getAllAlbums() instead?");
    }

    return (Album) albums.get(0);
  }

  /**
   * Returns <code>true</code> if there are more albums which can be fetched using
   * {@link AlbumsTxFactory#getNextAlbums(GetAlbumsTx)}.
   */
  public boolean canGetMoreAlbums() {
    return totalResults - getStartIndex() - getAlbumCount() > 0;
  }

  protected void setResponseData(JSONObject data) {
    totalResults = data.optInt(Fields.TOTAL_RESULTS);
    albums = Util.forEachItemInList(data, ResponseFields.LIST_KEY, new Converter() {
          Object convert(JSONObject json) {
            return new Album(json);
          }
        });
  }

  String getUserId() {
    return request.getUserId();
  }

  int getStartIndex() {
    return request.getStartIndex();
  }

  private int getRequestCount() {
    return request.getCount();
  }
}

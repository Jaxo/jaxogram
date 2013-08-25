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

/**
 * A factory to generate Albums related {@link Transaction} objects.
 *
 * @author Shishir Birmiwal
 */
public class AlbumsTxFactory {
  public AlbumsTxFactory() {}

  /**
   * Gets albums of a given user.
   *
   * @param userId the user id for whom to get albums.
   *     can be {@link Constants#USERID_ME} to fetch logged in
   *     user's albums
   * @return an {@link GetAlbumsTx} instance for this request
   */
  public GetAlbumsTx getAlbumsOf(String userId) {
    return new GetAlbumsTx(userId, null);
  }

  /** Alias to getAlbumsOf, for backward compatibility. */
  public GetAlbumsTx getAlbums(String userId) {
    return getAlbumsOf(userId);
  }

  /** Gets the albums for the user who is logged in. */
  public GetAlbumsTx getSelfAlbums() {
    return new GetAlbumsTx(Constants.USERID_ME,null);
  }

  /** Gets an album given the album id. The album must
      belong to the user who is currently logged in. */
  public GetAlbumsTx getSelfAlbum(String albumId) {
     return getAlbum(Constants.USERID_ME,albumId);
  }

  /**
   * Fetch an album given a user and an album id.
   *
   * @param userId the user id for whom to get an album
   * @param albumId the id of the album to fetch
   * @return an {@link GetAlbumsTx} instance for this request
   */
  public GetAlbumsTx getAlbum(String userId, String albumId) {
    return new GetAlbumsTx(userId, albumId);
  }

  /**
   * Given an {@link GetAlbumsTx}, get the next up-to maxCount
   * number of albums from the list of albums.
   *
   * @param prev the previous get albums transaction which fetched albums
   * @return an {@link GetAlbumsTx} instance for this request
   */
  public GetAlbumsTx getNextAlbums(GetAlbumsTx prev) {
    return new GetAlbumsTx(prev);
  }

  /**
   * Update an album's title, description and ACLs.
   *
   * @param album
   * @return an {@link UpdateAlbumTx} instance for this request
   */
  public UpdateAlbumTx updateAlbum(Album album) {
    return new UpdateAlbumTx(album);
  }

  /**
   * Create a new album.
   *
   * @param title the title of the album
   * @param description the description of the album
   * @return a {@link CreateAlbumTx} instance for this request
   */
  public CreateAlbumTx createAlbum(String title, String description) {
    return new CreateAlbumTx(title, description);
  }

  /**
   * Delete an album.
   *
   * @param albumId the id of the album to delete
   * @return a {@link DeleteAlbumTx} instance for this request
   */
  public DeleteAlbumTx deleteAlbum(String albumId) {
    return new DeleteAlbumTx(albumId);
  }

  /**
   * Shares an album with all your orkut friends.
   *
   * @param album the album to share
   * @return a {@link Transaction} instance for this request
   */
  public Transaction shareAlbumWithFriends(Album album) {
    return new UpdateAlbumShareTx(album, UpdateAlbumShareTx.FRIENDS);
  }
}

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

import com.google.orkut.client.api.Album.AclEntry;
import com.google.orkut.client.api.Album.AlbumAccessType;
import com.google.orkut.client.api.InternalConstants.Values;

/**
 * A {@link Transaction} to share an album with friends.
 *
 * @author Shishir Birmiwal
 */
public class UpdateAlbumShareTx extends Transaction {
  static final String EVERYONE_ON_ORKUT = "@orkut";
  static final String FRIENDS = "@friends";

  /**
   * Constructs a request to share an album.
   *
   * @param album the album to update
   * @param shareWith the group to share with
   */
  UpdateAlbumShareTx(Album album, String shareWith) {
    super(RequestIds.ALBUM_SHARE, MethodNames.ACL_CREATE);
    request.setUserId(Constants.USERID_ME);
    request.setGroupId(Group.SELF);
    request.addParameter(Params.TYPE, Values.ALBUMS);
    request.addParameter(Params.ALBUM_ID, album.getId());
    request.addParameter(Params.ACLENTRY,
        (new AclEntry("metaGroup", "", AlbumAccessType.READ, shareWith)).getJson());
  }
}

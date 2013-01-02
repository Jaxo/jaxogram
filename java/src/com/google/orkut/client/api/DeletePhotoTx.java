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
 * A {@link Transaction} to delete a photo on orkut.
 *
 * @author Shishir Birmiwal
 */
public class DeletePhotoTx extends Transaction {

  DeletePhotoTx(Photo photo) {
    super(RequestIds.MEDIAITEMS_DELETE, MethodNames.MEDIAITEMS_DELETE);
    request.setUserId(photo.getOwnerId());
    request.setGroupId(Group.SELF);
    request.addParameter(Params.ALBUM_ID, photo.getAlbumId());
    request.addParameter(Params.MEDIA_ITEM_ID, photo.getId());
  }

  protected void setResponseData(JSONObject data) {
    // do nothing -- the checks for error are all we need
    // delete does not return any data to parse
  }
}

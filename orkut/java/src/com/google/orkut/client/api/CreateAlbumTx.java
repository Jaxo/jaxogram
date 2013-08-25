/* EXPERIMENTAL (really) */
/*
 * Copyright (c) 2009 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.google.orkut.client.api;

import org.json.me.JSONException;
import org.json.me.JSONObject;

/**
 * A {@link Transaction} to create an album on orkut.
 * 
 * @author Shishir Birmiwal
 */
public class CreateAlbumTx extends Transaction {

  private Album album;

  CreateAlbumTx(String title, String description) {
    super(RequestIds.ALBUMS_CREATE, MethodNames.ALBUMS_CREATE);
    request.setUserId(Constants.USERID_ME);
    request.setGroupId(Group.SELF);
    JSONObject album = new JSONObject();
    try {
      album.put(Fields.TITLE, title);
      album.put(Fields.DESCRIPTION, description);
    } catch (JSONException e) {
      // wtf!?
      throw new RuntimeException("could not add fields to a json object");
    }
    request.addParameter(Params.ALBUM, album);
  }

  protected void setResponseData(JSONObject data) {
    album = new Album(data);
    album.setOwnerId(Constants.USERID_ME);
    try {
      JSONObject albumDetails = request.getParameters().getJSONObject(Params.ALBUM);
      album.setTitle(albumDetails.getString(Fields.TITLE));
      album.setDescription(albumDetails.getString(Fields.DESCRIPTION));
    } catch (Exception e) {
      // unexpected!
    }
  }

  /**
   * @return the album that was created by this transaction
   */
  public Album getAlbum() {
    return album;
  }
}

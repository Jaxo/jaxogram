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

import java.io.IOException;

/**
 * A {@link Transaction} to upload a photo to an orkut album.
 *
 * @author Sachin Shenoy
 */
public class UploadPhotoTx extends Transaction {

  private final JSONObject mediaItem;
  private final String paramName;
  private final byte[] image;
  private final String type;

  public static class ImageType {
    public static final String PNG = "png";
    public static final String JPG = "jpg";
    public static final String GIF = "gif";
  }

  UploadPhotoTx(String albumId, byte[] image, String type, String title) {
    super(MethodNames.MEDIAITEMS_CREATE);
    this.image = image;
    this.type = type;

    paramName = "image" + request.getId();
    mediaItem = new JSONObject();
    Util.putJsonValue(mediaItem, "title", title);
    Util.putJsonValue(mediaItem, "url", "@field:" + paramName);
    request.setUserId(Constants.USERID_ME)
           .setGroupId(Group.SELF)
           .setAlbumId(albumId)
           .addParameter("mediaItem", mediaItem);
  }

  public void addBody(MultipartBuilder builder) throws IOException {
    builder.addFile(paramName, "uploaded", "image/" + type, image);
  }
}

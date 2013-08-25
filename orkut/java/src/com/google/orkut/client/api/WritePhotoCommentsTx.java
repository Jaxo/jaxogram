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

import com.google.orkut.client.api.InternalConstants.Values;

import org.json.me.JSONObject;

/**
 * A {@link Transaction} to write a comment for a photo on orkut.
 *
 * @author Sachin Shenoy
 */
public class WritePhotoCommentsTx extends Transaction {
  private CommentEntry commentEntry;

  WritePhotoCommentsTx(String userId, String albumId, String photoId, String text) {
    super(RequestIds.COMMENTS_CREATE, MethodNames.COMMENTS_CREATE);
    JSONObject comment = new JSONObject();
    Util.putJsonValue(comment, Fields.TEXT, text);
    request.setUserId(userId)
           .setGroupId(Group.SELF)
           .setAlbumId(albumId)
           .setMediaItemId(photoId)
           .addParameter(Params.TYPE, Values.MEDIAITEMS)
           .addParameter(Params.COMMENT, comment);
  }

  protected void setResponseData(JSONObject data) {
    commentEntry = new CommentEntry(data);
  }

  /**
   * Returns the comment written.
   */
  public CommentEntry getCommentEntry() {
    return commentEntry;
  }
}

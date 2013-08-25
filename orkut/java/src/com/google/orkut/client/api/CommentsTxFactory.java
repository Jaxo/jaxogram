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

/**
 * Factory for creating transactions related to Comments.
 *
 * @author Sachin Shenoy
 */
public class CommentsTxFactory {

  public GetPhotoCommentsTx getPhotoComments(String userId, String albumId, String photoId) {
    return new GetPhotoCommentsTx(userId, albumId, photoId);
  }

  public GetPhotoCommentsTx getNextPhotoComments(GetPhotoCommentsTx prev) {
    return prev.getNext();
  }

  public GetPhotoCommentsTx getPrevPhotoComments(GetPhotoCommentsTx last) {
    return last.getPrev();
  }

  public WritePhotoCommentsTx writePhotoComments(String userId, String albumId, String photoId,
      String text) {
    return new WritePhotoCommentsTx(userId, albumId, photoId, text);
  }

  public Transaction deletePhotoComments(String userId, String albumId, String photoId,
      String commentId) {
    Transaction deleteCommentTx = new Transaction(RequestIds.COMMENTS_DELETE, MethodNames.COMMENTS_DELETE);
    deleteCommentTx.request
        .setUserId(userId)
        .setGroupId(Group.SELF)
        .setAlbumId(albumId)
        .setMediaItemId(photoId)
        .addParameter(Params.COMMENT_ID, commentId)
        .addParameter(Params.TYPE, Values.MEDIAITEMS);
    return deleteCommentTx;
  }
}

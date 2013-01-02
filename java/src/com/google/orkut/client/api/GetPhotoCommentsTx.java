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

import java.util.Vector;

/**
 * Factory for creating transactions for accessing comments.
 *
 * @author Sachin Shenoy
 */
public class GetPhotoCommentsTx extends Transaction {

  private static final int DEFAULT_NUM_COMMENTS = 10;
  private Vector comments;
  private int totalResults;

  GetPhotoCommentsTx(String userId, String albumId, String photoId) {
    super(RequestIds.COMMENTS_GET, MethodNames.COMMENTS_GET);
    request.setUserId(userId)
           .setGroupId(Group.SELF)
           .setCount(DEFAULT_NUM_COMMENTS)
           .setStartIndex(0)
           .addParameter(Params.TYPE, Values.MEDIAITEMS)
           .addParameter(Params.ALBUM_ID, albumId)
           .addParameter(Params.MEDIA_ITEM_ID, photoId);
  }

  public GetPhotoCommentsTx setCount(int count) {
    request.setCount(count);
    return this;
  }

  protected void setResponseData(JSONObject data) {
    totalResults = data.optInt(Fields.TOTAL_RESULTS);
    comments = Util.forEachItemInList(data, ResponseFields.LIST_KEY, new Converter() {
          Object convert(JSONObject json) {
            return new CommentEntry(json);
          }
        });
  }

  public int getCommentsCount() {
    return comments.size();
  }

  public CommentEntry getComment(int index) {
    return (CommentEntry) comments.get(index);
  }

  public boolean hasNext() {
    return request.getStartIndex() + request.getCount() < totalResults;
  }

  GetPhotoCommentsTx getNext() {
    GetPhotoCommentsTx fetchTx = new GetPhotoCommentsTx(request.getUserId(),
        getAlbumId(), getPhotoId());
    fetchTx.request
        .setStartIndex(getNextStartIndex())
        .setCount(request.getCount());
    return fetchTx;
  }

  public boolean hasPrev() {
    return request.getStartIndex() > 0;
  }

  GetPhotoCommentsTx getPrev() {
    GetPhotoCommentsTx fetchTx = new GetPhotoCommentsTx(request.getUserId(),
        getAlbumId(), getPhotoId());
    fetchTx.request
        .setStartIndex(getPrevStartIndex())
        .setCount(request.getCount());
    return fetchTx;
  }

  private int getNextStartIndex() {
    return request.getStartIndex() + request.getCount();
  }

  private int getPrevStartIndex() {
    return Math.max(0, request.getStartIndex() - request.getCount());
  }

  private String getAlbumId() {
    return request.getParameters().optString(Params.ALBUM_ID);
  }

  private String getPhotoId() {
    return request.getParameters().optString(Params.MEDIA_ITEM_ID);
  }
}

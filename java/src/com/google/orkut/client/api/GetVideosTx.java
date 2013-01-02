/* EXPERIMENTAL (really) */
/* Copyright (c) 2010 Google Inc.
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
 * A {@link Transaction} to get one (or multiple) videos from orkut.
 *
 * @author Prashant Tiwari
 */
public class GetVideosTx extends Transaction {
  private static final int DEFAULT_NUM_VIDEOS = 10;
  private int numVideos;
  private int currStartIndex;

  Vector videos;

  GetVideosTx() {
    this(Constants.USERID_ME);
  }
  
  /**
   * @param userId the userId for whom to get the videos
   */
  GetVideosTx(String userId) {
    this(userId, DEFAULT_NUM_VIDEOS);
  }

  /**
   * @param userId the userId for whom to get the videos
   * @param count the number of videos to get in a single transaction
   */
  GetVideosTx(String userId, int count) {
    super(RequestIds.VIDEOS_GET, MethodNames.VIDEOS_GET);
    request.setUserId(userId)
      .setGroupId(Group.SELF)
      .setCount(count)
      .addParameter(Fields.PAGE_TYPE, Params.PageType.FIRST);
  }
  
  GetVideosTx getNext() {
    GetVideosTx getVideosTx = new GetVideosTx(request.getUserId());
    getVideosTx.request.addParameter(Fields.PAGE_TYPE, Params.PageType.NEXT)
      .addParameter(Params.COUNT, request.getCount())
      .addParameter(Params.LAST_KEY, getLastMessageKey());
    currStartIndex = request.getStartIndex() + videos.size();
    return getVideosTx;
  }
  
  GetVideosTx getPrev() {
    GetVideosTx getVideosTx = new GetVideosTx(request.getUserId());
    getVideosTx.request.addParameter(Fields.PAGE_TYPE, Params.PageType.PREV)
      .addParameter(Params.COUNT, request.getCount())
      .addParameter(Params.LAST_KEY, getFirstMessageKey());
    return getVideosTx;
  }

  /**
   * Returns the number of videos fetched in this request.
   */
  public int getVideoCount() {
    return videos.size();
  }

  @Override
  protected void setResponseData(JSONObject data) {
    numVideos = data.optInt(Fields.TOTAL_RESULTS);
    videos = Util.forEachItemInList(data, ResponseFields.LIST_KEY, new Converter() {
      @Override    
      Object convert(JSONObject json) {
        return new Video(json);
      }
    });
  }
  
  public boolean hasNext() {
    return (numVideos - currStartIndex) > 0;
  }
  
  public GetVideosTx setCount(int count) {
    request.addParameter(Params.COUNT, count);
    return this;
  }
  
  /**
   * @param index the index to the video to get
   * @return the video from the current GetVideosTx transaction on the specified index in the list
   */
  public Video getVideo(int index) {
    return (Video) videos.get(index);
  }

  private String getFirstMessageKey() {
    if (videos.isEmpty()) {
      return "";
    }
    Video video = (Video) videos.firstElement();
    return video.getId();
  }
  
  private String getLastMessageKey() {
    if (videos.isEmpty()) {
      return "";
    }
    Video video = (Video) videos.lastElement();
    return video.getId();
  }
}

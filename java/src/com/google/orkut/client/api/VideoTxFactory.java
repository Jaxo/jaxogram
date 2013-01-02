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

/**
 * A factory to generate {@link Video} related {@link Transaction} objects.
 *
 * @author Prashant Tiwari
 */
public class VideoTxFactory {
  public VideoTxFactory() {}
  
  /**
   * Gets up to maxCount number of videos of the logged-in user (as defined in GetVideosTx.DEFAULT_NUM_VIDEOS).
   *
   * @return a {@link GetVideosTx} instance for this request
   */
  public GetVideosTx getVideos() {
    return new GetVideosTx();
  }
  
  /**
   * Gets up to maxCount number of videos of a given user (as defined in GetVideosTx.DEFAULT_NUM_VIDEOS).
   *
   * @param userId the user id for whom to get videos.
   *     can be {@link Constants#USERID_ME} to fetch logged-in
   *     user's videos
   * @return a {@link GetVideosTx} instance for this request
   */
  public GetVideosTx getVideos(String userId) {
    return new GetVideosTx(userId);
  }

  /**
   * Gets up to count number of videos of a given user
   * 
   * @param userId the user id for whom to get videos.
   *     can be {@link Constants#USERID_ME} to fetch logged-in
   *     user's videos
   * @param count the number of videos to get
   * @return a {@link GetVideosTx} instance for this request
   */
  public GetVideosTx getVideos(String userId, int count) {
    return new GetVideosTx(userId, count);
  }

  /**
   * Given a {@link GetVideosTx}, get the next videos
   * from the list of videos (up to a max of count in number).
   *
   * @param prev the previous get videos transaction which fetched videos
   * @return a {@link GetVideosTx} instance for this request
   */
  public GetVideosTx getNext(GetVideosTx prev) {
    return prev.getNext();
  }
  
  /**
   * Given a {@link GetVideosTx}, get the previous videos
   * from the list of videos (up to a max of count in number).
   *
   * @param last the last get videos transaction which fetched videos
   * @return a {@link GetVideosTx} instance for this request
   */
  public GetVideosTx getPrev(GetVideosTx last) {
    return last.getPrev();
  }
}

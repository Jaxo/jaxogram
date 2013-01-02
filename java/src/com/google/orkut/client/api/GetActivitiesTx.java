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

import java.util.Vector;

/**
 * A {@link Transaction} to fetch activities for a user.
 *
 * @author Sachin Shenoy
 */
public class GetActivitiesTx extends Transaction {
  private static final int DEFAULT_NUM_ACTIVITIES = 10;
  // Maximum number of activity we allow to be fetched for a user.
  private static final int MAX_START_INDEX = 70;
  private Vector activities;

  GetActivitiesTx(String personId) {
    super(MethodNames.ACTIVITIES_GET);
    request.setUserId(personId)
           .setGroupId(Group.ALL)
           .setCount(DEFAULT_NUM_ACTIVITIES)
           .setStartIndex(0)
           .addParameter("coalesce", true);
  }

  GetActivitiesTx() {
    this(Constants.USERID_ME);
  }

  public GetActivitiesTx setCount(int count) {
    request.setCount(count);
    return this;
  }

  public GetActivitiesTx alsoGetRelevantProfiles() {
    request.addField(Fields.RELEVANT_PROFILES);
    return this;
  }

  public GetActivitiesTx alsoGetPageUrls() {
    request.addField(Fields.PAGE_URL);
    return this;
  }

  /** includes you-tube urls in video-share activities */
  public GetActivitiesTx alsoGetYoutubeUrls() {
    request.addField(Fields.YOUTUBE_URL);
    return this;
  }

  GetActivitiesTx getNext() {
    GetActivitiesTx fetchActivityTx = new GetActivitiesTx();
    fetchActivityTx.request.addParameter(
        Params.UPDATED_BEFORE,
        Util.getFormattedTimestamp(((ActivityEntry) activities
          .lastElement()).getPostedTime() * 1000)).setCount(
              request.getCount());
    return fetchActivityTx;
  }
  
  protected void setResponseData(JSONObject data) {
    activities = Util.forEachItemInList(data, ResponseFields.LIST_KEY, new ActivityConverter());
  }

  public int getActivityCount() {
    if (activities == null) {
      return 0;
    }
    return activities.size();
  }

  public ActivityEntry getActivity(int index) {
    return (ActivityEntry) activities.get(index);
  }
  
  public boolean hasNext() {
    // TODO(sachins): figure out a way to find if we have reached the end.
    // Right now clients have to fetch more, and stop when the don't receive
    // any more activities. This check provides a max bound for hasNext to
    // return true.
    return !this.activities.isEmpty();
  }
  
}

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


import org.json.me.JSONArray;
import org.json.me.JSONException;
import org.json.me.JSONObject;

import java.util.Vector;

/**
 * A {@link Transaction} to get friends list of a user on orkut.
 *
 * @author Sachin Shenoy
 */
public class GetFriendTx extends Transaction {
  private static final int DEFAULT_NUM_FRIENDS = 20;

  private Vector friends;
  private int totalResults;

  GetFriendTx(String userId, int startIndex) {
    super(RequestIds.FRIENDS_GET, MethodNames.PEOPLE_GET);
    request.setUserId(userId).setGroupId(Group.FRIENDS);
    request.setStartIndex(startIndex);
    request.setCount(DEFAULT_NUM_FRIENDS);
  }

  GetFriendTx(GetFriendTx prev) {
    this(prev.getUserId(), prev.getStartIndex() + prev.getFriendsCount());
    setCount(prev.getRequestCount());
    JSONArray fields = prev.request.getFields();
    for (int i = 0; i < fields.length(); i++) {
      request.addFieldIfNotPresent(fields.optString(i));
    }
  }

  /**
   * Sets the (max) number of friends to fetch.
   */
  public GetFriendTx setCount(int count) {
    request.setCount(count);
    return this;
  }

  public GetFriendTx alsoGetName() {
    request.addFieldIfNotPresent(Fields.NAME);
    return this;
  }

  public GetFriendTx alsoGetThumbnailUrl() {
    request.addFieldIfNotPresent(Fields.THUMBNAIL_URL);
    return this;
  }

  public GetFriendTx alsoGetProfileUrl() {
    request.addFieldIfNotPresent(Fields.PROFILE_URL);
    return this;
  }

  public GetFriendTx alsoGetStatus() {
    request.addFieldIfNotPresent(Fields.STATUS);
    return this;
  }

  public GetFriendTx alsoGetEmails() {
    request.addFieldIfNotPresent(Fields.EMAILS);
    return this;
  }

  public GetFriendTx alsoGetGender() {
    request.addFieldIfNotPresent(Fields.GENDER);
    return this;
  }

  public GetFriendTx alsoGetPhoneNumbers() {
    request.addFieldIfNotPresent(Fields.PHONE_NUMBERS);
    return this;
  }

  protected void setResponseData(JSONObject data) {
    totalResults = data.optInt(Fields.TOTAL_RESULTS);
    friends = new Vector();
    try {
      JSONArray resultsArray = data.getJSONArray(ResponseFields.LIST_KEY);
      int numFriends = resultsArray.length();
      for (int i = 0; i < numFriends; i++) {
        JSONObject personJson = resultsArray.getJSONObject(i);
        friends.add(new OrkutPerson(personJson));
      }
    } catch (JSONException jse) {
      throw new RuntimeException("Unexpected json exception.", jse);
    }
  }

  public int getFriendsCount() {
    return friends.size();
  }

  public OrkutPerson getFriend(int index) {
    return (OrkutPerson) friends.get(index);
  }

  /**
   * Returns true if there are more friends available to be fetched.
   * Use {@link FriendTxFactory#getNextFriends(GetFriendTx)} to
   * get more friends.
   */
  public boolean canGetMoreFriends() {
    return totalResults - getStartIndex() - getFriendsCount() > 0;
  }

  private String getUserId() {
    return request.getUserId();
  }

  private int getStartIndex() {
    return request.getStartIndex();
  }

  private int getRequestCount() {
    return request.getCount();
  }
}

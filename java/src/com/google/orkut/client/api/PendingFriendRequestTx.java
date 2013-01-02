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
 * Fetches list of pending friend requests. Use {@link FriendRequestTx} to
 * create an instance of this.
 *
 * @author Sachin Shenoy
 */
public class PendingFriendRequestTx extends Transaction {

  private Vector entries;

  PendingFriendRequestTx() {
    super(MethodNames.MESSAGES_GET);
    request.setUserId(Constants.USERID_ME)
           .setGroupId(Group.FRIENDS)
           .addParameter("messageType", "notification")
           .addParameter("messageGroup", "friendRequests");
  }

  protected void setResponseData(JSONObject data) {
    entries = Util.forEachItemInList(data, ResponseFields.LIST_KEY, new Converter() {
      Object convert(JSONObject json) {
        return new FriendRequestEntry(json);
      }
    });
  }

  public FriendRequestEntry getPendingFriendRequest(int index) {
    return (FriendRequestEntry) entries.get(index);
  }
  
  public int getPendingFriendRequestCount() {
    return entries.size();
  }
}

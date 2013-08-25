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

/**
 * A {@link Transaction} to add a person as a friend.
 *
 * @author Sachin Shenoy
 */
public class FriendRequestTx extends Transaction {

  FriendRequestTx(String method, String personId) {
    super(RequestIds.FRIEND_REQUEST, method);
    request.setUserId(Constants.USERID_ME).setGroupId(Group.FRIENDS);
    request.addParameter(Params.PERSON_ID, personId);
  }

  /**
   * Sets bit to allow the friend to initiate IM requests.
   */
  public FriendRequestTx setAllowIm(boolean allowIm) {
    request.addParameter(Fields.ALLOW_IM, allowIm);
    return this;
  }

  /**
   * Sets the (personalized) message that may be sent to the friend.
   */
  public FriendRequestTx setNote(String note) {
    request.addParameter(Fields.NOTE, note);
    return this;
  }
}

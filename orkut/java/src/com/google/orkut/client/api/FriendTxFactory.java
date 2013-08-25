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
 * Factory for creating transaction related to friends. Use this class if you
 * want to:
 * <ul>
 * <li> Fetch list of friends of a user
 * <li> Send or Revoke a friend request
 * <li> Accept or Reject a friend request
 * <li> Remove a user from your friend list
 * <li> Fetch list of pending friend requests
 * </ul>
 *
 * @author Sachin Shenoy
 */
public class FriendTxFactory {

  /**
   * Returns {@link GetFriendTx} object to fetch self friends.
   */
  public GetFriendTx getSelfFriends() {
    return new GetFriendTx(Constants.USERID_ME, 0);
  }

  /**
   * Returns {@link GetFriendTx} object to fetch friends of the given user.
   */
  public GetFriendTx getFriendsOf(String userId) {
    return new GetFriendTx(userId, 0);
  }

  /**
   * Returns a {@link GetFriendTx} object to fetch the next set of friends.
   *
   * @param prev the previous {@link GetFriendTx} to fetch friends
   */
  public GetFriendTx getNextFriends(GetFriendTx prev) {
    return new GetFriendTx(prev);
  }

  /**
   * Returns {@link FriendRequestTx} object to send a friend request.
   *
   * @param personId id of the person to send the friend request to
   */
  public FriendRequestTx sendFriendRequest(String personId) {
    return new FriendRequestTx(MethodNames.FRIEND_REQUEST_SEND, personId);
  }

  /**
   * Returns {@link FriendRequestTx} object to accept a friend request.
   *
   * @param personId id of the person whose friend request to accept to
   */
  public FriendRequestTx acceptFriendRequest(String personId) {
    return new FriendRequestTx(MethodNames.FRIEND_REQUEST_ACCEPT, personId);
  }

  /**
   * Returns {@link Transaction} object to revoke a friend request. Use this
   * when you want to revoke (or take back) a friend request that you have sent.
   *
   * @param personId
   */
  public Transaction revokeFriendRequest(String personId) {
    return denyFriendRequestHelper(MethodNames.FRIEND_REQUEST_REVOKE, personId);
  }

  /**
   * Returns {@link Transaction} object to reject a friend request.
   *
   * @param personId id of the person whose friend request is to be rejected
   */
  public Transaction rejectFriendRequest(String personId) {
    return denyFriendRequestHelper(MethodNames.FRIEND_REQUEST_REJECT, personId);
  }

  /**
   * Removes a friend request.
   */
  public Transaction removeFriendRequest(String personId) {
    return denyFriendRequestHelper(MethodNames.FRIEND_REMOVE, personId);
  }

  /**
   * Gets pending friend requests.
   */
  public PendingFriendRequestTx getPendingFriendRequest() {
    return new PendingFriendRequestTx();
  }

  /**
   * Gets birthday notification of your friends.
   */
  public BirthdayNotificationTx getBirthdayNotification() {
    return new BirthdayNotificationTx();
  }
  
  /**
   * Helper to handle all the negative cases of friend request, like reject,
   * revoke, remove etc.
   */
  private Transaction denyFriendRequestHelper(String method, String personId) {
    Transaction transaction = new Transaction(method);
    transaction.getOrkutRequest().setUserId(Constants.USERID_ME)
        .setGroupId(Group.FRIENDS)
        .addParameter(Params.PERSON_ID, personId);
    return transaction;
  }
}

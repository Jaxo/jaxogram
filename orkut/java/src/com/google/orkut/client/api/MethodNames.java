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
 * Internal constants (OS names of methods) used by this library.
 *
 * @author Shishir Birmiwal
 */
class MethodNames {
  static final String ACL_CREATE = "aclentries.create";
  static final String ACTIVITIES_GET = "activities.get";
  static final String ALBUMS_CREATE = "albums.create";
  static final String ALBUMS_DELETE = "albums.delete";
  static final String ALBUMS_GET = "albums.get";
  static final String ALBUMS_UPDATE = "albums.update";

  static final String CAPTCHA_ANSWER = "captcha.answer";

  static final String COMMENTS_CREATE = "comments.create";
  static final String COMMENTS_DELETE = "comments.delete";
  static final String COMMENTS_GET = "comments.get";

  static final String CREATE_ACTIVITIES = "activities.create";

  static final String FRIEND_REMOVE = "people.removeFriend";
  static final String FRIEND_REQUEST_ACCEPT = "people.acceptFriendRequest";
  static final String FRIEND_REQUEST_REJECT = "people.rejectFriendRequest";
  static final String FRIEND_REQUEST_REVOKE = "people.revokeFriendRequest";
  static final String FRIEND_REQUEST_SEND = "people.sendFriendRequest";

  static final String MEDIAITEMS_CREATE = "mediaitems.create";
  static final String MEDIAITEMS_DELETE = "mediaitems.delete";
  static final String MEDIAITEMS_GET = "mediaitems.get";
  static final String MEDIAITEMS_UPDATE = "mediaitems.update";

  static final String MESSAGES_CREATE = "messages.create";
  static final String MESSAGES_DELETE = "messages.delete";
  static final String MESSAGES_GET = "messages.get";

  static final String PEOPLE_GET = "people.get";
  static final String PEOPLE_UPDATE = "people.update";
  
  static final String VIDEOS_GET = "videos.get";
}
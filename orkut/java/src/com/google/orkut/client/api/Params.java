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
 * Internal constants (parameters of requests) used by this library.
 *
 * @author Shishir Birmiwal
 */
class Params {
  static class PageType {
    static final String FIRST = "first";
    static final String LAST = "last";
    static final String NEXT = "next";
    static final String PREV = "prev";
  }
  static final String ACLENTRY = "aclentry";
  static final String ACTIVITY = "activity";
  static final String ALBUM = "album";
  static final String ALBUM_ID = "albumId";
  static final String CAPTCHA_ANSWER = "captchaAnswer";
  static final String CAPTCHA_TOKEN = "captchaToken";
  static final String CAPTCHA_URL = "captchaUrl";
  static final String COMMENT = "comment";
  static final String COMMENT_ID = "commentId";
  static final String COUNT = "count";
  static final String FIELDS = "fields";
  static final String GROUP_ID = "groupId";
  static final String LAST_KEY = "lastKey";
  static final String MEDIA_ITEM = "mediaItem";
  static final String MEDIA_ITEM_ID = "mediaItemId";
  static final String MESSAGE = "message";
  static final String MESSAGE_TYPE = "messageType";
  static final String METHOD = "method";
  static final String MSG_ID = "msgId";
  static final String NOTIFICATION = "notification";
  static final String OWNER_ID = "ownerId";
  static final String PARAMS = "params";
  static final String PERSON = "person";
  static final String PERSON_ID = "personId";
  static final String RELEVANT_USER_IDS = "relevantUserIds";
  static final String SORT_BY = "sortBy";
  static final String START_INDEX = "startIndex";
  static final String TYPE = "type";
  static final String UPDATED_BEFORE = "updatedBefore";
  static final String USER_ID = "userId";
}
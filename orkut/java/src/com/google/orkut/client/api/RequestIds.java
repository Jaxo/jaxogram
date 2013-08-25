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
 * Internal constants (request identifiers) used by this library.
 *
 * @author Shishir Birmiwal
 */
class RequestIds {
  static final String ACTIVITIES_CREATE = "a.c";

  static final String ALBUMS_CREATE = "a.c";
  static final String ALBUMS_DELETE = "a.d";
  static final String ALBUM_SHARE = "a.s";
  static final String ALBUMS_GET = "a.g";
  static final String ALBUMS_UPDATE = "a.u";

  static final String CAPTCHA_ANSWER = "cpta.a";

  static final String COMMENTS_CREATE = "c.c";
  static final String COMMENTS_DELETE = "c.d";
  static final String COMMENTS_GET = "c.g";

  static final String FRIEND_REQUEST = "fr";
  static final String FRIENDS_GET = "frnds.g";

  static final String MEDIAITEMS_CREATE = "m.c";
  static final String MEDIAITEMS_DELETE = "m.d";
  static final String MEDIAITEMS_GET = "m.g";
  static final String MEDIAITEMS_UPDATE = "m.u";

  static final String PEOPLE_GET = "p.g";
  static final String PROFILE_UPDATE = "p.u";

  static final String SCRAPS_DELETE = "scrp.d";
  static final String SCRAPS_GET = "scrp.g";
  static final String SCRAPS_WRITE = "scrp.c";
  
  static final String VIDEOS_GET = "v.g";
}
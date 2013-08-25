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
 * OpenSocial defined constants for user groups that are used to define collection of contacts to
 * be fetched by getPeople or collection of contacts whose activities need to be fetched in
 * getActivites.
 * See http://www.opensocial.org/Technical-Resources/opensocial-spec-v081/restful-protocol
 *
 * @author Rohit Jain
 */
class Group {
  /* The universal set of contacts (a collection) */
  static final String ALL = "@all";

  /* Friends only (a collection) */
  static final String FRIENDS = "@friends";

  /* The universal set of contacts (a collection) */
  static final String PROXY_CONTACTS = "@proxy";

  /* User guid only */
  static final String SELF = "@self";
}
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
 * Constants used in orkut client library api.
 *
 * @author Shishir Birmiwal
 */
public class Constants {
  /**
   * Defines possible values for a person's gender value
   * as returned from {@link OrkutPerson#getGender()}.
   */
  public static class Gender {
    public static final String FEMALE = "female";
    public static final String MALE = "male";
  }

  /**
   * Defines possible values for a media-item in an activity,
   * as returned from {@link MediaItem#getType()}.
   */
  public static class MediaItemType {
    public static final String IMAGE = "image";
    public static final String VIDEO = "video";
  }

  /**
   * Defines the possible value to be sent in the call to
   * {@link UpdateProfileTx#setRelationshipStatus(String)}.
   */
  public static class RelationshipStatus {
    public static String COMMITTED = "committed";
    public static String MARRIED = "married";
    public static String OPEN_MARRIAGE = "open marriage";
    public static String OPEN_RELATIONSHIP = "open relationship";
    public static String SINGLE = "single";
  }

  /** A user-id which represents the logged in person. */
  public static final String USERID_ME = "@me";
  
  /**Constants for the date formatter. */
  public static class DateFormatter {
    public static final String DATE_SEPARATOR = "-";
    public static final String TIME_SEPARATOR = ":";
    public static final String DATE_DELIM = "T";
    public static final String TIME_DELIM = "Z";
    public static final String UTC = "UTC";
  }
}

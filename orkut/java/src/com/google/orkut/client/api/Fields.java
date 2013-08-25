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
 * Internal constants (field names) used by this library.
 *
 * @author Shishir Birmiwal
 */
class Fields {
  static class AclEntryFields {
    /** Level of access for this entry in the ACL (read/write). */
    static final String ACCESS_TYPE = "accessType";
    static final String ACCESSOR_ID = "accessorId";
    /** The identifier for this ACL entry. */
    static final String ACCESSOR_INFO = "accessorInfo";
    /** Specifies the type of user in this entry of the ACL. */
    static final String ACCESSOR_TYPE = "accessorType";
  }
  public static final String BIRTHDAY = "birthday";
  public static final String COUNTRY = "country";
  public static final String CURRENT_LOCATION = "currentLocation";
  public static final String LOCALITY = "locality";
  public static final String POSTAL_CODE = "postal";
  public static final String REGION = "region";
  static final String ABOUT_ME = "aboutMe";
  /** data specifying the ACL for this album */
  static final String ACL = "accessControlList";
  static final String ACTIVITY_TYPE = "activityType";
  static final String ALBUM_ID = Params.ALBUM_ID;
  static final String ALBUM_TITLE = "albumTitle";
  static final String ALLOW_IM = "allowIm";
  static final String APP_ID = "appId";
  static final String AUTHOR_ID = "authorId";

  static final String BODY = "body";
  static final String COMMENTS = "comments";
  static final String CREATED = "created";
  static final String DESCRIPTION = "description";
  static final String DISPLAY_NAME = "displayName";
  static final String EMAILS = "emails";
  /** The list of entries in an ACL */
  static final String ENTRIES = "entries";
  static final String ERROR_TYPE = "errorType";
  static final String FROM_USER_ID = "fromUserId";
  static final String FROM_USER_PROFILE = "fromUserProfile";
  static final String GENDER = "gender";
  static final String ID = "id";

  /** the json field for location */
  static final String LOCATION = "location";
  /** the json field for count of media items in album */
  static final String MEDIA_ITEM_COUNT = "mediaItemCount";
  static final String MEDIA_ITEMS = "mediaItems";
  /** media mime types in the album */
  static final String MEDIA_MIME_TYPE = "mediaMimeType";
  /** media types in the album */
  static final String MEDIA_TYPE = "mediaType";
  static final String MESSAGE_TYPE = "messageType";
  static final String NAME = "name";
  // The next two are subfields of "name":
  static final String NAME_FAMILY_NAME = "familyName";
  static final String NAME_GIVEN_NAME = "givenName";
  static final String NOTE = "note";
  static final String PAGE_TYPE = "pageType";
  static final String PAGE_URL = "pageUrl";
  static final String PHONE_NUMBERS = "phoneNumbers";
  static final String POSTED_TIME = "postedTime";
  static final String PROFILE_FIELDS = "profileFields";
  static final String PROFILE_URL = "profileUrl";
  static final String RELATIONSHIP_STATUS = "relationshipStatus";
  static final String RELEVANT_PROFILES = "relevantProfiles";
  static final String RELEVANT_USER_IDS = "relevantUserIds";
  static final String STATUS = "status";
  static final String TEMPLATE_PARAMS = "templateParams";
  static final String TEXT = "text";
  /** the json field for thumbnail url */
  static final String THUMBNAIL_URL = "thumbnailUrl";
  static final String TIME = "time";
  static final String TITLE = "title";
  static final String TOTAL_RESULTS = "totalResults";
  static final String TYPE = "type";
  static final String URL = "url";
  static final String VALUE = "value";
  static final String YOUTUBE_URL = "youtubeUrl";
  static final String VIDEO_DURATION = "durationInSec";
}

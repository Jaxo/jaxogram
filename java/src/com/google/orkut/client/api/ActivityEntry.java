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
import org.json.me.JSONObject;

import java.util.HashMap;

/**
 * An activity entry.
 *
 * @author Sachin Shenoy
 */
public abstract class ActivityEntry {
  public static class ActivityType {
    public static final String ALBUM = "ALBUM";
    public static final String ALBUM_SHARE = "ALBUM_SHARE";
    public static final String APPLICATION = "APPLICATION";
    public static final String COMMUNITY_CREATE = "COMMUNITY_CREATE";
    public static final String COMMUNITY_JOIN = "COMMUNITY_JOIN";
    public static final String FRIEND_ADD = "FRIEND_ADD";
    public static final String MAKAMAKA = "MAKAMAKA";
    public static final String PHOTO = "PHOTO";
    public static final String PHOTO_COMMENT = "PHOTO_COMMENT";
    public static final String PHOTO_TAG = "PHOTO_TAG";
    public static final String PROFILE_UPDATE = "PROFILE_UPDATE";
    public static final String SCRAP = "SCRAP";
    public static final String SKIN_CHANGE = "SKIN_CHANGE";
    public static final String SOCIAL_EVENTS_CREATION = "SOCIAL_EVENTS_CREATION";
    public static final String STATUS_MSG = "STATUS_MSG";
    public static final String STATUS_MSG_COMMENT = "STATUS_MSG_COMMENT";
    public static final String TESTIMONIAL = "TESTIMONIAL";
    public static final String TESTIMONIAL_USER = "TESTIMONIAL_USER";
    public static final String VIDEO = "VIDEO";
    public static final String VIDEO_COMMENT = "VIDEO_COMMENT";
  }

  protected JSONObject json;
  protected JSONArray relevantUserIds;
  protected JSONArray relevantProfiles;
  private final HashMap relevantProfileMap = new HashMap();

  ActivityEntry(JSONObject json) {
    this.json = json;
    if (!json.has(Params.RELEVANT_USER_IDS)) {
      throw new CreationException("Field " + Params.RELEVANT_USER_IDS
          + " missing!");
    }
    relevantUserIds = json.optJSONArray(Params.RELEVANT_USER_IDS);

    if (hasRelevantProfiles()) {
      relevantProfiles = json.optJSONArray(Fields.RELEVANT_PROFILES);
      for (int i = 0; i < relevantProfiles.length(); ++i) {
        JSONObject jsonProfile = relevantProfiles.optJSONObject(i);
        OrkutPerson profile = new OrkutPerson(jsonProfile);
        relevantProfileMap.put(profile.getId(), profile);
      }
    }
  }

  /** Unique id associated with this activity */
  public String getId() {
    return json.optString("id");
  }

  /** ID of the owner (or creator) of the activity */
  public String getOwnerId() {
    return json.optString("userId");
  }

  public OrkutPerson getOwnerProfile() {
    return getRelevantProfile(getOwnerId());
  }

  /** Time at which the activity was posted */
  public long getPostedTime() {
    return json.optLong(Fields.POSTED_TIME);
  }

  protected int getRelevantUserIdCount() {
    return relevantUserIds.length();
  }

  protected String getRelevantUserId(int index) {
    return relevantUserIds.optString(index);
  }

  protected boolean hasRelevantProfiles() {
    return json.has(Fields.RELEVANT_PROFILES);
  }

  protected OrkutPerson getRelevantProfile(String userId) {
    return (OrkutPerson) relevantProfileMap.get(userId);
  }

  /**
   * Source application of the activity, <code>null</code> if activity was not generated
   * by an app, or if source app detail not known.
   */
  public Application getSource() {
    return null;
  }

  /** Type of the activity one from the {@link ActivityEntry.ActivityType}. */
  public abstract String getType();


  @Override
  public String toString() {
     return new StringBuilder()
       .append("Activity Type    : ").append(getType()).append("\n")
       .append("ID               : ").append(getId()).append("\n")
       .append("Owner ID         : ").append(getOwnerId()).append("\n")
       .append("Posted time      : ").append(String.valueOf(getPostedTime()))
       .append("\n")
       .append("Relevant users   : ")
       .append(String.valueOf(getRelevantUserIdCount())).append("\n")
       .append("Relevant profiles: ")
       .append(hasRelevantProfiles() ? "yes" : "no").append("\n").toString();
  }
}


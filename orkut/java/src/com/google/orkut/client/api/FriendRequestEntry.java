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

/**
 * Represents an incoming friend request entry.
 * 
 * @author Sachin Shenoy
 */
public class FriendRequestEntry {
  
  private String userId;
  private String message;
  private OrkutPerson userProfile;
  
  public FriendRequestEntry(JSONObject json) {
    userId = Util.JSONUtil.getRequiredStringField(Fields.FROM_USER_ID, json);
    message = json.optString(Fields.BODY);
    JSONObject userProfileJson =
        Util.JSONUtil.getRequiredJSONObjectField(Fields.FROM_USER_PROFILE, json);
    userProfile = new OrkutPerson(userProfileJson);
  }
  
  /**
   * Returns Id of the user who sent the friend request.
   */
  public String getUserId() {
    return userId;
  }
  
  /**
   * Returns the message received along with the friend invite.
   */
  public String getMessage() {
    return message;
  }
  
  /**
   * Returns profile of the user who sent the friend invite.
   */
  public OrkutPerson getUserProfile() {
    return userProfile;
  }
}

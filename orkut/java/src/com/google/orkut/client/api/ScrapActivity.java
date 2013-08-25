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
import org.json.me.JSONException;
import org.json.me.JSONObject;

/**
 * A scrap related activity on orkut.
 *
 * @author Sachin Shenoy
 */
public class ScrapActivity extends ActivityEntry {
  // receiver of scrap
  private static final int SCRAP_RECEIVER = 0;

  // sender of scrap
  private static final int SCRAP_SENDER = 1;

  private String receiverId;
  private String senderId;

  private OrkutPerson receiverProfile;
  private OrkutPerson senderProfile;

  ScrapActivity(JSONObject json) {
    super(json);
    parse(json);
  }

  private void parse(JSONObject json) {
    JSONArray userIds = json.optJSONArray(Fields.RELEVANT_USER_IDS);
    if (userIds != null) {
      try {
        receiverId = userIds.getString(SCRAP_RECEIVER);
        senderId = userIds.getString(SCRAP_SENDER);
      } catch (JSONException e) {
        throw new CreationException("scrap information incomplete", e);
      }
    }

    JSONArray relevantProfiles = json.optJSONArray(Fields.RELEVANT_PROFILES);
    if (relevantProfiles != null) {
      try {
        receiverProfile = new OrkutPerson(relevantProfiles.getJSONObject(SCRAP_RECEIVER));
      } catch (JSONException e) {
        // still nothing?
      }
      try {
        senderProfile = new OrkutPerson(relevantProfiles.getJSONObject(SCRAP_SENDER));
      } catch (JSONException e) {
        // nope.. nothing
      }
    }
  }

  public String getType() {
    return ActivityEntry.ActivityType.SCRAP;
  }

  /** Returns body of the scrap */
  public String getBody() {
    return json.optString(Fields.BODY);
  }

  /** Returns user id of the receiver of the scrap */
  public String getReceiverId() {
    return receiverId;
  }

  /** Returns user id of the sender of the scrap */
  public String getSenderId() {
    return senderId;
  }

  /** Returns profile of the receiver of scrap */
  public OrkutPerson getReceiverProfile() {
    return receiverProfile;
  }

  /** Returns profile of the sender of scrap */
  public OrkutPerson getSenderProfile() {
    return senderProfile;
  }
}
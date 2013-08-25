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
 * An {@link ActivityEntry} for a testimonial on orkut.
 *
 * @author Sachin Shenoy
 */
public class TestimonialActivity extends ActivityEntry {
  private static final int RECEIVER = 1;
  private static final int WRITER = 0;

  String receiverId;
  OrkutPerson receiverProfile;
  OrkutPerson writerProfile;

  TestimonialActivity(JSONObject json) {
    super(json);
    parse(json);
  }

  private void parse(JSONObject json) {
    JSONArray userIds = json.optJSONArray(Fields.RELEVANT_USER_IDS);
    if (userIds != null) {
      try {
        receiverId = userIds.getString(RECEIVER);
      } catch (JSONException e) {
        throw new CreationException("cannot have a testimonial activity without a receiver", e);
      }
    }

    JSONArray relevantProfiles = json.optJSONArray(Fields.RELEVANT_PROFILES);
    if (relevantProfiles != null) {
      try {
        receiverProfile = new OrkutPerson(relevantProfiles.getJSONObject(RECEIVER));
      } catch (JSONException e) {
        // still nothing?
      }
      try {
        writerProfile = new OrkutPerson(relevantProfiles.getJSONObject(WRITER));
      } catch (JSONException e) {
        // nope.. nothing
      }
    }
  }

  public String getType() {
    return ActivityEntry.ActivityType.TESTIMONIAL;
  }

  public String getWriterId() {
    return getOwnerId();
  }

  public String getReceiverId() {
    return receiverId;
  }

  public OrkutPerson getWriterProfile() {
    return writerProfile;
  }

  public OrkutPerson getReceiverProfile() {
    return receiverProfile;
  }
}

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
 * Converts a json to an {@link ActivityEntry}.
 *
 * @author Sachin Shenoy
 */
class ActivityConverter extends Converter {

  Object convert(JSONObject json) {
    String activityType = getTypeOf(json);

    if (ActivityEntry.ActivityType.FRIEND_ADD.equals(activityType)) {
      return new FriendAddActivity(json);
    }
    if (ActivityEntry.ActivityType.MAKAMAKA.equals(activityType)) {
      return new MakamakaActivity(json);
    }
    if (ActivityEntry.ActivityType.PHOTO_COMMENT.equals(activityType)) {
      return new PhotoCommentActivity(json);
    }
    if (ActivityEntry.ActivityType.PROFILE_UPDATE.equals(activityType)) {
      return new ProfileUpdateActivity(json);
    }
    if (ActivityEntry.ActivityType.SCRAP.equals(activityType)) {
      return new ScrapActivity(json);
    }
    if (ActivityEntry.ActivityType.SOCIAL_EVENTS_CREATION.equals(activityType)) {
      return new SocialEventActivity(json);
    }
    if (ActivityEntry.ActivityType.STATUS_MSG.equals(activityType)) {
      return new StatusMessageActivity(json);
    }
    if (ActivityEntry.ActivityType.TESTIMONIAL.equals(activityType)) {
      return new TestimonialActivity(json);
    }
    if (ActivityEntry.ActivityType.VIDEO.equals(activityType)) {
      return new VideoShareActivity(json);
    }
    if (ActivityEntry.ActivityType.PHOTO.equals(activityType)) {
      return new PhotoShareActivity(json);
    }

    return new GenericActivity(json);
  }

  /** Type of the activity one from the {@link ActivityEntry.ActivityType}. */
  static String getTypeOf(JSONObject json) {
    JSONObject templateParams = json.optJSONObject(Fields.TEMPLATE_PARAMS);
    if (templateParams == null) {
      return "";
    }
    return templateParams.optString(Fields.ACTIVITY_TYPE);
  }
}

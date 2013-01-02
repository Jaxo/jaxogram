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

import java.util.Vector;

/**
 * An {@link ActivityEntry} associated with a user updating his/her profile.
 *
 * @author Sachin Shenoy
 */
public class ProfileUpdateActivity extends ActivityEntry {

  public static class ProfileFields {
    public static final String FIRST_NAME = "first_name";
    public static final String LAST_NAME = "last_name";
    public static final String GENDER = "gender";
    public static final String STATUS = "status";
    public static final String BIRTH_DATE = "birth_date";
    public static final String HERE_FOR = "here_for";
    public static final String ABOUT_ME = "about_me";
    public static final String KIDS = "kids";
    public static final String ETHNICITY = "ethnicity";
    public static final String LANGUAGE = "language";
    public static final String POLITICAL = "political";
    public static final String RELIGION = "religion";
    public static final String HUMOR = "humor";
    public static final String SEX_PREF = "sex_pref";
    public static final String FASHION = "fashion";
    public static final String SMOKING = "smoking";
    public static final String DRINKING = "drinking";
    public static final String PETS = "pets";
    public static final String LIVING = "living";
    public static final String HOME_TOWN = "home_town";
    public static final String HOME_TOWN_STATE = "home_town_state";
    public static final String WEB_PAGE = "web_page";
    public static final String PASSIONS = "passions";
    public static final String SPORTS = "sports";
    public static final String ACTIVITIES = "activities";
    public static final String BOOKS = "books";
    public static final String MUSIC = "music";
    public static final String SHOWS = "shows";
    public static final String MOVIES = "movies";
    public static final String CUISINES = "cuisines";
    public static final String HOME_PHONE = "home_phone";
    public static final String CELL_PHONE = "cell_phone";
    public static final String CITY = "city";
    public static final String STATE = "state";
    public static final String COUNTRY = "country";
    public static final String POSTAL_CODE = "postal_code";
    public static final String WISHLIST_LIST = "wishlist_list";
    public static final String EDUCATION = "education";
    public static final String HIGH_SCHOOL = "high_school";
    public static final String HIGH_SCHOOL_STATE = "high_school_state";
    public static final String OCCUPATION = "occupation";
    public static final String INDUSTRY = "industry";
    public static final String SUB_INDUSTRY = "sub_industry";
    public static final String COMPANY = "company";
    public static final String TITLE = "title";
    public static final String JOB = "job";
    public static final String WORK_EMAIL = "work_email";
    public static final String WORK_PHONE = "work_phone";
    public static final String WORK_SKILLS = "work_skills";
    public static final String WORK_INTERESTS = "work_interests";
    public static final String HEADLINE = "headline";
    public static final String NOTICE = "notice";
    public static final String HEIGHT_CENTIMETERS = "height_centimeters";
    public static final String HEIGHT_FEET = "height_feet";
    public static final String HEIGHT_INCHES = "height_inches";
    public static final String EYE_COLOR = "eye_color";
    public static final String HAIR_COLOR = "hair_color";
    public static final String BUILD = "build";
    public static final String BODY_ART = "body_art";
    public static final String LOOKS = "looks";
    public static final String FEATURE = "feature";
    public static final String TURN_ON = "turn_on";
    public static final String TURN_OFF = "turn_off";
    public static final String FIRST_DATE = "first_date";
    public static final String LEARNED = "learned";
    public static final String FIVE_ITEMS = "five_items";
    public static final String BEDROOM = "bedroom";
    public static final String ABOUT_YOU = "about_you";
  }

  private Vector profileFields;

  public ProfileUpdateActivity(JSONObject json) {
    super(json);
    parse(json);
  }

  private void parse(JSONObject json) {
    profileFields = Util.forEachItemInList(json, Fields.PROFILE_FIELDS,
        new Converter() {
          Object convert(JSONObject json) {
            return json.optString(Fields.TYPE);
          }
        });
  }

  public String getType() {
    return ActivityEntry.ActivityType.PROFILE_UPDATE;
  }

  public int getProfileFieldCount() {
    return profileFields.size();
  }

  /** Returns profile filed string value from {@link ProfileFields}. */
  public String getProfileField(int index) {
    return (String) profileFields.get(index);
  }
}

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
 * Transaction object for updating profile of self. Use {@link ProfileTxFactory}
 * to create instances of this class.
 *
 * @author Sachin Shenoy
 */
public class UpdateProfileTx extends Transaction {
  private final JSONObject person;

  UpdateProfileTx() {
    super(RequestIds.PROFILE_UPDATE, MethodNames.PEOPLE_UPDATE);
    person = new JSONObject();
    request.addParameter(Params.PERSON, person);
    request.setUserId(Constants.USERID_ME).setGroupId(Group.SELF);
  }

  /**
   * Sets the orkut "status" of the user.
   *
   * @param status status value to set to
   */
  public UpdateProfileTx setStatus(String status) {
    Util.putJsonValue(person, Fields.STATUS, status);
    return this;
  }

  /**
   * Sets the "about me" profile field of the user.
   *
   * @param aboutMe about me value to be set
   */
  public UpdateProfileTx setAboutMe(String aboutMe) {
    Util.putJsonValue(person, Fields.ABOUT_ME, aboutMe);
    return this;
  }

  /**
   * Sets the "relationship status" profile field of the user.
   *
   * @param relationshipStatus relationship status, one of the constants from
   *     {@link Constants.RelationshipStatus}
   */
  public UpdateProfileTx setRelationshipStatus(String relationshipStatus) {
    Util.putJsonValue(person, Fields.RELATIONSHIP_STATUS, relationshipStatus);
    return this;
  }

  /**
   * Sets the birthday profile field of the user.
   *
   * @param birthday the birthday in seconds since unix epoch
   */
  public UpdateProfileTx setBirthday(long birthday) {
    Util.putJsonValue(person, Fields.BIRTHDAY, birthday);
    return this;
  }

  /**
   * Sets the address field of the user.
   *
   * @param address the address to set for the user
   */
  public UpdateProfileTx setAddress(Address address) {
    Util.putJsonValue(person, Fields.CURRENT_LOCATION, address.getJson());
    return this;
  }

  protected void setResponseData(JSONObject response) {}
}

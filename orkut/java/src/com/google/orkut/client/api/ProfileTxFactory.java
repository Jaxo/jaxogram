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
 * Factory for creating transactions related to user profile. Use this class if
 * you have to fetch a persons profile, or update a persons profile.
 *
 * <p/>
 * <b>Example for fetching own profile:</b>
 * <pre>
 * GetProfileTx getProfileTx = profileTxFactory.getSelfProfile();
 *
 * String request  = batch.add(getProfileTx).getRequest();
 * // <--- send request to server and get response --->
 * batch.setRespone(response);
 *
 * if (getProfileTx.hasError()) {
 *    // handle error.
 * }
 *
 * OrkutPerson myProfile = getProfileTx.getProfile();
 * System.out.println("Hi, My name is " + myProfile.getDisplayName());
 * </pre>
 *
 * <b>Example for updating own profile:</b>
 * <pre>
 * UpdateProfileTx udpateProfileTx = profileTxFactory.updateSelfProfile();
 *
 * updateProfileTx.setStatus("Is Using Orkut API");
 *
 * String request = batch.add(updateProfileTx).getRequest();
 * // <--- send request to server and get response --->
 * batch.setRespone(response);
 *
 * if (updateProfileTx.hasError()) {
 *    // handle error.
 * }
 * </pre>
 *
 * @author Sachin Shenoy
 */
public class ProfileTxFactory {

  /**
   * Returns a GetProfile transaction object to fetch self profile.
   *
   * @return transaction object
   */
  public GetProfileTx getSelfProfile() {
    return new GetProfileTx();
  }

  /**
   * Returns a {@link GetProfileTx} object to fetch the profile of the given
   * user.
   *
   * @param userId user id of the person whose profile to fetch
   * @return transaction object
   */
  public GetProfileTx getProfileOf(String userId) {
    return new GetProfileTx(userId);
  }

  /**
   * Returns an {@link UpdateProfileTx} object to update the profile of self.
   *
   * @return transaction object
   */
  public UpdateProfileTx updateSelfProfile() {
    return new UpdateProfileTx();
  }
}

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
 * An album entity. Represents an orkut album.
 *
 * @author Shishir Birmiwal
 */
public class Album {
  /** Possible values for the field returned by {@link AclEntry#getAccessorType()}. */
  static class AlbumAccessorType {
    // Defines permission for an email address.
    public static final String EXTERNAL = "external";
    // Defines permission for a phone number.
    public static final String PHONE_NUMBER = "phoneNumber";
    // Defines permission for an orkut user.
    public static final String USER = "user";
  }

  /**
   * Possible values for the field returned by {@link AclEntry#getAccessType()}.
   * Note: When sharing with another user, set access-type to {@link AlbumAccessType#READ}.
   *       Other values may not have the desired effect (and are not fully supported at present).
   */
  static class AlbumAccessType {
    public static final String CREATE = "create";
    public static final String DELETE = "delete";
    public static final String READ = "read";
    public static final String UPDATE = "update";
  }


  /**
   * An entry in an {@link AccessControlList}.
   *
   * @author Shishir Birmiwal
   */
  static class AclEntry {
    private JSONObject json;

    /**
     * Create a new {@link AclEntry}.
     * To share an album, create a new {@link AclEntry} and add it to a {@link AccessControlList}.
     * Set the {@link AccessControlList} in {@link Album} using {@link Album#setAccessControlList(AccessControlList)}.
     * Finally call {@link AlbumsTxFactory#updateAlbum(Album)}.
     *
     * <pre>
     *   acl = album.getAccessControlList();
     *   // ..
     *   // ..
     *   AclEntry newAcl = new AclEntry(Constants.AlbumAccessorType.EXTERNAL,
     *       "blah@bleh.bloh",
     *       Constants.AlbumAccessType.READ,
     *       "");
     *   acl.addAclEntry(newAcl);
     *   album.setAccessControlList(acl);
     *   // ..
     *   updateTx = albumsTxFatory.update(album);
     *   // .. add to batch and send to server
     * </pre>
     *
     * @param accessorType see {@link AlbumAccessorType} for valid values
     * @param accessorInfo phone number or email address (if applicable)
     * @param accessType see {@link AlbumAccessType} for valid values
     * @param accessorId the id of the accessor; used when the accessor type is user
     */
    AclEntry(String accessorType, String accessorInfo, String accessType, String accessorId) {
      this(null);
      try {
        json.put(Fields.AclEntryFields.ACCESSOR_TYPE, accessorType);
        json.put(Fields.AclEntryFields.ACCESSOR_INFO, accessorInfo);
        json.put(Fields.AclEntryFields.ACCESS_TYPE, accessType);
        json.put(Fields.AclEntryFields.ACCESSOR_ID, accessorId);
      } catch (JSONException e) {
        throw new RuntimeException("this should never happen!");
      }
    }

    AclEntry(JSONObject json) {
      if (json == null) {
        json = new JSONObject();
      }
      this.json = json;
    }

    AclEntry reJsonify(JSONObject json) {
      this.json = json;
      return this;
    }

    /**
     * The type of access granted by this {@link AclEntry}. Valid values are
     * in {@link Constants.AlbumAccessType}.
     */
    String getAccessType() {
      return json.optString(Fields.AclEntryFields.ACCESS_TYPE);
    }

    /**
     * The email address or phone number of the accessor. This is only applicable if
     * accessor type is email or phone number.
     *
     * @see AclEntry#getAccessorType()
     */
    String getAccessorInfo() {
      return json.optString(Fields.AclEntryFields.ACCESSOR_INFO);
    }

    /**
     * Returns the type of accessor defined by this {@link AclEntry}. Valid values are in
     * {@link Constants.AlbumAccessorType}. Depending on accessor type, related values are
     * had through {@link AclEntry#getAccessorInfo()} or {@link AclEntry#getAccessorId()}.
     */
    String getAccessorType() {
      return json.optString(Fields.AclEntryFields.ACCESSOR_TYPE);
    }

    /**
     * The userid of the user with access. This is present if accessor type is {@link AlbumAccessorType#USER}.
     */
    String getAccessorId() {
      return json.optString(Fields.AclEntryFields.ACCESSOR_ID);
    }

    JSONObject getJson() {
      return json;
    }

    public boolean equals(Object obj) {
      if (!(obj instanceof AclEntry)) {
        return false;
      }
      AclEntry other = (AclEntry) obj;
      return other.getAccessType().equals(getAccessType())
          && other.getAccessorInfo().equals(getAccessorInfo())
          && other.getAccessorType().equals(getAccessorType())
          && other.getAccessorId().equals(getAccessorId());
    }
  }

  /**
   * An access control list. To modify {@link AccessControlList} for
   * an album, get it using {@link Album#getAccessControlList()} and
   * set it back again using {@link Album#setAccessControlList(AccessControlList)}.
   * @deprecated use {@link AlbumsTxFactory#shareAlbumWithFriends(Album)}
   *
   * @author Shishir Birmiwal
   */
  static class AccessControlList {
    private JSONArray json;

    AccessControlList(JSONObject json) {
      if (json == null) {
        json = new JSONObject();
      }
      this.json = json.optJSONArray(Fields.ENTRIES);
      if (this.json == null) {
        this.json = new JSONArray();
      }
    }

    /**
     * @return the number of {@link AclEntry}s in {@link AccessControlList}
     */
    int length() {
      return json.length();
    }

    /**
     * @return the {@link AclEntry} at position {@code index}. Valid values of
     *     index are from {@code 0} to {@link #length()}{@code - 1}
     */
    AclEntry get(int index) {
      return new AclEntry(json.optJSONObject(index));
    }

    /**
     * Add an {@link AclEntry} to this {@link AccessControlList}.
     *
     * @see AclEntry
     */
    void addAclEntry(AclEntry aclEntry) {
      json.put(aclEntry.getJson());
    }

    /**
     * Removes an {@link AclEntry} from this {@link AccessControlList}.
     *
     * @param aclEntry the entry to remove
     * @return true if the given entry was found and removed
     */
    boolean removeAclEntry(AclEntry aclEntry) {
      AclEntry holder = null;
      JSONArray newEntries = new JSONArray();
      for (int i = 0; i < json.length(); i++) {
        if (holder == null) {
          holder = new AclEntry(json.optJSONObject(i));
        }
        JSONObject entry = json.optJSONObject(i);
        if (holder.reJsonify(entry).equals(aclEntry)) {
          // skip the one to delete
          continue;
        }
        newEntries.put(entry);
      }

      if (json.length() == newEntries.length()) {
        // not found
        return false;
      }
      json = newEntries;
      return true;
    }

    JSONObject getJson() {
      try {
        return (new JSONObject()).put(Fields.ENTRIES, json);
      } catch (JSONException e) {
        // this should not happen
        throw new RuntimeException("unable to add to a json object!?"
            + "this really should not happen!");
      }
    }
  }

  private final JSONObject json;

  Album(String ownerId, String albumId) {
    this.json = new JSONObject();
    Util.putJsonValue(json, Params.OWNER_ID, ownerId);
    Util.putJsonValue(json, Fields.ID, albumId);
  }

  Album(JSONObject json) {
    if (json == null) {
      throw new RuntimeException("cannot create an album out of a null json!");
    }
    this.json = json;
  }

  /**
   * @return the ID of the album. the albumId is guaranteed to be unique
   *     only among the albums for a given person.
   */
  public String getId() {
    return json.optString(Fields.ID);
  }

  /**
   * @return the title of the album
   */
  public String getTitle() {
    return json.optString(Fields.TITLE);
  }

  /**
   * @return the description of the album
   */
  public String getDescription() {
    return json.optString(Fields.DESCRIPTION);
  }

  /**
   * @return the url to the thumbnail of the album's cover image
   */
  public String getThumbnailUrl() {
    return json.optString(Fields.THUMBNAIL_URL);
  }

  /**
   * @return the ID of the owner of the album
   */
  public String getOwnerId() {
    return json.optString(Params.OWNER_ID);
  }

  /**
   * @deprecated use {@link AlbumsTxFactory#shareAlbumWithFriends(Album)}
   * @return the {@link AccessControlList} which grants access to the album
   */
  AccessControlList getAccessControlList() {
    return new AccessControlList(json.optJSONObject(Fields.ACL));
  }

  /**
   * @return the total number of photos in the album
   */
  public int getNumPhotos() {
    return json.optInt(Fields.MEDIA_ITEM_COUNT);
  }

  /**
   * Sets the title of the string. Follow this with {@link AlbumsTxFactory#updateAlbum(Album)} to
   * update an album.
   */
  public void setTitle(String title) {
    Util.putJsonValue(json, Fields.TITLE, title);
  }

  /**
   * Sets the description of the string. Follow this with {@link AlbumsTxFactory#updateAlbum(Album)} to
   * update an album.
   */
  public void setDescription(String desc) {
    Util.putJsonValue(json, Fields.DESCRIPTION, desc);
  }

  /**
   * Set an updated {@link AccessControlList}. Follow this with {@link AlbumsTxFactory#updateAlbum(Album)}
   * to update an album.
   * @deprecated use {@link AlbumsTxFactory#shareAlbumWithFriends(Album)}
   */
  void setAccessControlList(AccessControlList acl) {
    Util.putJsonValue(json, Fields.ACL, acl.getJson());
  }

  void setOwnerId(String ownerId) {
    Util.putJsonValue(json, Params.OWNER_ID, ownerId);
  }

  JSONObject getJson() {
    return json;
  }

  void setNumPhotos(int count) {
    Util.putJsonValue(json, Fields.MEDIA_ITEM_COUNT, count);
  }

  void setThumbnailUrl(String url) {
    Util.putJsonValue(json, Fields.THUMBNAIL_URL, url);
  }
}

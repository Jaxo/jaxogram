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

import com.google.orkut.client.api.UploadPhotoTx.ImageType;

/**
 * A factory to generate orkut photos related {@link Transaction}s.
 *
 * @author Shishir Birmiwal
 */
public class PhotosTxFactory {
  /**
   * Get photos for a given user and album.
   *
   * @return a {@link GetPhotosTx} instance that fetches the {@link Photo}s
   */
  public GetPhotosTx getPhotos(String userId, String albumId) {
    return new GetPhotosTx(userId, albumId);
  }

  /**
   * Get photos for a given user and album.
   *
   * @return a {@link GetPhotosTx} instance that fetches the {@link Photo}s
   */
  public GetPhotosTx getPhotos(Album album) {
    return new GetPhotosTx(album.getOwnerId(), album.getId());
  }

  /** Get photos on the current user's indicated album. */
  public GetPhotosTx getSelfPhotos(String albumId) {
    return new GetPhotosTx(Constants.USERID_ME, albumId);
  }

  /**
   * Get next set of Photos, given a completed transaction of photos.
   *
   * @return a {@link GetPhotosTx} instance that fetches upto {@code maxCount} photos
   */
  public GetPhotosTx getNextPhotos(GetPhotosTx prev) {
    return new GetPhotosTx(prev);
  }

  /**
   * Get a {@link Photo} given a photoId, userId and albumId.
   *
   * @return a {@link GetPhotosTx} instance to fetch the given photo
   */
  public GetPhotosTx getPhoto(String userId, String albumId, String photoId) {
    return new GetPhotosTx(userId, albumId, photoId);
  }

  /**
   * Get a {@link Photo} given a photoId and the {@link Album}.
   *
   * @return a {@link GetPhotosTx} instance to fetch the given photo
   */
  public GetPhotosTx getPhoto(Album album, String photoId) {
    return new GetPhotosTx(album.getOwnerId(), album.getId(), photoId);
  }

  /**
   * Update the title of the given photo. The title is set in the photo
   * using {@link Photo#setTitle(String)}.
   *
   * @return an {@link UpdatePhotoTx} instance that updates the photo
   */
  public UpdatePhotoTx updatePhoto(Photo photo) {
    return new UpdatePhotoTx(photo);
  }

  /**
   * Deletes a photo on orkut.
   *
   * @param photo the photo to delete.
   * @return a {@link DeletePhotoTx} instance to delete the photo
   */
  public DeletePhotoTx deletePhoto(Photo photo) {
    return new DeletePhotoTx(photo);
  }

  /**
   * Uploads a photo to an album on orkut.
   *
   * @param album the album in which to upload the photo
   * @param image the image bytes
   * @param type the type of the image; one from {@link ImageType}
   * @param title the title of the image
   * @return a {@link UpdatePhotoTx} instance to upload the photo
   */
  public UploadPhotoTx uploadPhoto(Album album, byte[] image, String type, String title) {
    return new UploadPhotoTx(album.getId(), image, type, title);
  }

  /**
   * Uploads a photo to an album on orkut.
   *
   * @param albumId the ID of the album in which to upload the photo
   * @param image the image bytes
   * @param type the type of the image; one from {@link ImageType}
   * @param title the title of the image
   * @return a {@link UpdatePhotoTx} instance to upload the photo
   */
  public UploadPhotoTx uploadPhoto(String albumId, byte[] image, String type, String title) {
    return new UploadPhotoTx(albumId, image, type, title);
  }

  /**
   * Uploads a photo to an album on orkut.
   *
   * @param albumId the ID of the album in which to upload the photo
   * @param jpegFile the path to the photo -- a local jpeg file 
   * @param title the title of the image
   * @return a {@link UpdatePhotoTx} instance to upload the photo
   */
  public UploadPhotoTx uploadPhoto(String albumId, String jpegFile, String title) 
					throws java.io.IOException {
    byte[] b = Util.loadFile(jpegFile);
    return new UploadPhotoTx(albumId, b, ImageType.JPG, title);
  }

}

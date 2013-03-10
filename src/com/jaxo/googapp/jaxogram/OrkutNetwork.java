/*
* $Id: $
*
* (C) Copyright 2013 Jaxo Inc.  All rights reserved.
* This work contains confidential trade secrets of Jaxo Inc.
* Use, examination, copying, transfer and disclosure to others
* are prohibited, except with the express written agreement of Jaxo.
*
* Author:  Pierre G. Richard
* Written: 1/6/2013
*/
package com.jaxo.googapp.jaxogram;

import java.util.logging.Logger;

import com.google.orkut.client.api.BatchTransaction;
import com.google.orkut.client.api.CreateAlbumTx;
import com.google.orkut.client.api.DefaultOrkutAdapter;
import com.google.orkut.client.api.GetAlbumsTx;
import com.google.orkut.client.api.GetProfileTx;
import com.google.orkut.client.api.OrkutPerson;
import com.google.orkut.client.api.UploadPhotoTx;

/*-- class OrkutNetwork --+
*//**
*
* @author  Pierre G. Richard
* @version $Id: $
*/
public class OrkutNetwork extends DefaultOrkutAdapter implements Network
{
   static final boolean IS_DEBUG = false;
   static Logger logger = Logger.getLogger("com.jaxo.googapp.jaxogram.OrkutNetwork");
   static final String consumerKey = "www.jaxo.com";
   static final String consumerSecret = "JYd4FdgQyQBvbgbq0rdDP44C";

   /*------------------------------------------------------------OrkutNetwork-+
   *//**
   *//*
   +-------------------------------------------------------------------------*/
   public OrkutNetwork() throws Exception {
      super(consumerKey, consumerSecret);
   }

   /*------------------------------------------------------------OrkutNetwork-+
   *//**
   *//*
   +-------------------------------------------------------------------------*/
   public OrkutNetwork(String accessPass) throws Exception {
      super(consumerKey, consumerSecret);
      setAccessPass(accessPass);
   }

   /*-------------------------------------------------------------whoIsAsJson-+
   *//**
   *//*
   +-------------------------------------------------------------------------*/
   public String whoIsAsJson(String id) throws Exception {
      BatchTransaction btx = newBatch();
      GetProfileTx tx;
      if (id == null) {
         tx = getProfileTF().getSelfProfile();
      }else {
         tx = getProfileTF().getProfileOf(id);
      }
      tx.alsoGetName();
      tx.alsoGetThumbnailUrl();
      tx.alsoGetProfileUrl();
      tx.alsoGetStatus();
      tx.alsoGetEmails();
      tx.alsoGetGender();
      tx.alsoGetPhoneNumbers();
      tx.alsoGetBirthday();
      tx.alsoGetAddress();
      btx.add(tx);
      submitBatch(btx);
      if (tx.hasError()) {
         throw new Exception("Error fetching full profile:" + tx.getError());
      }else {
         OrkutPerson person = tx.getProfile();
         if (person == null) {
            throw new Exception("No profile data returned.");
         }else {
            return person.toJsonString();
         }
      }
   }

   /*--------------------------------------------------------listAlbumsAsJson-+
   *//**
   *//*
   +-------------------------------------------------------------------------*/
   public String listAlbumsAsJson() throws Exception {
      GetAlbumsTx tx = getAlbumsTF().getSelfAlbums();
      tx.setCount(20);  // get first 20 albums
      BatchTransaction btx = newBatch();
      btx.add(tx);
      submitBatch(btx);

      if (tx.hasError()) {
         throw new Exception("Error listing albums: " + tx.getError());
      }else {
         StringBuilder sb = new StringBuilder();
         sb.append('[');
         for (int i=0, count=tx.getAlbumCount(); i < count; ++i) {
            if (i > 0) sb.append(',');
            sb.append(tx.getAlbum(i).toJsonString());
         }
         sb.append(']');
         return sb.toString();
      }
   }

   /*-------------------------------------------------------------createAlbum-+
   *//**
   *//*
   +-------------------------------------------------------------------------*/
   public void createAlbum(String title, String desc) throws Exception {
      CreateAlbumTx tx = getAlbumsTF().createAlbum(title, desc);
      BatchTransaction btx = newBatch();
      btx.add(tx);
      submitBatch(btx);
      if (tx.hasError()) {
         throw new Exception("Error creating album: " + tx.getError());
      }
   }

   /*-------------------------------------------------------------uploadPhoto-+
   *//**
   *//*
   +-------------------------------------------------------------------------*/
   public String uploadPhoto(
      String albumId,
      String title,
      byte[] image,
      String type // png, gif, or jpg.  see: ~/fxos/orkut/java/src/com/google/orkut/client/api/UploadPhotoTx.java
   )
   throws Exception
   {
      UploadPhotoTx tx = getPhotosTF().uploadPhoto(albumId, image, type, title);
      BatchTransaction btx = newBatch();
      btx.add(tx);
      submitBatch(btx);
      if (tx.hasError()) {
         throw new Exception("Error uploading photo:" + tx.getError());
      }
      return null;
   }

   /*---------------------------------------------------------------------say-+
   *//**
   *//*
   +-------------------------------------------------------------------------*/
   protected void say(String s) {
      if (IS_DEBUG) logger.info(s);
   }

// public void uploadPhoto(
//    String albumId,  // album ID
//    String filePath, // path to the JPG file
//    String title     // title of the photo
// ) throws Exception {
//    UploadPhotoTx tx = getPhotosTF().uploadPhoto(albumId, filePath, title);
//    BatchTransaction btx = newBatch();
//    btx.add(tx);
//    submitBatch(btx);
//    if (tx.hasError()) {
//       throw new Exception("Error uploading photo:" + tx.getError());
//    }
// }
//
// public void listFriends() throws Exception {
//    BatchTransaction btx = newBatch();
//    GetFriendTx friends = getFriendTF().getSelfFriends();
//    friends.setCount(100); // get a maximum of 100 friends
//    btx.add(friends);
//    submitBatch(btx);
//    if (friends.hasError()) {
//       throw new Exception("Error fetching friends: " + friends.getError());
//    }else {
//       for (int i=0, count=friends.getFriendsCount(); i < count; ++i) {
//          OrkutPerson f = friends.getFriend(i);
//          say("- " + f.getDisplayName() + " (id: " + f.getId() + ")");
//       }
//    }
// }
//
// public void updateStatus(String newStatus) throws Exception {
//    UpdateProfileTx tx = getProfileTF().updateSelfProfile();
//    tx.setStatus(newStatus);
//    BatchTransaction btx = newBatch();
//    btx.add(tx);
//    submitBatch(btx);
//    if (tx.hasError()) {
//       throw new Exception("Error updating profile! " + tx.getError());
//    }
// }
//
// public void listActivities() throws Exception {
//    BatchTransaction btx = newBatch();
//    GetActivitiesTx activities = getActivityTF().getSelfActivities();
//    btx.add(activities);
//    submitBatch(btx);
//    if (activities.hasError()) {
//       throw new Exception("Error fetching activities:" + activities.getError());
//    }else {
//       for (int page=0;; )  {
//          say("Page: " + (++page));
//          for (int i=0; i < activities.getActivityCount(); ++i) {
//             ActivityEntry entry = activities.getActivity(i);
//             say(entry.toString());
//          }
//          if (!activities.hasNext()) break;
//          activities = getActivityTF().getNext(activities);
//          btx = newBatch();
//          btx.add(activities);
//          submitBatch(btx);
//       }
//    }
// }
//
// public void getFullProfile(String id) throws Exception {
//    BatchTransaction btx = newBatch();
//    GetProfileTx tx;
//    if (id == null) {
//       tx = getProfileTF().getSelfProfile();
//    }else {
//       tx = getProfileTF().getProfileOf(id);
//    }
//    tx.alsoGetName();
//    tx.alsoGetThumbnailUrl();
//    tx.alsoGetProfileUrl();
//    tx.alsoGetStatus();
//    tx.alsoGetEmails();
//    tx.alsoGetGender();
//    tx.alsoGetPhoneNumbers();
//    tx.alsoGetBirthday();
//    tx.alsoGetAddress();
//    btx.add(tx);
//    submitBatch(btx);
//    if (tx.hasError()) {
//       throw new Exception("Error fetching full profile:" + tx.getError());
//    }else {
//       OrkutPerson person = tx.getProfile();
//       if (person == null) {
//          throw new Exception("No profile data returned.");
//       }else {
//          say("ID                    : " + nullsafe(person.getId()));
//          say("Given name            : " + nullsafe(person.getGivenName()));
//          say("Family name           : " + nullsafe(person.getFamilyName()));
//          say("Display name          : " + nullsafe(person.getDisplayName()));
//          say("Thumbnail URL         : " + nullsafe(person.getThumbnailUrl()));
//          say("Status                : " + nullsafe(person.getStatus()));
//          say("Gender                : " + nullsafe(person.getGender()));
//          say("Profile URL           : " + nullsafe(person.getProfileUrl()));
//          say("Birthday (long)       : " + String.valueOf(person.getBirthday()));
//          com.google.orkut.client.api.Address address = person.getAddress();
//          if (address != null) {
//             say("Address               : ");
//             say("       Country code   : " + nullsafe(address.getCountryCode()));
//             say("       Region code    : " + nullsafe(address.getRegion()));
//             say("       Locality       : " + nullsafe(address.getLocality()));
//             say("       Postal Code    : " + nullsafe(address.getPostalCode()));
//          }else {
//             say("Address                  : [not returned]");
//          }
//          say("E-Mails:");
//          for (int i=0, count=person.getEmailCount(); i < count; ++i) {
//             say("  - " + nullsafe(person.getEmail(i)));
//          }
//          say("Phones:");
//          for (int i=0, count=person.getPhoneNumberCount(); i < count; ++i) {
//             say("  - " + nullsafe(person.getPhoneNumber(i)));
//          }
//       }
//    }
// }
//
// public void getBirthdayNotifications() throws Exception {
//    BirthdayNotificationTx tx = getFriendTF().getBirthdayNotification();
//    BatchTransaction btx = newBatch();
//    btx.add(tx);
//    submitBatch(btx);
//    if (tx.hasError()) {
//        throw new Exception("Error fetching birthday notifs:" + tx.getError());
//    }else {
//       for (int i=0; i < tx.getBirthdayNotificationCount(); ++i) {
//          BirthdayNotificationEntry entry = tx.getBirthdayNotification(i);
//          say(
//             "User: " + entry.getUserProfile().getDisplayName() +
//             " (" + entry.getUserId() +
//             ") Birthdate: " + entry.getBirthDay() +
//             "/" + entry.getBirthMonth()
//          );
//       }
//    }
// }
//
// public void postNewActivity(String title, String body) throws Exception {
//    PostActivityTx tx = getActivityTF().postActivity(title,body);
//    BatchTransaction btx = newBatch();
//    btx.add(tx);
//    submitBatch(btx);
//    if (tx.hasError()) {
//       say("*** Error posting activity:" + tx.getError());
//    }
// }
//
// public void getScraps() throws Exception {
//    BatchTransaction btx = newBatch();
//    GetScrapsTx tx = getScrapTF().getSelfScraps();
//    tx.setMessageFormat(GetScrapsTx.MessageFormat.FULL_HTML);
//    tx.setCount(20);   // Getting first 20 scraps...
//    btx.add(tx);
//    submitBatch(btx);
//    if (tx.hasError()) {
//       throw new Exception("Error fetching scraps:" + tx.getError());
//    }else {
//       say(String.valueOf(tx.getScrapCount()) + " scrap(s) returned");
//       for (int i=0, count=tx.getScrapCount(); i < count; ++i) {
//          ScrapEntry se = tx.getScrap(i);
//          String id = nullsafe(se.getId());
//          String fromName;
//          if (se.hasFromUserProfile()) {
//             fromName = nullsafe(se.getFromUserProfile().getDisplayName());
//          }else {
//             fromName = "n/a";
//          }
//          say("Scrap #" + i + " (id " + id + "):");
//          say("From: " + fromName);
//          say("Body: " + nullsafe(se.getBody()));
//       }
//    }
// }
//
// public void writeScrap(
//    String personId,   // writing the scrap to?
//    String scrapId,    // id of the scrap you are replying to? (or null)
//    String body        // body text
// ) throws Exception {
//    WriteScrapTx tx;
//    if (scrapId != null) {
//       tx = getScrapTF().replyToScrap(personId,scrapId,body);
//    }else {
//       tx = getScrapTF().writeScrap(personId,body);
//    }
//    BatchTransaction btx = newBatch();
//    btx.add(tx);
//    submitBatch(btx);
//    if (tx.hasError()) {
//       OrkutError error = tx.getError();
//       if ((error == null) || !error.isCaptchaError()) {
//          throw new Exception("Error sending scrap:" + error);
//       }else {
//          say("Captcha solving is required... Downloading captcha.");
//          saveCaptchaToFile(error, "captcha.jpg");
//          submitBatchWithCaptcha(
//             btx, error, "text on the image captcha.jpg"
//          );
//       }
//    }
// }
//
// public String listAlbums() throws Exception {
//    GetAlbumsTx tx = getAlbumsTF().getSelfAlbums();
//    tx.setCount(20);  // get first 20 albums
//    BatchTransaction btx = newBatch();
//    btx.add(tx);
//    submitBatch(btx);
//
//    if (tx.hasError()) {
//       throw new Exception("Error listing albums: " + tx.getError());
//    }else {
//       StringBuilder sb = new StringBuilder();
//       for (int i=0, count=tx.getAlbumCount(); i < count; ++i) {
//          Album album = tx.getAlbum(i);
//          sb.append("Album Title  : ").append(nullsafe(album.getTitle())).append("\n");
//          sb.append("Description  : ").append(nullsafe(album.getDescription())).append("\n");
//          sb.append("Thumbnail URL: ").append(nullsafe(album.getThumbnailUrl())).append("\n");
//          sb.append("Album ID     : ").append(nullsafe(album.getId())).append("\n");
//          sb.append("Owner ID     : ").append(nullsafe(album.getOwnerId())).append("\n");
//       }
//       return sb.toString();
//    }
// }
//
// public void updateAlbum(
//    String albumId,  // album ID
//    String newTitle, // new title
//    String newDesc   // new description
// ) throws Exception {
//    GetAlbumsTx tx = getAlbumsTF().getSelfAlbum(albumId);
//    BatchTransaction btx = newBatch();
//    btx.add(tx);
//    submitBatch(btx);
//    if (tx.hasError()) {
//       throw new Exception("Failed to get album: " + tx.getError());
//    }else if (tx.getAlbumCount() != 1) {
//       throw new Exception(
//          "Expected count 1, found " + tx.getAlbumCount()
//       );
//    }else {
//       Album album = tx.getAlbum(0);
//       album.setTitle(newTitle);
//       album.setDescription(newDesc);
//       btx = newBatch();
//       UpdateAlbumTx utx = getAlbumsTF().updateAlbum(album);
//       btx.add(utx);
//       submitBatch(btx);
//       if (utx.hasError()) {
//          throw new Exception("Error updating album: " + utx.getError());
//       }
//    }
// }
//
// public void deleteAlbum(String albumId) throws Exception {
//    DeleteAlbumTx tx = getAlbumsTF().deleteAlbum(albumId);
//    BatchTransaction btx = newBatch();
//    btx.add(tx);
//    submitBatch(btx);
//    if (tx.hasError()) {
//       throw new Exception("Error deleting album: " + tx.getError());
//    }
// }
//
// public void listPhotos(String albumId) throws Exception {
//    GetPhotosTx tx = getPhotosTF().getSelfPhotos(albumId);
//    BatchTransaction btx = newBatch();
//    tx.setCount(20); // get up to 20 photos
//    btx.add(tx);
//    submitBatch(btx);
//    if (tx.hasError()) {
//       throw new Exception("Error getting photos:" + tx.getError());
//    }else {
//       for (int i=0, count=tx.getPhotoCount(); i < count ; ++i) {
//          Photo p = tx.getPhoto(i);
//          say("Photo ID        : " + p.getId());
//          say("Photo Title     : " + p.getTitle());
//          say("Photo URL       : " + p.getUrl());
//          say("Thumbnail URL   : " + p.getThumbnailUrl());
//          say("");
//       }
//    }
// }
//
// private static String nullsafe(String s) {
//    return s == null ? "(null)" : s;
// }
}
/*===========================================================================*/

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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.oauth.OAuth;
import net.oauth.OAuthAccessor;
import net.oauth.OAuthConsumer;
import net.oauth.OAuthMessage;

import com.google.orkut.client.api.AlbumsTxFactory;
import com.google.orkut.client.api.BatchTransaction;
import com.google.orkut.client.api.CreateAlbumTx;
import com.google.orkut.client.api.GetAlbumsTx;
import com.google.orkut.client.api.GetProfileTx;
import com.google.orkut.client.api.OrkutPerson;
import com.google.orkut.client.api.PhotosTxFactory;
import com.google.orkut.client.api.ProfileTxFactory;
import com.google.orkut.client.api.UploadPhotoTx;
import com.google.orkut.client.transport.HttpRequest;
import com.google.orkut.client.transport.OrkutHttpRequestFactory;
//*/ import java.util.logging.Logger;

/*-- class OrkutNetwork --+
*//**
*
* @author  Pierre G. Richard
* @version $Id: $
*/
public class OrkutNetwork extends OAuthorizer implements Network
{
   //*/ private static Logger logger = Logger.getLogger("com.jaxo.googapp.jaxogram.OrkutNetwork");
   private static final String CONSUMER_KEY = "www.jaxo.com";
   private static final String CONSUMER_SECRET = "JYd4FdgQyQBvbgbq0rdDP44C";
   private static final String OAUTH_REQUEST_URL = "https://www.google.com/accounts/OAuthGetRequestToken";
   private static final String OAUTH_AUTHORIZATION_URL = "https://www.google.com/accounts/OAuthAuthorizeToken";
   private static final String OAUTH_ACCESS_URL = "https://www.google.com/accounts/OAuthGetAccessToken";

   private static final String OAUTH_SCOPE = "http://orkut.gmodules.com/social";
   private static final String SERVER_URL = "http://www.orkut.com/social/rpc";

   private final ProfileTxFactory profileTxFactory = new ProfileTxFactory();
   private final AlbumsTxFactory albumsTxFactory = new AlbumsTxFactory();
   private final PhotosTxFactory photosTxFactory = new PhotosTxFactory();

   /*------------------------------------------------------------OrkutNetwork-+
   *//**
   *//*
   +-------------------------------------------------------------------------*/
   public OrkutNetwork() throws Exception {
      super(
         CONSUMER_KEY,
         CONSUMER_SECRET,
         OAUTH_REQUEST_URL,
         OAUTH_AUTHORIZATION_URL,
         OAUTH_ACCESS_URL
      );
   }

   /*------------------------------------------------------------OrkutNetwork-+
   *//**
   *//*
   +-------------------------------------------------------------------------*/
   public OrkutNetwork(String accessPass) throws Exception {
      this();
      String[] p = accessPass.split(" ");
      if (p.length != 2) {
         throw new Exception(
            "Access pass does not have correct format (token and secret)"
         );
      }
      accessor.accessToken = p[0];
      accessor.tokenSecret = p[1];
   }

   /*----------------------------------------------------------requestAuthURL-+
   *//**
   *//*
   +-------------------------------------------------------------------------*/
   public String requestAuthURL(String callbackUrl) throws Exception
   {
      List<OAuth.Parameter> callback = OAuth.newList(
         OAuth.OAUTH_CALLBACK, callbackUrl, "scope", OAUTH_SCOPE
      );
      OAuthMessage response = client.getRequestTokenResponse(
         accessor, null, callback
      );
      String authorizationURL = OAuth.addParameters(
         accessor.consumer.serviceProvider.userAuthorizationURL,
         OAuth.OAUTH_TOKEN, accessor.requestToken,
         "scope", OAUTH_SCOPE
      );
      if (response.getParameter(OAuth.OAUTH_CALLBACK_CONFIRMED) == null) {
         authorizationURL = OAuth.addParameters(authorizationURL, callback);
      }else {
         authorizationURL = OAuth.addParameters(
            accessor.consumer.serviceProvider.userAuthorizationURL,
            OAuth.OAUTH_TOKEN, accessor.requestToken,
            "scope", OAUTH_SCOPE
         );
      }
      return authorizationURL;
   }

   /*------------------------------------------------------------authenticate-+
   *//**
   *//*
   +-------------------------------------------------------------------------*/
   public String[] authenticate(String verifier, OAuthAccessor givenAccessor)
   throws Exception
   {
      String userName;
      client.getAccessToken(
         givenAccessor, null,
         OAuth.newList(OAuth.OAUTH_VERIFIER, verifier)
      );
      accessor.accessToken = givenAccessor.accessToken;
      accessor.tokenSecret = givenAccessor.tokenSecret;
      BatchTransaction btx = newBatch();
      GetProfileTx profile = profileTxFactory.getSelfProfile();
      btx.add(profile);
      submitBatch(btx);
      if (profile.hasError()) {
         userName = "???";
      }else {
         OrkutPerson person = profile.getProfile();
         userName = person.getGivenName() + " " + person.getFamilyName();
      }
      return new String[] {
         givenAccessor.accessToken + " " + givenAccessor.tokenSecret,
         userName
      };
   }

   /*-------------------------------------------------------------whoIsAsJson-+
   *//**
   *//*
   +-------------------------------------------------------------------------*/
   public String whoIsAsJson(String id) throws Exception {
      BatchTransaction btx = newBatch();
      GetProfileTx tx;
      if (id == null) {
         tx = profileTxFactory.getSelfProfile();
      }else {
         tx = profileTxFactory.getProfileOf(id);
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
      GetAlbumsTx tx = albumsTxFactory.getSelfAlbums();
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
      CreateAlbumTx tx = albumsTxFactory.createAlbum(title, desc);
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
      UploadPhotoTx tx = photosTxFactory.uploadPhoto(albumId, image, type, title);
      BatchTransaction btx = newBatch();
      btx.add(tx);
      submitBatch(btx);
      if (tx.hasError()) {
         throw new Exception("Error uploading photo:" + tx.getError());
      }
      return null;
   }

   /*----------------------------------------------------------------newBatch-+
   *//**
   *//*
   +-------------------------------------------------------------------------*/
   private BatchTransaction newBatch() throws Exception {
      return new BatchTransaction(
         new OrkutHttpRequestFactory(),
         new com.google.orkut.client.config.Config() {
            public String getRequestBaseUrl() { return SERVER_URL; }
         }
      );
   }

   /*-------------------------------------------------------------submitBatch-+
   *//**
   *//*
   +-------------------------------------------------------------------------*/
   @SuppressWarnings({ "rawtypes", "unchecked" })
   private void submitBatch(BatchTransaction btx) throws Exception
   {
      HttpRequest req = btx.build();
      byte[] body = req.getRequestBody();
      String method = req.getMethod();
      String baseURL = req.getRequestBaseUrl();
      Collection reqParams = req.getParameters();
      ArrayList<Map.Entry<String,String>> oauthParams = null;
      if (reqParams != null && reqParams.size() > 0) {
         oauthParams = new ArrayList<Map.Entry<String,String>>();
         Iterator it = reqParams.iterator();
         while (it.hasNext()) {
            HttpRequest.Parameter parameter = (
               (HttpRequest.Parameter)it.next()
            );
            oauthParams.add(
               new OAuth.Parameter(parameter.getKey(), parameter.getValue())
            );
         }
      }
      Collection reqHeaders = req.getHeaders();
      ArrayList<Map.Entry<String,String>> oauthHeaders = null;
      if (reqHeaders != null && reqHeaders.size() > 0) {
         oauthHeaders = new ArrayList<Map.Entry<String,String>>();
         Iterator it = reqHeaders.iterator();
         while (it.hasNext()) {
            HttpRequest.Header header = (HttpRequest.Header) it.next();
            oauthHeaders.add(
               new OAuth.Parameter(header.getName(), header.getValue())
            );
         }
      }
      OAuthMessage msg = new PostOAuthMessage(
         method, baseURL, oauthParams, body
      );
      msg.addRequiredParameters(accessor);
      Iterator it = oauthHeaders.iterator();
      while (it.hasNext()) {
         msg.getHeaders().add((Map.Entry)it.next());
      }
      Object accepted = accessor.consumer.getProperty(
          OAuthConsumer.ACCEPT_ENCODING
      );
      if (accepted != null) {
         msg.getHeaders().add(
            new OAuth.Parameter(
               net.oauth.http.HttpMessage.ACCEPT_ENCODING,
               accepted.toString()
            )
         );
      }
      OAuthMessage resp = client.invoke(
         msg, net.oauth.ParameterStyle.QUERY_STRING
      );
      btx.setResponse(resp.readBodyAsString());
   }

   /*------------------------------------------------ class PostOAuthMessage -+
   *//**
   *//*
   +-------------------------------------------------------------------------*/
   class PostOAuthMessage extends OAuthMessage {
      private final byte[] body;
      @SuppressWarnings("rawtypes")
      public PostOAuthMessage(
         String method,
         String url,
         Collection<? extends Map.Entry> parameters,
         byte[] body
      ) {
         super(method, url, parameters);
         this.body = body;
      }

      public InputStream getBodyAsStream() throws IOException {
         return (body == null) ? null : new ByteArrayInputStream(body);
      }
   }

// public void uploadPhoto(
//    String albumId,  // album ID
//    String filePath, // path to the JPG file
//    String title     // title of the photo
// ) throws Exception {
//    UploadPhotoTx tx = photosTxFactory.uploadPhoto(albumId, filePath, title);
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
//    UpdateProfileTx tx = profileTxFactory.updateSelfProfile();
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
//       tx = profileTxFactory.getSelfProfile();
//    }else {
//       tx = profileTxFactory.getProfileOf(id);
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
//    GetAlbumsTx tx = albumsTxFactory.getSelfAlbums();
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
//    GetAlbumsTx tx = albumsTxFactory.getSelfAlbum(albumId);
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
//       UpdateAlbumTx utx = albumsTxFactory.updateAlbum(album);
//       btx.add(utx);
//       submitBatch(btx);
//       if (utx.hasError()) {
//          throw new Exception("Error updating album: " + utx.getError());
//       }
//    }
// }
//
// public void deleteAlbum(String albumId) throws Exception {
//    DeleteAlbumTx tx = albumsTxFactory.deleteAlbum(albumId);
//    BatchTransaction btx = newBatch();
//    btx.add(tx);
//    submitBatch(btx);
//    if (tx.hasError()) {
//       throw new Exception("Error deleting album: " + tx.getError());
//    }
// }
//
// public void listPhotos(String albumId) throws Exception {
//    GetPhotosTx tx = photosTxFactory.getSelfPhotos(albumId);
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

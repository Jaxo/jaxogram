/*
* $Id: $
*
* (C) Copyright 2011 Jaxo Inc.  All rights reserved.
* This work contains confidential trade secrets of Jaxo Inc.
* Use, examination, copying, transfer and disclosure to others
* are prohibited, except with the express written agreement of Jaxo.
*
* Author:  Pierre G. Richard
* Written: 2/27/2013
*/
package com.jaxo.googapp.jaxogram;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.gdata.client.photos.PicasawebService;
import com.google.gdata.data.Kind.AdaptorException;
import com.google.gdata.data.Link;
import com.google.gdata.data.OtherContent;
import com.google.gdata.data.Person;
import com.google.gdata.data.PlainTextConstruct;
import com.google.gdata.data.media.mediarss.MediaGroup;
import com.google.gdata.data.media.mediarss.MediaThumbnail;
import com.google.gdata.data.photos.AlbumEntry;
import com.google.gdata.data.photos.AlbumFeed;
import com.google.gdata.data.photos.GphotoEntry;
import com.google.gdata.data.photos.GphotoFeed;
import com.google.gdata.data.photos.PhotoEntry;
import com.google.gdata.data.photos.UserFeed;
import com.google.gdata.util.AuthenticationException;
import com.google.gdata.util.ContentType;
import com.google.gdata.util.ServiceException;

/*-- class PicasaNetwork --+
*//**
*
* @author  Pierre G. Richard
* @version $Id: $
*/
public class PicasaNetwork {
   private static final String API_PREFIX = (
      "http://picasaweb.google.com/data/feed/api/user/"
   );
   private PicasawebService m_service;
   private String m_login;

   /*-----------------------------------------------------------PicasaNetwork-+
   *//**
   *//*
   +-------------------------------------------------------------------------*/
   public PicasaNetwork(String loginPasswd) throws AuthenticationException {
      int splitAt = loginPasswd.indexOf(' ');
      m_login = loginPasswd.substring(0, splitAt);
      m_service = new PicasawebService("Jaxogram_9");
      m_service.setConnectTimeout(30000);
      m_service.setUserCredentials(m_login, loginPasswd.substring(splitAt+1));
   }

   /*------------------------------------------------------------------whoAmI-+
   *//**
   *//*
   +-------------------------------------------------------------------------*/
   public String whoAmI() throws Exception {
      return (
         (List<Person>)m_service.getFeed(
            new URL(API_PREFIX + m_login + "?kind=album"),
            UserFeed.class
         ).getAuthors()
      ).get(0).getName();
   }

   /*-------------------------------------------------------------whoIsAsJson-+
   *//**
   *//*
   +-------------------------------------------------------------------------*/
   public String whoIsAsJson(String id) throws Exception {
      if (id != null) {
         throw new Exception("Not Implemented");
      }else {
         Person person = (
            (List<Person>)m_service.getFeed(
               new URL(API_PREFIX + m_login + "?kind=album"),
               UserFeed.class
            ).getAuthors()
         ).get(0);
         StringBuilder sb = new StringBuilder();
         sb.append("{\"name\":{\"givenName\":\"").
         append(person.getName()).
         append("\",\"familyName\":\" \"},").
//       append("\"birthday\":\"\",").
         append("\"gender\":\"\"}");
         return sb.toString();
      }
   }

   /*--------------------------------------------------------listAlbumsAsJson-+
   *//**
   *//*
   +-------------------------------------------------------------------------*/
   public String listAlbumsAsJson() throws Exception {
      StringBuilder sb = new StringBuilder();
      int albumsCount = -1;
      sb.append("[");
      for (AlbumEntry album : getAlbums()) {
         if (++albumsCount > 0) sb.append(',');
         albumToJson(album, sb);
      }
      sb.append("]");
      return sb.toString();
   }


   /*-------------------------------------------------------createAlbumAsJson-+
   *//**
   *//*
   +-------------------------------------------------------------------------*/
   public String createAlbumAsJson(
      String title,  // Album title
      String desc    // Album description
   ) throws Exception {
      AlbumEntry album = new AlbumEntry();
      album.setTitle(new PlainTextConstruct(title));
      album.setDescription(new PlainTextConstruct(desc));
      album.setAccess("public");
//    album.setLocation(location);
      album.setDate(new Date());
      m_service.insert(new URL(API_PREFIX + "default"), album);
      return listAlbumsAsJson();
   }

   /*-------------------------------------------------------------uploadPhoto-+
   *//**
   *//*
   +-------------------------------------------------------------------------*/
   public void uploadPhoto(
      String albumId,
      String title,
      byte[] image,
      String type // png, gif, jpg or jpeg
   )
   throws Exception
   {
//    URL albumUrl = new URL(API_PREFIX + m_login + "/" + albumId);
      for (AlbumEntry album : getAlbums()) {
         String id = album.getId();
         if (id.endsWith(albumId)) {
            PhotoEntry photo = new PhotoEntry();
            photo.setTitle(new PlainTextConstruct(title));
            // photo.setDescription(new PlainTextConstruct(description));
            photo.setTimestamp(new Date());
            OtherContent content = new OtherContent();
            content.setBytes(image);
            content.setMimeType(new ContentType("image/" + type));
            photo.setContent(content);
//          m_service.insert(albumUrl, photo);
            insert(album, photo);
            break;
         }
      }
   }

   /*-----------------------------------------------------listAllPhotosAsJson-+
   *//**
   *//*
   +-------------------------------------------------------------------------*/
   public String listAllPhotosAsJson() throws Exception
   {
      StringBuilder sb = new StringBuilder();

      int albumsCount = 0;
      sb.append("{\"albums\":[");
      for (AlbumEntry album : getAlbums()) {
         if (albumsCount > 0) sb.append(',');
         sb.append("{\"no\":\"").append(++albumsCount).
         append("\",\"title\":\"").append(album.getTitle().getPlainText()).
         append("\",\"photos\":[");
         List<PhotoEntry> photos = getPhotos(album);
         int photosCount = 0;
         for (PhotoEntry photo : photos) {
            if (photosCount > 0) sb.append(',');
            sb.
            append("{\"no\":\"").append(++photosCount).
            append("\",\"title\":\"").append(photo.getTitle().getPlainText()).
            append("\",\"descr\":\"").append(photo.getDescription().getPlainText()).
            append("\"}");
         }
         sb.append("]}");
      }
      sb.append("]}");
      return sb.toString();
   }

   /*---------------------------------------------------------------getAlbums-+
   *//**
   *//*
   +-------------------------------------------------------------------------*/
   public List<AlbumEntry> getAlbums() throws IOException, ServiceException
   {
      return m_service.getFeed(
         new URL(API_PREFIX + m_login + "?kind=album"),
         UserFeed.class
      ).getAlbumEntries();
   }

   /*---------------------------------------------------------------getPhotos-+
   *//**
   * Retrieves the photos for the given album.
   *//*
   +-------------------------------------------------------------------------*/
   @SuppressWarnings("rawtypes")
   public List<PhotoEntry> getPhotos(AlbumEntry album)
   throws IOException, ServiceException, AdaptorException
   {
      String feedHref = getLinkByRel(album.getLinks(), Link.Rel.FEED);
      AlbumFeed albumFeed = getFeed(feedHref, AlbumFeed.class);
      List<GphotoEntry> entries = albumFeed.getEntries();
      List<PhotoEntry> photos = new ArrayList<PhotoEntry>();
      for (GphotoEntry entry : entries) {
         GphotoEntry adapted = entry.getAdaptedEntry();
         if (adapted instanceof PhotoEntry) {
            photos.add((PhotoEntry) adapted);
         }
      }
      return photos;
   }

   /*-------------------------------------------------------------albumToJson-+
   *//**
   *//*
   +-------------------------------------------------------------------------*/
   private void albumToJson(AlbumEntry album, StringBuilder sb) {
      MediaGroup grp = album.getMediaGroup();
      String thumbnailUrl = "";
      if (grp != null) {
         List<MediaThumbnail> thumbnails = grp.getThumbnails();
         for (MediaThumbnail thumbnail : thumbnails) {
            thumbnailUrl = thumbnail.getUrl();
            int ixEnd = thumbnailUrl.lastIndexOf('/');
            int ixStart = thumbnailUrl.lastIndexOf('/', ixEnd-1);
            thumbnailUrl = (
               thumbnailUrl.substring(0, ixStart+1) +
               "s32" +  // s32-c (cropped?)
               thumbnailUrl.substring(ixEnd)
            );
            break;
         }
      }
      String id = album.getId();
      sb.append("{\"id\":\"").
      append(id.substring(1 + id.lastIndexOf('/'))).
      append("\",\"title\":\"").append(album.getTitle().getPlainText()).
      append("\",\"description\":\"").
      append(album.getDescription().getPlainText()).
      append("\",\"thumbnailUrl\":\"").append(thumbnailUrl).
      append("\"}");
   }

   /*------------------------------------------------------------------insert-+
   *//**
   * Insert an entry into another entry.  Because our entries are a hierarchy,
   * this lets you insert a photo into an album even if you only have the
   * album entry and not the album feed, making it quicker to traverse the
   * hierarchy.
   *//*
   +-------------------------------------------------------------------------*/
   @SuppressWarnings("rawtypes")
   public <T extends GphotoEntry> T insert(GphotoEntry<?> parent, T entry)
   throws IOException, ServiceException {
      String feedUrl = getLinkByRel(parent.getLinks(), Link.Rel.FEED);
      return m_service.insert(new URL(feedUrl), entry);
   }

   /*-----------------------------------------------------------------getFeed-+
   *//**
   * Helper function to allow retrieval of a feed by string url, which will
   * create the URL object for you.  Most of the Link objects have a string
   * href which must be converted into a URL by hand, this does the conversion.
   *//*
   +-------------------------------------------------------------------------*/
   @SuppressWarnings("rawtypes")
   public <T extends GphotoFeed> T getFeed(String feedHref, Class<T> feedClass)
   throws IOException, ServiceException {
      System.out.println("Get Feed URL: " + feedHref);
      return m_service.getFeed(new URL(feedHref), feedClass);
   }

   /*------------------------------------------------------------getLinkByRel-+
   *//**
   * Helper function to get a link by a rel value.
   *//*
   +-------------------------------------------------------------------------*/
   public String getLinkByRel(List<Link> links, String relValue) {
      for (Link link : links) {
         if (relValue.equals(link.getRel())) {
            return link.getHref();
         }
      }
      throw new IllegalArgumentException("Missing " + relValue + " link.");
   }
}
/*===========================================================================*/

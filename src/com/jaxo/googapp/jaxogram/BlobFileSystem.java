/*
* $Id: $
*
* (C) Copyright 2013 Jaxo Inc.  All rights reserved.
* This work contains confidential trade secrets of Jaxo Inc.
* Use, examination, copying, transfer and disclosure to others
* are prohibited, except with the express written agreement of Jaxo.
*
* Author:  Pierre G. Richard
* Written: 5/14/2013
*/
package com.jaxo.googapp.jaxogram;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.files.AppEngineFile;
import com.google.appengine.api.files.FileService;
import com.google.appengine.api.files.FileServiceFactory;
import com.google.appengine.api.files.FileWriteChannel;

// Storage: /home/pgr/jaxoapps/jaxogram/war/WEB-INF/appengine-generated/local_db.bin
// http://localhost:8888/jaxogram?OP=blob&ACT=store&CNT=blahblah3&FN=BlobTest.txt
// http://localhost:8888/jaxogram?OP=blob&ACT=serve&FN=BlobTest.txt
// <BlobKey: 1DL_IpOaqTpKvhfVSA8CyA>
// Proof that the following is wrong:
// http://stackoverflow.com/questions/12065430/finding-a-blob-in-google-appengines-blobstore-with-blobkey
class BlobFileSystem
{
   public static void write(String data, String fileName, String mimeType)
   throws Exception
   {
      FileService fileService = FileServiceFactory.getFileService();
      delete(fileName);  // no rewrite allowed in the blobstore
      AppEngineFile file = fileService.createNewBlobFile(mimeType, fileName);
      FileWriteChannel writeChannel = fileService.openWriteChannel(file, true);
      writeChannel.write(ByteBuffer.wrap(data.getBytes()));
      writeChannel.closeFinally();
   }

   public static void read(String fileName, HttpServletResponse response)
   throws IOException
   {
      BlobKey blobKey = getBlobKey(fileName);
      if (blobKey != null) {
         //response.addHeader(
         //   "content-disposition", "attachment; filename=" + fileName
         //);
         BlobstoreServiceFactory.getBlobstoreService().serve(blobKey, response);
      }
   }

   public static void delete(String fileName) throws IOException
   {
      Key blobInfoEntityKey = getBlobInfoEntityKey(fileName);
      if (blobInfoEntityKey != null) {
         FileService fileService = FileServiceFactory.getFileService();
         String blobKeyName = blobInfoEntityKey.getName(); // ID/Name
         BlobKey blobKey = new BlobKey(blobKeyName);
         AppEngineFile file = fileService.getBlobFile(blobKey);
         // delete the file itself
         if (file != null) fileService.delete(file);
         // delete in the blobstore
         BlobstoreServiceFactory.getBlobstoreService().delete(blobKey);
         // delete the __BlobInfo__ entry
         DatastoreServiceFactory.getDatastoreService().delete(blobInfoEntityKey);
         // delete the __BlobFileIndex__ entry
         DatastoreServiceFactory.getDatastoreService().delete(
            getBlobFileIndexEntityKey(blobKeyName)
         );
      }
   }

   public static BlobKey getBlobKey(String fileName) {
      Key key = getBlobInfoEntityKey(fileName);
      if (key != null) {
         return new BlobKey(key.getName());
      }else {
         return null;
      }
   }

   private static Key getBlobInfoEntityKey(String fileName) {
      Query query = new Query("__BlobInfo__");
      Query.FilterPredicate filter = new Query.FilterPredicate(
         "filename", FilterOperator.EQUAL, fileName
      );
      query.setFilter(filter);
      List<Entity> entList = DatastoreServiceFactory.getDatastoreService().
      prepare(query).
      asList(FetchOptions.Builder.withLimit(10));
      if (entList.size() > 0) {
         return entList.get(0).getKey();
      }else {
         return null;
      }
   }

   // The ID/Name field in BlobInfo is the blob_key in BlobFileIndex
   private static Key getBlobFileIndexEntityKey(String blobKeyName) {
      Query query = new Query("__BlobFileIndex__");
      Query.FilterPredicate filter = new Query.FilterPredicate(
         "blob_key", FilterOperator.EQUAL, blobKeyName
      );
      query.setFilter(filter);
      List<Entity> entList = DatastoreServiceFactory.getDatastoreService().
      prepare(query).
      asList(FetchOptions.Builder.withLimit(10));
      if (entList.size() > 0) {
         return entList.get(0).getKey();
      }else {
         return null;
      }
   }
}
/*===========================================================================*/

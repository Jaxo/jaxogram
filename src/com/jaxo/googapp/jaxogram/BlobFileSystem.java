//save(data, "nameOfSavedFile", "application/json")
//load("nameOfSavedFile");
package com.jaxo.googapp.jaxogram;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.blobstore.BlobInfo;
import com.google.appengine.api.blobstore.BlobInfoFactory;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.datastore.DatastoreService;
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
// http://localhost:8888/jaxogram?OP=blob&ACT=store&WHAT=blahblah3
// http://localhost:8888/jaxogram?OP=blob&ACT=serve
// <BlobKey: 1DL_IpOaqTpKvhfVSA8CyA>
// Proof that the following is wrong:
// http://stackoverflow.com/questions/12065430/finding-a-blob-in-google-appengines-blobstore-with-blobkey
class BlobFileSystem
{
   public static boolean foo;
   public static void store(String data, String fileName, String mimeType) throws Exception {
      FileService fileService = FileServiceFactory.getFileService();
      delete(fileName);  // if it already exists!

      AppEngineFile file = fileService.createNewBlobFile(mimeType, fileName);
      FileWriteChannel writeChannel = fileService.openWriteChannel(file, true);
      writeChannel.write(ByteBuffer.wrap(data.getBytes()));
      writeChannel.closeFinally();
      BlobKey blobKey = fileService.getBlobKey(file);
      BlobInfo info = getInfo(blobKey);
      if (info == null) {
         foo = true;
      }
   }

   public static void serve(String fileName, HttpServletResponse response) throws IOException
   {
//    getBlobInfos();
      BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
      BlobKey blobKey = getBlobKey(fileName);
      if (blobKey != null) {
         //response.addHeader(
         //   "content-disposition", "attachment; filename=" + fileName
         //);
         blobstoreService.serve(blobKey, response);
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

   // e4oDYf95ux4FBzpL3WmD-A  <= ID/Name in BlobInfo
   // e4oDYf95ux4FBzpL3WmD-A  <= blob_key in BlobFileIndex
   public static BlobKey getBlobKey(String fileName) {
      Key key = getBlobInfoEntityKey(fileName);
      if (key != null) {
         return new BlobKey(key.getName());
      }else {
         return null;
      }
   }

   private static Key getBlobInfoEntityKey(String fileName) {
      DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
      Query query = new Query("__BlobInfo__");
      // Query query = new Query("__BlobFileIndex__");
      Query.FilterPredicate filter = new Query.FilterPredicate(
         "filename", FilterOperator.EQUAL, fileName
      );
      query.setFilter(filter);
      List<Entity> entList = datastore.prepare(query).asList(
         FetchOptions.Builder.withLimit(10)
      );
      if (entList.size() > 0) {
         return entList.get(0).getKey();
      }else {
         return null;
      }
   }

   private static Key getBlobFileIndexEntityKey(String blobKeyName) {
      DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
      Query query = new Query("__BlobFileIndex__");
      Query.FilterPredicate filter = new Query.FilterPredicate(
         "blob_key", FilterOperator.EQUAL, blobKeyName
      );
      query.setFilter(filter);
      List<Entity> entList = datastore.prepare(query).asList(
         FetchOptions.Builder.withLimit(10)
      );
      if (entList.size() > 0) {
         return entList.get(0).getKey();
      }else {
         return null;
      }
   }

   public static BlobInfo getInfo(BlobKey key) {
      BlobInfoFactory infoFactory = new BlobInfoFactory();
      BlobInfo info = infoFactory.loadBlobInfo(key);
//    String fname = info.getFilename();
      return info;
   }

// Query.FilterPredicate filter = new Query.FilterPredicate(
//     "blob_key",
//     Query.FilterOperator.EQUAL,
//     blobKey.getKeyString()
// );
// Query query = new Query("__BlobFileIndex__");
// query.setFilter(filter);
// PreparedQuery pq = datastore.prepare(query);
// List<Entity> l = pq.asList(FetchOptions.Builder.withDefaults());
// for (Entity e: l) {
//    datastore.delete(e.getKey());
// }

   public static void getBlobInfos() {
//    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
//    BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
      Iterator<BlobInfo> iterator = new BlobInfoFactory().queryBlobInfos();
//    Iterator<BlobInfo> iterator = null;
//    if (afterBlobKey == null){
//       iterator = new BlobInfoFactory().queryBlobInfos();
//    }else{
//       iterator = new BlobInfoFactory().queryBlobInfosAfter(new BlobKey(afterBlobKey));
//    }
//    List<BlobInfo> blobsToCheck = new LinkedList<BlobInfo>();
//    while (iterator.hasNext()) {
//       blobsToCheck.add(iterator.next());
//    }
      while (iterator.hasNext()) {
         BlobInfo info = iterator.next();
         if (info == null) {
            foo = true;
         }
      }
      //Check those blobs if they have reference in datastore
      //Delete using blobstoreService.delete(blobKey);
   }
}

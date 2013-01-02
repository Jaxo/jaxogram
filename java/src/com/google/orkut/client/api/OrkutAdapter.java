package com.google.orkut.client.api;

/** Encapsulates access to the orkut servers. An OrkutAdapter
 *  is an object that can be used to make requests to the orkut
 *  servers. It stores authentication information and has member
 *  functions corresponding to the main operations. To use it,
 *  instantiate it through <tt>createDefaultAdapter</tt>, 
 *  then authenticate either with <tt>requestAuthURL</tt> +
 *  <tt>authenticate</tt>, or with <tt>setAccessPass</tt> (if you
 *  have an access pass saved from a previous session).<br><br>
 *
 *  For a full walkthrough on how to use this class and the client
 *  library in general, please read the 
 *  <a href='http://code.google.com/apis/orkut/docs/clientlib/intro.html'>Introduction to the orkut Client Library</a>.
 */
public abstract class OrkutAdapter {
   /** Returns the factory for activity-related transactions. */
   public abstract ActivityTxFactory getActivityTF();
   /** Returns the factory for album-related transactions. */
   public abstract AlbumsTxFactory   getAlbumsTF();
   /** Returns the factory for captcha-related transactions. */
   public abstract CaptchaTxFactory  getCaptchaTF();
   /** Returns the factory for comments-related transactions. */
   public abstract CommentsTxFactory getCommentsTF();
   /** Returns the factory for friends-related transactions. */
   public abstract FriendTxFactory   getFriendTF();
   /** Returns the factory for photos-related transactions. */
   public abstract PhotosTxFactory   getPhotosTF();
   /** Returns the factory for profile-related transactions. */
   public abstract ProfileTxFactory  getProfileTF();
   /** Returns the factory for scrap-related transactions. */
   public abstract ScrapTxFactory    getScrapTF();
   /** Returns the factory for video-related transactions. */
   public abstract VideoTxFactory    getVideoTF();


   /** Sets the listener for debug messages. Every time the adapter
    *  wants to print a debug message, it will call this listener. 
    *  Pass null to disable. */
   public abstract void setDebugListener(OrkutAdapterDebugListener l);

   /** Request the authentication URL. This method will contact the
    *  orkut servers and request the URL that the user should visit
    *  in order to authenticate and authorize the application to access
    *  his orkut data. After obtaining this URL, you must direct
    *  the user to visit it. After authentication/authorization, the
    *  user will be redirected to the callback URL you configured
    *  when creating the adapter, and an <tt>oauth_verifier</tt>
    *  parameter will be supplied. You must then supply the value
    *  of this parameter to the {@link #authenticate} method to
    *  finish the authentication ritual. */
   public abstract String requestAuthURL();

   /** Finish the authentication ritual. You must call this function
    *  to finish the authentication ritual when you receive the
    *  verifier code from the orkut servers. This code will be provided
    *  to you as the <tt>oauth_verifier</tt> parameter when the
    *  orkut servers redirect the user to your callback URL. Only
    *  after successfully calling this method will you be ready to
    *  use the adapter. */
   public abstract void authenticate(String verifier);

   /** Returns the access pass for future authentication. Once you
    *  have authenticated with {@link #requestAuthURL} and
    *  {@link #authenticate}, you can call this function to retrieve
    *  a string that works as an "access pass" that you can use
    *  next time instead of performing the authentication ritual again.
    *  In that case, instead of authenticating with 
    *  {@link #requestAuthURL} and {@link #authenticate}, you can
    *  simply call {@link #setAccessPass}. */
   public abstract String getAccessPass();

   /** Sets the access pass (alternative to authentication). You
    *  can call this function to set the access pass that you obtained
    *  from a previous session with {@link #getAccessPass}. This
    *  completely replaces the calls to {@link #requestAuthURL} and
    *  {@link #authenticate}. */
   public abstract void setAccessPass(String accessPass);

   /** Creates a new transaction batch. You must use this function
    *  rather than <tt>new BatchTransaction()</tt> (which is still possible
    *  for backward compatibility), because this function sets the
    *  necessary parameters on it. */
   public abstract BatchTransaction newBatch();

   /** Submits a transaction batch. This method submits the given transaction
    *  batch to the orkut servers. It will block until the server responds
    *  (or an error occurs). Therefore, depending on network speed and
    *  the size of your batch, be prepared to wait a few minutes for this
    *  call to complete. Please notice that library or protocol errors
    *  will be signaled via exceptions, while a server-side error will
    *  be signaled by setting the error flag on the individual transactions,
    *  which you can query with {@link Transaction#hasError} and
    *  {@link Transaction#getError}. */
   public abstract void submitBatch(BatchTransaction btx);

   /** Submits a batch of a single request. This is a convenience
    *  method that prepares a batch that consists of a single transaction
    *  and submits it. It returns the batch for your convenience. */
   public abstract BatchTransaction submitSingle(Transaction tx);

   /** Retrieve captcha image. This method can be called when you
    *  receive a captcha error from a previous transaction. This type
    *  of error indicates that you must have the user solve a captcha
    *  in order to resubmit the transaction. This function allows
    *  you to retrieve the captcha image as a byte array, in JPEG format.
    *
    *  @param er The {@link OrkutError} that was returned from the 
    *  previous transaction.
    *
    *  @return The image data as a byte array, in JPEG format.
    */
   public abstract byte[] getCaptchaImage(OrkutError er);

   /** Retrieve captcha image and save to file. This is a convenience
    *  method that does the same as {@link #getCaptchaImage} but
    *  instead of returning the data as a byte array, it will save
    *  the image to the given file name. */
   public abstract void saveCaptchaToFile(OrkutError er, String filePath)
                                             throws java.io.IOException;

   /** Resubmit batch with captcha solution. Call this method after
    *  you have the user solve the captcha you obtained from
    *  {@link #getCaptchaImage} or {@link #saveCaptchaToFile}.
    *  This will resubmit the transaction with the captcha solution,
    *  which should hopefully carry it to completion.
    *
    * @param btx The original batch transaction that failed with
    * a captcha error.
    * @param error The captcha error you got when submitting the last
    * transaction (<tt>btx</tt>).
    * @param answer The solution to the captcha. This will usually
    * be provided by the user or a highly, highly trained robot/pet.
    */
   public abstract void submitBatchWithCaptcha(BatchTransaction btx, 
                                      OrkutError error, String answer);
}


package com.google.orkut.client.api;

import net.oauth.OAuth;
import net.oauth.OAuthAccessor;
import net.oauth.OAuthConsumer;
import net.oauth.OAuthMessage;
import net.oauth.OAuthServiceProvider;
import net.oauth.client.OAuthClient;
import net.oauth.client.jnetclient.JNetClient;

import com.google.orkut.client.transport.HttpRequest;
import com.google.orkut.client.transport.OrkutHttpRequestFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class DefaultOrkutAdapter extends OrkutAdapter
{
   protected String consumerKey = "";
   protected String consumerSecret = "";
   protected String requestURL = "";

   protected OAuthConsumer consumer = null;
   protected OAuthAccessor accessor = null;
   protected OAuthClient client = null;

   protected static final String OAUTH_REQUEST_URL =
                "https://www.google.com/accounts/OAuthGetRequestToken";
   protected static final String OAUTH_AUTHORIZATION_URL =
                "https://www.google.com/accounts/OAuthAuthorizeToken";
   protected static final String OAUTH_ACCESS_URL =
                "https://www.google.com/accounts/OAuthGetAccessToken";
   protected static final String OAUTH_SCOPE =
                "http://orkut.gmodules.com/social";
   protected static final String SERVER_URL =
                "http://www.orkut.com/social/rpc";

   // Factories
   protected final ActivityTxFactory activityTxFactory = new ActivityTxFactory();
   protected final AlbumsTxFactory albumsTxFactory = new AlbumsTxFactory();
   protected final CaptchaTxFactory captchaTxFactory = new CaptchaTxFactory();
   protected final CommentsTxFactory commentsTxFactory = new CommentsTxFactory();
   protected final FriendTxFactory friendTxFactory = new FriendTxFactory();
   protected final PhotosTxFactory photosTxFactory = new PhotosTxFactory();
   protected final ProfileTxFactory profileTxFactory = new ProfileTxFactory();
   protected final ScrapTxFactory scrapTxFactory = new ScrapTxFactory();
   protected final VideoTxFactory videoTxFactory = new VideoTxFactory();


   @Override
   public ActivityTxFactory getActivityTF() { return activityTxFactory; }
   @Override
   public AlbumsTxFactory getAlbumsTF() { return albumsTxFactory; }
   @Override
   public CaptchaTxFactory getCaptchaTF() { return captchaTxFactory; }
   @Override
   public CommentsTxFactory getCommentsTF() { return commentsTxFactory; }
   @Override
   public FriendTxFactory getFriendTF() { return friendTxFactory; }
   @Override
   public PhotosTxFactory getPhotosTF() { return photosTxFactory; }
   @Override
   public ProfileTxFactory getProfileTF() { return profileTxFactory; }
   @Override
   public ScrapTxFactory getScrapTF() { return scrapTxFactory; }
   @Override
   public VideoTxFactory getVideoTF() { return videoTxFactory; }


   /*-----------------------------------------------------DefaultOrkutAdapter-+
   *//**
   * @param consumerKey Your OAuth consumer key.
   * @param consumerSecret Your OAuth consumer secret.
   *//*
   +-------------------------------------------------------------------------*/
   public DefaultOrkutAdapter(String consumerKey, String consumerSecret)
   {
      this.consumerKey = consumerKey;
      this.consumerSecret = consumerSecret;
      this.requestURL = SERVER_URL;

      say("Initting OAuth.");
      say("Consumer key     : " + consumerKey);
      say("Consumer secret  : <not shown>");
      say("Request URL is   : " + requestURL);

      try {
         say("Setting up oauth consumer.");
         consumer = new OAuthConsumer(null,
                   consumerKey, consumerSecret, new OAuthServiceProvider(
                   OAUTH_REQUEST_URL, OAUTH_AUTHORIZATION_URL,
                   OAUTH_ACCESS_URL));
         consumer.setProperty(OAuthClient.PARAMETER_STYLE,
                   net.oauth.ParameterStyle.QUERY_STRING);

         say("Setting up oauth accessor and client.");
         accessor = new OAuthAccessor(consumer);
//       client = new OAuthClient(new HttpClient4());  // USE JNET instead
         client = new OAuthClient(new JNetClient());
      }
      catch (Exception ex) {
         throw new OrkutAdapterException(
            "OrkutAdapter: Failed to initialize OAuth.", ex
         );
      }
   }

   @Override
   /*----------------------------------------------------------requestAuthURL-+
   *//**
   *//*
   +-------------------------------------------------------------------------*/
   public String requestAuthURL(String callbackUrl) throws Exception
   {
      say("Getting oauth request token.");
      List<OAuth.Parameter> callback = OAuth.newList(
        OAuth.OAUTH_CALLBACK, callbackUrl,
        "scope", OAUTH_SCOPE
      );
      OAuthMessage response = client.getRequestTokenResponse(
         accessor, null, callback
      );
      say("Response obtained.");
      String authorizationURL = OAuth.addParameters(
         accessor.consumer.serviceProvider.userAuthorizationURL,
         OAuth.OAUTH_TOKEN, accessor.requestToken,
         "scope", OAUTH_SCOPE
      );

      if (response.getParameter(OAuth.OAUTH_CALLBACK_CONFIRMED) == null) {
         say("No callback confirm - service provider is using 1.0, not 1.0a.");
         say("Adding callback as bare parameter.");
         authorizationURL = OAuth.addParameters(authorizationURL, callback);
      }else {
         authorizationURL = OAuth.addParameters(
            accessor.consumer.serviceProvider.userAuthorizationURL,
            OAuth.OAUTH_TOKEN, accessor.requestToken,
            "scope", OAUTH_SCOPE
         );
      }

      say("Request token: " + accessor.requestToken);
      say("Authorization URL: " + authorizationURL);
      return authorizationURL;
   }

   /*-------------------------------------------------------------getAccessor-+
   *//**
   *//*
   +-------------------------------------------------------------------------*/
   public OAuthAccessor getAccessor() {
      return accessor;
   }

   @Override
   /*-----------------------------------------------------------getAccessPass-+
   *//**
   *//*
   +-------------------------------------------------------------------------*/
   public String getAccessPass() {
      return accessor.accessToken + " " + accessor.tokenSecret;
   }

   @Override
   /*-----------------------------------------------------------setAccessPass-+
   *//**
   * @see
   *//*
   +-------------------------------------------------------------------------*/
   public void setAccessPass(String accessPass) throws Exception {
      say("Access pass provided: '" + accessPass + "'");
      String[] p = accessPass.split(" ");
      if (p.length != 2)
         throw new OrkutAdapterException(
           "Access pass does not have correct format (token and secret)",null);

      say("Literal access token and secret supplied:");
      say("Access token  : " + p[0]);
      say("Token secret  : " + p[1]);
      accessor.accessToken = p[0];
      accessor.tokenSecret = p[1];
   }

   /*------------------------------------------------------------authenticate-+
   *//**
   *//*
   +-------------------------------------------------------------------------*/
   public String[] authenticate(String verifier, OAuthAccessor givenAccessor)
   throws Exception
   {
      String accessPass;
      String userName;
      say("Verifier and Accessor provided: " + verifier);
      say("Obtaining access token...");
      client.getAccessToken(
         givenAccessor, null,
         OAuth.newList(OAuth.OAUTH_VERIFIER, verifier)
      );
      say("... authenticated");
      accessPass = givenAccessor.accessToken + " " + givenAccessor.tokenSecret;
      setAccessPass(accessPass);
      BatchTransaction btx = newBatch();
      GetProfileTx profile = getProfileTF().getSelfProfile();
      btx.add(profile);
      submitBatch(btx);
      if (profile.hasError()) {
         userName = "???";
      }else {
         OrkutPerson person = profile.getProfile();
         userName = person.getGivenName() + " " + person.getFamilyName();
      }
      return new String[] { accessPass, userName };
   }

   /*---------------------------------------------------------------------say-+
   *//**
   *//*
   +-------------------------------------------------------------------------*/
   protected void say(String s) {}


   /*------------------------------------------------------------------------*/
   @Override
   public void authenticate(String verifier) {
      say("Trying to authenticate with verifier: " + verifier);
      try {
         authenticate_inner(verifier);
      }catch (Exception e) {
         throw new OrkutAdapterException(
           "Orkut Adapter: Error authenticating.", e
         );
      }
   }

   private void authenticate_inner(String verifier) throws Exception {
      say("Verifier code provided: " + verifier);
      say("Obtaining access token...");

      client.getAccessToken(
         accessor, null,
         OAuth.newList(OAuth.OAUTH_VERIFIER, verifier)
      );
      say("Got access token   : " + accessor.accessToken);
      say("Access token secret: " + accessor.tokenSecret);
   }

   @Override
   public BatchTransaction newBatch() {
      try {
         return new BatchTransaction(new OrkutHttpRequestFactory(),
           new com.google.orkut.client.config.Config() {
              public String getRequestBaseUrl() { return requestURL; }
           });
       }
       catch (Exception ex) {
          throw new OrkutAdapterException("OrkutAdapter: error creating " +
              "batch transaction.", ex);
       }
   }

   @SuppressWarnings({ "rawtypes", "unchecked" })
   @Override
   public void submitBatch(BatchTransaction btx) {
      try {
         HttpRequest req   = btx.build();
         byte[] body       = req.getRequestBody();
         String method     = req.getMethod();
         String baseURL    = req.getRequestBaseUrl();
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
         String respBody = resp.readBodyAsString();
         btx.setResponse(respBody);
      }catch (Exception ex) {
         throw new OrkutAdapterException(
            "OrkutAdapter: error sending transaction batch.", ex
         );
      }
   }

   @Override
   public BatchTransaction submitSingle(Transaction tx) {
      BatchTransaction b = newBatch();
      b.add(tx);
      submitBatch(b);
      return b;
   }

   @Override
   public byte[] getCaptchaImage(OrkutError er) {
      try {
         String url = "http://www.orkut.com" + er.captchaUrl();
         OAuthMessage msg = new OAuthMessage("GET", url, null);
         msg.addRequiredParameters(accessor);
         OAuthMessage resp = client.invoke(msg,
                                net.oauth.ParameterStyle.QUERY_STRING);
         java.io.InputStream is = resp.getBodyAsStream();
         if (is == null)
            throw new OrkutAdapterException("No input stream in response.",
                                null);
         return Util.readAllFrom(is);
      }
      catch (Exception ex) {
         throw new OrkutAdapterException("Error getting captcha image", ex);
      }
   }

   @Override
   public void saveCaptchaToFile(OrkutError er, String filePath)
                                             throws IOException {
      byte[] b = getCaptchaImage(er);
      java.io.FileOutputStream fos = new java.io.FileOutputStream(filePath);
      fos.write(b);
      fos.close();
   }

   @Override
   public void submitBatchWithCaptcha(BatchTransaction btx,
                                      OrkutError error, String answer) {
      Transaction tx =
                captchaTxFactory.answerCaptcha(error.captchaToken(),answer);
      btx.add(tx);
      submitBatch(btx);
   }

   class PostOAuthMessage extends OAuthMessage {
      private final byte[] body;
      @SuppressWarnings("rawtypes")
     public PostOAuthMessage(String method, String url,
                   Collection<? extends Map.Entry> parameters, byte[] body) {
         super(method, url, parameters);
         this.body = body;
      }

      public InputStream getBodyAsStream() throws IOException {
         return (body == null) ? null : new ByteArrayInputStream(body);
      }
   }
}


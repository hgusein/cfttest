package ru.hgusein.cftest;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.util.Log;

public class CftTestApplication extends Application {

  public static final String TAG_PARCEL_JSON_DATASET = "parcel_json_dataset";
  
  private static String TAG = "CftTestApplication";
  private static Context cnt;

  @Override
  public void onCreate() {
    super.onCreate();
    Log.d(TAG, "onCreate()");
    cnt = this;
  }
  
  public boolean isInternetConnected() {

    ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
    if (connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == android.net.NetworkInfo.State.CONNECTED
        || connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == android.net.NetworkInfo.State.CONNECTED) {
      return true;
    } else {
      return false;
    }

  }
  
  public static Context getContext() {
    return cnt;
  }
  
  /**
   * Disables the SSL certificate checking for new instances of
   * {@link HttpsURLConnection} This has been created to aid testing on a local
   * box, not for use on production.
   * 
   * @see https://gist.github.com/aembleton/889392
   */
  public static void disableSSLCertificateChecking() {
    TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
      public X509Certificate[] getAcceptedIssuers() {
        return null;
      }

      @Override
      public void checkClientTrusted(X509Certificate[] arg0, String arg1)
          throws CertificateException {
        // Not implemented
      }

      @Override
      public void checkServerTrusted(X509Certificate[] arg0, String arg1)
          throws CertificateException {
        // Not implemented
      }
    } };

    try {
      SSLContext sc = SSLContext.getInstance("TLS");
      sc.init(null, trustAllCerts, new java.security.SecureRandom());
      HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

    } catch (KeyManagementException e) {
      e.printStackTrace();
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
    }
  }

  /** */
  public static void enableSSLCertificateChecking() {

    try {
      SSLContext dsc = SSLContext.getDefault();
      HttpsURLConnection.setDefaultSSLSocketFactory(dsc.getSocketFactory());
    } catch (Exception e) {
      e.printStackTrace();
    }

  }
  

}

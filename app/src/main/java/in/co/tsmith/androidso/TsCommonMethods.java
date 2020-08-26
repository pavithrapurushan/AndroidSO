package in.co.tsmith.androidso;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.UUID;


//Created by Pavithra on 21-08-2020

public class TsCommonMethods {

    private static final int MY_PERMISSIONS_REQUEST_READ_PHONE_STATE = 2;
    private static final int MY_PERMISSIONS_REQUEST_INTERNET = 4;
    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 6;
    private static final int MY_PERMISSIONS_REQUEST_READ_STORAGE = 8;
    private static final int MY_PERMISSIONS_REQUEST_WRITE_STORAGE = 10;

    Context CurrentContext;
    TelephonyManager mTelephony;
    boolean AllPermissionFlag ;


    public TsCommonMethods(Context cntxt) {
        CurrentContext = cntxt;
        AllPermissionFlag = false;
    }

    public boolean isNetworkConnected() {

        ConnectivityManager cm = (ConnectivityManager) CurrentContext.getSystemService(CurrentContext.CONNECTIVITY_SERVICE);

        NetworkInfo wifiNetwork = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifiNetwork != null && wifiNetwork.isConnected()) {
            return true;
        }

        NetworkInfo mobileNetwork = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (mobileNetwork != null && mobileNetwork.isConnected()) {
            return true;
        }

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null && activeNetwork.isConnected()) {
            return true;
        }

        return false;
    }

    public String GetDeviceUniqueId() {

        String DeviceUniqueId = "";
        String StoreIdentifier = "", WSUrl = "";

        TelephonyManager TelephoneManager1;
        TelephoneManager1 = (TelephonyManager) CurrentContext.getSystemService(CurrentContext.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(CurrentContext, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions((Activity)CurrentContext, new String[]{Manifest.permission.READ_PHONE_STATE}, MY_PERMISSIONS_REQUEST_READ_PHONE_STATE);

            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
//            return TODO;
        }

        if (ActivityCompat.checkSelfPermission(CurrentContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions((Activity) CurrentContext, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE_STORAGE);

            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
//            return TODO;
        }

        if (ActivityCompat.checkSelfPermission(CurrentContext, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions((Activity) CurrentContext, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_STORAGE);

            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
//            return TODO;
        }

        DeviceUniqueId = TelephoneManager1.getDeviceId();

        if(DeviceUniqueId == null || DeviceUniqueId.length() == 0 ) {

            SharedPreferences sharedPrefs1 = PreferenceManager.getDefaultSharedPreferences(CurrentContext);
            DeviceUniqueId = sharedPrefs1.getString("StoredDevId","");
            StoreIdentifier = sharedPrefs1.getString("StoreIdf","");
            WSUrl = sharedPrefs1.getString("WSUrl","");

            Log.d("MSAPP", "Taking From SharedPreferences");
            //Toast.makeText(CurrentContext,"Taking From SharedPreferences", Toast.LENGTH_SHORT).show();
        }

        if(DeviceUniqueId == null || DeviceUniqueId.length() == 0 ) {
            String strUUID  = String.valueOf( UUID.randomUUID());
            strUUID = strUUID.replace("-", "");
            DeviceUniqueId = strUUID.substring(0,15).toUpperCase();

            Log.d("MSAPP", "Taking From UUID");
        }

        Log.d("MSAPP", "DeviceUniqueId : " + DeviceUniqueId);
        Log.d("MSAPP", "StoreIdentifier : " + StoreIdentifier);
        Log.d("MSAPP", "WSUrl : " + WSUrl);

        //Toast.makeText(CurrentContext,"DeviceUniqueId : " + DeviceUniqueId, Toast.LENGTH_SHORT).show();
        //Toast.makeText(CurrentContext,"StoreIdentifier : " + StoreIdentifier, Toast.LENGTH_SHORT).show();
        //Toast.makeText(CurrentContext,"WSUrl : " + WSUrl, Toast.LENGTH_SHORT).show();

        return DeviceUniqueId;
    }

    public boolean checkPermissions(){

        if ((int) Build.VERSION.SDK_INT < 23)
        {
            //this is a check for build version below 23
            mTelephony = (TelephonyManager) CurrentContext.getSystemService(Context.TELEPHONY_SERVICE);
            return AllPermissionFlag;
        }
        else{
            if (ContextCompat.checkSelfPermission(CurrentContext, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED){

                ActivityCompat.requestPermissions((Activity) CurrentContext, new String[]{Manifest.permission.READ_PHONE_STATE}, MY_PERMISSIONS_REQUEST_READ_PHONE_STATE);
                Log.e("Tag",", request for permission");
                AllPermissionFlag = false;

            }  else    {
                mTelephony = (TelephonyManager) CurrentContext.getSystemService(Context.TELEPHONY_SERVICE);
                Log.e("Tag","Permission is granted");
            }

            if (ContextCompat.checkSelfPermission(CurrentContext, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){

                ActivityCompat.requestPermissions((Activity) CurrentContext, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_STORAGE);
                Log.e("Tag",", request for permission");
                AllPermissionFlag = false;

            }  else    {
                mTelephony = (TelephonyManager) CurrentContext.getSystemService(Context.TELEPHONY_SERVICE);
                Log.e("Tag","Permission is granted");
            }

            if (ContextCompat.checkSelfPermission(CurrentContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){

                ActivityCompat.requestPermissions((Activity) CurrentContext, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE_STORAGE);
                Log.e("Tag",", request for permission");
                AllPermissionFlag = false;

            }  else    {
                mTelephony = (TelephonyManager) CurrentContext.getSystemService(Context.TELEPHONY_SERVICE);
                Log.e("Tag","Permission is granted");
            }









            if (ContextCompat.checkSelfPermission(CurrentContext, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED){

                ActivityCompat.requestPermissions((Activity) CurrentContext, new String[]{Manifest.permission.INTERNET}, MY_PERMISSIONS_REQUEST_INTERNET);
                Log.e("Tag",", request for permission");
                AllPermissionFlag = false;

            }  else    {
                mTelephony = (TelephonyManager) CurrentContext.getSystemService(Context.TELEPHONY_SERVICE);
                Log.e("Tag","Permission is granted");
            }
            if (ContextCompat.checkSelfPermission(CurrentContext, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){

                ActivityCompat.requestPermissions((Activity) CurrentContext, new String[]{Manifest.permission.CAMERA}, MY_PERMISSIONS_REQUEST_CAMERA);
                Log.e("Tag",", request for permission");
                AllPermissionFlag = false;

            }  else    {
                mTelephony = (TelephonyManager) CurrentContext.getSystemService(Context.TELEPHONY_SERVICE);
                Log.e("Tag","Permission is granted");
            }
        }

        return AllPermissionFlag;

    }
}

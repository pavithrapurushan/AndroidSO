package in.co.tsmith.androidso;

import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;


//Created by Pavithra on 21-08-2020

public class SettingsActivity extends AppCompatActivity {

    TsCommonMethods tsCommonMethods;
    String DeviceId = "";
    TextView tvDeviceId;
    TextView tvVersionName;
    TextView tvUserName;
    Button btnRegister;
    EditText etStoreCode;
    EditText etEmail;
    EditText etPassword;
    TextView tvUrl;

    String user_name = "";

    AppUserPL appUserPLObj;
    String appUserPlJsnStr = "";
    SharedPreferences prefs;

    String strRegister = "";

    LinearLayout llSettingsHeader;
    double llSettingsHeaderHeight;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);



        llSettingsHeader = (LinearLayout)findViewById(R.id.llSettingsHeader);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screen_height = displayMetrics.heightPixels;
        int screen_width = displayMetrics.widthPixels;

        llSettingsHeaderHeight = (screen_height * 8.75)/100;  //56/640

        LinearLayout.LayoutParams paramsllHeader = (LinearLayout.LayoutParams) llSettingsHeader.getLayoutParams();
        paramsllHeader.height = (int) llSettingsHeaderHeight;
        paramsllHeader.width = LinearLayout.LayoutParams.MATCH_PARENT;
        llSettingsHeader.setLayoutParams(paramsllHeader);

        tsCommonMethods = new TsCommonMethods(this);
        appUserPLObj = new AppUserPL();
        DeviceId = tsCommonMethods.GetDeviceUniqueId();
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        tvDeviceId = (TextView) findViewById(R.id.tvDeviceIdValue);
        tvVersionName = (TextView) findViewById(R.id.tvAppVersionValue);
        tvUserName = (TextView) findViewById(R.id.tvUsername);
        btnRegister = (Button) findViewById(R.id.btnRegister);

        user_name = prefs.getString("TSSOUsername", "");
        tvUserName.setText("USERNAME : " + user_name);

        etStoreCode = (EditText) findViewById(R.id.etStoreCode);
        etEmail = (EditText) findViewById(R.id.etEmailId);
        etPassword = (EditText) findViewById(R.id.etPassword);
        tvUrl = (TextView) findViewById(R.id.tvUrlValue);
        tvUrl.setText(" : " + AppConfig.url);

        String appUSerPLRegisterStr = prefs.getString("AppUserPLJsonFRomRegister", "");
        if (appUSerPLRegisterStr != null && !appUSerPLRegisterStr.equals("")) {
            Gson gson = new Gson();
            appUserPLObj = gson.fromJson(appUSerPLRegisterStr, AppUserPL.class);
            etStoreCode.setText(appUserPLObj.StoreCode);
            etEmail.setText(appUserPLObj.Email);
//            etPassword.setText(appUserPLObj.Password);
            etPassword.setText("");
        }
        tvDeviceId.setText(" : " + DeviceId);
        PackageInfo pinfo = null;
        try {
            pinfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            int versionNumber = pinfo.versionCode;
            String versionName = pinfo.versionName;
            tvVersionName.setText(" : " + versionName);

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (etStoreCode.getText().toString().equals("") || etEmail.getText().toString().equals("") ||  etPassword.getText().toString().equals("")) {
                    Toast.makeText(SettingsActivity.this, "Fields are empty", Toast.LENGTH_SHORT).show();
                } else {

                    appUserPLObj.DeviceId = DeviceId;
                    appUserPLObj.StoreCode = etStoreCode.getText().toString();
                    appUserPLObj.Email = etEmail.getText().toString();
                    appUserPLObj.Password = etPassword.getText().toString();
//                       appUserPLObj.Password = etPassword.getText().toString();
                    Gson gson = new Gson();
                    appUserPlJsnStr = gson.toJson(appUserPLObj);

                    new RegisterTask().execute();
                }

            }
        });

    }

    private class RegisterTask extends AsyncTask<String,String,String>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {

            try {
                String myUrl = AppConfig.url + "Register";
                URL url = new URL(myUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setReadTimeout(15000);
                connection.setConnectTimeout(30000);
                connection.setRequestProperty("auth_key","77973105B4274128BD4AB868A3C2B15B");
                connection.setRequestProperty("device_id",DeviceId);
                connection.setRequestProperty("Content-Type","application/json");
                connection.setDoOutput(true);
                OutputStreamWriter wr = new OutputStreamWriter(connection.getOutputStream());
                wr.write( appUserPlJsnStr );
                wr.flush();
                connection.connect();
                try {
                    InputStreamReader streamReader = new InputStreamReader(connection.getInputStream());
                    BufferedReader reader = new BufferedReader(streamReader);
                    StringBuilder sb = new StringBuilder();
                    String inputLine = "";

                    while ((inputLine = reader.readLine()) != null) {
                        sb.append(inputLine);
                        break;
                    }

                    reader.close();
                    strRegister = sb.toString();


                } finally {
                    connection.disconnect();
                }

            } catch (Exception e) {
                Log.e("ERROR", e.getMessage(), e);
                return null;
            }
            return strRegister;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if (strRegister == null || strRegister.equals("")) {
                Toast.makeText(SettingsActivity.this, "No result from web, try later", Toast.LENGTH_SHORT).show();
            } else {
                String str = strRegister;
                Gson gson = new Gson();
                appUserPLObj = gson.fromJson(strRegister, AppUserPL.class);
                if (appUserPLObj.ErrorStatus.equals("0")) {
                    SharedPreferences.Editor editor = prefs.edit();
                    gson = new Gson();
                    String temp = gson.toJson(appUserPLObj);
                    editor.putString("AppUserPLJsonFRomRegister",temp);
                    editor.commit();

                    etStoreCode.setText(appUserPLObj.StoreCode);
                    etEmail.setText(appUserPLObj.Email);
                    etPassword.setText(appUserPLObj.Password);
                    tvUserName.setText("USERNAME : "+appUserPLObj.Username);

                    Toast.makeText(SettingsActivity.this, ""+appUserPLObj.Message, Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(SettingsActivity.this, appUserPLObj.Message, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

}

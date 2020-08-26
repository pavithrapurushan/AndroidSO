package in.co.tsmith.androidso;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

//Created by Pavithra on 21-08-2020

public class MainActivity extends AppCompatActivity {


    TsCommonMethods tsCommonMethods;
    ImageButton imgBtnSettings;
    EditText etUsername;
    EditText etPassword;
    Button btnLogin;
    TextView tvForgotPswd;

    SharedPreferences prefs;

    AppUserPL appUserPLObj;

    String appUserPlJsnStr = "";

    Gson gson;
    String DeviceId = "";

    String strCheckLogin = "";
    private AlphaAnimation buttonClick = new AlphaAnimation(1F, 0.1F);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tsCommonMethods = new TsCommonMethods(this);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        DeviceId = tsCommonMethods.GetDeviceUniqueId();
        etUsername = (EditText) findViewById(R.id.etUsername);
        etPassword = (EditText) findViewById(R.id.etPassword);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        tvForgotPswd = (TextView) findViewById(R.id.tvForgotPswd);

        appUserPLObj = new AppUserPL();

        imgBtnSettings = (ImageButton) findViewById(R.id.imgBtnSettings);
        imgBtnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intn = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intn);
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                view.startAnimation(buttonClick);

                if (etUsername.getText().toString().equals("") || etPassword.getText().toString().equals("")) {
                    Toast.makeText(MainActivity.this, "Fields are empty", Toast.LENGTH_SHORT).show();
                } else {
                    appUserPLObj.Username = etUsername.getText().toString();
                    appUserPLObj.Password = etPassword.getText().toString();
                    gson = new Gson();
                    appUserPlJsnStr = gson.toJson(appUserPLObj);

                    new CheckLoginTask().execute();

                }
            }
        });

        tvForgotPswd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intnt = new Intent(MainActivity.this, ForgotPasswordActivity.class);
                startActivity(intnt);
            }
        });
    }

    private class CheckLoginTask extends AsyncTask<String,String,String>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                String myUrl = AppConfig.url + "CheckLogin";
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
                    strCheckLogin = sb.toString();

//                    {"Id":9,"Username":"500_9","Name":"500 User 9","Password":"","Email":"pavithrapurushan06@gmail.com","Phone":"","Active":0,"StoreId":50,"StoreCode":"500","StoreName":"SEPL-PHARMA WAREHOUSE","DeviceId":"","PasswordOtp":"","ErrorStatus":0,"Message":"Success"}

                } finally {
                    connection.disconnect();
                }

            } catch (Exception e) {
                Log.e("ERROR", e.getMessage(), e);
                return null;
            }
            return strCheckLogin;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if (strCheckLogin == null || strCheckLogin.equals("")) {
                Toast.makeText(MainActivity.this, "No result from web, try later", Toast.LENGTH_SHORT).show();
            } else {
                String str = strCheckLogin;
                appUserPLObj = gson.fromJson(strCheckLogin, AppUserPL.class);
                if (appUserPLObj.ErrorStatus.equals("0")) {
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("StoreId", appUserPLObj.StoreId);
                    editor.putString("StoreName", appUserPLObj.StoreName);
                    editor.putString("TSSOUsername", appUserPLObj.Username);
                    editor.putString("TSSOPassword", etPassword.getText().toString());
                    editor.putInt("AppUserId", appUserPLObj.Id);
                    editor.commit();
                    Toast.makeText(MainActivity.this, "Successfully logged in", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MainActivity.this, CustomerInformationActivity.class);
//                                intent.putExtra("FromLogin",true);
                    startActivity(intent);
                    MainActivity.this.finish();

                } else {
                    Toast.makeText(MainActivity.this, appUserPLObj.Message, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}

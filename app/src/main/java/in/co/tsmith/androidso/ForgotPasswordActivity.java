package in.co.tsmith.androidso;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

//Created by Pavithra on 22-08-2020

public class ForgotPasswordActivity extends AppCompatActivity {

    Button btnSendOtp;

    AppUserPL appUserPLObj;
    SharedPreferences prefs;
    String appUserPLJsonStr = "";
    Gson gson;

    TextView tvOtpSendStatus;
    Button btnSubmit;
    Button btnCancel;
    EditText etOtpValue;
    EditText etNewPasswd;
    EditText etConfirmPswd;

    String user_name = "";
    String app_userpl_str = "";

    String strSendOtp = "";
    String DeviceId = "";
    TsCommonMethods tsCommonMethods;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgotpassword);

        tsCommonMethods = new TsCommonMethods(this);
        DeviceId = tsCommonMethods.GetDeviceUniqueId();
        btnSendOtp = (Button)findViewById(R.id.btnSendOtp);
        btnSubmit = (Button)findViewById(R.id.btnSubmit);
        btnCancel = (Button)findViewById(R.id.btnCancel);
        tvOtpSendStatus = (TextView)findViewById(R.id.tvOtpSendStatus);
        etOtpValue = (EditText) findViewById(R.id.etOtpValue);
        etNewPasswd = (EditText) findViewById(R.id.etNewPswd);
        etConfirmPswd = (EditText) findViewById(R.id.etConfirmPswd);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        appUserPLObj = new AppUserPL();

        gson = new Gson();
        appUserPLJsonStr =  prefs.getString("AppUserPLJsonFRomRegister", "");
        user_name =  prefs.getString("TSSOUsername", "");
        appUserPLObj = gson.fromJson(appUserPLJsonStr, AppUserPL.class);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("TSSOUsername",appUserPLObj.Username);
        editor.commit();

        btnSendOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new SendOtpTask().execute();
            }
        });

    }

    private class SendOtpTask extends AsyncTask<String,String,String>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                String myUrl = AppConfig.url + "SendOTP";
                URL url = new URL(myUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setReadTimeout(15000);
                connection.setConnectTimeout(30000);
                connection.setRequestProperty("auth_key", "77973105B4274128BD4AB868A3C2B15B");
                connection.setRequestProperty("device_id", DeviceId);
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setDoOutput(true);
                OutputStreamWriter wr = new OutputStreamWriter(connection.getOutputStream());
                wr.write(appUserPLJsonStr);
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
                    strSendOtp = sb.toString();

//                    {"Id":9,"Username":"500_9","Name":"500 User 9","Password":"","Email":"pavithrapurushan06@gmail.com","Phone":"","Active":0,"StoreId":50,"StoreCode":"500","StoreName":"SEPL-PHARMA WAREHOUSE","DeviceId":"","PasswordOtp":"","ErrorStatus":0,"Message":"Success"}

                } finally {
                    connection.disconnect();
                }

            } catch (Exception e) {
                Log.e("ERROR", e.getMessage(), e);
                return null;
            }
            return strSendOtp;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            try {
                if (strSendOtp == null || strSendOtp.equals("")) {
                    Toast.makeText(ForgotPasswordActivity.this, "No result from web, try later", Toast.LENGTH_SHORT).show();
                } else {
                    appUserPLObj = gson.fromJson(strSendOtp, AppUserPL.class);
                    if (appUserPLObj.ErrorStatus.equals("0")) {
                        tvOtpSendStatus.setText(appUserPLObj.Message);
                        tvOtpSendStatus.setVisibility(View.VISIBLE);
                    } else {
                        Toast.makeText(ForgotPasswordActivity.this, appUserPLObj.Message, Toast.LENGTH_SHORT).show();
                    }
                }
            } catch (Exception ex) {
                Log.e("FPA",""+ex);
            }
        }
    }
}

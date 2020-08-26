package in.co.tsmith.androidso;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
//Created by Pavithra on 21-08-2020

public class CustomerInformationActivity extends AppCompatActivity {


    LinearLayout llCustInfoToolbar;
    double llCustInfoToolbarHeight;
    LinearLayout llCustInfoBottombar;
    double llCustInfoBottombarHeight;

    SharedPreferences prefs;
    TextView tvStoreName;

    ImageButton imgBtnSearchbyCode;
    ImageButton imgBtnSearchbyHUID;
    EditText etCustCode;
    EditText etHUID;
    EditText etCustomerName;
    EditText etCustomerMobile;
    EditText etAdrsLine1;
    EditText etAdrsLine2;

    String strGetCustomerByCode = "";
    String strGetCustomerByHUID = "";
    String strFromSelectDoctor = "";
    String strSaveCustomer = "";
    String user_name =  "";
    String etCustCodeStr = "";
    String etHUIDStr = "";
    String DeviceId = "";
    TsCommonMethods tsCommonMethods;

    ListCustomerPL listCustomerPLObj;
    CustomerPL customerPLObj;

    AutoCompleteTextView acvSearchDoctor;
    ImageButton imgBtnSearchDoctor;
    Button btnClear;
    Button btnCreateSO;

    String acvSelectDoctrText = "";

    ListDoctorDetails listDoctorDetailsObj;
    String[] arrItems;
    KArrayAdapter<String> arradapter;

    private AlphaAnimation buttonClick = new AlphaAnimation(1F, 0.1F);
    int selected_doctor_id ;
    String selected_doctor = "";


    String customerPlObjStr = "";
    boolean isContinue = false;

    CustomerPL customerPL;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customerinformation);

        tsCommonMethods = new TsCommonMethods(this);
        DeviceId = tsCommonMethods.GetDeviceUniqueId();
        llCustInfoToolbar = (LinearLayout)findViewById(R.id.llCustInfoToolbar);
        llCustInfoBottombar = (LinearLayout)findViewById(R.id.llCustInfoBottombar);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screen_height = displayMetrics.heightPixels;
        int screen_width = displayMetrics.widthPixels;

        llCustInfoToolbarHeight = (screen_height * 8.75)/100;  //    56/640
        llCustInfoBottombarHeight = (screen_height * 21.25)/100;  //   136/640

        LinearLayout.LayoutParams paramsllHeader = (LinearLayout.LayoutParams) llCustInfoToolbar.getLayoutParams();
        paramsllHeader.height = (int) llCustInfoToolbarHeight;
        paramsllHeader.width = LinearLayout.LayoutParams.MATCH_PARENT;
        llCustInfoToolbar.setLayoutParams(paramsllHeader);

        LinearLayout.LayoutParams paramsllBottom = (LinearLayout.LayoutParams) llCustInfoBottombar.getLayoutParams();
        paramsllBottom.height = (int) llCustInfoBottombarHeight;
        paramsllBottom.width = LinearLayout.LayoutParams.MATCH_PARENT;
        llCustInfoBottombar.setLayoutParams(paramsllBottom);

        tvStoreName =  (TextView)findViewById(R.id.tvStoreName);
        imgBtnSearchbyCode =  (ImageButton) findViewById(R.id.imgBtnSearchbyCode);
        imgBtnSearchbyHUID=  (ImageButton) findViewById(R.id.imgBtnSearchbyHUID);
        etCustCode=  (EditText) findViewById(R.id.etCustCode);
        etHUID =  (EditText) findViewById(R.id.etHUID);
        etCustomerName =  (EditText) findViewById(R.id.etCustomerName);
        etCustomerMobile =  (EditText) findViewById(R.id.etCustomerMobile);
        etAdrsLine1 =  (EditText) findViewById(R.id.etAdrsline1);
        etAdrsLine2 =  (EditText) findViewById(R.id.etAdrsline2);
        acvSearchDoctor =  (AutoCompleteTextView) findViewById(R.id.acvSearchDoctor);
        imgBtnSearchDoctor =  (ImageButton) findViewById(R.id.imgBtnSearchDoctor);
        btnClear =  (Button) findViewById(R.id.btnClear);
        btnCreateSO =  (Button) findViewById(R.id.btnCreateSO);

        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        user_name = prefs.getString("TSSOUsername", "");
        customerPlObjStr = prefs.getString("CustomerInfoJsonStr","");
        String store_name = prefs.getString("StoreName","");
        tvStoreName.setText(store_name);

        listCustomerPLObj = new ListCustomerPL();

        imgBtnSearchbyCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("ListOfItemsAdded","");
                editor.putString("TokenNo","");
                editor.commit();

                if(etCustCode.getText().toString().equals("")||etCustCode.getText().toString() ==null){
                    Toast.makeText(CustomerInformationActivity.this, "Field is empty", Toast.LENGTH_SHORT).show();
                }else {

                    etCustCodeStr = etCustCode.getText().toString();
                    new GetCustomerByCodeTask().execute();

//                    String myUrl = AppConfig.url + "GetCustomer/Code/" + et_input_cust_code.getText().toString();
//                    HttpGetRequest getRequest = new HttpGetRequest(CustomerInformationActivity.this);
                }

            }
        });
        imgBtnSearchbyHUID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("ListOfItemsAdded","");
                editor.putString("TokenNo","");
                editor.commit();

                if(etHUID.getText().toString().equals("")||etHUID.getText().toString() == null){
                    Toast.makeText(CustomerInformationActivity.this, "Field is empty", Toast.LENGTH_SHORT).show();
                }else {
                    etHUIDStr = etHUID.getText().toString();
                    new GetCustomerByHUIDTask().execute();
                }
            }
        });

        imgBtnSearchDoctor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (acvSearchDoctor.getText().toString().length() > 1) {
                        acvSelectDoctrText = acvSearchDoctor.getText().toString();
                        new SelectDoctorTask().execute();

//                            DatabaseHandler dbHandler = new DatabaseHandler(SalesOrderModel2.this);
//                            listCustomersOffline = dbHandler.getSpecificCustomers(acvText);
//
//                            String[] arrCust = new String[listCustomersOffline.size()];
//                            for (int i = 0; i < listCustomersOffline.size(); i++) {
//                                arrCust[i] = listCustomersOffline.get(i).cust_name;
//                            }
//                            AutocompleteCustomArrayAdapter myAdapter = new AutocompleteCustomArrayAdapter(SalesOrderModel2.this, R.layout.autocomplte_view_row, arrCust);
//                            acvCustomer.setAdapter(myAdapter);

                    } else
                        Toast.makeText(CustomerInformationActivity.this, "Provide Atleast Two Chars", Toast.LENGTH_SHORT).show();
                }catch (Exception ex){
                    Toast.makeText(CustomerInformationActivity.this, ""+ex, Toast.LENGTH_SHORT).show();
                }

            }
        });

        acvSearchDoctor.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                acvSearchDoctor.setAdapter(null);
                acvSearchDoctor.setEnabled(true);
                acvSearchDoctor.requestFocus();

                selected_doctor = arradapter.getItem(i);

                for(int j = 0;j<listDoctorDetailsObj.lst.size();j++){
                    if(selected_doctor.equals(listDoctorDetailsObj.lst.get(j).Name)){
                        selected_doctor_id = Integer.valueOf(listDoctorDetailsObj.lst.get(j).Id);
                        break;
                    }
                }
            }
        });

        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                etCustCode.setText("");
                etHUID.setText("");
                etCustomerName.setText("");
                etCustomerMobile.setText("");
                etAdrsLine1.setText("");
                etAdrsLine2.setText("");
                acvSearchDoctor.setText("");

                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("CustomerInfoJsonStr","");
                editor.putString("ListOfItemsAdded","");//clearing the already saved items for this customer
                editor.commit();
                editor.commit();

            }
        });

        btnCreateSO.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                boolean canSave = false;
                view.startAnimation(buttonClick);

                customerPL = new CustomerPL();
                if(listCustomerPLObj == null ) {
                    customerPL.Id = 0;
                    canSave = true;
                }else {
                    if (listCustomerPLObj.lst.size() < 1) {
                        canSave = false;
//                        Toast.makeText(CustomerInformationActivity.this, "Cannot save", Toast.LENGTH_SHORT).show();
                    } else {
                        canSave = true;
                        customerPL.Id = listCustomerPLObj.lst.get(0).Id;
                    }
                }
                if(!etCustomerName.getText().toString().equals("")) {
                    customerPL.Code = etCustCode.getText().toString();
                    customerPL.HUID = etHUID.getText().toString();
                    customerPL.Name = etCustomerName.getText().toString();
                    customerPL.Phone = etCustomerMobile.getText().toString();
                    customerPL.AddressLine1 = etAdrsLine1.getText().toString();
                    customerPL.AddressLine2 = etAdrsLine2.getText().toString();

                    String acvText = acvSearchDoctor.getText().toString();

                    if(selected_doctor_id == 0 && !acvText.equals("")){
                        Toast.makeText(CustomerInformationActivity.this, "Select doctor from the list", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    customerPL.DoctorId = selected_doctor_id;

                    Gson gson = new Gson();
                    customerPlObjStr = gson.toJson(customerPL);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("CustomerInfoJsonStr", customerPlObjStr);
                    editor.commit();
                    isContinue = true;

                    new SaveCustomerTask().execute();

                }else {
                    Toast.makeText(CustomerInformationActivity.this, "Can not save, fields are empty", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private class SaveCustomerTask extends AsyncTask<String,String,String>{

        @Override
        protected String doInBackground(String... strings) {
            try {
//                String myUrl = AppConfig.url + "GetCustomer/Code/" + etCustCodeStr;
//                String myUrl = AppConfig.url +"GetDoctors/name/"+acvSelectDoctrText;
                String myUrl = AppConfig.url+"SaveCustomer";
                URL url = new URL(myUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                connection.setRequestMethod("POST");
                connection.setRequestProperty("user_key",user_name); // added by 1165 on 19-01-2019
                connection.setRequestProperty("auth_key","77973105B4274128BD4AB868A3C2B15B");
                connection.setRequestProperty("device_id",DeviceId);
                connection.setReadTimeout(15000);
                connection.setConnectTimeout(30000);
                connection.setRequestProperty("auth_key","77973105B4274128BD4AB868A3C2B15B");
                connection.setRequestProperty("device_id",DeviceId);

                connection.setRequestProperty("Content-Type","application/json");
                connection.setDoOutput(true);

                OutputStreamWriter wr = new OutputStreamWriter(connection.getOutputStream());
                wr.write( customerPlObjStr );
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
                    strSaveCustomer = sb.toString();
                    String temp = strSaveCustomer;

//                    {"Id":9,"Username":"500_9","Name":"500 User 9","Password":"","Email":"pavithrapurushan06@gmail.com","Phone":"","Active":0,"StoreId":50,"StoreCode":"500","StoreName":"SEPL-PHARMA WAREHOUSE","DeviceId":"","PasswordOtp":"","ErrorStatus":0,"Message":"Success"}

                } finally {
                    connection.disconnect();
                }

            } catch (Exception e) {
                Log.e("ERROR", e.getMessage(), e);
                return null;
            }
            return strSaveCustomer;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (strSaveCustomer == null || strSaveCustomer.equals("")) {
                Toast.makeText(CustomerInformationActivity.this, "No result from web", Toast.LENGTH_SHORT).show();
            } else {
                String str = strSaveCustomer;
                Gson gson = new Gson();
                customerPLObj =  gson.fromJson(str, CustomerPL.class);

                if (customerPLObj.ErrorStatus == 0) {
                    Intent intnt = new Intent(CustomerInformationActivity.this, SOActivity.class);
                    intnt.putExtra("MyClass", (Serializable) customerPL);
                    startActivity(intnt);
                    CustomerInformationActivity.this.finish();

                    gson = new Gson();
                    customerPlObjStr = gson.toJson(customerPLObj);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("CustomerInfoJsonStr", customerPlObjStr);
                    editor.commit();
                } else {
                    Toast.makeText(CustomerInformationActivity.this, customerPLObj.Message, Toast.LENGTH_SHORT).show();
                }
            }

        }
    }

    private class SelectDoctorTask extends AsyncTask<String,String,String> {

        @Override
        protected String doInBackground(String... strings) {
            try {
//                String myUrl = AppConfig.url + "GetCustomer/Code/" + etCustCodeStr;
                String myUrl = AppConfig.url +"GetDoctors/name/"+acvSelectDoctrText;
                URL url = new URL(myUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                connection.setRequestMethod("GET");
                connection.setRequestProperty("user_key",user_name); // added by 1165 on 19-01-2019
                connection.setRequestProperty("auth_key","77973105B4274128BD4AB868A3C2B15B");
                connection.setRequestProperty("device_id",DeviceId);
                connection.setReadTimeout(15000);
                connection.setConnectTimeout(30000);
                connection.setRequestProperty("auth_key","77973105B4274128BD4AB868A3C2B15B");
                connection.setRequestProperty("device_id",DeviceId);

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
                    strFromSelectDoctor = sb.toString();
                    String temp = strFromSelectDoctor;

//                    {"Id":9,"Username":"500_9","Name":"500 User 9","Password":"","Email":"pavithrapurushan06@gmail.com","Phone":"","Active":0,"StoreId":50,"StoreCode":"500","StoreName":"SEPL-PHARMA WAREHOUSE","DeviceId":"","PasswordOtp":"","ErrorStatus":0,"Message":"Success"}

                } finally {
                    connection.disconnect();
                }

            } catch (Exception e) {
                Log.e("ERROR", e.getMessage(), e);
                return null;
            }
            return strFromSelectDoctor;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if (strFromSelectDoctor == null || strFromSelectDoctor.equals("")) {
                Toast.makeText(CustomerInformationActivity.this, "No result from web", Toast.LENGTH_SHORT).show();
            } else {
                String str = strFromSelectDoctor;
                GsonBuilder gsonb = new GsonBuilder();
                Gson gson = gsonb.create();
                Type mainListType = new TypeToken<ListDoctorDetails>() {}.getType();
                listDoctorDetailsObj = new ListDoctorDetails();
                listDoctorDetailsObj = gson.fromJson(str, mainListType);

                if (listDoctorDetailsObj.ErrorStatus == 0) {

                    if(listDoctorDetailsObj.filter.equals(acvSearchDoctor.getText().toString())) {
                        arrItems = new String[listDoctorDetailsObj.lst.size()]; // added by 1165 on 02-11-2018
                        for (int j = 0; j < listDoctorDetailsObj.lst.size(); j++) {
                            arrItems[j] = listDoctorDetailsObj.lst.get(j).Name;
                        }
                        if (arrItems.length > 0) { //takes too much time to display autocomplete textview
                            arradapter = new KArrayAdapter<String>(CustomerInformationActivity.this, android.R.layout.simple_dropdown_item_1line, arrItems);
                            acvSearchDoctor.setAdapter(arradapter);
                            acvSearchDoctor.showDropDown();
                        } else {
                            acvSearchDoctor.setAdapter(null);
                        }
                    }else{
                        acvSearchDoctor.setAdapter(null);
                    }
                }else{
                    acvSearchDoctor.setAdapter(null); // added by 1165 on 12-10-2018 partially removed no items displayed in listview after adding
//                                    Toast.makeText(ProductListActivity.this, "No item found", Toast.LENGTH_SHORT).show();
                    Toast.makeText(CustomerInformationActivity.this,listDoctorDetailsObj.Message,Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private class GetCustomerByCodeTask extends AsyncTask<String,String,String>{

        @Override
        protected String doInBackground(String... strings) {
            try {
//                String myUrl = AppConfig.url + "CheckLogin";
                String myUrl = AppConfig.url + "GetCustomer/Code/" + etCustCodeStr;
                URL url = new URL(myUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                connection.setRequestMethod("GET");
                connection.setRequestProperty("user_key",user_name); // added by 1165 on 19-01-2019
                connection.setRequestProperty("auth_key","77973105B4274128BD4AB868A3C2B15B");
                connection.setRequestProperty("device_id",DeviceId);
                connection.setReadTimeout(15000);
                connection.setConnectTimeout(30000);
                connection.setRequestProperty("auth_key","77973105B4274128BD4AB868A3C2B15B");
                connection.setRequestProperty("device_id",DeviceId);

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
                    strGetCustomerByCode = sb.toString();
                    String temp = strGetCustomerByCode;

//                    {"Id":9,"Username":"500_9","Name":"500 User 9","Password":"","Email":"pavithrapurushan06@gmail.com","Phone":"","Active":0,"StoreId":50,"StoreCode":"500","StoreName":"SEPL-PHARMA WAREHOUSE","DeviceId":"","PasswordOtp":"","ErrorStatus":0,"Message":"Success"}

                } finally {
                    connection.disconnect();
                }

            } catch (Exception e) {
                Log.e("ERROR", e.getMessage(), e);
                return null;
            }
            return strGetCustomerByCode;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if (strGetCustomerByCode == null || strGetCustomerByCode.equals("")) {
                Toast.makeText(CustomerInformationActivity.this, "No result from web", Toast.LENGTH_SHORT).show();
            }else {
                String str = strGetCustomerByCode;
                Gson gson = new Gson();
                listCustomerPLObj = gson.fromJson(str, ListCustomerPL.class);

                if (listCustomerPLObj.ErrorStatus == 0) {
//                                 et_input_cust_Huid.setText(listCustomerPLObj.lst.get(0).HUID);
                    etHUID.setText(listCustomerPLObj.lst.get(0).HUID);
                    etCustomerName.setText(listCustomerPLObj.lst.get(0).Name);
                    etCustomerMobile.setText(listCustomerPLObj.lst.get(0).Phone);
                    etAdrsLine1.setText(listCustomerPLObj.lst.get(0).AddressLine1);
                    etAdrsLine2.setText(listCustomerPLObj.lst.get(0).AddressLine2);
                } else {
                    etHUID.setText("");
                    etCustomerName.setText("");
                    etCustomerMobile.setText("");
                    etAdrsLine1.setText("");
                    etAdrsLine2.setText("");
                    Toast.makeText(CustomerInformationActivity.this, listCustomerPLObj.Message, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private class GetCustomerByHUIDTask extends AsyncTask<String,String,String> {

        @Override
        protected String doInBackground(String... strings) {
            try {
//                String myUrl = AppConfig.url + "CheckLogin";
//                String myUrl = AppConfig.url + "GetCustomer/Code/" + etCustCodeStr;
                String myUrl = AppConfig.url + "GetCustomer/HUID/" + etHUIDStr;
                URL url = new URL(myUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                connection.setRequestMethod("GET");
                connection.setRequestProperty("user_key",user_name); // added by 1165 on 19-01-2019

                connection.setRequestProperty("auth_key","77973105B4274128BD4AB868A3C2B15B");
                connection.setRequestProperty("device_id",DeviceId);
                connection.setReadTimeout(15000);
                connection.setConnectTimeout(30000);
                connection.setRequestProperty("auth_key","77973105B4274128BD4AB868A3C2B15B");
                connection.setRequestProperty("device_id",DeviceId);

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
                    strGetCustomerByHUID = sb.toString();
                    String temp = strGetCustomerByHUID;

//                    {"Id":9,"Username":"500_9","Name":"500 User 9","Password":"","Email":"pavithrapurushan06@gmail.com","Phone":"","Active":0,"StoreId":50,"StoreCode":"500","StoreName":"SEPL-PHARMA WAREHOUSE","DeviceId":"","PasswordOtp":"","ErrorStatus":0,"Message":"Success"}

                } finally {
                    connection.disconnect();
                }

            } catch (Exception e) {
                Log.e("ERROR", e.getMessage(), e);
                return null;
            }
            return strGetCustomerByHUID;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if (strGetCustomerByHUID == null || strGetCustomerByHUID.equals("")) {
                Toast.makeText(CustomerInformationActivity.this, "No result from web", Toast.LENGTH_SHORT).show();

            } else {
                String str = strGetCustomerByHUID;

                Gson gson = new Gson();
                listCustomerPLObj = gson.fromJson(str, ListCustomerPL.class);

                if (listCustomerPLObj.ErrorStatus == 0) {

                    etCustCode.setText(listCustomerPLObj.lst.get(0).Code);
                    etHUID.setText(listCustomerPLObj.lst.get(0).HUID);
                    etCustomerName.setText(listCustomerPLObj.lst.get(0).Name);
                    etCustomerMobile.setText(listCustomerPLObj.lst.get(0).Phone);
                    etAdrsLine1.setText(listCustomerPLObj.lst.get(0).AddressLine1);
                    etAdrsLine2.setText(listCustomerPLObj.lst.get(0).AddressLine2);

//            {"lst":[{"Id":2,"Code":"1105","HUID":"H1105","Name":"Dhinesh M N","Phone":"9048964563","AddressLine1":"","AddressLine2":""}],"ErrorStatus":0,"Message":"Success"}

                } else {

                    etCustCode.setText("");
                    etCustomerName.setText("");
                    etCustomerMobile.setText("");
                    etAdrsLine1.setText("");
                    etAdrsLine2.setText("");
                    Toast.makeText(CustomerInformationActivity.this, listCustomerPLObj.Message, Toast.LENGTH_SHORT).show();

                }
            }
        }
    }
}

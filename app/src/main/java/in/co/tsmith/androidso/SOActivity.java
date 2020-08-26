package in.co.tsmith.androidso;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


//Created by Pavithra on 22-08-2020
//Content added by Pavithra on 24-08-2020

public class SOActivity extends AppCompatActivity {

    LinearLayout llSOToolbar;
    LinearLayout llSOBottombar;
    double llSOToolbarHeight;
    double llSOBottombarHeight;
    double prescriptionDialogWindowHeight;
    double prescriptionListDialogWindowHeight;

    TextView tvCustomerName;

    SharedPreferences prefs;

    ImageButton ic_search;

    Dialog dialog;
    ImageButton imgBtnItemSearchLookup;
//    EditText etItemSearchLookup;
    AutoCompleteTextView etItemSearchLookup;
    String itemSearchStringLookup = "";
    TsCommonMethods tsCommonMethods;

    String user_name = "";
    String DeviceId = "";
    String strGetItems = "";

    ListItemDetails listItemDetailsObj;

    String filterGlobal = "";
    String[] arrItems;
    KArrayAdapter<String> arradapter;

    ListView lvItemsPopup;
    ListView lvProductlist;

    TextView tvTotalAmountValue;
    Button btnAddPrescription;

    public static int TAKE_IMAGE = 111;
    public static int SELECT_IMAGE = 222;

    String mCurrentPhotoPath = "";

    String strFileName = "";
    List<ItemDetails> listItemDetails;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_salesorder);

        llSOToolbar = (LinearLayout)findViewById(R.id.llSOToolbar);
        llSOBottombar = (LinearLayout)findViewById(R.id.llSOBottombar);
        tvCustomerName = (TextView) findViewById(R.id.tvCustomerName);
        tvTotalAmountValue = (TextView) findViewById(R.id.tvAmountValue);
        ic_search = (ImageButton) findViewById(R.id.imgBtnSearchItem);
        lvProductlist = (ListView) findViewById(R.id.lvProductlist);

        btnAddPrescription = (Button)findViewById(R.id.btnAddPrescription);

        tsCommonMethods = new TsCommonMethods(this);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screen_height = displayMetrics.heightPixels;
        int screen_width = displayMetrics.widthPixels;

        llSOToolbarHeight = (screen_height * 8.75)/100;  //    56/640
        llSOBottombarHeight = (screen_height * 18.75)/100;  //   120/640   -- actual height in figma is 103 but here rounded and taken as 120
        prescriptionDialogWindowHeight = (screen_height * 37.97)/100;  //   243/640
        prescriptionListDialogWindowHeight = (screen_height * 74)/100;  //  474/640

        LinearLayout.LayoutParams paramsllHeader = (LinearLayout.LayoutParams) llSOToolbar.getLayoutParams();
        paramsllHeader.height = (int) llSOToolbarHeight;
        paramsllHeader.width = LinearLayout.LayoutParams.MATCH_PARENT;
        llSOToolbar.setLayoutParams(paramsllHeader);

        LinearLayout.LayoutParams paramsBottombar = (LinearLayout.LayoutParams) llSOBottombar.getLayoutParams();
        paramsBottombar.height = (int) llSOBottombarHeight;
        paramsBottombar.width = LinearLayout.LayoutParams.MATCH_PARENT;
        llSOBottombar.setLayoutParams(paramsBottombar);

        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String customerInfoJsonStr = prefs.getString("CustomerInfoJsonStr","");

        Gson gson = new Gson();
        CustomerPL customerPLObj =  gson.fromJson(customerInfoJsonStr, CustomerPL.class);
        tvCustomerName.setText(customerPLObj.Name);

        listItemDetails = new ArrayList<>();

        ic_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog = new Dialog(SOActivity.this);
                dialog.setContentView(R.layout.item_lookup_dialogwindow);
                dialog.setCanceledOnTouchOutside(false);
                dialog.setTitle("Product Lookup");
                dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
//                etItemSearchLookup = (EditText) dialog.findViewById(R.id.etSearchItemLookup);  //commented by Pavithra on 25-08-2020
                etItemSearchLookup = (AutoCompleteTextView) dialog.findViewById(R.id.etSearchItemLookup);
                imgBtnItemSearchLookup = (ImageButton) dialog.findViewById(R.id.imgBtnItemSearchLookup);
                lvItemsPopup = (ListView) dialog.findViewById(R.id.lvItems);

                imgBtnItemSearchLookup.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        itemSearchStringLookup = etItemSearchLookup.getText().toString();
                        if (itemSearchStringLookup.length() >= 3) {
                            if(tsCommonMethods.isNetworkConnected()) {
//                                RelativeLayout mainsearch = (RelativeLayout) dialog.findViewById(R.id.mainsearch);
//                                productlist.clear();
//                                productlistview.setAdapter(null);

//                                GetProductLookup getProductLookup = new GetProductLookup();
//                                getProductLookup.execute();

                                new GetItemsTask().execute();
//                                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//                                imm.hideSoftInputFromWindow(mainsearch.getWindowToken(), 0);

                            }else{
                                Toast.makeText(SOActivity.this, "No network connectivity", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(SOActivity.this, "Input atleast 3 characters", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                ImageButton imgBtnCloseItemLookup = (ImageButton) dialog.findViewById(R.id.imgBtnCloseItemLookup);

                //Added by 1165 on 22-02-2020
//                productlistview = (ListView) dialog.findViewById(R.id.product_listview);
//                productLookupAdapter = new ProductLookupAdapter(productlist, listModel, l2, billno, numofitems, itemtotal, disctotal, taxtotal, billdisc, billroundoff, billtotal, SalesActivity.this, dialog,tvtotalLinewiseDiscount);
//                productlistview.setAdapter(productLookupAdapter);

                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                lp.copyFrom(dialog.getWindow().getAttributes());
                lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                lp.height = WindowManager.LayoutParams.MATCH_PARENT;
                lp.gravity = Gravity.CENTER;
                dialog.getWindow().setAttributes(lp);

                imgBtnCloseItemLookup.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
//                        productlist.clear();
                    }
                });

                lvItemsPopup.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                        Toast.makeText(SOActivity.this, ""+listItemDetailsObj.lst.get(i).Name, Toast.LENGTH_SHORT).show();

                    }
                });
                dialog.show();

            }
        });

        btnAddPrescription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog = new Dialog(SOActivity.this);
                dialog.setContentView(R.layout.add_prescription_dialogwindow);
                dialog.setCanceledOnTouchOutside(false);
                dialog.setTitle("Product Lookup");
                dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

                ImageButton imgBtnCloseAddPrescrptn = (ImageButton) dialog.findViewById(R.id.imgBtnCloseAddPrescrptn);
                ImageButton imgBtnPrescrptnFromCamera = (ImageButton) dialog.findViewById(R.id.imgBtnPrescrptnFromCamera);
                ImageButton imgBtnPrescrptnInsertFromfile = (ImageButton) dialog.findViewById(R.id.imgBtnPrescrptnInsertFromfile);

                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                lp.copyFrom(dialog.getWindow().getAttributes());
                lp.width = WindowManager.LayoutParams.MATCH_PARENT;
//                lp.height = WindowManager.LayoutParams.MATCH_PARENT;
                lp.height = (int) prescriptionDialogWindowHeight;
                lp.gravity = Gravity.CENTER;
                dialog.getWindow().setAttributes(lp);

                imgBtnCloseAddPrescrptn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });

                imgBtnPrescrptnFromCamera.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        //click Prescription
                        try {
                            UUID uuid = UUID.randomUUID();
                            String captured_img_file_name = "IMG_" + String.valueOf(uuid)+ "_"+ ".jpg"; //name of the image created
                            File newfile =  new File(Environment.getExternalStorageDirectory(), captured_img_file_name); //complete path of the image taken
                            captured_img_file_name = newfile.getAbsolutePath();
                            Uri outputFileUri = FileProvider.getUriForFile(SOActivity.this, BuildConfig.APPLICATION_ID + ".provider",newfile); // this line is addeded instead of above becuse in oreo camera not opening
                            mCurrentPhotoPath = captured_img_file_name;
                            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
                            startActivityForResult(cameraIntent, TAKE_IMAGE);
                        } catch (Exception e) {
                            Log.e("Camera", "" + e);
                        }

                    }
                });

                imgBtnPrescrptnInsertFromfile.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Intent intnt = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                        startActivityForResult(intnt, SELECT_IMAGE);

                    }
                });

                dialog.show();

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if ((requestCode == TAKE_IMAGE) && (resultCode == RESULT_OK)) {

            String imgName = mCurrentPhotoPath;
            strFileName = imgName.substring(imgName.lastIndexOf("/") + 1);

            prefs = PreferenceManager.getDefaultSharedPreferences(this);
            String addedPrescrptnsJsonStr = prefs.getString("ListOfPrescrptnsAdded","");

            Gson gson = new Gson();
            if(!addedPrescrptnsJsonStr.equals("") && addedPrescrptnsJsonStr != null) {

                listItemDetails = gson.fromJson(addedPrescrptnsJsonStr, new TypeToken<List<ItemDetails>>() {
                }.getType());
            }


            ItemDetails itemDetailsObj = new ItemDetails();
            itemDetailsObj.Name = "kdfhg";
            itemDetailsObj.Amount = "0";
            itemDetailsObj.Qty = "0";
            itemDetailsObj.filename = strFileName;
            itemDetailsObj.filepath = mCurrentPhotoPath;
            listItemDetails.add(itemDetailsObj);

            gson = new Gson();
            String prescrptnsPLJsonstr = gson.toJson(listItemDetails);

            prefs = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("ListOfPrescrptnsAdded", prescrptnsPLJsonstr);
            editor.putBoolean("IsSaved", false);
            editor.commit();

            String[] arr = new String[listItemDetails.size()];
            for (int i = 0; i < listItemDetails.size(); i++) {
                arr[i] = listItemDetails.get(i).Name;
            }



            dialog = new Dialog(SOActivity.this);
            dialog.setContentView(R.layout.prescriptionlist_dialogwindow);
            dialog.setCanceledOnTouchOutside(false);
            dialog.setTitle("Prescriptionlist");
            dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

            ListView lvPrescriptionlist = (ListView) dialog.findViewById(R.id.lvPrescriptionlist);
            ImageButton imgBtnClosePrescrptnlistWindow = (ImageButton) dialog.findViewById(R.id.imgBtnClosePrescrptnlistWindow);

            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(dialog.getWindow().getAttributes());
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = (int) prescriptionListDialogWindowHeight;
            lp.gravity = Gravity.CENTER;
            dialog.getWindow().setAttributes(lp);

            imgBtnClosePrescrptnlistWindow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });

            PrescriptionAdapter arrayAdapter = new PrescriptionAdapter(this, arr, listItemDetails);
            lvPrescriptionlist.setAdapter(arrayAdapter);

            dialog.show();


//            PrescriptionAdapter arrayAdapter = new PrescriptionAdapter(this, arr, listItemDetails);
//            lvPrescriptions.setAdapter(arrayAdapter);

        }else if((requestCode == SELECT_IMAGE) && (resultCode == RESULT_OK)){
//            Uri selectedImage = data.getData();
//            String[] filePathColumn = {MediaStore.Images.Media.DATA};
//
//            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
//            cursor.moveToFirst();
//
//            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
//            String filePath = cursor.getString(columnIndex);
//            cursor.close();
//
//            String imgName = filePath;
////            String strFileName = imgName.substring(imgName.lastIndexOf("/") + 1);  // original
//            //Changing the name of imported images in order to avoid conflicts of duplication
//            UUID uuid = UUID.randomUUID();
//            String captured_img_file_name = "IMG_" + String.valueOf(uuid)+ "_"+ ".jpg"; //n
//            String strFileName = captured_img_file_name;
//
//            prefs = PreferenceManager.getDefaultSharedPreferences(this);
//            String addedPrescrptnsJsonStr = prefs.getString("ListOfPrescrptnsAdded","");
//
//            Gson gson = new Gson();
//            if(!addedPrescrptnsJsonStr.equals("") && addedPrescrptnsJsonStr != null) {
//                listItemDetails = gson.fromJson(addedPrescrptnsJsonStr, new TypeToken<List<ItemDetails>>() {
//                }.getType());
//            }
//            ItemDetails itemDetailsObj = new ItemDetails();
//            itemDetailsObj.Name = "kdfhg";
//            itemDetailsObj.Amount = "0";
//            itemDetailsObj.Qty = "0";
//            itemDetailsObj.filename = strFileName;
//            itemDetailsObj.filepath = filePath;
//            listItemDetails.add(itemDetailsObj);
//
//            gson = new Gson();
//            String prescrptnsPLJsonstr = gson.toJson(listItemDetails);
//
//            prefs = PreferenceManager.getDefaultSharedPreferences(this);
//            SharedPreferences.Editor editor = prefs.edit();
//            editor.putString("ListOfPrescrptnsAdded", prescrptnsPLJsonstr);
//            editor.putBoolean("IsSaved", false);
//            editor.commit();
//
//            String[] arr = new String[listItemDetails.size()];
//            for (int i = 0; i < listItemDetails.size(); i++) {
//                arr[i] = listItemDetails.get(i).Name;
//            }
//            ProductListActPrescriptionsAdapter arrayAdapter = new ProductListActPrescriptionsAdapter(this, arr, listItemDetails);
//            lvPrescriptions.setAdapter(arrayAdapter);
        }
    }

    private class GetItemsTask extends AsyncTask<String,String,String>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
//            custPlObjstr = "";
            String result = "";
            String inputLine;

            prefs = PreferenceManager.getDefaultSharedPreferences(SOActivity.this);
            user_name = prefs.getString("TSSOUsername", "");
            tsCommonMethods = new TsCommonMethods(SOActivity.this);
            DeviceId = tsCommonMethods.GetDeviceUniqueId();

            try {
                String Url = AppConfig.url + "GetItems/name/" + itemSearchStringLookup;
                URL myUrl = new URL(Url);
                //Create a connection
                HttpURLConnection connection =(HttpURLConnection)
                        myUrl.openConnection();

                connection.setRequestMethod("GET");
                connection.setRequestProperty("user_key",user_name); // added by 1165 on 16-01-2020
                connection.setReadTimeout(15000);
                connection.setConnectTimeout(30000);
                connection.setRequestProperty("auth_key","77973105B4274128BD4AB868A3C2B15B");


                connection.connect();
                InputStreamReader streamReader = new
                        InputStreamReader(connection.getInputStream());
                BufferedReader reader = new BufferedReader(streamReader);
                StringBuilder stringBuilder = new StringBuilder();
                //Check if the line we are reading is not null
                while((inputLine = reader.readLine()) != null){
                    stringBuilder.append(inputLine);
                }
                //Close our InputStream and Buffered reader
                reader.close();
                streamReader.close();
                //Set our result equal to our stringBuilder
                result = stringBuilder.toString();
                strGetItems = result;
                String str = result;

            }catch (IOException ex){
                ex.printStackTrace();
                result = null;
            }
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if (strGetItems == null || strGetItems.equals("")) {
                Toast.makeText(SOActivity.this, "No result from web", Toast.LENGTH_SHORT).show();
            } else {
                String str = strGetItems;
                GsonBuilder gsonb = new GsonBuilder();
                Gson gson = gsonb.create();
                Type mainListType = new TypeToken<ListItemDetails>() {
                }.getType();
                listItemDetailsObj = new ListItemDetails();
                listItemDetailsObj = gson.fromJson(str, mainListType);
                if (listItemDetailsObj.ErrorStatus == 0) {
/*Added by Pavithra on 25-08-2020****************************************************************************/
                    prefs = PreferenceManager.getDefaultSharedPreferences(SOActivity.this);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("ListItemDetailsObjStr",strGetItems);
                    editor.commit();
/***************************************************************************************************/
                    filterGlobal = listItemDetailsObj.filter;
                    if (listItemDetailsObj.filter.equals(etItemSearchLookup.getText().toString())) {
                        arrItems = new String[listItemDetailsObj.lst.size()]; // added by 1165 on 02-11-2018
                        for (int j = 0; j < listItemDetailsObj.lst.size(); j++) {
                            arrItems[j] = listItemDetailsObj.lst.get(j).Name;
                        }
                        if (arrItems.length > 0) { //takes too much time to display autocomplete textview
//                            public ItemListCustomAdapter(Context context,String[] v1, List<ItemDetails> listItemDetailsPL) {
                            ItemListCustomAdapter itemListCustomAdapter = new ItemListCustomAdapter(SOActivity.this,arrItems,listItemDetailsObj.lst);

//                            productLookupAdapter = new ProductLookupAdapter(productlist, listModel, l2, billno, numofitems, itemtotal, disctotal, taxtotal, billdisc, billroundoff, billtotal, SalesActivity.this, dialog,tvtotalLinewiseDiscount);
                            lvItemsPopup.setAdapter(itemListCustomAdapter);

//                            arradapter = new KArrayAdapter<String>(SOActivity.this, android.R.layout.simple_dropdown_item_1line, arrItems);
//                            etItemSearchLookup.setAdapter(arradapter);
//                            etItemSearchLookup.showDropDown();

                        } else {
//                            etItemSearchLookup.setAdapter(null);
                            lvItemsPopup.setAdapter(null);
                        }
                    } else {
//                        etItemSearchLookup.setAdapter(null);
                        lvItemsPopup.setAdapter(null);
                    }
                } else {
//                    etItemSearchLookup.setAdapter(null); // added by 1165 on 12-10-2018 partially removed no items displayed in listview after adding
                    lvItemsPopup.setAdapter(null); // added by 1165 on 12-10-2018 partially removed no items displayed in listview after adding
                    Toast.makeText(SOActivity.this, listItemDetailsObj.Message, Toast.LENGTH_LONG).show();
                }
            }
        }
    }
}

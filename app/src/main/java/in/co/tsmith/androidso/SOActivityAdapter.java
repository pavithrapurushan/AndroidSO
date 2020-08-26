package in.co.tsmith.androidso;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class SOActivityAdapter extends ArrayAdapter<String> {

    Context cntext;
    List<ItemDetails> listItemDetails;
    String[] values;

    TextView tvTotalAmount;

    SharedPreferences prefs;
    Dialog qtydialog;

    int selected_item_id = 0;
    String itemname =  "";
    String mrp =  "";
    String uperpack =  "";

    TextView tvSelectedItemName;
    TextView tvMrpInQtySelection;
    TextView tvUperpackInQtySelection;

    double Mrp ;
    int uper_pack ;

    int QtyInUnits = 0;
    int qtyInPacks = 0;
    int qty_Units_from_et = 0;

    int medicineQty;
    double TotAmt;
    String qty_dlg = "";
    String listOfItemsAddedStr = "";
    List<ItemDetails> itemDetailsPLList;

    ListItemDetails listItemDetailsObj ;
    ItemDetails itemDetailsObj;

    boolean isSaved = false;




    public SOActivityAdapter(Context context, String[] v1, List<ItemDetails> listItemDetailsPL, TextView tvTotalAmnt) {
        super(context, R.layout.activity_salesorder, v1);
        cntext = context;
        values = v1;
        listItemDetails = listItemDetailsPL;
        tvTotalAmount = tvTotalAmnt;
        prefs = PreferenceManager.getDefaultSharedPreferences(cntext);
    }


    @Override

    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) cntext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.item_lv_salesorder, parent, false);

        TextView tvSlNo = (TextView) rowView.findViewById(R.id.tvSlNo);
        TextView tvItem = (TextView) rowView.findViewById(R.id.tvProductName);
        TextView tvMrp = (TextView) rowView.findViewById(R.id.tvMRP);
        TextView tvQty = (TextView) rowView.findViewById(R.id.tvQty);
        TextView tvTotal = (TextView) rowView.findViewById(R.id.tvTotal);
        ImageButton btnDelete = (ImageButton) rowView.findViewById(R.id.btnDeleteItem);
        btnDelete.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                showDeletePopUP(position);
            }
        });

        //following added by Pavithra on 26-08-2020
        tvQty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(cntext, "Qty clicked", Toast.LENGTH_SHORT).show();
                Toast.makeText(cntext, ""+listItemDetails.get(position).Name, Toast.LENGTH_SHORT).show();
                itemname = listItemDetails.get(position).Name;
                mrp = listItemDetails.get(position).Mrp;
                uperpack = listItemDetails.get(position).Uperpack;
                selected_item_id =  listItemDetails.get(position).Id;

                qtydialog = new Dialog(cntext);
                qtydialog.setContentView(R.layout.quantity_selection_dialogwindow);
                qtydialog.setCanceledOnTouchOutside(false);
                qtydialog.setTitle("Quantity Selection");
                qtydialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);



                tvSelectedItemName = (TextView) qtydialog.findViewById(R.id.tvSelectedItemName); //setting product name
                tvMrpInQtySelection = (TextView) qtydialog.findViewById(R.id.tvMrpInQtySelection);
                tvUperpackInQtySelection = (TextView) qtydialog.findViewById(R.id.tvUperpackInQtySelection);

                tvSelectedItemName.setText(""+itemname);
                tvMrpInQtySelection.setText("MRP: "+mrp);
                tvUperpackInQtySelection.setText("UPerPack: "+uperpack);


                ImageButton btnPlusCountPacks = (ImageButton)qtydialog.findViewById(R.id.imgBtnPlusPack);
                ImageButton btnMinusCountPacks = (ImageButton)qtydialog.findViewById(R.id.imgBtnMinusPack);
                ImageButton btnPlusCountUnits = (ImageButton)qtydialog.findViewById(R.id.imgBtnPlusUnits);
                ImageButton btnMinusCountUnits = (ImageButton)qtydialog.findViewById(R.id.imgBtnMinusUnits);
                Button btnAddItem = (Button) qtydialog.findViewById(R.id.btnAddItem_qtySelection);


                final EditText etCountPacks = (EditText) qtydialog.findViewById(R.id.etQtyInPacks);
                final EditText etCountUnits = (EditText) qtydialog.findViewById(R.id.etQtyInUnits);
                TextView tvPack = (TextView)qtydialog.findViewById(R.id.tvPacks);
                final TextView tvQtyInUnits = (TextView)qtydialog.findViewById(R.id.tvQtyInUnits_qtySelection);
                final TextView tvTotAmt = (TextView) qtydialog.findViewById(R.id.tvTotalAmt_QtySelection);

                Mrp = Double.valueOf(mrp);
                uper_pack = Integer.parseInt(uperpack);

                if(uperpack.equals("1")){
                    btnPlusCountPacks.setVisibility(View.GONE);
                    btnMinusCountPacks.setVisibility(View.GONE);
                    etCountPacks.setVisibility(View.GONE);
                    tvPack.setVisibility(View.GONE);

                    etCountUnits.setText("1");

                    qty_Units_from_et = Integer.parseInt(etCountUnits.getText().toString());
                    QtyInUnits = qty_Units_from_et;
                    tvQtyInUnits.setText("Qty in units : "+QtyInUnits);

                    TotAmt =( Mrp/uper_pack) *QtyInUnits;
                    tvTotAmt.setText("Total Amount: ₹ "+String.format("%.2f",+TotAmt));

                }else {
                    btnPlusCountPacks.setVisibility(View.VISIBLE);
                    btnMinusCountPacks.setVisibility(View.VISIBLE);
                    etCountPacks.setVisibility(View.VISIBLE);
                    tvPack.setVisibility(View.VISIBLE);

                    qtyInPacks = Integer.parseInt(etCountPacks.getText().toString());
                    qty_Units_from_et = Integer.parseInt(etCountUnits.getText().toString());

                    QtyInUnits = (qtyInPacks * uper_pack) + qty_Units_from_et;
                    tvQtyInUnits.setText("Qty in units : "+QtyInUnits);

                    TotAmt =( Mrp/uper_pack) *QtyInUnits;
                    tvTotAmt.setText("Total Amount: ₹ "+String.format("%.2f",+TotAmt));
                }
                btnPlusCountPacks.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(etCountPacks.getText().toString().equals("")||etCountPacks.getText().toString() == null) {
                            etCountPacks.setText("0");
                        }
                        if (etCountUnits.getText().toString().equals("") || etCountUnits.getText().toString() == null) {
                            etCountUnits.setText("0");
                        }

                        medicineQty = Integer.parseInt(etCountPacks.getText().toString());
                        medicineQty += 1;
                        etCountPacks.setText("" + medicineQty);
                        qtyInPacks = Integer.parseInt(etCountPacks.getText().toString());
                        qty_Units_from_et = Integer.parseInt(etCountUnits.getText().toString());
                        QtyInUnits = (qtyInPacks * uper_pack) + qty_Units_from_et;
                        tvQtyInUnits.setText("Qty in units : " + QtyInUnits);
                        TotAmt = (Mrp / uper_pack) * QtyInUnits;
                        tvTotAmt.setText("Total Amount: ₹ " + String.format("%.2f", +TotAmt));
                    }
                });

                btnMinusCountPacks.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(!etCountPacks.getText().toString().equals("")&&etCountPacks.getText().toString() != null) {
                            if (!etCountUnits.getText().toString().equals("") && etCountUnits.getText().toString() != null) {

                                medicineQty = Integer.parseInt(etCountPacks.getText().toString());
                                if(medicineQty >= 1)
                                    medicineQty -= 1;
                                etCountPacks.setText(""+medicineQty);
                                qtyInPacks = Integer.parseInt(etCountPacks.getText().toString());
                                qty_Units_from_et = Integer.parseInt(etCountUnits.getText().toString());
                                QtyInUnits = (qtyInPacks * uper_pack) + qty_Units_from_et;
                                tvQtyInUnits.setText("Qty in units : "+QtyInUnits);

                                TotAmt =( Mrp/uper_pack) *QtyInUnits;
                                tvTotAmt.setText("Total Amount: ₹ "+String.format("%.2f",+TotAmt));
                            }else {
                                Toast.makeText(cntext, "Unit field is empty", Toast.LENGTH_SHORT).show();
                            }
                        }else{
                            Toast.makeText(cntext, "Pack field is empty", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                btnPlusCountUnits.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(etCountPacks.getText().toString().equals("")||etCountPacks.getText().toString() == null) {
                            etCountPacks.setText("0");

                        }
                        if (etCountUnits.getText().toString().equals("") || etCountUnits.getText().toString() == null) {
                            etCountUnits.setText("0");
                        }

                        medicineQty = Integer.parseInt(etCountUnits.getText().toString());
                        qtyInPacks = Integer.parseInt(etCountPacks.getText().toString());
                        if (uper_pack > 1) {
                            if (medicineQty < (uper_pack - 1)) {
                                medicineQty += 1;
                            }
                        } else {
                            medicineQty += 1;
                            qtyInPacks = 0;
                        }
                        etCountUnits.setText("" + medicineQty);
                        qty_Units_from_et = Integer.parseInt(etCountUnits.getText().toString());
                        QtyInUnits = (qtyInPacks * uper_pack) + qty_Units_from_et;
                        tvQtyInUnits.setText("Qty in units : " + QtyInUnits);

                        TotAmt = (Mrp / uper_pack) * QtyInUnits;
                        tvTotAmt.setText("Total Amount: ₹ " + String.format("%.2f", +TotAmt));
                    }
                });

                btnMinusCountUnits.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if(!etCountPacks.getText().toString().equals("")&&etCountPacks.getText().toString() != null) {
                            if (!etCountUnits.getText().toString().equals("") && etCountUnits.getText().toString() != null) {

                                medicineQty = Integer.parseInt(etCountUnits.getText().toString());


                                if (medicineQty >= 1)
                                    medicineQty -= 1;
                                if(uper_pack == 1 && medicineQty == 0)
                                    medicineQty = 1;


                                etCountUnits.setText("" + medicineQty);

                                qtyInPacks = Integer.parseInt(etCountPacks.getText().toString());
                                if(uper_pack ==1){
                                    qtyInPacks = 0;

                                }
                                qty_Units_from_et = Integer.parseInt(etCountUnits.getText().toString());
                                QtyInUnits = (qtyInPacks * uper_pack) + qty_Units_from_et;
                                tvQtyInUnits.setText("Qty in units : " + QtyInUnits);

                                TotAmt = (Mrp / uper_pack) * QtyInUnits;
                                tvTotAmt.setText("Total Amount: ₹ " + String.format("%.2f", +TotAmt));
                            } else {
                                Toast.makeText(cntext, "Unit field is empty", Toast.LENGTH_SHORT).show();
                            }
                        }else {
                            Toast.makeText(cntext, "Pack field is empty", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                etCountPacks.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        try {
                            if (!etCountPacks.getText().toString().equals("") && etCountPacks.getText().toString() != null) {
                                if (!etCountUnits.getText().toString().equals("") && etCountUnits.getText().toString() != null) {

                                    medicineQty = Integer.parseInt(etCountPacks.getText().toString());

                                    qtyInPacks = Integer.parseInt(etCountPacks.getText().toString());
                                    qty_Units_from_et = Integer.parseInt(etCountUnits.getText().toString());
                                    QtyInUnits = (qtyInPacks * uper_pack) + qty_Units_from_et;
                                    tvQtyInUnits.setText("Qty in units : " + QtyInUnits);
                                    TotAmt = (Mrp / uper_pack) * QtyInUnits;

                                    tvTotAmt.setText("Total Amount: ₹ " + String.format("%.2f", +TotAmt));
                                } else {
                                    Toast.makeText(cntext, "Unit field is empty", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                qtyInPacks = 0; // added by 1165 on 25-02-2019
                                // below 4 lines added by 1165 on 25-02-2019 for testing
                                QtyInUnits = (qtyInPacks * uper_pack) + qty_Units_from_et;
                                tvQtyInUnits.setText("Qty in units : " + QtyInUnits);
                                TotAmt = (Mrp / uper_pack) * QtyInUnits;
                                tvTotAmt.setText("Total Amount: ₹ " + String.format("%.2f", +TotAmt));
                            }
                        }catch (Exception ex){
                            Log.e("TAG",""+ex);
                        }
                    }
                });

                etCountUnits.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                        try {
                            if (!etCountPacks.getText().toString().equals("") && etCountPacks.getText().toString() != null) {
                                if (!etCountUnits.getText().toString().equals("") && etCountUnits.getText().toString() != null) {


                                    medicineQty = Integer.parseInt(etCountUnits.getText().toString());
                                    if (uper_pack > 1) {
                                        if (medicineQty < (uper_pack)) {
                                            qtyInPacks = Integer.parseInt(etCountPacks.getText().toString());
                                            qty_Units_from_et = Integer.parseInt(etCountUnits.getText().toString());
                                            QtyInUnits = (qtyInPacks * uper_pack) + qty_Units_from_et;
                                            tvQtyInUnits.setText("Qty in units : " + QtyInUnits);

                                            TotAmt = (Mrp / uper_pack) * QtyInUnits;
                                            tvTotAmt.setText("Total Amount: ₹ " + String.format("%.2f", +TotAmt));

                                        } else {
                                            Toast.makeText(cntext, "Cannnot add a qty greater than Uperpack", Toast.LENGTH_SHORT).show();
                                        }
                                    } else {


                                        qty_Units_from_et = Integer.parseInt(etCountUnits.getText().toString());
                                        qtyInPacks = 0;
                                        QtyInUnits = (qtyInPacks * uper_pack) + qty_Units_from_et;
                                        tvQtyInUnits.setText("Qty in units : " + QtyInUnits);
                                        TotAmt = (Mrp / uper_pack) * QtyInUnits;
                                        tvTotAmt.setText("Total Amount: ₹ " + String.format("%.2f", +TotAmt));
                                    }
                                } else {
                                    // added by 1165 on 25-02-2019 below lines
                                    qtyInPacks = Integer.parseInt(etCountPacks.getText().toString());
                                    qty_Units_from_et = 0;
                                    QtyInUnits = (qtyInPacks * uper_pack) + qty_Units_from_et;
                                    tvQtyInUnits.setText("Qty in units : " + QtyInUnits);
                                    TotAmt = (Mrp / uper_pack) * QtyInUnits;
                                    tvTotAmt.setText("Total Amount: ₹ " + String.format("%.2f", +TotAmt));

                                    Toast.makeText(cntext, "Unit field is empty", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(cntext, "Pack field is empty", Toast.LENGTH_SHORT).show();
                            }
                        }catch(Exception ex){
                            Log.e("TAG",""+ex);
                        }
                    }
                });

                btnAddItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Gson gson = new Gson();
                        listOfItemsAddedStr = prefs.getString("ListOfItemsAdded","");
                        itemDetailsPLList = gson.fromJson(listOfItemsAddedStr, new TypeToken<List<ItemDetails>>() {
                        }.getType());



                        qty_dlg = etCountPacks.getText().toString();

                        if(qty_dlg ==  null || qty_dlg.equals("") ){
                            Toast.makeText(cntext, "Qty can not be zero or empty", Toast.LENGTH_SHORT).show();
                        }else {

                            if (uper_pack > 1) {
                                if (medicineQty >= uper_pack) {
                                    Toast.makeText(cntext, "Cannot save, medicine qty is greater than uperpack", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                            }
                            qtydialog.dismiss();

                            //following two lines added by PAvithra on 25-08-2020
                            if (itemDetailsPLList == null || itemDetailsPLList.equals("")) {
                                itemDetailsPLList = new ArrayList<>();
                            }

//                            prefs = PreferenceManager.getDefaultSharedPreferences(cntext);
//                            String ListItemDetailsObjStr = prefs.getString("ListItemDetailsObjStr", "");
//                            GsonBuilder gsonb = new GsonBuilder();
//                            gson = gsonb.create();
//                            Type mainListType = new TypeToken<ListItemDetails>() {
//                            }.getType();
//                            listItemDetailsObj = new ListItemDetails();
//                            listItemDetailsObj = gson.fromJson(ListItemDetailsObjStr, mainListType);


//                            for (int i = 0; i < listItemDetailsObj.lst.size(); i++) {
//                                if (selected_item_id == listItemDetailsObj.lst.get(i).Id) {
//                                    itemDetailsObj = new ItemDetails();
//                                    itemDetailsObj.Id = selected_item_id;
//                                    itemDetailsObj.Name = tvSelectedItemName.getText().toString();
//                                    Double mrpWeb = Double.valueOf(listItemDetailsObj.lst.get(i).Mrp);
//                                    itemDetailsObj.Mrp = String.format("%.2f", mrpWeb);
//                                    itemDetailsObj.Uperpack = listItemDetailsObj.lst.get(i).Uperpack;
//                                    itemDetailsObj.Qty = String.valueOf(QtyInUnits);
//                                    itemDetailsObj.QtyType = 0; //added by 1165 on 22-02-2019
//                                    int qty = Integer.parseInt(itemDetailsObj.Qty);
//                                    Double mrp = Double.valueOf(itemDetailsObj.Mrp);
//
//                                    Double totAmount = qty * mrp;
//                                    itemDetailsObj.Amount = String.format("%.2f", TotAmt);
//                                    itemDetailsPLList.add(itemDetailsObj);
//
//                                }
//                            }



                            for (int i = 0; i < itemDetailsPLList.size(); i++) {
                                if (selected_item_id == itemDetailsPLList.get(i).Id) {

                                    itemDetailsPLList.get(i).Id = selected_item_id;
                                    itemDetailsPLList.get(i).Qty = String.valueOf(QtyInUnits);;

                                    int qty = Integer.parseInt(itemDetailsPLList.get(i).Qty);
                                    Double mrp = Double.valueOf(itemDetailsPLList.get(i).Mrp);

                                    Double totAmount = qty * mrp;
                                    itemDetailsPLList.get(i).Amount = String.format("%.2f", TotAmt);


//                                    itemDetailsObj = new ItemDetails();
//                                    itemDetailsObj.Id = selected_item_id;
//                                    itemDetailsObj.Name = tvSelectedItemName.getText().toString();
//                                    Double mrpWeb = Double.valueOf(listItemDetailsObj.lst.get(i).Mrp);
//                                    itemDetailsObj.Mrp = String.format("%.2f", mrpWeb);
//                                    itemDetailsObj.Uperpack = listItemDetailsObj.lst.get(i).Uperpack;
//                                    itemDetailsObj.Qty = String.valueOf(QtyInUnits);
//                                    itemDetailsObj.QtyType = 0; //added by 1165 on 22-02-2019
//                                    int qty = Integer.parseInt(itemDetailsObj.Qty);
//                                    Double mrp = Double.valueOf(itemDetailsObj.Mrp);
//
//                                    Double totAmount = qty * mrp;
//                                    itemDetailsObj.Amount = String.format("%.2f", TotAmt);
//                                    itemDetailsPLList.add(itemDetailsObj);

                                }
                            }

                            gson = new Gson();
                            String ListOfItemsAddedStr = gson.toJson(itemDetailsPLList);
                            Double totamnt = 0d;

                            String[] arr = new String[itemDetailsPLList.size()];
                            for (int j = 0; j < itemDetailsPLList.size(); j++) {
                                arr[j] = itemDetailsPLList.get(j).Name;
                                if (j != 0)
                                    totamnt = totamnt + Double.valueOf(itemDetailsPLList.get(j).Amount);
                            }
                            isSaved = false;

                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putBoolean("IsSaved", false);
                            editor.putString("ListOfItemsAdded", ListOfItemsAddedStr);
                            editor.commit();


                            SOActivityAdapter productListActivityAdapter = new SOActivityAdapter(cntext, arr, itemDetailsPLList, ((SOActivity) cntext).tvTotalAmountValue);
                            ((SOActivity) cntext).lvProductlist.setAdapter(productListActivityAdapter);
                            ((SOActivity) cntext).tvTotalAmountValue.setText("Total Amount : " + String.format("%.2f", totamnt));
                            ((SOActivity) cntext).tvTotalAmountValue.setVisibility(View.VISIBLE);
                        }

                    }
                });

                ImageButton cancel = (ImageButton) qtydialog.findViewById(R.id.imgBtnCloseQtySelection);
                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                lp.copyFrom(qtydialog.getWindow().getAttributes());
                lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                lp.height = WindowManager.LayoutParams.MATCH_PARENT;
                lp.gravity = Gravity.CENTER;
                qtydialog.getWindow().setAttributes(lp);

                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        qtydialog.dismiss();
                    }
                });
                qtydialog.show();




            }
        });

        ItemDetails itemDetailsPLObj = listItemDetails.get(position);
        tvSlNo.setText(String.valueOf(position));
        tvItem.setText(itemDetailsPLObj.Name);
        tvMrp.setText(String.valueOf(itemDetailsPLObj.Mrp));
        tvMrp.setGravity(Gravity.LEFT);
        tvQty.setText(String.valueOf(itemDetailsPLObj.Qty));
        tvQty.setGravity(Gravity.RIGHT);
        tvTotal.setText(String.valueOf(itemDetailsPLObj.Amount));
        tvTotal.setGravity(Gravity.RIGHT);

        return rowView;
    }


    @Override
    public int getCount() {
        return listItemDetails.size();
    }

    public void showDeletePopUP(final int position) {

        AlertDialog.Builder b = new AlertDialog.Builder(cntext);
        b.setTitle("Confirm Delete");
        b.setMessage("Are you sure to delete " + listItemDetails.get(position).Name);  //item name should specify

        b.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                deleteItem(position);
                Toast.makeText(cntext, "Item Deleted successfully", Toast.LENGTH_LONG).show();
            }
        });

        b.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        b.show();
    }

    public void deleteItem(int position) {
        this.listItemDetails.remove(position);
        this.notifyDataSetChanged();

        Double totamnt = 0d;
        if (listItemDetails.size() > 0) {

            for (int j = 0; j < listItemDetails.size(); j++) {
                if (j != 0)
                    totamnt = totamnt + Double.valueOf(listItemDetails.get(j).Amount);
            }

            tvTotalAmount.setText("Total Amount : " + String.format("%.2f", totamnt));
            tvTotalAmount.setVisibility(View.VISIBLE);

        } else {
            tvTotalAmount.setVisibility(View.GONE);
            this.listItemDetails.remove(0);
            this.notifyDataSetChanged();
        }

        //Following added by Pavithra on 26-08-2020
        Gson gson = new Gson();
        String ListOfItemsAddedStr = gson.toJson(listItemDetails);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("ListOfItemsAdded", ListOfItemsAddedStr);
        editor.commit();
    }

}

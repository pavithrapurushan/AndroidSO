package in.co.tsmith.androidso;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.File;
import java.util.List;

public class PrescriptionAdapter  extends ArrayAdapter<String> {

    Context cntext;
    List<ItemDetails> listPrescriptins;
    String [] values;


    public PrescriptionAdapter(Context context,String[] v1, List<ItemDetails> listItemDetailsPL) {
        super(context,R.layout.activity_salesorder, v1);
        cntext = context;
        values = v1;
        listPrescriptins = listItemDetailsPL;
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) { //getview will call many times
        final ItemDetails item = listPrescriptins.get(position);
        convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_lvprescriptionlist, parent, false);

        TextView tvItemName = (TextView) convertView.findViewById(R.id.tvPrescrptnImageName);
//        TextView tvfileSize = (TextView) convertView.findViewById(R.id.tvFileSize);
        ImageButton imgBtnDelete = (ImageButton) convertView.findViewById(R.id.imgBtnDeletePrescrptn);
//        ImageView imgViewItem = (ImageView) convertView.findViewById(R.id.imgViewPrescrptn);
        TextView tvFilePath = (TextView) convertView.findViewById(R.id.tvFilePath);

        if(item.filepath != null && !item.filepath.equals("")) {
            File tempFile = new File(item.filepath);
            if(tempFile.exists()) { // added on 07/06/2017
                Bitmap bitmapImg = BitmapFactory.decodeFile(item.filepath);
                Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmapImg,100, 100, false);
//                imgViewItem.setMinimumWidth(100);
//                imgViewItem.setMinimumHeight(100);

//                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(100, 100);
//                imgViewItem.setLayoutParams(layoutParams);

//                imgViewItem.setImageBitmap(resizedBitmap);

                tvItemName.setText(item.filename);
                File file = new File(item.filepath);
                long length = file.length() / 1024; // Size in KB
                String filesize = String.valueOf(length);
//                tvfileSize.setText("File Size :" + filesize + " " + "KB");
                tvFilePath.setText(item.filepath);
            }else{
                Toast.makeText(cntext, "file not exist", Toast.LENGTH_SHORT).show();
            }
        }else{
//            imgViewItem.setVisibility(View.GONE);
        }
        imgBtnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeletePopUP(position);
            }
        });

        return convertView;
    }

    @Override
    public int getCount() {
        return listPrescriptins.size();
    }

    public void showDeletePopUP(final int position){

        AlertDialog.Builder b = new AlertDialog.Builder(cntext);
        b.setTitle("Confirm Delete");
        b.setMessage("Are you sure to delete "+listPrescriptins.get(position).filename);  //item name should specify

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
        this.listPrescriptins.remove(position);
        this.notifyDataSetChanged();

        Gson gson = new Gson();
        String listPrescriptinsJsonstr = gson.toJson(listPrescriptins);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(cntext);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("ListOfPrescrptnsAdded",listPrescriptinsJsonstr);
        editor.commit();
    }
}

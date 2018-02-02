package com.example.prabu.imagestorageandretrieval.view;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.example.prabu.imagestorageandretrieval.controller.DAOdb;
import com.example.prabu.imagestorageandretrieval.controller.DBhelper;
import com.example.prabu.imagestorageandretrieval.controller.ImageAdapter;
import com.example.prabu.imagestorageandretrieval.MainActivity;
import com.example.prabu.imagestorageandretrieval.model.MyImage;
import com.example.prabu.imagestorageandretrieval.R;
import com.google.gson.Gson;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

/**
 * Created by Admin on 29/01/2018.
 */
public class ViewAllCustomer extends ListActivity {
    private ArrayList<MyImage> images;
    private ImageAdapter imageAdapter;
    private ListView listView;
    private Uri mCapturedImageURI;
    private static final int RESULT_LOAD_IMAGE = 1;
    private static final int REQUEST_IMAGE_CAPTURE = 2;
    private static final int PERMISSION_REQUEST_CODE = 200;

    private DAOdb daOdb;
    private View view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_all_customer);
        images = new ArrayList();
        // Create the adapter to convert the array to views
        imageAdapter = new ImageAdapter(this, images);
        listView = (ListView) findViewById(android.R.id.list);
        final ImageView img = (ImageView) findViewById(R.id.imageView1);
        listView.setAdapter(imageAdapter);
        addItemClickListener(listView);
        initDB();
        final DAOdb cb = new DAOdb(this);


        img.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                view = v;
                PopupMenu popup = new PopupMenu(ViewAllCustomer.this, img);
                //Inflating the Popup using xml file
                popup.getMenuInflater().inflate(R.menu.menu_main, popup.getMenu());

                //registering popup with OnMenuItemClickListener


                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                    public boolean onMenuItemClick(MenuItem item) {
                        // Toast.makeText(ViewAllCustomer.this,"You Clicked : " + item.getTitle(),Toast.LENGTH_SHORT).show();

                        if (checkPermission()) {

                            final Cursor cursor = cb.getuser();
                            File sd = Environment.getExternalStorageDirectory();
                            String csvFile = "Customer_Details.xls";

                            File directory = new File(sd.getAbsolutePath());
                            //create directory if not exist
                            if (!directory.isDirectory()) {
                                directory.mkdirs();
                            }
                            try {

                                //file path
                                File file = new File(directory, csvFile);
                                WorkbookSettings wbSettings = new WorkbookSettings();
                                wbSettings.setLocale(new Locale("en", "EN"));
                                WritableWorkbook workbook;
                                workbook = Workbook.createWorkbook(file, wbSettings);
                                //Excel sheet name. 0 represents first sheet
                                WritableSheet sheet = workbook.createSheet("CustomerList", 0);
                                // column and row
                                sheet.addCell(new Label(0, 0, "cusId"));
                                sheet.addCell(new Label(1, 0, "Name"));
                                sheet.addCell(new Label(2, 0, "Mobile"));
                                sheet.addCell(new Label(3, 0, "Model"));
                                sheet.addCell(new Label(4, 0, "Complaint"));
                                sheet.addCell(new Label(5, 0, "Amount"));
                                sheet.addCell(new Label(6, 0, "Status"));
                                sheet.addCell(new Label(7, 0, "Date"));

                                if (cursor.moveToFirst()) {
                                    do {
                                        String cusId = cursor.getString(cursor.getColumnIndex(DBhelper.COLUMN_ID));
                                        String name = cursor.getString(cursor.getColumnIndex(DBhelper.COLUMN_NAME));
                                        String mobile = cursor.getString(cursor.getColumnIndex(DBhelper.COLUMN_MOBILE));
                                        String model = cursor.getString(cursor.getColumnIndex(DBhelper.COLUMN_MODEL));
                                        String complaint = cursor.getString(cursor.getColumnIndex(DBhelper.COLUMN_COMPLAINT));
                                        String amount = cursor.getString(cursor.getColumnIndex(DBhelper.COLUMN_AMOUNT));
                                        String status = cursor.getString(cursor.getColumnIndex(DBhelper.COLUMN_STATUS));
                                        String date = cursor.getString(cursor.getColumnIndex(DBhelper.COLUMN_DATE));

                                        int i = cursor.getPosition() + 1;
                                        sheet.addCell(new Label(0, i, cusId));
                                        sheet.addCell(new Label(1, i, name));
                                        sheet.addCell(new Label(2, i, mobile));
                                        sheet.addCell(new Label(3, i, model));
                                        sheet.addCell(new Label(4, i, complaint));
                                        sheet.addCell(new Label(5, i, amount));
                                        sheet.addCell(new Label(6, i, status));
                                        sheet.addCell(new Label(7, i, date));
                                    } while (cursor.moveToNext());
                                }

                                //closing cursor
                                cursor.close();
                                workbook.write();
                                workbook.close();
                                Toast.makeText(getApplicationContext(), "Data Exported in a Excel Sheet", Toast.LENGTH_LONG).show();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else if (!checkPermission()) {

                            requestPermission();

                        } else {
                            Toast t = Toast.makeText(ViewAllCustomer.this, "Permission already granted", Toast.LENGTH_SHORT);
                            t.show();
                            return true;
                        }
                        return true;
                    }
                });

                popup.show();//showing popup menu
            }
        });


    }

    private void initDB() {
        daOdb = new DAOdb(this);
        //        add images from database to images ArrayList
        for (MyImage mi : daOdb.getImages()) {
            images.add(mi);
        }
    }

    private void addItemClickListener(final ListView listView) {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                MyImage image = (MyImage) listView.getItemAtPosition(position);
                Intent intent =
                        new Intent(ViewAllCustomer.this, DisplayImage.class);
                intent.putExtra("IMAGE", (new Gson()).toJson(image));
                startActivity(intent);
                finish();
            }
        });
    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), READ_EXTERNAL_STORAGE);

        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {

        ActivityCompat.requestPermissions(this, new String[]{WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0) {

                    boolean locationAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean cameraAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                    if (locationAccepted && cameraAccepted) {
                        Toast t = Toast.makeText(ViewAllCustomer.this, "Permission Granted, Now you can write Excel data", Toast.LENGTH_SHORT);
                        t.show();
                    } else {
                        Toast t = Toast.makeText(ViewAllCustomer.this, "Permission Denied, You cannot write Excel data", Toast.LENGTH_SHORT);
                        t.show();
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (shouldShowRequestPermissionRationale(WRITE_EXTERNAL_STORAGE)) {
                                showMessageOKCancel("You need to allow access to both the permissions",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                    requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE},
                                                            PERMISSION_REQUEST_CODE);
                                                }
                                            }
                                        });
                                return;
                            }
                        }

                    }
                }


                break;
        }
    }


    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(ViewAllCustomer.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ViewAllCustomer.this, MainActivity.class);
        startActivity(intent);
        finish();

    }

}



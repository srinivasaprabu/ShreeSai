package com.example.prabu.imagestorageandretrieval.view;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.prabu.imagestorageandretrieval.controller.CustomOnItemSelectedListener;
import com.example.prabu.imagestorageandretrieval.controller.DAOdb;
import com.example.prabu.imagestorageandretrieval.controller.DatePickerFragment;
import com.example.prabu.imagestorageandretrieval.controller.ImageAdapter;
import com.example.prabu.imagestorageandretrieval.MainActivity;
import com.example.prabu.imagestorageandretrieval.model.MyImage;
import com.example.prabu.imagestorageandretrieval.R;
import com.example.prabu.imagestorageandretrieval.controller.Validation;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

/**
 * Created by Admin on 30/01/2018.
 */
import static android.Manifest.permission.CAMERA;

public class AddUpdateCustomers extends AppCompatActivity implements DatePickerFragment.DateDialogListener {
    private static final String EXTRA_CUS_ID = "com.example.prabu.cusId";
    private static final String EXTRA_ADD_UPDATE = "com.example.prabu.add_update";
    private static final String DIALOG_DATE = "DialogDate";
    private ImageView calendarImage;
    private ArrayList<MyImage> images;
    private Uri mCapturedImageURI;
    private static final int RESULT_LOAD_IMAGE = 1;
    private static final int REQUEST_IMAGE_CAPTURE = 2;
    private EditText name;
    private EditText mobileno;
    private EditText model;
    private EditText complaint;

    private EditText amount;
    private Spinner status;
    private EditText dateEditText;
    private EditText imagepath;
    private Button addImage;
    private Button addUpdateButton;

    private String mode;
    private long cusId;
    private DAOdb daOdb;
    private MyImage oldCustomer;
    private ImageAdapter imageAdapter;
    private static final int PERMISSION_REQUEST_CODE = 200;
    //private View view1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        String value = "";

        if (getIntent().hasExtra("imagepath")) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_add_update_customer);
            if (getIntent().getStringExtra(EXTRA_CUS_ID) != null) {
                //  Toast t1 = Toast.makeText(AddUpdateCustomers.this, getIntent().getStringExtra(EXTRA_CUS_ID), Toast.LENGTH_SHORT);
                // t1.show();
            }
            value = getIntent().getStringExtra("imagepath");
            mode = getIntent().getStringExtra("mode");
            //  mode = getIntent().getStringExtra(EXTRA_ADD_UPDATE);

            // Toast t = Toast.makeText(AddUpdateCustomers.this, "Flag " + mode, Toast.LENGTH_SHORT);
            //t.show();
            images = new ArrayList();
            oldCustomer = new MyImage();
            // Create the adapter to convert the array to views
            imageAdapter = new ImageAdapter(this, images);

            name = (EditText) findViewById(R.id.edit_text_name);
            mobileno = (EditText) findViewById(R.id.edit_text_mobileno);
            model = (EditText) findViewById(R.id.edit_text_model);
            complaint = (EditText) findViewById(R.id.edit_text_complaint);
            amount = (EditText) findViewById(R.id.edit_text_amount);
            status = (Spinner) findViewById(R.id.status);
            dateEditText = (EditText) findViewById(R.id.edit_text_date);
            calendarImage = (ImageView) findViewById(R.id.image_view_date);
            imagepath = (EditText) findViewById(R.id.edit_text_imagepath);
            addUpdateButton = (Button) findViewById(R.id.button_add_update_customer);
            addImage = (Button) findViewById(R.id.Addimg);
            imagepath.setText(value);
            name.setText(getIntent().getStringExtra("name"));
            mobileno.setText(getIntent().getStringExtra("mobile"));
            model.setText(getIntent().getStringExtra("model"));
            complaint.setText(getIntent().getStringExtra("complaint"));
            amount.setText(getIntent().getStringExtra("amount"));
            int spinnerPosition = getIndex(status, getIntent().getStringExtra("status"));
            status.setSelection(spinnerPosition);
            dateEditText.setText(getIntent().getStringExtra("date"));
            initDB();
            imagepath.setEnabled(false);

            mobileno.addTextChangedListener(new TextWatcher() {
                public void afterTextChanged(Editable s) {
                    Validation.isPhoneNumber(mobileno, false);
                }

                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }
            });


            calendarImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    FragmentManager manager = getSupportFragmentManager();
                    DatePickerFragment dialog = new DatePickerFragment();
                    dialog.show(manager, DIALOG_DATE);
                }
            });


            addUpdateButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mode.equals("Add")) {
                        if (!name.getText().toString().isEmpty() && !mobileno.getText().toString().isEmpty() && !model.getText().toString().isEmpty() && !complaint.getText().toString().isEmpty() && !amount.getText().toString().isEmpty() && !String.valueOf(status.getSelectedItem()).isEmpty() && !dateEditText.getText().toString().isEmpty() && !imagepath.getText().toString().isEmpty() && checkValidation()) {
                            MyImage image = new MyImage();
                            image.setName(name.getText().toString());
                            image.setMobile(mobileno.getText().toString());
                            image.setModel(model.getText().toString());
                            image.setComplaint(complaint.getText().toString());
                            image.setAmount(amount.getText().toString());
                            image.setStatus(String.valueOf(status.getSelectedItem()));
                            image.setDate(dateEditText.getText().toString());
                            image.setPath(imagepath.getText().toString());
                            images.add(image);
                            daOdb.addImage(image);
                            Toast t = Toast.makeText(AddUpdateCustomers.this, "Customer " + image.getName() + " has been added successfully !", Toast.LENGTH_SHORT);
                            t.show();
                            Intent i = new Intent(AddUpdateCustomers.this, MainActivity.class);
                            startActivity(i);
                            finish();
                        } else {
                            Toast t = Toast.makeText(AddUpdateCustomers.this, "Enter all fields", Toast.LENGTH_SHORT);
                            t.show();
                        }
                    } else {
                        if (!name.getText().toString().isEmpty() && !mobileno.getText().toString().isEmpty() && !model.getText().toString().isEmpty() && !complaint.getText().toString().isEmpty() && !amount.getText().toString().isEmpty() && !String.valueOf(status.getSelectedItem()).isEmpty() && !dateEditText.getText().toString().isEmpty() && checkValidation()) {
                            MyImage oldCustomer = new MyImage();
                            oldCustomer.setCusId(cusId);
                            oldCustomer.setName(name.getText().toString());
                            oldCustomer.setMobile(mobileno.getText().toString());
                            oldCustomer.setModel(model.getText().toString());
                            oldCustomer.setComplaint(complaint.getText().toString());
                            oldCustomer.setAmount(amount.getText().toString());
                            oldCustomer.setStatus(String.valueOf(status.getSelectedItem()));
                            oldCustomer.setDate(dateEditText.getText().toString());
                            oldCustomer.setPath(imagepath.getText().toString());
                            images.add(oldCustomer);

                            daOdb.updateCustomer(oldCustomer);

                            Toast t = Toast.makeText(AddUpdateCustomers.this, "Customer " + oldCustomer.getName() + " has been updated successfully !", Toast.LENGTH_SHORT);
                            t.show();
                            Intent i = new Intent(AddUpdateCustomers.this, MainActivity.class);
                            startActivity(i);
                            finish();
                        } else {
                            Toast t = Toast.makeText(AddUpdateCustomers.this, "Enter all fields", Toast.LENGTH_SHORT);
                            t.show();
                        }
                    }
                }


            });


        } else {

            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_add_update_customer);
            mode = getIntent().getStringExtra(EXTRA_ADD_UPDATE);
            if (getIntent().getStringExtra(EXTRA_CUS_ID) != null) {
                if (mode.equals("Update")) {
                    addUpdateButton = (Button) findViewById(R.id.button_add_update_customer);
                    addUpdateButton.setText("Update Customer");
                    cusId = Long.parseLong(getIntent().getStringExtra(EXTRA_CUS_ID));
                    imagepath = (EditText) findViewById(R.id.edit_text_imagepath);
                    imagepath.setEnabled(false);
                    addImage = (Button) findViewById(R.id.Addimg);
                    addImage.setEnabled(false);
                    initializeCustomer(cusId);

                }


            }
            images = new ArrayList();
            // Create the adapter to convert the array to views
            imageAdapter = new ImageAdapter(this, images);
            oldCustomer = new MyImage();
            name = (EditText) findViewById(R.id.edit_text_name);
            mobileno = (EditText) findViewById(R.id.edit_text_mobileno);
            model = (EditText) findViewById(R.id.edit_text_model);
            complaint = (EditText) findViewById(R.id.edit_text_complaint);
            amount = (EditText) findViewById(R.id.edit_text_amount);
            status = (Spinner) findViewById(R.id.status);
            dateEditText = (EditText) findViewById(R.id.edit_text_date);
            calendarImage = (ImageView) findViewById(R.id.image_view_date);
            imagepath = (EditText) findViewById(R.id.edit_text_imagepath);
            addUpdateButton = (Button) findViewById(R.id.button_add_update_customer);
            initDB();
            imagepath.setEnabled(false);
            mobileno.addTextChangedListener(new TextWatcher() {
                public void afterTextChanged(Editable s) {
                    Validation.isPhoneNumber(mobileno, false);
                }

                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }
            });

            calendarImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    FragmentManager manager = getSupportFragmentManager();
                    DatePickerFragment dialog = new DatePickerFragment();
                    dialog.show(manager, DIALOG_DATE);
                }
            });


            addUpdateButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mode.equals("Add")) {
                        if (!name.getText().toString().isEmpty() && !mobileno.getText().toString().isEmpty() && !model.getText().toString().isEmpty() && !complaint.getText().toString().isEmpty() && !amount.getText().toString().isEmpty() && !String.valueOf(status.getSelectedItem()).isEmpty() && !dateEditText.getText().toString().isEmpty() && !imagepath.getText().toString().isEmpty() && checkValidation()) {
                            MyImage image = new MyImage();
                            image.setName(name.getText().toString());
                            image.setMobile(mobileno.getText().toString());
                            image.setModel(model.getText().toString());
                            image.setComplaint(complaint.getText().toString());
                            image.setAmount(amount.getText().toString());
                            image.setStatus(String.valueOf(status.getSelectedItem()));
                            image.setDate(dateEditText.getText().toString());
                            image.setPath(imagepath.getText().toString());
                            images.add(image);
                            daOdb.addImage(image);
                            Toast t = Toast.makeText(AddUpdateCustomers.this, "Customer " + image.getName() + " has been added successfully !", Toast.LENGTH_SHORT);
                            t.show();
                            Intent i = new Intent(AddUpdateCustomers.this, MainActivity.class);
                            startActivity(i);
                            finish();
                        } else {
                            Toast t = Toast.makeText(AddUpdateCustomers.this, "Enter all fields", Toast.LENGTH_SHORT);
                            t.show();
                        }
                    } else {
                        //initializeCustomer(getIntent().getLongExtra("EXTRA_CUS_ID",0));
                        if (!name.getText().toString().isEmpty() && !mobileno.getText().toString().isEmpty() && !model.getText().toString().isEmpty() && !complaint.getText().toString().isEmpty() && !amount.getText().toString().isEmpty() && !String.valueOf(status.getSelectedItem()).isEmpty() && !dateEditText.getText().toString().isEmpty() && !imagepath.getText().toString().isEmpty() && checkValidation()) {
                            MyImage oldCustomer = new MyImage();
                            oldCustomer.setCusId(cusId);
                            oldCustomer.setName(name.getText().toString());
                            oldCustomer.setMobile(mobileno.getText().toString());
                            oldCustomer.setModel(model.getText().toString());
                            oldCustomer.setComplaint(complaint.getText().toString());
                            oldCustomer.setAmount(amount.getText().toString());
                            oldCustomer.setStatus(String.valueOf(status.getSelectedItem()));
                            oldCustomer.setDate(dateEditText.getText().toString());
                            oldCustomer.setPath(imagepath.getText().toString());
                            images.add(oldCustomer);

                            daOdb.updateCustomer(oldCustomer);

                            Toast t = Toast.makeText(AddUpdateCustomers.this, "Customer " + oldCustomer.getName() + " has been updated successfully !", Toast.LENGTH_SHORT);
                            t.show();
                            Intent i = new Intent(AddUpdateCustomers.this, MainActivity.class);
                            startActivity(i);
                            finish();
                        } else {
                            Toast t = Toast.makeText(AddUpdateCustomers.this, "Enter all fields", Toast.LENGTH_SHORT);
                            t.show();
                        }
                    }
                }

            });

        }

    }

    /**
     * item clicked listener used to implement the react action when an item is
     * clicked.
     *
     * @param listView
     */
    private void addItemClickListener(final ListView listView) {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                MyImage image = (MyImage) listView.getItemAtPosition(position);
                Intent intent =
                        new Intent(getBaseContext(), DisplayImage.class);
                intent.putExtra("IMAGE", (new Gson()).toJson(image));


                startActivity(intent);
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

    public void addListenerOnSpinnerItemSelection() {
        status = (Spinner) findViewById(R.id.status);
        status.setOnItemSelectedListener(new CustomOnItemSelectedListener());
    }


    @Override
    public void onFinishDialog(Date date) {
        dateEditText.setText(formatDate(date));
    }

    public String formatDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String hireDate = sdf.format(date);
        return hireDate;
    }

    private boolean checkValidation() {
        boolean ret = true;

        if (!Validation.isPhoneNumber(mobileno, true)) ret = false;

        return ret;
    }

    private int getIndex(Spinner spinner, String myString) {

        int index = 0;

        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).equals(myString)) {
                index = i;
            }
        }
        return index;
    }

    public void btnAddOnClick(View view) {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.custom_dialog_box);
        // view1 = view;
        if (checkPermission()) {

            dialog.setTitle("Choose Image");
            Button btnExit = (Button) dialog.findViewById(R.id.btnExit);
            //  name = (EditText) dialog.findViewById(R.id.edit_text_name);
            btnExit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            dialog.findViewById(R.id.btnChoosePath)
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            activeGallery();
                        }
                    });
            dialog.findViewById(R.id.btnTakePhoto)
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            activeTakePhoto();
                        }
                    });

            // show dialog on screen
            dialog.show();
        } else if (!checkPermission()) {

            requestPermission();

        } else {
            Toast t = Toast.makeText(AddUpdateCustomers.this, "Permission already granted.", Toast.LENGTH_SHORT);
            t.show();
        }


    }

    /**
     * take a photo
     */

    private void activeTakePhoto() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            String fileName = "temp.jpg";
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.TITLE, fileName);
            mCapturedImageURI = getContentResolver()
                    .insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                            values);
            takePictureIntent
                    .putExtra(MediaStore.EXTRA_OUTPUT, mCapturedImageURI);
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    /**
     * to gallery
     */
    private void activeGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(intent, RESULT_LOAD_IMAGE);
    }


    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //String name = data.getStringExtra("name");
        switch (requestCode) {
            case RESULT_LOAD_IMAGE:
                if (requestCode == RESULT_LOAD_IMAGE &&
                        resultCode == RESULT_OK && null != data) {
                    Uri selectedImage = data.getData();

                    String[] filePathColumn = {MediaStore.Images.Media.DATA};
                    Cursor cursor = getContentResolver()
                            .query(selectedImage, filePathColumn, null, null,
                                    null);
                    cursor.moveToFirst();
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String picturePath = cursor.getString(columnIndex);
                    // cursor.close();
                    mode = getIntent().getStringExtra(EXTRA_ADD_UPDATE);
                    // Toast t = Toast.makeText(AddUpdateCustomers.this, "Flag " + mode, Toast.LENGTH_SHORT);
                    //t.show();
                    Intent intent = new Intent(this, AddUpdateCustomers.class);
                    intent.putExtra("imagepath", picturePath);
                    intent.putExtra("name", name.getText().toString());
                    intent.putExtra("mobile", mobileno.getText().toString());
                    intent.putExtra("model", model.getText().toString());
                    intent.putExtra("complaint", complaint.getText().toString());
                    intent.putExtra("amount", amount.getText().toString());
                    intent.putExtra("status", String.valueOf(status.getSelectedItem()));
                    intent.putExtra("date", dateEditText.getText().toString());
                    intent.putExtra("mode", mode);
                    startActivity(intent);
                    finish();
                }
            case REQUEST_IMAGE_CAPTURE:
                if (requestCode == REQUEST_IMAGE_CAPTURE &&
                        resultCode == RESULT_OK) {
                    String[] projection = {MediaStore.Images.Media.DATA};
                    Cursor cursor =
                            managedQuery(mCapturedImageURI, projection, null,
                                    null, null);
                    int column_index_data = cursor.getColumnIndexOrThrow(
                            MediaStore.Images.Media.DATA);
                    cursor.moveToFirst();
                    String picturePath = cursor.getString(column_index_data);
                    mode = getIntent().getStringExtra(EXTRA_ADD_UPDATE);
                    // Toast t = Toast.makeText(AddUpdateCustomers.this, "Flag " + mode, Toast.LENGTH_SHORT);
                    //t.show();
                    Intent intent = new Intent(this, AddUpdateCustomers.class);
                    intent.putExtra("imagepath", picturePath);
                    intent.putExtra("name", name.getText().toString());
                    intent.putExtra("mobile", mobileno.getText().toString());
                    intent.putExtra("model", model.getText().toString());
                    intent.putExtra("complaint", complaint.getText().toString());
                    intent.putExtra("amount", amount.getText().toString());
                    intent.putExtra("status", String.valueOf(status.getSelectedItem()));
                    intent.putExtra("date", dateEditText.getText().toString());
                    intent.putExtra("mode", mode);
                    startActivity(intent);
                    finish();

                }
        }
    }

    private void initializeCustomer(long cusId) {
        DAOdb db = new DAOdb(this);
        oldCustomer = db.getCustomer(cusId);
        name = (EditText) findViewById(R.id.edit_text_name);
        mobileno = (EditText) findViewById(R.id.edit_text_mobileno);
        model = (EditText) findViewById(R.id.edit_text_model);
        complaint = (EditText) findViewById(R.id.edit_text_complaint);
        amount = (EditText) findViewById(R.id.edit_text_amount);
        status = (Spinner) findViewById(R.id.status);
        dateEditText = (EditText) findViewById(R.id.edit_text_date);
        calendarImage = (ImageView) findViewById(R.id.image_view_date);
        imagepath = (EditText) findViewById(R.id.edit_text_imagepath);
        name.setText(oldCustomer.getName());
        mobileno.setText(oldCustomer.getMobile());
        model.setText(oldCustomer.getModel());
        complaint.setText(oldCustomer.getComplaint());
        amount.setText(oldCustomer.getAmount());
        int spinnerPosition = getIndex(status, oldCustomer.getStatus());
        status.setSelection(spinnerPosition);
        dateEditText.setText(oldCustomer.getDate());
        imagepath.setText(oldCustomer.getPath());
    }


    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), CAMERA);

        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {

        ActivityCompat.requestPermissions(this, new String[]{WRITE_EXTERNAL_STORAGE, CAMERA}, PERMISSION_REQUEST_CODE);

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0) {

                    boolean locationAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean cameraAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                    if (locationAccepted && cameraAccepted) {
                        Toast t = Toast.makeText(AddUpdateCustomers.this, "Permission Granted, Now you can access Camera", Toast.LENGTH_SHORT);
                        t.show();
                    }
                    // Snackbar.make(view1, "Permission Granted, Now you can write Excel data", Snackbar.LENGTH_LONG).show();
                    else {
                        Toast t = Toast.makeText(AddUpdateCustomers.this, "Permission Denied, You cannot access Camera", Toast.LENGTH_SHORT);
                        t.show();
                        // Snackbar.make(view1, "Permission Denied, You cannot write Excel data", Snackbar.LENGTH_LONG).show();

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
        new AlertDialog.Builder(AddUpdateCustomers.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(AddUpdateCustomers.this, MainActivity.class);
        startActivity(intent);
        finish();

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // Save the user's current game state
        if (mCapturedImageURI != null) {
            outState.putString("mCapturedImageURI",
                    mCapturedImageURI.toString());
        }
        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        // Always call the superclass so it can restore the view hierarchy
        super.onRestoreInstanceState(savedInstanceState);

        // Restore state members from saved instance
        if (savedInstanceState.containsKey("mCapturedImageURI")) {
            mCapturedImageURI = Uri.parse(
                    savedInstanceState.getString("mCapturedImageURI"));
        }
    }

}

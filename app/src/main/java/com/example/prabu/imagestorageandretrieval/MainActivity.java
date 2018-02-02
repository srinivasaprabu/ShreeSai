package com.example.prabu.imagestorageandretrieval;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.example.prabu.imagestorageandretrieval.controller.DAOdb;
import com.example.prabu.imagestorageandretrieval.controller.ImageAdapter;
import com.example.prabu.imagestorageandretrieval.model.MyImage;
import com.example.prabu.imagestorageandretrieval.view.AddUpdateCustomers;
import com.example.prabu.imagestorageandretrieval.view.ViewAllCustomer;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private ArrayList<MyImage> images;
    private ImageAdapter imageAdapter;
    private ListView listView;
    private Uri mCapturedImageURI;
    private static final int RESULT_LOAD_IMAGE = 1;
    private static final int REQUEST_IMAGE_CAPTURE = 2;
    private Button addCustomerButton;
    private Button viewAllCustomerButton;
    private DAOdb daOdb;
    public EditText name;

    private static final String EXTRA_CUS_ID = "com.example.prabu.cusId";
    private static final String EXTRA_ADD_UPDATE = "com.example.prabu.add_update";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Construct the data source
        images = new ArrayList();
        // Create the adapter to convert the array to views
        imageAdapter = new ImageAdapter(this, images);
        addCustomerButton = (Button) findViewById(R.id.btnAdd);
        viewAllCustomerButton = (Button) findViewById(R.id.button_view_customers);
        // Attach the adapter to a ListView
        //  listView = (ListView) findViewById(R.id.main_list_view);
        // listView.setAdapter(imageAdapter);
        //addItemClickListener(listView);
//        initDB();
        addCustomerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, AddUpdateCustomers.class);
                i.putExtra(EXTRA_ADD_UPDATE, "Add");
                startActivity(i);
                finish();
            }
        });
        viewAllCustomerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, ViewAllCustomer.class);
                startActivity(i);
                finish();
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
    public void onBackPressed() {
        finish();
    }



}

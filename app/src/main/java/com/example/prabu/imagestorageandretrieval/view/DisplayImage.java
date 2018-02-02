package com.example.prabu.imagestorageandretrieval.view;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Matrix;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.PointF;
import android.util.Log;
import android.view.View.OnTouchListener;

import com.example.prabu.imagestorageandretrieval.controller.DAOdb;
import com.example.prabu.imagestorageandretrieval.controller.ImageResizer;
import com.example.prabu.imagestorageandretrieval.model.MyImage;
import com.example.prabu.imagestorageandretrieval.R;

/**
 * Created by Admin on 29/01/2018.
 */

public class DisplayImage extends Activity implements OnTouchListener {
    private MyImage image;
    private ImageView imageView;
    private TextView description;
    private String jstring;
    private long ID;

    private static final String EXTRA_CUS_ID = "com.example.prabu.cusId";
    private static final String EXTRA_ADD_UPDATE = "com.example.prabu.add_update";
    private static final String TAG = "Touch";

    // These matrices will be used to move and zoom image
    Matrix matrix = new Matrix();
    Matrix savedMatrix = new Matrix();

    // We can be in one of these 3 states
    static final int NONE = 0;
    static final int DRAG = 1;
    static final int ZOOM = 2;
    int mode = NONE;

    // Remember some things for zooming


    PointF start = new PointF();
    PointF mid = new PointF();
    float oldDist = 1f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_image);
        imageView = (ImageView) findViewById(R.id.display_image_view);
        description = (TextView) findViewById(R.id.text_view_description);
        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            jstring = extras.getString("IMAGE");
        }
        image = getMyImage(jstring);
        description.setText("Name= " + image.getName() + "\n" + "Model= " + image.getModel());

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;
        imageView.setImageBitmap(ImageResizer
                .decodeSampledBitmapFromFile(image.getPath(), width, height));
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER); // make the image fit to the center.
        imageView.setOnTouchListener(this);
    }


    public boolean onTouch(View v, MotionEvent event) {
        ImageView view = (ImageView) v;
        // make the image scalable as a matrix
        view.setScaleType(ImageView.ScaleType.MATRIX);
        float scale;

        // Handle touch events here...
        switch (event.getAction() & MotionEvent.ACTION_MASK) {

            case MotionEvent.ACTION_DOWN: //first finger down only
                savedMatrix.set(matrix);
                start.set(event.getX(), event.getY());
                Log.d(TAG, "mode=DRAG");
                mode = DRAG;
                break;
            case MotionEvent.ACTION_UP: //first finger lifted
            case MotionEvent.ACTION_POINTER_UP: //second finger lifted
                mode = NONE;
                Log.d(TAG, "mode=NONE");
                break;
            case MotionEvent.ACTION_POINTER_DOWN: //second finger down
                oldDist = spacing(event); // calculates the distance between two points where user touched.
                Log.d(TAG, "oldDist=" + oldDist);
                // minimal distance between both the fingers
                if (oldDist > 5f) {
                    savedMatrix.set(matrix);
                    midPoint(mid, event); // sets the mid-point of the straight line between two points where user touched.
                    mode = ZOOM;
                    Log.d(TAG, "mode=ZOOM");
                }
                break;

            case MotionEvent.ACTION_MOVE:
                if (mode == DRAG) { //movement of first finger
                    matrix.set(savedMatrix);
                    if (view.getLeft() >= -392) {
                        matrix.postTranslate(event.getX() - start.x, event.getY() - start.y);
                    }
                } else if (mode == ZOOM) { //pinch zooming
                    float newDist = spacing(event);
                    Log.d(TAG, "newDist=" + newDist);
                    if (newDist > 5f) {
                        matrix.set(savedMatrix);
                        scale = newDist / oldDist; //thinking I need to play around with this value to limit it**
                        matrix.postScale(scale, scale, mid.x, mid.y);
                    }
                }
                break;
        }

        // Perform the transformation
        view.setImageMatrix(matrix);

        return true; // indicate event was handled
    }

    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        float res = (float) Math.sqrt(x * x + y * y);
        return res;
    }

    private void midPoint(PointF point, MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }


    private MyImage getMyImage(String image) {
        try {
            JSONObject job = new JSONObject(image);
            return (new MyImage(Long.parseLong(job.getString("cusId")),
                    job.getString("name"),
                    job.getString("mobile"),
                    job.getString("model"),
                    job.getString("complaint"),
                    job.getString("amount"),
                    job.getString("status"),
                    job.getString("date"),
                    job.getString("path")
            ));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(DisplayImage.this, ViewAllCustomer.class);
        startActivity(i);
        finish();
    }


    public void btnUpdateOnClick(View view) {
        String itemValue = image.toString();
        // Toast t1 = Toast.makeText(DisplayImage.this, itemValue.substring(6, itemValue.indexOf("Name")).toString().trim(), Toast.LENGTH_SHORT);
        //t1.show();
        Intent i = new Intent(DisplayImage.this, AddUpdateCustomers.class);
        i.putExtra(EXTRA_ADD_UPDATE, "Update");
        i.putExtra(EXTRA_CUS_ID, itemValue.substring(6, itemValue.indexOf("Name")).toString().trim());
        startActivity(i);
        finish();

    }

    /**
     * delete the current item;
     *
     * @param v
     */
    public void btnDeleteOnClick(View v) {
        String itemValue = image.toString();
        Long.parseLong(itemValue.substring(6, itemValue.indexOf("Name")).toString().trim());

        DAOdb db = new DAOdb(this);
        db.removeCustomer(db.getCustomer(Long.parseLong(itemValue.substring(6, itemValue.indexOf("Name")).toString().trim())));
        Toast t = Toast.makeText(DisplayImage.this, "Customer removed successfully!", Toast.LENGTH_SHORT);
        t.show();
        Intent i = new Intent(DisplayImage.this, ViewAllCustomer.class);
        startActivity(i);
        finish();
        //DAOdb db = new DAOdb(this);
        //db.deleteImage(image);
        //db.close();
        //startActivity(new Intent(this, MainActivity.class));
        //finish();
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // Save the user's current game state
        if (jstring != null) {
            outState.putString("jstring", jstring);
        }
        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        // Always call the superclass so it can restore the view hierarchy
        super.onRestoreInstanceState(savedInstanceState);

        // Restore state members from saved instance
        if (savedInstanceState.containsKey("jstring")) {
            jstring = savedInstanceState.getString("jstring");
        }
    }


}

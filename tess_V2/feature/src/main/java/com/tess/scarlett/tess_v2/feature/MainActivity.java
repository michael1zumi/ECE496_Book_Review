package com.tess.scarlett.tess_v2.feature;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
//import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    public static final String TESS_DATA = "/tessdata";
    private TextView textView;
    //private TessBaseAPI tessBaseAPI;
    private Uri outputFileDir;
    private static final String DATA_PATH = Environment.getExternalStorageDirectory().toString()+"/Tess_V2";


    private TextView mTextMessage;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            int id = item.getItemId();
            if (id == R.id.navigation_home) {
                mTextMessage.setText(R.string.title_home);
                return true;
            } else if (id == R.id.navigation_dashboard) {
                mTextMessage.setText(R.string.title_dashboard);
                return true;
            } else if (id == R.id.navigation_notifications) {
                mTextMessage.setText(R.string.title_notifications);
                return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        final Button button = findViewById(R.id.button_cam);
        button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View c){
                startCameraActivity();
            }
        });
    }


    private void startCameraActivity(){

        try{

            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
            String imagePath = DATA_PATH + "/imgs";
            File dir = new File(imagePath);
            isWriteStoragePermissionGranted();
            if (isWriteStoragePermissionGranted() == true) {
                if(!dir.exists()){ System.out.println("HERE1\n");
                    if (!dir.mkdirs()) {
                        Log.e(TAG, "ERROR: Creation of directory " + imagePath + " failed, check does Android Manifest have permission to write to external storage.");
                    }
                } else {
                    Log.i(TAG, "Created directory " + imagePath);
                }
            }
            String imageFilePath = imagePath + "/ocr_v2.jpg";
            outputFileDir = Uri.fromFile(new File(imageFilePath));
            System.out.println("+++++++++++++++data path is "+DATA_PATH+"\n\n\n\n");

            final Intent pictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,outputFileDir);

            if(pictureIntent.resolveActivity(getPackageManager() ) != null){
                startActivityForResult(pictureIntent,100);
            }
        } catch (Exception e){
            Log.e(TAG, e.getMessage());
        }
    }


    public  boolean isWriteStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG,"Permission is granted2");
                return true;
            } else {

                Log.v(TAG,"Permission is revoked2");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG,"Permission is granted2");
            return true;
        }
    }










}

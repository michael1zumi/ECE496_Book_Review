package com.tess.scarlett.tess_v5;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity{

    private static final String TAG = MainActivity.class.getSimpleName();
    public static final String TESS_DATA = "/tessdata";
    private TextView textView;
    private TessBaseAPI tessBaseAPI;
    private Uri outputFileDir;
    private static final String DATA_PATH = Environment.getExternalStorageDirectory().toString()+"/tess_v5";

    private TextView mTextMessage;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment selectedFragment = null;
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    //mTextMessage.setText(R.string.title_home);
                    selectedFragment = HomeFragment.newInstance("","");
                    break;
                case R.id.navigation_camera:
                    startCameraActivity();
                    return true;
                case R.id.navigation_notifications:
                    //mTextMessage.setText(R.string.title_notifications);
                    selectedFragment = NotificationFragment.newInstance("","");
                    break;

            }
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.frame_layout, selectedFragment);
            transaction.commit();
            return true;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextMessage = (TextView) findViewById(R.id.message);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }


    private void startCameraActivity(){

        try{
            //camera.setDisplayOrientation(90);
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
            String imagePath = DATA_PATH + "/imgs";
            File dir = new File(imagePath);
            if (isWriteStoragePermissionGranted() == true) {
                if(!dir.exists()){ System.out.println("HERE1\n");
                    if (!dir.mkdirs()) {
                        Log.e(TAG, "ERROR: Creation of directory " + imagePath + " failed, check does Android Manifest have permission to write to external storage.");
                    }
                } else {
                    Log.i(TAG, "Created directory " + imagePath);
                }
            }
            String imageFilePath = imagePath + "/ocr_v5.jpg";
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


    public boolean isWriteStoragePermissionGranted() {
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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if (requestCode == 100 && resultCode == Activity.RESULT_OK){
            prepareTessData();
            System.out.println("outputFileDir is "+outputFileDir+"\n");
            startOCR(outputFileDir);
            System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!\n\n\n\n");
        }else{
            Toast.makeText(getApplicationContext(),"Image problem", Toast.LENGTH_SHORT).show();
        }

    }


    private void prepareTessData(){
        try{System.out.print("lallalala0\n");
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
            File dir = new File(DATA_PATH + TESS_DATA);
            if (!dir.exists()){
                System.out.print("lallalala0.2\n");
                //if (isWriteStoragePermissionGranted() == true) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
                System.out.print("lallalala0.1\n");
                dir.mkdir();

                //}
            }
            String fileList[] = getAssets().list("");
            System.out.print("fileList is"+fileList[0]+" \n");
            for (String fileName : fileList){
                String pathToDataFile = DATA_PATH + TESS_DATA + "/" + fileName;
                System.out.print("file name is " + fileName + "\n");
                if (!(new File(pathToDataFile)).exists()){
                    System.out.print("lallalala1.1\n");
                    InputStream in = getAssets().open(fileName);
                    System.out.print("lallalala1.2\n");
                    OutputStream out = new FileOutputStream(pathToDataFile);
                    System.out.print("lallalala1.3\n");
                    byte [] buff = new byte[1024];
                    int len;
                    System.out.print("lallalala2\n");
                    while((len = in.read(buff)) > 0){
                        out.write(buff,0,len);
                        System.out.print("lallalala3\n");
                    }
                    in.close();
                    out.close();
                }
            }
        } catch (Exception e){
            Log.e(TAG, e.getMessage());
            System.out.print("Exception Appear!!!!!!!!!!!\n");
        }
    }


    private void startOCR(Uri imageUri){
        try{
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 7;
            Bitmap bitmap = BitmapFactory.decodeFile(imageUri.getPath(),options);
            String result = this.getText(bitmap);
            System.out.println("return string is "+result +"\n");
            mTextMessage.setText(result);
        } catch (Exception e){
            Log.e(TAG, e.getMessage());
        }
    }

    private String getText(Bitmap bitmap){
        try{
            tessBaseAPI = new TessBaseAPI();
        } catch (Exception e){
            Log.e(TAG, e.getMessage());
        }
        tessBaseAPI.init(DATA_PATH,"eng");
        tessBaseAPI.setImage(bitmap);
        String retStr = "No result";
        try{
            retStr = tessBaseAPI.getUTF8Text();
        } catch (Exception e){
            Log.e(TAG, e.getMessage());
        }
        tessBaseAPI.end();
        return retStr;
    }







}

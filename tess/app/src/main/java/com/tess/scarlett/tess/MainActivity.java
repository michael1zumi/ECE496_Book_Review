package com.tess.scarlett.tess;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    public static final String TESS_DATA = "/tessdata";
    private TextView textView;
    private TessBaseAPI tessBaseAPI;
    private Uri outputFileDir;
    private static final String DATA_PATH = Environment.getExternalStorageDirectory().toString()+"/Tess";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        textView = (TextView) findViewById(R.id.message);

        final Button button = findViewById(R.id.button_cam);
        button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View c){
                startCameraActivity();
            }
        });


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

    private void startCameraActivity(){

        try{

            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
            String imagePath = DATA_PATH + "/imgs";
            File dir = new File(imagePath);
            if (isWriteStoragePermissionGranted() == true) {
                if(!dir.exists()){
                    if (!dir.mkdirs()) {
                        Log.e(TAG, "ERROR: Creation of directory " + imagePath + " failed, check does Android Manifest have permission to write to external storage.");
                    }
                } else {
                    Log.i(TAG, "Created directory " + imagePath);
                }
            }
            String imageFilePath = imagePath + "/ocr.jpg";
            outputFileDir = Uri.fromFile(new File(imageFilePath));


            final Intent pictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,outputFileDir);

            if(pictureIntent.resolveActivity(getPackageManager() ) != null){
                startActivityForResult(pictureIntent,100);
            }
        } catch (Exception e){
            Log.e(TAG, e.getMessage());
        }
    }
    /*
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if (requestCode == 100 && resultCode == Activity.RESULT_OK){
            prepareTessData();
            startOCR(outputFileDir);
        }else{
            Toast.makeText(getApplicationContext(),"Image problem", Toast.LENGTH_SHORT).show();
        }

    }


    private void prepareTessData(){
        try{System.out.print("lallalala0\n");
            File dir = new File(DATA_PATH + TESS_DATA);
            if (!dir.exists()){
                System.out.print("lallalala0.2\n");
                if (isWriteStoragePermissionGranted() == true) {
                    System.out.print("lallalala0.1\n");
                    dir.mkdir();

                }
            }
            String fileList[] = getAssets().list("");System.out.print("lallalala1\n");
            for (String fileName : fileList){
                String pathToDataFile = DATA_PATH + TESS_DATA + "/" + fileName;System.out.print("file name is " + fileName + "\n");
                if (!(new File(pathToDataFile)).exists()){System.out.print("lallalala1.1\n");
                    InputStream in = getAssets().open(fileName);     System.out.print("lallalala1.2\n");
                    OutputStream out = new FileOutputStream(pathToDataFile);System.out.print("lallalala1.3\n");
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
            System.out.print("lallalala4\n");
        }
    }

    private void startOCR(Uri imageUri){
        try{
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 7;
            Bitmap bitmap = BitmapFactory.decodeFile(imageUri.getPath(),options);
            String result = this.getText(bitmap);
            textView.setText(result);
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


    */

    /*

    String mCurrentPhotoPath;

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyMMddHHmmssZ").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }


    // enable the camera feature
    // convert the camera data to a bitmap
    static final int REQUEST_TAKE_PHOTO = 1;

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);

            }
        }
    }



    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }
    */

}

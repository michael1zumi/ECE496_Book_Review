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
import android.widget.RatingBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.CardView;
import android.widget.RelativeLayout;
import android.widget.EditText;
import android.widget.Button;

import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import java.io.IOException;
import android.os.AsyncTask;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class MainActivity extends AppCompatActivity{

    private static final String TAG = MainActivity.class.getSimpleName();
    public static final String TESS_DATA = "/tessdata";
    private TextView textView;
    private TessBaseAPI tessBaseAPI;
    private Uri outputFileDir;
    private static final String DATA_PATH = Environment.getExternalStorageDirectory().toString()+"/tess_v5";

    private TextView mTextMessage;
    private SearchView searchview;
    private CardView cardview;
    private RelativeLayout relativeLayout;
    private EditText editText;
    private Button button;

    private String Brand = "";
    private String Model = "";
    private String query = "";

    // string array
    private String[] price = new String[2];
    private String[] rate = new String[2];
    private String[] review = new String[4];

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment selectedFragment = null;
            switch (item.getItemId()) {
                case R.id.navigation_search:
                    //mTextMessage.setText(R.string.title_home);
                    selectedFragment = SearchFragment.newInstance("","");
                    hideUpButton();
                    break;
                case R.id.navigation_camera:
                    selectedFragment = SearchFragment.newInstance("","");
                    hideUpButton();
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.frame_layout, selectedFragment);
                    transaction.commit();
                    startCameraActivity();
                    return true;
                case R.id.access_gallery:
                    final SearchFragment fragment = new SearchFragment();
                    selectedFragment = SearchFragment.newInstance("","");
                    hideUpButton();
                    transaction = getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.frame_layout, selectedFragment);
                    transaction.commit();
                    //start from here
                    pickImage();
                    Toast.makeText(getApplicationContext(),"Accessing Gallery!", Toast.LENGTH_LONG).show();



                    return true;
                case R.id.navigation_profile:
                    //mTextMessage.setText(R.string.title_notifications);
                    selectedFragment = ProfileFragment.newInstance("","");
                    hideUpButton();
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

        //mTextMessage = (TextView) findViewById(R.id.message);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        //render Search fragment when first time visit
        Fragment selectedFragment = SearchFragment.newInstance("","");
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_layout, selectedFragment);
        transaction.commit();
    }


    public void showUpButton() { getSupportActionBar().setDisplayHomeAsUpEnabled(true); }
    public void hideUpButton() { getSupportActionBar().setDisplayHomeAsUpEnabled(false); }

    public boolean onOptionsItemSelected(MenuItem item)  {

        int itemId = item.getItemId();
        switch (itemId) {
            case android.R.id.home: //back to profile page
                Fragment selectedFragment = ProfileFragment.newInstance("","");
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.frame_layout, selectedFragment);
                transaction.commit();
                break;
        }

        return true;
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
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == Activity.RESULT_OK){

            if (data != null) {
                Uri contentURI = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), contentURI);
                    //String path = saveImage(bitmap);
                    Toast.makeText(getApplicationContext(),"Image Saved Sueecssfully!", Toast.LENGTH_SHORT).show();
                    //imageview.setImageBitmap(bitmap);
                    String retstr = getText(bitmap);
                    System.out.println("\n Gallery Returned Str is : \n" + retstr);

                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(),"Failed!", Toast.LENGTH_SHORT).show();
                }
                System.out.println("\n????????????request 1 Main???????????????");
            }
        }
        else if (requestCode == 100 && resultCode == Activity.RESULT_OK){
            prepareTessData();
            System.out.println("outputFileDir is "+outputFileDir+"\n");
            startOCR(outputFileDir);
            System.out.println("!!!!!!!!!!!!!OCR finished Main!!!!!!!!!!!!!\n\n\n\n");
        }/*
        else if (requestCode == 1 && resultCode == Activity.RESULT_OK){
            final Bundle extras = data.getExtras();
            if (extras != null) {
                //Get image
                Bitmap newProfilePic = extras.getParcelable("data");
            }
            System.out.println("\nhahahah"+ "hahahahaha" + "\n~~~~~~~~~~~~~~~~~~~");
        }*/
        else{
            System.out.println("\n????????????else: image problem???????????????");
            Toast.makeText(getApplicationContext(),"Image problem", Toast.LENGTH_SHORT).show();
        }


        System.out.println("---------------------end onActivityResult Main------------------------\n\n\n\n");

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
            processText(result);
            //mTextMessage.setText(result);
            searchview = findViewById(R.id.searchView);
            cardview = findViewById(R.id.cardView);
            cardview.setVisibility(View.GONE);
            //searchview.setQuery(result,false);
            //searchview.requestFocus();
            editText = findViewById(R.id.brand_entry);
            editText.setText(Brand, TextView.BufferType.EDITABLE);
            editText = findViewById(R.id.model_entry);
            editText.setText(Model, TextView.BufferType.EDITABLE);

            relativeLayout = findViewById(R.id.brand_and_model);
            relativeLayout.setVisibility(View.VISIBLE);

            button = findViewById(R.id.confirm_query);
            button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    relativeLayout.setVisibility(View.GONE);
                    cardview.setVisibility(View.VISIBLE);
                    Brand = editText.getText().toString();
                    Model = editText.getText().toString();
                    query = Brand+" "+Model;
                    showResult("text");
                }
            });
            button = findViewById(R.id.cancel_query);
            button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    relativeLayout.setVisibility(View.GONE);
                    cardview.setVisibility(View.VISIBLE);
                }
            });

        } catch (Exception e){
            Log.e(TAG, e.getMessage());
        }
    }

    private void processText(String text){
        int i, row, j=0;
        row = 0;
        int model_start_id=0;
        //ArrayList<Character> brand = new ArrayList<>();
        //ArrayList<Character> model = new ArrayList<>();
        String Brand = "";
        String Model = "";
        for (i = 0; i< text.length(); i++){
            if (text.charAt(i) == '\n'){


                if (row == 0) {
                    Brand = Brand.copyValueOf(text.toCharArray(), 0, i);
                    System.out.println("----------brand is " + Brand + "\n");

                    model_start_id = i+1;
                }
                if (row == 1) {
                    Model = Model.copyValueOf(text.toCharArray(), model_start_id, i - 1);
                    System.out.println("----------model is " + Model + ", row number is " + Integer.toString(row) + "\n");
                }
                row ++;
            }
            /*else{
                if (row == 0 && text.charAt(i) != ' ') {
                    brand.add(text.charAt(i));
                }
                else if (row == 1)
                    model.add(text.charAt(i));
            }*/
            if (row >1)
                break;
        }
        /*
        String newBrand = "";
        for (i=0; i<Brand.length(); i++){
            if (Brand.charAt(i) == ' '){
                continue;
            }
            newBrand.setCharAt()

        }*/
        Brand.replaceAll(" ", "");


    }


    public String getText(Bitmap bitmap){
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

    public void showResult(String userInput){
        /*TextView info;
        RatingBar rating;
        RelativeLayout search_results;
        float score = (float)4.4;

        search_results = findViewById(R.id.search_results);
        search_results.setVisibility(View.VISIBLE);

        info = findViewById(R.id.price);
        info.setText("Price: $"+"50");

        info = findViewById(R.id.ratings);
        info.setText("Ratings: "+String.valueOf(score));

        rating = findViewById(R.id.ratingBar);
        rating.setRating(score);

        info = findViewById(R.id.reviews);
        info.setText("Reviews: "+"This is a super longgggggggggggggggggggggggggggg test review" +
                "asdadadasdasdqwewqe" +
                "qweqweqweqw" +
                "qweqweqwewqeqeqwe" +
                "wqeqweqwe" +
                "qweqwewqeq" +
                "wqeqweqwe" +
                "qweqweqeqwe" +
                "qewqeqweqe" +
                "qweqweqeqweqe" +
                "qweqeqeqe" +
                "qewqewqeqeqe" +
                "qewqewqeqwe" +
                "qweqweqeqwe" +
                "qeqweqeqeqe" +
                "qewqeqeqee" +
                "qeqeqeqeqeqe" +
                "qeqeqeqeqe" +
                "qeq" +
                "sadasd" +
                "asdasdasd" +
                "" +
                "asdasdasd" +
                "" +
                "asda" +
                "sda" +
                "sd" +
                "ad" +
                "asd" +
                "asdas" +
                "da" +
                "dasdasdasd" +
                "adsadasdada" +
                "adasdaaa");*/
        JsoupAsyncTask jsoupAsyncTask = new JsoupAsyncTask();
        jsoupAsyncTask.execute();
    }

    private class JsoupAsyncTask extends AsyncTask<Void, Void, Void> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                Document secondSearch;
                Elements productDetail;
                Elements reviews;
                Elements firstReview;
                Elements secondReview;
                String isbn = "";

                String bookname = "the+fifth+risk";
                String url = "https://www.amazon.ca/s/ref=nb_sb_noss_1?url=search-alias%3Daps&field-keywords=" + bookname;
                Document firstSearch = Jsoup.connect(url).userAgent("Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; Trident/5.0; MALC)")
                        .timeout(999999999)
                        .get();
                Elements itemInList = firstSearch.select("div[data-index=0]");

                // get amazon rate
                Elements rateINFO =  itemInList.select("span[class=a-icon-alt]");
                String amazon_rate = rateINFO.text();
                amazon_rate = amazon_rate.substring(0,3);
                rate[0] = amazon_rate;


                Element itemLink = itemInList.select("a[class=a-size-base a-link-normal a-text-bold]").first();
                String itemVersion = itemLink.text();

                if (itemVersion.startsWith("Paperback")==false&itemVersion.startsWith("Hardcover")==false){
                    price[0] = "Something wrong";
                }
                else {
                    String link = "https://www.amazon.ca"+ itemLink.attr("href");
                    secondSearch = Jsoup.connect(link).userAgent("Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; Trident/5.0; MALC)")
                            .timeout(999999999)
                            .get();

                    // get amazon price
                    Elements priceINFO = secondSearch.getElementsByClass("swatchElement selected");
                    priceINFO = priceINFO.select("a[class=a-button-text]");
                    String amazon_price = priceINFO.text();
                    int start = amazon_price.indexOf("$");
                    amazon_price = amazon_price.substring(start+1);
                    price[0] = amazon_price;


                    // get book isbn
                    productDetail = secondSearch.select("div[class=content]");
                    isbn = productDetail.select("li:contains(ISBN-13)").text();

                    // get amazon review
                    reviews = secondSearch.select("div[class=a-row a-spacing-small review-data]");
                    firstReview = reviews.eq(1);
                    secondReview = reviews.eq(2);
                    String amazon_review_1 = firstReview.text();
                    String amazon_review_2 = secondReview.text();
                    int end = amazon_review_1.lastIndexOf("Read more");
                    amazon_review_1 = amazon_review_1.substring(0,end);
                    end = amazon_review_2.lastIndexOf("Read more");
                    amazon_review_2 = amazon_review_2.substring(0,end);
                    review[0] = amazon_review_1;
                    review[1] = amazon_review_2;
                }

                // search for indigo
                String finalISBN = isbn.substring(9, 12)+isbn.substring(13, 23);

                String indigourl = "https://www.chapters.indigo.ca/en-ca/books/as/" + finalISBN + "-item.html";
                Document indigoSearch = Jsoup.connect(indigourl).userAgent("Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; Trident/5.0; MALC)")
                        .timeout(999999999)
                        .get();
                productDetail = indigoSearch.getElementsByClass​("item-price__price-amount");
                String indigoPrice = productDetail.text();
                int start = indigoPrice.indexOf("$");
                int end = indigoPrice.lastIndexOf("online");
                indigoPrice = indigoPrice.substring(start+1,end);
                price[1] = indigoPrice;


                // search for goodread
                String goodreadurl = "https://www.goodreads.com/search?q=" + finalISBN;
                Document goodreadSearch = Jsoup.connect(goodreadurl).userAgent("Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; Trident/5.0; MALC)")
                        .timeout(999999999)
                        .get();
                String goodreadRates = goodreadSearch.select("span[itemprop='ratingValue']").first().text();
                rate[1] = goodreadRates;

                Elements goodreadReviews = goodreadSearch.getElementsByClass​("reviewText stacked");
                Elements goodreadFirstReview = goodreadReviews.eq(0);
                Elements goodreadSecondReview = goodreadReviews.eq(2);
                String goodread_review_1 = goodreadFirstReview.text();
                String goodread_review_2 = goodreadSecondReview.text();
                review[2] = goodread_review_1;
                review[3] = goodread_review_2;



            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        // print on screen
        @Override
        protected void onPostExecute(Void result) {
            TextView info;
            RatingBar rating;
            RelativeLayout search_results;

            search_results = findViewById(R.id.search_results);
            search_results.setVisibility(View.VISIBLE);

            System.out.println("Price: "+ price[0] + " + " + price[1]);
            info = findViewById(R.id.price);
            info.setText("Price: $"+price[0]);

            info = findViewById(R.id.ratings);
            info.setText("Ratings: "+ rate[0]);

            rating = findViewById(R.id.ratingBar);
            rating.setRating(Float.valueOf(rate[0]));

            info = findViewById(R.id.reviews);
            info.setText("Reviews: "+ review[0]);

        }
    }


    public void pickImage() {
        Intent intent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        /*
        intent.setType("image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("scale", true);
        intent.putExtra("outputX", 256);
        intent.putExtra("outputY", 256);
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        //intent.putExtra("return-data", true);*/
        startActivityForResult(intent, 1);
        System.out.println("\n~~~~~~~~~~~~~~~~~~~~~~"+ "pickImage_Main" + "~~~~~~~~~~~~~~~~~~~\n");
    }




}

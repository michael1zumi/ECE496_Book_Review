package com.tess.scarlett.tess_v5;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
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
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.ScrollView;
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
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.os.AsyncTask;

import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import static java.util.Collections.list;

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
    private boolean foundProduct = false;


    // string array
    private String bookname;
    private String foundItem;
    private String[] productLink = new String[2];
    private String[] price = new String[2];
    private String[] rate = new String[2];
    private String[] review = new String[4];

    public static final int REQUEST_CODE = 0x0000c0de; // Only use bottom 16 bits




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
                case R.id.navigation_isbn:
                    selectedFragment = SearchFragment.newInstance("","");
                    hideUpButton();
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.frame_layout, selectedFragment);
                    transaction.commit();
                    //new code below
                    System.out.println("lololololololol   before getParent()\n ");
                    IntentIntegrator integrator = new IntentIntegrator(MainActivity.this); //////// may not be correct
                    System.out.println("lololololololol   after getParent()\n ");
                    integrator.initiateScan();
                    break;
                case R.id.navigation_camera:
                    selectedFragment = SearchFragment.newInstance("","");
                    hideUpButton();
                    transaction = getSupportFragmentManager().beginTransaction();
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
        //Uri message = Uri.parse("111");
        Intent geniusIntent= new Intent(this, edu.sfsu.cs.orange.ocr.CaptureActivity.class);



        startActivityForResult(geniusIntent, 205);

        /*try{
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
        }*/
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

                    prepareTessData();


                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), contentURI);
                    //String path = saveImage(bitmap);
                    Toast.makeText(getApplicationContext(),"Image Saved Sueecssfully!", Toast.LENGTH_SHORT).show();
                    //imageview.setImageBitmap(bitmap);
                    System.out.println("\n [[[[[[[[[[[ right before gettexttt : \n");
                    String retstr = getText(bitmap);    //invoked own Tesseract OCR here



                    searchview = findViewById(R.id.searchView);
                    searchview.setQuery(retstr,false);
                    searchview.requestFocus();

                    //showResult(retstr);
                    //System.out.println("\n Gallery Returned Str is : \n" + retstr);

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
        }
        else if (requestCode == 205 && resultCode == Activity.RESULT_OK){
            //Uri OCRuri = data.getData();
            String recvData = data.getStringExtra("returnedLibString");
            System.out.println("........returnedLibString is :" + recvData);
            showResult(recvData);
        }
        else if (requestCode == REQUEST_CODE) {
            IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
            if (scanResult != null) {
                showResult(scanResult.getContents());
                System.out.println("&&&&&&&&&&&&&&&&&&&&&&Scan result is " + scanResult.getContents() + "\n");
            }
        }
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
            searchview = findViewById(R.id.searchView);
            searchview.setQuery(result,false);
            searchview.requestFocus();
            showResult(result);
//            processText(result);
//            searchview = findViewById(R.id.searchView);
//            cardview = findViewById(R.id.cardView);
//            cardview.setVisibility(View.GONE);
//            editText = findViewById(R.id.brand_entry);
//            editText.setText(Brand, TextView.BufferType.EDITABLE);
//            editText = findViewById(R.id.model_entry);
//            editText.setText(Model, TextView.BufferType.EDITABLE);
//
//            relativeLayout = findViewById(R.id.brand_and_model);
//            relativeLayout.setVisibility(View.VISIBLE);
//
//            button = findViewById(R.id.confirm_query);
//            button.setOnClickListener(new View.OnClickListener() {
//                public void onClick(View v) {
//                    relativeLayout.setVisibility(View.GONE);
//                    cardview.setVisibility(View.VISIBLE);
//                    Brand = editText.getText().toString();
//                    Model = editText.getText().toString();
//                    query = Brand+" "+Model;
//                    showResult(query);
//                }
//            });
//            button = findViewById(R.id.cancel_query);
//            button.setOnClickListener(new View.OnClickListener() {
//                public void onClick(View v) {
//                    relativeLayout.setVisibility(View.GONE);
//                    cardview.setVisibility(View.VISIBLE);
//                }
//            });

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
        RelativeLayout layout;
        RelativeLayout search_results;
        RelativeLayout notFoundPage;
        ScrollView scrollview;

        search_results = findViewById(R.id.search_results);
        search_results.setVisibility(View.GONE);
        notFoundPage = findViewById(R.id.productNotFound);
        notFoundPage.setVisibility(View.GONE);

        scrollview = findViewById(R.id.scrollView1);
        scrollview.fullScroll(ScrollView.FOCUS_UP);

        layout = findViewById(R.id.progressBarLayer);
        layout.setVisibility(View.VISIBLE);

        bookname = userInput;
        bookname = bookname.replaceAll(" ", "+");

        BottomNavigationView nav;
        nav = findViewById(R.id.navigation);
        nav.setOnNavigationItemSelectedListener(null);
        int size = nav.getMenu().size();
        for (int i=0;i<size;i++){
            nav.getMenu().getItem(i).setCheckable(false);
        }

        System.out.println(userInput);
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
                Element firstReview;
                String isbn = "";

                String url = "https://www.amazon.ca/s?k=" + bookname;
                Document firstSearch = Jsoup.connect(url).userAgent("Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; Trident/5.0; MALC)")
                        .timeout(999999999)
                        .get();



                Element itemInList;
                String searchWord;
                String itemContent;
                int index = 0;
                int bool = 0;
                do{
                    searchWord = "div[data-index="+ String.valueOf(index) +"]";
                    itemInList = firstSearch.select(searchWord).first();
                    if (itemInList == null){
                        bool = 1;
                        break;
                    }
                    itemContent = itemInList.text();
                    index ++;
                } while(itemContent.matches("(.*)Sponsored(.*)"));

                if (bool == 1){
                    foundItem = bookname;
                    rate[0] = "0.0";
                    rate[1] = "0.0";
                    price[0] = "0.0";
                    price[1] = "0.0";
                    review[0] = "None";
                    review[1] = "None";
                    review[2] = "None";
                    review[3] = "None";
                    productLink[0] = "None";
                    productLink[1] = "None";
                }
                else {
                    Element itemLink = itemInList.select("a[class=a-size-base a-link-normal a-text-bold]").first();
                    if (itemLink == null){ // may be electronic device
                        Element itemname = itemInList.select("a[class=a-link-normal a-text-normal]").first();
                        if (itemname == null){
                            foundItem = bookname;
                            rate[0] = "0.0";
                            rate[1] = "0.0";
                            price[0] = "0.0";
                            price[1] = "0.0";
                            review[0] = "None";
                            review[1] = "None";
                            review[2] = "None";
                            review[3] = "None";
                            productLink[0] = "None";
                            productLink[1] = "None";
                        }
                        else{
                            foundItem = itemname.text();
                            String rateINFO =  itemInList.select("span[class=a-icon-alt]").text();
                            if (itemInList.select("span[class=a-icon-alt]").first() == null){
                                foundItem = bookname;
                                rate[0] = "0.0";
                                rate[1] = "0.0";
                                price[0] = "0.0";
                                price[1] = "0.0";
                                review[0] = "None";
                                review[1] = "None";
                                review[2] = "None";
                                review[3] = "None";
                                productLink[0] = "None";
                                productLink[1] = "None";
                            }
                            else{
                                foundProduct = true;
                                rateINFO = rateINFO.substring(0,3);
                                rate[0] = rateINFO;
                                rate[0] = "Amazon: " + rate[0] + "/5";
                                //System.out.println(rate[0]);

                                String link = "https://www.amazon.ca"+ itemname.attr("href");

                                System.out.println("123"+link);
                                secondSearch = Jsoup.connect(link).userAgent("Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; Trident/5.0; MALC)")
                                        .timeout(999999999)
                                        .get();
                                String priceInfo = "";
                                if (secondSearch.select("span[id=priceblock_dealprice]").first()==null){
                                    priceInfo = secondSearch.select("span[id=priceblock_ourprice]").first().text();
                                }
                                else {
                                    priceInfo = secondSearch.select("span[id=priceblock_dealprice]").first().text();
                                }
                                int start = priceInfo.indexOf("$");
                                priceInfo = priceInfo.substring(start+1);
                                priceInfo = priceInfo.replaceAll(" ","");
                                price[0] = priceInfo;



                                reviews = secondSearch.select("div[id=productDescription]");
                                if (reviews.first() == null){
                                    foundProduct = false;
                                    foundItem = bookname;
                                    rate[0] = "0.0";
                                    rate[1] = "0.0";
                                    price[0] = "0.0";
                                    price[1] = "0.0";
                                    review[0] = "None";
                                    review[1] = "None";
                                    review[2] = "None";
                                    review[3] = "None";
                                    productLink[0] = "None";
                                    productLink[1] = "None";
                                }
                                else {
                                    foundProduct = true;

                                    productLink[0] = link;
                                    firstReview = reviews.select("p").first();
                                    review[0] = firstReview.text();
                                    review[0] = "<b>" + "Amazon: " + "</b> " + review[0];
                                    //System.out.println(review[0]);

                                    String searchName = foundItem;


                                    start = foundItem.lastIndexOf("(");
                                    if (start>0){
                                        searchName = foundItem.substring(0, start);
                                    }
                                    start = foundItem.lastIndexOf("[");
                                    if (start>0){
                                        searchName = foundItem.substring(0, start);
                                    }
                                    searchName = searchName.replaceAll(" ", "+");

                                    System.out.println(searchName);
                                    foundItem = "<b>" + foundItem + "</b>";

                                    int same = 1;
                                    String bestbuyurl = "https://www.bestbuy.ca/en-CA/Search/SearchResults.aspx?type=product&page=1&sortBy=relevance&sortDir=desc&query=" + searchName;
                                    Document bestbuySearch = Jsoup.connect(bestbuyurl).userAgent("Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; Trident/5.0; MALC)")
                                            .timeout(999999999)
                                            .get();
                                    Element cantfind = bestbuySearch.select("div[class=search-no-results]").first();
                                    if (cantfind != null){
                                        System.out.println("item name of amazon can not be found in busybuy");
                                        System.out.println(bookname);
                                        bestbuyurl = "https://www.bestbuy.ca/en-CA/Search/SearchResults.aspx?type=product&page=1&sortBy=relevance&sortDir=desc&query=" + bookname;
                                        bestbuySearch = Jsoup.connect(bestbuyurl).userAgent("Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; Trident/5.0; MALC)")
                                                .timeout(999999999)
                                                .get();
                                        cantfind = bestbuySearch.select("div[class=search-no-results]").first();
                                        same = 0;
                                        if (cantfind != null){
                                            rate[1] = "No rating in Bestbuy";
                                            price[1] = "Can't find the specific product in Bestbuy";
                                            review[2] = "No user review in "+"<b>" + "Bestbuy " + "</b> ";
                                            productLink[1] = "None";
                                            return null;
                                        }
                                    }


                                    Element selectedItem = bestbuySearch.select("ul[class=listing-items util_equalheight clearfix]").first();

                                    String bestbuyDesc = "";
                                    if (selectedItem == null){
                                        System.out.println("here");
                                        if (bestbuySearch.select("h1[class=product-title]").first() == null){
                                            rate[1] = "No rating in Bestbuy";
                                            price[1] = "Can't find the specific product in Bestbuy";
                                            review[2] = "No user review in "+"<b>" + "Bestbuy " + "</b> ";
                                            productLink[1] = "None";
                                        }
                                        else{
                                            if (same == 0){
                                                String itemInBestbuy = bestbuySearch.select("h1[class=product-title]").first().text();
                                                foundItem = "<p>Found in Amazon: " + "<b>"+foundItem + "</b></p>";
                                                foundItem = foundItem + "<p>Found in Bestbuy: " + "<b>"+itemInBestbuy + "</b></p>";
                                                foundItem = foundItem + "<small>Please try searching with a longer keyword</small>";
                                            }

                                            productLink[1] = bestbuyurl;
                                            if(bestbuySearch.select("div[class=prodprice ]").first() == null){
                                                price[1] = bestbuySearch.select("div[class=prodprice price-onsale]").text();
                                            }
                                            else{
                                                price[1] = bestbuySearch.select("div[class=prodprice ]").text();
                                            }
                                            start = price[1].indexOf("$");
                                            price[1] = price[1].substring(start+1);


                                            Element bestbuyRate = bestbuySearch.select("div[itemprop=ratingvalue]").first();

                                            if(bestbuyRate == null){
                                                rate[1] = "No rating of this product in Bestbuy";
                                            }
                                            else{
                                                //System.out.println(bestbuyRate);
                                                rate[1] = bestbuyRate.text();
                                                rate[1] = "Bestbuy: " + rate[1] + "/5";
                                            }

                                            bestbuyDesc  = bestbuySearch.select("div[class=tab-overview-item]").text();
                                            start = bestbuyDesc.indexOf("Overview");
                                            //System.out.println(bestbuyDesc);
                                            bestbuyDesc = bestbuyDesc.substring(start+9);
                                            review[2] = bestbuyDesc;
                                            review[2] = "<b>" + "Bestbuy: " + "</b> " + review[2];
                                        }



                                    }
                                    else {
                                        System.out.println("there");
                                        selectedItem = selectedItem.select("h4[class=prod-title]").first();
                                        selectedItem = selectedItem.select("a[href]").first();
                                        String itemlink = "https://www.bestbuy.ca"+ selectedItem.attr("href");
                                        //System.out.println(itemlink);
                                        secondSearch = Jsoup.connect(itemlink).userAgent("Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; Trident/5.0; MALC)")
                                                .timeout(999999999)
                                                .get();
                                        if (same == 0){
                                            String itemInBestbuy = secondSearch.select("h1[class=product-title]").first().text();
                                            foundItem = "<p>Found in Amazon: " + "<b>"+foundItem + "</b></p>";
                                            foundItem = foundItem + "<p>Found in Bestbuy: " + "<b>"+itemInBestbuy + "</b></p>";
                                            foundItem = foundItem + "<small>Please try searching with a longer keyword</small>";
                                        }

                                        if(secondSearch.select("div[class=prodprice ]").first() == null){
                                            price[1] = secondSearch.select("div[class=prodprice price-onsale]").first().text();
                                        }
                                        else{
                                            price[1] = secondSearch.select("div[class=prodprice ]").first().text();
                                        }

                                        start = price[1].indexOf("$");
                                        price[1] = price[1].substring(start+1);


                                        if(secondSearch.select("div[itemprop=ratingvalue]").first() == null){
                                            rate[1] = "No rating of this product in Bestbuy" ;
                                        }
                                        else {
                                            rate[1] = secondSearch.select("div[itemprop=ratingvalue]").first().text();
                                            rate[1] = "Bestbuy: " + rate[1] + "/5";
                                        }


                                        bestbuyDesc  = secondSearch.select("div[class=tab-overview-item]").text();
                                        start = bestbuyDesc.indexOf("Overview");
                                        bestbuyDesc = bestbuyDesc.substring(start+9);
                                        review[2] = bestbuyDesc;
                                        review[2] = "<b>" + "Bestbuy: " + "</b> " + review[2];
                                    }


                                    price[0] = price[0].replaceAll(",", "");
                                    price[1] = price[1].replaceAll(",", "");
                                    if (price[1].startsWith("Can't") == false){
                                        if (Float.parseFloat(price[0])>Float.parseFloat(price[1])){
                                            String swap = price[0];
                                            price[0] = "Bestbuy: $" + price[1];
                                            price[1] = "Amazon: $" + swap;
                                        }
                                        else{
                                            price[0] = "Amazon: $" + price[0];
                                            price[1] = "Bestbuy: $" + price[1];
                                        }

                                    }
                                    else {
                                        price[0] = "Amazon: $" + price[0];
                                    }


                                }







                            }

                        }
                    }

                    else {// may be book
                        String itemVersion = itemLink.text();

                        if (itemVersion.startsWith("Paperback")==false&itemVersion.startsWith("Hardcover")==false){
                            foundItem = bookname;
                            rate[0] = "0.0";
                            rate[1] = "0.0";
                            price[0] = "0.0";
                            price[1] = "0.0";
                            review[0] = "None";
                            review[1] = "None";
                            review[2] = "None";
                            review[3] = "None";
                            productLink[0] = "None";
                            productLink[1] = "None";
                        }
                        else {
                            foundProduct = true;
                            // get amazon rate
                            Elements rateINFO =  itemInList.select("span[class=a-icon-alt]");
                            String amazon_rate = rateINFO.text();
                            amazon_rate = amazon_rate.substring(0,3);
                            rate[1] = amazon_rate;
                            rate[1] = "Amazon: " + rate[1] + "/5";


                            // get book name
                            String itemname = itemInList.select("a[class=a-link-normal a-text-normal]").text();
                            foundItem = "<b>" + itemname +"</b>";

                            String link = "https://www.amazon.ca"+ itemLink.attr("href");
                            productLink[0] = link;
                            secondSearch = Jsoup.connect(link).userAgent("Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; Trident/5.0; MALC)")
                                    .timeout(999999999)
                                    .get();

                            // get amazon price
                            Elements priceINFO = secondSearch.getElementsByClass("swatchElement selected");
                            priceINFO = priceINFO.select("a[class=a-button-text]");
                            String amazon_price = priceINFO.text();
                            int start = amazon_price.indexOf("$");
                            amazon_price = amazon_price.substring(start+1);
                            amazon_price = amazon_price.replaceAll(" ","");
                            price[0] = amazon_price;

                            // get book isbn
                            productDetail = secondSearch.getElementsByClass​("content");
                            isbn = productDetail.select("li:contains(ISBN-13)").text();

                            // get amazon review
                            reviews = secondSearch.select("div[class=a-row a-spacing-small review-data]");
                            Element Review_1 = reviews.first();
                            //Elements Review_2 = reviews.eq(2);
                            String amazon_review_1 = Review_1.text();
                            //String amazon_review_2 = Review_2.text();
                            int end = amazon_review_1.lastIndexOf("Read more");
                            amazon_review_1 = amazon_review_1.substring(0,end);
                            //end = amazon_review_2.lastIndexOf("Read more");
                            //amazon_review_2 = amazon_review_2.substring(0,end);
                            review[0] = amazon_review_1;
                            review[1] = " ";
                            review[0] = "<b>" + "Amazon: " + "</b> " + review[0];
                            review[1] = "<b>" + "Amazon: " + "</b> " + review[1];



                            String finalISBN = isbn.substring(9, 12)+isbn.substring(13, 23);

                            String indigourl = "https://www.chapters.indigo.ca/en-ca/books/as/" + finalISBN + "-item.html";
                            productLink[1] = indigourl;
                            Document indigoSearch = Jsoup.connect(indigourl).userAgent("Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; Trident/5.0; MALC)")
                                    .timeout(999999999)
                                    .get();
                            Element productPrice = indigoSearch.getElementsByClass​("item-price__price-amount").first();
                            if (productPrice == null){
                                productPrice = indigoSearch.getElementsByClass​("item-price__normal").first();
                            }
                            String indigoPrice = productPrice.text();
                            start = indigoPrice.indexOf("$");
                            end = indigoPrice.lastIndexOf("online");
                            if (end>=0){
                                indigoPrice = indigoPrice.substring(start+1,end);
                            }
                            else{
                                indigoPrice = indigoPrice.substring(start+1);
                            }

                            price[1] = indigoPrice;




                            // search for goodread
                            String goodreadurl = "https://www.goodreads.com/search?q=" + finalISBN;
                            Document goodreadSearch = Jsoup.connect(goodreadurl).userAgent("Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; Trident/5.0; MALC)")
                                    .timeout(999999999)
                                    .get();
                            String goodreadRates = goodreadSearch.select("span[itemprop='ratingValue']").first().text();
                            rate[0] = goodreadRates;
                            rate[0] = "Goodreads: " + rate[0] + "/5";

                            Elements goodreadReviews = goodreadSearch.getElementsByClass​("reviewText stacked");
                            Elements goodreadFirstReview = goodreadReviews.eq(0);
                            Elements goodreadSecondReview = goodreadReviews.eq(2);
                            String goodread_review_1 = goodreadFirstReview.text();
                            String goodread_review_2 = goodreadSecondReview.text();
                            review[2] = goodread_review_1;
                            review[3] = goodread_review_2;
                            review[2] = "<b>" + "Goodreads: " + "</b> " + review[2];
                            review[3] = "<b>" + "Goodreads: " + "</b> " + review[3];


                            if (Float.parseFloat(price[0])>Float.parseFloat(price[1])){
                                String swap = price[0];
                                price[0] = "Indigo: $" + price[1];
                                price[1] = "Amazon: $" + swap;
                            }
                            else{
                                price[0] = "Amazon: $" + price[0];
                                price[1] = "Indigo: $" + price[1];
                            }
                        }
                    }
                }




            }
            catch (HttpStatusException e){
                System.out.println("catch1");

            }
            catch (IOException e) {
                System.out.println("catch2");
            }
            return null;
        }

        // print on screen
        @Override
        protected void onPostExecute(Void result) {
            TextView info;
//            RatingBar rating;
            RelativeLayout search_results;
            RelativeLayout layout;
            RelativeLayout notFoundPage;
            layout = findViewById(R.id.progressBarLayer);
            layout.setVisibility(View.GONE);
            BottomNavigationView nav;
            nav = findViewById(R.id.navigation);
            nav.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
            int size = nav.getMenu().size();
            for (int i=0;i<size;i++){
                nav.getMenu().getItem(i).setCheckable(true);
            }

            if (foundProduct) {
                search_results = findViewById(R.id.search_results);
                search_results.setVisibility(View.VISIBLE);

                info = findViewById(R.id.product_name);
                info.setText(Html.fromHtml(foundItem));

                System.out.println("Price: " + price[0] + " + " + price[1]);
                info = findViewById(R.id.price);
                info.setText(price[0]);
                info = findViewById(R.id.price2);
                info.setText(price[1]);

                info = findViewById(R.id.ratings);
                info.setText(rate[0]);
                info = findViewById(R.id.ratings2);
                info.setText(rate[1]);

//            rating = findViewById(R.id.ratingBar);
//            rating.setRating(Float.valueOf(rate[1]));
//            rating = findViewById(R.id.ratingBar2);
//            rating.setRating(Float.valueOf(rate[0]));

                info = findViewById(R.id.reviews);
                String amazon_review = review[0];
                info.setText(Html.fromHtml(amazon_review));
                String goodreads_review = review[2];
                info = findViewById(R.id.reviews2);
                info.setText(Html.fromHtml(goodreads_review));

                foundProduct=false;
            }
            else{
                notFoundPage = findViewById(R.id.productNotFound);
                notFoundPage.setVisibility(View.VISIBLE);
                info = findViewById(R.id.notFoundText3);
                info.setText("\""+foundItem+"\"");
            }
        }
    }


    public void pickImage() {
        Intent intent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);

        startActivityForResult(intent, 1);
        System.out.println("\n~~~~~~~~~~~~~~~~~~~~~~"+ "pickImage_Main" + "~~~~~~~~~~~~~~~~~~~\n");
    }

    public String getBookname() {
        return bookname;
    }

    public String[] getProductLink(){
        return productLink;
    }

}

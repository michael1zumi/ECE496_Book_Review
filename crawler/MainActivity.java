package com.example.atry;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class MainActivity extends AppCompatActivity {
    private TextView parsedHtmlNode;
    private String htmlContentInStringFormat = " ";
    
	// string array
	private String[] price = new String[2];
    private String[] rate = new String[2];
    private String[] review = new String[4];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        parsedHtmlNode = (TextView)findViewById(R.id.html_content);
        Button htmlTitleButton = (Button)findViewById(R.id.button);
        htmlTitleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JsoupAsyncTask jsoupAsyncTask = new JsoupAsyncTask();
                jsoupAsyncTask.execute();
            }
        });
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

                String bookname = "becoming";
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
                    System.out.println("wrong version");
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
            for (int i=0;i<2;i++){
                htmlContentInStringFormat = htmlContentInStringFormat + rate[i] + "\n";
            }
            parsedHtmlNode.setText(htmlContentInStringFormat);
        }
    }
}

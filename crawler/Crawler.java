/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package crawler;
import java.io.IOException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.util.Scanner; 

/**
 *
 * @author limengl2
 */
public class Crawler {

    public static void get_book_info(String url, Book_info new_book){
        try {
            Document secondSearch;
            Elements productDetail;
            Elements reviews;
            Elements firstReview;
            Elements secondReview;
            String isbn = "";
            
            //String Hprice_amazon;
            //String Pprice_amazon;
            
            Document firstSearch = Jsoup.connect(url).userAgent("Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; Trident/5.0; MALC)")
                                                .timeout(999999999)
                                                .get();
            Elements itemInList = firstSearch.select("div[data-index=0]");
            
            // get amazon rate
            Elements rateINFO =  itemInList.select("span[class=a-icon-alt]");
            new_book.set_amazon_rate(rateINFO.text());
                       
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
                new_book.set_amazon_price(priceINFO.text());
                
                // get book isbn
                productDetail = secondSearch.getElementsByClass​("content");
                isbn = productDetail.select("li:contains(ISBN-13)").text();
                
                // get amazon review
                reviews = secondSearch.getElementsByClass​("a-row a-spacing-small review-data");
                firstReview = reviews.eq(1);
                secondReview = reviews.eq(2);
                new_book.set_amazon_review1(firstReview.text()); 
                new_book.set_amazon_review2(secondReview.text()); 
            }                       
                   
            // search for indigo
            String finalISBN = isbn.substring(9, 12)+isbn.substring(13, 23);
            new_book.set_bookisbn(finalISBN);
            
            String indigourl = "https://www.chapters.indigo.ca/en-ca/books/as/" + finalISBN + "-item.html";
            Document indigoSearch = Jsoup.connect(indigourl).userAgent("Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; Trident/5.0; MALC)")
                                        .timeout(999999999)
                                        .get();
            productDetail = indigoSearch.getElementsByClass​("item-price__price-amount");
            String indigoPrice = productDetail.text();
            new_book.set_indigo_price(indigoPrice);
            

            // search for goodread
            String goodreadurl = "https://www.goodreads.com/search?q=" + finalISBN;
            Document goodreadSearch = Jsoup.connect(goodreadurl).userAgent("Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; Trident/5.0; MALC)")
                                        .timeout(999999999)
                                        .get();
            String goodreadRates = goodreadSearch.select("span[itemprop='ratingValue']").first().text() + " out of 5 stars";
            new_book.set_goodread_rate(goodreadRates);
            
            Elements goodreadReviews = goodreadSearch.getElementsByClass​("reviewText stacked");
            Elements goodreadFirstReview = goodreadReviews.eq(0);
            Elements goodreadSecondReview = goodreadReviews.eq(2);
            new_book.set_goodread_review1(goodreadFirstReview.text());
            new_book.set_goodread_review2(goodreadSecondReview.text());

        } catch (IOException e) {
                e.printStackTrace();
        }
    }
    public static void main(String[] args) {
        Scanner userInput = new Scanner(System.in);
        String product;

        // Enter username and press Enter
        System.out.println("Enter product name"); 
        product = userInput.nextLine();   
  
        product = product.replaceAll(" ", "+");
        System.out.println("Product name is: " + product);    
        //product = "hunger+games";
        String url = "https://www.amazon.ca/s/ref=nb_sb_noss_1?url=search-alias%3Daps&field-keywords=" + product;
        
        Book_info newbook = new Book_info();
        newbook.set_bookname(product);
        get_book_info(url,newbook);
        
        System.out.println("Bookname: "+newbook.get_bookname());
        System.out.println("ISBN: "+newbook.get_bookisbn());
        System.out.println("From AMAZON");
        System.out.println("Price: "+newbook.get_amazon_price());
        System.out.println("Rate: "+newbook.get_amazon_rate());
        System.out.println("Review1: "+newbook.get_amazon_review1());
        System.out.println("Review2: "+newbook.get_amazon_review2());
        System.out.println("From INDIGO");
        System.out.println("Price: "+newbook.get_indigo_price());
        System.out.println("From GOODREAD");
        System.out.println("Rate: "+newbook.get_goodread_rate());
        System.out.println("Review1: "+newbook.get_goodread_review1());
        System.out.println("Review2: "+newbook.get_goodread_review2()
        
        );
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package crawler;

/**
 *
 * @author limengl2
 */
public class Book_info {
    // private variables declared  
    // these can only be accessed by  
    // public methods of class 
    private String bookname; 
    private String isbn; 
    private String amazon_price; 
    private String amazon_rate; 
    private String amazon_review1; 
    private String amazon_review2; 
    private String indigo_price; 
    private String goodread_rate;
    private String goodread_review1;
    private String goodread_review2; 
  

    public String get_bookname()  
    { 
      return bookname; 
    } 
    
    public String get_bookisbn()  
    { 
      return isbn; 
    }
    
    public String get_amazon_price()  
    { 
      return amazon_price; 
    } 
    
    public String get_amazon_rate()  
    { 
      return amazon_rate; 
    } 
     
    public String get_amazon_review1()  
    { 
      return amazon_review1; 
    } 
     
    public String get_amazon_review2()  
    { 
      return amazon_review2; 
    } 
    
    public String get_indigo_price()  
    { 
      return indigo_price; 
    } 
    
    public String get_goodread_rate()  
    { 
      return goodread_rate; 
    } 
    
    public String get_goodread_review1()  
    { 
      return goodread_review1; 
    } 
    
    public String get_goodread_review2()  
    { 
      return goodread_review2; 
    } 
   
    // set method for age to access  
    // private variable geekage 
    public void set_bookname(String newbook) 
    { 
      bookname = newbook; 
    }
    
    public void set_bookisbn(String newisbn) 
    { 
      isbn = newisbn; 
    }

    public void set_amazon_price(String price) 
    { 
      amazon_price = price; 
    } 
    
    public void set_amazon_rate(String rate) 
    { 
      amazon_rate = rate; 
    } 
    
    public void set_amazon_review1(String review) 
    { 
      amazon_review1 = review; 
    } 
    
    public void set_amazon_review2(String review) 
    { 
      amazon_review2 = review; 
    } 
    
    public void set_indigo_price(String price) 
    { 
      indigo_price = price; 
    } 
    
    public void set_goodread_rate(String rate) 
    { 
      goodread_rate = rate; 
    } 
    
    public void set_goodread_review1(String review) 
    { 
      goodread_review1 = review; 
    } 
    
    public void set_goodread_review2(String review) 
    { 
      goodread_review2 = review; 
    } 
    
   
}
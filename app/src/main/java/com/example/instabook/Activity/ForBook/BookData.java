package com.example.instabook.Activity.ForBook;

public class BookData {
    public String BookName;
    public String Publisher;
    public String ISBN13;
    public String[] Author;

    public BookData() {

    }
    /**TODO 저자정보 배열로 받아보기
     public String[] getAuthor() {
     return Author;
     }

     public void setAuthor(String[] author) {
     this.Author = author;
     }
     */
    public String getTitle() {
        return BookName;
    }

    public void setTitle(String title) {
        this.BookName = title;
    }

    public String getPublisher() {
        return Publisher;
    }

    public void setPublisher(String Publisher) {
        this.Publisher = Publisher;
    }

    public String getIsbn() {
        return ISBN13;
    }

    public void setIsbn(String isbn) {
        this.ISBN13 = isbn;
    }

}
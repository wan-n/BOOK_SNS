package com.example.instabook.Activity.ForBook;

public class BookData {
    public String BookName;
    public String Publisher;
    public String ISBN13;
    public String Author;
    public String BookImageUrl;

    public BookData() {

    }

    public void setAuthor(String author) {
        Author = author;
    }

    public String getAuthor() {
        return Author;
    }

    public String getBookImageUrl() {
        return BookImageUrl;
    }

    public void setBookImageUrl(String bookImageUrl) {
        BookImageUrl = bookImageUrl;
    }

    public String getBookName() {
        return BookName;
    }

    public void setBookName(String bookName) {
        BookName = bookName;
    }

    public String getPublisher() {
        return Publisher;
    }

    public void setPublisher(String Publisher) {
        this.Publisher = Publisher;
    }

    public String getISBN13() {
        return ISBN13;
    }

    public void setISBN13(String ISBN13) {
        this.ISBN13 = ISBN13;
    }
}
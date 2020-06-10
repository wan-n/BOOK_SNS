package com.example.instabook.ListView;

public class SearchBookItem {
    public int iconDrawable;
    public String title;
    public String isbn;
    public String publisher;
    public String author;

    public SearchBookItem(String t, String is, String p){
        this.title = t;
        this.isbn = is;
        this.publisher = p;
    }

    public void setIcon(int icon) {
        iconDrawable = icon ;
    }
    public void setTitle(String title) { this.title = title ; }
    public void setIsbn(String isbn) { this.isbn = isbn; }
    public void setAuthor(String author) {this.author = author; }
    public void setPublisher(String publisher) { this.publisher = publisher; }

    public int getIcon() {
        return this.iconDrawable ;
    }
    public String getTitle() {
        return this.title ;
    }
    public String getAuthor() {return this.author;}
    public String getIsbn() {return this.isbn ; }
    public String getPublisher() { return this.publisher ;}
}

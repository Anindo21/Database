package de.unidue.inf.is.domain;


public class Comment {
    final String name;
    final String text;

    public Comment(String name, String text) {
        this.name = name;
        this.text = text;
    }
   // public void setName(String name) {
     //   this.name = name;
    //}

    public String getName() {
        return name;
    }
   // public void setText(String text) {
     //   this.text = text;
    //}

    public String getText() {
        return text;
    }
}

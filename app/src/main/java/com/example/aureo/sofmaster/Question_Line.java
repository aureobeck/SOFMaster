package com.example.aureo.sofmaster;

/**
 * Created by Aureo on 28/02/2016.
 */
public class Question_Line {

    private String title;
    private String user;
    private String image_url;
    private String classification;

    public Question_Line(String title,
                         String user,
                         String image_url,
                         String classification

    ){
        super();
        this.title = title;
        this.user = user;
        this.image_url = image_url;
        this.classification = classification;

    }

    public String getTitle(){return title;}
    public void setTitle(String str) {title = str;}

    public String getUser(){return user;}
    public void setUser(String str) {user = str;}

    public String getImageURL(){return image_url;}
    public void setImageURL(String str) {image_url = str;}

    public String getClassification(){return classification;}
    public void setClassification(String integer) {classification = integer;}

}

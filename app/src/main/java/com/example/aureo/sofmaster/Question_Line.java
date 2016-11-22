package com.example.aureo.sofmaster;

public class Question_Line {

    private String title;
    private String user;
    private String image_url;
    private String classification;
    private String id;
    private String body;
    private String answerJSON;


    public Question_Line(String title,
                         String user,
                         String image_url,
                         String classification,
                         String id,
                         String body,
                         String answerJSON

    ){
        super();
        this.title = title;
        this.user = user;
        this.image_url = image_url;
        this.classification = classification;
        this.id = id;
        this.body = body;
        this.answerJSON = answerJSON;

    }

    public String getTitle(){return title;}
    public void setTitle(String str) {title = str;}

    public String getUser(){return user;}
    public void setUser(String str) {user = str;}

    public String getImageURL(){return image_url;}
    public void setImageURL(String str) {image_url = str;}

    public String getClassification(){return classification;}
    public void setClassification(String str) {classification = str;}

    public String getId(){return id;}
    public void setId(String str) {id = str;}

    public String getBody(){return body;}
    public void setBody(String str) {body = str;}

    public String getAnswerJSON(){return answerJSON;}
    public void setAnswerJSON(String str) {answerJSON = str;}
}

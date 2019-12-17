package com.example.youssefiibrahim.gallereo.model;


public class Request {
    public String jsonObject;
    public String endpoint;
    public String contentType;

    public Request(String jsonObject, String endpoint, String contentType) {
        this.jsonObject = jsonObject;
        this.endpoint = endpoint;
        this.contentType = contentType;
    }
}

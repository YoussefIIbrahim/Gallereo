package com.example.youssefiibrahim.gallereo.model;

import android.support.annotation.Nullable;

import java.util.ArrayList;

public class ResponseWrapper {

    public static ResponseWrapper singleton;

    public ArrayList<Response> responses;

    public ResponseWrapper(ArrayList<Response> responses) {
        this.responses = responses;
    }
    public ResponseWrapper() {
        this.responses = new ArrayList<>();
    }

    public boolean add(Response response) {
        return this.responses.add(response);
    }

    public boolean add(ResponseWrapper responseWrapper) {
        return this.responses.addAll(responseWrapper.responses);
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        ResponseWrapper other = (ResponseWrapper)obj;
        if (other.responses.size() != this.responses.size()) {
            System.out.print("FALSE!");
            return false;
        }
        boolean ret = true;
        for (int i = 0; i < this.responses.size(); i++) {
            System.out.print("FALSE! " + i);
            ret &= this.responses.get(i).equals(other.responses.get(i));
        }
        return ret;
    }
}

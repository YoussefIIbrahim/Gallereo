package com.example.youssefiibrahim.gallereo.model;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;

public class Response {

    public String id;
    public ArrayList<ResponseItem> data;

    public Response(String id, ArrayList<ResponseItem> data) {
        this.id = id;
        this.data = data;
    }
    public Response(String id) {
        this.id = id;
        this.data = new ArrayList<ResponseItem>();
    }

    public boolean add(ResponseItem item) {
        return this.data.add(item);
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        Response other = (Response)obj;
        if (!this.id.equals(other.id) || this.data.size() != other.data.size()) {
            return false;

        }
        boolean ret = true;
        ArrayList<ResponseItem> otherResponseItems = other.data;
        for (int i = 0; i < this.data.size(); i++) {
            if (!this.data.get(i).equals(otherResponseItems.get(i))) {
                ret = false;
            }
        }
        return ret;
    }

    @NonNull
    @Override
    public String toString() {
        System.out.println("CALLING TOSTRING");
        return id + "," + data.toString();
    }
}

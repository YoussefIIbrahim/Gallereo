package com.example.youssefiibrahim.gallereo.model;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class ResponseItem {
    public String label;
    public Double score;

    public ResponseItem(String label, Double score) {
        this.label = label;
        this.score = score;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        ResponseItem other = (ResponseItem)obj;
        return this.label.equals(other.label) && this.score.equals(other.score);
    }

    @NonNull
    @Override
    public String toString() {
        return "label=" + this.label + ",score=" + this.score;
    }
}

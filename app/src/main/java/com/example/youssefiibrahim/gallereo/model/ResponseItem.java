package com.example.youssefiibrahim.gallereo.model;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class ResponseItem {
    public String label;
    public Double score;
    public Boolean isProcessed;

    public ResponseItem(String label, Double score, Boolean isProcessed) {
        this.label = label;
        this.score = score;
        this.isProcessed = isProcessed;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        ResponseItem other = (ResponseItem)obj;
        return this.label.equals(other.label) && this.score.equals(other.score) && this.isProcessed.equals(other.isProcessed);
    }

    @NonNull
    @Override
    public String toString() {
        return "label=" + this.label + ",score=" + this.score + ",isProcessed=" + this.isProcessed;
    }
}

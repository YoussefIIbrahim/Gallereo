package com.example.youssefiibrahim.gallereo.model;

import android.support.annotation.Nullable;

public class ImageStructure{

    private final String IMG = "IMG";
    private String imageBase64;

    public ImageStructure(String imageBase64) {
        this.imageBase64 = imageBase64;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        ImageStructure structure = (ImageStructure) obj;
        return this.imageBase64.equals(structure.imageBase64);
    }
}

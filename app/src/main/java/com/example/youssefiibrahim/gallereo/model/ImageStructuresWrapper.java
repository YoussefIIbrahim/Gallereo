package com.example.youssefiibrahim.gallereo.model;

import android.support.annotation.Nullable;

import java.util.ArrayList;

public class ImageStructuresWrapper {
    public ArrayList<ImageStructure> imageStructures;

    public ImageStructuresWrapper() {
        imageStructures = new ArrayList<>();
    }
    public ImageStructuresWrapper(ArrayList<ImageStructure> imageStructures) {
        this.imageStructures = imageStructures;
    }

    public boolean add(ImageStructure imageStructure) {
        return this.imageStructures.add(imageStructure);
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        ImageStructuresWrapper other = (ImageStructuresWrapper)obj;
        if (other.imageStructures.size() != this.imageStructures.size()) {
            return false;
        }
        boolean ret = true;

        for (int i = 0; i < this.imageStructures.size(); i++) {
            ret &= this.imageStructures.get(i).equals(other.imageStructures.get(i));
        }
        return ret;
    }
}

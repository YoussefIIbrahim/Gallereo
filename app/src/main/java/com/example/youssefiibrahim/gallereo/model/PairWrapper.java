package com.example.youssefiibrahim.gallereo.model;

import java.util.ArrayList;

public class PairWrapper {

    public  ArrayList<Pair> pairs;

    public PairWrapper() {
        this.pairs = new ArrayList<>();
    }

    public PairWrapper(ArrayList<Pair> pairs) {
        this.pairs = pairs;
    }

    public void add(Pair pair) {
        this.pairs.add(pair);
    }
}

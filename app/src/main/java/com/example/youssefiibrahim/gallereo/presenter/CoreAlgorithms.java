package com.example.youssefiibrahim.gallereo.presenter;

import android.content.Context;

import com.example.youssefiibrahim.gallereo.model.ImageStructuresWrapper;
import com.example.youssefiibrahim.gallereo.model.Pair;
import com.example.youssefiibrahim.gallereo.model.PairWrapper;
import com.example.youssefiibrahim.gallereo.model.Response;
import com.example.youssefiibrahim.gallereo.model.ResponseItem;
import com.example.youssefiibrahim.gallereo.model.ResponseWrapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.TreeSet;

public class CoreAlgorithms {

    public static ArrayList<String> filterImages(PairWrapper pairWrapper) {

        if (ResponseWrapper.singleton == null || ResponseWrapper.singleton.responses == null || pairWrapper == null || pairWrapper.pairs == null) {
            return new ArrayList<String>();
        }

        ArrayList<String> ret = new ArrayList<>();
        ArrayList<Response> wanted = new ArrayList<>();

        for (Response response : ResponseWrapper.singleton.responses) {
            Double degreeOfSimilarity = 0.0;
            for (ResponseItem item : response.data) {
                for (Pair pair : pairWrapper.pairs) {
                    if (pair.key == null || item.label == null) continue;
                    String word = pair.key.toLowerCase();
                    String[] labels = item.label.toLowerCase().split("\\s+");
                    for (String label : labels) {
                        if (label.equals(word)) {
                            degreeOfSimilarity += pair.value * item.score;
                        }
                    }

                }
            }

            response.degreeOfSimilarity = degreeOfSimilarity;
            if (response.degreeOfSimilarity > 0) {
                wanted.add(response);
            }
        }

        Collections.sort(wanted);

        for (Response response : wanted) {
            ret.add(response.id);
        }
        TreeSet<String> tree = new TreeSet<>(ret);
        ret = new ArrayList<>(tree);

        return ret;
    }


}

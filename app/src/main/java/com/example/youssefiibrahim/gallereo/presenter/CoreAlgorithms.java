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

public class CoreAlgorithms {

    public static ArrayList<String> filterImages(String input) throws IOException {

        ArrayList<String> ret = new ArrayList<>();

//        PairWrapper processedInput = communication.processInput(input);
//        ResponseWrapper responseWrapper = ResponseWrapper.singleton;

        return ret;
    }

    public static ArrayList<String> filterImages(PairWrapper pairWrapper) {
        ArrayList<String> ret = new ArrayList<>();
        ArrayList<Response> wanted = new ArrayList<>();

        for (Response response : ResponseWrapper.singleton.responses) {
            System.out.println("RESPONSE ITEM = " + response.data.get(1));
            Double degreeOfSimilarity = 0.0;
            for (ResponseItem item : response.data) {
                for (Pair pair : pairWrapper.pairs) {
                    if (pair.word.equals(item.label)) {
                        degreeOfSimilarity += pair.weight * item.score;
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
        return ret;
    }


}

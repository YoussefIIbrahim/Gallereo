package com.example.youssefiibrahim.gallereo.presenter;

import android.content.Context;

import com.example.youssefiibrahim.gallereo.model.ImageStructuresWrapper;
import com.example.youssefiibrahim.gallereo.model.PairWrapper;
import com.example.youssefiibrahim.gallereo.model.ResponseWrapper;

import java.io.IOException;
import java.util.ArrayList;

public class CoreAlgorithms {

    public static ArrayList<String> filterImages(String input) throws IOException {

        ArrayList<String> ret = new ArrayList<>();

        PairWrapper processedInput = communication.processInput(input);
        ResponseWrapper responseWrapper = ResponseWrapper.singleton;

        return ret;
    }


}

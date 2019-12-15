package com.example.youssefiibrahim.gallereo.presenter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Debug;
import android.util.Log;

import com.example.youssefiibrahim.gallereo.model.ImageStructure;
import com.example.youssefiibrahim.gallereo.model.ImageStructuresWrapper;
import com.example.youssefiibrahim.gallereo.model.Response;
import com.example.youssefiibrahim.gallereo.model.ResponseItem;
import com.example.youssefiibrahim.gallereo.model.ResponseWrapper;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class ProcessingTest{

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void encodeToBase64() {
        int width = 300;
        int height = 300;
        Bitmap actualBm = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        actualBm.eraseColor(Color.WHITE);

        assert (actualBm != null);

        String encoded = Processing.encodeToBase64(actualBm, Bitmap.CompressFormat.JPEG, 100);

        Bitmap expectedBm = Processing.decodeBase64(encoded);
        assert(expectedBm.equals(actualBm));
    }

    @Test
    public void decodeBase64() {
        int width = 300;
        int height = 300;
        Bitmap actualBm = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        actualBm.eraseColor(Color.BLACK);

        assert (actualBm != null);

        String encoded = Processing.encodeToBase64(actualBm, Bitmap.CompressFormat.PNG, 100);

        Bitmap expectedBm = Processing.decodeBase64(encoded);
        assert(expectedBm.equals(actualBm));
    }

    @Test
    public void getResizedBitmap() {
        int width = 500;
        int height = 500;
        Bitmap actualBm = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        actualBm.eraseColor(Color.BLACK);

        Bitmap expectedBm = Processing.getResizedBitmap(actualBm, 300);

        assert (expectedBm != null && expectedBm.getWidth() == 300 && expectedBm.getHeight() == 300);
    }

//    @Test
//    public void shouldToJson() {
//        ImageStructure structure = new ImageStructure(null, "base64");
//        String json = Processing.toJson(structure);
//        assertEquals("{\"IMG\":\"IMG\",\"imageBase64\":\"base64\"}", json);
//    }
//
//
//    @Test
//    public void shouldFromJson() {
//        String json = "{\"IMG\":\"IMG\",\"imageBase64\":\"base64\"}";
//        ImageStructure expectedStructure = new ImageStructure(null, "base64");
//        ImageStructure actualStructure = Processing.fromJson(json);
//        assertEquals(expectedStructure, actualStructure);
//    }

    @Test
    public void shouldToJsonWrapper() {
        ImageStructure structure = new ImageStructure("1", "base64");
        ImageStructure structure2 = new ImageStructure("2", "base64");
        ImageStructuresWrapper wrapper = new ImageStructuresWrapper();
        wrapper.add(structure);
        wrapper.add(structure2);
        String json = Processing.toJson(wrapper);
        assertEquals("{\"imageStructures\":[{\"id\":\"1\",\"imageBase64\":\"base64\"},{\"id\":\"2\",\"imageBase64\":\"base64\"}]}", json);




    }

    @Test
    public void shouldFromJsonWrapper() {
        ImageStructure structure = new ImageStructure("1", "base64");
        ImageStructure structure2 = new ImageStructure("2", "base64");
        ImageStructuresWrapper wrapper = new ImageStructuresWrapper();
        wrapper.add(structure);
        wrapper.add(structure2);
        String json = Processing.toJson(wrapper);

        ImageStructuresWrapper fromJson = (ImageStructuresWrapper) Processing.fromJson(json, ImageStructuresWrapper.class);
        assertEquals(wrapper, fromJson);

        json = "{\"responses\":[{\"data\":[{\"label\":\"Natural environment\",\"score\":0.9935060739517212},{\"label\":\"Desert\",\"score\":0.9629292488098145}],\"id\":\"1\"},{\"data\":[{\"label\":\"Natural environment\",\"score\":0.9935060739517212},{\"label\":\"Desert\",\"score\":0.9629292488098145}],\"id\":\"2\"}]}";
        ResponseWrapper responseWrapper = (ResponseWrapper) Processing.fromJson(json, ResponseWrapper.class);

        String jsonString = Processing.toJson(responseWrapper);

        assertEquals(jsonString, json);

        ResponseWrapper expectedWrapper = new ResponseWrapper();

        Response response1 = new Response("1");
        Response response2 = new Response("2");

        ResponseItem item = new ResponseItem("Natural environment", 0.9935060739517212);

        ResponseItem item2 = new ResponseItem("Desert", 0.9629292488098145);

        response1.add(item);
        response1.add(item2);

        response2.add(item);
        response2.add(item2);

        expectedWrapper.add(response1);
        expectedWrapper.add(response2);

        assertEquals(expectedWrapper, responseWrapper);
    }
}
package com.example.youssefiibrahim.gallereo.presenter;

import com.example.youssefiibrahim.gallereo.model.ImageStructure;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class ProcessingTest {

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void encodeToBase64() {
    }

    @Test
    public void decodeBase64() {
    }

    @Test
    public void getResizedBitmap() {
    }

    @Test
    public void toJson() {
        ImageStructure structure = new ImageStructure("base64");
        String json = Processing.toJson(structure);
        assertEquals("{\"IMG\":\"IMG\",\"imageBase64\":\"base64\"}", json);
    }

    
    @Test
    public void fromJson() {
        String json = "{\"IMG\":\"IMG\",\"imageBase64\":\"base64\"}";
        ImageStructure expectedStructure = new ImageStructure("base64");
        ImageStructure actualStructure = Processing.fromJson(json);
        assertEquals(expectedStructure, actualStructure);
    }
}
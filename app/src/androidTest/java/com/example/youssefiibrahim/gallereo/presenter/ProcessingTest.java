package com.example.youssefiibrahim.gallereo.presenter;

import android.graphics.Bitmap;
import android.graphics.Color;

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

        Bitmap expectedBm = Processing.getResizedBitmap(actualBm, 300, 300);

        assert (expectedBm != null && expectedBm.getWidth() == 300 && expectedBm.getHeight() == 300);
    }

    @Test
    public void shouldToJson() {
        ImageStructure structure = new ImageStructure(null, "base64");
        String json = Processing.toJson(structure);
        assertEquals("{\"IMG\":\"IMG\",\"imageBase64\":\"base64\"}", json);
    }


    @Test
    public void shouldFromJson() {
        String json = "{\"IMG\":\"IMG\",\"imageBase64\":\"base64\"}";
        ImageStructure expectedStructure = new ImageStructure(null, "base64");
        ImageStructure actualStructure = Processing.fromJson(json);
        assertEquals(expectedStructure, actualStructure);
    }
}
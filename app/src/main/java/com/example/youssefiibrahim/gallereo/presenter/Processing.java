package com.example.youssefiibrahim.gallereo.presenter;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.util.Base64;

import com.example.youssefiibrahim.gallereo.model.ImageStructure;

import com.example.youssefiibrahim.gallereo.model.ImageStructuresWrapper;
import com.example.youssefiibrahim.gallereo.model.ResponseWrapper;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.GenericSignatureFormatError;

public class Processing {

    private static Gson jsonify = new Gson();

    public static String encodeToBase64(Bitmap image, Bitmap.CompressFormat compressFormat, int quality)
    {
        ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream();
        image.compress(compressFormat, quality, byteArrayOS);
        return Base64.encodeToString(byteArrayOS.toByteArray(), Base64.DEFAULT);
    }

    public static Bitmap decodeBase64(String input)
    {
        byte[] decodedBytes = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }

    public static Bitmap getResizedBitmap(Bitmap bm, int maxSize) {

        int width = bm.getWidth();
        int height = bm.getHeight();
        int minn = Math.min(width, height);

        float ratio = ((float) maxSize) / minn;
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bm, Math.round(ratio * width), Math.round(ratio * height), false);
        return resizedBitmap;
    }

    public static String toJson(Object obj) {
        return jsonify.toJson(obj);
    }


    public static Object fromJson(String jsonString, Class<?> cls) {
        return jsonify.fromJson(jsonString, cls);
    }


}

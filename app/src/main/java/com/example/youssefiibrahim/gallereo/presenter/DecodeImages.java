package com.example.youssefiibrahim.gallereo.presenter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import com.example.youssefiibrahim.gallereo.model.ImageStructure;
import com.example.youssefiibrahim.gallereo.model.ImageStructuresWrapper;
import com.example.youssefiibrahim.gallereo.model.PairWrapper;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;

public class DecodeImages extends AsyncTask<ArrayList<String>, Void, ArrayList<ImageStructuresWrapper>> {

    private Context context;
    private static final int WIDTH = 1000;
    private static final int BATCH_SIZE = 5;

    public DecodeImages(Context context) {
        this.context = context;
    }

    @Override
    protected ArrayList<ImageStructuresWrapper> doInBackground(ArrayList<String>... paths) {
        ArrayList<String> files = paths[0];

        ArrayList<ImageStructuresWrapper> ret = new ArrayList<>();
        ImageStructuresWrapper imageWrapper = new ImageStructuresWrapper();
        System.out.println("Started reading images " + files.size());
        Instant start = Instant.now();
        for (int i = 0; i < files.size(); i++) {
            String file = files.get(i);

            BitmapFactory.Options opts = new BitmapFactory.Options();
            // Get bitmap dimensions before reading...
            opts.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(file, opts);

            int width = opts.outWidth;
            int height = opts.outHeight;
            int minimum = Math.min(width, height);
            opts.inJustDecodeBounds = false; // This time it's for real!
            Bitmap b = BitmapFactory.decodeFile(file, opts);

            Bitmap resizedBitmap = null;

            if(b.getHeight() > WIDTH && b.getWidth() > WIDTH) {
//                System.out.println(b.getAllocationByteCount() + ", " + b.getByteCount() + ", " + b.getRowBytes());
//                System.out.println("HEIGHT = " + b.getHeight() + ", WIDTH = " + b.getWidth());
                resizedBitmap = Processing.getResizedBitmap(b, WIDTH);
//                System.out.println(resizedBitmap.getAllocationByteCount() + ", " + resizedBitmap.getByteCount() + ", " + resizedBitmap.getRowBytes());
//                System.out.println("HEIGHT = " + resizedBitmap.getHeight() + ", WIDTH = " + resizedBitmap.getWidth());
            }

            String imageEncoded = Processing.encodeToBase64(resizedBitmap == null ? b : resizedBitmap, Bitmap.CompressFormat.JPEG, 100);
            imageWrapper.add(new ImageStructure(file, imageEncoded));

            if ((i+1) % BATCH_SIZE == 0) {
                ret.add(imageWrapper);
                imageWrapper = new ImageStructuresWrapper();
            }
        }
        if (!imageWrapper.imageStructures.isEmpty()) {
            ret.add(imageWrapper);
        }

        Instant end = Instant.now();
        Duration timeElapsed = Duration.between(start, end);
        System.out.println("Time taken: "+ timeElapsed.toMillis() +" milliseconds");
        System.out.println("Finished reading images " + ret.size());
        return ret;
    }


    protected void onPostExecute(ArrayList<ImageStructuresWrapper> imageStructuresWrappers) {

        for (ImageStructuresWrapper wrapper : imageStructuresWrappers) {
            ProcessImagesAndSave thread = new ProcessImagesAndSave(this.context);
            thread.execute(wrapper);
            System.out.println("NEW THREAD STARTED");
        }
    }

}

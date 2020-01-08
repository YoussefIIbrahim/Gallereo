package com.example.youssefiibrahim.gallereo.presenter;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v4.content.CursorLoader;
import android.util.Log;

import com.example.youssefiibrahim.gallereo.model.ImageStructure;
import com.example.youssefiibrahim.gallereo.model.ImageStructuresWrapper;
import com.example.youssefiibrahim.gallereo.model.Response;
import com.example.youssefiibrahim.gallereo.model.ResponseWrapper;
import com.google.android.gms.common.util.Predicate;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.TreeSet;

public class DataRW {
    public static final String PATH = "labels.json";
    public static final Integer WIDTH = 300;
    private static final int BATCH_SIZE = 20;

    /**
     *
     * @param allPaths all images paths
     * @return the paths of images which are not processed
     */
    public static ArrayList<String> filterPaths(ArrayList<String> allPaths) {

        if (ResponseWrapper.singleton == null) {
            return allPaths;
        } else {
            TreeSet<String> appStoredPathsTree = new TreeSet<>();
            TreeSet<String> allPathsTree = new TreeSet<>(allPaths);
            ArrayList<String> filtered;
            ArrayList<String> toDelete;
            for (Response response : ResponseWrapper.singleton.responses) {
                appStoredPathsTree.add(response.id);
            }

            filtered = filterTwoLists(allPathsTree, appStoredPathsTree);
            toDelete = filterTwoLists(appStoredPathsTree, allPathsTree);
            TreeSet<String> toDeleteTree = new TreeSet<>(toDelete);
            deleteRedundantPaths(toDeleteTree);

            return filtered;
        }
    }

    private static ArrayList<String> filterTwoLists(TreeSet<String> in, TreeSet<String> out) {
        ArrayList<String> ret = new ArrayList<>();
        for (String s : in) {
            if (!out.contains(s)) {
                ret.add(s);
            }
        }
        return ret;
    }

    private static void deleteRedundantPaths(TreeSet<String> redundantPaths) {
        for (int i = 0 ; i < ResponseWrapper.singleton.responses.size() ; ++i) {
            if (redundantPaths.contains(ResponseWrapper.singleton.responses.get(i).id)) {
                ResponseWrapper.singleton.responses.remove(i);
                --i;
            }
        }
    }

    public static void loadFileIntoMemory(Context context) {
        String content = readFromFile(context);
        if (!content.isEmpty()) {
            ResponseWrapper.singleton = (ResponseWrapper) Processing.fromJson(content, ResponseWrapper.class);
            System.out.println("FILE READ " + content);
        }
    }

    public static void processAndSave(ArrayList<String> paths, Context context) throws IOException, InterruptedException {
        ArrayList<ImageStructuresWrapper> wrappers = getImages(paths);

        System.out.println("STARTED REQUESTING LABELS");
        ResponseWrapper responseWrapper = new ResponseWrapper();

        for (ImageStructuresWrapper imageStructuresWrapper : wrappers) {
            responseWrapper.add(communication.requestLabels(imageStructuresWrapper));
        }
        System.out.println("FINISHED REQUESTING LABELS");
        if (ResponseWrapper.singleton == null) {
            ResponseWrapper.singleton = new ResponseWrapper();
        }
        ResponseWrapper.singleton.add(responseWrapper);
        // now commit to device
        String json = Processing.toJson(ResponseWrapper.singleton);
        writeToFile(json, context);
    }

    public static void processAndSave2(ArrayList<ImageStructuresWrapper> wrappers, Context context) throws IOException, InterruptedException {
        System.out.println("STARTED REQUESTING LABELS");
        ResponseWrapper responseWrapper = new ResponseWrapper();

        for (ImageStructuresWrapper imageStructuresWrapper : wrappers) {

            responseWrapper.add(communication.requestLabels(imageStructuresWrapper));
        }

        System.out.println("FINISHED REQUESTING LABELS");
        if (ResponseWrapper.singleton == null) {
            ResponseWrapper.singleton = new ResponseWrapper();
        }
        ResponseWrapper.singleton.add(responseWrapper);
        // now commit to device
        String json = Processing.toJson(ResponseWrapper.singleton);
        writeToFile(json, context);
    }

    public static void writeToFile(String data, Context context) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput(PATH, Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
            System.out.println("LABELS.JSON has been written");
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    public static String readFromFile(Context context) {

        String ret = "";

        try {
            InputStream inputStream = context.openFileInput(PATH);

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        }
        catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }

        return ret;
    }

    public static ArrayList<ImageStructuresWrapper> getImages(ArrayList<String> files) throws FileNotFoundException {

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
            int largerSide = Math.max(width, height);
            opts.inJustDecodeBounds = false; // This time it's for real!
            int sampleSize = 1; // Calculate your sampleSize here
            opts.inSampleSize = sampleSize;
            Bitmap b = BitmapFactory.decodeFile(file, opts);

            Bitmap resizedBitmap = null;
            if(b.getHeight() > WIDTH && b.getWidth() > WIDTH) {
                resizedBitmap = Processing.getResizedBitmap(b, WIDTH);
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

//    public static ImageStructuresWrapper getImages(Activity activity) throws FileNotFoundException {
//        ArrayList<String> files = getImagesPath(activity);
//        return getImages(files);
//    }


    public static ArrayList<String> getImagesPath(Activity activity) {
        Uri uri;
        ArrayList<String> listOfAllImages = new ArrayList<String>();
        Cursor cursor;
        int column_index_data, column_index_folder_name;
        String PathOfImage = null;
        uri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        String[] projection = { MediaStore.MediaColumns.DATA,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME };

        cursor = activity.getContentResolver().query(uri, projection, null,
                null, null);

        column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        column_index_folder_name = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
        while (cursor.moveToNext()) {
            PathOfImage = cursor.getString(column_index_data);

            listOfAllImages.add(PathOfImage);
        }
        return listOfAllImages;
    }


}

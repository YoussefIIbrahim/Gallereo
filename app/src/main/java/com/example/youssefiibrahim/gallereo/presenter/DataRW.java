package com.example.youssefiibrahim.gallereo.presenter;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.TreeSet;

public class DataRW {
    public static final String PATH = "labels.json";
    public static final Integer WIDTH = 300;


    public static ArrayList<String> filterPaths(ArrayList<String> allPaths) {
        TreeSet<String> tree = new TreeSet<String>();
        ArrayList<String> filtered = new ArrayList<>();
        for (Response response : ResponseWrapper.singleton.responses) {
            tree.add(response.id);
        }

        for (String path : allPaths) {
            if (!tree.contains(path)) {
                filtered.add(path);
            }
        }
        return filtered;
    }

    public static void loadFileIntoMemory(Context context) {
        String content = readFromFile(context);
        if (!content.isEmpty()) {
            ResponseWrapper.singleton = (ResponseWrapper) Processing.fromJson(content, ResponseWrapper.class);
        }
    }

    public static void processAndSave(ArrayList<String> paths, Context context) throws IOException {
        ImageStructuresWrapper wrapper = getImages(paths);
        ResponseWrapper responseWrapper = communication.requestLabels(wrapper);
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

    public static ImageStructuresWrapper getImages(ArrayList<String> files) throws FileNotFoundException {

        ImageStructuresWrapper ret = new ImageStructuresWrapper();

        for (String file : files) {
            File f = new File(file);
            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
            Bitmap resizedBitmap = null;
            if(b.getHeight() > WIDTH && b.getWidth() > WIDTH) {
                resizedBitmap = Processing.getResizedBitmap(b, WIDTH);
                System.out.println("WIDTH = " + resizedBitmap.getWidth() + " , HEIGHT = " + resizedBitmap.getHeight());

            }

            String imageEncoded = Processing.encodeToBase64(resizedBitmap == null ? b : resizedBitmap, Bitmap.CompressFormat.JPEG, 100);
            ret.add(new ImageStructure(f.getAbsolutePath(), imageEncoded));
        }
        return ret;
    }

    public static ImageStructuresWrapper getImages(Activity activity) throws FileNotFoundException {
        ArrayList<String> files = getImagesPath(activity);
        return getImages(files);
    }


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

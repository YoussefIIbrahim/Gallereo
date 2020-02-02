package com.example.youssefiibrahim.gallereo.presenter;

import android.content.Context;
import android.os.AsyncTask;

import com.example.youssefiibrahim.gallereo.model.ImageStructuresWrapper;
import com.example.youssefiibrahim.gallereo.model.ResponseWrapper;

import java.io.IOException;

public class ProcessImagesAndSave extends AsyncTask<ImageStructuresWrapper, Void, String> {

    private Context context;
    public ProcessImagesAndSave(Context context) {
        this.context = context;
    }

    @Override
    protected String doInBackground(ImageStructuresWrapper... imageStructuresWrappers) {
        ImageStructuresWrapper imageStructuresWrapper = imageStructuresWrappers[0];

        try {

            ResponseWrapper responseWrapper = communication.requestLabels(imageStructuresWrapper);

            if (responseWrapper == null) {
                return null;
            }

            if (ResponseWrapper.singleton == null) {
                ResponseWrapper.singleton = new ResponseWrapper();
            }

            synchronized (ResponseWrapper.singleton) {
                if (responseWrapper != null) {
                    ResponseWrapper.singleton.add(responseWrapper);
                }
                String fileContent = DataRW.readFromFile(this.context);
                if (!fileContent.isEmpty()) {
                    ResponseWrapper instance = (ResponseWrapper)Processing.fromJson(fileContent, ResponseWrapper.class);
                    instance.add(responseWrapper);
                    String newFileContent = Processing.toJson(instance);
                    DataRW.writeToFile(newFileContent, this.context);
                } else {
                    String newFileContent = Processing.toJson(responseWrapper);
                    DataRW.writeToFile(newFileContent, this.context);
                }
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        imageStructuresWrapper = null;

        return null;
    }
}

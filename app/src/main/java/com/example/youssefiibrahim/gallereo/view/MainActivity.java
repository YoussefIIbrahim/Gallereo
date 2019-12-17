package com.example.youssefiibrahim.gallereo.view;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v7.widget.SearchView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import com.example.youssefiibrahim.gallereo.R;
import com.example.youssefiibrahim.gallereo.model.ImageStructuresWrapper;
import com.example.youssefiibrahim.gallereo.model.ResponseWrapper;
import com.example.youssefiibrahim.gallereo.presenter.DataRW;
import com.example.youssefiibrahim.gallereo.presenter.ProcessAndSaveThread;
import com.example.youssefiibrahim.gallereo.presenter.communication;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor>, MediaStorageAdapter.OnClickThumbListener {

    private static final String TAG = "MainActivity";


    private final static int READ_EXTERNAL_STORAGE_PERMISSION_RESULT = 0;
    private final static int MEDIASTORE_LOADER_ID = 0;
    private RecyclerView memberRecyclerView;
    private MediaStorageAdapter memberMediaStoreAdapter;
    private Toolbar toolbar;
    private ArrayList<String> mImageUrls = new ArrayList<>();
    private ArrayList<String> allPaths;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar)findViewById(R.id.toolbar1);

        toolbar.setTitleTextColor(getResources().getColor(R.color.toolbarColor));

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        memberRecyclerView = (RecyclerView) findViewById(R.id.thumbnailRecyclerView);


        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
        memberRecyclerView.setLayoutManager(gridLayoutManager);

        try {
            prepareData();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        memberMediaStoreAdapter = new MediaStorageAdapter(this, null, false, allPaths);
        memberRecyclerView.setAdapter(memberMediaStoreAdapter);
        checkReadExternalStoragePermission();
    }


    private void prepareData() throws IOException {
        DataRW.loadFileIntoMemory(this);

        if (allPaths == null && isPermissionGranted()) {
            allPaths = DataRW.getImagesPath(this);
        }
        if (ResponseWrapper.singleton != null) {
            ArrayList<String> imagesToProcess = DataRW.getImagesPath(this); //DataRW.filterPaths(allPaths);
            ImageStructuresWrapper wrapper = DataRW.getImages(imagesToProcess);
            System.out.println("imagesToProcess = " + imagesToProcess);
            ProcessAndSaveThread thread = new ProcessAndSaveThread(imagesToProcess, this);
            thread.start();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_search_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setQueryHint("Type here to search");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

//                Context context = getApplicationContext();
//                int duration = Toast.LENGTH_SHORT;

//                Toast toast = Toast.makeText(context, query, duration);
//                toast.show();
//                memberRecyclerView.invalidate();
//                memberRecyclerView.setAdapter(null);
//                initImageBitmaps();
                memberMediaStoreAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
//                Context context = getApplicationContext();
//                int duration = Toast.LENGTH_SHORT;
//
//                Toast toast = Toast.makeText(context, newText, duration);
//                toast.show();
                memberMediaStoreAdapter.getFilter().filter(newText);

                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case READ_EXTERNAL_STORAGE_PERMISSION_RESULT:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //Call cursor loader
                    Toast.makeText(this,"Permission Granted", Toast.LENGTH_SHORT).show();
                    getSupportLoaderManager().initLoader(MEDIASTORE_LOADER_ID, null, this);
                    this.allPaths = DataRW.getImagesPath(this);
                    try {
                        DataRW.processAndSave(this.allPaths, this);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        }
    }

    private boolean isPermissionGranted() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_GRANTED;
    }

    private void checkReadExternalStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) ==
                    PackageManager.PERMISSION_GRANTED) {
                getSupportLoaderManager().initLoader(MEDIASTORE_LOADER_ID, null, this);

            } else {
                if(shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    Toast.makeText(this, "The app needs to view the images on the device.", Toast.LENGTH_SHORT).show();
                }
                requestPermissions(new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},
                READ_EXTERNAL_STORAGE_PERMISSION_RESULT);
            }
        } else {
            getSupportLoaderManager().initLoader(MEDIASTORE_LOADER_ID, null, this);

        }
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int i, @Nullable Bundle bundle) {
        String[] projection = {
                MediaStore.Files.FileColumns._ID,
                MediaStore.Files.FileColumns.DATE_ADDED,
                MediaStore.Files.FileColumns.DATA,
                MediaStore.Files.FileColumns.MEDIA_TYPE
        };
        String selection = MediaStore.Files.FileColumns.MEDIA_TYPE + '='
                + MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE
                + " OR "
                + MediaStore.Files.FileColumns.MEDIA_TYPE + '='
                + MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO;
        return new CursorLoader(
                this,
                MediaStore.Files.getContentUri("external"),
                projection,
                selection,
                null,
                MediaStore.Files.FileColumns.DATE_ADDED + " DESC"
        );
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
        memberMediaStoreAdapter.changeCursor(cursor);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        memberMediaStoreAdapter.changeCursor(null);
    }

    @Override
    public void OnClickImage(Uri imageUri) {
        // Toast.makeText(MediaThumbMainActivity.this, "Image uri = " + imageUri.toString(), Toast.LENGTH_SHORT).show();
        Intent fullScreenIntent = new Intent(this, FullScreenImageActivity.class);
        fullScreenIntent.setData(imageUri);
        startActivity(fullScreenIntent);
    }

    @Override
    public void OnClickVideo(Uri videoUri) {
        Intent videoPlayIntent = new Intent(this, VideoPlayActivity.class);
        videoPlayIntent.setData(videoUri);
        startActivity(videoPlayIntent);
    }

    private void initImageBitmaps(){
        Log.d(TAG, "initImageBitmaps: preparing bitmaps.");

        mImageUrls.add("https://c1.staticflickr.com/5/4636/25316407448_de5fbf183d_o.jpg");

        mImageUrls.add("https://i.redd.it/tpsnoz5bzo501.jpg");

        mImageUrls.add("https://i.redd.it/qn7f9oqu7o501.jpg");

        mImageUrls.add("https://i.redd.it/j6myfqglup501.jpg");

        mImageUrls.add("https://i.redd.it/0h2gm1ix6p501.jpg");

        mImageUrls.add("https://i.redd.it/k98uzl68eh501.jpg");

        mImageUrls.add("https://i.redd.it/glin0nwndo501.jpg");

        mImageUrls.add("https://i.redd.it/obx4zydshg601.jpg");

        mImageUrls.add("https://i.imgur.com/ZcLLrkY.jpg");

        initRecyclerView();
    }

    private void initRecyclerView(){
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
        memberRecyclerView.setLayoutManager(gridLayoutManager);

        memberMediaStoreAdapter = new MediaStorageAdapter(this, null, true, DataRW.getImagesPath(this));
        memberRecyclerView.setAdapter(memberMediaStoreAdapter);

    }
}

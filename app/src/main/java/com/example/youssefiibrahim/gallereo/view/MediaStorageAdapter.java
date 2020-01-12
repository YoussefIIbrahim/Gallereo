package com.example.youssefiibrahim.gallereo.view;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.module.AppGlideModule;
import com.bumptech.glide.request.RequestOptions;
import com.example.youssefiibrahim.gallereo.R;
import com.example.youssefiibrahim.gallereo.model.PairWrapper;
import com.example.youssefiibrahim.gallereo.presenter.CoreAlgorithms;
import com.example.youssefiibrahim.gallereo.presenter.DataRW;
import com.example.youssefiibrahim.gallereo.presenter.SendHttpToHandler;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class MediaStorageAdapter extends RecyclerView.Adapter<MediaStorageAdapter.ViewHolder> implements Filterable {

    private static final int PRELOAD_WIDTH = 150;
    private Cursor memberMediaStoreCursor;
    private final Activity memberActivity;
    private OnClickThumbListener mOnClickThumbListener;
    private ArrayList<String> mImages = new ArrayList<>();
    private ArrayList<String> allPaths;
    public Boolean searchMode;
    private int contentIterator;
    private String TAG = "FROM MEDIASTORAGE ADAPTER: ";

    public Cursor getMemberMediaStoreCursor() {
        return memberMediaStoreCursor;
    }

    public void setMemberMediaStoreCursor(Cursor memberMediaStoreCursor) {
        this.memberMediaStoreCursor = memberMediaStoreCursor;
    }

    public interface OnClickThumbListener {
        void OnClickImage(Uri imageUri);

        void OnClickVideo(Uri videoUri);
    }

    public MediaStorageAdapter(Activity memberActivity, ArrayList<String> images, Boolean searchMode, ArrayList<String> allPaths) {
        this.memberActivity = memberActivity;
        this.mOnClickThumbListener = (OnClickThumbListener) memberActivity;
        this.mImages = images;
        this.searchMode = searchMode;
        this.allPaths = allPaths;
        this.contentIterator = 0;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.single_image_thumbnail, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        if (searchMode) {
            RequestOptions myOptions = new RequestOptions()
                    .fitCenter()
                    .override(PRELOAD_WIDTH, PRELOAD_WIDTH);
            Glide.with(memberActivity)
                    .asBitmap()
                    .load(mImages.get(i))
                    .apply(myOptions)
                    .into(viewHolder.getMemberImageView());
            contentIterator += 1;
        } else {
            RequestOptions myOptions = new RequestOptions()
                    .fitCenter()
                    .override(PRELOAD_WIDTH, PRELOAD_WIDTH);
            Glide.with(memberActivity)
                    .asBitmap()
                    .load(getUriFromMediaStore(i))
                    .apply(myOptions)
                    .into(viewHolder.getMemberImageView());
        }
    }

    @Override
    public int getItemCount() {
        if (searchMode && mImages != null) {
            return mImages.size();
        } else {
            return (memberMediaStoreCursor == null ? 0 : memberMediaStoreCursor.getCount());
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final ImageView memberImageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            memberImageView = (ImageView) itemView.findViewById(R.id.MediaStoreimageView);
            memberImageView.setOnClickListener(this);
        }

        public ImageView getMemberImageView() {
            return memberImageView;
        }

        @Override
        public void onClick(View v) {
            getOnClickUri(getAdapterPosition());
        }
    }

    private Cursor swapCursor(Cursor cursor) {
        if (memberMediaStoreCursor == cursor) {
            return null;
        }
        Cursor oldCursor = memberMediaStoreCursor;
        this.memberMediaStoreCursor = cursor;
        if (cursor != null) {
            this.notifyDataSetChanged();
        }
        return oldCursor;
    }

    public void changeCursor(Cursor cursor) {
        Cursor oldCursor = swapCursor(cursor);
        if (oldCursor != null) {
            oldCursor.close();
        }
    }

    private Bitmap getBitmapMediaStore(int position) {
        int idIndex = memberMediaStoreCursor.getColumnIndex(MediaStore.Files.FileColumns._ID);
        int mediaTypeIndex = memberMediaStoreCursor.getColumnIndex(MediaStore.Files.FileColumns.MEDIA_TYPE);

        memberMediaStoreCursor.moveToPosition(position);

        //Check if video or image
        switch (memberMediaStoreCursor.getInt(mediaTypeIndex)) {
            case MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE:
                return MediaStore.Images.Thumbnails.getThumbnail(
                        memberActivity.getContentResolver(),
                        memberMediaStoreCursor.getLong(idIndex),
                        MediaStore.Images.Thumbnails.MINI_KIND,
                        null
                );

            case MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO:
                return MediaStore.Video.Thumbnails.getThumbnail(
                        memberActivity.getContentResolver(),
                        memberMediaStoreCursor.getLong(idIndex),
                        MediaStore.Video.Thumbnails.MINI_KIND,
                        null
                );

            default:
                return null;
        }
    }

    public Uri getUriFromMediaStore(int position) {
        int dataIndex = memberMediaStoreCursor.getColumnIndex(MediaStore.Files.FileColumns.DATA);
        memberMediaStoreCursor.moveToPosition(position);

        String dataString = memberMediaStoreCursor.getString(dataIndex);
        Uri mediaUri = Uri.parse("file://" + dataString);
        return mediaUri;
    }

    public void getOnClickUri(int position) {
        if (searchMode) {
            String authorities = memberActivity.getPackageName() + ".fileprovider";
            Uri mediaUri = FileProvider.getUriForFile(memberActivity, authorities, new File(mImages.get(position)));
            mOnClickThumbListener.OnClickImage(mediaUri);
        } else {
            int mediaTypeIndex = memberMediaStoreCursor.getColumnIndex(MediaStore.Files.FileColumns.MEDIA_TYPE);
            int dataIndex = memberMediaStoreCursor.getColumnIndex(MediaStore.Files.FileColumns.DATA);

            memberMediaStoreCursor.moveToPosition(position);
            String dataString = memberMediaStoreCursor.getString(dataIndex);
            String authorities = memberActivity.getPackageName() + ".fileprovider";
            Uri mediaUri = FileProvider.getUriForFile(memberActivity, authorities, new File(dataString));

            switch (memberMediaStoreCursor.getInt(mediaTypeIndex)) {
                case MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE:
                    mOnClickThumbListener.OnClickImage(mediaUri);
                    break;
                case MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO:
                    mOnClickThumbListener.OnClickVideo(mediaUri);
                    break;
                default:
            }
        }

    }

    @Override
    public Filter getFilter() {
        return exampleFilter;
    }

    public void handler(ArrayList<String> paths) {
        // SHOW IMAGES
        mImages = paths;
        notifyDataSetChanged();
        System.out.println("PATHS = " + paths);

    }

    private Filter exampleFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            if (constraint == null || constraint.length() == 0) {
                searchMode = false;
                notifyDataSetChanged();
            } else {
                searchMode = true;
                contentIterator = 0;
                new SendHttpToHandler(MediaStorageAdapter.this).execute(constraint.toString());
            }
            System.out.println("NO HTTP REQUEST: " + searchMode);
            FilterResults results = new FilterResults();
            results.values = mImages;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
//            new SendHttpToHandler(MediaStorageAdapter.this).execute(constraint.toString());
//            initImageBitmaps();
//            searchMode = true;
//            mImages.clear();
//            mImages.addAll((List) results.values);
//            notifyDataSetChanged();
        }
    };


    private void initImageBitmaps() {
        mImages = new ArrayList<>();

        mImages.add("/storage/emulated/0/WhatsApp/Media/WhatsApp Images/IMG-20191108-WA0020.jpg");
        mImages.add("/storage/emulated/0/WhatsApp/Media/WhatsApp Images/IMG-20191108-WA0020.jpg");
        mImages.add("/storage/emulated/0/WhatsApp/Media/WhatsApp Images/IMG-20191108-WA0020.jpg");
        mImages.add("/storage/emulated/0/DCIM/Camera/20191216_215937.jpg");

//        mImages.add("https://i.redd.it/tpsnoz5bzo501.jpg");
//
//        mImages.add("https://i.redd.it/qn7f9oqu7o501.jpg");
//
//        mImages.add("https://i.redd.it/j6myfqglup501.jpg");
//
//        mImages.add("https://i.redd.it/0h2gm1ix6p501.jpg");
//
//        mImages.add("https://i.redd.it/k98uzl68eh501.jpg");
//
//        mImages.add("https://i.redd.it/glin0nwndo501.jpg");
//
//        mImages.add("https://i.redd.it/obx4zydshg601.jpg");
//
//        mImages.add("https://i.imgur.com/ZcLLrkY.jpg");

    }
}

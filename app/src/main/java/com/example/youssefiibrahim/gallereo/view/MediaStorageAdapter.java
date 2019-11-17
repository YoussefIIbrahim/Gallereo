package com.example.youssefiibrahim.gallereo.view;

import android.app.Activity;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.youssefiibrahim.gallereo.R;

public class MediaStorageAdapter extends RecyclerView.Adapter<MediaStorageAdapter.ViewHolder> {

    private Cursor memberMediaStoreCursor;
    private final Activity memberActivity;
    private OnClickThumbListener mOnClickThumbListener;

    public interface OnClickThumbListener {
        void OnClickImage(Uri imageUri);
        void OnClickVideo(Uri videoUri);
    }

    public MediaStorageAdapter(Activity memberActivity) {
        this.memberActivity = memberActivity;
        this.mOnClickThumbListener = (OnClickThumbListener)memberActivity;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.full_screen_image_mode, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
//        Bitmap bitmap = getBitmapMediaStore(i);
//        if (bitmap != null) {
//            viewHolder.getMemberImageView().setImageBitmap(bitmap);
//        }
        Glide.with(memberActivity)
                .load(getUriFromMediaStore(i))
                .centerCrop()
                .override(96, 96)
                .into(viewHolder.getMemberImageView());
    }

    @Override
    public int getItemCount() {
        return (memberMediaStoreCursor == null ? 0 : memberMediaStoreCursor.getCount());
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final ImageView memberImageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            memberImageView = (ImageView) itemView.findViewById(R.id.MediaStoreimageView);
        }

        public ImageView getMemberImageView() {
            return memberImageView;
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
        switch(memberMediaStoreCursor.getInt(mediaTypeIndex)) {
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

    private Uri getUriFromMediaStore(int position) {
        int dataIndex = memberMediaStoreCursor.getColumnIndex(MediaStore.Files.FileColumns.DATA);
        memberMediaStoreCursor.moveToPosition(position);

        String dataString = memberMediaStoreCursor.getString(dataIndex);
        Uri mediaUri = Uri.parse("file://" + dataString);
        return mediaUri;
    }
}

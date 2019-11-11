package com.example.youssefiibrahim.gallereo.view;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.youssefiibrahim.gallereo.R;

public class MediaStorageAdapter extends RecyclerView.Adapter<MediaStorageAdapter.ViewHolder> {

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.full_screen_image_mode, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

    }

    @Override
    public int getItemCount() {
        return 0;
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
}

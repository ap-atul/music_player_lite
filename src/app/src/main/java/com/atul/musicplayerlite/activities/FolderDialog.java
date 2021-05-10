package com.atul.musicplayerlite.activities;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.atul.musicplayerlite.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;

public class FolderDialog extends BottomSheetDialog {

    public FolderDialog(@NonNull Context context) {
        super(context);
        setContentView(R.layout.dialog_queue);

    }


    private class FolderAdapter extends RecyclerView.Adapter<FolderAdapter.MyViewHolder>{

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_folder, parent, false);
            return new FolderAdapter.MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        }

        @Override
        public int getItemCount() {
            return 0;
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            public MyViewHolder(@NonNull View itemView) {
                super(itemView);
            }
        }
    }
}

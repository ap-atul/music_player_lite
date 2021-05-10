package com.atul.musicplayerlite.activities;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.atul.musicplayerlite.MPPreferences;
import com.atul.musicplayerlite.R;
import com.atul.musicplayerlite.model.Folder;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.List;
import java.util.Set;

public class FolderDialog extends BottomSheetDialog {

    public FolderDialog(@NonNull Context context, List<Folder> folderList) {
        super(context);
        setContentView(R.layout.dialog_folder);

        RecyclerView recyclerView = findViewById(R.id.folder_layout);
        FolderAdapter adapter = new FolderAdapter(folderList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
    }

    private class FolderAdapter extends RecyclerView.Adapter<FolderAdapter.MyViewHolder> {
        private final List<Folder> folderList;
        private final Set<String> exclusionList;

        public FolderAdapter(List<Folder> folderList) {
            this.folderList = folderList;
            exclusionList = MPPreferences.getExcludedFolders(getContext());
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_folder, parent, false);
            return new FolderAdapter.MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            if (exclusionList.contains(folderList.get(position).name)) {
                holder.folderSelect.setChecked(true);
            }

            holder.folderName.setText(folderList.get(position).name);
        }

        @Override
        public int getItemCount() {
            return folderList.size();
        }

        class MyViewHolder extends RecyclerView.ViewHolder {

            private final CheckBox folderSelect;
            private final TextView folderName;

            public MyViewHolder(@NonNull View itemView) {
                super(itemView);

                folderSelect = itemView.findViewById(R.id.control_select_folder);
                folderName = itemView.findViewById(R.id.folder_name);

                folderSelect.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    if (isChecked) {
                        exclusionList.add(folderList.get(getAdapterPosition()).name);
                    } else {
                        exclusionList.remove(folderList.get(getAdapterPosition()).name);
                    }

                    MPPreferences.storeExcludedFolders(getContext(), exclusionList);
                });
            }
        }
    }
}

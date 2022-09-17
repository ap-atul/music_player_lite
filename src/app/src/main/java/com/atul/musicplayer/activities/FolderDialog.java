package com.atul.musicplayer.activities;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.atul.musicplayer.App;
import com.atul.musicplayer.MPPreferences;
import com.atul.musicplayer.R;
import com.atul.musicplayer.model.Folder;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.List;

public class FolderDialog extends BottomSheetDialog {

    private final List<String> exclusionFolders;

    public FolderDialog(@NonNull Context context, List<Folder> folderList) {
        super(context);
        setContentView(R.layout.dialog_folder);

        exclusionFolders = MPPreferences.getExcludedFolders(context);

        RecyclerView recyclerView = findViewById(R.id.folder_layout);
        FolderAdapter adapter = new FolderAdapter(folderList, exclusionFolders, new FolderAdapter.FolderExclusionListListener() {
            @Override
            public void add(String name) {
                addToExcluded(name);
            }

            @Override
            public void remove(String name) {
                removeFromExcluded(name);
            }
        });


        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
    }

    private void addToExcluded(String name) {
        if (!exclusionFolders.contains(name)) {
            exclusionFolders.add(name);
        }
        MPPreferences.storeExcludedFolders(App.getContext(), exclusionFolders);
    }

    private void removeFromExcluded(String name) {
        exclusionFolders.remove(name);
        MPPreferences.storeExcludedFolders(App.getContext(), exclusionFolders);
    }

    static class FolderAdapter extends RecyclerView.Adapter<FolderAdapter.MyViewHolder> {
        private final List<Folder> folderList;
        private final List<String> exclusionList;
        private final FolderExclusionListListener listener;

        public FolderAdapter(List<Folder> folderList, List<String> exclusionList, FolderExclusionListListener listener) {
            this.folderList = folderList;
            this.exclusionList = exclusionList;
            this.listener = listener;
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

            holder.folderSelect.setText(folderList.get(position).name);
        }

        @Override
        public int getItemCount() {
            return folderList.size();
        }

        public interface FolderExclusionListListener {
            void add(String name);

            void remove(String name);
        }

        class MyViewHolder extends RecyclerView.ViewHolder {

            private final CheckBox folderSelect;

            public MyViewHolder(@NonNull View itemView) {
                super(itemView);

                folderSelect = itemView.findViewById(R.id.control_select_folder);

                folderSelect.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    if (isChecked) {
                        listener.add(folderList.get(getAdapterPosition()).name);
                    } else {
                        listener.remove(folderList.get(getAdapterPosition()).name);
                    }
                });
            }
        }
    }
}

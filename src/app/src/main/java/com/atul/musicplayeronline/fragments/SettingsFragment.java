package com.atul.musicplayeronline.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.atul.musicplayeronline.MPConstants;
import com.atul.musicplayeronline.MPPreferences;
import com.atul.musicplayeronline.R;
import com.atul.musicplayeronline.activities.FolderDialog;
import com.atul.musicplayeronline.adapter.AccentAdapter;
import com.atul.musicplayeronline.helper.ThemeHelper;
import com.atul.musicplayeronline.model.Folder;
import com.atul.musicplayeronline.viewmodel.MainViewModel;
import com.atul.musicplayeronline.viewmodel.MainViewModelFactory;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.ArrayList;
import java.util.List;

public class SettingsFragment extends Fragment implements View.OnClickListener {

    private MainViewModel viewModel;
    private RecyclerView accentView;
    private boolean state;
    private LinearLayout chipLayout;
    private ImageView currentThemeMode;

    private MaterialToolbar toolbar;
    private FolderDialog folderDialog;

    public SettingsFragment() {
    }

    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity(), new MainViewModelFactory(requireActivity())).get(MainViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        SwitchMaterial switchMaterial = view.findViewById(R.id.album_switch);
        accentView = view.findViewById(R.id.accent_view);
        chipLayout = view.findViewById(R.id.chip_layout);
        currentThemeMode = view.findViewById(R.id.current_theme_mode);
        toolbar = view.findViewById(R.id.toolbar);

        LinearLayout accentOption = view.findViewById(R.id.accent_option);
        LinearLayout albumOption = view.findViewById(R.id.album_options);
        LinearLayout themeModeOption = view.findViewById(R.id.theme_mode_option);
        LinearLayout folderOption = view.findViewById(R.id.folder_options);
        LinearLayout refreshOption = view.findViewById(R.id.refresh_options);

        state = MPPreferences.getAlbumRequest(requireActivity().getApplicationContext());
        switchMaterial.setChecked(state);
        setCurrentThemeMode();

        accentView.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL, false));
        accentView.setAdapter(new AccentAdapter(getActivity()));

        accentOption.setOnClickListener(this);
        albumOption.setOnClickListener(this);
        switchMaterial.setOnClickListener(this);
        themeModeOption.setOnClickListener(this);
        folderOption.setOnClickListener(this);
        refreshOption.setOnClickListener(this);

        view.findViewById(R.id.night_chip).setOnClickListener(this);
        view.findViewById(R.id.light_chip).setOnClickListener(this);
        view.findViewById(R.id.auto_chip).setOnClickListener(this);

        setUpOptions();

        return view;

    }


    private void setUpOptions() {
        toolbar.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();

            if (id == R.id.github) {
                startActivity(new Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse(MPConstants.GITHUB_REPO_URL)
                ));
                return true;
            }

            return false;
        });
        toolbar.setNavigationOnClickListener(v -> requireActivity().finish());
    }

    private void setCurrentThemeMode() {
        int mode = MPPreferences.getThemeMode(requireActivity().getApplicationContext());

        if (mode == AppCompatDelegate.MODE_NIGHT_NO)
            currentThemeMode.setImageResource(R.drawable.ic_theme_mode_light);

        else if (mode == AppCompatDelegate.MODE_NIGHT_YES)
            currentThemeMode.setImageResource(R.drawable.ic_theme_mode_night);

        else
            currentThemeMode.setImageResource(R.drawable.ic_theme_mode_auto);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == R.id.accent_option) {
            int visibility = (accentView.getVisibility() == View.VISIBLE) ? View.GONE : View.VISIBLE;
            accentView.setVisibility(visibility);
        }

        else if (id == R.id.album_options)
            setAlbumRequest();

        else if (id == R.id.album_switch)
            setAlbumRequest();

        else if (id == R.id.theme_mode_option) {
            int mode = chipLayout.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE;
            chipLayout.setVisibility(mode);
        }

        else if (id == R.id.night_chip)
            selectTheme(AppCompatDelegate.MODE_NIGHT_YES);

        else if (id == R.id.light_chip)
            selectTheme(AppCompatDelegate.MODE_NIGHT_NO);

        else if (id == R.id.auto_chip)
            selectTheme(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);

        else if (id == R.id.folder_options)
            showFolderSelectionDialog();

        else if (id == R.id.refresh_options) {
            Toast.makeText(requireActivity(), "Looking ...", Toast.LENGTH_SHORT).show();
            ThemeHelper.applySettings(requireActivity());
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (folderDialog != null)
            folderDialog.dismiss();
    }

    private void showFolderSelectionDialog() {
        List<Folder> folderList = new ArrayList<>(viewModel.getFolders(false));
        folderDialog = new FolderDialog(requireActivity(), folderList);
        folderDialog.show();

        folderDialog.setOnDismissListener(dialog ->
                ThemeHelper.applySettings(requireActivity())
        );
    }

    private void selectTheme(int theme) {
        AppCompatDelegate.setDefaultNightMode(theme);
        MPPreferences.storeThemeMode(requireActivity().getApplicationContext(), theme);
    }

    private void setAlbumRequest() {
        MPPreferences.storeAlbumRequest(requireActivity().getApplicationContext(), (!state));
        ThemeHelper.applySettings(getActivity());
    }
}
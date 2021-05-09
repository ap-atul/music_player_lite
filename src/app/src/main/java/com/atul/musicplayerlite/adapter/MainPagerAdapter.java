package com.atul.musicplayerlite.adapter;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.atul.musicplayerlite.fragments.AlbumsFragment;
import com.atul.musicplayerlite.fragments.ArtistsFragment;
import com.atul.musicplayerlite.fragments.SettingsFragment;
import com.atul.musicplayerlite.fragments.SongsFragment;
import com.atul.musicplayerlite.listener.MusicSelectListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class MainPagerAdapter extends FragmentPagerAdapter {

    List<Fragment> fragments = new ArrayList<>();
    private final MusicSelectListener selectListener;

    public MainPagerAdapter(FragmentManager fm, MusicSelectListener selectListener) {
        super(fm);
        this.selectListener = selectListener;

        setFragments();
    }

    public void setFragments() {
        fragments.add(SongsFragment.newInstance(selectListener));
        fragments.add(ArtistsFragment.newInstance());
        fragments.add(AlbumsFragment.newInstance());
        fragments.add(SettingsFragment.newInstance());
    }

    @NotNull
    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return null;
    }

    @Override
    public int getCount() {
        return fragments.size();
    }
}
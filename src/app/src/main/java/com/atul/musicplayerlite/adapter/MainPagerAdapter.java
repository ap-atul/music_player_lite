package com.atul.musicplayerlite.adapter;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.atul.musicplayerlite.R;
import com.atul.musicplayerlite.fragments.AlbumsFragment;
import com.atul.musicplayerlite.fragments.ArtistsFragment;
import com.atul.musicplayerlite.fragments.FoldersFragment;
import com.atul.musicplayerlite.fragments.SettingsFragment;
import com.atul.musicplayerlite.fragments.SongsFragment;
import com.atul.musicplayerlite.listener.PlayerControlListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class MainPagerAdapter extends FragmentPagerAdapter {

    @StringRes
    private static final int[] TAB_TITLES = new int[]{
            R.string.tab_one, R.string.tab_two, R.string.tab_three,
            R.string.tab_four, R.string.tab_five
    };
    List<Fragment> fragments = new ArrayList<>();

    public void setFragments(PlayerControlListener listener) {
        fragments.add(ArtistsFragment.newInstance());
        fragments.add(AlbumsFragment.newInstance());
        fragments.add(SongsFragment.newInstance(listener));
        fragments.add(FoldersFragment.newInstance());
        fragments.add(SettingsFragment.newInstance());
    }

    private final Context mContext;

    public MainPagerAdapter(Context context, FragmentManager fm, PlayerControlListener listener) {
        super(fm);
        mContext = context;
        setFragments(listener);
    }

    @NotNull
    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mContext.getResources().getString(TAB_TITLES[position]);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }
}
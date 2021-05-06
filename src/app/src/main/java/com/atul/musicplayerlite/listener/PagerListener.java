package com.atul.musicplayerlite.listener;

import androidx.fragment.app.Fragment;

public interface PagerListener {
    void setFragment(Fragment toRemove, Fragment toAdd, int position);
}

package com.atul.musicplayerlite.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class Network implements Parcelable {
    public List<Music> musicList;
    public List<Album> albumList;

    public Network(){}

    protected Network(Parcel in) {
        musicList = in.createTypedArrayList(Music.CREATOR);
        albumList = in.createTypedArrayList(Album.CREATOR);
    }

    public static final Creator<Network> CREATOR = new Creator<Network>() {
        @Override
        public Network createFromParcel(Parcel in) {
            return new Network(in);
        }

        @Override
        public Network[] newArray(int size) {
            return new Network[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(musicList);
        dest.writeTypedList(albumList);
    }

    @NotNull
    @Override
    public String toString() {
        return "Network{" +
                "musicList=" + musicList +
                ", albumList=" + albumList +
                '}';
    }
}

package com.atul.musicplayer.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.atul.musicplayer.helper.ListHelper;


import java.util.ArrayList;
import java.util.List;

public class Album implements Parcelable {
    public static final Creator<Album> CREATOR = new Creator<Album>() {
        @Override
        public Album createFromParcel(Parcel in) {
            return new Album(in);
        }

        @Override
        public Album[] newArray(int size) {
            return new Album[size];
        }
    };
    public String title;
    public String year;
    public String artist;
    public Long duration;
    public List<Music> music;

    public Album(String artist, String title, String year, Long duration, List<Music> music) {
        this.artist = ListHelper.ifNull(artist);
        this.title = ListHelper.ifNull(title);
        this.year = ListHelper.ifNull(year);
        this.duration = duration;
        this.music = music;
    }

    protected Album(Parcel in) {
        title = in.readString();
        year = in.readString();
        artist = in.readString();
        music = new ArrayList<>();
        in.readTypedList(music, Music.CREATOR);

        if (in.readByte() == 0) {
            duration = null;
        } else {
            duration = in.readLong();
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(year);
        dest.writeString(artist);
        dest.writeTypedList(music);

        if (duration == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(duration);
        }
    }

    @NonNull
    @Override
    public String toString() {
        return "Album{" +
                "title='" + title + '\'' +
                ", year='" + year + '\'' +
                ", artist='" + artist + '\'' +
                ", duration=" + duration +
                ", music=" + music +
                '}';
    }
}

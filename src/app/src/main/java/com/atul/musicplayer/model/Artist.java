package com.atul.musicplayer.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.atul.musicplayer.helper.ListHelper;

import java.util.List;

public class Artist implements Parcelable {
    public static final Creator<Artist> CREATOR = new Creator<Artist>() {
        @Override
        public Artist createFromParcel(Parcel in) {
            return new Artist(in);
        }

        @Override
        public Artist[] newArray(int size) {
            return new Artist[size];
        }
    };
    public String name;
    public List<Album> albums;
    public int songCount;
    public int albumCount;

    public Artist(String name, List<Album> albums, int songCount, int albumCount) {
        this.name = ListHelper.ifNull(name);
        this.albums = albums;
        this.songCount = songCount;
        this.albumCount = albumCount;
    }

    protected Artist(Parcel in) {
        name = in.readString();
        albums = in.createTypedArrayList(Album.CREATOR);
        songCount = in.readInt();
        albumCount = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeTypedList(albums);
        dest.writeInt(songCount);
        dest.writeInt(albumCount);
    }
}

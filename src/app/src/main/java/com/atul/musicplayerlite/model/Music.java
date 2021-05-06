package com.atul.musicplayerlite.model;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import org.jetbrains.annotations.NotNull;

public class Music implements Parcelable {
    public static final Creator<Music> CREATOR = new Creator<Music>() {
        @Override
        public Music createFromParcel(Parcel in) {
            return new Music(in);
        }

        @Override
        public Music[] newArray(int size) {
            return new Music[size];
        }
    };
    public String artist;
    public String title;
    public String displayName;
    public String album;
    public String relativePath;
    public Uri albumArt;
    public int year;
    public int track;
    public int startFrom;
    public int dateAdded;
    public long id;
    public long duration;
    public long albumId;

    public Music(String artist, String title, String displayName, String album, String relativePath,
                 int year, int track, int startFrom, int dateAdded,
                 long id, long duration, long albumId,
                 Uri albumArt) {
        this.artist = artist;
        this.title = title;
        this.displayName = displayName;
        this.album = album;
        this.relativePath = relativePath;
        this.year = year;
        this.track = track;
        this.startFrom = startFrom;
        this.dateAdded = dateAdded;
        this.id = id;
        this.duration = duration;
        this.albumId = albumId;
        this.albumArt = albumArt;
    }

    protected Music(Parcel in) {
        artist = in.readString();
        title = in.readString();
        displayName = in.readString();
        album = in.readString();
        relativePath = in.readString();
        albumArt = in.readParcelable(Uri.class.getClassLoader());
        year = in.readInt();
        track = in.readInt();
        startFrom = in.readInt();
        dateAdded = in.readInt();
        id = in.readLong();
        duration = in.readLong();
        albumId = in.readLong();
    }

    @NotNull
    @Override
    public String toString() {
        return "Music{" +
                "artist='" + artist + '\'' +
                ", title='" + title + '\'' +
                ", displayName='" + displayName + '\'' +
                ", album='" + album + '\'' +
                ", relativePath='" + relativePath + '\'' +
                ", year=" + year +
                ", track=" + track +
                ", startFrom=" + startFrom +
                ", dateAdded=" + dateAdded +
                ", id=" + id +
                ", duration=" + duration +
                ", albumId=" + albumId +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(artist);
        dest.writeString(title);
        dest.writeString(displayName);
        dest.writeString(album);
        dest.writeString(relativePath);
        dest.writeParcelable(albumArt, flags);
        dest.writeInt(year);
        dest.writeInt(track);
        dest.writeInt(startFrom);
        dest.writeInt(dateAdded);
        dest.writeLong(id);
        dest.writeLong(duration);
        dest.writeLong(albumId);
    }
}

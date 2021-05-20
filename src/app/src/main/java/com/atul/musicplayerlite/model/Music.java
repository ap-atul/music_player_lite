package com.atul.musicplayerlite.model;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import com.atul.musicplayerlite.helper.ListHelper;

import org.jetbrains.annotations.NotNull;

import java.util.Calendar;
import java.util.Random;

public class Music implements Parcelable {

    public String artist;
    public String title;
    public String displayName;
    public String album;
    public String relativePath;
    public Uri albumArt;
    public int year;
    public int track;
    public int startFrom;
    public long dateAdded;
    public long id;
    public long duration;
    public long albumId;
    public String url; // for online

    public Music(String url, String artist, String title, String displayName, String album, Uri albumArt) {
        this.url = url;
        this.artist = ListHelper.ifNull(artist);
        this.title = ListHelper.ifNull(title);
        this.displayName = ListHelper.ifNull(displayName);
        this.album = ListHelper.ifNull(album);
        this.relativePath = ListHelper.ifNull(relativePath);
        this.year = 0;
        this.track = 0;
        this.startFrom = 0;
        this.dateAdded =  Calendar.getInstance().getTimeInMillis();
        this.id = 0;
        this.duration = 0;
        this.albumId = new Random().nextLong();
        this.albumArt = albumArt;
    }

    public Music(String artist, String title, String displayName, String album, String relativePath,
                 int year, int track, int startFrom, long dateAdded,
                 long id, long duration, long albumId,
                 Uri albumArt) {
        this.artist = ListHelper.ifNull(artist);
        this.title = ListHelper.ifNull(title);
        this.displayName = ListHelper.ifNull(displayName);
        this.album = ListHelper.ifNull(album);
        this.relativePath = ListHelper.ifNull(relativePath);
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
        dateAdded = in.readLong();
        id = in.readLong();
        duration = in.readLong();
        albumId = in.readLong();
        url = in.readString();
    }

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
                ", url=" + url +
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
        dest.writeLong(dateAdded);
        dest.writeLong(id);
        dest.writeLong(duration);
        dest.writeLong(albumId);
        dest.writeString(url);
    }
}

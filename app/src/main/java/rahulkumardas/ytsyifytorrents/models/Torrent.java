package rahulkumardas.ytsyifytorrents.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Rahul Kumar Das on 07-05-2017.
 */

public class Torrent implements Parcelable{
    public Torrent(String torUrl, String hash, String quality, String size, long seeds, long peers) {
        this.torUrl = torUrl;
        this.hash = hash;
        this.quality = quality;
        this.size = size;
        this.seeds = seeds;
        this.peers = peers;
    }

    public String torUrl, hash, quality, size;
    public long seeds, peers;

    protected Torrent(Parcel in) {
        torUrl = in.readString();
        hash = in.readString();
        quality = in.readString();
        size = in.readString();
        seeds = in.readLong();
        peers = in.readLong();
    }

    public static final Creator<Torrent> CREATOR = new Creator<Torrent>() {
        @Override
        public Torrent createFromParcel(Parcel in) {
            return new Torrent(in);
        }

        @Override
        public Torrent[] newArray(int size) {
            return new Torrent[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(torUrl);
        parcel.writeString(hash);
        parcel.writeString(quality);
        parcel.writeString(size);
        parcel.writeLong(seeds);
        parcel.writeLong(peers);
    }
}
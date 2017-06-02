package rahulkumardas.ytsyifytorrents.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by Rahul Kumar Das on 06-05-2017.
 */

public class Movie implements Parcelable{

    public Movie(String imageUrl, String movieName, int year) {
        this.imageUrl = imageUrl;
        this.movieName = movieName;
        this.year = year;
    }

    public Movie(){

    }

    public String imageUrl,bgImageUrl, movieName, genre, trailerCode, synopsis, imdbCode, language, ytsUrl, titleLong;
    public float rating;
    public int year, length;
    public long likeCount, downloadCount, id;
    public List<Torrent> list;

    protected Movie(Parcel in) {
        imageUrl = in.readString();
        bgImageUrl = in.readString();
        movieName = in.readString();
        genre = in.readString();
        trailerCode = in.readString();
        synopsis = in.readString();
        imdbCode = in.readString();
        language = in.readString();
        ytsUrl = in.readString();
        titleLong = in.readString();
        rating = in.readFloat();
        year = in.readInt();
        length = in.readInt();
        likeCount = in.readLong();
        downloadCount = in.readLong();
        id = in.readLong();
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(imageUrl);
        parcel.writeString(bgImageUrl);
        parcel.writeString(movieName);
        parcel.writeString(genre);
        parcel.writeString(trailerCode);
        parcel.writeString(synopsis);
        parcel.writeString(imdbCode);
        parcel.writeString(language);
        parcel.writeString(ytsUrl);
        parcel.writeString(titleLong);
        parcel.writeFloat(rating);
        parcel.writeInt(year);
        parcel.writeInt(length);
        parcel.writeLong(likeCount);
        parcel.writeLong(downloadCount);
        parcel.writeLong(id);
    }
}

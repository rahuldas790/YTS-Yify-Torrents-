package rahulkumardas.ytsyifytorrents;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.target.ViewTarget;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import rahulkumardas.ytsyifytorrents.Utils.Config;
import rahulkumardas.ytsyifytorrents.databases.BookmarkDatabase;
import rahulkumardas.ytsyifytorrents.dialogs.DownloadDialog;
import rahulkumardas.ytsyifytorrents.dialogs.DownloadProgressDialog;
import rahulkumardas.ytsyifytorrents.models.Movie;
import rahulkumardas.ytsyifytorrents.models.Torrent;
import rahulkumardas.ytsyifytorrents.network.RestAdapterAPI;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MovieDetailsActivity extends AppCompatActivity implements YouTubePlayer.OnInitializedListener, View.OnClickListener {

    private final String TAG = "MovieDetailsActivity";
    private ImageView mainImage;
    private YouTubePlayerSupportFragment youTubePlayerFragment;
    private static final int RECOVERY_REQUEST = 1;
    private TextView name, year, genres, length, rating, synopsis, title, imdbLink, ytsLink;
    private Movie movie;
    private View rootView;
    private Target target;
    private Button download;

    /*
     * Available torrent and their views
     */
    private List<Torrent> list;
    private TextView low, high, threeD; //available movie qualities
    private int indexLow, indexHigh, indexThreeD; //to store the indices of the from list

    private boolean isBookmark = false;
    private BookmarkDatabase database;

    /*
     * Suggestions
     */
    private TextView name1, year1, name2, year2, name3, year3, name4, year4;
    private ImageView image1, image2, image3, image4;
    List<Movie> suggestions;
    private Call<JsonObject> result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle b = this.getIntent().getExtras();
        if (b != null)
            movie = b.getParcelable("Movie");

        getSupportActionBar().setTitle(movie.movieName);

        mainImage = (ImageView) findViewById(R.id.mainImage);
        name = (TextView) findViewById(R.id.name);
        year = (TextView) findViewById(R.id.year);
        genres = (TextView) findViewById(R.id.genres);
        length = (TextView) findViewById(R.id.length);
        rating = (TextView) findViewById(R.id.rating);
        synopsis = (TextView) findViewById(R.id.synopsis);
        rootView = findViewById(R.id.root);
        low = (TextView) findViewById(R.id.lowRes);
        high = (TextView) findViewById(R.id.highRes);
        threeD = (TextView) findViewById(R.id.threeD);
        download = (Button) findViewById(R.id.download);
        imdbLink = (TextView) findViewById(R.id.imdb_link);
        ytsLink = (TextView) findViewById(R.id.yts_link);
        ytsLink.setOnClickListener(this);
        imdbLink.setOnClickListener(this);
        download.setOnClickListener(this);

        name.setVisibility(View.GONE);

        Spannable buttonLabel = new SpannableString("      Download");
        buttonLabel.setSpan(new ImageSpan(getApplicationContext(), R.drawable.ic_download,
                ImageSpan.ALIGN_BOTTOM), 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        download.setText(buttonLabel);

        name.setText(movie.movieName);
        year.setText(movie.year + "");
        genres.setText(movie.genre.substring(2).trim());
        length.setText("Dur : " + movie.length + " mins.");
        rating.setText("IMDB : " + movie.rating + "/10");
        synopsis.setText(movie.synopsis);
        list = getIntent().getParcelableArrayListExtra("list");

        //set the available downloads
        setAvailable();

        target = new GlideDrawableViewBackgroundTarget(rootView);
        Glide.with(this)
                .load(movie.bgImageUrl)
                .centerCrop()
                .placeholder(R.color.colorPrimary)
                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                .into(target);

        Glide.with(this)
                .load(movie.imageUrl.split(";")[1])
                .placeholder(R.mipmap.my_logo)
                .dontAnimate()
                .fitCenter()
                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                .into(mainImage);

        mainImage.setOnClickListener(this);


        youTubePlayerFragment = (YouTubePlayerSupportFragment) getSupportFragmentManager()
                .findFragmentById(R.id.youtube_view);
        youTubePlayerFragment.initialize(Config.YOUTUBE_API_KEY, this);

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        getMovieSuggestions(movie.id);

    }

    private void getMovieSuggestions(long movieId) {
        name1 = (TextView) findViewById(R.id.name1);
        name2 = (TextView) findViewById(R.id.name2);
        name3 = (TextView) findViewById(R.id.name3);
        name4 = (TextView) findViewById(R.id.name4);

        year1 = (TextView) findViewById(R.id.year1);
        year2 = (TextView) findViewById(R.id.year2);
        year3 = (TextView) findViewById(R.id.year3);
        year4 = (TextView) findViewById(R.id.year4);

        image1 = (ImageView) findViewById(R.id.image1);
        image2 = (ImageView) findViewById(R.id.image2);
        image3 = (ImageView) findViewById(R.id.image3);
        image4 = (ImageView) findViewById(R.id.image4);

        RestAdapterAPI adapter = Config.getRestAdapter();

        result = adapter.getMovieSuggestions(movieId);
        result.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                JsonObject object = response.body();
                JsonArray movieList = object.get("data").getAsJsonObject().get("movies").getAsJsonArray();
                suggestions = new ArrayList<Movie>();
                for (int i = 0; i < movieList.size(); i++) {
                    Movie movie = new Movie();
                    JsonObject item = movieList.get(i).getAsJsonObject();
                    long id = item.get("id").getAsLong();
                    String url = item.get("url").getAsString();
                    String imdb_code = item.get("imdb_code").getAsString();
                    String title = item.get("title").getAsString();
                    String titleLong = item.get("title_long").getAsString();
                    int year = item.get("year").getAsInt();
                    float rating = item.get("rating").getAsFloat();
                    int length = item.get("runtime").getAsInt();

                    String genres = "";
                    JsonArray gen = item.get("genres").getAsJsonArray();
                    for (int j = 0; j < gen.size(); j++) {
                        genres = genres + " / " + gen.get(j).getAsString();
                    }

                    String synopsis = item.get("synopsis").getAsString();
                    String ytTrailer = item.get("yt_trailer_code").getAsString();
                    String language = item.get("language").getAsString();
                    String mpa_rating = item.get("mpa_rating").getAsString();
                    String background_image = item.get("background_image").getAsString();
                    String medium_cover_image = item.get("small_cover_image").getAsString();
                    medium_cover_image = medium_cover_image + ";" + item.get("medium_cover_image").getAsString();
                    medium_cover_image = medium_cover_image + ";" + item.get("medium_cover_image").getAsString();

                    List<Torrent> torrentList = new ArrayList<Torrent>();
                    JsonArray torrents = item.get("torrents").getAsJsonArray();
                    for (int j = 0; j < torrents.size(); j++) {
                        JsonObject torrent = torrents.get(j).getAsJsonObject();
                        String urlTorr = torrent.get("url").getAsString();
                        String hash = torrent.get("hash").getAsString();
                        String quality = torrent.get("quality").getAsString();
                        String size = torrent.get("size").getAsString();

                        long seeds = torrent.get("seeds").getAsLong();
                        long peers = torrent.get("peers").getAsLong();

                        torrentList.add(new Torrent(urlTorr, hash, quality, size, seeds, peers));
                    }

                    movie.ytsUrl = url;
                    movie.imageUrl = medium_cover_image;
                    movie.bgImageUrl = background_image;
                    movie.genre = genres;
                    movie.movieName = title;
                    movie.titleLong = titleLong;
                    movie.id = id;
                    movie.imdbCode = imdb_code;
                    movie.language = language;
                    movie.length = length;
                    movie.rating = rating;
                    movie.synopsis = synopsis;
                    movie.year = year;
                    movie.trailerCode = ytTrailer;
                    movie.list = torrentList;

                    suggestions.add(movie);

                    if (!MovieDetailsActivity.this.isDestroyed())
                        switch (i) {
                            case 0:
                                name1.setText(movie.movieName);
                                year1.setText(movie.year + "");
                                Glide.with(MovieDetailsActivity.this)
                                        .load(movie.imageUrl.split(";")[1])
                                        .placeholder(R.mipmap.my_logo)
                                        .diskCacheStrategy(DiskCacheStrategy.RESULT)
                                        .into(image1);
                                break;
                            case 1:
                                name2.setText(movie.movieName);
                                year2.setText(movie.year + "");
                                Glide.with(MovieDetailsActivity.this)
                                        .load(movie.imageUrl.split(";")[1])
                                        .placeholder(R.mipmap.my_logo)
                                        .diskCacheStrategy(DiskCacheStrategy.RESULT)
                                        .into(image2);
                                break;
                            case 2:
                                name3.setText(movie.movieName);
                                year3.setText(movie.year + "");
                                Glide.with(MovieDetailsActivity.this)
                                        .load(movie.imageUrl.split(";")[1])
                                        .placeholder(R.mipmap.my_logo)
                                        .diskCacheStrategy(DiskCacheStrategy.RESULT)
                                        .into(image3);
                                break;
                            case 3:
                                name4.setText(movie.movieName);
                                year4.setText(movie.year + "");
                                Glide.with(MovieDetailsActivity.this)
                                        .load(movie.imageUrl.split(";")[1])
                                        .placeholder(R.mipmap.my_logo)
                                        .diskCacheStrategy(DiskCacheStrategy.RESULT)
                                        .into(image4);
                                break;
                        }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(MovieDetailsActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Glide.clear(mainImage);
        Glide.clear(target);
        Glide.clear(image1);
        Glide.clear(image2);
        Glide.clear(image3);
        Glide.clear(image4);
        result.cancel();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        boolean val = super.onPrepareOptionsMenu(menu);
        database = new BookmarkDatabase(this);
        if (database.existMOVIE(movie.id + "")) {
            isBookmark = true;
            menu.getItem(0).setIcon(R.drawable.ic_bookmark_green);
        }

        return val;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_movie_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.bookmark) {
            if (isBookmark) {
                item.setIcon(R.drawable.ic_bookmark_white);
                database.deleteMovie(movie.id + "");
                isBookmark = false;
            } else {
                item.setIcon(R.drawable.ic_bookmark_green);
                movie.list = list;
                database.insertMovie(movie);
                isBookmark = true;
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setAvailable() {
        for (int i = 0; i < list.size(); i++) {
            Torrent torrent = list.get(i);
            if (torrent.quality.equals("720p")) {
                low.setVisibility(View.VISIBLE);
                low.setOnClickListener(this);
                indexLow = i;
            } else if (torrent.quality.equals("1080p")) {
                high.setVisibility(View.VISIBLE);
                high.setOnClickListener(this);
                indexHigh = i;
            } else {
                threeD.setVisibility(View.VISIBLE);
                threeD.setOnClickListener(this);
                indexThreeD = i;
            }
        }
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player, boolean wasRestored) {
        if (!wasRestored) {
            player.cueVideo(movie.trailerCode); // Plays https://www.youtube.com/watch?v=fhWaJi1Hsfo
        }
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult errorReason) {
        if (errorReason.isUserRecoverableError()) {
            errorReason.getErrorDialog(this, RECOVERY_REQUEST).show();
        } else {
            String error = String.format(getString(R.string.player_error), errorReason.toString());
            Toast.makeText(this, error, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.lowRes:
                DownloadProgressDialog.newInstance(list.get(indexLow), movie.movieName, "torrent")
                        .show(getSupportFragmentManager(), "Download");
                break;
            case R.id.highRes:
                DownloadProgressDialog.newInstance(list.get(indexHigh), movie.movieName, "torrent")
                        .show(getSupportFragmentManager(), "Download");
                break;
            case R.id.threeD:
                DownloadProgressDialog.newInstance(list.get(indexThreeD), movie.movieName, "torrent")
                        .show(getSupportFragmentManager(), "Download");
                break;
            case R.id.mainImage:
                Torrent torrent = new Torrent(movie.imageUrl.split(";")[2], null, null, null, 0, 0);
                DownloadProgressDialog.newInstance(torrent, movie.movieName, "image")
                        .show(getSupportFragmentManager(), "Download");
                break;

            case R.id.download:
                DownloadDialog.newInstance(list, movie.language, movie.movieName).show(getSupportFragmentManager(), "download");
                break;
            case R.id.yts_link:
                Intent i = new Intent(this, WebViewActivity.class);
                i.putExtra("url", movie.ytsUrl);
                startActivity(i);
                break;
            case R.id.imdb_link:
                Intent i2 = new Intent(this, WebViewActivity.class);
                i2.putExtra("url", "http://www.imdb.com/title/" + movie.imdbCode + "/");
                startActivity(i2);
                break;
        }
    }

    // below two classes are used to set background image using Glide from server

    /**
     * ImageViewTarget
     */
    public abstract class ViewBackgroundTarget<Z> extends ViewTarget<View, Z> implements GlideAnimation.ViewAdapter {
        public ViewBackgroundTarget(View view) {
            super(view);
        }

        @Override
        public void onLoadCleared(Drawable placeholder) {
            setBackground(placeholder);
        }

        @Override
        public void onLoadStarted(Drawable placeholder) {
            setBackground(placeholder);
        }

        @Override
        public void onLoadFailed(Exception e, Drawable errorDrawable) {
            setBackground(errorDrawable);
        }

        @Override
        public void onResourceReady(Z resource, GlideAnimation<? super Z> glideAnimation) {
            if (glideAnimation == null || !glideAnimation.animate(resource, this)) {
                setResource(resource);
            }
        }

        @Override
        public void setDrawable(Drawable drawable) {
            setBackground(drawable);
        }

        @Override
        public Drawable getCurrentDrawable() {
            return view.getBackground();
        }

        @SuppressWarnings("deprecation")
        protected void setBackground(Drawable drawable) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                view.setBackground(drawable);
            } else {
                view.setBackgroundDrawable(drawable);
            }
        }

        protected abstract void setResource(Z resource);
    }

    /**
     * @see GlideDrawableImageViewTarget
     */
    public class GlideDrawableViewBackgroundTarget extends ViewBackgroundTarget<GlideDrawable> {
        private int maxLoopCount;
        private GlideDrawable resource;

        public GlideDrawableViewBackgroundTarget(View view) {
            this(view, GlideDrawable.LOOP_FOREVER);
        }

        public GlideDrawableViewBackgroundTarget(View view, int maxLoopCount) {
            super(view);
            this.maxLoopCount = maxLoopCount;
        }

        @Override
        public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> animation) {
            super.onResourceReady(resource, animation);
            this.resource = resource;
            resource.setLoopCount(maxLoopCount);
            resource.start();
        }

        @Override
        protected void setResource(GlideDrawable resource) {
            setBackground(resource);
        }

        @Override
        public void onStart() {
            if (resource != null) {
                resource.start();
            }
        }

        @Override
        public void onStop() {
            if (resource != null) {
                resource.stop();
            }
        }
    }
}

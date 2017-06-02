package rahulkumardas.ytsyifytorrents;

import android.animation.ValueAnimator;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import rahulkumardas.ytsyifytorrents.Utils.CircleProgressBar;
import rahulkumardas.ytsyifytorrents.Utils.Config;
import rahulkumardas.ytsyifytorrents.adapters.MoviesAdapter;
import rahulkumardas.ytsyifytorrents.adapters.SearchAdapter;
import rahulkumardas.ytsyifytorrents.bitlet.util.Utils;
import rahulkumardas.ytsyifytorrents.models.Movie;
import rahulkumardas.ytsyifytorrents.models.Torrent;
import rahulkumardas.ytsyifytorrents.network.RestAdapterAPI;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SearchActivity extends AppCompatActivity {

    private CircleProgressBar circleProgressBar;
    private EditText input;
    private RecyclerView recyclerView;
    private List<Movie> searchList;
    private ValueAnimator animator;
    private SearchAdapter adapter;
    private LinearLayoutManager layoutManager;
    private Call<JsonObject> result;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        circleProgressBar = (CircleProgressBar) findViewById(R.id.line_progress);
        input = (EditText) findViewById(R.id.input);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        searchList = new ArrayList<>();

        animator = ValueAnimator.ofInt(0, 100);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int progress = (int) animation.getAnimatedValue();
                circleProgressBar.setProgress(progress);
            }
        });
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setDuration(2000);

        searchList = new ArrayList<>();

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        adapter = new SearchAdapter(this, searchList);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        adapter.setClickListener(new MoviesAdapter.ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (YtsApplication.AtoSearchEnabled) {
                    getDetails(searchList.get(position).movieName);
                    dialog = new ProgressDialog(SearchActivity.this);
                    dialog.setTitle("Fetching details...");
                    dialog.show();
                } else {
                    Intent i = new Intent();
                    Bundle b = new Bundle();
                    b.putParcelable("Movie", searchList.get(position));
                    i.putParcelableArrayListExtra("list", (ArrayList<Torrent>) searchList.get(position).list);
                    i.putExtras(b);
                    i.setClass(SearchActivity.this, MovieDetailsActivity.class);
                    startActivity(i);
                }
            }
        });

        input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() >= 3) {
                    if (result != null) {
                        result.cancel();
                    }
                    if (YtsApplication.AtoSearchEnabled)
                        getAutoCompleteList(charSequence.toString());
                    else
                        getList(charSequence.toString());
                    startAnimation();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void getDetails(final String query) {
        RestAdapterAPI adapterAPI = Config.getRestAdapter();
        Call<JsonObject> result = adapterAPI.getListWithQuery(query);
        result.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
//                searchList.clear();
//                adapter.notifyDataSetChanged();
//                stopAnimation();
                JsonObject object = response.body();
                JsonArray movieList = new JsonArray();
                if (object.get("data").getAsJsonObject().has("movies")) {
                    movieList = object.get("data").getAsJsonObject().get("movies").getAsJsonArray();
                } else {
                    Toast.makeText(SearchActivity.this, "No result found", Toast.LENGTH_SHORT).show();
                }

                for (int i = 0; i < movieList.size(); i++) {

                    JsonObject item = movieList.get(i).getAsJsonObject();
                    String title = item.get("title").getAsString();
                    if (title.equals(query)) {
                        Movie movie = new Movie();
                        long id = item.get("id").getAsLong();
                        String url = item.get("url").getAsString();
                        String imdb_code = item.get("imdb_code").getAsString();

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
                        medium_cover_image = medium_cover_image + ";" + item.get("large_cover_image").getAsString();

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

                        Intent intent = new Intent();
                        Bundle b = new Bundle();
                        b.putParcelable("Movie", movie);
                        intent.putParcelableArrayListExtra("list", (ArrayList<Torrent>) movie.list);
                        intent.putExtras(b);
                        intent.setClass(SearchActivity.this, MovieDetailsActivity.class);
                        startActivity(intent);
                        dialog.dismiss();
                        break;
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });
    }

    private void startAnimation() {
        circleProgressBar.setVisibility(View.VISIBLE);
        animator.start();
    }

    private void stopAnimation() {
        circleProgressBar.setVisibility(View.INVISIBLE);
        animator.end();
    }

    private void getAutoCompleteList(String query) {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        // set your desired log level
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        // add your other interceptors â€¦

        // add logging as last interceptor
        httpClient.addInterceptor(logging);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(RestAdapterAPI.BASE_END_POINT2)
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient.build())
                .build();
        RestAdapterAPI adapterAPI = retrofit.create(RestAdapterAPI.class);
        result = adapterAPI.autoSeach(query);
        result.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                searchList.clear();
                adapter.notifyDataSetChanged();
                stopAnimation();
                JsonObject object = response.body();
                JsonArray movieList = new JsonArray();

                if (object.has("data")) {
                    movieList = object.get("data").getAsJsonArray();
                } else {
                    Toast.makeText(SearchActivity.this, "No result found", Toast.LENGTH_SHORT).show();
                }

                for (int i = 0; i < movieList.size(); i++) {
                    Movie movie = new Movie();
                    JsonObject item = movieList.get(i).getAsJsonObject();

                    String title = item.get("title").getAsString();
                    int year = item.get("year").getAsInt();
                    String medium_cover_image = item.get("img").getAsString();
                    medium_cover_image = medium_cover_image + ";" + item.get("img").getAsString();
                    medium_cover_image = medium_cover_image + ";" + item.get("img").getAsString();


                    movie.imageUrl = medium_cover_image;
                    movie.movieName = title;
                    movie.year = year;

                    SearchActivity.this.searchList.add(movie);
                    adapter.notifyDataSetChanged();

                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });

    }

    private void getList(String query) {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        // set your desired log level
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        // add your other interceptors â€¦

        // add logging as last interceptor
        httpClient.addInterceptor(logging);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(RestAdapterAPI.BASE_END_POINT)
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient.build())
                .build();


        RestAdapterAPI adapterAPI = retrofit.create(RestAdapterAPI.class);

        result = adapterAPI.getListWithQuery(query);
        result.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                searchList.clear();
                adapter.notifyDataSetChanged();
                stopAnimation();
                JsonObject object = response.body();
                JsonArray movieList = new JsonArray();
                if (object.get("data").getAsJsonObject().has("movies")) {
                    movieList = object.get("data").getAsJsonObject().get("movies").getAsJsonArray();
                } else {
                    Toast.makeText(SearchActivity.this, "No result found", Toast.LENGTH_SHORT).show();
                }

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
                    medium_cover_image = medium_cover_image + ";" + item.get("large_cover_image").getAsString();

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

                    SearchActivity.this.searchList.add(movie);
                    adapter.notifyDataSetChanged();

                }

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
//                Toast.makeText(SearchActivity.this, "Error is " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }
}

package rahulkumardas.ytsyifytorrents;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final int MAX_ITEMS_PER_REQUEST = 10;
    private List<Movie> movieList;
    private RecyclerView recyclerView;
    private final int COLUMNS = 2;
    private MoviesAdapter adapter;
    private GridLayoutManager layoutManager;
    private int page = 1;
    // Store a member variable for the listener
    private EndlessRecyclerViewScrollListener scrollListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        movieList = new ArrayList<>();

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        adapter = new MoviesAdapter(this, movieList);
        layoutManager = new GridLayoutManager(this, COLUMNS, GridLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        adapter.setClickListener(new MoviesAdapter.ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
//                startActivity(new Intent(MainActivity.this, MovieDetailsActivity.class));
                Intent i = new Intent();
                Bundle b = new Bundle();
                b.putParcelable("Movie", movieList.get(position));
                i.putExtras(b);
                i.setClass(MainActivity.this, MovieDetailsActivity.class);
                startActivity(i);
//                Toast.makeText(MainActivity.this, "Position is "+position, Toast.LENGTH_SHORT).show();
            }
        });

        // Retain an instance so that you can call `resetState()` for fresh searches
        scrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
//                loadNextDataFromApi(page);
                getMovieList();
                Toast.makeText(MainActivity.this, "Reached last", Toast.LENGTH_SHORT).show();
            }
        };
        // Adds the scroll listener to RecyclerView
        recyclerView.addOnScrollListener(scrollListener);


        getMovieList();
    }


    private void getMovieList() {
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

        Call<JsonObject> result = adapterAPI.getListWithPage(page);
        result.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                page++;
//                findViewById(R.id.progress).setVisibility(View.GONE);
                JsonObject object = response.body();
                JsonArray movieList = object.get("data").getAsJsonObject().get("movies").getAsJsonArray();

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
                    String medium_cover_image = item.get("medium_cover_image").getAsString();

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

                    MainActivity.this.movieList.add(movie);
                    adapter.notifyDataSetChanged();

                }

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Error is " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.login) {
            new LoginDialog().show(getSupportFragmentManager(), "login");
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}

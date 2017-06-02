package rahulkumardas.ytsyifytorrents;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
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
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import rahulkumardas.ytsyifytorrents.Utils.EndlessRecyclerViewScrollListener;
import rahulkumardas.ytsyifytorrents.adapters.MoviesAdapter;
import rahulkumardas.ytsyifytorrents.dialogs.LoginDialog;
import rahulkumardas.ytsyifytorrents.models.Movie;
import rahulkumardas.ytsyifytorrents.models.Torrent;
import rahulkumardas.ytsyifytorrents.network.RestAdapterAPI;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    private static final int MAX_ITEMS_PER_REQUEST = 10;
    private List<Movie> movieList;
    private RecyclerView recyclerView;
    private final int COLUMNS = 2;
    private MoviesAdapter adapter;
    private GridLayoutManager layoutManager;
    private int page = 1;
    private boolean isFilter = false;
    private String genres, qualities, ratings, orders, sorts, terms;
    // Store a member variable for the listener
    private EndlessRecyclerViewScrollListener scrollListener;
    private FragmentManager fragmentManager;

    /*
     * Filter dialog
     */
    private Dialog filter;
    private Button apply;
    private Spinner rating, quality, genre, order, sort;
    private EditText term;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.latest_movies);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        movieList = new ArrayList<>();

        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                1);

    }

    @Override
    protected void onResume() {
        super.onResume();

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        adapter = new MoviesAdapter(this, movieList, YtsApplication.Span);
        layoutManager = new GridLayoutManager(this, YtsApplication.Span, GridLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        adapter.setClickListener(new MoviesAdapter.ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
//                startActivity(new Intent(MainActivity.this, MovieDetailsActivity.class));
                Intent i = new Intent();
                Bundle b = new Bundle();
                b.putParcelable("Movie", movieList.get(position));
                i.putParcelableArrayListExtra("list", (ArrayList<Torrent>) movieList.get(position).list);
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
                if (isFilter)
                    getMoviesWithFilter();
                else
                    getMovieList();

//                Toast.makeText(MainActivity.this, "Reached last", Toast.LENGTH_SHORT).show();
            }
        };
        // Adds the scroll listener to RecyclerView
        recyclerView.addOnScrollListener(scrollListener);
        fragmentManager = getSupportFragmentManager();


        filter = new Dialog(this);
        filter.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        filter.setContentView(R.layout.dialog_filter);
        filter.setTitle("Filter");
        apply = (Button) filter.findViewById(R.id.apply);
        rating = (Spinner) filter.findViewById(R.id.rating);
        order = (Spinner) filter.findViewById(R.id.order);
        sort = (Spinner) filter.findViewById(R.id.sort);
        genre = (Spinner) filter.findViewById(R.id.genre);
        quality = (Spinner) filter.findViewById(R.id.quality);

        term = (EditText) filter.findViewById(R.id.term);
        apply.setOnClickListener(this);

        getMovieList();
    }

    private RestAdapterAPI getApiAdapter() {
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


        return retrofit.create(RestAdapterAPI.class);
    }

    private void getMovieList() {

        RestAdapterAPI adapterAPI = getApiAdapter();

        Call<JsonObject> result = adapterAPI.getListWithPage(page);
        enqueueResult(result);

    }

    private void getMoviesWithFilter() {
        RestAdapterAPI api = getApiAdapter();
        Call<JsonObject> result = api.getListWithFilter(page, terms, qualities, ratings,
                genres, sorts, orders);
        enqueueResult(result);
    }

    private void enqueueResult(Call<JsonObject> result) {
        result.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                page++;
//                findViewById(R.id.progress).setVisibility(View.GONE);
                JsonObject object = response.body();
                if (object.get("data").getAsJsonObject().has("movies")) {
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

                        MainActivity.this.movieList.add(movie);
                        adapter.notifyDataSetChanged();
                    }
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
        if (id == R.id.action_serach) {
            startActivity(new Intent(this, SearchActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            isFilter = false;
            getSupportActionBar().setTitle(R.string.latest_movies);
            page = 1;
            movieList.clear();
            adapter.notifyDataSetChanged();
            getMovieList();
        } else if (id == R.id.nav_filter) {
            isFilter = true;
            getSupportActionBar().setTitle(R.string.filter_movies);
            filter.show();
        } else if (id == R.id.nav_downloads) {
            startActivity(new Intent(this, DownloadsActivity.class));
        } else if (id == R.id.nav_bookmark) {
            startActivity(new Intent(this, BookmarksActivity.class));
        } else if (id == R.id.nav_manage) {
            startActivity(new Intent(this, SettingsActivity.class));
        } else if (id == R.id.login) {
            new LoginDialog().show(getSupportFragmentManager(), "login");
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.apply:
                filter.dismiss();
                page = 1;
                movieList.clear();
                adapter.notifyDataSetChanged();
                terms = term.getText().toString();
                qualities = (String) quality.getSelectedItem();
                ratings = ((String) rating.getSelectedItem()).replace("+", "");
                genres = genre.getSelectedItemPosition() == 0 ? "" : (String) genre.getSelectedItem();
                sorts = (String) sort.getSelectedItem();
                orders = (String) order.getSelectedItem();
                getMoviesWithFilter();

        }
    }
}

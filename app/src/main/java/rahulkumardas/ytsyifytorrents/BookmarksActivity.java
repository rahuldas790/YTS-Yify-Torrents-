package rahulkumardas.ytsyifytorrents;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import rahulkumardas.ytsyifytorrents.adapters.BookmarkAdapter;
import rahulkumardas.ytsyifytorrents.databases.BookmarkDatabase;
import rahulkumardas.ytsyifytorrents.models.Movie;
import rahulkumardas.ytsyifytorrents.models.Torrent;

public class BookmarksActivity extends AppCompatActivity {

    private List<Movie> movieList;
    private RecyclerView recyclerView;
    private final int COLUMNS = 2;
    private BookmarkAdapter adapter;
    private GridLayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmarks);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setTitle("Bookmarks");

        BookmarkDatabase db = new BookmarkDatabase(this);
        movieList = db.getAllMovies();

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        adapter = new BookmarkAdapter(this, movieList);
        layoutManager = new GridLayoutManager(this, COLUMNS, GridLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        adapter.setClickListener(new BookmarkAdapter.ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
//                startActivity(new Intent(MainActivity.this, MovieDetailsActivity.class));
                Intent i = new Intent();
                Bundle b = new Bundle();
                b.putParcelable("Movie", movieList.get(position));
                i.putParcelableArrayListExtra("list", (ArrayList<Torrent>) movieList.get(position).list);
                i.putExtras(b);
                i.setClass(BookmarksActivity.this, MovieDetailsActivity.class);
                startActivity(i);
//                Toast.makeText(MainActivity.this, "Position is "+position, Toast.LENGTH_SHORT).show();
            }
        });

    }
}

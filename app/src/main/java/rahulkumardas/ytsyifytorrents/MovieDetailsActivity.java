package rahulkumardas.ytsyifytorrents;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.ViewTarget;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

public class MovieDetailsActivity extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener{

    private ImageView mainImage;
    private YouTubePlayerView youTubeView;
    private static final int RECOVERY_REQUEST = 1;
    private TextView name, year, genres, length, rating, synopsis;
    private Movie movie;
    private View rootView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        Bundle b = this.getIntent().getExtras();
        if (b != null)
            movie = b.getParcelable("Movie");

        mainImage = (ImageView)findViewById(R.id.mainImage);
        name = (TextView)findViewById(R.id.name);
        year = (TextView)findViewById(R.id.year);
        genres = (TextView)findViewById(R.id.genres);
        length = (TextView)findViewById(R.id.length);
        rating = (TextView)findViewById(R.id.rating);
        synopsis = (TextView)findViewById(R.id.synopsis);
        rootView = findViewById(R.id.root);

        name.setText(movie.movieName);
        year.setText(movie.year+"");
        genres.setText(movie.genre.substring(2).trim());
        length.setText("Dur : "+movie.length+" mins.");
        rating.setText("IMDB : "+movie.rating+"/10");
        synopsis.setText(movie.synopsis);

        Glide.with(this)
                .load(movie.bgImageUrl)
                .centerCrop()
                .placeholder(R.color.colorPrimary)
                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                .into(new GlideDrawableViewBackgroundTarget(rootView));

        Glide.with(this)
                .load(movie.imageUrl)
                .placeholder(R.mipmap.ic_launcher)
                .dontAnimate()
                .fitCenter()
                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                .into(mainImage);

        youTubeView = (YouTubePlayerView) findViewById(R.id.youtube_view);
        youTubeView.initialize(Config.YOUTUBE_API_KEY, this);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RECOVERY_REQUEST) {
            // Retry initialization if user performed a recovery action
            getYouTubePlayerProvider().initialize(Config.YOUTUBE_API_KEY, this);
        }
    }

    protected YouTubePlayer.Provider getYouTubePlayerProvider() {
        return youTubeView;
    }


    /**  ImageViewTarget */
    public abstract class ViewBackgroundTarget<Z> extends ViewTarget<View, Z> implements GlideAnimation.ViewAdapter {
        public ViewBackgroundTarget(View view) { super(view); }
        @Override public void onLoadCleared(Drawable placeholder) { setBackground(placeholder); }
        @Override public void onLoadStarted(Drawable placeholder) { setBackground(placeholder); }
        @Override public void onLoadFailed(Exception e, Drawable errorDrawable) { setBackground(errorDrawable); }
        @Override public void onResourceReady(Z resource, GlideAnimation<? super Z> glideAnimation) {
            if (glideAnimation == null || !glideAnimation.animate(resource, this)) {
                setResource(resource);
            }
        }
        @Override public void setDrawable(Drawable drawable) { setBackground(drawable); }
        @Override public Drawable getCurrentDrawable() { return view.getBackground(); }

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

    /** @see GlideDrawableImageViewTarget */
    public class GlideDrawableViewBackgroundTarget extends ViewBackgroundTarget<GlideDrawable> {
        private int maxLoopCount;
        private GlideDrawable resource;
        public GlideDrawableViewBackgroundTarget(View view) { this(view, GlideDrawable.LOOP_FOREVER); }
        public GlideDrawableViewBackgroundTarget(View view, int maxLoopCount) {
            super(view);
            this.maxLoopCount = maxLoopCount;
        }

        @Override public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> animation) {
            super.onResourceReady(resource, animation);
            this.resource = resource;
            resource.setLoopCount(maxLoopCount);
            resource.start();
        }

        @Override protected void setResource(GlideDrawable resource) { setBackground(resource); }
        @Override public void onStart() { if (resource != null) { resource.start(); } }
        @Override public void onStop() { if (resource != null) { resource.stop(); } }
    }
}

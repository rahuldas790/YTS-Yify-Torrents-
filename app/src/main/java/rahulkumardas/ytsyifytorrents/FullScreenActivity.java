package rahulkumardas.ytsyifytorrents;

import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import rahulkumardas.ytsyifytorrents.Utils.TouchImageView;

/**
 * Created by Rahul Kumar Das on 01-06-2017.
 */

public class FullScreenActivity extends AppCompatActivity {
    TouchImageView iv;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen);
        iv = (TouchImageView) findViewById(R.id.image);
        String uri = getIntent().getStringExtra("uri");
        iv.setImageURI(Uri.parse(uri));
    }
}

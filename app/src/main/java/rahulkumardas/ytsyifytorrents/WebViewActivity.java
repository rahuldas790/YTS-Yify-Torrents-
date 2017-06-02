package rahulkumardas.ytsyifytorrents;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

/**
 * Created by Rahul Kumar Das on 25-05-2017.
 */

public class WebViewActivity extends AppCompatActivity {

    private WebView webView;
    private ProgressDialog progress;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        progress = new ProgressDialog(this);
        String url = getIntent().getStringExtra("url");
        Toast.makeText(this, url, Toast.LENGTH_SHORT).show();
        webView = (WebView)findViewById(R.id.webView);
        webView = (WebView) findViewById(R.id.webView);
        webView.setVerticalScrollBarEnabled(true);
        webView.setHorizontalScrollBarEnabled(true);
        webView.getSettings().setJavaScriptEnabled(true);

        webView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                progress.show();
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                progress.dismiss();
            }

        });
        webView.loadUrl(url);
    }
}

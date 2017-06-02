package rahulkumardas.ytsyifytorrents;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import rahulkumardas.ytsyifytorrents.dialogs.RegisterFragment;

/**
 * Created by Rahul Kumar Das on 27-05-2017.
 */

public class DownloadsActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private TabLayout tabs;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_downloads);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Downloads");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);


        viewPager = (ViewPager) findViewById(R.id.viewPager);
        tabs = (TabLayout) findViewById(R.id.tabs);
        viewPager.setAdapter(new ViewPagerAdpater(getSupportFragmentManager()));
        tabs.setupWithViewPager(viewPager);
    }

    public class ViewPagerAdpater extends FragmentPagerAdapter {

        public ViewPagerAdpater(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new TorrentDownloads();
                case 1:
                    return new ImageDownloads();
            }
            return null;
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Torrents";
                case 1:
                    return "Images";
                default:
                    return "Downloads";
            }
        }
    }
}

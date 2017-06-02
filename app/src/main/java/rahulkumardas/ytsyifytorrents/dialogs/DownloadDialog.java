package rahulkumardas.ytsyifytorrents.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

import rahulkumardas.ytsyifytorrents.R;
import rahulkumardas.ytsyifytorrents.models.Torrent;

/**
 * Created by Rahul Kumar Das on 17-05-2017.
 */

public class DownloadDialog extends DialogFragment {

    private List<Torrent> list;
    public static String language;
    public static String movieName;

    public static DownloadDialog newInstance(List<Torrent> list, String language, String movieName) {
        DownloadDialog f = new DownloadDialog();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putParcelableArrayList("list", (ArrayList<Torrent>)list);
        args.putString("lang", language);
        args.putString("name", movieName);
        f.setArguments(args);

        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        list = getArguments().getParcelableArrayList("list");
        language = getArguments().getString("lang");
        movieName = getArguments().getString("name");
    }

    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialog.getWindow().setBackgroundDrawableResource(R.color.semi_background);
//        dialog.getWindow().getAttributes().windowAnimations = R.style.animationdialog;

        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_login, container);
        TabLayout tabs = (TabLayout) view.findViewById(R.id.tabs);
        ViewPager viewPager = (ViewPager) view.findViewById(R.id.viewPager);
        ViewPagerAdapter pagerAdapter = new ViewPagerAdapter(getChildFragmentManager());
        viewPager.setAdapter(pagerAdapter);
        tabs.setupWithViewPager(viewPager);

        return view;
    }

    public class ViewPagerAdapter extends FragmentPagerAdapter {

        public ViewPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position){
                case 0:
                    for(int i=0;i<list.size();i++)
                        if(list.get(i).quality.equals("720p"))
                            return QualityFragment.newInstance(list.get(i));

                case 1:
                    for(int i=0;i<list.size();i++)
                        if(list.get(i).quality.equals("1080p"))
                            return QualityFragment.newInstance(list.get(i));

                case 2:
                    for(int i=0;i<list.size();i++)
                        if(list.get(i).quality.equals("3D"))
                            return QualityFragment.newInstance(list.get(i));


            }
            return  null;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "720p";
                case 1:
                    return "1080p";
                case 2:
                    return "3D";

                default:
                    return "";
            }
        }
    }

}

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

import rahulkumardas.ytsyifytorrents.R;

/**
 * Created by Rahul Kumar Das on 03-05-2017.
 */

public class LoginDialog extends DialogFragment {

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
        TabLayout tabs = (TabLayout)view.findViewById(R.id.tabs);
        ViewPager viewPager = (ViewPager)view.findViewById(R.id.viewPager);
        ViewPagerAdapter pagerAdapter = new ViewPagerAdapter(getChildFragmentManager());
        viewPager.setAdapter(pagerAdapter);
        tabs.setupWithViewPager(viewPager);

        return view;
    }

    public class ViewPagerAdapter extends FragmentPagerAdapter{

        public ViewPagerAdapter(FragmentManager fragmentManager){
            super(fragmentManager);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position){
                case 0: return new LoginFragment();
                case 1: return new RegisterFragment();
            }
            return new LoginFragment();
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position){
                case 0: return "Login";
                case 1: return "Register";

                default: return "";
            }
        }
    }
}

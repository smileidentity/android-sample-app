package com.demo.smileid.sid_sdk;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

public class SIDHomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.sid_activity_home);

        ((TextView) findViewById(R.id.tvVersion)).setText(String.format(getString(
            R.string.lbl_version_number_2), BuildConfig.VERSION_NAME));

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tlMenu);
        StaticPager pager = (StaticPager) findViewById(R.id.spScreens);
        pager.setAdapter(new ScreensPagerAdapter(getSupportFragmentManager()));
        pager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(pager));
        pager.setOffscreenPageLimit(3);
    }

    class ScreensPagerAdapter extends FragmentPagerAdapter {

        private int fragmentCount = 3;
        ArrayList<BaseFragment> mFragments = new ArrayList<>(fragmentCount);

        public ScreensPagerAdapter(@NonNull FragmentManager fm) {
            super(fm);
        }

        @Override
        @NonNull
        public Fragment getItem(int position) {
            if (position< mFragments.size() && mFragments.get(position) != null) {
                return mFragments.get(position);
            } else {
                switch (position) {
                    case 1:
                        return new ResourcesFragment();

                    case 2:
                        return new SettingsFragment();

                    default:
                        return new HomeFragment();
                }
            }
        }

        @Override
        @NonNull
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            BaseFragment createdFragment = (BaseFragment) super.instantiateItem(container, position);
            mFragments.add(createdFragment);
            return createdFragment;
        }

        @Override
        @NonNull
        public void destroyItem(ViewGroup container, int position, Object object) {
        }

        private Fragment getChildFragment(int position)  {
            return (BaseFragment) mFragments.get(position);
        }

        @Override
        public int getCount() {
            return fragmentCount;
        }
    }
}
package com.demo.smileid.sid_sdk;

import static com.demo.smileid.sid_sdk.SIDStringExtras.EXTRA_TAG_PREFERENCES_AUTH_TAGS;
import static com.demo.smileid.sid_sdk.SIDStringExtras.SHARED_PREF_USER_ID;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import com.google.android.material.tabs.TabLayout;
import java.util.ArrayList;

public class SIDHomeActivity extends BaseSIDActivity implements BaseFragment.TabActionListener {

    private StaticPager mStaticPager = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.sid_activity_home);

        ((TextView) findViewById(R.id.tvVersion)).setText(String.format(getString(
            R.string.lbl_version_number_2), BuildConfig.VERSION_NAME));

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tlMenu);
        mStaticPager = (StaticPager) findViewById(R.id.spScreens);
        mStaticPager.setAdapter(new ScreensPagerAdapter(getSupportFragmentManager()));
        mStaticPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mStaticPager));
        mStaticPager.setOffscreenPageLimit(3);
    }

    private void resetJob() {
        jobType = -1;
        mUseMultipleEnroll = false;
        mUseOffLineAuth = false;
    }

    @Override
    public void doBasicKYC() {
        resetJob();
        jobType = 4;
        startSelfieCapture(true, false);
    }

    @Override
    public void doEnhancedKYC() {
        resetJob();
        mConsentRequired = true;
        jobType = 1;
        startSelfieCapture(true, true, false, false, true);
    }

    @Override
    public void doBiometricKYC() {
        resetJob();
        jobType = 1;
        startSelfieCapture(true);
    }

    @Override
    public void performDocV() {
        mConsentRequired = true;
        resetJob();
        jobType = 6;
        startSelfieCapture(true);
    }

    @Override
    public void doSmartSelfieAuth() {
        resetJob();
        jobType = 2;

        if (!hasSavedUser()) {
            enrolFirstDialog();
            return;
        }

        startSelfieCapture(false);
    }

    private void enrolFirstDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("You have to enrol (register) first before you can authenticate");
        builder.setCancelable(false);

        builder.setPositiveButton(
                "Register (Enroll)",
                (dialog, id) -> startSelfieCapture(true, false));

        builder.setNegativeButton(
                "Cancel",
                (dialog, id) -> dialog.cancel());

        AlertDialog alert = builder.create();
        alert.show();
    }

    private boolean hasSavedUser() {
        SharedPreferences sharedPreferences = getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);
        String tags = sharedPreferences.getString(SHARED_PREF_USER_ID, null);
        return !TextUtils.isEmpty(tags);
    }

    @Override
    public void openUrl(String url) {
        if (url == null || url.isEmpty()) {
            Toast.makeText(this, "URL not provided", Toast.LENGTH_LONG).show();
            return;
        }
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
    }

    @Override
    public void move2Tab(int position) {
        mStaticPager.setCurrentItem(position, true);
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
            createdFragment.setListener(SIDHomeActivity.this);
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
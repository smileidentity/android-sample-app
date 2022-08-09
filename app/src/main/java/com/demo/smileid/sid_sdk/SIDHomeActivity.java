package com.demo.smileid.sid_sdk;

import static com.demo.smileid.sid_sdk.SIDStringExtras.SHARED_PREF_USER_ID;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import com.google.android.material.tabs.TabLayout;
import java.util.ArrayList;

public class SIDHomeActivity extends BaseSIDActivity implements BaseFragment.TabActionListener {

    private boolean mConsentRequired = false;
    private StaticPager mStaticPager = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.sid_activity_home);

        ((TextView) findViewById(R.id.tvVersion)).setText(String.format(getString(
            R.string.home_screen_lbl_version_nbr), BuildConfig.VERSION_NAME));

        TabLayout tabLayout = findViewById(R.id.tlMenu);
        mStaticPager = findViewById(R.id.spScreens);
        mStaticPager.setAdapter(new ScreensPagerAdapter(getSupportFragmentManager()));
        mStaticPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mStaticPager));
        mStaticPager.setOffscreenPageLimit(3);
    }

    private void resetJob() {
        mKYCProductType = null;
        mConsentRequired = false;
    }

    @Override
    public void testEnroll() {
        resetJob();
        mKYCProductType = KYC_PRODUCT_TYPE.ENROLL_TEST;
        startKYCProcess();
    }

    @Override
    public void doBasicKYC() {
        resetJob();
        mConsentRequired = true;
        mKYCProductType = KYC_PRODUCT_TYPE.BASIC_KYC;
        startKYCProcess();
    }

    @Override
    public void doEnhancedKYC() {
        resetJob();
        mConsentRequired = true;
        mKYCProductType = KYC_PRODUCT_TYPE.ENHANCED_KYC;
        startKYCProcess();
    }

    @Override
    public void doBiometricKYC() {
        resetJob();
        mConsentRequired = true;
        mKYCProductType = KYC_PRODUCT_TYPE.BIOMETRIC_KYC;
        startKYCProcess();
    }

    @Override
    public void performDocV() {
        resetJob();
        mKYCProductType = KYC_PRODUCT_TYPE.DOCUMENT_VERIFICATION;
        startKYCProcess();
    }

    @Override
    public void doSmartSelfieAuth() {
        resetJob();
        mConsentRequired = true;
        mKYCProductType = KYC_PRODUCT_TYPE.SMART_SELFIE_AUTH;
        startKYCProcess();
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

    @Override
    public void move2Screen(Class classObject) {
        startActivity(new Intent(this, classObject));
    }

    protected Intent buildIntent() {
        return new Intent(this, GetStartedActivity.class) {
            {
                putExtra(GetStartedActivity.REQUIRE_CONSENT, mConsentRequired);
            }
        };
    }

    public void onBackPressed() {
        if (mStaticPager.getCurrentItem() != 0) {
            mStaticPager.setCurrentItem(0);
            return;
        }

        super.onBackPressed();
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

        @Override
        public int getCount() {
            return fragmentCount;
        }
    }
}
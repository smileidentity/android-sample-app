package com.demo.smileid.sid_sdk;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ConsentActivity extends AppCompatActivity {

    public static final String TAG = "TAG";
    public static final String PARTNER_LOGO = "PARTNER_LOGO";
    public static final String PARTNER_NAME = "PARTNER_NAME";
    public static final String PRIVACY_LINK = "PRIVACY_LINK";

    private String mTag = "";
    private Bitmap mPartnerLogo;
    private String mPartnerName;
    private String mPrivacyLink;
    private ArrayList<ConsentCategory> mInfoCats = new ArrayList<>();

    ClickableSpan mClickableSpan = new ClickableSpan() {
        @Override
        public void onClick(View textView) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(mPrivacyLink)));
        }
        @Override
        public void updateDrawState(TextPaint ds) {
            super.updateDrawState(ds);
            ds.setUnderlineText(true);
            ds.setColor(Color.parseColor("#004071"));
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_consent);
        findViewById(R.id.ivBackArrow).setOnClickListener(v -> super.onBackPressed());

        populateScreen();
    }

    private void populateScreen() {
        Bundle extras = getIntent().getExtras();
        if (extras == null) return;

        mTag = extras.getString(TAG);
        mPartnerLogo = extras.getParcelable(PARTNER_LOGO);
        mPartnerName = extras.getString(PARTNER_NAME);
        mPrivacyLink = extras.getString(PRIVACY_LINK);

        if (!mPrivacyLink.startsWith("http://") && !mPrivacyLink.startsWith("https://")) {
            mPrivacyLink = "http://" + mPrivacyLink;
        }

        populateTexts();
        populateCats();
    }

    private void populateTexts() {
        /*ImageView ivLogo = findViewById(R.id.ivLogo);

        if (mPartnerLogo != null) {
            ivLogo.setImageBitmap(mPartnerLogo);
            ivLogo.setVisibility(View.VISIBLE);
        }*/

        TextView tvBigTxt = findViewById(R.id.tvBigTxt);
        Spanned bigTxt = Html.fromHtml(getString(com.smileidentity.libsmileid.R.string.user_consent_big_txt, mPartnerName));
        tvBigTxt.setText(bigTxt);

        TextView tvSmallTxt = findViewById(R.id.tvSmallTxt);
        String smallTxt = getString(com.smileidentity.libsmileid.R.string.user_consent_small_txt, mPartnerName);
        tvSmallTxt.setText(smallTxt);

        TextView tvPrivacy2 = findViewById(R.id.tvPolicy2);
        Spanned spPrivacy2 = Html.fromHtml(String.format(getString(com.smileidentity.libsmileid.R.string.user_consent_policy_2), mPartnerName));
        tvPrivacy2.setText(spPrivacy2);

        Spanned strPrivacy = Html.fromHtml(getString(com.smileidentity.libsmileid.R.string.user_consent_policy, mPartnerName));
        SpannableStringBuilder ssb = new SpannableStringBuilder(strPrivacy);
        int start = strPrivacy.toString().indexOf("here");
        int end = start + "here".length();
        ssb.setSpan(mClickableSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        TextView tvPrivacy = findViewById(R.id.tvPolicy);
        tvPrivacy.setText(ssb);
        tvPrivacy.setMovementMethod(LinkMovementMethod.getInstance());
        tvPrivacy.setHighlightColor(Color.TRANSPARENT);
    }

    private void populateCats() {
        mInfoCats = new ArrayList<ConsentCategory>() {
            {
                add(new ConsentCategory(R.drawable.ic_personal_details, getString(com.smileidentity.libsmileid.R.string.user_consent_cat_lbl_1), getString(com.smileidentity.libsmileid.R.string.user_consent_tooltip_lbl_1), getString(com.smileidentity.libsmileid.R.string.user_consent_cat_tooltip_1)));
                add(new ConsentCategory(R.drawable.ic_contact_info, getString(com.smileidentity.libsmileid.R.string.user_consent_cat_lbl_2), getString(com.smileidentity.libsmileid.R.string.user_consent_tooltip_lbl_2), getString(com.smileidentity.libsmileid.R.string.user_consent_cat_tooltip_2)));
                add(new ConsentCategory(R.drawable.ic_doc_info, getString(com.smileidentity.libsmileid.R.string.user_consent_cat_lbl_3), getString(com.smileidentity.libsmileid.R.string.user_consent_tooltip_lbl_3), getString(com.smileidentity.libsmileid.R.string.user_consent_cat_tooltip_3)));
            }
        };

        ConsentCatAdapter adapter = new ConsentCatAdapter();
        ((RecyclerView) findViewById(R.id.clInfoCat)).setAdapter(adapter);
        adapter.setCategories(mInfoCats);
    }

    public void allow(View view) {
        goBack(true);
    }

    public void cancel(View view) {
        goBack(false);
    }

    private void goBack(boolean successful) {
        Intent intent = new Intent();
        intent.putExtra(TAG, mTag);
        setResult(successful ? RESULT_OK : RESULT_CANCELED, intent);
        finish();
    }
}
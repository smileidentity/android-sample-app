package com.demo.smileid.sid_sdk;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.demo.smileid.sid_sdk.DropDownAdapter.DropDownObject;
import com.smileidentity.libsmileid.utils.Version;

import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

public class SIDSplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.sid_activity_splash);

        populateLangs();
    }

    private void populateLangs() {
        ArrayList<DropDownObject> languages = new ArrayList<DropDownObject>() {
            {
                add(new DropDownObject(R.drawable.ic_uk_flag, "English"));
                add(new DropDownObject(R.drawable.ic_french_flag, "French"));
                add(new DropDownObject(R.drawable.ic_germany_flag, "German"));
                add(new DropDownObject(R.drawable.ic_spain_flag, "Spanish"));
                add(new DropDownObject(R.drawable.ic_portugal_flag, "Portuguese"));
            }
        };

        DropDownAdapter langAdapter = new DropDownAdapter(this, languages);
        langAdapter.setListener(this::applyChoice);
        ((Spinner) findViewById(R.id.spLang)).setAdapter(langAdapter);

        ((Spinner) findViewById(R.id.spLang)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                DropDownObject language = (DropDownObject) langAdapter.getItem(position);
                applyChoice(findViewById(R.id.tvLang), language, true);

                if (!language.getLabel().equalsIgnoreCase("English")) {
                    String errorMsg = getString(R.string.home_screen_lang_change_error_txt, language.getLabel());
                    Toast.makeText(SIDSplashActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void applyChoice(TextView tvLang, DropDownObject language, boolean isMain) {
        tvLang.setText(language.getLabel());
        Drawable left = getResources().getDrawable(language.getFlagResId());
        Drawable right = isMain ? getResources().getDrawable(R.drawable.ic_down_arrow) : null;
        tvLang.setCompoundDrawablesWithIntrinsicBounds(left, null, right, null);
    }

    public void move2HomeScreen(View view) {
        startActivity(new Intent(SIDSplashActivity.this, SIDHomeActivity.class));
    }

    public void move2AboutScreen(View view) {
        startActivity(new Intent(SIDSplashActivity.this, AboutDemoActivity.class));
    }

    public void chooseLanguage(View view) {
        findViewById(R.id.spLang).performClick();
    }
}
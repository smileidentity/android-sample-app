package com.demo.smileid.sid_sdk;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class GetStartedActivity extends AppCompatActivity {

    public static final String REQUIRE_CONSENT = "REQUIRE_CONSENT";
    private Bundle mParams = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_get_started);
        findViewById(R.id.ivBackArrow).setOnClickListener(v -> super.onBackPressed());

        mParams = getIntent().getExtras();
    }

    protected void requestUserConsent() {
        //To be replaced by a partner-set values as returned by the backend
        Intent intent = new Intent(this, ConsentActivity.class);
        intent.putExtra(ConsentActivity.TAG, "USER_TAG");
        intent.putExtra(ConsentActivity.PARTNER_LOGO, BitmapFactory.decodeResource(getResources(), R.drawable.ic_purse));
        intent.putExtra(ConsentActivity.PARTNER_NAME, "AM Loans Inc.");
        intent.putExtra(ConsentActivity.PRIVACY_LINK, "www.google.com");
        startActivityForResult(intent, 123);
    }

    public void getStarted(View view) {
        if ((mParams != null) && (mParams.getBoolean(REQUIRE_CONSENT))) {
            mParams.remove(REQUIRE_CONSENT);
            requestUserConsent();
        } else {
            moveForward();
        }
    }

    private void moveForward() {
        finish();

        startActivity(
            new Intent(this, SIDSelfieActivity.class) {
                {
                    putExtras(mParams);
                }
            }
        );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            moveForward();
        } else {
            Toast.makeText(this, getString(R.string.consent_screen_consent_declined_error),
                Toast.LENGTH_LONG).show();

            finish();
        }
    }
}
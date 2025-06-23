package com.example.music_zengmeilian.splash;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.example.music_zengmeilian.R;
import com.example.music_zengmeilian.home.MainActivity;

public class SplashActivity extends AppCompatActivity {
    private static final String TAG = "SplashActivity";
    private static final String PREFS_NAME = "AppPrefs";
    private static final String PRIVACY_AGREED = "privacy_agreed";
    private static final long MIN_SPLASH_DURATION = 2000;

    private long mStartTime;
    private boolean mIsActive = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        mStartTime = System.currentTimeMillis();
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkPrivacyStatus();
    }

    @Override
    protected void onDestroy() {
        mIsActive = false;
        super.onDestroy();
    }

    private void checkPrivacyStatus() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean privacyAgreed = prefs.getBoolean(PRIVACY_AGREED, false);

        long elapsed = System.currentTimeMillis() - mStartTime;
        long remaining = Math.max(0, MIN_SPLASH_DURATION - elapsed);

        new Handler().postDelayed(() -> {
            if (!mIsActive) return;

            if (privacyAgreed) {
                checkNetworkAndProceed();
            } else {
                showPrivacyDialog();
            }
        }, remaining);
    }

    private void showPrivacyDialog() {
        PrivacyDialogFragment dialog = new PrivacyDialogFragment();
        dialog.setPrivacyCallback(new PrivacyDialogFragment.PrivacyCallback() {
            @Override
            public void onAgree() {
                // 保存同意状态
                getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
                        .edit()
                        .putBoolean(PRIVACY_AGREED, true)
                        .apply();
                checkNetworkAndProceed();
            }

            @Override
            public void onDisagree() {
                finish();
            }
        });
        dialog.show(getSupportFragmentManager(), "PrivacyDialog");
    }

    private void checkNetworkAndProceed() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        if (activeNetwork != null && activeNetwork.isConnectedOrConnecting()) {
            proceedToMain();
        } else {
            showNetworkErrorDialog();
        }
    }

    private void showNetworkErrorDialog() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.network_error_title)
                .setMessage(R.string.network_error_message)
                .setPositiveButton(R.string.retry, (dialog, which) -> checkNetworkAndProceed())
                .setNegativeButton(R.string.exit, (dialog, which) -> finish())
                .setCancelable(false)
                .show();
    }

    private void proceedToMain() {
        if (isFinishing()) return;

        startActivity(new Intent(this, MainActivity.class));
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

        // 延迟关闭SplashActivity，确保转场动画完成
        new Handler().postDelayed(this::finish, 300);
    }
}
package com.example.music_zengmeilian.splash;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.music_zengmeilian.R;
import com.example.music_zengmeilian.home.MainActivity;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {
    private Button buttonUserAgree;
    private TextView textUserDisagree;
    private TextView textPrivacy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        init(); // 先初始化控件
        isFirstStart(); // 然后进行检查
    }

    private void init() {
        buttonUserAgree = findViewById(R.id.button_user_agree);
        textUserDisagree = findViewById(R.id.text_user_disgree);
        textPrivacy = findViewById(R.id.text_privacy);
    }

    /**
     * 判断是否是首次启动
     */
    public void isFirstStart() {
        SharedPreferences preferences = getSharedPreferences("NB_FIRST_START", 0);
        boolean isFirst = preferences.getBoolean("FIRST_START", true);
        if (isFirst) { // 第一次启动app
            showPrivacyDialog();
        } else { // 非第一次启动app
            preferences.edit().putBoolean("FIRST_START", false).apply();
            new Handler().postDelayed(() -> {
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                finish();
            }, 2000);
        }
    }

    private void showPrivacyDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.activity_privacy, null);
        builder.setView(view);
        builder.setCancelable(false);
        AlertDialog dialog = builder.show();
        dialog.getWindow().getDecorView().setBackground(null);

        // 设置声明条款内容的跳转
        String fullText = "欢迎使用音乐社区，我们将严格遵守相关法律和隐私政策保护您的个人隐私，请您阅读并同意《用户协议》与《隐私政策》。";
        SpannableString spannableString = new SpannableString(fullText);

        // 找到《用户协议》和《隐私政策》的位置
        int userAgreementStart = fullText.indexOf("《用户协议》");
        int userAgreementEnd = userAgreementStart + "《用户协议》".length();
        int privacyPolicyStart = fullText.indexOf("《隐私政策》");
        int privacyPolicyEnd = privacyPolicyStart + "《隐私政策》".length();

        // 设置《用户协议》的点击事件
        ClickableSpan userAgreementSpan = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.mi.com"));
                startActivity(browserIntent);
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(ds.linkColor);
                ds.setUnderlineText(false);
            }
        };

        // 设置《隐私政策》的点击事件
        ClickableSpan privacyPolicySpan = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.xiaomiev.com/"));
                startActivity(browserIntent);
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(ds.linkColor);
                ds.setUnderlineText(false);
            }
        };

        // 将ClickableSpan应用到文本
        spannableString.setSpan(userAgreementSpan, userAgreementStart, userAgreementEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(privacyPolicySpan, privacyPolicyStart, privacyPolicyEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        // 设置文本并启用点击
        textPrivacy.setText(spannableString);
        textPrivacy.setMovementMethod(LinkMovementMethod.getInstance());

        // 同意隐私政策
        buttonUserAgree.setOnClickListener(view1 -> {
            SharedPreferences preferences = getSharedPreferences("NB_FIRST_START", 0);
            preferences.edit().putBoolean("FIRST_START", false).apply();
            dialog.dismiss();
            startActivity(new Intent(SplashActivity.this, MainActivity.class));
            finish();
        });

        // 不同意隐私政策
        textUserDisagree.setOnClickListener(view1 -> {
            dialog.dismiss();
            finish();
        });
    }

    // 拦截返回键事件
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
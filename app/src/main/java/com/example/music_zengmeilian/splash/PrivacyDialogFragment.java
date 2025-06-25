package com.example.music_zengmeilian.splash;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.example.music_zengmeilian.R;

public class PrivacyDialogFragment extends DialogFragment {

    // 回调接口，用于通知用户选择（同意/不同意）
    public interface PrivacyCallback {
        void onAgree();
        void onDisagree();
    }

    private PrivacyCallback callback;
    private static final String PREFS_NAME = "AppPrefs"; // SharedPreferences文件名
    private static final String PRIVACY_AGREED = "privacy_agreed"; // 存储用户是否同意的键

    // 设置回调监听器
    public void setPrivacyCallback(PrivacyCallback callback) {
        this.callback = callback;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        // 创建对话框并设置透明背景、不可取消
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // 加载布局文件
        View view = inflater.inflate(R.layout.dialog_privacy, container, false);

        // 设置协议文本的超链接和蓝色样式
        TextView privacyText = view.findViewById(R.id.text_privacy);
        setupPrivacyTextLinks(privacyText);

        // 同意按钮点击事件
        view.findViewById(R.id.button_user_agree).setOnClickListener(v -> {
            savePrivacyAgreement(true); // 保存"已同意"状态
            if (callback != null) callback.onAgree();
            dismiss();
        });

        // 不同意按钮点击事件
        view.findViewById(R.id.text_user_disgree).setOnClickListener(v -> {
            savePrivacyAgreement(false); // 保存"未同意"状态
            if (callback != null) callback.onDisagree();
            dismiss();
        });

        return view;
    }

    // 保存用户选择状态到SharedPreferences
    private void savePrivacyAgreement(boolean agreed) {
        SharedPreferences prefs = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().putBoolean(PRIVACY_AGREED, agreed).apply();
    }

    // 静态方法：检查用户是否已同意隐私协议
    public static boolean hasUserAgreed(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getBoolean(PRIVACY_AGREED, false); // 默认返回false（未同意）
    }

    // 设置协议文本中的超链接和样式
    private void setupPrivacyTextLinks(TextView textView) {
        String fullText = getString(R.string.privacy_agreement_text);
        SpannableString spannable = new SpannableString(fullText);

        // 用户协议链接（《用户协议》）
        int userAgreementStart = fullText.indexOf("《用户协议》");
        if (userAgreementStart >= 0) {
            // 1. 设置点击事件
            spannable.setSpan(new ClickableSpan() {
                @Override
                public void onClick(@NonNull View widget) {
                    openUrl("https://www.mi.com"); // 跳转到用户协议链接
                }
            }, userAgreementStart, userAgreementStart + 6, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            // 2. 设置蓝色文字
            int colorBlue = ContextCompat.getColor(requireContext(), R.color.blue);
            spannable.setSpan(
                    new ForegroundColorSpan(colorBlue),
                    userAgreementStart,
                    userAgreementStart + 6,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            );
        }

        // 隐私政策链接（《隐私政策》）
        int privacyPolicyStart = fullText.indexOf("《隐私政策》");
        if (privacyPolicyStart >= 0) {
            // 1. 设置点击事件
            spannable.setSpan(new ClickableSpan() {
                @Override
                public void onClick(View widget) {
                    openUrl("https://www.xiaomiev.com/"); // 跳转到隐私政策链接
                }
            }, privacyPolicyStart, privacyPolicyStart + 6, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            // 2. 设置蓝色文字
            int colorBlue = ContextCompat.getColor(requireContext(), R.color.blue);
            spannable.setSpan(
                    new ForegroundColorSpan(colorBlue),
                    privacyPolicyStart,
                    privacyPolicyStart + 6,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            );
        }

        textView.setText(spannable);
        textView.setMovementMethod(LinkMovementMethod.getInstance()); // 启用链接点击
    }

    // 打开URL链接
    private void openUrl(String url) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
    }
}
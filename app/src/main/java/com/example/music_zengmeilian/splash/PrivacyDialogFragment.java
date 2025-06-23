package com.example.music_zengmeilian.splash;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.music_zengmeilian.R;

public class PrivacyDialogFragment extends DialogFragment {
    public interface PrivacyCallback {
        void onAgree();
        void onDisagree();
    }

    private PrivacyCallback callback;

    public void setPrivacyCallback(PrivacyCallback callback) {
        this.callback = callback;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        // 保持对话框样式不变
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_privacy, container, false);

        // 设置协议文本点击
        TextView privacyText = view.findViewById(R.id.text_privacy);
        setupPrivacyTextLinks(privacyText);

        // 同意按钮
        view.findViewById(R.id.button_user_agree).setOnClickListener(v -> {
            if (callback != null) callback.onAgree();
            dismiss();
        });

        // 不同意按钮
        view.findViewById(R.id.text_user_disgree).setOnClickListener(v -> {
            if (callback != null) callback.onDisagree();
            dismiss();
        });

        return view;
    }

    private void setupPrivacyTextLinks(TextView textView) {
        String fullText = textView.getText().toString();
        SpannableString spannable = new SpannableString(fullText);

        // 用户协议链接
        int userAgreementStart = fullText.indexOf("《用户协议》");
        if (userAgreementStart >= 0) {
            spannable.setSpan(new ClickableSpan() {
                @Override
                public void onClick(View widget) {
                    openUrl("https://www.mi.com");
                }
            }, userAgreementStart, userAgreementStart + 6, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        // 隐私协议链接
        int privacyPolicyStart = fullText.indexOf("《隐私政策》");
        if (privacyPolicyStart >= 0) {
            spannable.setSpan(new ClickableSpan() {
                @Override
                public void onClick(View widget) {
                    openUrl("https://www.xiaomiev.com/");
                }
            }, privacyPolicyStart, privacyPolicyStart + 6, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        textView.setText(spannable);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private void openUrl(String url) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
    }
}
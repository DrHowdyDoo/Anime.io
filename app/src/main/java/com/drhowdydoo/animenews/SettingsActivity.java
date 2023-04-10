package com.drhowdydoo.animenews;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.drhowdydoo.animenews.databinding.ActivitySettingsBinding;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivitySettingsBinding binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        SharedPreferences preferences = getSharedPreferences("com.drhowdydoo.preferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        boolean syncOnStart = preferences.getBoolean("com.drhowdydoo.settings.syncOnStart",false);
        binding.switchSyncOnStart.setChecked(syncOnStart);

        binding.switchSyncOnStart.setOnCheckedChangeListener((buttonView, isChecked) -> editor.putBoolean("com.drhowdydoo.settings.syncOnStart",isChecked).apply());

        int savedLimitId = preferences.getInt(getString(R.string.settings_article_limit),0);
        int checkedLimit = 40;
        if (savedLimitId == 1) checkedLimit = 50;
        else if (savedLimitId == 2) checkedLimit = 60;

        binding.txtSubtitleArticleLimit.setText(checkedLimit + " articles");

        CharSequence[] articleLimits = {"40","50","60"};
        binding.articleLimit.setOnClickListener(v -> {
                int checkedLimitId = preferences.getInt(getString(R.string.settings_article_limit),0);
                new MaterialAlertDialogBuilder(this)
                .setTitle("Limit")
                .setSingleChoiceItems(articleLimits, checkedLimitId, (dialog, which) -> {
                    editor.putInt(getString(R.string.settings_article_limit),which).apply();
                    int item = which == 0 ? 40 : which == 1 ? 50 : 60;
                    binding.txtSubtitleArticleLimit.setText(item + " articles");
                    dialog.dismiss();
                })
                .show();
        });
    }
}
package com.drhowdydoo.animenews;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.drhowdydoo.animenews.databinding.ActivitySettingsBinding;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class SettingsActivity extends AppCompatActivity {

    private static final DateTimeFormatter format = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy h:mm a");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivitySettingsBinding binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        SharedPreferences preferences = getSharedPreferences("com.drhowdydoo.preferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        String lastDBCleanUpTime = preferences.getString("com.drhowdydoo.service.dbCleanUpTime","");
        LocalDateTime date = LocalDateTime.parse(lastDBCleanUpTime,DateTimeFormatter.ISO_LOCAL_DATE_TIME);

        binding.txtDbCleanupTime.setText("Last database cleanup at " + date.format(format));

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
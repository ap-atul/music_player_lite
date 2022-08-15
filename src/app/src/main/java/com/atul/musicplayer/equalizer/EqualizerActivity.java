//package com.atul.musicplayerlite.equalizer;
//
//import android.os.Bundle;
//import android.widget.Toast;
//
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.appcompat.app.AppCompatDelegate;
//
//import com.atul.musicplayerlite.MPPreferences;
//import com.atul.musicplayerlite.R;
//import com.atul.musicplayerlite.helper.ThemeHelper;
////import com.bullhead.equalizer.EqualizerFragment;
//
//public class EqualizerActivity extends AppCompatActivity {
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setTheme(ThemeHelper.getTheme(MPPreferences.getTheme(getApplicationContext())));
//        AppCompatDelegate.setDefaultNightMode(MPPreferences.getThemeMode(getApplicationContext()));
//        setContentView(R.layout.activity_equalizer);
//
//        int sessionId = getIntent().getIntExtra("session", -1);
//
//        if (sessionId != -1) {
//            EqualizerFragment equalizerFragment = EqualizerFragment.newBuilder()
//                    .setAccentColor(ThemeHelper.resolveColorAttr(this, R.attr.colorPrimary))
//                    .setAudioSessionId(sessionId)
//                    .build();
//
//            getSupportFragmentManager().beginTransaction()
//                    .replace(R.id.eqFrame, equalizerFragment)
//                    .commit();
//        } else {
//            Toast.makeText(this, "Failed to launch", Toast.LENGTH_SHORT).show();
//        }
//    }
//}
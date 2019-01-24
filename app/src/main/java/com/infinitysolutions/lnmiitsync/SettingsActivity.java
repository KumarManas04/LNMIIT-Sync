package com.infinitysolutions.lnmiitsync;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import static com.infinitysolutions.lnmiitsync.Contract.ValuesContract.SHARED_PREF_NAME;
import static com.infinitysolutions.lnmiitsync.Contract.ValuesContract.SHARED_PREF_NOTIFY_BEFORE;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        final EditText editText = findViewById(R.id.notify_before_time);
        final SharedPreferences sharedPrefs = getSharedPreferences(SHARED_PREF_NAME,MODE_PRIVATE);
        int num = sharedPrefs.getInt(SHARED_PREF_NOTIFY_BEFORE,10);
        editText.setText(Integer.toString(num));

        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_DONE){
                    try {
                        int num = Integer.parseInt(editText.getText().toString());
                        SharedPreferences.Editor editor = sharedPrefs.edit();
                        editor.putInt(SHARED_PREF_NOTIFY_BEFORE,num);
                        editor.commit();
                        Toast.makeText(SettingsActivity.this, "Done", Toast.LENGTH_SHORT).show();
                    }catch (NumberFormatException e){
                        Toast.makeText(SettingsActivity.this, "Please enter a number", Toast.LENGTH_SHORT).show();
                    }
                    return true;
                }
                return false;
            }
        });
    }

    public void backPress(View view){
        EditText editText = findViewById(R.id.notify_before_time);
        try {
            int num = Integer.parseInt(editText.getText().toString());
            SharedPreferences sharedPrefs = getSharedPreferences(SHARED_PREF_NAME,MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPrefs.edit();
            editor.putInt(SHARED_PREF_NOTIFY_BEFORE,num);
            editor.commit();
        }catch (NumberFormatException e){
            e.printStackTrace();
        }
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        EditText editText = findViewById(R.id.notify_before_time);
        try {
            int num = Integer.parseInt(editText.getText().toString());
            SharedPreferences sharedPrefs = getSharedPreferences(SHARED_PREF_NAME,MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPrefs.edit();
            editor.putInt(SHARED_PREF_NOTIFY_BEFORE,num);
            editor.commit();
        }catch (NumberFormatException e){
            e.printStackTrace();
        }
    }
}
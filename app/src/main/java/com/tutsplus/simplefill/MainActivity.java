package com.tutsplus.simplefill;

import android.content.SharedPreferences;
import android.support.v4.content.SharedPreferencesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import java.util.HashSet;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void saveEmailAddresses(View view) {

        // Get both email addresses
        String primaryEmailAddress = ((EditText)findViewById(R.id.primary))
                                    .getText().toString();
        String secondaryEmailAddress = ((EditText)findViewById(R.id.secondary))
                .getText().toString();

        // Save them using SharedPreferences
        SharedPreferences.Editor editor = getSharedPreferences("EMAIL_STORAGE", MODE_PRIVATE)
                                                        .edit();
        editor.putString("PRIMARY_EMAIL", primaryEmailAddress);
        editor.putString("SECONDARY_EMAIL", secondaryEmailAddress);
        editor.commit();
    }
}

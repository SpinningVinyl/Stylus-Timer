package net.prsv.stimer;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class PreferencesActivity extends STimerBaseActivity {

    private final STimerPreferences preferences = STimerPreferences.getInstance();

    private EditText etCustomSide, etCustomLP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences);

        // find the widgets
        etCustomSide = findViewById(R.id.etCustomSide);
        etCustomLP = findViewById(R.id.etCustomLP);
        Button btnSavePreferences = findViewById(R.id.btnSavePreferences);

        // initialize EditText values
        etCustomSide.setText(String.valueOf(preferences.getCustomSide()));
        etCustomLP.setText(String.valueOf(preferences.getCustomLP()));

        //set the onClick listener
        btnSavePreferences.setOnClickListener(this::savePreferences);

        // set up the action bar and enable the back button
        Toolbar toolbar = findViewById(R.id.toolbar_preferences);
        setSupportActionBar(toolbar);
        ActionBar supportActionBar = getSupportActionBar();
        assert supportActionBar != null;
        supportActionBar.setDisplayHomeAsUpEnabled(true);
        supportActionBar.setDisplayShowHomeEnabled(true);
        supportActionBar.setTitle(R.string.preferences);

    }

    private void savePreferences(View v) {
        // grab the values from the EditText widgets
        String customSideString = etCustomSide.getText().toString();
        if (!STimerApp.isInteger(customSideString)) {
            customSideString = "0";
        }
        int customSide = Integer.parseInt(customSideString);

        String customLpString = etCustomLP.getText().toString();
        if (!STimerApp.isInteger(customLpString)) {
            customLpString = "0";
        }
        int customLP = Integer.parseInt(customLpString);

        // save preferences
        preferences.setCustomSide(customSide);
        preferences.setCustomLP(customLP);
        finish();
    }


    /**
     * Return to the main activity when the user taps the back button on the action bar.
     */
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}
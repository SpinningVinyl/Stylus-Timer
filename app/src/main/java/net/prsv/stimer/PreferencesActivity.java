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
        int customSide = Math.max(Integer.parseInt(etCustomSide.getText().toString()), 0);
        int customLP = Math.max(Integer.parseInt(etCustomLP.getText().toString()), 0);
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
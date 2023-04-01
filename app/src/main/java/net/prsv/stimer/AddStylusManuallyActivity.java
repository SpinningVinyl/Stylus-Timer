package net.prsv.stimer;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;

public class AddStylusManuallyActivity extends STimerBaseActivity {

    ArrayList<StylusProfile> profileList = new ArrayList<>();
    private Spinner spinnerProfiles;
    private EditText etCartridgeName, etTrackingForce, etCustomThreshold;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_stylus_manually);

        // set up the action bar and enable the back button
        Toolbar toolbar = findViewById(R.id.add_manually_toolbar);
        setSupportActionBar(toolbar);
        ActionBar supportActionBar = getSupportActionBar();
        assert supportActionBar != null;
        supportActionBar.setDisplayHomeAsUpEnabled(true);
        supportActionBar.setDisplayShowHomeEnabled(true);
        supportActionBar.setTitle(R.string.add_new_cartridge_title);

        //get widgets
        spinnerProfiles = findViewById(R.id.spinnerProfiles);
        etCartridgeName = findViewById(R.id.etCartridgeName);
        etTrackingForce = findViewById(R.id.etTrackingForce);
        etCustomThreshold = findViewById(R.id.etCustomThreshold);

        // get a list of stylus profiles and populate the spinner
        try(DataHelper helper = new DataHelper()) {
            profileList = helper.getProfiles();
        }
        if (profileList.size() > 0) {
            Collections.sort(profileList);
            ArrayAdapter<StylusProfile> profileAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, profileList);
            profileAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerProfiles.setAdapter(profileAdapter);
            Button btnSave = findViewById(R.id.btnSave);
            btnSave.setOnClickListener(this::saveStylus);
        } else {
            Toast.makeText(this, R.string.error_getting_profiles, Toast.LENGTH_LONG).show();
        }


    }

    /**
     * Return to the main activity when the user taps the back button on the action bar.
     */
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void saveStylus(View v) {
        String name = etCartridgeName.getText().toString();
        if (name.trim().equals("")) {
            Toast.makeText(this, R.string.error_name_empty, Toast.LENGTH_LONG).show();
            return;
        }
        double trackingForce = Double.parseDouble(etTrackingForce.getText().toString());
        int customThreshold = Math.max(Integer.parseInt(etCustomThreshold.getText().toString()), 0);
        StylusProfile profile = (StylusProfile) spinnerProfiles.getSelectedItem();
        assert profile != null;
        int profileId = profile.getId();
        Stylus stylus = new Stylus(-1, name, profileId, trackingForce, customThreshold);
        try(DataHelper helper = new DataHelper()) {
            stylus.setId(helper.insertStylus(stylus));
        }
        Intent returnDataIntent = new Intent();
        returnDataIntent.putExtra(MainActivity.STYLUS_ID_RETURN_KEY, stylus.getId());
        setResult(RESULT_OK, returnDataIntent);
        finish();
    }

}
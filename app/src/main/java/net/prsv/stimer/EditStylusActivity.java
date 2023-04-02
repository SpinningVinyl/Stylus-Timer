package net.prsv.stimer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;

public class EditStylusActivity extends STimerBaseActivity {

    private Stylus stylus;
    private StylusProfile profile;
    private int adapterPosition;

    private EditText etPropsCustomThreshold;
    private TextView tvCartridgePropsName, tvStylusPropsProfile, tvStylusPropsUsage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_stylus);

        // set up the action bar but do not enable the back button
        Toolbar toolbar = findViewById(R.id.stylus_props_toolbar);
        setSupportActionBar(toolbar);
        ActionBar supportActionBar = getSupportActionBar();
        assert supportActionBar != null;
        supportActionBar.setTitle(R.string.edit_cartridge_title);

        // find the widgets
        Button btnResetHours = findViewById(R.id.btnResetHours);
        Button btnSetCustomThreshold = findViewById(R.id.btnSetCustomThreshold);
        Button btnDeleteCartridge = findViewById(R.id.btnDeleteCartridge);
        Button btnReturn = findViewById(R.id.btnReturn);
        etPropsCustomThreshold = findViewById(R.id.etPropsCustomThreshold);
        tvCartridgePropsName = findViewById(R.id.tvCartridgePropsName);
        tvStylusPropsProfile = findViewById(R.id.tvStylusPropsProfile);
        tvStylusPropsUsage = findViewById(R.id.tvStylusPropsUsage);

        // set listeners
        btnResetHours.setOnClickListener(this::resetHours);
        btnReturn.setOnClickListener(this::returnToMain);
        btnDeleteCartridge.setOnClickListener(this::deleteCartridge);
        btnSetCustomThreshold.setOnClickListener(this::setCustomThreshold);

        // get data from the caller
        Intent intent = getIntent();
        int stylusID = intent.getIntExtra(MainActivity.STYLUS_ID_KEY,-1);
        adapterPosition = intent.getIntExtra(MainActivity.ADAPTER_POSITION_KEY, -1);

        try(DataHelper helper = new DataHelper()) {
            stylus = helper.getStylus(stylusID);
            assert stylus != null;
            profile = helper.getProfile(stylus.getProfileId());
            assert profile != null;
        }

        initDisplay();

    }

    private void initDisplay() {
        tvCartridgePropsName.setText(stylus.getName());
        tvStylusPropsUsage.setText(String.format(getString(R.string.tv_props_usage), stylus.getHours()));
        tvStylusPropsProfile.setText(String.format(getString(R.string.tv_profile), profile.toString()));
        etPropsCustomThreshold.setText(String.valueOf(stylus.getCustomThreshold()));
    }

    private void resetHours(View v) {
        stylus.setHours(0);
        try(DataHelper helper = new DataHelper()) {
            helper.updateStylus(stylus);
        }
        initDisplay();
    }

    private void deleteCartridge(View v) {
        try(DataHelper helper = new DataHelper()) {
            helper.deleteStylus(stylus.getId());
            Intent returnDataIntent = new Intent();
            returnDataIntent.putExtra(MainActivity.RETURN_RESULT_KEY, MainActivity.RESULT_DELETE_STYLUS);
            returnDataIntent.putExtra(MainActivity.ADAPTER_POSITION_KEY, adapterPosition);
            setResult(RESULT_OK, returnDataIntent);
            finish();
        }
    }

    private void setCustomThreshold(View v) {
        int customThreshold = Integer.parseInt(etPropsCustomThreshold.getText().toString());
        stylus.setCustomThreshold(customThreshold);
        try(DataHelper helper = new DataHelper()) {
            helper.updateStylus(stylus);
        }
        initDisplay();
    }

    private void returnToMain(View v) {
        Intent returnDataIntent = new Intent();
        returnDataIntent.putExtra(MainActivity.RETURN_RESULT_KEY, MainActivity.RESULT_EDIT_STYLUS);
        returnDataIntent.putExtra(MainActivity.ADAPTER_POSITION_KEY, adapterPosition);
        returnDataIntent.putExtra(MainActivity.STYLUS_ID_KEY, stylus.getId());
        setResult(RESULT_OK, returnDataIntent);
        finish();
    }

}
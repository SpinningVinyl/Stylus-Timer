package net.prsv.stimer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;

public class AddStylusFromListActivity extends STimerBaseActivity {

    private final ArrayList<Stylus> stylusList = new ArrayList<>();
    private Spinner spinnerStyli;
    private Button btnOk;

    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_stylus_from_list);

        // grab the context -- used in fetchCartridgeData()
        mContext = this;

        // set up the action bar and enable the back button
        Toolbar toolbar = findViewById(R.id.add_from_list_toolbar);
        setSupportActionBar(toolbar);
        ActionBar supportActionBar = getSupportActionBar();
        assert supportActionBar != null;
        supportActionBar.setDisplayHomeAsUpEnabled(true);
        supportActionBar.setDisplayShowHomeEnabled(true);
        supportActionBar.setTitle(R.string.add_new_cartridge_title);

        // set up the UI widgets
        spinnerStyli = findViewById(R.id.spinnerProfiles);
        btnOk = findViewById(R.id.btnOk);

        fetchCartridgeData();

    }

    private void fetchCartridgeData() {
        final AtomicBoolean success = new AtomicBoolean(false);

        // get the Firebase RTDB reference
        DatabaseReference db = FirebaseDatabase.getInstance().getReference().child(STimerPreferences.RTDB_CARTRIDGES_PATH);

        // create a new value event listener
        ValueEventListener dataFetchListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot result) {
                success.set(true);
                for (DataSnapshot child : result.getChildren()) {
                    Stylus stylus = child.getValue(Stylus.class);
                    assert stylus != null;
                    stylusList.add(stylus);
                }
                // sort the list alphabetically
                Collections.sort(stylusList);
                // use an ArrayAdapter to show items from the list in the spinner
                ArrayAdapter<Stylus> spinnerAdapter = new ArrayAdapter<>(AddStylusFromListActivity.this, android.R.layout.simple_spinner_item, stylusList);
                spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerStyli.setAdapter(spinnerAdapter);
                // set the even listener for the OK button
                btnOk.setOnClickListener(v -> returnData(v));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                success.set(false);
            }
        };

        // attach the listener to the database reference
        db.addListenerForSingleValueEvent(dataFetchListener);

        // the following extremely ugly code is necessary to handle Firebase timeouts
        // because Google did not bother to provide appropriate solutions in their API
        Timer timeoutTimer = new Timer();
        TimerTask timeoutTask = new TimerTask() {
            @Override
            public void run() {
                timeoutTimer.cancel();
                if (!success.get()) {
                    // remove the listener from the database reference
                    db.removeEventListener(dataFetchListener);
                    // can't create a UI element from a non-UI thread, offload to the main looper
                    ContextCompat.getMainExecutor(mContext).execute(() ->
                    Toast.makeText(mContext, R.string.download_error, Toast.LENGTH_LONG).show());
                }
            }
        };
        // set timer delay to 2s
        timeoutTimer.schedule(timeoutTask, 2000L);
    }

    public void returnData(View v) {
        Stylus stylus = (Stylus) spinnerStyli.getSelectedItem();
        int returnValue = -1;
        Intent returnDataIntent = new Intent();
        try(DataHelper helper = new DataHelper()) {
            returnValue = helper.insertStylus(stylus);
        }
        returnDataIntent.putExtra(MainActivity.STYLUS_ID_KEY, returnValue);
        setResult(RESULT_OK, returnDataIntent);
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
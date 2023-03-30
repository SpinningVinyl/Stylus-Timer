package net.prsv.stimer;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MainActivity extends STimerBaseActivity {

    private RecyclerView recycler;
    private StylusViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // set the custom toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);

        // find the RecyclerView
        recycler = findViewById(R.id.stylusView);

        // grab the Preferences object
        STimerPreferences preferences = STimerPreferences.getInstance();

        // if initial setup is not complete, call the InitialSetupActivity
        if (!preferences.isSetupComplete()) {

            Intent initialSetupIntent = new Intent(this, InitialSetupActivity.class);
            startActivity(initialSetupIntent);

        } else {
            prepareStylusViewAdapter();
        }

    }

    private void prepareStylusViewAdapter() {
        try(DataHelper helper = new DataHelper()) {
            ArrayList<StylusProfile> profiles = helper.getProfiles();

            ArrayList<Stylus> styli = helper.getAllStyli();

            adapter = new StylusViewAdapter(this, styli, profiles);
            recycler.setAdapter(adapter);
            recycler.setLayoutManager(new LinearLayoutManager(getBaseContext()));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.app_bar, menu);
        return true;
    }

    @SuppressLint("NotifyDataSetChanged")
    private void resetStylusData() {
        adapter.clearDataSet();
        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_add) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
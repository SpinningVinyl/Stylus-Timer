package net.prsv.stimer;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MainActivity extends STimerBaseActivity {

    private RecyclerView recycler;
    private StylusViewAdapter adapter;
    private ActivityResultLauncher<Intent> addStylusLauncher;

    public static String STYLUS_ID_RETURN_KEY = "STYLUS_ID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // set the custom toolbar
        Toolbar toolbar = findViewById(R.id.main_toolbar);
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

        // register an activity result callback for adding styli
        addStylusLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), this::addStylusResultHandler);

    }

    @SuppressLint("NotifyDataSetChanged")
    private void addStylusResultHandler(@NonNull ActivityResult result) {
        if (result.getResultCode() == Activity.RESULT_OK) {
            Intent returnIntent = result.getData();
            assert returnIntent != null;
            int returnValue = returnIntent.getIntExtra(STYLUS_ID_RETURN_KEY, -1);
            if (returnValue == -1) {
                Toast.makeText(this, R.string.error_adding_cartridge, Toast.LENGTH_LONG).show();
            } else {
                try (DataHelper helper = new DataHelper()) {
                    Stylus stylus = helper.getStylusById(returnValue);
                    adapter.addToDataSet(stylus);
                    adapter.notifyItemInserted(adapter.getItemCount() - 1);
                }
            }

        }
    }

    private void prepareStylusViewAdapter() {
        try(DataHelper helper = new DataHelper()) {
            ArrayList<StylusProfile> profiles = helper.getProfiles();

            ArrayList<Stylus> styli = helper.getAllStyli();


            adapter = new StylusViewAdapter(this, styli, profiles);
            recycler.setAdapter(adapter);
            recycler.setLayoutManager(new LinearLayoutManager(getBaseContext()));
            if (styli.size() == 0) {
                Toast.makeText(this, R.string.add_cartridge, Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.app_bar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_add) {

            // create a new pop-up dialog
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
            dialogBuilder.setTitle(R.string.add_new_cartridge_title);

            // create a text view for displaying the message
            final TextView addStylusMessage = new TextView(this);
            addStylusMessage.setPadding(64,32,32,32);
            addStylusMessage.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            addStylusMessage.setText(R.string.add_new_cartridge_dialog_message);

            // put the text view inside the dialog
            dialogBuilder.setView(addStylusMessage);


            // "Add manually" button + event handler
            dialogBuilder.setPositiveButton(R.string.button_add_manually, (dialog, which) -> {

            });

            // "Select from list" button + event handler
            dialogBuilder.setNeutralButton(R.string.button_select_from_list, (dialog, which) -> {
                Intent selectFromListIntent = new Intent(this, AddStylusFromListActivity.class);
                addStylusLauncher.launch(selectFromListIntent);
            });

            // show the dialog
            dialogBuilder.show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
package net.prsv.stimer;

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

public class MainActivity extends STimerBaseActivity implements EditStylusClickListener {

    private RecyclerView recycler;
    private StylusViewAdapter adapter;
    private ActivityResultLauncher<Intent> addLauncher, editLauncher;

    public static String STYLUS_ID_KEY = "STYLUS_ID";
    public static String ADAPTER_POSITION_KEY = "ADAPTER_POSITION";
    public static String RETURN_RESULT_KEY = "RETURN_RESULT";
    public static int RESULT_DELETE_STYLUS = 101;
    public static int RESULT_EDIT_STYLUS = 102;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // set the custom toolbar
        Toolbar toolbar = findViewById(R.id.toolbar_main);
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
        addLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), this::addResultHandler);
        editLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), this::editResultHandler);
    }

    private void addResultHandler(@NonNull ActivityResult result) {
        if (result.getResultCode() == Activity.RESULT_OK) {
            Intent returnIntent = result.getData();
            assert returnIntent != null;
            int stylusID = returnIntent.getIntExtra(STYLUS_ID_KEY, -1);
            if (stylusID == -1) {
                Toast.makeText(this, R.string.error_adding_cartridge, Toast.LENGTH_LONG).show();
            } else {
                try (DataHelper helper = new DataHelper()) {
                    Stylus stylus = helper.getStylus(stylusID);
                    adapter.addToDataSet(stylus);
                    adapter.notifyItemInserted(adapter.getItemCount() - 1);
                }
            }
        }
    }

    private void editResultHandler(@NonNull ActivityResult result) {
        if(result.getResultCode() == Activity.RESULT_OK) {
            Intent returnIntent = result.getData();
            assert returnIntent != null;
            int returnCode = returnIntent.getIntExtra(RETURN_RESULT_KEY, -1);
            int adapterPosition = returnIntent.getIntExtra(ADAPTER_POSITION_KEY, -1);
            if (returnCode == RESULT_EDIT_STYLUS) {
                try(DataHelper helper = new DataHelper()) {
                    int stylusId = returnIntent.getIntExtra(STYLUS_ID_KEY, -1);
                    adapter.replaceItemInDataSet(adapterPosition, helper.getStylus(stylusId));
                    adapter.notifyItemChanged(adapterPosition);
                }
            } else if (returnCode == RESULT_DELETE_STYLUS) {
                adapter.removeFromDataSet(adapterPosition);
                adapter.notifyItemRemoved(adapterPosition);
            }
        }
    }

    private void prepareStylusViewAdapter() {
        try(DataHelper helper = new DataHelper()) {
            ArrayList<StylusProfile> profiles = helper.getAllProfiles();

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
        // user selects the + option
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
                Intent addManuallyIntent = new Intent(this, AddStylusManuallyActivity.class);
                addLauncher.launch(addManuallyIntent);
            });

            // "Select from list" button + event handler
            dialogBuilder.setNeutralButton(R.string.button_select_from_list, (dialog, which) -> {
                Intent selectFromListIntent = new Intent(this, AddStylusFromListActivity.class);
                addLauncher.launch(selectFromListIntent);
            });

            // show the dialog
            dialogBuilder.show();
            return true;
        } else if (item.getItemId() == R.id.action_preferences) { // user selects App preferences
            Intent preferencesIntent = new Intent(this, PreferencesActivity.class);
            startActivity(preferencesIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void callEditStylusActivity(int stylusId, int adapterPosition) {
        Intent editStylusIntent = new Intent(this, EditStylusActivity.class);
        editStylusIntent.putExtra(STYLUS_ID_KEY, stylusId);
        editStylusIntent.putExtra(ADAPTER_POSITION_KEY, adapterPosition);
        editLauncher.launch(editStylusIntent);
    }
}
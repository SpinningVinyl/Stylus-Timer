package net.prsv.stimer;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class MainActivity extends STimerBaseActivity {

    private RecyclerView recycler;
    private StylusViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);

        recycler = findViewById(R.id.stylusView);

        STimerPreferences preferences = STimerPreferences.getInstance();
        if (!preferences.isSetupComplete()) {

            Toast.makeText(STimerApp.getContext(), "Performing initial setup", Toast.LENGTH_SHORT).show();

            DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
            try(DataHelper helper = new DataHelper()) {
                dbRef.child("Profiles").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if (!task.isSuccessful()) {
                            Log.e("firebase", "Error getting data", task.getException());
                        } else {
                            DataSnapshot result = task.getResult();
                            for (DataSnapshot child : result.getChildren()) {
                                StylusProfile profile = child.getValue(StylusProfile.class);
                                assert profile != null;
                                helper.insertProfile(profile);
                            }
                            preferences.completeSetup();
                            prepareStylusViewAdapter();
                        }
                    }
                });
            }
        } else {
            prepareStylusViewAdapter();
        }






    }

    private void prepareStylusViewAdapter() {
        try(DataHelper helper = new DataHelper()) {
            ArrayList<StylusProfile> profiles = helper.getProfiles();
            // ArrayList<Stylus> styli = helper.getAllStyli();

            // setting up some test data
            ArrayList<Stylus> styli = new ArrayList<>();
            Stylus testStylus1 = new Stylus(1, "Audio-Technica AT-VM95ML", 4, 2.0, 0);
            testStylus1.setHours(320.35);
            Stylus testStylus2 = new Stylus(2, "Denon DL-103R", 1, 2.75, 0);
            testStylus2.setHours(281.7);
            Stylus testStylus3 = new Stylus(2, "Shure M75ED T2", 2, 1.25, 0);
            testStylus3.setHours(230);
            styli.add(testStylus1);
            styli.add(testStylus2);
            styli.add(testStylus3);

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


}
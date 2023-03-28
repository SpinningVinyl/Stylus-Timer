package net.prsv.stimer;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends STimerBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);

        RecyclerView recycler = findViewById(R.id.stylusView);
        ArrayList<Stylus> mStyli = new ArrayList<>();


        Stylus testStylus1 = new Stylus(1, "Audio-Technica AT-VM95ML", 4, 2.0, 0);
        testStylus1.setHours(320.35);

        Stylus testStylus2 = new Stylus(2, "Denon DL-103R", 1, 2.75, 0);
        testStylus2.setHours(281.7);

        Stylus testStylus3 = new Stylus(2, "Shure M75ED T2", 2, 1.25, 0);
        testStylus3.setHours(230);

        mStyli.add(testStylus1);
        mStyli.add(testStylus2);
        mStyli.add(testStylus3);

        ArrayList<StylusProfile> mProfiles = new ArrayList<>();

        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();

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
                        Log.d("firebase", profile.toString());
                    }

                }

            }
        });

        mProfiles.add(new StylusProfile(1, "Spherical", 300));
        mProfiles.add(new StylusProfile(2, "Elliptical", 450));
        mProfiles.add(new StylusProfile(3, "Shibata", 500));
        mProfiles.add(new StylusProfile(4, "Micro Linear", 750));
        mProfiles.add(new StylusProfile(5, "Hyperelliptical", 500));
        mProfiles.add(new StylusProfile(6, "FG I/FG S", 1000));



        StylusViewAdapter adapter = new StylusViewAdapter(this, mStyli, mProfiles);
        recycler.setAdapter(adapter);
        recycler.setLayoutManager(new LinearLayoutManager(getBaseContext()));



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.app_bar, menu);
        return true;
    }


}
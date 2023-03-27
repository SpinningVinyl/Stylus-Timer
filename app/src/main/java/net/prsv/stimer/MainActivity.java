package net.prsv.stimer;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
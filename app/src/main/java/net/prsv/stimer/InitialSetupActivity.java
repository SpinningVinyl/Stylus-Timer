package net.prsv.stimer;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;

public class InitialSetupActivity extends STimerBaseActivity {

    private Button btnContinue;
    private TextView tvWelcomeMessage1, tvWelcomeMessage2;
    private Context mContext;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initial_setup);

        mContext = this;

        btnContinue = findViewById(R.id.btnContinue);
        tvWelcomeMessage1 = findViewById(R.id.tvWelcomeMessage1);
        tvWelcomeMessage2 = findViewById(R.id.tvWelcomeMessage2);
        ImageView ivSplash = findViewById(R.id.ivSplash);
        ivSplash.setClipToOutline(true);

        btnContinue.setOnClickListener(this::performSetup);

    }

    private void performSetup(View v) {
        final AtomicBoolean success = new AtomicBoolean(false);
        // grab the preferences object
        STimerPreferences preferences = STimerPreferences.getInstance();
        DatabaseReference db = FirebaseDatabase.getInstance().getReference().child(STimerPreferences.RTDB_PROFILES_PATH);
        ValueEventListener dataFetchListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot result) {
                try (DataHelper helper = new DataHelper()) {
                    success.set(true);
                    for (DataSnapshot child : result.getChildren()) {
                        StylusProfile profile = child.getValue(StylusProfile.class);
                        assert profile != null;
                        helper.insertProfile(profile);
                    }
                    preferences.completeSetup();
                    tvWelcomeMessage1.setText(getString(R.string.setup_complete_1));
                    tvWelcomeMessage2.setText(getString(R.string.setup_complete_2));
                }
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
        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                timer.cancel();
                if (!success.get()) {
                    // remove the listener from the database reference
                    db.removeEventListener(dataFetchListener);
                    // can't manipulate UI elements from a non-UI thread, offload to the main looper
                    ContextCompat.getMainExecutor(mContext).execute(() -> {
                        tvWelcomeMessage1.setTextColor(getColor(R.color.red));
                        tvWelcomeMessage1.setText(getString(R.string.setup_error_1));
                        tvWelcomeMessage2.setText(getString(R.string.setup_error_2));
                    }
                    );
                }
            }
        };
        // set the timer delay to 1s
        timer.schedule(timerTask, 1000L);
        btnContinue.setText(getString(R.string.button_restart));
        btnContinue.setOnClickListener(this::restartApp);

    }

    private void restartApp(View v) {
        STimerApp.restart();
    }
}
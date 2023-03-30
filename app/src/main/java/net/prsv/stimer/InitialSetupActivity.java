package net.prsv.stimer;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class InitialSetupActivity extends STimerBaseActivity {

    Button btnContinue;
    TextView tvWelcomeMessage1, tvWelcomeMessage2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initial_setup);

        btnContinue = findViewById(R.id.btnContinue);
        tvWelcomeMessage1 = findViewById(R.id.tvWelcomeMessage1);
        tvWelcomeMessage2 = findViewById(R.id.tvWelcomeMessage2);

        btnContinue.setOnClickListener(this::performSetup);

    }

    private void performSetup(View v) {
        STimerPreferences preferences = STimerPreferences.getInstance();

        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
        try(DataHelper helper = new DataHelper()) {
            dbRef.child("Profiles").get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DataSnapshot result = task.getResult();
                    for (DataSnapshot child : result.getChildren()) {
                        StylusProfile profile = child.getValue(StylusProfile.class);
                        assert profile != null;
                        helper.insertProfile(profile);
                    }
                    preferences.completeSetup();
                    tvWelcomeMessage1.setText(getString(R.string.setup_complete_1));
                    tvWelcomeMessage2.setText(getString(R.string.setup_complete_2));

                } else {
                    tvWelcomeMessage1.setText(getString(R.string.setup_error_1));
                    tvWelcomeMessage2.setText(getString(R.string.setup_error_2));
                }
                btnContinue.setText(getString(R.string.button_restart));
                btnContinue.setOnClickListener(this::restartApp);
            });
        }
    }

    private void restartApp(View v) {
        STimerApp.restart();
    }
}
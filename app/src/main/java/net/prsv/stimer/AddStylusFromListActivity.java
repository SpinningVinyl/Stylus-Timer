package net.prsv.stimer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Collections;

public class AddStylusFromListActivity extends STimerBaseActivity {

    private final ArrayList<Stylus> stylusList = new ArrayList<>();
    private Spinner spinnerStyli;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_stylus_from_list);

        spinnerStyli = findViewById(R.id.spinnerStyli);
        Button btnOk = findViewById(R.id.btnOk);

        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        db.child("Cartridges").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DataSnapshot result = task.getResult();
                for (DataSnapshot child : result.getChildren()) {
                    Stylus stylus = child.getValue(Stylus.class);
                    assert stylus != null;
                    stylusList.add(stylus);
                }
                Collections.sort(stylusList);
                ArrayAdapter<Stylus> spinnerAdapter = new ArrayAdapter<>(AddStylusFromListActivity.this, android.R.layout.simple_spinner_item, stylusList);
                spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerStyli.setAdapter(spinnerAdapter);
                btnOk.setOnClickListener(this::returnData);
            }
        });

    }



    public void returnData(View v) {
        Stylus stylus = (Stylus) spinnerStyli.getSelectedItem();
        int returnValue;
        Intent returnDataIntent = new Intent();
        try(DataHelper helper = new DataHelper()) {
            returnValue = helper.insertStylus(stylus);
        }
        returnDataIntent.putExtra(MainActivity.STYLUS_ID_RETURN_KEY, returnValue);
        setResult(RESULT_OK, returnDataIntent);
        finish();
    }
}
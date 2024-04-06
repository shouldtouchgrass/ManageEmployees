package com.ashstudios.safana.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ashstudios.safana.R;
import com.ashstudios.safana.adapters.WorkerRVAdapter;
import com.ashstudios.safana.adapters.WorkerRVSelectAdapter;
import com.ashstudios.safana.ui.worker_details.WorkerDetailsViewModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class NewProjectSelectWorkerActivity extends AppCompatActivity {

    private static final String TAG = "New_Project_Select_Worker_Activity";
    private WorkerDetailsViewModel workerDetailsViewModel;
    private WorkerRVSelectAdapter workerRVSelectAdapter;
    ArrayList<String> selectedIDList = new ArrayList<>();

    String title, budget, startDate, dueDate, projectID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_project_select_worker);

        workerDetailsViewModel = new WorkerDetailsViewModel();

        RecyclerView recyclerView = findViewById(R.id.rc_worker_details);
        workerRVSelectAdapter = new WorkerRVSelectAdapter(workerDetailsViewModel,NewProjectSelectWorkerActivity.this);

        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setAdapter(workerRVSelectAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(NewProjectSelectWorkerActivity.this));
        workerDetailsViewModel.setDataChangedListener(() -> {
            this.runOnUiThread(() -> {
                workerRVSelectAdapter.notifyDataSetChanged();
                recyclerView.setVisibility(View.VISIBLE);
            });
        });

        title = getIntent().getStringExtra("name");
        budget = getIntent().getStringExtra("budget");
        startDate = getIntent().getStringExtra("stDate");
        dueDate = getIntent().getStringExtra("duDate");

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Safana");

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.employee_select, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_settings:

                Log.d(TAG, "run");
                selectedIDList = workerRVSelectAdapter.getSelectedWorkersID();
                if (!selectedIDList.isEmpty()){
                    projectID = generateID();
                    WriteIntoDB(projectID, title, budget, startDate, dueDate, selectedIDList);
                    finish();
                } else {
                    Toast.makeText(NewProjectSelectWorkerActivity.this, "Please choose suitable employees!", Toast.LENGTH_LONG).show();
                }
                //return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    public void WriteIntoDB(String projectID, String title, String budget, String startDate, String dueDate, ArrayList<String> selectedIDList){
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Creating a HashMap to hold data
        Map<String, Object> data = new HashMap<>();
        data.put("Title", title);
        data.put("Budget", budget);
        data.put("Start Date", startDate);
        data.put("Due Date", dueDate);
        data.put("Workers ID List", selectedIDList);

        // Writing data to Firestore
        db.collection("Projects").document(projectID)
                .set(data)
                .addOnSuccessListener(aVoid -> {

                    // Data successfully written
                    WriteBatch batch = db.batch();
                    for (String empID : selectedIDList) {
                        DocumentReference empRef = db.collection("Employees").document(empID);
                        batch.update(empRef, "projectID", FieldValue.arrayUnion(projectID));
                    }
                    batch.commit()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    System.out.println("ProjectID added to employee documents successfully!");
                                }
                            })
                            .addOnFailureListener(e -> {
                                // Handle any errors
                                System.err.println("Error updating employee documents: " + e);
                            });
                    Toast.makeText(NewProjectSelectWorkerActivity.this, "Succeed to write into Firestore!", Toast.LENGTH_LONG).show();
                })
                .addOnFailureListener(e -> {
                    // Handle any errors
                    Toast.makeText(NewProjectSelectWorkerActivity.this, "Fail to write into Firestore!", Toast.LENGTH_LONG).show();

                });
    }

    private String generateID() {
        return "PROJECT" + generateNumber();
    }

    private int generateNumber() {
        Random r = new Random();
        return r.nextInt(100);
    }
}

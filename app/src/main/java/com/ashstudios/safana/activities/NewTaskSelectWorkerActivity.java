package com.ashstudios.safana.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ashstudios.safana.R;
import com.ashstudios.safana.adapters.SelectedWorkerForTaskAdapter;
import com.ashstudios.safana.models.WorkerModel;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class NewTaskSelectWorkerActivity extends AppCompatActivity {

    private static final String TAG = "NEW_TASK_SELECT_WORKER_ACTIVITY";
    private ArrayList<WorkerModel> arrayListMutableLiveData;
    ArrayList<WorkerModel> workers = new ArrayList<>();
    SelectedWorkerForTaskAdapter selectedWorkerForTaskAdapter;

    String projectName, taskName, dueDate, taskDesc, taskID;
    String selectedID;  //EMP ID

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_task_select_worker_activity);
        arrayListMutableLiveData = new ArrayList<>();

        RecyclerView recyclerView = findViewById(R.id.rc_worker_details);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setFocusable(false);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        //set the adapter
        selectedWorkerForTaskAdapter = new SelectedWorkerForTaskAdapter(this,workers);
        recyclerView.setAdapter(selectedWorkerForTaskAdapter);

        projectName = getIntent().getStringExtra("projectname");
        taskName = getIntent().getStringExtra("taskname");
        dueDate = getIntent().getStringExtra("duDate");
        taskDesc = getIntent().getStringExtra("taskDescription");

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Projects")
                .whereEqualTo("Title", projectName) // Assuming projectName is the variable storing the project name
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            List<String> workersIdList = (List<String>) document.get("Workers ID List");
                            if (workersIdList != null) {
                                getWorkerDetails(workersIdList);
                            }
                        }
                    } else {

                    }
                });
        selectedWorkerForTaskAdapter.notifyDataSetChanged();

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
                selectedID = selectedWorkerForTaskAdapter.getSelectedWorker();
                Log.d(TAG, selectedID);
                if (selectedID!=null && !selectedID.isEmpty()){
                    taskID = generateID();
                    WriteIntoDB(taskID, taskName, dueDate, taskDesc, selectedID, projectName);
                    finish();
                } else {
                    Toast.makeText(NewTaskSelectWorkerActivity.this, "Please choose suitable employee!", Toast.LENGTH_LONG).show();
                }
                //return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    void getWorkerDetails(List<String> workersIdList) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        for (String workerId : workersIdList) {
            db.collection("Employees").document(workerId).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null && document.exists()) {
                        String name = document.getString("name");
                        String role = document.getString("role");
                        String profileImg = document.getString("profile_image");
                        String mail = document.getString("mail");
                        String mobile = document.getString("mobile");
                        String sex = document.getString("sex");
                        String birthdate = document.getString("birth_date");
                        String password = document.getString("password");
                        List<String> allowanceIds = (List<String>) document.get("allowance_ids");

                        WorkerModel workerModel = new WorkerModel(name, role, profileImg, workerId, mail, mobile, sex, birthdate, password, allowanceIds);
                        arrayListMutableLiveData.add(workerModel);
                        workers.add(workerModel);
                        selectedWorkerForTaskAdapter.notifyDataSetChanged();
                    }
                } else {

                }
            });
        }
    }

    public void WriteIntoDB(String taskID, String taskName, String dueDate, String taskDesc, String selectedID, String projectName) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Creating a HashMap to hold data
        Map<String, Object> data = new HashMap<>();
        data.put("Task Name", taskName);
        data.put("Due Date", dueDate);
        data.put("Task Description", taskDesc);
        data.put("EMP ID", selectedID);
        data.put("Project Name", projectName);
        data.put("Status(%)", "0");

        // Writing data to Firestore
        db.collection("Tasks").document(taskID)
                .set(data)
                .addOnSuccessListener(aVoid -> {
                    // Successfully wrote data to Tasks collection

                    // Update employee's task array
                    DocumentReference employeeRef = db.collection("Employees").document(selectedID);
                    employeeRef.update("taskID", FieldValue.arrayUnion(taskID))
                            .addOnSuccessListener(aVoid1 -> {
                                // Successfully updated employee tasks array
                            })
                            .addOnFailureListener(e -> {
                                // Handle failure to update employee tasks array
                            });

                    // Update project's task array
                    db.collection("Projects")
                            .whereEqualTo("Title", projectName)
                            .get()
                            .addOnSuccessListener(queryDocumentSnapshots -> {
                                for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                    DocumentReference projectRef = documentSnapshot.getReference();
                                    projectRef.update("taskID List", FieldValue.arrayUnion(taskID))
                                            .addOnSuccessListener(aVoid2 -> {
                                                // Successfully updated project tasks array
                                            })
                                            .addOnFailureListener(e -> {
                                                // Handle failure to update project tasks array
                                            });
                                }
                            })
                            .addOnFailureListener(e -> {
                                // Handle failure to query Projects collection
                            });
                    Toast.makeText(NewTaskSelectWorkerActivity.this, "Succeed to write data!", Toast.LENGTH_LONG).show();
                })
                .addOnFailureListener(e -> {
                    // Handle failure to write data to Tasks collection
                });
    }


    private String generateID() {
        return "TASK" + generateNumber();
    }

    private int generateNumber() {
        Random r = new Random();
        return r.nextInt(100);
    }
}
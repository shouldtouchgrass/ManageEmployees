package com.ashstudios.safana.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import com.ashstudios.safana.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class TaskDetailsActivity extends AppCompatActivity {
    TextView project_name, assigned_to, due_date, task_desc, status;
    String taskID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_details);
        project_name = findViewById(R.id.title_project_name);
        assigned_to = findViewById(R.id.tv_task_assigned_to);
        due_date = findViewById(R.id.tv_task_date);
        task_desc = findViewById(R.id.tv_task_desc);
        status = findViewById(R.id.tv_status);
        taskID = getIntent().getStringExtra("taskID");

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Safana");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getData();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void getData() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Tasks").document(taskID).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null && document.exists()) {
                            String projName = document.getString("Project Name");
                            String dueDate = document.getString("Due Date");
                            String taskDesc = document.getString("Task Description");
                            String taskStatus = "0"; // Default value
                            if (document.contains("Status(%)")) {
                                taskStatus = document.getString("Status(%)");
                            }

                            String employeeID = document.getString("EMP ID");
                            if (employeeID != null) {
                                db.collection("Employees").document(employeeID).get()
                                        .addOnCompleteListener(empTask -> {
                                            if (empTask.isSuccessful()) {
                                                String empName = "";
                                                DocumentSnapshot empDocument = empTask.getResult();
                                                if (empDocument != null && empDocument.exists()) {
                                                    empName = empDocument.getString("name");
                                                }
                                                assigned_to.setText(empName);
                                            }
                                        });
                            }
                            project_name.setText(projName);
                            due_date.setText(dueDate);
                            task_desc.setText(taskDesc);
                            status.setText(taskStatus + "%");
                        }

                    } else {
                        // Handle task failure
                    }
                });
    }


}

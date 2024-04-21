package com.ashstudios.safana.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ashstudios.safana.R;
import com.ashstudios.safana.adapters.TaskStatusCategory;
import com.ashstudios.safana.adapters.TaskStatusCategoryAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class MyTaskDetailsActivity extends AppCompatActivity {

    Spinner spinner;
    CheckBox check;
    TaskStatusCategoryAdapter taskStatusCategoryAdapter;
    TextView project_name, assigned_to, due_date, task_desc, task_status;
    String taskID;
    String empID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mytask_details);
        project_name = findViewById(R.id.title_project_name);
        assigned_to = findViewById(R.id.tv_task_assigned_to);
        due_date = findViewById(R.id.tv_task_date);
        task_desc = findViewById(R.id.tv_task_desc);
        task_status = findViewById(R.id.tv_status);
        taskID = getIntent().getStringExtra("taskID");
        empID = getIntent().getStringExtra("empID");

        spinner = findViewById(R.id.spinner_category);
        taskStatusCategoryAdapter = new TaskStatusCategoryAdapter(this, R.layout.item_selected, getListCategory());
        spinner.setAdapter(taskStatusCategoryAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("TaskDetail");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getData();
        onClick();
    }
    private List<TaskStatusCategory> getListCategory(){
        List<TaskStatusCategory> list = new ArrayList<>();
        list.add(new TaskStatusCategory("TO DO"));
        list.add(new TaskStatusCategory("PROCESS"));
        list.add(new TaskStatusCategory("DONE"));
        return list;
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

    public void getData(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Tasks").document(taskID)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            DocumentSnapshot document = task.getResult();
                            if(document.exists()){
                                String proj_name = document.getString("Project Name");
                                String duDate = document.getString("Due Date");
                                String taskDesc = document.getString("Task Description");
                                String taskStatus = document.getString("Status(%)");
                                String employeeID = document.getString("EMP ID");

                                if (employeeID != null) {
                                    db.collection("Employees").document(employeeID).get()
                                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        DocumentSnapshot document2 = task.getResult();
                                                        if (document2 != null && document2.exists()) {
                                                            String empName = document2.getString("name");

                                                            project_name.setText(proj_name);
                                                            assigned_to.setText(empName);
                                                            due_date.setText(duDate);
                                                            task_desc.setText(taskDesc);
                                                            task_status.setText(taskStatus + "%");
                                                        }
                                                    }
                                                }
                                            });
                                }

                            }else{

                            }
                        }else{ //task.isSuccessful()

                        }
                    }
                });
    }

    public void onClick(){
        Button b = findViewById(R.id.button);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handle();
            }
        });
    }

    public void handle(){
        int percent;
        TextView item = findViewById(R.id.tv_selected);
        String item_selected = item.getText().toString();
        check = findViewById(R.id.check);
        if(check.isChecked()){
            if(item_selected.equals("TO DO")){
                percent = 0;
            }else if(item_selected.equals("PROCESS")){
                percent = 50;
            }else{
                percent = 100;
            }

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference taskRef = db.collection("Tasks").document(taskID);

            taskRef.update("Status(%)", String.valueOf(percent))
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            // Update successful
                            Toast.makeText(MyTaskDetailsActivity.this, "Succeed to update data!", Toast.LENGTH_LONG).show();
                            if (percent == 100) {
                                updateTaskComplete(db);
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Update failed

                        }
                    });
        }else{
            Toast.makeText(MyTaskDetailsActivity.this, "Please check into complete area!", Toast.LENGTH_LONG).show();
        }
    }
    private void updateTaskComplete(FirebaseFirestore db) {
        db.collection("Employees").document(empID) // replace "EMP_ID" with the actual employee ID
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    Long taskComplete = documentSnapshot.getLong("taskComplete");
                    if (taskComplete == null) {
                        taskComplete = 1L;
                    } else {
                        taskComplete += 1;
                    }
                    db.collection("Employees").document(empID).update("taskComplete", taskComplete)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(MyTaskDetailsActivity.this, "Task completion count updated!", Toast.LENGTH_SHORT).show();
                                finish();
                            })
                            .addOnFailureListener(e -> Toast.makeText(MyTaskDetailsActivity.this, "Failed to update task completion count.", Toast.LENGTH_SHORT).show());
                })
                .addOnFailureListener(e -> Toast.makeText(MyTaskDetailsActivity.this, "Failed to fetch employee data.", Toast.LENGTH_SHORT).show());
    }
}
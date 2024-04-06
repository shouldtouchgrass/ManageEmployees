package com.ashstudios.safana.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.ashstudios.safana.R;
import com.ashstudios.safana.ui.tasks.ProjectNameList;
import com.ashstudios.safana.ui.tasks.ProjectNameListAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.List;

public class CreateTaskActivity extends AppCompatActivity {
    private static final String TAG = "CREATE_TASK_ACTIVITY";

    ProjectNameListAdapter projectNameListAdapter;
    Button b;
    Spinner spinner;
    EditText task_name,dueDate, task_desc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_task);

        task_name = findViewById(R.id.et_task_name);
        dueDate = findViewById(R.id.et_due_date);
        task_desc = findViewById(R.id.et_desc);

        spinner = findViewById(R.id.project_spinner);
        projectNameListAdapter = new ProjectNameListAdapter(this, R.layout.item_selected_project_name, getListCategory());
        spinner.setAdapter(projectNameListAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        b = findViewById(R.id.button);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TextView projectName = findViewById(R.id.tv_selected);

                boolean flag = true;

                if(task_name.getText().toString().equals("")) {
                    task_name.setError("Please fill");
                    flag = false;
                }
                if(dueDate.getText().toString().equals("")){
                    dueDate.setError("Please fill");
                    flag = false;
                }
                if(task_desc.getText().toString().equals("")){
                    task_desc.setError("Please fill");
                    flag = false;
                }

                if(flag)
                {
                    Intent i = new Intent(CreateTaskActivity.this, NewTaskSelectWorkerActivity.class);
                    i.putExtra("projectname",projectName.getText().toString());
                    i.putExtra("taskname",task_name.getText().toString());
                    i.putExtra("duDate",dueDate.getText().toString());
                    i.putExtra("taskDescription",task_desc.getText().toString());
                    startActivity(i);

                    finish();
                }
            }
        });

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Create Tasks");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }


    private List<ProjectNameList> getListCategory(){
        final List<ProjectNameList> list = new ArrayList<>();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Projects").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String title = document.getString("Title");
                        ProjectNameList projectName = new ProjectNameList(title);
                        list.add(projectName);
                        projectNameListAdapter.notifyDataSetChanged();
                    }
                } else {

                }
            }
        });
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
}

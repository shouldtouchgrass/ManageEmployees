package com.ashstudios.safana.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.res.ResourcesCompat;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.ashstudios.safana.R;
import com.ashstudios.safana.others.SharedPref;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.gesture.ContainerScrollType;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.LineChartView;
import lecho.lib.hellocharts.view.PieChartView;

public class ProjectDetailsActivity extends AppCompatActivity {

    private static final String TAG = "PROJECT_DETAILS_ACTIVITY";


    private PieChartView pieLineChartView;
    public FirebaseFirestore db  = FirebaseFirestore.getInstance();
    ProgressBar progressBar;
    private AlertDialog dialog;
    SharedPref sharedPref;
    ScrollView scrollView;
    String projectId;
    ArrayList<String> taskstatuslist = new ArrayList<>();

    TextView project_name, project_budget, project_stDate, project_duDate, project_worker_num, details;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_details);
        Toolbar toolbar = findViewById(R.id.toolbar);
        progressBar = findViewById(R.id.progress_bar);
        scrollView = findViewById(R.id.sv_project_details);

        project_name = findViewById(R.id.title_project_name);
        project_budget = findViewById(R.id.tv_budget);
        project_stDate = findViewById(R.id.tv_start_date);
        project_duDate = findViewById(R.id.tv_due_date);
        project_worker_num = findViewById(R.id.tv_num_worker);
        details = findViewById(R.id.tv_additional_details);

        projectId = getIntent().getStringExtra("projectID");
        taskstatuslist = getIntent().getStringArrayListExtra("taskstatuslist");

        ArrayList<Integer> integerList = new ArrayList<>();
        for(String status: taskstatuslist){
            int num = Integer.parseInt(status);
            integerList.add(num);
        }
        Log.d(TAG, "run");

        ProgressBar progressBarLoading = new ProgressBar(ProjectDetailsActivity.this);
        progressBarLoading.setPadding(10,30,10,30);
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(ProjectDetailsActivity.this);
        dialog = alertDialog.create();
        dialog.setCancelable(false);
        dialog.setView(progressBarLoading);


        // firestore

        toolbar.setTitle("Safana");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        LoadData();
        initGraphs(integerList);
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

    public void LoadData() {
        sharedPref = new SharedPref(ProjectDetailsActivity.this);
        String empId = sharedPref.getEMP_ID();
        dialog.show();
        db.collection("Employees")
                .document(empId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot documentSnapshot = task.getResult();
                            if (documentSnapshot.exists()) {
                                if (projectId != null) {
                                    db.collection("Projects").document(projectId)
                                            .get()
                                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        DocumentSnapshot projectDocument = task.getResult();
                                                        if (projectDocument.exists()) {
                                                            String title = projectDocument.getString("Title");
                                                            String budget = projectDocument.getString("Budget");
                                                            List<String> employeeList = (List<String>) projectDocument.get("Workers ID List");
                                                            String stDate = projectDocument.getString("Start Date");
                                                            String duDate = projectDocument.getString("Due Date");


                                                            int num = employeeList.size();
                                                            project_name.setText(title);
                                                            project_budget.setText(budget);
                                                            project_stDate.setText(stDate);
                                                            project_duDate.setText(duDate);
                                                            project_worker_num.setText(String.valueOf(num));
                                                            scrollView.setVisibility(View.VISIBLE);

                                                            if (projectDocument.contains("taskID List")) {
                                                                List<String> taskIDList = (List<String>) projectDocument.get("taskID List");
                                                                ArrayList<String> taskNamesList = new ArrayList<>();
                                                                // Loop through taskIDList to fetch task names from database
                                                                for (String taskId : taskIDList) {
                                                                    Log.d(TAG, taskId);
                                                                    FirebaseFirestore.getInstance().collection("Tasks").document(taskId)
                                                                            .get()
                                                                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<DocumentSnapshot> task2) {
                                                                                    if (task2.isSuccessful()) {
                                                                                        DocumentSnapshot taskDocument = task2.getResult();
                                                                                        if (taskDocument.exists()) {
                                                                                            String taskName = taskDocument.getString("Task Name");
                                                                                            Log.d(TAG, taskName);
                                                                                            taskNamesList.add(taskName);
                                                                                            // Check if all task names have been fetched
                                                                                            if (taskNamesList.size() == taskIDList.size()) {
                                                                                                StringBuilder stringBuilder = new StringBuilder();
                                                                                                for (String item : taskNamesList) {
                                                                                                    stringBuilder.append(item).append("\n");
                                                                                                }
                                                                                                details.setText(stringBuilder.toString());  // display additional details
                                                                                            }
                                                                                        }
                                                                                    }//task2.isSuccessful()
                                                                                }
                                                                            });
                                                                }//for (String taskId : taskIDList)
                                                            }else {//projectDocument.contains("taskID List")
                                                                details.setText("");
                                                            }
                                                        }
                                                    }//task.isSuccessful()
                                                }
                                            });
                                }
                            }
                        } else {
                            // Handle error
                        }
                        dialog.dismiss();
                    }
                });
    }


    private void initGraphs(ArrayList<Integer> taskStatusList) {
        int percent, n;
        int sum  =0;
        if(taskStatusList==null || taskStatusList.isEmpty()){
            percent = 0;
            n = 0;
        }else{
            for (int i : taskStatusList){
                sum+=i;
            }
            n = taskStatusList.size();
            percent = sum/n;
        }

        progressBar.setProgress(percent);
        TextView text = findViewById(R.id.progress_bar_text);
        TextView p_status = findViewById(R.id.tv_status);
        text.setText(String.valueOf(percent) + "%");

        if(percent < 100){
            p_status.setText("IN PROGRESS");
        }else{
            p_status.setText("DONE");
        }
        LineChartView lineChartView = findViewById(R.id.chart);
        pieLineChartView = findViewById(R.id.pie_chart);
        pieCharData(taskStatusList);
        lineChartView.setInteractive(true);
        lineChartView.setContainerScrollEnabled(true, ContainerScrollType.HORIZONTAL);
        List<PointValue> values = new ArrayList<>();
        for(int i = 0; i <n; i ++) {
            values.add(new PointValue(i, i+1));
        }

        //In most cased you can call data model methods in builder-pattern-like manner.
        Line line = new Line(values).setColor(Color.BLUE).setCubic(true);
        List<Line> lines = new ArrayList<>();
        lines.add(line);

        LineChartData data = new LineChartData();
        data.setLines(lines);

        lineChartView.setLineChartData(data);
        lineChartView.animate();
    }


    private void pieCharData(ArrayList<Integer> taskStatusList) {
        List<SliceValue> values = new ArrayList<>();
        int numValues;

        if (taskStatusList == null || taskStatusList.isEmpty()) {
            numValues = 4;
        } else {
            numValues = taskStatusList.size();
        }

        for (int i = 0; i < numValues; ++i) {
            float value = (taskStatusList != null && !taskStatusList.isEmpty()) ? taskStatusList.get(i) : 0;
            SliceValue sliceValue = new SliceValue(value, ChartUtils.pickColor());
            values.add(sliceValue);
        }

        PieChartData data = new PieChartData(values);
        boolean hasLabels = true;
        data.setHasLabels(hasLabels);
        boolean hasLabelForSelected = false;
        data.setHasLabelsOnlyForSelected(hasLabelForSelected);
        boolean hasCenterCircle = true;
        data.setHasCenterCircle(hasCenterCircle);


        boolean isExploded = true;
        if (isExploded) {
            data.setSlicesSpacing(5);
        }

        String defaultText = "Task Percent"; // Default center text
        // Set center text based on conditions
        if (taskStatusList == null || taskStatusList.isEmpty()) {
            data.setCenterText1(" ");
            // Load font from resources
            Typeface tf = ResourcesCompat.getFont(ProjectDetailsActivity.this, R.font.brownbold);
            data.setCenterText1Typeface(tf);

            // Get font size from dimens.xml and convert it to sp(library uses sp values).
            data.setCenterText1FontSize(ChartUtils.px2sp(getResources().getDisplayMetrics().scaledDensity, 50));
        } else {
            data.setCenterText1(defaultText);
            // Load font from resources
            Typeface tf = ResourcesCompat.getFont(ProjectDetailsActivity.this, R.font.brownbold);
            data.setCenterText1Typeface(tf);

            // Get font size from dimens.xml and convert it to sp(library uses sp values).
            data.setCenterText1FontSize(ChartUtils.px2sp(getResources().getDisplayMetrics().scaledDensity, 50));
        }

        pieLineChartView.setPieChartData(data);
    }
}
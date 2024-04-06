package com.ashstudios.safana.ui.tasks;

import android.os.Bundle;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.ashstudios.safana.models.ProjectModel;
import com.ashstudios.safana.models.TaskModel;
import com.ashstudios.safana.ui.project__details.ProjectDetailsViewModel;
import com.ashstudios.safana.ui.worker_details.WorkerDetailsViewModel;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Objects;

public class TasksViewModel extends ViewModel {

    private ArrayList<TaskModel> taskModels;
    ProjectDetailsViewModel.DataChangedListener listener;

    public TasksViewModel() {
        taskModels = new ArrayList<>();
        getData();
    }

    public ArrayList<TaskModel> getArrayListMutableLiveData() {
        return taskModels;
    }

    public void getData() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Tasks")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                            String taskID = document.getId();
                            String name = document.getString("Task Name");
                            String duDate = document.getString("Due Date");

                            TaskModel taskModel = new TaskModel(taskID,name,duDate);
                            taskModels.add(taskModel);
                            if (listener != null) {
                                listener.onDataChanged();
                            }
                        }} else {
                        Toast.makeText(null, "Error fetching tasks!!!", Toast.LENGTH_SHORT).show();
                    }
                    });
    }

    public void sort(Bundle b) {
        taskModels.remove(0);
    }

    public void setDataChangedListener(ProjectDetailsViewModel.DataChangedListener listener) {
        this.listener = listener;
    }
}
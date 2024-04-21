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
import java.util.Collections;
import java.util.Comparator;
import java.util.Objects;

public class TasksViewModel extends ViewModel {

    private ArrayList<TaskModel> taskModels;
    FirebaseFirestore db;
    ProjectDetailsViewModel.DataChangedListener listener;

    public TasksViewModel() {
        taskModels = new ArrayList<>();
        db = FirebaseFirestore.getInstance();
        db.collection("Tasks")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                            String taskID = document.getId();
                            String name = document.getString("Task Name");
                            String duDate = document.getString("Due Date");
                            String status = document.getString("Status(%)");
                            String empID = document.getString("EMP ID");

                            TaskModel taskModel = new TaskModel(status, taskID,name,duDate, empID);
                            taskModels.add(taskModel);
                            if (listener != null) {
                                listener.onDataChanged();
                            }
                        }} else {
                        Toast.makeText(null, "Error fetching tasks!!!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public ArrayList<TaskModel> getArrayListMutableLiveData() {
        return taskModels;
    }

    public void sort(Bundle b) {
        Comparator<TaskModel> comparator = Comparator.comparing(TaskModel::getDate);
        Collections.sort(taskModels, comparator); // Sort the list using the comparator
    }

    public void sort_completed(Bundle b) {
        ArrayList<TaskModel> completedTasks = new ArrayList<>();
        for (TaskModel taskModel : taskModels) {
            String task_percent = taskModel.getStatus();
            int num = Integer.parseInt(task_percent);
            if (num >= 100) {
                completedTasks.add(taskModel);
            }
        }

        // Replace taskModels with the sorted list of completed tasks
        taskModels.clear();
        taskModels.addAll(completedTasks);

        // Notify listener if needed
        if (listener != null) {
            listener.onDataChanged();
        }
    }

    public void sort_incompleted(Bundle b) {
        ArrayList<TaskModel> completedTasks = new ArrayList<>();
        for (TaskModel taskModel : taskModels) {
            String task_percent = taskModel.getStatus();
            int num = Integer.parseInt(task_percent);
            if (num < 100) {
                completedTasks.add(taskModel);
            }
        }

        // Replace taskModels with the sorted list of completed tasks
        taskModels.clear();
        taskModels.addAll(completedTasks);

        // Notify listener if needed
        if (listener != null) {
            listener.onDataChanged();
        }

    }

    public void setDataChangedListener(ProjectDetailsViewModel.DataChangedListener listener) {
        this.listener = listener;
    }
}
package com.ashstudios.safana.ui.mytasks;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ashstudios.safana.R;
import com.ashstudios.safana.activities.CreateTaskActivity;
import com.ashstudios.safana.adapters.SupervisorTaskAdapter;
import com.ashstudios.safana.adapters.TaskAdapter;
import com.ashstudios.safana.models.AllowanceModel;
import com.ashstudios.safana.models.TaskModel;
import com.ashstudios.safana.others.SharedPref;
import com.ashstudios.safana.others.SwipeToDeleteCallback;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MyTasksFragment extends Fragment {

    private static  final String TAG = "My_Tasks_Fragment";
    static private MyTasksViewModel homeViewModel;
    static private RecyclerView recyclerView;
    private TaskAdapter taskAdapter;
    private ConstraintLayout constraintLayout;
    private ArrayList<TaskModel> arrayListMutableLiveData;
    ArrayList<TaskModel> tasks = new ArrayList<>();

    FirebaseFirestore db;
    TextView tv;
    private Boolean isUndo = false;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        homeViewModel = ViewModelProviders.of(this).get(MyTasksViewModel.class);
        View root = inflater.inflate(R.layout.fragment_mytasks, container, false);

        tv = root.findViewById(R.id.no_tasks);
        arrayListMutableLiveData = new ArrayList<>();

        constraintLayout = root.findViewById(R.id.constraint_layout);
        recyclerView = root.findViewById(R.id.rv_tasks);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        //set the adapter
        taskAdapter = new TaskAdapter(getActivity(),tasks);
        recyclerView.setAdapter(taskAdapter);
        Context context = getContext();
        SharedPref sharedPref = new SharedPref(context);
        String currentUserId = sharedPref.getEMP_ID();
        Log.d(TAG, currentUserId);
        if (currentUserId != null && !currentUserId.isEmpty()) {
            checkLeaveRequests(currentUserId);
        }
        tv.setText(" ");
        enableSwipeToCompleteAndUndo();
        return root;
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.action_settings).setVisible(false);
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    private void enableSwipeToCompleteAndUndo() {
        SwipeToDeleteCallback swipeToDeleteCallback = new SwipeToDeleteCallback(getActivity()) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
                isUndo = true;
                final int position = viewHolder.getAdapterPosition();
                final TaskModel item = taskAdapter.getData().get(position);

                taskAdapter.removeItem(position);

                Snackbar snackbar = Snackbar
                        .make(constraintLayout, "Task is moved to the completed list.", Snackbar.LENGTH_LONG);
                snackbar.setAction("UNDO", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(isUndo) {
                            taskAdapter.restoreItem(item, position);
                            recyclerView.scrollToPosition(position);
                            isUndo = false;
                        }
                    }
                });

                snackbar.setActionTextColor(getResources().getColor(R.color.colorAccent));
                snackbar.show();

            }
        };

        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeToDeleteCallback);
        itemTouchhelper.attachToRecyclerView(recyclerView);
    }

    public void sort(Context mContext, Bundle b, boolean chip_date, boolean chip_complete, boolean chip_incomplete) {
        Toast.makeText( mContext, "sorting...", Toast.LENGTH_LONG).show();
        if(chip_date){
            sort_date();
        }

        if(chip_complete){
            Log.d(TAG, "run");
            sort_completed();
            Log.d(TAG, "run2");
        }

        if(chip_incomplete){
            sort_incompleted();
        }

        taskAdapter.notifyDataSetChanged();

    }

    private void sort_date(){
        Comparator<TaskModel> comparator = Comparator.comparing(TaskModel::getDate);
        Collections.sort(tasks, comparator); // Sort the list using the comparator
        taskAdapter.notifyDataSetChanged();
    }


    private void sort_completed(){
        ArrayList<TaskModel> completedTasks = new ArrayList<>();
        for (TaskModel task : tasks) {
            Log.d(TAG, "run3");
            String task_percent = task.getStatus();
            int num = Integer.parseInt(task_percent);
            if (num == 100) {
                completedTasks.add(task);
            }
        }
        tasks.clear();
        tasks.addAll(completedTasks);
        Log.d(TAG, "run5");
        taskAdapter.notifyDataSetChanged();
    }



    private void sort_incompleted(){
        ArrayList<TaskModel> incompletedTasks = new ArrayList<>();
        for (TaskModel task : tasks) {
            String task_percent = task.getStatus();
            int num = Integer.parseInt(task_percent);
            if (num < 100) {
                incompletedTasks.add(task);
            }
        }
        tasks.clear();
        tasks.addAll(incompletedTasks);
        taskAdapter.notifyDataSetChanged();
    }

    void getTaskDetails(List<String> taskIds) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        for (String taskId : taskIds) {
            db.collection("Tasks").document(taskId).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null && document.exists()) {
                        String name = document.getString("Task Name");
                        String duDate = document.getString("Due Date");
                        String status = document.getString("Status(%)");
                        String empid = document.getString("EMP ID");
                        if(status != null && !status.equals("100")){
                            TaskModel taskModel = new TaskModel(status, taskId, name, duDate, empid);
                            arrayListMutableLiveData.add(taskModel);
                            tasks.add(taskModel);
                            tv.setText(" ");
                            // Update your adapter here
                            taskAdapter.notifyDataSetChanged();
                        }else{
                            tv.setText("NO ASSIGNMENTS");
                        }

                    }
                } else {

                }
            });
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        updateTasksFromDatabase();
    }

    private void updateTasksFromDatabase() {
        Context context = getContext();
        SharedPref sharedPref = new SharedPref(context);
        String currentUserId = sharedPref.getEMP_ID();

        if (currentUserId != null && !currentUserId.isEmpty()) {
            tv.setText("Loading tasks...");
            tasks.clear();  // Clear the current list of tasks
            db = FirebaseFirestore.getInstance();
            db.collection("Employees").document(currentUserId).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null && document.exists()) {
                        List<String> taskIds = (List<String>) document.get("taskID");
                        if (taskIds != null && !taskIds.isEmpty()) {
                            getTaskDetails(taskIds);
                        } else {
                            tv.setText("No tasks assigned.");
                            taskAdapter.notifyDataSetChanged(); // Notify adapter about data set changes
                        }
                    } else {
                        tv.setText("No tasks found.");
                        taskAdapter.notifyDataSetChanged(); // Notify adapter about data set changes
                    }
                } else {
                    Log.e(TAG, "Error getting documents: ", task.getException());
                    tv.setText("Failed to load tasks.");
                    taskAdapter.notifyDataSetChanged(); // Notify adapter about data set changes
                }
            });
        } else {
            tv.setText("Invalid user ID.");
        }
    }
    private void checkLeaveRequests(String currentUserId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Leaves")
                .whereEqualTo("empid", currentUserId)
                .whereEqualTo("notify", "unread")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot document : task.getResult()) {
                            String status = document.getString("Status");
                            showStatusDialog(status, document);
                        }
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                });
    }

    private void showStatusDialog(String status, DocumentSnapshot document) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        if ("Accept".equals(status)) {
            builder.setMessage("Yêu cầu nghỉ phép của bạn đã được chấp thuận.");
        } else if ("Reject".equals(status)) {
            builder.setMessage("Yêu cầu nghỉ phép của bạn đã bị từ chối.");
        } else {
            return; 
        }
        builder.setPositiveButton("OK", (dialog, which) -> {
            dialog.dismiss();
            markNotificationAsRead(document.getId());
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    private void markNotificationAsRead(String documentId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Leaves").document(documentId)
                .update("notify", "read")
                .addOnSuccessListener(aVoid -> Log.d(TAG, "DocumentSnapshot successfully updated!"))
                .addOnFailureListener(e -> Log.w(TAG, "Error updating document", e));
    }
}
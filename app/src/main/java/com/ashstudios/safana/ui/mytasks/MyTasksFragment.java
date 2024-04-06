package com.ashstudios.safana.ui.mytasks;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
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
    private Boolean isUndo = false;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        homeViewModel = ViewModelProviders.of(this).get(MyTasksViewModel.class);
        View root = inflater.inflate(R.layout.fragment_mytasks, container, false);

        arrayListMutableLiveData = new ArrayList<>();
//        final TextView textView = root.findViewById(R.id.text_home);
//        homeViewModel.getText().observe(this, new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
//                textView.setText(s);
//            }
//        });

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

        db = FirebaseFirestore.getInstance();
        db.collection("Employees").document(currentUserId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document != null && document.exists()) {
                    List<String> taskIds = (List<String>) document.get("taskID");
                    if (taskIds != null) {
                        getTaskDetails(taskIds);
                    }
                }
            } else {

            }
        });

        taskAdapter.notifyDataSetChanged();
        enableSwipeToCompleteAndUndo();
        return root;
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

    public static void sort(Context mContext, Bundle b)
    {
        Toast.makeText( mContext, "sorting...", Toast.LENGTH_LONG).show();
        homeViewModel.sort(b);
        TaskAdapter leaveManagementRVAdapter = new TaskAdapter(mContext,homeViewModel.getArrayListMutableLiveData());
        recyclerView.setAdapter(leaveManagementRVAdapter);
    }

    void getTaskDetails(List<String> taskIds) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        for (String taskId : taskIds) {
            Log.d(TAG, taskId);
            db.collection("Tasks").document(taskId).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null && document.exists()) {
                        String name = document.getString("Task Name");
                        String duDate = document.getString("Due Date");

                        TaskModel taskModel = new TaskModel(taskId, name, duDate);
                        arrayListMutableLiveData.add(taskModel);
                        tasks.add(taskModel);
                        // Update your adapter here
                        taskAdapter.notifyDataSetChanged();
                    }
                } else {

                }
            });
        }
    }


}
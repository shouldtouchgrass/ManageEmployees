package com.ashstudios.safana.ui.tasks;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
import com.ashstudios.safana.adapters.LeaveManagementRVAdapter;
import com.ashstudios.safana.adapters.SupervisorTaskAdapter;
import com.ashstudios.safana.adapters.TaskAdapter;
import com.ashstudios.safana.adapters.WorkerRVAdapter;
import com.ashstudios.safana.models.TaskModel;
import com.ashstudios.safana.others.SwipeToDeleteCallback;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

public class TasksFragment extends Fragment {

    static private TasksViewModel tasksViewModel;
    static private RecyclerView recyclerView;
    static private SupervisorTaskAdapter supervisorTaskAdapter;
    private ConstraintLayout constraintLayout;
    private Boolean isUndo = false;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        tasksViewModel =
                ViewModelProviders.of(this).get(TasksViewModel.class);
        View root = inflater.inflate(R.layout.fragment_tasks, container, false);
        FloatingActionButton fab = root.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), CreateTaskActivity.class);
                startActivity(intent);
            }
        });
        constraintLayout = root.findViewById(R.id.constraint_layout);
        recyclerView = root.findViewById(R.id.rv_sup_tasks);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        //set the adapter
        supervisorTaskAdapter = new SupervisorTaskAdapter(getActivity(),tasksViewModel.getArrayListMutableLiveData());
        //tasksViewModel.sort(new Bundle());
        recyclerView.setAdapter(supervisorTaskAdapter);

        tasksViewModel.setDataChangedListener(() -> {
            getActivity().runOnUiThread(() -> {
                supervisorTaskAdapter.notifyDataSetChanged();
                recyclerView.setVisibility(View.VISIBLE);
            });
        });
        enableSwipeToCompleteAndUndo();
        return root;
    }

    private static TasksViewModel getTaskViewModel()
    {
        return tasksViewModel;
    }

    private void enableSwipeToCompleteAndUndo() {
        SwipeToDeleteCallback swipeToDeleteCallback = new SwipeToDeleteCallback(getActivity()) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
                isUndo = true;
                final int position = viewHolder.getAdapterPosition();
                final TaskModel item = supervisorTaskAdapter.getData().get(position);

                supervisorTaskAdapter.removeItem(position);

                Snackbar snackbar = Snackbar
                        .make(constraintLayout, "Task is moved to the completed list.", Snackbar.LENGTH_LONG);
                snackbar.setAction("UNDO", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(isUndo) {
                            supervisorTaskAdapter.restoreItem(item, position);
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


    public static void sort(Context mContext, Bundle b, boolean chip_date, boolean chip_complete, boolean chip_incomplete) {
        Toast.makeText( mContext, "sorting...", Toast.LENGTH_LONG).show();

        if(chip_date){
            tasksViewModel.sort(b);
        }

        if(chip_complete){
            tasksViewModel.sort_completed(b);
        }

        if(chip_incomplete){
            tasksViewModel.sort_incompleted(b);
        }
        supervisorTaskAdapter.notifyDataSetChanged();
    }

}
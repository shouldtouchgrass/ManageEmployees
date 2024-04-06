package com.ashstudios.safana.ui.project__details;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.ashstudios.safana.R;
import com.ashstudios.safana.activities.NewProjectActivity;
import com.ashstudios.safana.adapters.SupervisorProjectAdapter;
import com.ashstudios.safana.models.ProjectModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import android.content.Context;
import android.widget.Toast;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ashstudios.safana.activities.CreateTaskActivity;
import com.ashstudios.safana.adapters.SupervisorTaskAdapter;
import com.ashstudios.safana.adapters.TaskAdapter;
import com.ashstudios.safana.others.SwipeToDeleteCallback;
import com.google.android.material.snackbar.Snackbar;

public class ProjectDetailsFragment extends Fragment {

    static private ProjectDetailsViewModel projectDetailsViewModel;
    static private RecyclerView recyclerView;
    private SupervisorProjectAdapter supervisorProjectAdapter;
    private ConstraintLayout constraintLayout;
    private Boolean isUndo = false;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        projectDetailsViewModel =
                ViewModelProviders.of(this).get(ProjectDetailsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_projects, container, false);
        FloatingActionButton fab = root.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), NewProjectActivity.class);
                startActivity(intent);
            }
        });
        constraintLayout = root.findViewById(R.id.constraint_layout);
        recyclerView = root.findViewById(R.id.rv_sup_projects);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        //set the adapter
        supervisorProjectAdapter = new SupervisorProjectAdapter(getActivity(),projectDetailsViewModel.getArrayListMutableLiveData());
        //tasksViewModel.sort(new Bundle());
        recyclerView.setAdapter(supervisorProjectAdapter);
        projectDetailsViewModel.setDataChangedListener(() -> {
            getActivity().runOnUiThread(() -> {
                supervisorProjectAdapter.notifyDataSetChanged();
                recyclerView.setVisibility(View.VISIBLE);
            });
        });
        enableSwipeToCompleteAndUndo();
        return root;
    }

    private void enableSwipeToCompleteAndUndo() {
        SwipeToDeleteCallback swipeToDeleteCallback = new SwipeToDeleteCallback(getActivity()) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
                isUndo = true;
                final int position = viewHolder.getAdapterPosition();
                final ProjectModel item = supervisorProjectAdapter.getData().get(position);

                supervisorProjectAdapter.removeItem(position);

                Snackbar snackbar = Snackbar
                        .make(constraintLayout, "Task is moved to the completed list.", Snackbar.LENGTH_LONG);
                snackbar.setAction("UNDO", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(isUndo) {
                            supervisorProjectAdapter.restoreItem(item, position);
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


    /*public static void sort(Context mContext, Bundle b)
    {
        Toast.makeText( mContext, "sorting...", Toast.LENGTH_LONG).show();
        projectDetailsViewModel.sort(b);
        ProjectAdapter  leaveManagementRVAdapter = new ProjectAdapter(mContext,projectDetailsViewModel.getArrayListMutableLiveData());
        recyclerView.setAdapter(leaveManagementRVAdapter);
    }*/

}


package com.ashstudios.safana.ui.projectstatus;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ashstudios.safana.R;
import com.ashstudios.safana.adapters.ProjectAdapter;
import com.ashstudios.safana.models.ProjectModel;
import com.ashstudios.safana.others.SharedPref;
import com.ashstudios.safana.others.SwipeToDeleteCallback;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ProjectStatusFragment extends Fragment {
    private static final String TAG = "PROJECT_STATUS_FRAGMENT";
    private ProjectStatusViewModel projectStatusViewModel;
    static private RecyclerView recyclerView;
    private ProjectAdapter projectAdapter;
    private ConstraintLayout constraintLayout;
    FirebaseFirestore db;
    private ArrayList<ProjectModel> arrayListMutableLiveData;
    ArrayList<ProjectModel> projects = new ArrayList<>();
    private Boolean isUndo = false;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        projectStatusViewModel =ViewModelProviders.of(this).get(ProjectStatusViewModel.class);
        View root = inflater.inflate(R.layout.fragment_myprojects, container, false);
        arrayListMutableLiveData = new ArrayList<>();

        constraintLayout = root.findViewById(R.id.constraint_layout);
        recyclerView = root.findViewById(R.id.rv_projects);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        //set the adapter
        projectAdapter = new ProjectAdapter(getActivity(), projects);
        recyclerView.setAdapter(projectAdapter);
        Context context = getContext();
        SharedPref sharedPref = new SharedPref(context);
        String currentUserId = sharedPref.getEMP_ID();

        db = FirebaseFirestore.getInstance();
        db.collection("Employees").document(currentUserId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document != null && document.exists()) {
                    List<String> projectIds = (List<String>) document.get("projectID");
                    if (projectIds != null) {
                        getProjectDetails(projectIds);
                    }
                }
            } else {

            }
        });
        projectAdapter.notifyDataSetChanged();
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
                final ProjectModel item = projectAdapter.getData().get(position);

                projectAdapter.removeItem(position);

                Snackbar snackbar = Snackbar
                        .make(constraintLayout, "Project is moved to the completed list.", Snackbar.LENGTH_LONG);
                snackbar.setAction("UNDO", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (isUndo) {
                            projectAdapter.restoreItem(item, position);
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


    public void getProjectDetails(List<String> projectIDs) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        for (String projectId : projectIDs) {
            Log.d(TAG, projectId);
            db.collection("Projects").document(projectId).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        String title = document.getString("Title");
                        String stDate = document.getString("Start Date");
                        String duDate = document.getString("Due Date");

                        db.collection("Tasks")
                                .whereEqualTo("Project Name", title)
                                .get()
                                .addOnCompleteListener(tasksTask -> {
                                    if (tasksTask.isSuccessful()) {
                                        ArrayList<String> taskStatusList = new ArrayList<>();
                                        for (QueryDocumentSnapshot document2 : Objects.requireNonNull(tasksTask.getResult())) {
                                            if (document2.contains("Status(%)")) {
                                                String status = document2.getString("Status(%)");
                                                Log.d(TAG, status);
                                                taskStatusList.add(status);
                                            }
                                        }
                                        ProjectModel projectModel = new ProjectModel(projectId, taskStatusList, title, stDate, duDate);
                                        projects.add(projectModel);
                                        arrayListMutableLiveData.add(projectModel);
                                        projectAdapter.notifyDataSetChanged();

                                    } else {//tasksTask.isSuccessful()

                                    }
                                });

                    } else {
                        // Handle the case where the document does not exist
                    }
                } else {
                    // Handle failures
                }
            });
        }
    }
}
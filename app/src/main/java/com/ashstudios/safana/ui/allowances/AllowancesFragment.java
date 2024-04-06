package com.ashstudios.safana.ui.allowances;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ashstudios.safana.R;
import com.ashstudios.safana.activities.WorkerDashboardActivity;
import com.ashstudios.safana.adapters.AllowanceAdapter;
import com.ashstudios.safana.adapters.TaskAdapter;
import com.ashstudios.safana.models.AllowanceModel;
import com.ashstudios.safana.others.SharedPref;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class AllowancesFragment extends Fragment {

    private RecyclerView recyclerView;
    private AllowancesViewModel alloweViewModel;
    private AllowanceAdapter allowanceAdapter;
    private ArrayList<AllowanceModel> arrayListMutableLiveData;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    ArrayList<AllowanceModel> allowances = new ArrayList<>();


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        alloweViewModel =
                ViewModelProviders.of(this).get(AllowancesViewModel.class);
        View root = inflater.inflate(R.layout.fragment_allowances, container, false);
        arrayListMutableLiveData = new ArrayList<>();

        recyclerView = root.findViewById(R.id.rv_allowances);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setFocusable(false);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        //set the adapter
        allowanceAdapter = new AllowanceAdapter(getActivity(),allowances);
        recyclerView.setAdapter(allowanceAdapter);
        Context context = getContext();
        SharedPref sharedPref = new SharedPref(context);
        String currentUserId = sharedPref.getEMP_ID();

        db.collection("Employees").document(currentUserId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document != null && document.exists()) {
                    List<String> allowanceIds = (List<String>) document.get("allowance_ids");
                    if (allowanceIds != null) {
                        getAllowancesDetails(allowanceIds);
                    }
                }
            } else {

            }
        });
        allowanceAdapter.notifyDataSetChanged();
        return root;
    }

    private void getData() {
        arrayListMutableLiveData.add(new AllowanceModel(getActivity().getResources().getString(R.string.food),"Food","2 Months"));
        arrayListMutableLiveData.add(new AllowanceModel(getActivity().getResources().getString(R.string.laptop),"Laptop","Lifetime"));
        arrayListMutableLiveData.add(new AllowanceModel(getActivity().getResources().getString(R.string.car),"Vehicle","3 Months"));
        arrayListMutableLiveData.add(new AllowanceModel(getActivity().getResources().getString(R.string.building),"Flat","4 Months"));
        arrayListMutableLiveData.add(new AllowanceModel(getActivity().getResources().getString(R.string.insurence),"Insurance","6 Months"));
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
    void getAllowancesDetails(List<String> allowanceIds) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        for (String allowanceId : allowanceIds) {
            db.collection("Allowances").document(allowanceId).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null && document.exists()) {
                        String url = document.getString("url");
                        String duration = document.getString("duration");
                        AllowanceModel allowance = new AllowanceModel(url, document.getId(), duration);
                        allowances.add(allowance);
                        arrayListMutableLiveData.add(allowance);
                        // Update your adapter here
                        allowanceAdapter.notifyDataSetChanged();
                    }
                } else {

                }
            });
        }
    }
}
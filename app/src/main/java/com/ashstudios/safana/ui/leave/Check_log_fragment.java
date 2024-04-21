package com.ashstudios.safana.ui.leave;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ashstudios.safana.R;
import com.ashstudios.safana.adapters.LeaveManagementRVAdapter;
import com.ashstudios.safana.adapters.LeaveStatusRVAdapter;
import com.ashstudios.safana.others.SharedPref;
import com.ashstudios.safana.ui.leave_management.LeaveManagementViewModel;

public class Check_log_fragment extends Fragment {
    static private LeaveViewModel leaveStatusViewModel;
    static RecyclerView recyclerView;
    private static LeaveStatusRVAdapter leaveStatusRVAdapter;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        leaveStatusViewModel =
                ViewModelProviders.of(this).get(LeaveViewModel.class);
        View root = inflater.inflate(R.layout.fragment_leave_check_log, container, false);

        recyclerView = root.findViewById(R.id.rc_worker_leave_log);
        leaveStatusRVAdapter = new LeaveStatusRVAdapter(leaveStatusViewModel, getContext());
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setAdapter(leaveStatusRVAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        Context context = getContext();
        SharedPref sharedPref = new SharedPref(context);
        String currentUserId = sharedPref.getEMP_ID();

        leaveStatusViewModel.initwithUserId(currentUserId);
        leaveStatusViewModel.setDataChangedListener(() -> {
            getActivity().runOnUiThread(() -> {
                leaveStatusRVAdapter.notifyDataSetChanged();
                recyclerView.setVisibility(View.VISIBLE);
            });
        });
        Log.d("TAGXXX","log");
        return root;
    }
}
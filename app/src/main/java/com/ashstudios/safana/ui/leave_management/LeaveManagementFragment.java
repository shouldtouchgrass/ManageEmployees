package com.ashstudios.safana.ui.leave_management;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ashstudios.safana.models.LeaveModel;
import com.ashstudios.safana.R;
import com.ashstudios.safana.adapters.LeaveManagementRVAdapter;
import com.ashstudios.safana.others.SwipeToDeleteCallback;
import com.ashstudios.safana.ui.search.SearchViewModel;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class LeaveManagementFragment extends Fragment {

    static private LeaveManagementViewModel leaveManagementViewModel;
    static RecyclerView recyclerView;
    private Boolean isUndo = false;
    static private LeaveManagementRVAdapter adapter;
    private ConstraintLayout constraintLayout;

    public String data;
    private static DataChangedListener listener;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        leaveManagementViewModel =
                ViewModelProviders.of(this).get(LeaveManagementViewModel.class);
        View root = inflater.inflate(R.layout.fragment_leave_management, container, false);

        constraintLayout = root.findViewById(R.id.cl_leave_management);
        recyclerView = root.findViewById(R.id.rc_worker_leave_requests);
        adapter = new LeaveManagementRVAdapter(leaveManagementViewModel, getContext());
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        leaveManagementViewModel.InitData();
        leaveManagementViewModel.setDataChangedListener(() -> {
            getActivity().runOnUiThread(() -> {
                adapter.notifyDataSetChanged();
                recyclerView.setVisibility(View.VISIBLE);
            });
        });
        enableSwipeToCompleteAndUndo();

        return root;
    }
    public static void showLeaveDialog(Context context,String Date,String DateEnd,String Reason,int position){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.leave_management_dialog,null);
        builder.setView(view);
        TextView ll_date = view.findViewById(R.id.ll_date);
        TextView ll_dateend = view.findViewById(R.id.ll_date_end);
        TextView ll_reason = view.findViewById(R.id.ll_reason);
        Button btn_accept = view.findViewById(R.id.btn_accept);
        Button btn_reject = view.findViewById(R.id.btn_reject);
        Button btn_calendar = view.findViewById(R.id.btn_go_to_calender);
        //
        ll_date.setText(Date);
        ll_dateend.setText(DateEnd);
        ll_reason.setText(Reason);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        AlertDialog dialog = builder.create();
        //
        btn_accept.setOnClickListener(v -> {
            db.collection("Leaves").document(Date).update("Status","Accept")
                    .addOnCompleteListener(aVoid->{
                        Log.d("TAG","Add Status Accept success");
                    })
                    .addOnFailureListener(e->{
                        Log.e("TAG","Error Adding Status Accept");
                    });
            adapter.removeItem(position);
            dialog.dismiss();
        });
        btn_reject.setOnClickListener(v -> {
            db.collection("Leaves").document(Date).update("Status","Reject")
                    .addOnCompleteListener(aVoid->{
                        Log.d("TAG","Add Status Reject success");
                    })
                    .addOnFailureListener(e->{
                        Log.e("TAG","Error Adding Status Reject");
                    });
            adapter.removeItem(position);
            dialog.dismiss();
        });
        dialog.show();
    }
    private void enableSwipeToCompleteAndUndo() {

        SwipeToDeleteCallback swipeToDeleteCallback = new SwipeToDeleteCallback(getActivity()) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
                isUndo = true;
                final int position = viewHolder.getAdapterPosition();
                final LeaveModel item = adapter.getLeaveModels().get(position);

                adapter.removeItem(position);

                Snackbar snackbar = Snackbar
                        .make(constraintLayout, "Leave Request Approved", Snackbar.LENGTH_LONG);
                snackbar.setAction("UNDO", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (isUndo) {
                            adapter.restoreItem(item, position);
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

    public static void sort(Context mContext, Bundle b) {
        Toast.makeText(mContext, "sorting...", Toast.LENGTH_LONG).show();
        leaveManagementViewModel.sort(b);
        LeaveManagementRVAdapter adapter = new LeaveManagementRVAdapter(leaveManagementViewModel, mContext);
        recyclerView.setAdapter(adapter);
    }
    public interface DataChangedListener {
        void onDataChanged();
    }
    public void setDataChangedListener(DataChangedListener listener) {
        this.listener = listener;
    }
}
package com.ashstudios.safana.ui.leave_management;

import android.os.Bundle;
import android.widget.Toast;

import androidx.lifecycle.ViewModel;

import com.ashstudios.safana.models.LeaveModel;
import com.ashstudios.safana.ui.worker_details.WorkerDetailsViewModel;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Objects;

public class LeaveManagementViewModel extends ViewModel {

    ArrayList<LeaveModel> leaveModels;
    LeaveManagementFragment lmg;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private WorkerDetailsViewModel.DataChangedListener listener;

    public LeaveManagementViewModel() {
        leaveModels = new ArrayList<>();
        db.collection("Leaves").get().
                addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                            String empid = document.getString("empid");
                            String datesign = document.getId();
                            String reason = document.getString("reason");
                            String name = document.getString("name");
                            String img_url = document.getString("profile_image");
                            String dateend = document.getString("to");
                            String status = document.getString("Status");
                            if(status==null) {
                                LeaveModel leaveModel = new LeaveModel(name, reason, img_url, empid, datesign, dateend);
                                leaveModels.add(leaveModel);
                            }else{

                            }
                            if (listener != null) {
                                listener.onDataChanged();
                            }
                        }
                    }else {
                        Toast.makeText(null, "Error"+ task.getException(), Toast.LENGTH_SHORT).show();
                    }
                });

    }

    public void sort(Bundle b) {
        leaveModels.remove(0);
    }
    public ArrayList<LeaveModel> getLeaveModels() {
        return leaveModels;
    }
    public interface DataChangedListener {
        void onDataChanged();
    }
    public void setDataChangedListener(WorkerDetailsViewModel.DataChangedListener listener) {
        this.listener = listener;
    }
}
package com.ashstudios.safana.ui.leave_management;

import android.os.Bundle;
import android.widget.Toast;

import androidx.lifecycle.ViewModel;

import com.ashstudios.safana.models.LeaveModel;
import com.ashstudios.safana.models.LeaveStatusModel;
import com.ashstudios.safana.ui.worker_details.WorkerDetailsViewModel;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Objects;

public class LeaveManagementViewModel extends ViewModel {

    ArrayList<LeaveModel> leaveModels;
    ArrayList<LeaveStatusModel> leaveStatusModels ;
    LeaveManagementFragment lmg;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DataChangedListener listener;

    public LeaveManagementViewModel() {
        leaveModels = new ArrayList<>();
    }
    public void InitData(){
        db.collection("Leaves").get().
                addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                            String empid = document.getString("empid");
                            String datesign = document.getString("date");
                            String reason = document.getString("reason");
                            String name = document.getString("name");
                            String img_url = document.getString("profile_image");
                            String dateend = document.getString("to");
                            String status = document.getString("Status");
                            if(status==null) {
                                LeaveModel leaveModel = new LeaveModel(name, reason, img_url, empid, datesign, dateend);
                                leaveModels.add(leaveModel);
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
        Comparator<LeaveModel> comparator = Comparator.comparing(LeaveModel::getDate);
        Collections.sort(leaveModels, comparator); // Sort the list using the comparator
    }

    public void sort_name(Bundle b) {
        Comparator<LeaveModel> comparator = Comparator.comparing(leaveModel -> leaveModel.getName().substring(0, 1));
        Collections.sort(leaveModels, comparator);
    }
    public ArrayList<LeaveModel> getLeaveModels() {return leaveModels;}
    public interface DataChangedListener {
        void onDataChanged();
    }
    public void setDataChangedListener(DataChangedListener listener) {
        this.listener = listener;
    }
}
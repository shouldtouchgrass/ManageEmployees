package com.ashstudios.safana.ui.leave;

import android.util.Log;

import androidx.lifecycle.ViewModel;

import com.ashstudios.safana.models.LeaveModel;
import com.ashstudios.safana.models.LeaveStatusModel;
import com.ashstudios.safana.ui.leave_management.LeaveManagementViewModel;
import com.ashstudios.safana.ui.project__details.ProjectDetailsViewModel;
import com.ashstudios.safana.ui.search.SearchViewModel;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

public class LeaveViewModel extends ViewModel {
    private ArrayList<LeaveStatusModel> leaveStatusModels;

    FirebaseFirestore db;
    private DataOnChangedListener listener;
    public LeaveViewModel() {
        leaveStatusModels = new ArrayList<>();
    }
    public void initwithUserId(String userID){
        db = FirebaseFirestore.getInstance();
        Log.d("TAGXXX",userID);
        db.collection("Leaves").whereEqualTo("empid",userID).get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                            String reason = document.getString("reason");
                            String name = document.getString("name");
                            String img_url = document.getString("profile_image");
                            String dateend = document.getString("to");
                            String status = document.getString("Status");
                            String date = document.getId();
                            if(status!=null) {
                                LeaveStatusModel leaveStatusModel = new LeaveStatusModel(name,reason,img_url,userID,date,dateend,status);
                                leaveStatusModels.add(leaveStatusModel);
                            }else{
                                LeaveStatusModel leaveStatusModel = new LeaveStatusModel(name,reason,img_url,userID,date,dateend,"Waiting...");
                                leaveStatusModels.add(leaveStatusModel);
                            }
                            if (listener != null) {
                                listener.onDataChanged();
                            }
                        }
                    }
                });
    }
    public ArrayList<LeaveStatusModel> getLeaveStatusModels() {
        return leaveStatusModels;
    }
    public interface DataOnChangedListener{
        void onDataChanged();
    }
    public void setDataChangedListener(DataOnChangedListener listener){
        this.listener = listener;
    }
}
package com.ashstudios.safana.ui.worker_details;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.ViewModel;

import com.ashstudios.safana.models.WorkerModel;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class WorkerDetailsViewModel extends ViewModel {

    private ArrayList<WorkerModel> workerModels;
    FirebaseFirestore db;

    WorkerModel workerModel;
    private DataChangedListener listener;


    public WorkerDetailsViewModel() {
        workerModels = new ArrayList<>();
        db = FirebaseFirestore.getInstance();
        db.collection("Employees")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Duyệt qua mỗi document trong collection
                        for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                            // Lấy dữ liệu từ mỗi document và tạo instance mới của WorkerModel
                            String name = document.getString("name");
                            String role = document.getString("role");
                            String profileImg = document.getString("profile_image");
                            String empId = document.getId(); // Lấy ID của document
                            String mail = document.getString("mail");
                            String mobile = document.getString("mobile");
                            String sex = document.getString("sex");
                            String birthdate = document.getString("birth_date");
                            String password = document.getString("password");
                            List<String> allowanceIds = (List<String>) document.get("allowance_ids");

                            workerModel = new WorkerModel(name, role, profileImg, empId, mail, mobile, sex, birthdate, password, allowanceIds);
                            workerModels.add(workerModel);
                            if(listener != null) {
                                listener.onDataChanged();
                            }
                        }
                    } else {
                        Toast.makeText(null, "Error"+ task.getException(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

//    "https://i.imgur.com/[0-9a-zA-Z]*.(jpg|png)

    public ArrayList<WorkerModel> getWorkerModels() {
        return workerModels;
    }

    public void sort(Bundle b) {
        workerModels.remove(0);
    }

    public interface DataChangedListener {
        void onDataChanged();
    }
    public void setDataChangedListener(DataChangedListener listener) {
        this.listener = listener;
    }
}
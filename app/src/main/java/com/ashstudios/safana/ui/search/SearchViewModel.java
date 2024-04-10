package com.ashstudios.safana.ui.search;


import androidx.lifecycle.ViewModel;
import com.ashstudios.safana.models.WorkerModel;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SearchViewModel extends ViewModel {

    private final FirebaseFirestore db;
    WorkerModel workerModel;
    private final ArrayList<WorkerModel> workerModels;
    private final String empid = "EMP002";
    private List<String> id_project;

    public SearchViewModel() {
        workerModels = new ArrayList<>();
        db = FirebaseFirestore.getInstance();
        db.collection("Employees").document(empid).get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                DocumentSnapshot documentSnapshot = task.getResult();
                if(documentSnapshot != null && documentSnapshot.exists()){
                    List<String> id_project = (List<String>) documentSnapshot.get("projectID");
                    if(id_project != null && !id_project.isEmpty()){
                        for(String projectId : id_project){
                            db.collection("Employees").whereArrayContains("projectID", projectId)
                                    .get()
                                    .addOnCompleteListener(task2 -> {
                                        if(task2.isSuccessful()){
                                            for (QueryDocumentSnapshot document : Objects.requireNonNull(task2.getResult())) {
                                                String empId = document.getId();

                                                // Kiểm tra nếu nhân viên đã tồn tại trong workerModels
                                                boolean isExisting = empId.equals(empid);
                                                for (WorkerModel existingWorker : workerModels) {
                                                    if(existingWorker.getEmp_id().equals(empId)){
                                                        isExisting = true;
                                                        break;
                                                    }
                                                }
                                                if(!isExisting){
                                                    String name = document.getString("name");
                                                    String role = document.getString("role");
                                                    String profileImg = document.getString("profile_image");
                                                    String mail = document.getString("mail");
                                                    String mobile = document.getString("mobile");
                                                    String sex = document.getString("sex");
                                                    String birthdate = document.getString("birth_date");
                                                    String password = document.getString("password");
                                                    List<String> allowanceIds = (List<String>) document.get("allowance_ids");

                                                    WorkerModel workerModel = new WorkerModel(name, role, profileImg, empId, mail, mobile, sex, birthdate, password, allowanceIds);
                                                    workerModels.add(workerModel);
                                                }
                                            }
                                        }
                                    });
                        }
                    }
                }
            }
        });
    }
    public ArrayList<WorkerModel> getSearchViewModel() {
        return workerModels;
    }
}
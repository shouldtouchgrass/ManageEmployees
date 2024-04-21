package com.ashstudios.safana.ui.leave_management;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.ashstudios.safana.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class    LeaveDialog {
    static FirebaseFirestore db;
    public static void showLeaveDialog(Context context,String Date,String DateEnd,String Reason,String emp_id){
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
        //
        db = FirebaseFirestore.getInstance();
        db.collection("Leaves").whereEqualTo("empid",emp_id).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        for(QueryDocumentSnapshot document: task.getResult()){
                            String documentID = document.getId();
                            btn_accept.setOnClickListener(v -> {
                                db.collection("Leaves").document(documentID).update("Status","Accept")
                                        .addOnCompleteListener(aVoid->{
                                            Log.d("TAG","Add Status Accept success");
                                        })
                                        .addOnFailureListener(e->{
                                            Log.e("TAG","Error Adding Status Accept");
                                        });
                            });
                            btn_reject.setOnClickListener(v -> {
                                db.collection("Leaves").document(documentID).update("Status","Reject")
                                        .addOnCompleteListener(aVoid->{
                                            Log.d("TAG","Add Status Reject success");
                                        })
                                        .addOnFailureListener(e->{
                                            Log.e("TAG","Error Adding Status Reject");
                                        });
                            });
                        }
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
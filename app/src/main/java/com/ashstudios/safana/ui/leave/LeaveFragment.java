package com.ashstudios.safana.ui.leave;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.ashstudios.safana.R;
import com.ashstudios.safana.others.SharedPref;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.android.material.snackbar.Snackbar;

import java.util.HashMap;

public class LeaveFragment extends Fragment {

    private LeaveViewModel toolsViewModel;
    private TextView etFrom, etTo,etReason;
    private Button ReQuest;
    private FirebaseFirestore db;

    public String from,to,reason;
    private String Collections = "Leaves";

    @SuppressLint("MissingInflatedId")
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        toolsViewModel =
                ViewModelProviders.of(this).get(LeaveViewModel.class);
        View root = inflater.inflate(R.layout.fragment_leave, container, false);
        db = FirebaseFirestore.getInstance();
        etFrom = root.findViewById(R.id.et_from);
        etTo = root.findViewById(R.id.et_to);
        etReason = root.findViewById(R.id.et_reason);
        ReQuest = root.findViewById(R.id.request);
        //
        Context context = getContext();
        SharedPref sharedPref = new SharedPref(context);
        final String empid = sharedPref.getEMP_ID();
        ReQuest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                from = etFrom.getText().toString();
                to = etTo.getText().toString();
                reason = etReason.getText().toString();
                HashMap<String,Object> leaves = new HashMap<>();
                leaves.put("empid:",empid);
                leaves.put("to:",to);
                leaves.put("reason:",reason);
                db.collection(Collections).document(from).set(leaves).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Snackbar.make(view, "Tải lên thành công!", Snackbar.LENGTH_SHORT).show();
                        } else {
                            Snackbar.make(view, "Tải lên không thành công!", Snackbar.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
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
}
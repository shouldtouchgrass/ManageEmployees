package com.ashstudios.safana.ui.allowance_management;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.ashstudios.safana.activities.CreateAllowanceActivity;
import com.ashstudios.safana.R;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class AllowanceManagementFragment extends Fragment {

    private AllowanceManagementViewModel allowanceManagementViewModel;
    private FloatingActionButton floatingActionButton;
    private AutoCompleteTextView autoCompleteTextView;
    private ChipGroup chipGroup;
    private ArrayList<String> chips;
    private Spinner spinner;
    Button mBtnGrant;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ArrayList<String> allowanceNames = new ArrayList<>();
    private ArrayList<String> employeeNames = new ArrayList<>();

    private static final int CREATE_ALLOWANCE_REQUEST = 1;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        allowanceManagementViewModel =
                ViewModelProviders.of(this).get(AllowanceManagementViewModel.class);
        View root = inflater.inflate(R.layout.fragment_allowance_management, container, false);
        floatingActionButton = root.findViewById(R.id.fab);
        autoCompleteTextView = root.findViewById(R.id.et_emp_id_name);
        chipGroup = root.findViewById(R.id.cg_emp_names);
        spinner = root.findViewById(R.id.spinner_allowance_name);
        mBtnGrant = root.findViewById(R.id.btn_grant);
        chips = new ArrayList<>();
        fetchAllowances();
        fetchEmployees();


        floatingActionButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), CreateAllowanceActivity.class);
            startActivityForResult(intent, CREATE_ALLOWANCE_REQUEST);
        });


        mBtnGrant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lấy Allowance được chọn từ Spinner
                String selectedAllowance = spinner.getSelectedItem().toString();
                // Lấy danh sách Employees được chọn từ ChipGroup
                ArrayList<String> selectedEmployees = new ArrayList<>();
                for (int i = 0; i < chipGroup.getChildCount(); i++) {
                    Chip chip = (Chip) chipGroup.getChildAt(i);
                    String employeeName = chip.getText().toString();
                    selectedEmployees.add(employeeName); // Hoặc lưu ID nếu bạn đã setup như vậy
                }
                // Cập nhật Firestore
                updateEmployeesAllowances(selectedEmployees, selectedAllowance);

                Toast.makeText(getContext(), "Granted Allowance!", Toast.LENGTH_SHORT).show();
                autoCompleteTextView.setText("");
                chipGroup.removeAllViews();
                chips.clear();
            }
        });

        String[] arraySpinner = new String[] {"-- Select Allowance --","Food Allowance", "Travel Allowance", "Laptop", "Accommodation"};
        String[] arr = new String[] {"Harsh Saglani", "Krunal Pande", "Rohit Suthar", "Manav Shah", "John Doe", "Robert Downey Jr.","Carry Minati","Tanmay Bhatt", "Bhuvan Bam", "Parth Nakil","Chaitanya Dhakre","David Levithan","Dan Brown","Bill gates","Anthony Horowitz"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1 , employeeNames);
        ArrayAdapter<String> spinner_adapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_dropdown_item, allowanceNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinner_adapter);

        autoCompleteTextView.setThreshold(1);
        autoCompleteTextView.setAdapter(adapter);

        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                final Chip chip = new Chip(getActivity());
                chip.setText(arg0.getItemAtPosition(arg2).toString());
                chip.setId(arg2);
                chip.setOnCloseIconClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        chipGroup.removeView(chip);
                        chips.remove(chip.getText());
                    }
                });
                chip.setCloseIconVisible(true);
                if(!chips.contains(arg0.getItemAtPosition(arg2).toString())) {
                    chipGroup.addView(chip);
                    chips.add(arg0.getItemAtPosition(arg2).toString());
                }
                autoCompleteTextView.setText("");
            }
        });


        return root;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CREATE_ALLOWANCE_REQUEST && resultCode == Activity.RESULT_OK) {
            // Gọi phương thức để cập nhật dữ liệu
            fetchAllowances();
        }
    }
    private void fetchAllowances() {
        db.collection("Allowances").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                allowanceNames.clear(); // Xóa dữ liệu cũ
                for (QueryDocumentSnapshot document : task.getResult()) {
                    String allowanceName = document.getId();
                    allowanceNames.add(allowanceName);
                }
                // Đảm bảo cập nhật UI trên main thread
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        // Cập nhật adapter của spinner
                        ArrayAdapter<String> spinnerAdapter = (ArrayAdapter<String>) spinner.getAdapter();
                        spinnerAdapter.notifyDataSetChanged();
                    });
                }
            } else {
                // Xử lý lỗi
            }
        });
    }

    private void fetchEmployees() {
        db.collection("Employees").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                employeeNames.clear(); // Xóa dữ liệu cũ
                for (QueryDocumentSnapshot document : task.getResult()) {
                    String employeeName = document.getString("name");
                    employeeNames.add(employeeName);
                }
                // Đảm bảo cập nhật UI trên main thread
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        // Cập nhật adapter của autoCompleteTextView
                        ArrayAdapter<String> autoCompleteAdapter = (ArrayAdapter<String>) autoCompleteTextView.getAdapter();
                        autoCompleteAdapter.notifyDataSetChanged();
                    });
                }
            } else {
                // Xử lý lỗi
            }
        });
    }
    private void updateEmployeesAllowances(ArrayList<String> employeeNames, String allowance) {
        // Lấy ID của allowance từ tên, nếu bạn lưu tên làm key, thì bỏ qua bước này

        // Duyệt qua từng Employee và cập nhật allowance_ids
        for (String employeeName : employeeNames) {
            db.collection("Employees").whereEqualTo("name", employeeName)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            DocumentReference employeeRef = documentSnapshot.getReference();

                            // Thêm allowance vào list, giả sử allowance_ids là một ArrayList
                            employeeRef.update("allowance_ids", FieldValue.arrayUnion(allowance))
                                    .addOnSuccessListener(aVoid -> Log.d("Update", "Allowance added successfully"))
                                    .addOnFailureListener(e -> Log.d("Update", "Error adding allowance", e));
                        }
                    });
        }
    }
}
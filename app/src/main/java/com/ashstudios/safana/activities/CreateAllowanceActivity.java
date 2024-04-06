package com.ashstudios.safana.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.ashstudios.safana.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class CreateAllowanceActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    Button button;
    EditText allowanceNameEditText, allowanceDurationEditText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_allowance);
        button = findViewById(R.id.btn_create);
        allowanceNameEditText = findViewById(R.id.et_allowance_name);
        allowanceDurationEditText = findViewById(R.id.et_allowance_duration);
        db  = FirebaseFirestore.getInstance();
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String allowanceName = allowanceNameEditText.getText().toString();
                String allowanceDuration = allowanceDurationEditText.getText().toString();
                // Kiểm tra dữ liệu không được để trống
                if (!allowanceName.isEmpty() && !allowanceDuration.isEmpty()) {
                    // Lưu vào Firestore
                    Map<String, Object> allowance = new HashMap<>();
                    allowance.put("duration", allowanceDuration);
                    allowance.put("url", "https://i.imgur.com/WzTVSsM.png"); // URL là chuỗi trống như yêu cầu

                    db.collection("Allowances").document(allowanceName).set(allowance)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(getBaseContext(), "Allowance Created!", Toast.LENGTH_SHORT).show();
                                setResult(Activity.RESULT_OK);
                                finish(); // Đóng activity khi hoàn tất
                            })
                            .addOnFailureListener(e -> Toast.makeText(getBaseContext(), "Error adding document", Toast.LENGTH_SHORT).show());
                } else {
                    Toast.makeText(getBaseContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Create information allowance");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}

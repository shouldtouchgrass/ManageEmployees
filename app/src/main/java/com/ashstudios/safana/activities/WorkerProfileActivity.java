package com.ashstudios.safana.activities;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.ashstudios.safana.R;
import com.ashstudios.safana.models.WorkerModel;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

public class WorkerProfileActivity extends AppCompatActivity {
    private FirebaseFirestore mDatabase;
    public static String employeeId;
    private Context mContext;
    Button btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worker_profile);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Detailed Information");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        employeeId = getIntent().getStringExtra("EMPLOYEE_ID");
        // Lấy tham chiếu đến Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        // Lấy chi tiết nhân viên từ Firestore
        if (employeeId != null) {
            db.collection("Employees").document(employeeId).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null && document.exists()) {
                        WorkerModel worker = document.toObject(WorkerModel.class);
                        // Cập nhật UI với thông tin nhân viên
                        if (worker != null) {
                            updateUI(worker);
                        }
                    }
                } else {
                    Toast.makeText(WorkerProfileActivity.this, "Error getting employee details.", Toast.LENGTH_SHORT).show();
                }
            });
        }

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
    private void updateUI(WorkerModel workerModel) {
        // Ví dụ: Cập nhật ImageView và TextView
        ImageView profileImage = findViewById(R.id.profile_img);
        TextView tvEmpId = findViewById(R.id.tv_emp_id), empName = findViewById(R.id.emp_name),
                empRole = findViewById(R.id.emp_role), tvEmail = findViewById(R.id.tv_email),
                tvMob = findViewById(R.id.tv_mob), tvSex = findViewById(R.id.tv_sex),
                tvBdate = findViewById(R.id.tv_bdate), tvAllowance = findViewById(R.id.tv_allowances_given);


        // Sử dụng Picasso hoặc một thư viện tương tự để load hình ảnh
        Picasso.get().load(workerModel.getImgUrl()).into(profileImage);

        // Cập nhật thông tin nhân viên vào TextView
        // Giả sử bạn đã thêm các getter trong WorkerModel
        tvEmpId.setText(employeeId); // Hoặc cách khác để lấy ID
        empName.setText(workerModel.getName());
        empRole.setText(workerModel.getRole());
        tvEmail.setText(workerModel.getMail());
        tvMob.setText(workerModel.getMobile());
        tvSex.setText(workerModel.getSex());
        tvBdate.setText(workerModel.getBirthdate());
        if (workerModel.getAllowance_ids() != null && !workerModel.getAllowance_ids().isEmpty()) {
            String allowances = TextUtils.join("\n", workerModel.getAllowance_ids());
            tvAllowance.setText(allowances);
        } else {
            tvAllowance.setText("No allowances"); // Hoặc một giá trị placeholder khác
        }
    }
}

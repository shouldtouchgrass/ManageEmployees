package com.ashstudios.safana.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.ashstudios.safana.R;
import com.ashstudios.safana.models.UserModel;
import com.ashstudios.safana.others.SharedPref;
import com.ashstudios.safana.utils.AndroidUtil;
import com.ashstudios.safana.utils.FirebaseUtil;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentSnapshot;

public class SplashActivity extends AppCompatActivity {
    private Context context;
    private SharedPref sharedPref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        setContentView(R.layout.activity_splash);
        FirebaseApp.initializeApp(this);
        context = SplashActivity.this;
        sharedPref = new SharedPref(context);
        if (getIntent().getExtras() != null && getIntent().getExtras().getString("userId") != null) {

            //from notification
            String userId = getIntent().getExtras().getString("userId");
            FirebaseUtil.allUserCollectionReference().document(userId).get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            Intent mainintent = new Intent(context, WorkerDashboardActivity.class);
                            mainintent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                            startActivity(mainintent);

                            Intent intent = new Intent(context, ChatActivity.class);
                            intent.putExtra("EMPLOYEE_ID", document.getId());
                            startActivity(intent);
                            finish();
                        }
                    });


        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (sharedPref.getIS_LOGGED_IN()) {
                        //if already login then open the dashboard
                        //check if it is supervisor or worker
                        if (sharedPref.getEMP_ID().contains("SUP")) {
                            Intent intent = new Intent(context, SupervisorDashboard.class);
                            finish();
                            startActivity(intent);
                        } else {
                            Intent intent = new Intent(context, WorkerDashboardActivity.class);
                            finish();
                            startActivity(intent);
                        }
                    } else {
                        Intent intent = new Intent(context, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
            }, 1500);
        }
    }
}

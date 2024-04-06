package com.ashstudios.safana.activities;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.ashstudios.safana.Fragments.BottomSheetTaskFragment;
import com.ashstudios.safana.R;
import com.ashstudios.safana.others.Msg;
import com.ashstudios.safana.others.SharedPref;
import com.ashstudios.safana.ui.mytasks.MyTasksFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

public class WorkerDashboardActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ImageView mProfileImage;
    private TextView mTvName,mTvEmail;
    private NavigationView navigationView;
    private Bundle taskSortBundle;
    private LinearLayout linearLayout;
    private SharedPref sharedPref;
    private TextView nav_name, nav_email;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worker_dashboard);
        db = FirebaseFirestore.getInstance();
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        taskSortBundle = new Bundle();
        sharedPref = new SharedPref(WorkerDashboardActivity.this);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        //setting nav header items
        View header = navigationView.getHeaderView(0);
        nav_name = header.findViewById(R.id.nav_name);
        nav_email = header.findViewById(R.id.nav_email);
        nav_name.setText(sharedPref.getNAME());
        nav_email.setText(sharedPref.getEMAIL());
        header.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WorkerDashboardActivity.this, OwnWorkerProfileActivity.class);
                startActivity(intent);
            }
        });
        mProfileImage = header.findViewById(R.id.profile_image);
        loadNavViewHeaderImage();
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_tasks, R.id.nav_calendar, R.id.nav_search,
                R.id.nav_leave, R.id.nav_allowance, R.id.nav_project_status,
                R.id.nav_scaner_qr, R.id.nav_calendar_attendance)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        navigationView.setItemIconTintList(null);
        //setting icon tint to white for other menu items
        setDefaultIconTint();
        //setting custom icon tint to display project status
        MenuItem favoriteItem = navigationView.getMenu().findItem(R.id.nav_project_status);
        Drawable favoriteIcon = DrawableCompat.wrap(favoriteItem.getIcon());
        ColorStateList colorSelector = ResourcesCompat.getColorStateList(getResources(), R.color.success, getTheme());
        DrawableCompat.setTintList(favoriteIcon, colorSelector);
        favoriteItem.setIcon(favoriteIcon);
    }
    private void loadNavViewHeaderImage() {
        db.collection("Employees").document(sharedPref.getEMP_ID()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot documentSnapshot = task.getResult();
                if(!documentSnapshot.getData().containsKey("profile_image") || documentSnapshot.getString("profile_image").equals("")) {
                    ColorGenerator colorGenerator = ColorGenerator.MATERIAL;
                    int color = colorGenerator.getRandomColor();
                    TextDrawable textDrawable = TextDrawable.builder().buildRect(String.valueOf(sharedPref.getNAME().charAt(0)),color);
                    mProfileImage.setImageDrawable(textDrawable);
                }
                else {
                    Picasso.get().load(task.getResult().getString("profile_image")).fit().into(mProfileImage);
                }
            }
        });
    }

    private void setDefaultIconTint() {
        ColorStateList colorSelector1 = ResourcesCompat.getColorStateList(getResources(), android.R.color.white, getTheme());

        MenuItem favoriteItem = navigationView.getMenu().findItem(R.id.nav_tasks);
        Drawable favoriteIcon = DrawableCompat.wrap(favoriteItem.getIcon());
        DrawableCompat.setTintList(favoriteIcon, colorSelector1);
        favoriteItem.setIcon(favoriteIcon);

        MenuItem favoriteItem1 = navigationView.getMenu().findItem(R.id.nav_calendar);
        Drawable favoriteIcon1 = DrawableCompat.wrap(favoriteItem1.getIcon());
        DrawableCompat.setTintList(favoriteIcon1, colorSelector1);
        favoriteItem1.setIcon(favoriteIcon1);

        MenuItem favoriteItem2 = navigationView.getMenu().findItem(R.id.nav_search);
        Drawable favoriteIcon2 = DrawableCompat.wrap(favoriteItem2.getIcon());
        DrawableCompat.setTintList(favoriteIcon2, colorSelector1);
        favoriteItem2.setIcon(favoriteIcon2);

        MenuItem favoriteItem3 = navigationView.getMenu().findItem(R.id.nav_allowance);
        Drawable favoriteIcon3 = DrawableCompat.wrap(favoriteItem3.getIcon());
        DrawableCompat.setTintList(favoriteIcon3, colorSelector1);
        favoriteItem3.setIcon(favoriteIcon3);

        MenuItem favoriteItem4 = navigationView.getMenu().findItem(R.id.nav_leave);
        Drawable favoriteIcon4 = DrawableCompat.wrap(favoriteItem4.getIcon());
        DrawableCompat.setTintList(favoriteIcon4, colorSelector1);
        favoriteItem4.setIcon(favoriteIcon4);

        MenuItem favoriteItem5 = navigationView.getMenu().findItem(R.id.nav_worker_laws);
        Drawable favoriteIcon5 = DrawableCompat.wrap(favoriteItem5.getIcon());
        DrawableCompat.setTintList(favoriteIcon5, colorSelector1);
        favoriteItem5.setIcon(favoriteIcon5);
        linearLayout = findViewById(R.id.ll_logout);
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPref sharedPref = new SharedPref(getBaseContext());
                sharedPref.logout();
                ExitActivity.exitApplicationAndRemoveFromRecent(WorkerDashboardActivity.this);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.worker_dashboard, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                if(navigationView.getMenu().findItem(R.id.nav_tasks).isChecked())
                {
                    BottomSheetTaskFragment bottomSheetTaskFragment = new BottomSheetTaskFragment();
                    taskSortBundle = initTaskSortBundle();  // for remembering the sorting. Otherwise default sorting is always displayed not the selected one
                    bottomSheetTaskFragment.setArguments(taskSortBundle);
                    bottomSheetTaskFragment.show(getSupportFragmentManager(), "bstf");
                }
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void onWorkerDetailsSortingChanged(Bundle b)
    {
        taskSortBundle = (Bundle)b.clone();
        MyTasksFragment.sort(getBaseContext(),taskSortBundle);
    }

    private Bundle initTaskSortBundle()
    {
        if(taskSortBundle.isEmpty())
        {
            taskSortBundle.putBoolean("isSupervisor",false);
            taskSortBundle.putBoolean("dateChip",false);
            taskSortBundle.putBoolean("completedChip",false);
            taskSortBundle.putBoolean("incompleteChip",false);
            return taskSortBundle;
        }
        else
        {
            return taskSortBundle;
        }
    }
    
}

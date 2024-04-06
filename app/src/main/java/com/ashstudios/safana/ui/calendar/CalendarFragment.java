package com.ashstudios.safana.ui.calendar;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.ashstudios.safana.R;
import com.ashstudios.safana.adapters.SupervisorTaskAdapter;
import com.ashstudios.safana.adapters.TaskAdapter;
import com.ashstudios.safana.models.TaskModel;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class CalendarFragment extends Fragment implements TaskCalendarAdapter.OnItemClickListener {

    private TextView monthYearText;
    private Button prev_button, next_button;
    RecyclerView calendarRecyclerView;
    CalendarViewModel calendarViewModel;
    SupervisorTaskAdapter supervisorTaskAdapter;
    TaskCalendarAdapter taskCalendarAdapter;
    private LocalDate selectedDate;
    LinearLayout linearLayout;
    ArrayList<TaskModel> taskModels = new ArrayList<>();
    ArrayList<DayModel> daysInMonth;
    ArrayList<String> dateList;
    TextView tv_tasks;
    private static  final String TAG = "CALENDAR_FRAGMENT";

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        calendarViewModel = ViewModelProviders.of(this).get(CalendarViewModel.class);

        View root = inflater.inflate(R.layout.fragment_calendar, container, false);
        daysInMonth = new ArrayList<>();

        tv_tasks =root.findViewById(R.id.event);
        linearLayout = root.findViewById(R.id.calendar_layout);
        calendarRecyclerView = root.findViewById(R.id.calendarRecyclerView);
        monthYearText = root.findViewById(R.id.monthYearTV);
        selectedDate = LocalDate.now();
        setMonthView();

        prev_button = root.findViewById(R.id.previous);
        prev_button.setOnClickListener(v -> {
            previousMonthAction();
        });

        next_button = root.findViewById(R.id.next);
        next_button.setOnClickListener(v -> {
            nextMonthAction();
        });

        RecyclerView recyclerView = root.findViewById(R.id.rv_calendar_task);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        //set the adapter
        supervisorTaskAdapter = new SupervisorTaskAdapter(getActivity(), taskModels);
        recyclerView.setAdapter(supervisorTaskAdapter);
        supervisorTaskAdapter.notifyDataSetChanged();
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

    private void setMonthView() {
        monthYearText.setText(monthYearFromDate(selectedDate));

        YearMonth yearMonth = YearMonth.from(selectedDate);
        int size = yearMonth.lengthOfMonth();

        LocalDate firstOfMonth = selectedDate.withDayOfMonth(1);
        DayOfWeek dayOfWeek = firstOfMonth.getDayOfWeek();
        int startDayOfWeekValue = dayOfWeek.getValue();
        String year = String.valueOf(selectedDate.getYear());

        String month = String.valueOf(selectedDate.getMonthValue());
        String finalMonth = String.format("%02d", Integer.parseInt(month));



        // Initialize Firestore outside the completion listener
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Tasks").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Set<String> daySet = new HashSet<>(); // Initialize daySet here
                dateList = new ArrayList<>(); // Initialize dateList here

                for (QueryDocumentSnapshot document : task.getResult()) {
                    String dueDate = document.getString("Due Date");
                    if (dueDate != null) {
                        dateList.add(dueDate);
                    }
                }

                daysInMonth = new ArrayList<>(); // Initialize daysInMonth here

                for (int i = 1; i <= 42; i++) {
                    if (i <= startDayOfWeekValue || i > size + startDayOfWeekValue) {
                        daysInMonth.add(new DayModel(" ", finalMonth, year, Color.WHITE));
                    } else {
                        String day = String.valueOf(i - startDayOfWeekValue);
                        String date = day + "/"+ finalMonth +"/" +year;    //bind elements of the date to compare
                        //Log.d(TAG, date);
                        if (dateList.contains(date)) {
                            daysInMonth.add(new DayModel(day, finalMonth, year, Color.GREEN));

                        } else {
                            daysInMonth.add(new DayModel(day, finalMonth, year, Color.WHITE));
                        }
                    }
                }

                // Update UI on the main thread
                getActivity().runOnUiThread(() -> {
                    calendarRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 7));
                    taskCalendarAdapter = new TaskCalendarAdapter(daysInMonth, this);
                    calendarRecyclerView.setAdapter(taskCalendarAdapter);
                });
            } else { //task.isSuccessful()
                Log.d(TAG, "Fail to read due date!!!");
            }
        });
    }


    private String monthYearFromDate(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return date.format(formatter);
    }

    public void previousMonthAction() {
        selectedDate = selectedDate.minusMonths(1);
        setMonthView();
    }

    public void nextMonthAction() {
        selectedDate = selectedDate.plusMonths(1);
        setMonthView();
    }

    @Override
    public void onItemClick(int position, String dayText) {
        if(!dayText.equals("")) {
            taskModels.clear();   //clear taskModels to display new events
            GetData(dayText);
        }
    }

    public void GetData(String chooseDate){

        Log.d(TAG, chooseDate);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Tasks")
                .whereEqualTo("Due Date", chooseDate)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {

                            String taskID = document.getId();
                            String taskName = document.getString("Task Name");
                            String dueDate = document.getString("Due Date");
                            TaskModel taskModel = new TaskModel(taskID, taskName, dueDate);
                            taskModels.add(taskModel);
                        }
                        supervisorTaskAdapter.notifyDataSetChanged();

                        if (taskModels.isEmpty()) {
                            tv_tasks.setText("No events on " + chooseDate);
                        } else {
                            tv_tasks.setText(chooseDate + " has some events below");
                        }
                    } else {//task.isSuccessful()
                        Log.d(TAG, "Error!!!");
                    }
                });
    }
}
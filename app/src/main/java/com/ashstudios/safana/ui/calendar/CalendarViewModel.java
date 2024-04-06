package com.ashstudios.safana.ui.calendar;

import androidx.lifecycle.ViewModel;

import com.ashstudios.safana.models.TaskModel;

import java.util.ArrayList;

public class CalendarViewModel extends ViewModel {

    private ArrayList<TaskModel> arrayListMutableLiveData;
    public CalendarViewModel() {
        arrayListMutableLiveData = new ArrayList<>();
        getData();
    }

    public ArrayList<TaskModel> getArrayListMutableLiveData() {
        return arrayListMutableLiveData;
    }

    public void getData() {

    }
}
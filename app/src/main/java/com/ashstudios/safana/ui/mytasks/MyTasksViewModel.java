package com.ashstudios.safana.ui.mytasks;

import android.os.Bundle;
import androidx.lifecycle.ViewModel;
import com.ashstudios.safana.models.TaskModel;
import java.util.ArrayList;

public class MyTasksViewModel extends ViewModel {
    private ArrayList<TaskModel> arrayListMutableLiveData;
    public MyTasksViewModel() {
        arrayListMutableLiveData = new ArrayList<>();
        getData();
    }

    public ArrayList<TaskModel> getArrayListMutableLiveData() {
        return arrayListMutableLiveData;
    }

    public void getData() {
    }

    public void sort(Bundle b) {
        arrayListMutableLiveData.remove(0);
    }
}
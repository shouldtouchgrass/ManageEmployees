package com.ashstudios.safana.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ashstudios.safana.R;

import java.util.List;

public class TaskStatusCategoryAdapter extends ArrayAdapter<TaskStatusCategory> {

    public TaskStatusCategoryAdapter(Context context, int resource, List<TaskStatusCategory> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, @Nullable View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_selected, parent, false);
        TextView tvSelected = convertView.findViewById(R.id.tv_selected);
        TaskStatusCategory category = this.getItem(position);

        if(category!=null){
            tvSelected.setText(category.getName());
        }
        return  convertView;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category, parent, false);
        TextView tvCatergory = convertView.findViewById(R.id.tv_category);
        TaskStatusCategory category = this.getItem(position);

        if(category!=null){
            tvCatergory.setText(category.getName());
        }
        return  convertView;
    }
}

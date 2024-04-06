package com.ashstudios.safana.ui.tasks;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ashstudios.safana.R;
import com.ashstudios.safana.adapters.TaskStatusCategory;

import java.util.List;

public class ProjectNameListAdapter extends ArrayAdapter<ProjectNameList> {
    public ProjectNameListAdapter(Context context, int resource, List<ProjectNameList> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, @Nullable View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_selected_project_name, parent, false);
        TextView tvSelected = convertView.findViewById(R.id.tv_selected);
        ProjectNameList projectName = this.getItem(position);

        if(projectName!=null){
            tvSelected.setText(projectName.getName());
        }
        return  convertView;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category_project_name, parent, false);
        TextView tvCatergory = convertView.findViewById(R.id.tv_category);
        ProjectNameList projectName = this.getItem(position);

        if(projectName!=null){
            tvCatergory.setText(projectName.getName());
        }
        return  convertView;
    }
}

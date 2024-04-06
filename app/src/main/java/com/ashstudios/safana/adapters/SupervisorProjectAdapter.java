package com.ashstudios.safana.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.ashstudios.safana.R;
import com.ashstudios.safana.activities.ProjectDetailsActivity;
import com.ashstudios.safana.activities.TaskDetailsActivity;
import com.ashstudios.safana.models.ProjectModel;
import com.ashstudios.safana.models.TaskModel;

import java.util.ArrayList;

public class SupervisorProjectAdapter extends RecyclerView.Adapter<SupervisorProjectAdapter.MyViewHolder> {
    private Context context;
    private ArrayList<ProjectModel> projectModelArrayList;

    public SupervisorProjectAdapter(Context context, ArrayList<ProjectModel> projectModelArrayList) {
        this.context = context;
        this.projectModelArrayList = projectModelArrayList;
    }

    public void restoreItem(ProjectModel item, int position) {
        projectModelArrayList.add(position,item);
        notifyDataSetChanged();
    }

    public void removeItem(int position) {
        projectModelArrayList.remove(position);
        notifyDataSetChanged();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private TextView name;
        private TextView start_date;
        private TextView due_date;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.iv_count);
            name = itemView.findViewById(R.id.tv_title);
            start_date = itemView.findViewById(R.id.tv_start_date);
            due_date = itemView.findViewById(R.id.tv_due_date);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        ProjectModel projectModel = projectModelArrayList.get(position);
                        Intent intent = new Intent(context, ProjectDetailsActivity.class);
                        intent.putExtra("projectID", projectModel.getProjectID());
                        intent.putExtra("taskstatuslist", projectModel.getTaskStatusList());
                        context.startActivity(intent);
                    }
                }
            });
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_project_model,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        ProjectModel projectModel = projectModelArrayList.get(position);
        ColorGenerator colorGenerator = ColorGenerator.MATERIAL;
        TextDrawable textDrawable = TextDrawable.builder().buildRect(String.valueOf(position+1),colorGenerator.getRandomColor());
        holder.imageView.setImageDrawable(textDrawable);
        holder.name.setText(projectModel.getTitle());
        holder.start_date.setText(projectModel.getStartDate());
        holder.due_date.setText(projectModel.getDueDate());
    }

    @Override
    public int getItemCount() {
        return projectModelArrayList.size();
    }

    public ArrayList<ProjectModel> getData() {
        return projectModelArrayList;
    }
}


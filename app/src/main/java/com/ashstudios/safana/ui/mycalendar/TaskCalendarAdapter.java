package com.ashstudios.safana.ui.mycalendar;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ashstudios.safana.R;

import java.util.ArrayList;

public class TaskCalendarAdapter extends RecyclerView.Adapter<TaskCalendarAdapter.MyViewHolder> {
    String selectedDate;
    private final ArrayList<DayModel> daysOfMonth;
    private OnItemClickListener onItemClickListener;

    public TaskCalendarAdapter(ArrayList<DayModel> daysOfMonth, OnItemClickListener onItemClickListener) {
        this.daysOfMonth = daysOfMonth;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.day_cell_model, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        DayModel dayModel = daysOfMonth.get(position);
        holder.bind(dayModel);
    }

    @Override
    public int getItemCount() {
        return daysOfMonth.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView dayOfMonth;
        private LinearLayout calendarCellLL;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            dayOfMonth = itemView.findViewById(R.id.cellDate);
            calendarCellLL = itemView.findViewById(R.id.calendarCellLayout);
            itemView.setOnClickListener(this);
        }

        public void bind(DayModel dayModel) {
            dayOfMonth.setText(dayModel.getDay());
            calendarCellLL.setBackgroundColor(dayModel.getColor());
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                DayModel dayModel = daysOfMonth.get(position);
                selectedDate = dayModel.getDay() + "/" + dayModel.getMonth() + "/" + dayModel.getYear();
                Toast.makeText(v.getContext(), selectedDate, Toast.LENGTH_LONG).show();
                onItemClickListener.onItemClick(position, selectedDate);
            }
        }
    }

    interface OnItemClickListener {
        void onItemClick(int position, String dayText);
    }
}
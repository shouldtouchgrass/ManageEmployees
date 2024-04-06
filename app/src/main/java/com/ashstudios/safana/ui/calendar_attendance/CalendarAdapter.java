package com.ashstudios.safana.ui.calendar_attendance;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.ashstudios.safana.R;

import java.util.ArrayList;

class CalendarAdapter extends RecyclerView.Adapter<CalendarViewHolder>
{
    private final ArrayList<DayItem> daysOfMonth;
    private final ArrayList<DayItem> greenDays;
    private final OnItemListener onItemListener;

    public CalendarAdapter(ArrayList<DayItem> daysOfMonth, ArrayList<DayItem> greenDays, OnItemListener onItemListener)
    {
        this.daysOfMonth = daysOfMonth;
        this.greenDays = greenDays;
        this.onItemListener = onItemListener;
    }

    @NonNull
    @Override
    public CalendarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.calendar_cell, parent, false);
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.height = (int) (parent.getHeight() * 0.166666666);


        return new CalendarViewHolder(view, onItemListener);
    }

    @Override
    public void onBindViewHolder(@NonNull CalendarViewHolder holder, int position)
    {

        int gray = ContextCompat.getColor(holder.calendarCellLL.getContext(), R.color.gray);
        int lightGreen = ContextCompat.getColor(holder.calendarCellLL.getContext(), R.color.light_green);
        int green = ContextCompat.getColor(holder.calendarCellLL.getContext(), R.color.green);
        int mediumDarkGreen = ContextCompat.getColor(holder.calendarCellLL.getContext(), R.color.medium_dark_green);
        int darkGreen = ContextCompat.getColor(holder.calendarCellLL.getContext(), R.color.dark_green);
        DayItem dayItem = daysOfMonth.get(holder.getAdapterPosition());
        holder.dayOfMonth.setText(String.valueOf(dayItem.getDay()));
        for (int i = 0; i < greenDays.size(); i++)
        {
            if (daysOfMonth.get(position).getDay() == greenDays.get(i).getDay() && daysOfMonth.get(position).getMonth() == greenDays.get(i).getMonth())
            {
                if (greenDays.get(i).getFrequency() == 1) {
                    holder.calendarCellLL.setBackgroundColor(lightGreen);
                } else if (greenDays.get(i).getFrequency() == 2) {
                    holder.calendarCellLL.setBackgroundColor(green);
                } else if (greenDays.get(i).getFrequency() == 3) {
                    holder.calendarCellLL.setBackgroundColor(mediumDarkGreen);
                }  else if (greenDays.get(i).getFrequency() > 3) {
                    holder.calendarCellLL.setBackgroundColor(darkGreen);
                } else if (greenDays.get(i).getFrequency() < 0) {
                    holder.calendarCellLL.setBackgroundColor(gray);
                }
//                holder.parentLayout.setBackgroundColor(Color.GREEN);
            }
        }
        holder.itemView.setOnClickListener(v -> {
            if (onItemListener != null) {
                String dayText = String.valueOf(dayItem.getDay()); // Convert day to string
                // Sử dụng thông tin từ dayItem
                int day = dayItem.getDay();
                int month = dayItem.getMonth();
                if (month == 13 ) {
                    month = 1;
                }
                if (month == 14){
                    month= 2;
                }
                int year = dayItem.getYear();
                if (year == 2023){
                    year = 2024;
                }
               String chuoi ="Selected Date "+day+" "+month+" "+year;

                onItemListener.onItemClick(position, chuoi); // Pass day as a string
            }
        });
    }

    @Override
    public int getItemCount()
    {
        return daysOfMonth.size();
    }

    public interface  OnItemListener
    {
        void onItemClick(int position, String dayText);
    }
}

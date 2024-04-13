package com.ashstudios.safana.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.ashstudios.safana.R;
import com.ashstudios.safana.models.LeaveStatusModel;
import com.ashstudios.safana.ui.leave.LeaveViewModel;
import com.ashstudios.safana.ui.leave_management.LeaveManagementViewModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class LeaveStatusRVAdapter extends RecyclerView.Adapter<LeaveStatusRVAdapter.ViewHolder>{

    private ArrayList<LeaveStatusModel> leaveStatusModels;
    private Context mContext;
    public ArrayList<LeaveStatusModel> getLeaveStatusModels() {
        return leaveStatusModels;
    }

    public LeaveStatusRVAdapter(LeaveViewModel leaveViewModel, Context mContext) {
        this.leaveStatusModels = leaveViewModel.getLeaveStatusModels();
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.worker_leave_log_list_item,parent,false);
        return (new ViewHolder(view));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        LeaveStatusModel leaveStatusModel= leaveStatusModels.get(position);
        holder.name.setText(leaveStatusModel.getName());
        holder.date.setText(leaveStatusModel.getDate());
        holder.reason.setText(leaveStatusModel.getReason());
        holder.status.setText(leaveStatusModel.getStatus());
            Picasso.get()
                .load(leaveStatusModel.getImgUrl())
                .noFade()
                .resizeDimen(R.dimen.profile_photo,R.dimen.profile_photo)
                .into(holder.circleImageView);
    }

    @Override
    public int getItemCount() {
        return leaveStatusModels.size();
    }

    public void removeItem(int position) {
        leaveStatusModels.remove(position);
        notifyDataSetChanged();
    }

    public void restoreItem(LeaveStatusModel item, int position) {
        leaveStatusModels.add(position,item);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        ImageView circleImageView;
        TextView name,date,reason,status;
        ConstraintLayout ll_worker_item;
        TextDrawable textDrawable;
        ColorGenerator colorGenerator;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            circleImageView = itemView.findViewById(R.id.profile_image_log);
            colorGenerator = ColorGenerator.MATERIAL;
            textDrawable = TextDrawable.builder().buildRect("H",colorGenerator.getRandomColor());
            name = itemView.findViewById(R.id.worker_name_log);
            date = itemView.findViewById(R.id.leave_date_log);
            reason = itemView.findViewById(R.id.reason_log);
            status = itemView.findViewById(R.id.tv_status_log);
            ll_worker_item = itemView.findViewById(R.id.ll_worker_leave_log_item);
        }
    }

}

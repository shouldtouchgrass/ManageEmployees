package com.ashstudios.safana.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.ashstudios.safana.R;
import com.ashstudios.safana.models.WorkerModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class SelectedWorkerForTaskAdapter  extends RecyclerView.Adapter<SelectedWorkerForTaskAdapter.ViewHolder>  {
    private ArrayList<WorkerModel> workerModels;
    private Context mContext;

    public SelectedWorkerForTaskAdapter(Context mContext, ArrayList<WorkerModel> workerModels ) {
        this.mContext = mContext;
        this.workerModels = workerModels;
    }

    @NonNull
    @Override
    public SelectedWorkerForTaskAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.worker_list_item,parent,false);
        return (new SelectedWorkerForTaskAdapter.ViewHolder(view));
    }

    @Override
    public void onBindViewHolder(@NonNull final SelectedWorkerForTaskAdapter.ViewHolder holder, int position) {
        WorkerModel workerModel = workerModels.get(position);
        holder.name.setText(workerModel.getName());
        holder.role.setText(workerModel.getRole());

        Picasso.get()
                .load(workerModel.getImgUrl())
                .noFade()
                .resizeDimen(R.dimen.profile_photo,R.dimen.profile_photo)
                .into(holder.circleImageView);

        holder.ll_worker_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!workerModel.isSelected())
                {
                    workerModel.setSelected(true);
                    holder.circleImageView.setImageDrawable(v.getResources().getDrawable(R.drawable.ic_tick));
                }
                else
                {
                    workerModel.setSelected(false);
                    Picasso.get()
                            .load(workerModel.getImgUrl())
                            .noFade()
                            .resizeDimen(R.dimen.profile_photo,R.dimen.profile_photo)
                            .into(holder.circleImageView);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return workerModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        ImageView circleImageView;
        TextView name,role;
        LinearLayout ll_worker_item;
        TextDrawable textDrawable;
        ColorGenerator colorGenerator;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            circleImageView = itemView.findViewById(R.id.profile_image);
            colorGenerator = ColorGenerator.MATERIAL;
            textDrawable = TextDrawable.builder().buildRect("H",colorGenerator.getRandomColor());
            name = itemView.findViewById(R.id.worker_name);
            role = itemView.findViewById(R.id.worker_role);
            ll_worker_item = itemView.findViewById(R.id.ll_worker_item);
        }
    }

    public String getSelectedWorker() {
        for (WorkerModel workerModel : workerModels) {
            if (workerModel.isSelected()) {
                return workerModel.getEmp_id();
            }
        }
        return "";
    }
}

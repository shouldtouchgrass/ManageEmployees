package com.ashstudios.safana.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ashstudios.safana.R;
import com.ashstudios.safana.activities.ChatActivity;
import com.ashstudios.safana.models.ChatroomModel;
import com.ashstudios.safana.models.UserModel;
import com.ashstudios.safana.utils.FirebaseUtil;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.squareup.picasso.Picasso;

public class RecentChatRecyclerAdapter extends FirestoreRecyclerAdapter<ChatroomModel, RecentChatRecyclerAdapter.ChatroomModelViewHolder> {

    Context context;

    public RecentChatRecyclerAdapter(@NonNull FirestoreRecyclerOptions<ChatroomModel> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull ChatroomModelViewHolder holder, int position, @NonNull ChatroomModel model) {
        FirebaseUtil.getOtherUserFromChatroom(model.getUserIds())
                .get().addOnCompleteListener(task -> {
                        if(task.isSuccessful() && task.getResult() != null){
                            String userName = task.getResult().getString("name");
                            String profileImg = task.getResult().getString("profile_image");
                            Picasso.get().load(profileImg).into(holder.profilePic);
                            boolean lastMessageSentByMe = model.getLastMessageSenderId().equals(FirebaseUtil.currentUserId());
                            holder.lastMessageTime.setText(FirebaseUtil.timestampToString(model.getLastMessageTimestamp()));
                            holder.usernameText.setText(userName);
                            if(lastMessageSentByMe) {
                                holder.lastMessageText.setText("You : " + model.getLastMessage());
                                holder.lastMessageText.setTypeface(null, Typeface.NORMAL);
                            }else {
                                holder.lastMessageText.setText(model.getLastMessage());
                                holder.lastMessageText.setTypeface(null, Typeface.BOLD);
                            }
                            // Hiển thị số lượng tin nhắn chưa đọc
                            String currentUserId = FirebaseUtil.currentUserId();
                            if(model.getUnreadMessageCount() != null && model.getUnreadMessageCount().containsKey(currentUserId)) {
                                int unreadCount = model.getUnreadMessageCount().get(currentUserId);
                                if(unreadCount > 0) {
                                    holder.unreadMessage.setVisibility(View.VISIBLE);
                                    holder.unreadMessage.setText(String.valueOf(unreadCount));
                                } else {
                                    holder.unreadMessage.setVisibility(View.GONE);
                                    holder.lastMessageText.setTypeface(null, Typeface.NORMAL);
                                }
                            } else {
                                holder.unreadMessage.setVisibility(View.GONE);
                            }

                            holder.itemView.setOnClickListener(v -> {
                                Intent intent = new Intent(context, ChatActivity.class);
                                intent.putExtra("EMPLOYEE_ID", task.getResult().getId());
                                context.startActivity(intent);
                            });

                        }
                });
    }

    @NonNull
    @Override
    public ChatroomModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recent_chat_recycler_row,parent,false);
        return new ChatroomModelViewHolder(view);
    }

    class ChatroomModelViewHolder extends RecyclerView.ViewHolder{
        TextView usernameText;
        TextView lastMessageText;
        TextView lastMessageTime;
        ImageView profilePic;
        TextView unreadMessage;

        public ChatroomModelViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameText = itemView.findViewById(R.id.user_name_text);
            lastMessageText = itemView.findViewById(R.id.last_message_text);
            lastMessageTime = itemView.findViewById(R.id.last_message_time_text);
            profilePic = itemView.findViewById(R.id.profile_pic_image_view);
            unreadMessage = itemView.findViewById(R.id.unreadMessageCount);
        }
    }
}

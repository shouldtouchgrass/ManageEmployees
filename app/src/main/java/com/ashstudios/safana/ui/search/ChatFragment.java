package com.ashstudios.safana.ui.search;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ashstudios.safana.R;
import com.ashstudios.safana.adapters.RecentChatRecyclerAdapter;
import com.ashstudios.safana.models.ChatroomModel;
import com.ashstudios.safana.others.SharedPref;
import com.ashstudios.safana.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;


public class ChatFragment extends Fragment {

    RecyclerView recyclerView;
    RecentChatRecyclerAdapter adapter;

    static private ChatViewModel chatViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        chatViewModel = ViewModelProviders.of(this).get(ChatViewModel.class);
        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        recyclerView = view.findViewById(R.id.recyler_view);

        Context context = getContext();
        SharedPref sharedPref = new SharedPref(context);
        String currentUserId = sharedPref.getEMP_ID();
        FirebaseUtil.setCurrentUserId(currentUserId);
        setupRecyclerView(view);

        return view;
    }
    void setupRecyclerView (View view) {
        Context context = getContext();
        SharedPref sharedPref = new SharedPref(context);
        String currentUserId = sharedPref.getEMP_ID();
        Query query = FirebaseUtil.allChatroomCollectionReference()
                .whereArrayContains("userIds", currentUserId)
                .orderBy("lastMessageTimestamp", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<ChatroomModel> options = new FirestoreRecyclerOptions.Builder<ChatroomModel>()
                .setQuery(query, ChatroomModel.class).build();

        adapter = new RecentChatRecyclerAdapter(options, getContext()) {
            @Override
            public void onDataChanged() {
                // Nếu không có items, hiển thị TextView
                if (getItemCount() == 0) {
                    view.findViewById(R.id.tv_no_conversations).setVisibility(View.VISIBLE);
                } else {
                    view.findViewById(R.id.tv_no_conversations).setVisibility(View.GONE);
                }
            }
        };
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        adapter.startListening();

    }

    @Override
    public void onStart () {
        super.onStart();
        if (adapter != null)
            adapter.startListening();
    }

    @Override
    public void onStop () {
        super.onStop();
        if (adapter != null)
            adapter.stopListening();
    }

    @Override
    public void onResume () {
        super.onResume();
        if (adapter != null)
            adapter.notifyDataSetChanged();
    }
}

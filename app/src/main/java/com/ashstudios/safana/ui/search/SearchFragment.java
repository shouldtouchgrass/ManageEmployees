package com.ashstudios.safana.ui.search;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ashstudios.safana.R;
import com.ashstudios.safana.adapters.WorkerChatAdapter;
import com.ashstudios.safana.adapters.WorkerRVAdapter;
import com.ashstudios.safana.models.UserModel;
import com.ashstudios.safana.models.WorkerModel;
import com.ashstudios.safana.others.SharedPref;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SearchFragment extends Fragment {
    private RecyclerView rcvSearch;
    private WorkerChatAdapter adapter;
    FirebaseFirestore db;
    UserModel userModel;
    static private SearchViewModel searchViewModel;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        searchViewModel =ViewModelProviders.of(this).get(SearchViewModel.class);
        View root = inflater.inflate(R.layout.fragment_search, container, false);

        SearchView searchView = root.findViewById(R.id.search_view);
        searchView.setIconified(false);
        rcvSearch = root.findViewById(R.id.rcv_search);

        adapter = new WorkerChatAdapter(searchViewModel,getContext());

        rcvSearch.setNestedScrollingEnabled(false);
        rcvSearch.setAdapter(adapter);
        rcvSearch.setLayoutManager(new LinearLayoutManager(getContext()));

        Context context = getContext();
        SharedPref sharedPref = new SharedPref(context);
        String currentUserId = sharedPref.getEMP_ID();

        searchViewModel.initWithUserId(currentUserId);
        searchViewModel.setDataChangedListener2(() -> {
            getActivity().runOnUiThread(() -> {
                adapter.notifyDataSetChanged();
                rcvSearch.setVisibility(View.VISIBLE);
            });
        });


        return root;
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }
//
}
package com.ashstudios.safana.ui.search;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ashstudios.safana.R;
import com.ashstudios.safana.adapters.WorkerRVAdapter;

public class SearchFragment extends Fragment {

    static private SearchViewModel slideshowViewModel;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        slideshowViewModel =ViewModelProviders.of(this).get(SearchViewModel.class);
        View root = inflater.inflate(R.layout.fragment_search, container, false);
        SearchView searchView = root.findViewById(R.id.search_view);
        searchView.setIconified(false);
        RecyclerView rcvSearch = root.findViewById(R.id.rcv_search);
        WorkerRVAdapter MemberAdapter = new WorkerRVAdapter(slideshowViewModel,getContext());
        rcvSearch.setNestedScrollingEnabled(false);
        rcvSearch.setAdapter(MemberAdapter);
        rcvSearch.setLayoutManager(new LinearLayoutManager(getContext()));
        return root;
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }
//
}
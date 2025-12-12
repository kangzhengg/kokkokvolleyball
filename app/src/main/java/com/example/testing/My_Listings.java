package com.example.testing;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class My_Listings extends Fragment {

    private BookAdapter adapter;
    private TextView tvSubTitle;

    public My_Listings() { }

    public static My_Listings newInstance(String param1, String param2) {
        My_Listings fragment = new My_Listings();
        Bundle args = new Bundle();
        args.putString("param1", param1);
        args.putString("param2", param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my__listings, container, false);

        BookRepository.seedIfEmpty();

        tvSubTitle = view.findViewById(R.id.tvSubTitle);
        updateSubtitle();

        ImageButton btnBack = view.findViewById(R.id.BtnLeftArrow);
        btnBack.setOnClickListener(v -> getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new HomeFragment())
                .commit());

        Button btnAddBook = view.findViewById(R.id.btnAddBook);
        btnAddBook.setOnClickListener(v -> getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, Add_New_Book.newInstance("", ""))
                .addToBackStack(null)
                .commit());

        RecyclerView recyclerView = view.findViewById(R.id.rvBooks);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new BookAdapter(new ArrayList<>(BookRepository.getBooks()), count -> updateSubtitle());
        recyclerView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getParentFragmentManager().setFragmentResultListener("newBook", getViewLifecycleOwner(), (requestKey, bundle) -> {
            // Book is already added to repository in Add_New_Book, just refresh the adapter
            if (adapter != null) {
                adapter.setBooks(new ArrayList<>(BookRepository.getBooks()));
                adapter.notifyDataSetChanged();
            }
            updateSubtitle();
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (adapter != null) {
            adapter.setBooks(new ArrayList<>(BookRepository.getBooks()));
        }
        updateSubtitle();
    }

    private void updateSubtitle() {
        if (tvSubTitle != null) {
            tvSubTitle.setText(BookRepository.getBooks().size() + " books listed");
        }
    }
}
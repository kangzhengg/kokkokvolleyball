package com.example.testing;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BookAddedFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BookAddedFragment extends Fragment {

    public BookAddedFragment() {}

    public static BookAddedFragment newInstance(String param1, String param2) {
        BookAddedFragment fragment = new BookAddedFragment();
        Bundle args = new Bundle();
        args.putString("param1", param1);
        args.putString("param2", param2);
        fragment.setArguments(args);
        return fragment;
    }

    public static BookAddedFragment newInstance() {
        return new BookAddedFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_book_added, container, false);

        Button btnBackHome = view.findViewById(R.id.btnBackToHome);
        btnBackHome.setOnClickListener(v -> getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new HomeFragment())
                .commit());

        return view;
    }
}
package com.example.testing;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private HomeBookAdapter adapter;
    private String currentFilter = "Exchange";
    private TextView tvSectionTitle;
    private TextView tvEmptyState;

    public HomeFragment() {}

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        BookRepository.seedIfEmpty();

        ImageButton btnBack = view.findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> requireActivity().getOnBackPressedDispatcher().onBackPressed());

        View btnMyListings = view.findViewById(R.id.btnMyListings);
        btnMyListings.setOnClickListener(v -> getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, My_Listings.newInstance("", ""))
                .commit());

        View btnUploadBook = view.findViewById(R.id.btnUploadBook);
        btnUploadBook.setOnClickListener(v -> getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, Add_New_Book.newInstance("", ""))
                .commit());

        Button btnExchange = view.findViewById(R.id.btnExchange);
        Button btnDonate = view.findViewById(R.id.btnDonate);
        tvSectionTitle = view.findViewById(R.id.tvSectionTitle);
        tvEmptyState = view.findViewById(R.id.tvEmptyState);

        EditText etSearch = view.findViewById(R.id.etSearch);
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { }
            @Override public void afterTextChanged(Editable s) {
                applyFilters(s.toString());
            }
        });

        btnExchange.setOnClickListener(v -> {
            currentFilter = "Exchange";
            updateToggleButtons(btnExchange, btnDonate);
            applyFilters(etSearch.getText().toString());
        });

        btnDonate.setOnClickListener(v -> {
            currentFilter = "Donate";
            updateToggleButtons(btnExchange, btnDonate);
            applyFilters(etSearch.getText().toString());
        });

        RecyclerView recyclerView = view.findViewById(R.id.rvHomeBooks);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setHasFixedSize(false);
        recyclerView.setNestedScrollingEnabled(true);
        adapter = new HomeBookAdapter(this);
        recyclerView.setAdapter(adapter);

        // Initial load
        updateToggleButtons(btnExchange, btnDonate);
        applyFilters("");

        // Handle window insets for proper spacing at the top
        View rootLayout = view.findViewById(R.id.rootLayout);
        if (rootLayout != null) {
            ViewCompat.setOnApplyWindowInsetsListener(rootLayout, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(
                    v.getPaddingLeft(),
                    systemBars.top + 8, // 8dp base padding
                    v.getPaddingRight(),
                    v.getPaddingBottom()
                );
                return insets;
            });
        }

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        applyFilters(""); // refresh list with latest data
    }

    private void updateToggleButtons(Button btnExchange, Button btnDonate) {
        boolean exchangeSelected = "Exchange".equalsIgnoreCase(currentFilter);

        int selectedBg = Color.parseColor("#4A63E7");
        int unselectedBg = Color.parseColor("#E8EBF7");
        int selectedText = Color.WHITE;
        int unselectedText = ContextCompat.getColor(requireContext(), R.color.black);

        btnExchange.setBackgroundTintList(ColorStateList.valueOf(exchangeSelected ? selectedBg : unselectedBg));
        btnExchange.setTextColor(exchangeSelected ? selectedText : unselectedText);

        btnDonate.setBackgroundTintList(ColorStateList.valueOf(!exchangeSelected ? selectedBg : unselectedBg));
        btnDonate.setTextColor(!exchangeSelected ? selectedText : unselectedText);

        tvSectionTitle.setText(exchangeSelected ? "Available for Exchange" : "Available for Donation");
    }

    private void applyFilters(String query) {
        List<Book> source = BookRepository.getBooks();
        List<Book> filtered = new ArrayList<>();
        String lowerQuery = query == null ? "" : query.toLowerCase();

        for (Book book : source) {
            String status = book.getStatus() == null ? "" : book.getStatus();
            String title = book.getTitle() == null ? "" : book.getTitle();
            String author = book.getAuthor() == null ? "" : book.getAuthor();

            boolean matchesType = currentFilter.equalsIgnoreCase(status);
            boolean matchesQuery = lowerQuery.isEmpty()
                    || title.toLowerCase().contains(lowerQuery)
                    || author.toLowerCase().contains(lowerQuery);
            if (matchesType && matchesQuery) {
                filtered.add(book);
            }
        }

        adapter.submitList(filtered);
        tvEmptyState.setVisibility(filtered.isEmpty() ? View.VISIBLE : View.GONE);
    }
}
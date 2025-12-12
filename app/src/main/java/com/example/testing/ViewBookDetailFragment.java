package com.example.testing;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ViewBookDetailFragment extends Fragment {

    private static final String ARG_BOOK = "book";
    private Book book;

    public ViewBookDetailFragment() {}

    public static ViewBookDetailFragment newInstance(Book book) {
        ViewBookDetailFragment fragment = new ViewBookDetailFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_BOOK, book);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            book = (Book) getArguments().getSerializable(ARG_BOOK);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_book_detail, container, false);

        // Back button
        LinearLayout backNav = view.findViewById(R.id.back_navigation_area);
        backNav.setOnClickListener(v -> requireActivity().getOnBackPressedDispatcher().onBackPressed());

        // Populate book data
        if (book != null) {
            TextView tvTitle = view.findViewById(R.id.book_title);
            TextView tvAuthor = view.findViewById(R.id.book_author);
            TextView tvCondition = view.findViewById(R.id.condition_tag);
            TextView tvDescription = view.findViewById(R.id.book_description);
            TextView tvOwnerName = view.findViewById(R.id.owner_name);
            TextView tvOwnerPhone = view.findViewById(R.id.owner_phone_number);
            Button btnRequest = view.findViewById(R.id.bottom_action_button);

            tvTitle.setText(book.getTitle() != null ? book.getTitle() : "Unknown Title");
            tvAuthor.setText(book.getAuthor() != null ? "by " + book.getAuthor() : "by Unknown Author");
            
            // Use condition field if available, otherwise use status
            String conditionText = book.getCondition() != null && !book.getCondition().isEmpty() 
                ? book.getCondition() : (book.getStatus() != null ? book.getStatus() : "Available");
            tvCondition.setText(conditionText);
            
            // Use actual description from book, or default if empty
            String description = book.getDescription() != null && !book.getDescription().isEmpty() 
                ? book.getDescription() 
                : "A great book in excellent condition. Perfect for reading and sharing with others.";
            tvDescription.setText(description);
            
            // Owner info - using uploadedBy as owner name, default phone
            String ownerName = book.getUploadedBy() != null && !book.getUploadedBy().isEmpty() 
                ? book.getUploadedBy() : "Unknown User";
            tvOwnerName.setText(ownerName);
            tvOwnerPhone.setText("+60 12-345 6789"); // Default phone, can be extended later

            // Update button text based on book status
            if ("Donate".equalsIgnoreCase(book.getStatus())) {
                btnRequest.setText("Request Donation");
            } else {
                btnRequest.setText("Request Exchange");
            }

            // Request button click - open exchange selection
            btnRequest.setOnClickListener(v -> showExchangeDialog());
        }

        return view;
    }

    private void showExchangeDialog() {
        Dialog dialog = new Dialog(requireContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_exchange_selection);

        Window window = dialog.getWindow();
        if (window != null) {
            int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.95);
            window.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        ImageButton btnClose = dialog.findViewById(R.id.btnClose);
        Button btnAddBook = dialog.findViewById(R.id.btnAddBookForExchange);
        Button btnConfirm = dialog.findViewById(R.id.btnConfirmExchange);
        TextView tvEmpty = dialog.findViewById(R.id.tvEmptyState);
        RecyclerView rv = dialog.findViewById(R.id.rvExchangeChoices);

        List<Book> userExchangeBooks = new ArrayList<>();
        for (Book b : BookRepository.getBooks()) {
            boolean isExchange = b.getStatus() != null && "Exchange".equalsIgnoreCase(b.getStatus());
            if (isExchange) {
                userExchangeBooks.add(b);
            }
        }

        ExchangeChoiceAdapter adapter = new ExchangeChoiceAdapter(userExchangeBooks, selected -> {
            btnConfirm.setEnabled(selected != null);
            btnConfirm.setAlpha(selected != null ? 1f : 0.5f);
        });
        rv.setLayoutManager(new LinearLayoutManager(requireContext()));
        rv.setAdapter(adapter);

        boolean hasBooks = !userExchangeBooks.isEmpty();
        tvEmpty.setVisibility(hasBooks ? View.GONE : View.VISIBLE);
        btnConfirm.setEnabled(hasBooks);
        btnConfirm.setAlpha(hasBooks ? 1f : 0.5f);

        btnClose.setOnClickListener(v -> dialog.dismiss());
        btnAddBook.setOnClickListener(v -> {
            dialog.dismiss();
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, Add_New_Book.newExchangeOnlyInstance())
                    .addToBackStack(null)
                    .commit();
        });

        btnConfirm.setOnClickListener(v -> {
            Book selected = adapter.getSelected();
            if (selected != null) {
                dialog.dismiss();
                showExchangeDoneDialog();
            }
        });

        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }

    private void showExchangeDoneDialog() {
        Dialog dialog = new Dialog(requireContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.fragment_request_sent_pop_up);

        Window window = dialog.getWindow();
        if (window != null) {
            int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.9);
            window.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        View btnClose = dialog.findViewById(R.id.btn_close);
        TextView tvTitle = dialog.findViewById(R.id.text_title);
        TextView tvMessage = dialog.findViewById(R.id.text_message);
        tvTitle.setText("Exchange Done");
        tvMessage.setText("This exchange will post to the forum.");

        btnClose.setOnClickListener(v -> dialog.dismiss());
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }

    private static class ExchangeChoiceAdapter extends RecyclerView.Adapter<ExchangeChoiceAdapter.Holder> {
        private final List<Book> books;
        private final OnSelectListener listener;
        private int selectedPos = -1;

        interface OnSelectListener {
            void onSelected(Book book);
        }

        ExchangeChoiceAdapter(List<Book> books, OnSelectListener listener) {
            this.books = books;
            this.listener = listener;
        }

        @NonNull
        @Override
        public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_exchange_choice, parent, false);
            return new Holder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull Holder holder, int position) {
            Book book = books.get(position);
            holder.title.setText(book.getTitle());
            holder.author.setText(book.getAuthor());
            holder.radio.setChecked(position == selectedPos);

            View.OnClickListener selectListener = v -> {
                int oldPos = selectedPos;
                selectedPos = holder.getAdapterPosition();
                if (oldPos >= 0) {
                    notifyItemChanged(oldPos);
                }
                notifyItemChanged(selectedPos);
                if (listener != null) {
                    listener.onSelected(getSelected());
                }
            };
            holder.radio.setOnClickListener(selectListener);
            holder.itemView.setOnClickListener(selectListener);
        }

        @Override
        public int getItemCount() {
            return books.size();
        }

        void addBook(Book book) {
            books.add(book);
            notifyItemInserted(books.size() - 1);
            if (listener != null) {
                listener.onSelected(getSelected());
            }
        }

        Book getSelected() {
            if (selectedPos >= 0 && selectedPos < books.size()) {
                return books.get(selectedPos);
            }
            return null;
        }

        static class Holder extends RecyclerView.ViewHolder {
            final TextView title;
            final TextView author;
            final RadioButton radio;

            Holder(@NonNull View itemView) {
                super(itemView);
                title = itemView.findViewById(R.id.tvBookTitle);
                author = itemView.findViewById(R.id.tvBookAuthor);
                radio = itemView.findViewById(R.id.rbSelect);
            }
        }
    }
}


package com.example.bookcare_qy;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

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

            // Request button click - handle exchange/donation request
            btnRequest.setOnClickListener(v -> {
                if ("Exchange".equalsIgnoreCase(book.getStatus())) {
                    handleExchangeRequest();
                } else {
                    handleDonationRequest();
                }
            });
        }

        return view;
    }

    private void handleExchangeRequest() {
        CreditManager creditManager = new CreditManager(requireContext());

        // Check if user has enough credits
        if (!creditManager.hasEnoughCredits(1)) {
            showInsufficientCreditsDialog();
            return;
        }

        // Deduct 1 credit and proceed with exchange
        if (creditManager.deductCredits(1)) {
            // Remove the book from the repository after successful exchange
            if (book != null) {
                BookRepository.removeBook(book);
            }
            showExchangeDoneDialog();
        } else {
            showInsufficientCreditsDialog();
        }
    }

    private void handleDonationRequest() {
        // Donations don't require credits
        showExchangeDoneDialog();
    }

    private void showInsufficientCreditsDialog() {
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
        tvTitle.setText("Insufficient Credits");

        CreditManager creditManager = new CreditManager(requireContext());
        int currentCredits = creditManager.getCredits();
        tvMessage.setText("You need 1 credit to request an exchange. You currently have " +
                currentCredits + " credit(s). Add a book for exchange to earn credits!");

        btnClose.setOnClickListener(v -> dialog.dismiss());
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

        btnClose.setOnClickListener(v -> {
            dialog.dismiss();
            // Navigate back to home fragment to see updated list
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, HomeFragment.newInstance())
                    .commit();
        });
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }
}
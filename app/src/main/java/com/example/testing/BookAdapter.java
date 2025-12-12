package com.example.testing;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.BookViewHolder> {

    private List<Book> books;
    public interface OnBooksChangedListener {
        void onBooksChanged(int count);
    }
    private final OnBooksChangedListener listener;

    public BookAdapter(List<Book> books) {
        this(books, null);
    }

    public BookAdapter(List<Book> books, OnBooksChangedListener listener) {
        this.books = books;
        this.listener = listener;
    }

    public static class BookViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvAuthor, tvStatus, tvViews, tvInterested;
        ImageButton btnDelete;
        public BookViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvBookTitle);
            tvAuthor = itemView.findViewById(R.id.tvBookAuthor);
            tvStatus = itemView.findViewById(R.id.tvBookStatus);
            tvViews = itemView.findViewById(R.id.tvViews);
            tvInterested = itemView.findViewById(R.id.tvInterested);
            btnDelete = itemView.findViewById(R.id.btnDelete);

        }
    }

    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_book, parent, false);
        return new BookViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {
        Book book = books.get(position);
        holder.tvTitle.setText(book.getTitle());
        holder.tvAuthor.setText(book.getAuthor());
        holder.tvStatus.setText(book.getStatus());
        holder.tvViews.setText(book.getViews() + " views");
        holder.tvInterested.setText(book.getInterested() + " interested");

        holder.btnDelete.setOnClickListener(v -> {
            books.remove(position);          // remove from list
            notifyItemRemoved(position);     // notify RecyclerView
            notifyItemRangeChanged(position, books.size());
            if (listener != null) {
                listener.onBooksChanged(books.size());
            }
        });
    }


    @Override
    public int getItemCount() {
        return books.size();
    }

    public void addBook(Book book) {
        books.add(book);
        notifyItemInserted(books.size() - 1);
        if (listener != null) {
            listener.onBooksChanged(books.size());
        }
    }

    public void setBooks(List<Book> updated) {
        this.books = updated;
        notifyDataSetChanged();
        if (listener != null) {
            listener.onBooksChanged(books.size());
        }
    }
}
package com.example.testing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Simple in-memory store to share books across screens until Firebase is added.
 */
public final class BookRepository {

    private static final ArrayList<Book> books = new ArrayList<>();

    private BookRepository() { }

    public static List<Book> getBooks() {
        return Collections.unmodifiableList(books);
    }

    public static void addBook(Book book) {
        books.add(book);
    }

    public static void seedIfEmpty() {
        if (!books.isEmpty()) {
            return;
        }

        books.add(new Book("The Midnight Library", "Matt Haig", "Exchange", 12, 3, "hannah", 
            "A thought-provoking novel that explores the infinite possibilities of life. Perfect condition, well-maintained pages, no markings or damage. A must-read for anyone interested in philosophical fiction.", 
            "Fiction", "Like New"));
        books.add(new Book("Atomic Habits", "James Clear", "Exchange", 28, 9, "amir", 
            "An easy and proven way to build good habits and break bad ones. Tiny changes that make a remarkable difference. The book is in excellent condition with no highlights or notes.", 
            "Non-fiction", "Good"));
        books.add(new Book("The Two Towers", "J.R.R. Tolkien", "Donate", 8, 2, "samwise", 
            "The second volume of The Lord of the Rings. Classic fantasy literature. Book is in good reading condition, some wear on the cover but pages are intact.", 
            "Fantasy", "Good"));
        books.add(new Book("The Obstacle Is The Way", "Ryan Holiday", "Exchange", 16, 4, "marcus", 
            "The timeless art of turning trials into triumph. A practical guide to stoic philosophy. Book is like new, barely read.", 
            "Non-fiction", "Like New"));
        books.add(new Book("You Are What You Risk", "Michele Wucker", "Donate", 6, 1, "grace", 
            "A fascinating exploration of how we assess, communicate, and make decisions about risk. Free book for anyone interested in psychology and decision-making.", 
            "Non-fiction", "Fair"));
        books.add(new Book("The Psychology of Money", "Morgan Housel", "Exchange", 31, 12, "alex", 
            "Timeless lessons on wealth, greed, and happiness. Short stories about doing well with money. Excellent condition, no marks or highlights.", 
            "Non-fiction", "Like New"));
    }
}



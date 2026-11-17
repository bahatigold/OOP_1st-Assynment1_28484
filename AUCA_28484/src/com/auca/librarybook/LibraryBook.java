package com.auca.librarybook;

import com.auca.exceptions.ValidationException;
import com.auca.interfaces.Identifiable;
import com.auca.interfaces.Registrable;

public class LibraryBook implements Identifiable, Registrable {
    private final String bookId;
    private final String isbn;
    private String title;
    private String author;
    private int totalCopies;
    private int availableCopies;

    public LibraryBook(String bookId, String isbn, String title, String author, int totalCopies, int availableCopies) {
        this.bookId = bookId;
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.totalCopies = totalCopies;
        this.availableCopies = availableCopies;
    }

    public String getBookId() { return bookId; }
    public String getIsbn() { return isbn; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public int getTotalCopies() { return totalCopies; }
    public int getAvailableCopies() { return availableCopies; }
    public void setTitle(String title) { this.title = title; }
    public void setAuthor(String author) { this.author = author; }
    public void setTotalCopies(int totalCopies) { this.totalCopies = totalCopies; }
    public void setAvailableCopies(int availableCopies) { this.availableCopies = availableCopies; }

    @Override public String getId() { return bookId; }
    @Override public String getDisplayName() { return title + " (" + availableCopies + " available)"; }
    @Override public boolean isActive() { return totalCopies > 0; }

    @Override
    public void validate() throws ValidationException {
        Registrable.requireText(bookId, "Book ID", MIN_ID_LENGTH, MAX_ID_LENGTH);
        Registrable.requireText(isbn, "ISBN", 10, 17);
        Registrable.requireText(title, "Title", 2, 120);
        Registrable.requireText(author, "Author", 2, 100);
        if (totalCopies < 0) throw new ValidationException("Total copies cannot be negative.");
        if (availableCopies < 0) throw new ValidationException("Available copies cannot be negative.");
        if (availableCopies > totalCopies) throw new ValidationException("Available copies cannot exceed total copies.");
    }

    @Override
    public void printDetails() {
        System.out.println("--- Library Book ---");
        System.out.println("  ID            : " + bookId);
        System.out.println("  ISBN          : " + isbn);
        System.out.println("  Title         : " + title);
        System.out.println("  Author        : " + author);
        System.out.println("  Total copies  : " + totalCopies);
        System.out.println("  Available     : " + availableCopies);
    }

    @Override
    public String getRegistrationSummary() {
        return "[Book] " + bookId + " - " + title + " by " + author + " | " + availableCopies + "/" + totalCopies + " available";
    }

    @Override
    public String toString() {
        return "LibraryBook{id=" + bookId + ", title=" + title + ", available=" + availableCopies + "/" + totalCopies + "}";
    }
}

package com.auca.bookloan;

import com.auca.exceptions.ValidationException;
import com.auca.interfaces.Identifiable;
import com.auca.interfaces.Registrable;
import com.auca.librarybook.LibraryBook;
import com.auca.student.Student;
import java.time.LocalDate;

public class BookLoan implements Identifiable, Registrable {
    private final String loanId;
    private Student student;
    private LibraryBook book;
    private LocalDate borrowedDate;
    private LocalDate dueDate;
    private LocalDate returnedDate; // null = not returned yet

    public BookLoan(String loanId, Student student, LibraryBook book, LocalDate borrowedDate,
                    LocalDate dueDate, LocalDate returnedDate) {
        this.loanId = loanId;
        this.student = student;
        this.book = book;
        this.borrowedDate = borrowedDate;
        this.dueDate = dueDate;
        this.returnedDate = returnedDate;
    }

    /** Constructor chaining with this(...): a new loan has no returned date yet. */
    public BookLoan(String loanId, Student student, LibraryBook book, LocalDate borrowedDate, LocalDate dueDate) {
        this(loanId, student, book, borrowedDate, dueDate, null);
    }

    public String getLoanId() { return loanId; }
    public Student getStudent() { return student; }
    public LibraryBook getBook() { return book; }
    public LocalDate getBorrowedDate() { return borrowedDate; }
    public LocalDate getDueDate() { return dueDate; }
    public LocalDate getReturnedDate() { return returnedDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }
    public void setReturnedDate(LocalDate returnedDate) { this.returnedDate = returnedDate; }

    @Override public String getId() { return loanId; }
    @Override public String getDisplayName() {
        return (book == null ? "?" : book.getTitle()) + " <- " + (student == null ? "?" : student.getDisplayName());
    }
    @Override public boolean isActive() { return returnedDate == null; }

    @Override
    public void validate() throws ValidationException {
        Registrable.requireText(loanId, "Loan ID", MIN_ID_LENGTH, MAX_ID_LENGTH);
        Registrable.requireNotNull(student, "Student");
        Registrable.requireNotNull(book, "Book");
        Registrable.requireNotFuture(borrowedDate, "Borrowed date");
        Registrable.requireNotNull(dueDate, "Due date");
        if (!dueDate.isAfter(borrowedDate)) throw new ValidationException("Due date must be after the borrowed date.");
        if (returnedDate != null && returnedDate.isBefore(borrowedDate))
            throw new ValidationException("Returned date cannot be before the borrowed date.");
    }

    @Override
    public void printDetails() {
        System.out.println("--- Book Loan ---");
        System.out.println("  ID            : " + loanId);
        System.out.println("  Student       : " + (student == null ? "(none)" : student.getDisplayName()));
        System.out.println("  Book          : " + (book == null ? "(none)" : book.getTitle()));
        System.out.println("  Borrowed      : " + borrowedDate);
        System.out.println("  Due           : " + dueDate);
        System.out.println("  Returned      : " + (returnedDate == null ? "(not returned yet)" : returnedDate.toString()));
    }

    @Override
    public String getRegistrationSummary() {
        return "[Loan] " + loanId + " - " + getDisplayName() + " | due " + dueDate
                + (returnedDate == null ? " | OUT" : " | returned " + returnedDate);
    }

    @Override
    public String toString() {
        return "BookLoan{id=" + loanId + ", " + getDisplayName() + ", due=" + dueDate + ", returned=" + returnedDate + "}";
    }
}

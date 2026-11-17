package com.auca.feepayment;

import com.auca.exceptions.ValidationException;
import com.auca.interfaces.Identifiable;
import com.auca.interfaces.Registrable;
import com.auca.student.Student;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class FeePayment implements Identifiable, Registrable {
    private final String paymentId;
    private Student student;
    private BigDecimal amount;
    private LocalDate paymentDate;
    private String paymentMethod;
    private final String receiptNumber;

    public FeePayment(String paymentId, Student student, BigDecimal amount, LocalDate paymentDate,
                      String paymentMethod, String receiptNumber) {
        this.paymentId = paymentId;
        this.student = student;
        this.amount = amount;
        this.paymentDate = paymentDate;
        this.paymentMethod = paymentMethod;
        this.receiptNumber = receiptNumber;
    }

    public static List<String> allowedMethods() { return List.of("CASH", "MOBILE_MONEY", "BANK_TRANSFER", "CARD"); }

    public String getPaymentId() { return paymentId; }
    public Student getStudent() { return student; }
    public BigDecimal getAmount() { return amount; }
    public LocalDate getPaymentDate() { return paymentDate; }
    public String getPaymentMethod() { return paymentMethod; }
    public String getReceiptNumber() { return receiptNumber; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public void setPaymentDate(LocalDate paymentDate) { this.paymentDate = paymentDate; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    @Override public String getId() { return paymentId; }
    @Override public String getDisplayName() { return "Receipt " + receiptNumber + " (" + amount + ")"; }
    @Override public boolean isActive() { return true; }

    @Override
    public void validate() throws ValidationException {
        Registrable.requireText(paymentId, "Payment ID", MIN_ID_LENGTH, MAX_ID_LENGTH);
        Registrable.requireNotNull(student, "Student");
        if (amount == null || amount.signum() <= 0) throw new ValidationException("Amount must be greater than zero.");
        Registrable.requireNotFuture(paymentDate, "Payment date");
        Registrable.requireOneOf(paymentMethod, "Payment method", allowedMethods());
        Registrable.requireText(receiptNumber, "Receipt number", 3, 30);
    }

    @Override
    public void printDetails() {
        System.out.println("--- Fee Payment ---");
        System.out.println("  ID            : " + paymentId);
        System.out.println("  Student       : " + (student == null ? "(none)" : student.getDisplayName()));
        System.out.println("  Amount        : " + amount);
        System.out.println("  Date          : " + paymentDate);
        System.out.println("  Method        : " + paymentMethod);
        System.out.println("  Receipt no.   : " + receiptNumber);
    }

    @Override
    public String getRegistrationSummary() {
        return "[FeePayment] " + paymentId + " - " + (student == null ? "?" : student.getDisplayName())
                + " | " + amount + " | " + paymentMethod + " | " + receiptNumber;
    }

    @Override
    public String toString() {
        return "FeePayment{id=" + paymentId + ", amount=" + amount + ", receipt=" + receiptNumber + "}";
    }
}

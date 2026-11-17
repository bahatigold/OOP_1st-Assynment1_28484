package com.auca.assessment;

import com.auca.enrollment.Enrollment;
import com.auca.exceptions.ValidationException;
import com.auca.interfaces.Identifiable;
import com.auca.interfaces.Registrable;
import java.time.LocalDate;
import java.util.List;

public class Assessment implements Identifiable, Registrable {
    private final String assessmentId;
    private Enrollment enrollment;
    private String assessmentType;
    private double maximumScore;
    private double obtainedScore;
    private LocalDate assessmentDate;

    public Assessment(String assessmentId, Enrollment enrollment, String assessmentType,
                      double maximumScore, double obtainedScore, LocalDate assessmentDate) {
        this.assessmentId = assessmentId;
        this.enrollment = enrollment;
        this.assessmentType = assessmentType;
        this.maximumScore = maximumScore;
        this.obtainedScore = obtainedScore;
        this.assessmentDate = assessmentDate;
    }

    public static List<String> allowedTypes() { return List.of("QUIZ", "ASSIGNMENT", "MIDTERM", "FINAL_EXAM"); }

    public String getAssessmentId() { return assessmentId; }
    public Enrollment getEnrollment() { return enrollment; }
    public String getAssessmentType() { return assessmentType; }
    public double getMaximumScore() { return maximumScore; }
    public double getObtainedScore() { return obtainedScore; }
    public LocalDate getAssessmentDate() { return assessmentDate; }
    public void setAssessmentType(String assessmentType) { this.assessmentType = assessmentType; }
    public void setMaximumScore(double maximumScore) { this.maximumScore = maximumScore; }
    public void setObtainedScore(double obtainedScore) { this.obtainedScore = obtainedScore; }
    public void setAssessmentDate(LocalDate assessmentDate) { this.assessmentDate = assessmentDate; }

    @Override public String getId() { return assessmentId; }
    @Override public String getDisplayName() { return assessmentType + " " + obtainedScore + "/" + maximumScore; }
    @Override public boolean isActive() { return true; }

    @Override
    public void validate() throws ValidationException {
        Registrable.requireText(assessmentId, "Assessment ID", MIN_ID_LENGTH, MAX_ID_LENGTH);
        Registrable.requireNotNull(enrollment, "Enrollment");
        Registrable.requireOneOf(assessmentType, "Assessment type", allowedTypes());
        if (!(maximumScore > 0)) throw new ValidationException("Maximum score must be greater than zero.");
        if (!(obtainedScore >= 0 && obtainedScore <= maximumScore))
            throw new ValidationException("Obtained score must be between 0 and " + maximumScore + ".");
        Registrable.requireNotFuture(assessmentDate, "Assessment date");
    }

    @Override
    public void printDetails() {
        System.out.println("--- Assessment ---");
        System.out.println("  ID            : " + assessmentId);
        System.out.println("  Enrollment    : " + (enrollment == null ? "(none)" : enrollment.getDisplayName()));
        System.out.println("  Type          : " + assessmentType);
        System.out.println("  Max score     : " + maximumScore);
        System.out.println("  Obtained      : " + obtainedScore);
        System.out.println("  Date          : " + assessmentDate);
    }

    @Override
    public String getRegistrationSummary() {
        return "[Assessment] " + assessmentId + " - " + getDisplayName() + " | "
                + (enrollment == null ? "?" : enrollment.getDisplayName());
    }

    @Override
    public String toString() {
        return "Assessment{id=" + assessmentId + ", type=" + assessmentType + ", score=" + obtainedScore
                + "/" + maximumScore + "}";
    }
}

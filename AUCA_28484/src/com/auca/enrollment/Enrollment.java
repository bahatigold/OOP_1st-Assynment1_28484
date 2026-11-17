package com.auca.enrollment;

import com.auca.course.Course;
import com.auca.exceptions.ValidationException;
import com.auca.interfaces.Identifiable;
import com.auca.interfaces.Registrable;
import com.auca.student.Student;
import java.time.LocalDate;
import java.util.List;

public class Enrollment implements Identifiable, Registrable {
    private final String enrollmentId;
    private Student student;
    private Course course;
    private LocalDate enrollmentDate;
    private String semester;
    private String enrollmentStatus;

    public Enrollment(String enrollmentId, Student student, Course course, LocalDate enrollmentDate,
                      String semester, String enrollmentStatus) {
        this.enrollmentId = enrollmentId;
        this.student = student;
        this.course = course;
        this.enrollmentDate = enrollmentDate;
        this.semester = semester;
        this.enrollmentStatus = enrollmentStatus;
    }

    /** Constructor chaining with this(...): a new enrollment starts as ACTIVE. */
    public Enrollment(String enrollmentId, Student student, Course course, LocalDate enrollmentDate, String semester) {
        this(enrollmentId, student, course, enrollmentDate, semester, DEFAULT_STATUS);
    }

    public static List<String> allowedStatuses() { return List.of("ACTIVE", "COMPLETED", "DROPPED"); }
    public static List<String> allowedSemesters() { return List.of("SEMESTER_1", "SEMESTER_2", "SUMMER"); }

    public String getEnrollmentId() { return enrollmentId; }
    public Student getStudent() { return student; }
    public Course getCourse() { return course; }
    public LocalDate getEnrollmentDate() { return enrollmentDate; }
    public String getSemester() { return semester; }
    public String getEnrollmentStatus() { return enrollmentStatus; }
    public void setEnrollmentDate(LocalDate enrollmentDate) { this.enrollmentDate = enrollmentDate; }
    public void setSemester(String semester) { this.semester = semester; }
    public void setEnrollmentStatus(String enrollmentStatus) { this.enrollmentStatus = enrollmentStatus; }

    @Override public String getId() { return enrollmentId; }
    @Override public String getDisplayName() {
        return (student == null ? "?" : student.getDisplayName()) + " -> "
                + (course == null ? "?" : course.getCourseCode()) + " (" + semester + ")";
    }
    @Override public boolean isActive() { return DEFAULT_STATUS.equals(enrollmentStatus); }

    @Override
    public void validate() throws ValidationException {
        Registrable.requireText(enrollmentId, "Enrollment ID", MIN_ID_LENGTH, MAX_ID_LENGTH);
        Registrable.requireNotNull(student, "Student");
        Registrable.requireNotNull(course, "Course");
        Registrable.requireNotFuture(enrollmentDate, "Enrollment date");
        Registrable.requireOneOf(semester, "Semester", allowedSemesters());
        Registrable.requireOneOf(enrollmentStatus, "Enrollment status", allowedStatuses());
    }

    @Override
    public void printDetails() {
        System.out.println("--- Enrollment ---");
        System.out.println("  ID            : " + enrollmentId);
        System.out.println("  Student       : " + (student == null ? "(none)" : student.getDisplayName()));
        System.out.println("  Course        : " + (course == null ? "(none)" : course.getDisplayName()));
        System.out.println("  Date          : " + enrollmentDate);
        System.out.println("  Semester      : " + semester);
        System.out.println("  Status        : " + enrollmentStatus);
    }

    @Override
    public String getRegistrationSummary() {
        return "[Enrollment] " + enrollmentId + " - " + getDisplayName() + " | " + enrollmentStatus;
    }

    @Override
    public String toString() {
        return "Enrollment{id=" + enrollmentId + ", " + getDisplayName() + ", status=" + enrollmentStatus + "}";
    }
}

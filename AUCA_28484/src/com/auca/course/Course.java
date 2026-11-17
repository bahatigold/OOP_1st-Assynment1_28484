package com.auca.course;

import com.auca.department.Department;
import com.auca.exceptions.ValidationException;
import com.auca.interfaces.Identifiable;
import com.auca.interfaces.Registrable;
import com.auca.teacher.Teacher;

public class Course implements Identifiable, Registrable {
    private final String courseId;
    private final String courseCode;
    private String courseTitle;
    private int creditHours;
    private Department department;
    private Teacher assignedTeacher;

    public Course(String courseId, String courseCode, String courseTitle, int creditHours,
                  Department department, Teacher assignedTeacher) {
        this.courseId = courseId;
        this.courseCode = courseCode;
        this.courseTitle = courseTitle;
        this.creditHours = creditHours;
        this.department = department;
        this.assignedTeacher = assignedTeacher;
    }

    public String getCourseId() { return courseId; }
    public String getCourseCode() { return courseCode; }
    public String getCourseTitle() { return courseTitle; }
    public int getCreditHours() { return creditHours; }
    public Department getDepartment() { return department; }
    public Teacher getAssignedTeacher() { return assignedTeacher; }
    public void setCourseTitle(String courseTitle) { this.courseTitle = courseTitle; }
    public void setCreditHours(int creditHours) { this.creditHours = creditHours; }
    public void setDepartment(Department department) { this.department = department; }
    public void setAssignedTeacher(Teacher assignedTeacher) { this.assignedTeacher = assignedTeacher; }

    @Override public String getId() { return courseId; }
    @Override public String getDisplayName() { return courseCode + " " + courseTitle; }
    @Override public boolean isActive() { return true; }

    @Override
    public void validate() throws ValidationException {
        Registrable.requireText(courseId, "Course ID", MIN_ID_LENGTH, MAX_ID_LENGTH);
        Registrable.requireText(courseCode, "Course code", 2, 20);
        Registrable.requireText(courseTitle, "Course title", 2, 120);
        if (creditHours < 1 || creditHours > 6) throw new ValidationException("Credit hours must be between 1 and 6.");
        Registrable.requireNotNull(department, "Department");
        Registrable.requireNotNull(assignedTeacher, "Assigned teacher");
    }

    @Override
    public void printDetails() {
        System.out.println("--- Course ---");
        System.out.println("  ID            : " + courseId);
        System.out.println("  Code          : " + courseCode);
        System.out.println("  Title         : " + courseTitle);
        System.out.println("  Credit hours  : " + creditHours);
        System.out.println("  Department    : " + (department == null ? "(none)" : department.getDisplayName()));
        System.out.println("  Teacher       : " + (assignedTeacher == null ? "(none)" : assignedTeacher.getDisplayName()));
    }

    @Override
    public String getRegistrationSummary() {
        return "[Course] " + courseId + " - " + courseCode + " " + courseTitle + " (" + creditHours + " credits)";
    }

    @Override
    public String toString() {
        return "Course{id=" + courseId + ", code=" + courseCode + ", title=" + courseTitle + "}";
    }
}

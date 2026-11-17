package com.auca.abstracts;

import com.auca.department.Department;
import com.auca.exceptions.ValidationException;
import com.auca.interfaces.Registrable;
import java.time.LocalDate;
import java.util.List;

/** Abstract middle level: a Person who belongs to a Department (Student or Teacher). */
public abstract class AcademicMember extends Person {
    private final String memberNumber;
    private Department department;
    private LocalDate joinDate;
    private String academicStatus;
    private String institutionalEmail;
    private String advisorName;

    protected AcademicMember(String personId, String firstName, String lastName, String email,
                             String phoneNumber, LocalDate dateOfBirth,
                             String memberNumber, Department department, LocalDate joinDate,
                             String academicStatus, String institutionalEmail, String advisorName) {
        super(personId, firstName, lastName, email, phoneNumber, dateOfBirth); // constructor chaining upward
        this.memberNumber = memberNumber;
        this.department = department;
        this.joinDate = joinDate;
        this.academicStatus = academicStatus;
        this.institutionalEmail = institutionalEmail;
        this.advisorName = advisorName;
    }

    public abstract String getAcademicCategory();

    /** Allowed values for academicStatus. */
    public static List<String> allowedStatuses() { return List.of("ACTIVE", "INACTIVE"); }

    public String getMemberNumber() { return memberNumber; }
    public Department getDepartment() { return department; }
    public LocalDate getJoinDate() { return joinDate; }
    public String getAcademicStatus() { return academicStatus; }
    public String getInstitutionalEmail() { return institutionalEmail; }
    public String getAdvisorName() { return advisorName; }
    public void setDepartment(Department department) { this.department = department; }
    public void setJoinDate(LocalDate joinDate) { this.joinDate = joinDate; }
    public void setAcademicStatus(String academicStatus) { this.academicStatus = academicStatus; }
    public void setInstitutionalEmail(String institutionalEmail) { this.institutionalEmail = institutionalEmail; }
    public void setAdvisorName(String advisorName) { this.advisorName = advisorName; }

    @Override
    public boolean isActive() { return DEFAULT_STATUS.equalsIgnoreCase(academicStatus); }

    @Override
    public void validate() throws ValidationException {
        super.validate();
        Registrable.requireText(memberNumber, "Member number", MIN_ID_LENGTH, MAX_ID_LENGTH);
        Registrable.requireNotNull(department, "Department");
        Registrable.requireNotFuture(joinDate, "Join date");
        Registrable.requireOneOf(academicStatus, "Academic status", allowedStatuses());
        Registrable.requireEmail(institutionalEmail, "Institutional email");
        Registrable.requireText(advisorName, "Advisor name", 2, 120);
    }

    @Override
    public void printDetails() {
        super.printDetails();
        System.out.println("  Member number : " + memberNumber);
        System.out.println("  Department    : " + (department == null ? "(none)" : department.getDisplayName()));
        System.out.println("  Join date     : " + joinDate);
        System.out.println("  Status        : " + academicStatus);
        System.out.println("  Inst. email   : " + institutionalEmail);
        System.out.println("  Advisor       : " + advisorName);
    }
}

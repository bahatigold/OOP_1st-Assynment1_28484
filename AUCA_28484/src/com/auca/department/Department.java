package com.auca.department;

import com.auca.exceptions.ValidationException;
import com.auca.interfaces.Identifiable;
import com.auca.interfaces.Registrable;
import com.auca.teacher.Teacher;

public class Department implements Identifiable, Registrable {
    private final String departmentId;
    private String departmentName;
    private final String departmentCode;
    private String officeLocation;
    private String contactEmail;
    private Teacher headTeacher; // optional: may be null until a teacher exists

    public Department(String departmentId, String departmentName, String departmentCode,
                      String officeLocation, String contactEmail, Teacher headTeacher) {
        this.departmentId = departmentId;
        this.departmentName = departmentName;
        this.departmentCode = departmentCode;
        this.officeLocation = officeLocation;
        this.contactEmail = contactEmail;
        this.headTeacher = headTeacher;
    }

    public String getDepartmentId() { return departmentId; }
    public String getDepartmentName() { return departmentName; }
    public String getDepartmentCode() { return departmentCode; }
    public String getOfficeLocation() { return officeLocation; }
    public String getContactEmail() { return contactEmail; }
    public Teacher getHeadTeacher() { return headTeacher; }
    public void setDepartmentName(String departmentName) { this.departmentName = departmentName; }
    public void setOfficeLocation(String officeLocation) { this.officeLocation = officeLocation; }
    public void setContactEmail(String contactEmail) { this.contactEmail = contactEmail; }
    public void setHeadTeacher(Teacher headTeacher) { this.headTeacher = headTeacher; }

    @Override public String getId() { return departmentId; }
    @Override public String getDisplayName() { return departmentName; }
    @Override public boolean isActive() { return true; }

    @Override
    public void validate() throws ValidationException {
        Registrable.requireText(departmentId, "Department ID", MIN_ID_LENGTH, MAX_ID_LENGTH);
        Registrable.requireText(departmentName, "Department name", MIN_NAME_LENGTH, MAX_NAME_LENGTH);
        Registrable.requireText(departmentCode, "Department code", 2, 10);
        Registrable.requireText(officeLocation, "Office location", 2, 60);
        Registrable.requireEmail(contactEmail, "Contact email");
    }

    @Override
    public void printDetails() {
        System.out.println("--- Department ---");
        System.out.println("  ID            : " + departmentId);
        System.out.println("  Name          : " + departmentName);
        System.out.println("  Code          : " + departmentCode);
        System.out.println("  Office        : " + officeLocation);
        System.out.println("  Contact email : " + contactEmail);
        System.out.println("  Head teacher  : " + (headTeacher == null ? "(not assigned)" : headTeacher.getDisplayName()));
    }

    @Override
    public String getRegistrationSummary() {
        return "[Department] " + departmentId + " - " + departmentName + " (" + departmentCode + ")";
    }

    @Override
    public String toString() {
        return "Department{id=" + departmentId + ", name=" + departmentName + ", code=" + departmentCode + "}";
    }
}

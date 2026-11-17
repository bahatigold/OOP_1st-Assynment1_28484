package com.auca.teacher;

import com.auca.abstracts.AcademicMember;
import com.auca.department.Department;
import com.auca.exceptions.ValidationException;
import com.auca.interfaces.Registrable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/** Hierarchical inheritance: Teacher is a sibling of Student (both extend AcademicMember). */
public class Teacher extends AcademicMember {
    private final String employeeNumber;
    private String specialization;
    private LocalDate hireDate;
    private BigDecimal salary;
    private String officeNumber;
    private String employmentType;

    public Teacher(String personId, String firstName, String lastName, String email, String phoneNumber,
                   LocalDate dateOfBirth, String memberNumber, Department department, LocalDate joinDate,
                   String academicStatus, String institutionalEmail, String advisorName,
                   String employeeNumber, String specialization, LocalDate hireDate, BigDecimal salary,
                   String officeNumber, String employmentType) {
        super(personId, firstName, lastName, email, phoneNumber, dateOfBirth, memberNumber, department,
                joinDate, academicStatus, institutionalEmail, advisorName);
        this.employeeNumber = employeeNumber;
        this.specialization = specialization;
        this.hireDate = hireDate;
        this.salary = salary;
        this.officeNumber = officeNumber;
        this.employmentType = employmentType;
    }

    public static List<String> allowedEmploymentTypes() { return List.of("FULL_TIME", "PART_TIME", "CONTRACT"); }

    public String getEmployeeNumber() { return employeeNumber; }
    public String getSpecialization() { return specialization; }
    public LocalDate getHireDate() { return hireDate; }
    public BigDecimal getSalary() { return salary; }
    public String getOfficeNumber() { return officeNumber; }
    public String getEmploymentType() { return employmentType; }
    public void setSpecialization(String specialization) { this.specialization = specialization; }
    public void setHireDate(LocalDate hireDate) { this.hireDate = hireDate; }
    public void setSalary(BigDecimal salary) { this.salary = salary; }
    public void setOfficeNumber(String officeNumber) { this.officeNumber = officeNumber; }
    public void setEmploymentType(String employmentType) { this.employmentType = employmentType; }

    @Override public String getRoleDescription() { return "Teacher"; }
    @Override public String getAcademicCategory() { return "Instructor"; }

    @Override
    public void validate() throws ValidationException {
        super.validate();
        Registrable.requireText(employeeNumber, "Employee number", MIN_ID_LENGTH, MAX_ID_LENGTH);
        Registrable.requireText(specialization, "Specialization", 2, 120);
        Registrable.requireNotFuture(hireDate, "Hire date");
        if (salary == null || salary.signum() <= 0) throw new ValidationException("Salary must be greater than zero.");
        Registrable.requireText(officeNumber, "Office number", 1, 30);
        Registrable.requireOneOf(employmentType, "Employment type", allowedEmploymentTypes());
    }

    @Override
    public void printDetails() {
        super.printDetails();
        System.out.println("  Employee no.  : " + employeeNumber);
        System.out.println("  Specialization: " + specialization);
        System.out.println("  Hire date     : " + hireDate);
        System.out.println("  Salary        : " + salary);
        System.out.println("  Office        : " + officeNumber);
        System.out.println("  Employment    : " + employmentType);
    }

    @Override
    public String getRegistrationSummary() {
        return super.getRegistrationSummary() + " | " + employeeNumber + " | " + specialization;
    }

    @Override
    public String toString() {
        return "Teacher{id=" + getId() + ", name=" + getDisplayName() + ", employeeNumber=" + employeeNumber
                + ", specialization=" + specialization + "}";
    }
}

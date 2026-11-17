package com.auca.abstracts;

import com.auca.exceptions.ValidationException;
import com.auca.interfaces.Identifiable;
import com.auca.interfaces.Registrable;
import java.time.LocalDate;

/**
 * Abstract parent of every human in the system. It cannot be instantiated because
 * getRoleDescription() has no body here: only a concrete child can say what its role is.
 */
public abstract class Person implements Identifiable, Registrable {
    private final String personId;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private LocalDate dateOfBirth;

    protected Person(String personId, String firstName, String lastName, String email,
                     String phoneNumber, LocalDate dateOfBirth) {
        this.personId = personId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.dateOfBirth = dateOfBirth;
    }

    /** Each child describes its own role (polymorphism). */
    public abstract String getRoleDescription();

    public String getPersonId() { return personId; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getEmail() { return email; }
    public String getPhoneNumber() { return phoneNumber; }
    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public void setEmail(String email) { this.email = email; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    @Override public String getId() { return personId; }
    @Override public String getDisplayName() { return firstName + " " + lastName; }

    @Override
    public void validate() throws ValidationException {
        Registrable.requireText(personId, "Person ID", MIN_ID_LENGTH, MAX_ID_LENGTH);
        Registrable.requireText(firstName, "First name", MIN_NAME_LENGTH, MAX_NAME_LENGTH);
        Registrable.requireText(lastName, "Last name", MIN_NAME_LENGTH, MAX_NAME_LENGTH);
        Registrable.requireEmail(email, "Email");
        Registrable.requirePhone(phoneNumber, "Phone");
        Registrable.requirePast(dateOfBirth, "Date of birth");
    }

    @Override
    public void printDetails() {
        System.out.println("--- " + getRoleDescription() + " ---");
        System.out.println("  Person ID     : " + personId);
        System.out.println("  Name          : " + getDisplayName());
        System.out.println("  Email         : " + email);
        System.out.println("  Phone         : " + phoneNumber);
        System.out.println("  Date of birth : " + dateOfBirth);
    }

    @Override
    public String getRegistrationSummary() {
        return "[" + getRoleDescription() + "] " + personId + " - " + getDisplayName();
    }

    @Override
    public String toString() {
        return getRoleDescription() + "{id=" + personId + ", name=" + getDisplayName() + ", email=" + email + "}";
    }
}

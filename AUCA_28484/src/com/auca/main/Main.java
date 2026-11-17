package com.auca.main;

import com.auca.abstracts.AcademicMember;
import com.auca.abstracts.Person;
import com.auca.administrator.Administrator;
import com.auca.assessment.Assessment;
import com.auca.bookloan.BookLoan;
import com.auca.classroom.Classroom;
import com.auca.course.Course;
import com.auca.department.Department;
import com.auca.enrollment.Enrollment;
import com.auca.exceptions.BookUnavailableException;
import com.auca.exceptions.DuplicateRecordException;
import com.auca.exceptions.PrerequisiteMissingException;
import com.auca.exceptions.RecordNotFoundException;
import com.auca.exceptions.ValidationException;
import com.auca.feepayment.FeePayment;
import com.auca.interfaces.Identifiable;
import com.auca.interfaces.Registrable;
import com.auca.librarybook.LibraryBook;
import com.auca.student.Student;
import com.auca.teacher.Teacher;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class Main {

    // ONE shared Scanner for the whole program
    private final Scanner scanner = new Scanner(System.in);

    // ---------- typed in-memory collections ----------
    private final List<Department> departments = new ArrayList<>();
    private final List<Student> students = new ArrayList<>();
    private final List<Teacher> teachers = new ArrayList<>();
    private final List<Administrator> administrators = new ArrayList<>();
    private final List<Course> courses = new ArrayList<>();
    private final List<Classroom> classrooms = new ArrayList<>();
    private final List<Enrollment> enrollments = new ArrayList<>();
    private final List<Assessment> assessments = new ArrayList<>();
    private final List<FeePayment> payments = new ArrayList<>();
    private final List<LibraryBook> books = new ArrayList<>();
    private final List<BookLoan> loans = new ArrayList<>();

    // ---------- result of asking one field ----------
    private static final int OK = 0;
    private static final int BACK = 1;
    private static final int CANCEL = 2;
    private int signal = OK;      // set to BACK or CANCEL when the user types that word
    private int fieldNo = 1;      // number shown in front of the prompt (01, 02 ...)

    // ---------- form numbers ----------
    private static final int DEPT = 0, STUDENT = 1, TEACHER = 2, ADMIN = 3, COURSE = 4, CLASSROOM = 5,
            ENROLL = 6, ASSESS = 7, PAYMENT = 8, BOOK = 9, LOAN = 10;

    // ---------- date rules ----------
    private static final int PAST = 0;         // strictly before today (date of birth)
    private static final int NOT_FUTURE = 1;   // today or earlier

    // ---------- form descriptions (titles, field labels, step of each field) ----------
    private static final String[] TITLES = {"DEPARTMENT REGISTRATION", "STUDENT REGISTRATION",
            "TEACHER REGISTRATION", "ADMINISTRATOR REGISTRATION", "COURSE REGISTRATION",
            "CLASSROOM REGISTRATION", "ENROLL STUDENT", "RECORD ASSESSMENT", "RECORD FEE PAYMENT",
            "LIBRARY BOOK REGISTRATION", "BORROW LIBRARY BOOK"};

    private static final String[] PERSON = {"Person ID", "First name", "Last name", "Email", "Phone", "Date of birth"};
    private static final String[] ACADEMIC = {"Member number", "Department", "Join date", "Academic status",
            "Institutional email", "Advisor name"};

    private static final String[][] LABELS = {
            {"Department ID", "Department name", "Department code", "Office location", "Contact email", "Head teacher"},
            join(PERSON, ACADEMIC, new String[]{"Student number", "Program name", "Admission date", "Study year",
                    "Guardian phone", "Scholarship"}),
            join(PERSON, ACADEMIC, new String[]{"Employee number", "Specialization", "Hire date", "Salary",
                    "Office number", "Employment type"}),
            join(PERSON, new String[]{"Staff number", "Job title", "Assigned office", "Employment date",
                    "Access level", "Active"}),
            {"Course ID", "Course code", "Course title", "Credit hours", "Department", "Assigned teacher"},
            {"Classroom ID", "Building name", "Room number", "Capacity", "Floor number", "Available"},
            {"Enrollment ID", "Student", "Course", "Enrollment date", "Semester", "Enrollment status"},
            {"Assessment ID", "Enrollment", "Assessment type", "Maximum score", "Obtained score", "Assessment date"},
            {"Payment ID", "Student", "Amount", "Payment date", "Payment method", "Receipt number"},
            {"Book ID", "ISBN", "Title", "Author", "Total copies", "Available copies"},
            {"Loan ID", "Student", "Book", "Borrowed date", "Due date", "Returned date"}
    };

    private static final int[] S666 = {1, 1, 1, 1, 1, 1, 2, 2, 2, 2, 2, 2, 3, 3, 3, 3, 3, 3};
    private static final int[][] STEP_OF = {
            {1, 1, 1, 2, 2, 3},
            S666,
            S666,
            {1, 1, 1, 1, 1, 1, 2, 2, 2, 3, 3, 3},
            {1, 1, 1, 2, 2, 3},
            {1, 1, 1, 2, 2, 3},
            {1, 1, 2, 2, 3, 3},
            {1, 1, 2, 2, 3, 3},
            {1, 1, 2, 2, 3, 3},
            {1, 1, 1, 2, 2, 3},
            {1, 1, 2, 2, 3, 3}
    };

    private static final String[][] STEPS = {
            {"Identity", "Location & contact", "Leadership", "Review & confirm"},
            {"Personal details", "Academic details", "Student details", "Review & confirm"},
            {"Personal details", "Academic details", "Teacher details", "Review & confirm"},
            {"Personal details", "Staff details", "Access details", "Review & confirm"},
            {"Identity", "Credits & department", "Teacher", "Review & confirm"},
            {"Location", "Size", "Availability", "Review & confirm"},
            {"Student", "Course & date", "Semester & status", "Review & confirm"},
            {"Enrollment", "Type & maximum", "Result & date", "Review & confirm"},
            {"Student", "Amount & date", "Method & receipt", "Review & confirm"},
            {"Identity", "Author & stock", "Availability", "Review & confirm"},
            {"Student", "Book & borrowed date", "Due & return", "Review & confirm"}
    };

    /** Joins several String arrays into one (used to build the Student/Teacher label lists). */
    private static String[] join(String[]... parts) {
        int total = 0;
        for (String[] p : parts) total += p.length;
        String[] all = new String[total];
        int pos = 0;
        for (String[] p : parts) {
            for (String s : p) all[pos++] = s;
        }
        return all;
    }

    // =====================================================================
    // 1. START + MENU
    // =====================================================================
    public static void main(String[] args) {
        new Main().run();
    }

    private void run() {
        System.out.println("Welcome to AUCA_ID School Management System");
        boolean running = true;
        while (running) {
            printMenu();
            String input = ask("Select an option: ");
            int choice;
            try {
                choice = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("! '" + input + "' is not a number. Please type a number from the menu.");
                continue;
            }
            try {
                switch (choice) {
                    case 1 -> runRegistration(DEPT);
                    case 2 -> runRegistration(STUDENT);
                    case 3 -> runRegistration(TEACHER);
                    case 4 -> runRegistration(ADMIN);
                    case 5 -> runRegistration(COURSE);
                    case 6 -> runRegistration(CLASSROOM);
                    case 7 -> runRegistration(ENROLL);
                    case 8 -> runRegistration(ASSESS);
                    case 9 -> runRegistration(PAYMENT);
                    case 10 -> runRegistration(BOOK);
                    case 11 -> runRegistration(LOAN);
                    case 12 -> returnBook();
                    case 13 -> viewAllRecords();
                    case 14 -> searchById();
                    case 15 -> updateRecord();
                    case 16 -> showStudentAcademicSummary();
                    case 17 -> showPaymentHistory();
                    case 18 -> showAvailableBooks();
                    case 19 -> demonstratePolymorphism();
                    case 0 -> {
                        System.out.println("Goodbye!");
                        running = false;
                    }
                    default -> System.out.println("! Invalid option. Choose a number from 0 to 19.");
                }
            } catch (RecordNotFoundException | PrerequisiteMissingException | BookUnavailableException
                     | ValidationException | DuplicateRecordException e) {
                // business-rule problems: show the message and go back to the menu
                System.out.println("! " + e.getMessage());
            } catch (RuntimeException e) {
                // safety net: one failed action must never close the program
                System.out.println("! Unexpected problem (" + e.getMessage() + "). Returning to the main menu.");
            }
        }
    }

    private void printMenu() {
        String[] m = {"Exit", "Register Department", "Register Student", "Register Teacher",
                "Register Administrator", "Register Course", "Register Classroom", "Enroll Student",
                "Record Assessment", "Record Fee Payment", "Register Library Book", "Borrow Library Book",
                "Return Library Book", "View All Records", "Search Record by ID", "Update Existing Record",
                "Student Academic Summary", "Student Payment History", "View Available Books",
                "Demonstrate Polymorphism"};
        System.out.println();
        System.out.println("============== AUCA_ID SCHOOL MANAGEMENT ==============");
        for (int i = 1; i <= 10; i++) {
            int right = (i == 10) ? 0 : i + 10;
            System.out.printf("%2d. %-28s %2d. %s%n", i, m[i], right, m[right]);
        }
    }

    // =====================================================================
    // 2. BASIC INPUT HELPERS (every input goes through the ONE scanner)
    // =====================================================================
    /** Reads one complete line (never nextInt, so no skipped input). Exits cleanly at end of input. */
    private String read() {
        if (!scanner.hasNextLine()) {
            System.out.println("\nInput closed. Exiting.");
            System.exit(0);
        }
        return scanner.nextLine();
    }

    /** Prints a prompt and returns the trimmed answer. */
    private String ask(String prompt) {
        System.out.print(prompt);
        return read().trim();
    }

    private boolean confirm(String question) {
        while (true) {
            String s = ask(question + " (Y/N): ");
            if (s.equalsIgnoreCase("Y") || s.equalsIgnoreCase("YES")) return true;
            if (s.equalsIgnoreCase("N") || s.equalsIgnoreCase("NO")) return false;
            System.out.println("! Please answer Y or N.");
        }
    }

    // =====================================================================
    // 3. FIELD INPUT HELPERS - each returns the value, or null if the user typed BACK / CANCEL
    //    (and then "signal" tells which one). Each loops until the value is valid.
    // =====================================================================
    private String readValue(String label, String hint) {
        System.out.printf("%02d %-20s [%s] : ", fieldNo, label, hint);
        String s = read().trim();
        if (s.equalsIgnoreCase("CANCEL")) {
            signal = CANCEL;
            return null;
        }
        if (s.equalsIgnoreCase("BACK")) {
            signal = BACK;
            return null;
        }
        return s;
    }

    private String askText(String label, String hint, int min, int max) {
        while (true) {
            String s = readValue(label, hint);
            if (s == null) return null;
            try {
                return Registrable.requireText(s, label, min, max);
            } catch (ValidationException e) {
                System.out.println("! " + e.getMessage());
            }
        }
    }

    /** Text that must also be unique. "kind" tells isUsed() where to look for duplicates. */
    private String askUnique(String label, String hint, int min, int max, String kind) {
        while (true) {
            String s = readValue(label, hint);
            if (s == null) return null;
            try {
                String t = Registrable.requireText(s, label, min, max);
                if (isUsed(kind, t)) throw new DuplicateRecordException(label + " " + t + " already exists.");
                return t;
            } catch (ValidationException | DuplicateRecordException e) {
                System.out.println("! " + e.getMessage());
            }
        }
    }

    private String askEmail(String label, boolean mustBeUnique) {
        while (true) {
            String s = readValue(label, mustBeUnique ? "String | unique | email" : "String | email");
            if (s == null) return null;
            try {
                String t = Registrable.requireEmail(s, label);
                if (mustBeUnique && isUsed("EMAIL", t))
                    throw new DuplicateRecordException(label + " " + t + " already exists.");
                return t;
            } catch (ValidationException | DuplicateRecordException e) {
                System.out.println("! " + e.getMessage());
            }
        }
    }

    private String askPhone(String label) {
        while (true) {
            String s = readValue(label, "String | 10-15 digits");
            if (s == null) return null;
            try {
                return Registrable.requirePhone(s, label);
            } catch (ValidationException e) {
                System.out.println("! " + e.getMessage());
            }
        }
    }

    private LocalDate askDate(String label, int rule) {
        while (true) {
            String s = readValue(label, "LocalDate | YYYY-MM-DD");
            if (s == null) return null;
            try {
                LocalDate d = Registrable.parseDate(s, label);   // catches DateTimeParseException inside
                if (rule == PAST) Registrable.requirePast(d, label);
                else Registrable.requireNotFuture(d, label);
                return d;
            } catch (ValidationException e) {
                System.out.println("! " + e.getMessage());
            }
        }
    }

    /** A date that must come strictly AFTER another date (due date after borrowed date). */
    private LocalDate askDateAfter(String label, LocalDate after) {
        while (true) {
            String s = readValue(label, "LocalDate | after " + after);
            if (s == null) return null;
            try {
                LocalDate d = Registrable.parseDate(s, label);
                if (!d.isAfter(after)) throw new ValidationException(label + " must be after " + after + ".");
                return d;
            } catch (ValidationException e) {
                System.out.println("! " + e.getMessage());
            }
        }
    }

    private Integer askInt(String label, String hint, int min, int max) {
        while (true) {
            String s = readValue(label, hint);
            if (s == null) return null;
            try {
                int v = Integer.parseInt(s);
                if (v >= min && v <= max) return v;
                System.out.println("! " + label + " must be between " + min + " and " + max + ".");
            } catch (NumberFormatException e) {
                System.out.println("! " + label + " must be a whole number.");
            }
        }
    }

    private Double askDouble(String label, String hint, double min, boolean minAllowed, double max) {
        while (true) {
            String s = readValue(label, hint);
            if (s == null) return null;
            try {
                double v = Double.parseDouble(s);
                boolean lowOk = minAllowed ? v >= min : v > min;
                if (Double.isFinite(v) && lowOk && v <= max) return v;
                if (minAllowed) System.out.println("! " + label + " must be between " + min + " and " + max + ".");
                else System.out.println("! " + label + " must be greater than " + min + ".");
            } catch (NumberFormatException e) {
                System.out.println("! " + label + " must be a number.");
            }
        }
    }

    private BigDecimal askMoney(String label) {
        while (true) {
            String s = readValue(label, "BigDecimal | > 0");
            if (s == null) return null;
            try {
                BigDecimal v = new BigDecimal(s).setScale(2, RoundingMode.HALF_UP);
                if (v.signum() > 0) return v;
                System.out.println("! " + label + " must be greater than zero.");
            } catch (NumberFormatException e) {
                System.out.println("! " + label + " must be a valid amount, e.g. 150000.50");
            }
        }
    }

    /** User types the option number OR its name. */
    private String askChoice(String label, List<String> options) {
        StringBuilder hint = new StringBuilder();
        for (int i = 0; i < options.size(); i++) {
            if (i > 0) hint.append(" / ");
            hint.append(i + 1).append(' ').append(options.get(i));
        }
        while (true) {
            String s = readValue(label, hint.toString());
            if (s == null) return null;
            for (String o : options) {
                if (o.equalsIgnoreCase(s)) return o;
            }
            try {
                int n = Integer.parseInt(s);
                if (n >= 1 && n <= options.size()) return options.get(n - 1);
            } catch (NumberFormatException e) {
                System.out.println("! " + label + " must be one of: " + hint);
                continue;
            }
            System.out.println("! " + label + " must be one of: " + hint);
        }
    }

    private Boolean askYesNo(String label) {
        while (true) {
            String s = readValue(label, "1 YES / 2 NO");
            if (s == null) return null;
            if (s.equals("1") || s.equalsIgnoreCase("YES") || s.equalsIgnoreCase("Y")) return true;
            if (s.equals("2") || s.equalsIgnoreCase("NO") || s.equalsIgnoreCase("N")) return false;
            System.out.println("! " + label + " must be 1 (YES) or 2 (NO).");
        }
    }

    // =====================================================================
    // 4. SELECTING EXISTING RECORDS (numbered list + nested registration)
    // =====================================================================
    private List<? extends Identifiable> listFor(String key) {
        return switch (key) {
            case "department" -> departments;
            case "teacher" -> teachers;
            case "student" -> students;
            case "course" -> courses;
            case "enrollment" -> activeEnrollments();
            case "book" -> availableBooks();
            default -> activeLoans();
        };
    }

    private String nameFor(String key) {
        return switch (key) {
            case "department" -> "Department";
            case "teacher" -> "Teacher";
            case "student" -> "Student";
            case "course" -> "Course";
            case "enrollment" -> "Active Enrollment";
            case "book" -> "Available Book";
            default -> "Active Loan";
        };
    }

    /** Opens the registration form of the missing prerequisite (nested registration). */
    private void registerNested(String key) {
        switch (key) {
            case "department" -> runForm(DEPT);
            case "teacher" -> runForm(TEACHER);
            case "student" -> runForm(STUDENT);
            case "course" -> runForm(COURSE);
            case "enrollment" -> runForm(ENROLL);
            case "book" -> runForm(BOOK);
            default -> runForm(LOAN);
        }
    }

    /**
     * Shows a numbered list of REAL saved records. The user may pick one, register a new one
     * (the outer form is still waiting, so nothing already typed is lost), skip (if optional) or cancel.
     * Returns the chosen object, or null (signal tells BACK / CANCEL; null with OK means "skipped").
     */
    private Object pick(String key, boolean optional) {
        System.out.printf("%02d %s%n", fieldNo, nameFor(key));
        while (true) {
            List<? extends Identifiable> list = listFor(key);   // fresh list each time
            String name = nameFor(key);
            System.out.println("+----------------- CHOOSE " + name.toUpperCase() + " -----------------+");
            if (list.isEmpty()) {
                System.out.println("| No " + name + " found yet. Register one now (option 1) or cancel (0).");
            }
            for (int i = 0; i < list.size(); i++) {
                System.out.println("| " + (i + 1) + " [" + list.get(i).getId() + "] " + list.get(i).getDisplayName());
            }
            int newNo = list.size() + 1;
            System.out.println("| " + newNo + " [+] Register a new " + name);
            if (optional) System.out.println("| S Skip (assign later)");
            System.out.println("| 0 Cancel");
            String s = ask("Select option: ");
            if (s.equals("0") || s.equalsIgnoreCase("CANCEL")) {
                signal = CANCEL;
                return null;
            }
            if (s.equalsIgnoreCase("BACK")) {
                signal = BACK;
                return null;
            }
            if (optional && s.equalsIgnoreCase("S")) return null;
            try {
                int n = Integer.parseInt(s);
                if (n >= 1 && n <= list.size()) return list.get(n - 1);
                if (n == newNo) {
                    int savedFieldNo = fieldNo;
                    registerNested(key);          // nested form; then the loop shows the refreshed list
                    fieldNo = savedFieldNo;
                    continue;
                }
                System.out.println("! Choose a number between 0 and " + newNo + ".");
            } catch (NumberFormatException e) {
                System.out.println("! Please type a number from the list.");
            }
        }
    }

    /** Head teacher is optional: with no teacher yet it is simply left empty. */
    private Teacher askHeadTeacher() {
        if (teachers.isEmpty()) {
            System.out.printf("%02d %-20s [Teacher | optional]%n", fieldNo, "Head teacher");
            System.out.println("  (No teacher exists yet - left empty; assign it later with Update Record.)");
            return null;
        }
        return (Teacher) pick("teacher", true);
    }

    // =====================================================================
    // 5. THE FORM ENGINE
    // =====================================================================
    /** Runs a registration and then offers: register another / view / main menu. */
    private void runRegistration(int type) {
        while (true) {
            Object saved = runForm(type);
            if (saved == null) return;              // cancelled
            while (true) {
                System.out.println("  [1] Register another  [2] View record  [0] Main menu");
                String c = ask("Select option: ");
                if (c.equals("1")) break;
                if (c.equals("2")) ((Registrable) saved).printDetails();
                else if (c.equals("0")) return;
                else System.out.println("! Choose 1, 2 or 0.");
            }
        }
    }

    /** Asks every field, then the review screen. Returns the saved record, or null if cancelled. */
    private Object runForm(int type) {
        int count = LABELS[type].length;
        Object[] v = new Object[count];
        printBanner(type);
        int i = 0;
        int shownStep = 0;
        while (i < count) {
            if (isDerived(type, i)) {          // derived fields are never typed
                i++;
                continue;
            }
            int step = STEP_OF[type][i];
            if (step != shownStep) {
                shownStep = step;
                System.out.println("\n[STEP " + step + "/4] " + STEPS[type][step - 1].toUpperCase());
            }
            int result = askField(type, v, i);
            if (result == CANCEL) {
                if (confirm("Discard this unsaved record?")) {
                    System.out.println("Cancelled. Nothing was saved.");
                    return null;
                }
                continue;                      // not discarded: ask the same field again
            }
            if (result == BACK) {
                i = Math.max(0, i - 1);
                while (i > 0 && isDerived(type, i)) i--;
                continue;
            }
            i++;
        }
        return review(type, v);
    }

    private boolean isDerived(int type, int i) {
        return type == LOAN && i == 5;         // returnedDate is empty until the book is returned
    }

    private void printBanner(int type) {
        String bar = "+------------------------------------------------------------+";
        System.out.println();
        System.out.println(bar);
        System.out.println("| AUCA_ID / SCHOOL MANAGEMENT");
        System.out.println("| " + TITLES[type]);
        System.out.println(bar);
        for (int i = 0; i < 4; i++) System.out.println("| [" + (i + 1) + "] " + STEPS[type][i]);
        System.out.println("| Type BACK for the previous field; CANCEL to discard.");
        System.out.println(bar);
    }

    /** Asks field number i of the given form. Returns OK, BACK or CANCEL. */
    private int askField(int type, Object[] v, int i) {
        signal = OK;
        fieldNo = i + 1;
        switch (type) {
            case DEPT -> askDepartmentField(v, i);
            case STUDENT -> askStudentField(v, i);
            case TEACHER -> askTeacherField(v, i);
            case ADMIN -> askAdministratorField(v, i);
            case COURSE -> askCourseField(v, i);
            case CLASSROOM -> askClassroomField(v, i);
            case ENROLL -> askEnrollmentField(v, i);
            case ASSESS -> askAssessmentField(v, i);
            case PAYMENT -> askPaymentField(v, i);
            case BOOK -> askBookField(v, i);
            default -> askLoanField(v, i);
        }
        return signal;
    }

    private Object review(int type, Object[] v) {
        while (true) {
            String bar = "+------------------------------------------------------------+";
            System.out.println();
            System.out.println(bar);
            System.out.println("| REVIEW - " + TITLES[type]);
            System.out.println(bar);
            int shown = 0;
            for (int i = 0; i < v.length; i++) {
                int step = STEP_OF[type][i];
                if (step != shown) {
                    shown = step;
                    System.out.println("| " + STEPS[type][step - 1].toUpperCase());
                }
                System.out.printf("|  %02d %-20s: %s%s%n", i + 1, LABELS[type][i], show(v[i]),
                        isDerived(type, i) ? "  (derived)" : "");
            }
            System.out.println(bar);
            System.out.println("| [1] Save   [2] Edit field   [0] Cancel");
            String c = ask("Select option: ");
            switch (c) {
                case "1" -> {
                    try {
                        return saveRecord(type, v);     // validate + duplicates + store
                    } catch (ValidationException | DuplicateRecordException | PrerequisiteMissingException
                             | BookUnavailableException | RecordNotFoundException e) {
                        System.out.println("! Cannot save: " + e.getMessage());
                        System.out.println("  Use option 2 to correct the data.");
                    } catch (RuntimeException e) {
                        System.out.println("! Unexpected error while saving (" + e.getMessage() + "). Nothing was saved.");
                    }
                }
                case "2" -> editField(type, v);
                case "0" -> {
                    if (confirm("Discard this unsaved record?")) {
                        System.out.println("Cancelled. Nothing was saved.");
                        return null;
                    }
                }
                default -> System.out.println("! Invalid option. Choose 1, 2 or 0.");
            }
        }
    }

    private void editField(int type, Object[] v) {
        String s = ask("Field number to edit (01-" + String.format("%02d", v.length) + "): ");
        int n;
        try {
            n = Integer.parseInt(s);
        } catch (NumberFormatException e) {
            System.out.println("! Please type a field number.");
            return;
        }
        if (n < 1 || n > v.length) {
            System.out.println("! No such field.");
            return;
        }
        if (isDerived(type, n - 1)) {
            System.out.println("! That field is derived and cannot be edited.");
            return;
        }
        Object old = v[n - 1];
        System.out.println("Old value: " + show(old));
        int result = askField(type, v, n - 1);       // same method that asked it the first time
        if (result != OK) {
            v[n - 1] = old;
            System.out.println("Edit abandoned. Old value kept.");
        } else {
            System.out.println("Field " + String.format("%02d", n) + " updated.");
        }
    }

    /** Readable text for any value. */
    private String show(Object value) {
        if (value == null) return "(none)";
        if (value instanceof Boolean b) return b ? "YES" : "NO";
        if (value instanceof Identifiable r) return "[" + r.getId() + "] " + r.getDisplayName();
        return value.toString();
    }

    // =====================================================================
    // 6. ONE "askXxxField" METHOD PER FORM  (switch on the field index)
    // =====================================================================
    private void askPersonField(Object[] v, int i) {
        switch (i) {
            case 0 -> v[0] = askUnique("Person ID", "String | unique | 4-30 chars", 4, 30, "ID");
            case 1 -> v[1] = askText("First name", "String | 2-60 chars", 2, 60);
            case 2 -> v[2] = askText("Last name", "String | 2-60 chars", 2, 60);
            case 3 -> v[3] = askEmail("Email", true);
            case 4 -> v[4] = askPhone("Phone");
            case 5 -> v[5] = askDate("Date of birth", PAST);
        }
    }

    private void askAcademicField(Object[] v, int i) {
        switch (i) {
            case 6 -> v[6] = askUnique("Member number", "String | unique | 4-30", 4, 30, "MEMBER");
            case 7 -> v[7] = pick("department", false);
            case 8 -> v[8] = askDate("Join date", NOT_FUTURE);
            case 9 -> v[9] = askChoice("Academic status", AcademicMember.allowedStatuses());
            case 10 -> v[10] = askEmail("Institutional email", true);
            case 11 -> v[11] = askText("Advisor name", "String | 2-120 chars", 2, 120);
        }
    }

    private void askDepartmentField(Object[] v, int i) {
        switch (i) {
            case 0 -> v[0] = askUnique("Department ID", "String | unique | 4-30", 4, 30, "ID");
            case 1 -> v[1] = askText("Department name", "String | 2-60 chars", 2, 60);
            case 2 -> v[2] = askUnique("Department code", "String | unique | 2-10", 2, 10, "DEPTCODE");
            case 3 -> v[3] = askText("Office location", "String | 2-60 chars", 2, 60);
            case 4 -> v[4] = askEmail("Contact email", false);
            case 5 -> v[5] = askHeadTeacher();
        }
    }

    private void askStudentField(Object[] v, int i) {
        if (i < 6) {
            askPersonField(v, i);
        } else if (i < 12) {
            askAcademicField(v, i);
        } else {
            switch (i) {
                case 12 -> v[12] = askUnique("Student number", "String | unique | 4-30", 4, 30, "STUDENT");
                case 13 -> v[13] = askText("Program name", "String | 2-120 chars", 2, 120);
                case 14 -> v[14] = askDate("Admission date", NOT_FUTURE);
                case 15 -> v[15] = askInt("Study year", "int | 1-6", 1, 6);
                case 16 -> v[16] = askPhone("Guardian phone");
                case 17 -> v[17] = askYesNo("Scholarship");
            }
        }
    }

    private void askTeacherField(Object[] v, int i) {
        if (i < 6) {
            askPersonField(v, i);
        } else if (i < 12) {
            askAcademicField(v, i);
        } else {
            switch (i) {
                case 12 -> v[12] = askUnique("Employee number", "String | unique | 4-30", 4, 30, "EMPLOYEE");
                case 13 -> v[13] = askText("Specialization", "String | 2-120 chars", 2, 120);
                case 14 -> v[14] = askDate("Hire date", NOT_FUTURE);
                case 15 -> v[15] = askMoney("Salary");
                case 16 -> v[16] = askText("Office number", "String | 1-30 chars", 1, 30);
                case 17 -> v[17] = askChoice("Employment type", Teacher.allowedEmploymentTypes());
            }
        }
    }

    private void askAdministratorField(Object[] v, int i) {
        if (i < 6) {
            askPersonField(v, i);
        } else {
            switch (i) {
                case 6 -> v[6] = askUnique("Staff number", "String | unique | 4-30", 4, 30, "STAFF");
                case 7 -> v[7] = askText("Job title", "String | 2-80 chars", 2, 80);
                case 8 -> v[8] = askText("Assigned office", "String | 1-60 chars", 1, 60);
                case 9 -> v[9] = askDate("Employment date", NOT_FUTURE);
                case 10 -> v[10] = askInt("Access level", "int | 1-5", 1, 5);
                case 11 -> v[11] = askYesNo("Active");
            }
        }
    }

    private void askCourseField(Object[] v, int i) {
        switch (i) {
            case 0 -> v[0] = askUnique("Course ID", "String | unique | 4-30", 4, 30, "ID");
            case 1 -> v[1] = askUnique("Course code", "String | unique | 2-20", 2, 20, "COURSECODE");
            case 2 -> v[2] = askText("Course title", "String | 2-120 chars", 2, 120);
            case 3 -> v[3] = askInt("Credit hours", "int | 1-6", 1, 6);
            case 4 -> v[4] = pick("department", false);
            case 5 -> v[5] = pick("teacher", false);
        }
    }

    private void askClassroomField(Object[] v, int i) {
        switch (i) {
            case 0 -> v[0] = askUnique("Classroom ID", "String | unique | 4-30", 4, 30, "ID");
            case 1 -> v[1] = askText("Building name", "String | 2-60 chars", 2, 60);
            case 2 -> v[2] = askText("Room number", "String | 1-20 chars", 1, 20);
            case 3 -> v[3] = askInt("Capacity", "int | > 0", 1, 100000);
            case 4 -> v[4] = askInt("Floor number", "int | >= 0", 0, 200);
            case 5 -> v[5] = askYesNo("Available");
        }
    }

    private void askEnrollmentField(Object[] v, int i) {
        switch (i) {
            case 0 -> v[0] = askUnique("Enrollment ID", "String | unique | 4-30", 4, 30, "ID");
            case 1 -> v[1] = pick("student", false);
            case 2 -> v[2] = pick("course", false);
            case 3 -> v[3] = askDate("Enrollment date", NOT_FUTURE);
            case 4 -> {
                String semester;
                while (true) {
                    semester = askChoice("Semester", Enrollment.allowedSemesters());
                    if (semester == null) break;                       // BACK / CANCEL
                    if (v[1] != null && v[2] != null
                            && activeEnrollmentExists((Student) v[1], (Course) v[2], semester)) {
                        System.out.println("! This student already has an ACTIVE enrollment in that course for "
                                + semester + ".");
                        continue;
                    }
                    break;
                }
                v[4] = semester;
            }
            case 5 -> v[5] = askChoice("Enrollment status", Enrollment.allowedStatuses());
        }
    }

    private void askAssessmentField(Object[] v, int i) {
        switch (i) {
            case 0 -> v[0] = askUnique("Assessment ID", "String | unique | 4-30", 4, 30, "ID");
            case 1 -> v[1] = pick("enrollment", false);
            case 2 -> v[2] = askChoice("Assessment type", Assessment.allowedTypes());
            case 3 -> v[3] = askDouble("Maximum score", "double | > 0", 0, false, Double.MAX_VALUE);
            case 4 -> {
                double max = (v[3] == null) ? Double.MAX_VALUE : (Double) v[3];
                v[4] = askDouble("Obtained score", "double | 0 to maximum", 0, true, max);
            }
            case 5 -> v[5] = askDate("Assessment date", NOT_FUTURE);
        }
    }

    private void askPaymentField(Object[] v, int i) {
        switch (i) {
            case 0 -> v[0] = askUnique("Payment ID", "String | unique | 4-30", 4, 30, "ID");
            case 1 -> v[1] = pick("student", false);
            case 2 -> v[2] = askMoney("Amount");
            case 3 -> v[3] = askDate("Payment date", NOT_FUTURE);
            case 4 -> v[4] = askChoice("Payment method", FeePayment.allowedMethods());
            case 5 -> v[5] = askUnique("Receipt number", "String | unique | 3-30", 3, 30, "RECEIPT");
        }
    }

    private void askBookField(Object[] v, int i) {
        switch (i) {
            case 0 -> v[0] = askUnique("Book ID", "String | unique | 4-30", 4, 30, "ID");
            case 1 -> v[1] = askUnique("ISBN", "String | unique | 10-17", 10, 17, "ISBN");
            case 2 -> v[2] = askText("Title", "String | 2-120 chars", 2, 120);
            case 3 -> v[3] = askText("Author", "String | 2-100 chars", 2, 100);
            case 4 -> v[4] = askInt("Total copies", "int | >= 0", 0, 100000);
            case 5 -> {
                int total = (v[4] == null) ? 100000 : (Integer) v[4];
                v[5] = askInt("Available copies", "int | 0 to total", 0, total);
            }
        }
    }

    private void askLoanField(Object[] v, int i) {
        switch (i) {
            case 0 -> v[0] = askUnique("Loan ID", "String | unique | 4-30", 4, 30, "ID");
            case 1 -> v[1] = pick("student", false);
            case 2 -> v[2] = pick("book", false);
            case 3 -> v[3] = askDate("Borrowed date", NOT_FUTURE);
            case 4 -> {
                LocalDate borrowed = (v[3] == null) ? LocalDate.MIN : (LocalDate) v[3];
                v[4] = askDateAfter("Due date", borrowed);
            }
            case 5 -> v[5] = null;     // derived: not returned yet
        }
    }

    // =====================================================================
    // 7. SAVING (build object -> validate() -> duplicate check -> add to list)
    // =====================================================================
    private Object saveRecord(int type, Object[] v) {
        return switch (type) {
            case DEPT -> saveDepartment(v);
            case STUDENT -> saveStudent(v);
            case TEACHER -> saveTeacher(v);
            case ADMIN -> saveAdministrator(v);
            case COURSE -> saveCourse(v);
            case CLASSROOM -> saveClassroom(v);
            case ENROLL -> saveEnrollment(v);
            case ASSESS -> saveAssessment(v);
            case PAYMENT -> savePayment(v);
            case BOOK -> saveBook(v);
            default -> saveLoan(v);
        };
    }

    // small readers: turn v[i] (an Object) back into its real type
    private String str(Object[] v, int i) { return (String) v[i]; }
    private LocalDate dt(Object[] v, int i) { return (LocalDate) v[i]; }
    private int num(Object[] v, int i) { return (Integer) v[i]; }
    private double dbl(Object[] v, int i) { return (Double) v[i]; }
    private boolean flag(Object[] v, int i) { return (Boolean) v[i]; }
    private BigDecimal money(Object[] v, int i) { return (BigDecimal) v[i]; }

    private void mustBeUnique(String kind, String value) {
        if (isUsed(kind, value)) throw new DuplicateRecordException(value + " already exists (" + kind + ").");
    }

    /** Checks done again at Save time for every kind of person. */
    private void checkPersonUnique(Person p) {
        mustBeUnique("ID", p.getId());
        mustBeUnique("EMAIL", p.getEmail());
        if (p instanceof AcademicMember m) {
            if (m.getInstitutionalEmail().equalsIgnoreCase(m.getEmail()))
                throw new ValidationException("Institutional email must differ from the personal email.");
            mustBeUnique("EMAIL", m.getInstitutionalEmail());
            mustBeUnique("MEMBER", m.getMemberNumber());
        }
    }

    private Object saveDepartment(Object[] v) {
        Department d = new Department(str(v, 0), str(v, 1), str(v, 2), str(v, 3), str(v, 4), (Teacher) v[5]);
        d.validate();
        mustBeUnique("ID", d.getId());
        mustBeUnique("DEPTCODE", d.getDepartmentCode());
        departments.add(d);
        System.out.println("+ Department " + d.getId() + " registered successfully.");
        return d;
    }

    private Object saveStudent(Object[] v) {
        Student s = new Student(str(v, 0), str(v, 1), str(v, 2), str(v, 3), str(v, 4), dt(v, 5),
                str(v, 6), (Department) v[7], dt(v, 8), str(v, 9), str(v, 10), str(v, 11),
                str(v, 12), str(v, 13), dt(v, 14), num(v, 15), str(v, 16), flag(v, 17));
        s.validate();
        checkPersonUnique(s);
        mustBeUnique("STUDENT", s.getStudentNumber());
        students.add(s);
        System.out.println("+ Student " + s.getId() + " registered successfully.");
        return s;
    }

    private Object saveTeacher(Object[] v) {
        Teacher t = new Teacher(str(v, 0), str(v, 1), str(v, 2), str(v, 3), str(v, 4), dt(v, 5),
                str(v, 6), (Department) v[7], dt(v, 8), str(v, 9), str(v, 10), str(v, 11),
                str(v, 12), str(v, 13), dt(v, 14), money(v, 15), str(v, 16), str(v, 17));
        t.validate();
        checkPersonUnique(t);
        mustBeUnique("EMPLOYEE", t.getEmployeeNumber());
        teachers.add(t);
        System.out.println("+ Teacher " + t.getId() + " registered successfully.");
        return t;
    }

    private Object saveAdministrator(Object[] v) {
        Administrator a = new Administrator(str(v, 0), str(v, 1), str(v, 2), str(v, 3), str(v, 4), dt(v, 5),
                str(v, 6), str(v, 7), str(v, 8), dt(v, 9), num(v, 10), flag(v, 11));
        a.validate();
        checkPersonUnique(a);
        mustBeUnique("STAFF", a.getStaffNumber());
        administrators.add(a);
        System.out.println("+ Administrator " + a.getId() + " registered successfully.");
        return a;
    }

    private Object saveCourse(Object[] v) {
        Course c = new Course(str(v, 0), str(v, 1), str(v, 2), num(v, 3), (Department) v[4], (Teacher) v[5]);
        c.validate();
        mustBeUnique("ID", c.getId());
        mustBeUnique("COURSECODE", c.getCourseCode());
        courses.add(c);
        System.out.println("+ Course " + c.getId() + " registered successfully.");
        return c;
    }

    private Object saveClassroom(Object[] v) {
        Classroom c = new Classroom(str(v, 0), str(v, 1), str(v, 2), num(v, 3), num(v, 4), flag(v, 5));
        c.validate();
        mustBeUnique("ID", c.getId());
        classrooms.add(c);
        System.out.println("+ Classroom " + c.getId() + " registered successfully.");
        return c;
    }

    private Object saveEnrollment(Object[] v) {
        Student st = (Student) v[1];
        Course co = (Course) v[2];
        if (st == null || co == null) throw new PrerequisiteMissingException("A student and a course are required.");
        Enrollment e = new Enrollment(str(v, 0), st, co, dt(v, 3), str(v, 4), str(v, 5));
        e.validate();
        mustBeUnique("ID", e.getId());
        if (e.isActive() && activeEnrollmentExists(st, co, e.getSemester()))
            throw new DuplicateRecordException("An ACTIVE enrollment for this student, course and semester");
        enrollments.add(e);
        System.out.println("+ Enrollment " + e.getId() + " registered successfully.");
        return e;
    }

    private Object saveAssessment(Object[] v) {
        Enrollment en = (Enrollment) v[1];
        if (en == null) throw new PrerequisiteMissingException("An enrollment is required.");
        if (!en.isActive()) throw new PrerequisiteMissingException("Only an ACTIVE enrollment can be assessed.");
        Assessment a = new Assessment(str(v, 0), en, str(v, 2), dbl(v, 3), dbl(v, 4), dt(v, 5));
        a.validate();
        mustBeUnique("ID", a.getId());
        assessments.add(a);
        System.out.println("+ Assessment " + a.getId() + " recorded successfully.");
        return a;
    }

    private Object savePayment(Object[] v) {
        Student st = (Student) v[1];
        if (st == null) throw new PrerequisiteMissingException("A student is required.");
        FeePayment p = new FeePayment(str(v, 0), st, money(v, 2), dt(v, 3), str(v, 4), str(v, 5));
        p.validate();
        mustBeUnique("ID", p.getId());
        mustBeUnique("RECEIPT", p.getReceiptNumber());
        payments.add(p);
        System.out.println("+ Payment " + p.getId() + " recorded successfully.");
        return p;
    }

    private Object saveBook(Object[] v) {
        LibraryBook b = new LibraryBook(str(v, 0), str(v, 1), str(v, 2), str(v, 3), num(v, 4), num(v, 5));
        b.validate();
        mustBeUnique("ID", b.getId());
        mustBeUnique("ISBN", b.getIsbn());
        books.add(b);
        System.out.println("+ Book " + b.getId() + " registered successfully.");
        return b;
    }

    /** Borrowing: stock goes down by ONE, and only here (cancel/failure never touches the stock). */
    private Object saveLoan(Object[] v) {
        Student st = (Student) v[1];
        LibraryBook bk = (LibraryBook) v[2];
        if (st == null || bk == null) throw new PrerequisiteMissingException("A student and a book are required.");
        BookLoan loan = new BookLoan(str(v, 0), st, bk, dt(v, 3), dt(v, 4));   // 5-argument constructor (this(...) chaining)
        loan.validate();
        mustBeUnique("ID", loan.getId());
        if (bk.getAvailableCopies() <= 0)
            throw new BookUnavailableException("No copy of '" + bk.getTitle() + "' is available right now.");
        bk.setAvailableCopies(bk.getAvailableCopies() - 1);
        loans.add(loan);
        System.out.println("+ Loan " + loan.getId() + " created. Copies left: " + bk.getAvailableCopies());
        return loan;
    }

    // =====================================================================
    // 8. RETURN BOOK  (updates the existing loan, stock goes up by ONE)
    // =====================================================================
    private void returnBook() {
        signal = OK;
        fieldNo = 1;
        Object chosen = pick("loan", false);
        if (chosen == null) {
            System.out.println("Return cancelled. Nothing changed.");
            return;
        }
        BookLoan loan = (BookLoan) chosen;
        System.out.println("Returning: " + loan.getDisplayName() + " (borrowed " + loan.getBorrowedDate() + ")");
        LocalDate returned = null;
        while (returned == null) {
            fieldNo = 1;
            String s = readValue("Return date", "LocalDate | YYYY-MM-DD");
            if (s == null) {
                System.out.println("Return cancelled. Nothing changed.");
                return;
            }
            try {
                LocalDate d = Registrable.parseDate(s, "Return date");
                Registrable.requireNotFuture(d, "Return date");
                if (d.isBefore(loan.getBorrowedDate()))
                    throw new ValidationException("Return date cannot be before the borrowed date ("
                            + loan.getBorrowedDate() + ").");
                returned = d;
            } catch (ValidationException e) {
                System.out.println("! " + e.getMessage());
            }
        }
        if (!confirm("Confirm return on " + returned + "?")) {
            System.out.println("Return cancelled. Nothing changed.");
            return;
        }
        LibraryBook bk = loan.getBook();
        if (bk.getAvailableCopies() >= bk.getTotalCopies())
            throw new ValidationException("Stock is already full; cannot add another returned copy.");
        loan.setReturnedDate(returned);
        bk.setAvailableCopies(bk.getAvailableCopies() + 1);
        System.out.println("+ Book returned. Copies available now: " + bk.getAvailableCopies() + "/" + bk.getTotalCopies());
    }

    // =====================================================================
    // 9. VIEW / SEARCH
    // =====================================================================
    private static final String[] TYPE_NAMES = {"Departments", "Students", "Teachers", "Administrators",
            "Courses", "Classrooms", "Enrollments", "Assessments", "Fee payments", "Library books", "Book loans"};

    private List<? extends Identifiable> listByNumber(int n) {
        return switch (n) {
            case 1 -> departments;
            case 2 -> students;
            case 3 -> teachers;
            case 4 -> administrators;
            case 5 -> courses;
            case 6 -> classrooms;
            case 7 -> enrollments;
            case 8 -> assessments;
            case 9 -> payments;
            case 10 -> books;
            case 11 -> loans;
            default -> allRecords();
        };
    }

    private void viewAllRecords() {
        while (true) {
            System.out.println("\nView which records?");
            for (int i = 0; i < TYPE_NAMES.length; i++) System.out.println(" " + (i + 1) + ". " + TYPE_NAMES[i]);
            System.out.println(" 12. Everything");
            System.out.println("  0. Back to main menu");
            String s = ask("Select option: ");
            int n;
            try {
                n = Integer.parseInt(s);
            } catch (NumberFormatException e) {
                System.out.println("! Please type a number.");
                continue;
            }
            if (n == 0) return;
            if (n < 1 || n > 12) {
                System.out.println("! Choose 0-12.");
                continue;
            }
            List<? extends Identifiable> list = listByNumber(n);
            String title = (n == 12) ? "Everything" : TYPE_NAMES[n - 1];
            if (list.isEmpty()) {
                System.out.println("(" + title + ": no records yet.)");
            } else {
                System.out.println("--- " + title + " (" + list.size() + ") ---");
                for (Identifiable r : list) System.out.println(" " + ((Registrable) r).getRegistrationSummary());
            }
        }
    }

    private Identifiable findById(String id) {
        for (Identifiable r : allRecords()) {
            if (r.getId().equalsIgnoreCase(id)) return r;
        }
        throw new RecordNotFoundException("No record found with ID '" + id + "'.");
    }

    private void searchById() {
        String id = ask("Enter the ID to search (0 = back): ");
        if (id.equals("0")) return;
        if (id.isEmpty()) throw new ValidationException("ID cannot be blank.");
        Identifiable r = findById(id);
        System.out.println("Found: " + ((Registrable) r).getRegistrationSummary());
        ((Registrable) r).printDetails();
    }

    // =====================================================================
    // 10. UPDATE (only mutable fields; IDs/codes can never change)
    // =====================================================================
    private List<String> updateOptions(Identifiable rec) {
        List<String> o = new ArrayList<>();
        if (rec instanceof Person) { o.add("Phone"); o.add("Email"); }
        if (rec instanceof AcademicMember) { o.add("Advisor name"); o.add("Academic status"); }
        if (rec instanceof Student) { o.add("Program name"); o.add("Study year"); o.add("Guardian phone"); o.add("Scholarship"); }
        if (rec instanceof Teacher) { o.add("Specialization"); o.add("Salary"); o.add("Office number"); o.add("Employment type"); }
        if (rec instanceof Administrator) { o.add("Job title"); o.add("Assigned office"); o.add("Access level"); o.add("Active"); }
        if (rec instanceof Department) { o.add("Department name"); o.add("Office location"); o.add("Contact email"); o.add("Head teacher"); }
        if (rec instanceof Course) { o.add("Course title"); o.add("Credit hours"); o.add("Assigned teacher"); }
        if (rec instanceof Classroom) { o.add("Capacity"); o.add("Floor number"); o.add("Available"); }
        if (rec instanceof Enrollment) { o.add("Enrollment status"); }
        if (rec instanceof Assessment) { o.add("Assessment type"); o.add("Obtained score"); }
        if (rec instanceof FeePayment) { o.add("Payment method"); }
        if (rec instanceof LibraryBook) { o.add("Title"); o.add("Author"); o.add("Total copies"); }
        if (rec instanceof BookLoan) { o.add("Due date"); }
        return o;
    }

    private void updateRecord() {
        String id = ask("Enter the ID of the record to update (0 = back): ");
        if (id.equals("0")) return;
        if (id.isEmpty()) throw new ValidationException("ID cannot be blank.");
        Identifiable rec = findById(id);
        if (rec instanceof BookLoan l && !l.isActive()) {
            System.out.println("! A returned loan cannot be updated.");
            return;
        }
        List<String> options = updateOptions(rec);
        while (true) {
            System.out.println("\nEditing: " + ((Registrable) rec).getRegistrationSummary());
            for (int i = 0; i < options.size(); i++) System.out.printf(" %02d %s%n", i + 1, options.get(i));
            System.out.println(" 00 Done (the ID cannot be changed)");
            String s = ask("Field number to edit: ");
            int n;
            try {
                n = Integer.parseInt(s);
            } catch (NumberFormatException e) {
                System.out.println("! Please type a field number.");
                continue;
            }
            if (n == 0) {
                System.out.println("Update finished.");
                return;
            }
            if (n < 1 || n > options.size()) {
                System.out.println("! No such field.");
                continue;
            }
            applyUpdate(rec, options.get(n - 1));
        }
    }

    /** Asks the new value (same helpers as the forms), confirms, applies. */
    private boolean go(String label, Object oldValue, Object newValue) {
        if (signal != OK) {
            System.out.println("Edit abandoned.");
            return false;
        }
        if (!confirm("Change " + label + " from '" + show(oldValue) + "' to '" + show(newValue) + "'?")) {
            System.out.println("No change made.");
            return false;
        }
        return true;
    }

    private void finish(Identifiable rec) {
        try {
            ((Registrable) rec).validate();      // validate the whole record again
            System.out.println("+ Updated successfully.");
        } catch (ValidationException e) {
            System.out.println("! Warning: record now fails validation: " + e.getMessage());
        }
    }

    private void applyUpdate(Identifiable rec, String label) {
        signal = OK;
        fieldNo = 1;
        if (rec instanceof Person p) {
            if (label.equals("Phone")) {
                String x = askPhone("Phone");
                if (go(label, p.getPhoneNumber(), x)) { p.setPhoneNumber(x); finish(rec); }
                return;
            }
            if (label.equals("Email")) {
                String x = askEmail("Email", true);
                if (go(label, p.getEmail(), x)) { p.setEmail(x); finish(rec); }
                return;
            }
        }
        if (rec instanceof AcademicMember m) {
            if (label.equals("Advisor name")) {
                String x = askText("Advisor name", "String | 2-120 chars", 2, 120);
                if (go(label, m.getAdvisorName(), x)) { m.setAdvisorName(x); finish(rec); }
                return;
            }
            if (label.equals("Academic status")) {
                String x = askChoice("Academic status", AcademicMember.allowedStatuses());
                if (go(label, m.getAcademicStatus(), x)) { m.setAcademicStatus(x); finish(rec); }
                return;
            }
        }
        if (rec instanceof Student s) {
            if (label.equals("Program name")) {
                String x = askText("Program name", "String | 2-120 chars", 2, 120);
                if (go(label, s.getProgramName(), x)) { s.setProgramName(x); finish(rec); }
            } else if (label.equals("Study year")) {
                Integer x = askInt("Study year", "int | 1-6", 1, 6);
                if (go(label, s.getStudyYear(), x)) { s.setStudyYear(x); finish(rec); }
            } else if (label.equals("Guardian phone")) {
                String x = askPhone("Guardian phone");
                if (go(label, s.getGuardianPhone(), x)) { s.setGuardianPhone(x); finish(rec); }
            } else if (label.equals("Scholarship")) {
                Boolean x = askYesNo("Scholarship");
                if (go(label, s.isScholarshipStatus(), x)) { s.setScholarshipStatus(x); finish(rec); }
            }
            return;
        }
        if (rec instanceof Teacher t) {
            if (label.equals("Specialization")) {
                String x = askText("Specialization", "String | 2-120 chars", 2, 120);
                if (go(label, t.getSpecialization(), x)) { t.setSpecialization(x); finish(rec); }
            } else if (label.equals("Salary")) {
                BigDecimal x = askMoney("Salary");
                if (go(label, t.getSalary(), x)) { t.setSalary(x); finish(rec); }
            } else if (label.equals("Office number")) {
                String x = askText("Office number", "String | 1-30 chars", 1, 30);
                if (go(label, t.getOfficeNumber(), x)) { t.setOfficeNumber(x); finish(rec); }
            } else if (label.equals("Employment type")) {
                String x = askChoice("Employment type", Teacher.allowedEmploymentTypes());
                if (go(label, t.getEmploymentType(), x)) { t.setEmploymentType(x); finish(rec); }
            }
            return;
        }
        if (rec instanceof Administrator a) {
            if (label.equals("Job title")) {
                String x = askText("Job title", "String | 2-80 chars", 2, 80);
                if (go(label, a.getJobTitle(), x)) { a.setJobTitle(x); finish(rec); }
            } else if (label.equals("Assigned office")) {
                String x = askText("Assigned office", "String | 1-60 chars", 1, 60);
                if (go(label, a.getAssignedOffice(), x)) { a.setAssignedOffice(x); finish(rec); }
            } else if (label.equals("Access level")) {
                Integer x = askInt("Access level", "int | 1-5", 1, 5);
                if (go(label, a.getAccessLevel(), x)) { a.setAccessLevel(x); finish(rec); }
            } else if (label.equals("Active")) {
                Boolean x = askYesNo("Active");
                if (go(label, a.isActive(), x)) { a.setActive(x); finish(rec); }
            }
            return;
        }
        if (rec instanceof Department d) {
            if (label.equals("Department name")) {
                String x = askText("Department name", "String | 2-60 chars", 2, 60);
                if (go(label, d.getDepartmentName(), x)) { d.setDepartmentName(x); finish(rec); }
            } else if (label.equals("Office location")) {
                String x = askText("Office location", "String | 2-60 chars", 2, 60);
                if (go(label, d.getOfficeLocation(), x)) { d.setOfficeLocation(x); finish(rec); }
            } else if (label.equals("Contact email")) {
                String x = askEmail("Contact email", false);
                if (go(label, d.getContactEmail(), x)) { d.setContactEmail(x); finish(rec); }
            } else if (label.equals("Head teacher")) {
                Teacher x = askHeadTeacher();     // S = skip -> clears the head teacher
                if (go(label, d.getHeadTeacher(), x)) { d.setHeadTeacher(x); finish(rec); }
            }
            return;
        }
        if (rec instanceof Course c) {
            if (label.equals("Course title")) {
                String x = askText("Course title", "String | 2-120 chars", 2, 120);
                if (go(label, c.getCourseTitle(), x)) { c.setCourseTitle(x); finish(rec); }
            } else if (label.equals("Credit hours")) {
                Integer x = askInt("Credit hours", "int | 1-6", 1, 6);
                if (go(label, c.getCreditHours(), x)) { c.setCreditHours(x); finish(rec); }
            } else if (label.equals("Assigned teacher")) {
                Teacher x = (Teacher) pick("teacher", false);
                if (go(label, c.getAssignedTeacher(), x)) { c.setAssignedTeacher(x); finish(rec); }
            }
            return;
        }
        if (rec instanceof Classroom c) {
            if (label.equals("Capacity")) {
                Integer x = askInt("Capacity", "int | > 0", 1, 100000);
                if (go(label, c.getCapacity(), x)) { c.setCapacity(x); finish(rec); }
            } else if (label.equals("Floor number")) {
                Integer x = askInt("Floor number", "int | >= 0", 0, 200);
                if (go(label, c.getFloorNumber(), x)) { c.setFloorNumber(x); finish(rec); }
            } else if (label.equals("Available")) {
                Boolean x = askYesNo("Available");
                if (go(label, c.isAvailable(), x)) { c.setAvailable(x); finish(rec); }
            }
            return;
        }
        if (rec instanceof Enrollment e) {
            String x = askChoice("Enrollment status", Enrollment.allowedStatuses());
            if (go(label, e.getEnrollmentStatus(), x)) { e.setEnrollmentStatus(x); finish(rec); }
            return;
        }
        if (rec instanceof Assessment a) {
            if (label.equals("Assessment type")) {
                String x = askChoice("Assessment type", Assessment.allowedTypes());
                if (go(label, a.getAssessmentType(), x)) { a.setAssessmentType(x); finish(rec); }
            } else if (label.equals("Obtained score")) {
                Double x = askDouble("Obtained score", "double | 0 to " + a.getMaximumScore(), 0, true, a.getMaximumScore());
                if (go(label, a.getObtainedScore(), x)) { a.setObtainedScore(x); finish(rec); }
            }
            return;
        }
        if (rec instanceof FeePayment p) {
            String x = askChoice("Payment method", FeePayment.allowedMethods());
            if (go(label, p.getPaymentMethod(), x)) { p.setPaymentMethod(x); finish(rec); }
            return;
        }
        if (rec instanceof LibraryBook b) {
            if (label.equals("Title")) {
                String x = askText("Title", "String | 2-120 chars", 2, 120);
                if (go(label, b.getTitle(), x)) { b.setTitle(x); finish(rec); }
            } else if (label.equals("Author")) {
                String x = askText("Author", "String | 2-100 chars", 2, 100);
                if (go(label, b.getAuthor(), x)) { b.setAuthor(x); finish(rec); }
            } else if (label.equals("Total copies")) {
                int onLoan = b.getTotalCopies() - b.getAvailableCopies();     // copies currently borrowed
                Integer x = askInt("Total copies", "int | >= " + onLoan + " (copies on loan)", onLoan, 100000);
                if (go(label, b.getTotalCopies(), x)) {
                    b.setTotalCopies(x);
                    b.setAvailableCopies(x - onLoan);
                    finish(rec);
                }
            }
            return;
        }
        if (rec instanceof BookLoan l) {
            LocalDate x = askDateAfter("Due date", l.getBorrowedDate());
            if (go(label, l.getDueDate(), x)) { l.setDueDate(x); finish(rec); }
        }
    }

    // =====================================================================
    // 11. REPORTS
    // =====================================================================
    private void showStudentAcademicSummary() {
        signal = OK;
        fieldNo = 1;
        Object r = pick("student", false);
        if (!(r instanceof Student s)) {
            System.out.println("Cancelled.");
            return;
        }
        System.out.println("\n=========== STUDENT ACADEMIC SUMMARY ===========");
        s.printDetails();
        System.out.println("--- Enrollments & assessments ---");
        boolean any = false;
        double totalPercent = 0;
        int count = 0;
        for (Enrollment e : enrollments) {
            if (e.getStudent() != s) continue;
            any = true;
            System.out.println(" " + e.getCourse().getDisplayName() + " | " + e.getSemester() + " | " + e.getEnrollmentStatus());
            for (Assessment a : assessments) {
                if (a.getEnrollment() != e) continue;
                double pct = a.getObtainedScore() / a.getMaximumScore() * 100;
                System.out.printf("     %-10s %.1f / %.1f  (%.1f%%)  on %s%n", a.getAssessmentType(),
                        a.getObtainedScore(), a.getMaximumScore(), pct, a.getAssessmentDate());
                totalPercent += pct;
                count++;
            }
        }
        if (!any) System.out.println(" No enrollments yet.");
        if (count > 0) System.out.printf("Average assessment result: %.1f%%%n", totalPercent / count);
        else System.out.println("No assessments recorded yet.");
    }

    private void showPaymentHistory() {
        signal = OK;
        fieldNo = 1;
        Object r = pick("student", false);
        if (!(r instanceof Student s)) {
            System.out.println("Cancelled.");
            return;
        }
        System.out.println("\n--- Payment history for " + s.getDisplayName() + " ---");
        BigDecimal total = BigDecimal.ZERO;
        int count = 0;
        for (FeePayment p : payments) {
            if (p.getStudent() != s) continue;
            System.out.println(" " + p.getPaymentDate() + " | " + p.getAmount() + " | " + p.getPaymentMethod()
                    + " | receipt " + p.getReceiptNumber());
            total = total.add(p.getAmount());
            count++;
        }
        if (count == 0) System.out.println(" No payments recorded for this student.");
        else System.out.println("Total paid: " + total + " (" + count + " payment(s))");
    }

    private void showAvailableBooks() {
        List<LibraryBook> list = availableBooks();
        if (list.isEmpty()) {
            System.out.println("No book currently has an available copy.");
            return;
        }
        System.out.println("--- Available books ---");
        for (LibraryBook b : list) {
            System.out.println(" [" + b.getId() + "] " + b.getTitle() + " by " + b.getAuthor()
                    + " - " + b.getAvailableCopies() + "/" + b.getTotalCopies() + " available");
        }
    }

    private void demonstratePolymorphism() {
        System.out.println("\n===== 1) SUPERCLASS POLYMORPHISM: List<Person> =====");
        List<Person> people = allPeople();
        if (people.isEmpty()) System.out.println("No Student/Teacher/Administrator registered yet.");
        for (Person p : people) {
            // same call through a Person reference, different behaviour at runtime (overridden methods)
            System.out.println(p.getRoleDescription() + " -> " + p.getRegistrationSummary() + " | active=" + p.isActive());
            if (p instanceof AcademicMember m) {
                System.out.println("    academic category: " + m.getAcademicCategory());
            }
        }
        System.out.println("\n===== 2) INTERFACE POLYMORPHISM: List<Identifiable> =====");
        List<Identifiable> items = allRecords();
        if (items.isEmpty()) System.out.println("No records yet.");
        for (Identifiable i : items) {
            System.out.println(i.getId() + " | " + i.getDisplayName() + " | active=" + i.isActive());
        }
        System.out.println("\n===== NOTES =====");
        System.out.println("* Person and AcademicMember are abstract: they declare abstract methods without a body");
        System.out.println("  (getRoleDescription, getAcademicCategory), so 'new Person(...)' is refused by the compiler.");
        System.out.println("* Multilevel inheritance: Person -> AcademicMember -> Student / Teacher.");
        System.out.println("* Hierarchical inheritance: Student and Teacher share AcademicMember; Administrator extends Person.");
        System.out.println("* Constructor chaining: Student -> super(...) -> AcademicMember -> super(...) -> Person;");
        System.out.println("  BookLoan and Enrollment also use this(...) between their own constructors.");
    }

    // =====================================================================
    // 12. LISTS + DUPLICATE CHECKS (plain loops)
    // =====================================================================
    private List<Person> allPeople() {
        List<Person> all = new ArrayList<>();
        all.addAll(students);
        all.addAll(teachers);
        all.addAll(administrators);
        return all;
    }

    private List<Identifiable> allRecords() {
        List<Identifiable> all = new ArrayList<>();
        all.addAll(departments);
        all.addAll(students);
        all.addAll(teachers);
        all.addAll(administrators);
        all.addAll(courses);
        all.addAll(classrooms);
        all.addAll(enrollments);
        all.addAll(assessments);
        all.addAll(payments);
        all.addAll(books);
        all.addAll(loans);
        return all;
    }

    private List<Enrollment> activeEnrollments() {
        List<Enrollment> r = new ArrayList<>();
        for (Enrollment e : enrollments) if (e.isActive()) r.add(e);
        return r;
    }

    private List<LibraryBook> availableBooks() {
        List<LibraryBook> r = new ArrayList<>();
        for (LibraryBook b : books) if (b.getAvailableCopies() > 0) r.add(b);
        return r;
    }

    private List<BookLoan> activeLoans() {
        List<BookLoan> r = new ArrayList<>();
        for (BookLoan l : loans) if (l.isActive()) r.add(l);
        return r;
    }

    private boolean activeEnrollmentExists(Student s, Course c, String semester) {
        for (Enrollment e : enrollments) {
            if (e.isActive() && e.getStudent() == s && e.getCourse() == c && e.getSemester().equals(semester)) return true;
        }
        return false;
    }

    /** Is this value already used? "kind" says what to compare it with. */
    private boolean isUsed(String kind, String value) {
        switch (kind) {
            case "ID":
                for (Identifiable r : allRecords()) if (r.getId().equalsIgnoreCase(value)) return true;
                return false;
            case "EMAIL":
                for (Person p : allPeople()) {
                    if (p.getEmail().equalsIgnoreCase(value)) return true;
                    if (p instanceof AcademicMember m && m.getInstitutionalEmail().equalsIgnoreCase(value)) return true;
                }
                return false;
            case "MEMBER":
                for (Student s : students) if (s.getMemberNumber().equalsIgnoreCase(value)) return true;
                for (Teacher t : teachers) if (t.getMemberNumber().equalsIgnoreCase(value)) return true;
                return false;
            case "STUDENT":
                for (Student s : students) if (s.getStudentNumber().equalsIgnoreCase(value)) return true;
                return false;
            case "EMPLOYEE":
                for (Teacher t : teachers) if (t.getEmployeeNumber().equalsIgnoreCase(value)) return true;
                return false;
            case "STAFF":
                for (Administrator a : administrators) if (a.getStaffNumber().equalsIgnoreCase(value)) return true;
                return false;
            case "DEPTCODE":
                for (Department d : departments) if (d.getDepartmentCode().equalsIgnoreCase(value)) return true;
                return false;
            case "COURSECODE":
                for (Course c : courses) if (c.getCourseCode().equalsIgnoreCase(value)) return true;
                return false;
            case "RECEIPT":
                for (FeePayment p : payments) if (p.getReceiptNumber().equalsIgnoreCase(value)) return true;
                return false;
            case "ISBN":
                for (LibraryBook b : books) if (b.getIsbn().equalsIgnoreCase(value)) return true;
                return false;
            default:
                return false;
        }
    }
}

import java.io.*;
import java.time.LocalDate;
import java.util.*;

public class GymApp {

    // File to store member data
    static final String FILE_NAME = "members.txt";

    // File to store trainer data
    static final String TRAINER_FILE = "trainers.txt";

    // Next available member ID
    static int nextId = 1;

    // List to store all trainers
    static List<Trainer> trainers = new ArrayList<>();

    // Interface defining user actions: login and panel navigation
    interface User {
        boolean login(Scanner sc);
        void panel(Scanner sc, MemberList ml);
    }

    // Abstract class for shared User logic
    abstract static class AbstractUser implements User {
        String id;
        String password;

        // Constructor for common credentials
        AbstractUser(String id, String password) {
            this.id = id;
            this.password = password;
        }
    }
    // Admin user with hardcoded credentials
    static class Admin extends AbstractUser {

        Admin() {
            super("admin", "admin123");
        }

        // Admin login validation
        public boolean login(Scanner sc) {
            System.out.print("Admin username: ");
            String u = sc.nextLine();
            System.out.print("Password: ");
            String p = sc.nextLine();
            if (id.equals(u) && password.equals(p)) {
                System.out.println("Successfully logged in as Admin.");
                return true;
            } else {
                System.out.println("Access denied. Invalid username or password.");
                return false;
            }
        }

        // Admin panel menu and actions
        public void panel(Scanner sc, MemberList ml) {
            String c;
            do {
                System.out.println("\n-- Admin Panel --");
                System.out.println("1. Add Member");
                System.out.println("2. List Members");
                System.out.println("3. Remove Member");
                System.out.println("4. Show Membership Deadline");
                System.out.println("5. List Members by Trainer");
                System.out.println("6. Manage Trainers");
                System.out.println("0. Logout");
                System.out.print("Choice: ");
                c = sc.nextLine();
                switch (c) {
                    case "1":
                        addMember(sc, ml); // Add new member
                        break;
                    case "2":
                        ml.listAllMembers(); // Show all members
                        break;
                    case "3":
                        // Remove member by ID
                        System.out.print("ID to remove: ");
                        try {
                            int id = Integer.parseInt(sc.nextLine());
                            if (ml.remove(id)) {
                                saveToFile(ml);
                                System.out.println("Member with ID " + id + " has been successfully removed.");
                            } else {
                                System.out.println("Member with ID " + id + " was not found.");
                            }
                        } catch (Exception e) {
                            System.out.println("Invalid input. Please enter a valid member ID."); //NumberFormatException
                        }
                        break;
                    case "4":
                        // Display membership deadline
                        System.out.print("Member ID: ");
                        try {
                            int id = Integer.parseInt(sc.nextLine());
                            ml.showDeadlineById(id);
                        } catch (Exception e) {
                            System.out.println("Invalid input. Please enter a valid member ID.");
                        }
                        break;
                    case "5":
                        // List members by selected trainer
                        String t = chooseTrainer(sc);
                        ml.listMembersByTrainer(t);
                        break;
                    case "6":
                        // Manage trainer data
                        manageTrainers(sc);
                        break;
                    case "0":
                        System.out.println("Logging out.");
                        break;
                    default:
                        System.out.println("Invalid option.");
                }
            } while (!"0".equals(c));
        }
    }

    // Trainer user functionality
    static class TrainerUser extends AbstractUser {
        TrainerUser(String id, String password) {
            super(id, password);
        }

        // Trainer login validation
        public boolean login(Scanner sc) {
            System.out.print("Trainer ID: ");
            String tid = sc.nextLine();
            System.out.print("Password: ");
            String tp = sc.nextLine();

            if (id.equals(tid) && password.equals(tp)) {
                System.out.println("Successfully logged in as Trainer.");
                return true;
            } else {
                System.out.println("Invalid credentials for trainer login. Please try again.");
                return false;
            }
        }

        // Trainer panel menu and actions
        public void panel(Scanner sc, MemberList ml) {
            String ch;
            do {
                System.out.println("\n-- Trainer Panel --");
                System.out.println("1. My Members");
                System.out.println("2. Assign Member");
                System.out.println("0. Log out");
                System.out.print("Select: ");
                ch = sc.nextLine();
                switch (ch) {
                    case "1":
                        // List members assigned to this trainer
                        ml.listMembersByTrainer(id);
                        break;
                    case "2":
                        // Assign a member to this trainer
                        System.out.print("Member ID to assign: ");
                        try {
                            int memberId = Integer.parseInt(sc.nextLine());
                            Member m = ml.findById(memberId);
                            if (m != null) {
                                m.setTrainer(id);
                                saveToFile(ml);
                                System.out.println("Member with ID " + memberId + " has been assigned to you.");
                            } else {
                                System.out.println("No member found with ID " + memberId + ".");
                            }
                        } catch (Exception e) {
                            System.out.println("Invalid input. Please enter a valid member ID.");
                        }
                        break;
                    case "0":
                        System.out.println("Logging out.");
                        break;
                    default:
                        System.out.println("Invalid option.");
                }
            } while (!"0".equals(ch));
        }
    }

    // Customer role handling
    static class Customer implements User {
        private Member m;

        Customer(Member m) {
            this.m = m;
        }

        // Customer login is allowed if member object exists
        public boolean login(Scanner sc) {
            return m != null;
        }

        // Customer panel: view details, membership status, and renew membership
        public void panel(Scanner sc, MemberList ml) {
            String ch;
            do {
                System.out.println("\n-- Customer Panel --");
                System.out.println("1. View Personal Details");
                System.out.println("2. View Membership Deadline");
                System.out.println("3. Renew Membership");
                System.out.println("0. Log out");
                System.out.print("Select: ");
                ch = sc.nextLine();
                switch (ch) {
                    case "1":
                        // Print member details
                        System.out.println(m);
                        break;
                    case "2":
                        // Print member details
                        System.out.println("Membership ends on: " + m.getEndDate());
                        break;
                    case "3":
                        // Renew membership
                        renewMembership(sc, m);
                        saveToFile(ml);
                        break;
                    case "0":
                        System.out.println("Logging out.");
                        break;
                    default:
                        System.out.println("Invalid option.");
                }
            } while (!"0".equals(ch));
        }
    }

    // Trainer class stores credentials for authentication
    static class Trainer {
        private String id;
        private String password;

        Trainer(String id, String password) {
            this.id = id;
            this.password = password;
        }

        public String getId() {
            return id;
        }

        public String getPassword() {
            return password;
        }

        public String toString() {
            return id;
        }
    }

    // Member class represents a gym member's data and membership info
    static class Member {
        private int id;
        private String name;
        private int age;
        private String membership;
        private LocalDate startDate;
        private LocalDate endDate;
        private String trainer;

        Member(int id, String name, int age, String membership, LocalDate startDate, LocalDate endDate, String trainer) {
            this.id = id;
            this.name = name;
            this.age = age;
            this.membership = membership;
            this.startDate = startDate;
            this.endDate = endDate;
            this.trainer = trainer;
        }

        public int getId() {
            return id;
        }

        public LocalDate getEndDate() {
            return endDate;
        }

        // Assign a trainer to this member
        public void setTrainer(String t) {
            trainer = t;
        }

        // Member class methods
        public void renew(String mem, LocalDate s, LocalDate e) {
            membership = mem;
            startDate = s;
            endDate = e;
        }

        public String toString() {
            return String.format("%-5d %-15s %-5d %-10s %-12s %-12s %-10s", id, name, age, membership, startDate, endDate, trainer);
        }

        // Format member info for file saving
        String toFileString() {
            return id + "," + name.replace(",", "") + "," + age + "," + membership + "," + startDate + "," + endDate + "," + trainer;
        }

        // Parse a Member object from a CSV line in the file
        static Member fromFileString(String line) {
            String[] parts = line.split(",", 7);
            return new Member(Integer.parseInt(parts[0]), parts[1], Integer.parseInt(parts[2]), parts[3],
                    LocalDate.parse(parts[4]), LocalDate.parse(parts[5]), parts[6]);
        }
    }

    // Node class for linked list implementation of MemberList
    static class Node {
        Member data;
        Node next;

        Node(Member d) {
            data = d;
        }
    }

    // Custom linked list for members
    static class MemberList {
        Node head;

        // Add member at end of list
        void add(Member m) {
            if (head == null) head = new Node(m);
            else {
                Node cur = head;
                while (cur.next != null) cur = cur.next;
                cur.next = new Node(m);
            }
        }

        // Remove member by id
        boolean remove(int id) {
            if (head == null) return false;
            if (head.data.getId() == id) {
                head = head.next;
                return true;
            }
            Node cur = head;
            while (cur.next != null) {
                if (cur.next.data.getId() == id) {
                    cur.next = cur.next.next;
                    return true;
                }
                cur = cur.next;
            }
            return false;
        }

        // Convert linked list to array
        Member[] toArray() {
            List<Member> list = new ArrayList<>();
            for (Node cur = head; cur != null; cur = cur.next) list.add(cur.data);
            return list.toArray(new Member[0]);
        }

        // List all members
        void listAllMembers() {
            if (head == null) {
                System.out.println("There are currently no members in the gym.");
                return;
            }
            printHeader();
            for (Node cur = head; cur != null; cur = cur.next) System.out.println(cur.data);
        }

        // List members assigned to a specific trainer
        void listMembersByTrainer(String trainer) {
            boolean found = false;
            printHeader();
            for (Node cur = head; cur != null; cur = cur.next) {
                if (cur.data.trainer.equalsIgnoreCase(trainer)) {
                    System.out.println(cur.data);
                    found = true;
                }
            }
            if (!found) System.out.println("No members are currently assigned to trainer " + trainer + ".");
        }

        // Show membership deadline by member ID
        void showDeadlineById(int id) {
            for (Node cur = head; cur != null; cur = cur.next) {
                if (cur.data.getId() == id) {
                    System.out.println("Membership for member with ID " + id + " ends on: " + cur.data.getEndDate());
                    return;
                }
            }
            System.out.println("No member found with the specified ID.");
        }

        // Find member by ID
        Member findById(int id) {
            Member[] arr = toArray();
            int left = 0, right = arr.length - 1;
            while (left <= right) {
                int mid = (left + right) / 2;
                int midId = arr[mid].getId();
                if (midId == id) return arr[mid];
                if (id < midId) right = mid - 1;
                else left = mid + 1;
            }
            return null;
        }
    }

    // Add a new member with input validation and trainer assignment
    static void addMember(Scanner sc, MemberList ml) {
        try {
            System.out.print("Name: ");
            String name = sc.nextLine();
            if (!name.matches("[a-zA-Z ]+")) {
                System.out.println("Name must contain only letters and spaces.");
                return;
            }
            System.out.print("Age: ");
            int age = Integer.parseInt(sc.nextLine());
            if (age < 16 ) {
                System.out.println("Age must be 16 or older.");
                return;
            }
            if (age > 100 ) {
                System.out.println("Invalid age.");
                return;
            }
            System.out.print("Membership (Monthly/Quarterly/Yearly): ");
            String membership = sc.nextLine();
            if (!(membership.equalsIgnoreCase("Monthly") || membership.equalsIgnoreCase("Quarterly") || membership.equalsIgnoreCase("Yearly"))) {
                System.out.println("Invalid membership type.");
                return;
            }
            LocalDate startDate = LocalDate.now();
            LocalDate endDate = calculateEndDate(startDate, membership);
            String trainer = chooseTrainer(sc);

            Member newMember = new Member(nextId++, name, age, membership, startDate, endDate, trainer);
            ml.add(newMember);
            saveToFile(ml);
            System.out.println("Member has been added successfully.");
        } catch (NumberFormatException e) {
            System.out.println("Invalid number entered.");
        }
    }

    // Renew membership with new duration
    static void renewMembership(Scanner sc, Member m) {
        System.out.print("New membership (Monthly/Quarterly/Yearly): ");
        String mem = sc.nextLine();
        if (!(mem.equalsIgnoreCase("Monthly") || mem.equalsIgnoreCase("Quarterly") || mem.equalsIgnoreCase("Yearly"))) {
            System.out.println("Invalid membership type.");
            return;
        }
        LocalDate s = LocalDate.now();
        LocalDate e = calculateEndDate(s, mem);
        m.renew(mem, s, e);
        System.out.println("Membership has been renewed successfully.");
    }

    // Calculate membership end date based on membership type
    static LocalDate calculateEndDate(LocalDate start, String membership) {
        switch (membership.toLowerCase()) {
            case "monthly":
                return start.plusMonths(1);
            case "quarterly":
                return start.plusMonths(3);
            case "yearly":
                return start.plusYears(1);
            default:
                return start;
        }
    }

    // Let admin choose trainer from available list
    static String chooseTrainer(Scanner sc) {
        System.out.println("Available Trainers:");
        for (Trainer t : trainers) System.out.println("- " + t.getId());
        System.out.print("Assign trainer: ");
        String trainer = sc.nextLine();
        for (Trainer t : trainers) {
            if (t.getId().equalsIgnoreCase(trainer)) return t.getId();
        }
        System.out.println("Trainer not found. Assigning default trainer 'none'.");
        return "none";
    }

    // Admin panel to manage trainers
    static void manageTrainers(Scanner sc) {
        String c;
        do {
            System.out.println("\n-- Manage Trainers --");
            System.out.println("1. List Trainers");
            System.out.println("2. Add Trainer");
            System.out.println("3. Remove Trainer");
            System.out.println("0. Back");
            System.out.print("Choice: ");
            c = sc.nextLine();
            switch (c) {
                case "1":
                    if (trainers.isEmpty()) System.out.println("No trainers found.");
                    else for (Trainer t : trainers) {
                        System.out.println("Trainer ID: " + t.getId());
                    }
                    break;
                case "2":
                    System.out.print("New Trainer ID: ");
                    String id = sc.nextLine();
                    System.out.print("Password: ");
                    String pw = sc.nextLine();
                    trainers.add(new Trainer(id, pw));
                    saveTrainers();
                    System.out.println("Trainer has been added successfully.");
                    break;
                case "3":
                    System.out.print("Trainer ID to remove: ");
                    String rid = sc.nextLine();
                    boolean removed = false;

                    for (int i = 0; i < trainers.size(); i++) {
                        if (trainers.get(i).getId().equalsIgnoreCase(rid)) {
                            trainers.remove(i);
                            removed = true;
                            break;
                        }
                    }
                    if (removed) {
                        saveTrainers();
                        System.out.println("Trainer has been removed successfully.");
                    } else {
                        System.out.println("Trainer not found.");
                    }
                    break;

                case "0":
                    break;
                default:
                    System.out.println("Invalid option.");
            }
        } while (!"0".equals(c));
    }

    // Load members from file to MemberList
    static void loadFromFile(MemberList ml) {
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = br.readLine()) != null) {
                ml.add(Member.fromFileString(line));
            }
        } catch (IOException e) {
            // File may not exist at first run, no error needed
            System.out.println("File doesn't exist.");
        }
    }

    // Save members to file
    static void saveToFile(MemberList ml) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(FILE_NAME))) {
            for (Member m : ml.toArray()) {
                pw.println(m.toFileString());
            }
        } catch (IOException e) {
            System.out.println("Error occurred while saving member data.");
        }
    }

    // Load trainers from file
    public static void loadTrainers() {
        try (BufferedReader br = new BufferedReader(new FileReader("trainers.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.trim().split(",");
                if (parts.length == 2) {
                    trainers.add(new Trainer(parts[0].trim(), parts[1].trim()));
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading trainers file.");
        }
    }

    // Save trainers to file
    static void saveTrainers() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(TRAINER_FILE))) {
            for (Trainer t : trainers) {
                pw.println(t.getId() + "," + t.getPassword());
            }
        } catch (IOException e) {
            System.out.println("Error occurred while saving trainers data.");
        }
    }

    // Get next available ID based on current members
    static int getNextId(MemberList ml) {
        int max = 0;
        for (Member m : ml.toArray()) {
            if (m.getId() > max) max = m.getId();
        }
        return max + 1;
    }

    // Print table header for member listing
    static void printHeader() {
        System.out.printf("%-5s %-15s %-5s %-10s %-12s %-12s %-10s%n",
                "ID", "Name", "Age", "Membership", "Start Date", "End Date", "Trainer");
    }

    // Main app entry and user type selection loop
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        MemberList ml = new MemberList();
        loadFromFile(ml);
        loadTrainers();
        nextId = getNextId(ml);

        while (true) {
            System.out.println("\nSelect user type:");
            System.out.println("1. Admin");
            System.out.println("2. Trainer");
            System.out.println("3. Customer");
            System.out.println("0. Exit");
            System.out.print("Select: ");
            String choice = sc.nextLine();

            switch (choice) {
                case "1": {
                    User a = new Admin();
                    if (a.login(sc)) a.panel(sc, ml);
                    else System.out.println("Access denied.");
                    break;
                }

                case "2": {
                    System.out.print("Enter Trainer ID: ");
                    String tid = sc.nextLine().trim();
                    System.out.print("Enter Password: ");
                    String tp = sc.nextLine().trim();

                    boolean trainerFound = false;
                    for (Trainer t : trainers) {
                        if (t.getId().equalsIgnoreCase(tid) && t.getPassword().equals(tp)) {
                            User user = new TrainerUser(t.getId(), t.getPassword());
                            System.out.println("Successfully logged in as Trainer.");
                            user.panel(sc, ml);
                            trainerFound = true;
                            break;
                        }
                    }
                    if (!trainerFound) {
                        System.out.println("Invalid trainer credentials. Try again.");
                    }
                    break;
                }

                case "3": {
                    System.out.print("Member ID: ");
                    try {
                        int memberId = Integer.parseInt(sc.nextLine());
                        Member m = ml.findById(memberId);
                        if (m != null) {
                            Customer customer = new Customer(m);
                            if (customer.login(sc)) {
                                System.out.println("Successfully logged in as Customer.");
                                customer.panel(sc, ml);
                            }
                        } else {
                            System.out.println("Member not found.");
                        }
                    } catch (Exception e) {
                        System.out.println("Invalid input.");
                    }
                    break;
                }

                case "0":
                    System.out.println("Goodbye!");
                    sc.close();
                    System.exit(0);

                default:
                    System.out.println("Invalid option.");
            }
        }
    }
}
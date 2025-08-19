🏋️ Gym Membership Management System

A Java-based console application designed to simplify and streamline daily gym operations. This system ensures seamless interaction between Admins, Trainers, and Members, making fitness management smarter and more efficient.

✨ Features
👨‍💼 Admin

Register and manage gym members.

Assign members to trainers.

Verify member details (name, age, membership duration, etc.).

Track membership start/end dates.

Ensure timely membership renewals.

🏋️ Trainers

View all assigned members.

Organize members for better management.

Provide personalized support and guidance.

🙋 Members

Access personal profiles.

Check membership validity and duration.

Track progress.

Renew membership plans.

🛠️ Tech Stack

Language: Java

Paradigm: Object-Oriented Programming (OOP)

Data Handling: Custom Linked Lists

Persistence: File-based storage (text files)

Validation: Smart input checks for data integrity

🧩 System Design Highlights

Role-based Access: Admin, Trainer, and Member panels.

Custom Data Structures: Linked lists for efficient member management.

Persistence: File storage for saving member and trainer data.

Extensible: Easily upgradeable to advanced UI or database integration.

Error Handling: Validations for duplicate IDs, invalid inputs, and expired memberships.

📂 Project Structure
GymMembershipSystem/
│── src/
│   ├── Main.java
│   ├── models/
│   │   ├── Member.java
│   │   ├── Trainer.java
│   │   ├── Admin.java
│   │   └── User.java
│   ├── data/
│   │   ├── members.txt
│   │   └── trainers.txt
│   └── utils/
│       ├── MemberList.java
│       └── Validation.java
│
└── README.md

🚀 Getting Started
1️⃣ Clone the Repository
git clone https://github.com/your-username/gym-membership-system.git
cd gym-membership-system

2️⃣ Compile the Program
javac src/Main.java

3️⃣ Run the Program
java src/Main

🔮 Future Enhancements

✅ GUI-based interface (JavaFX / Swing).

✅ Database integration (MySQL / PostgreSQL).

✅ Payment gateway for online renewals.

✅ Reporting & analytics (active members, expired memberships, trainer loads).


📜 License

This project is licensed under the MIT License.

# Blockbuster Employee Management System (EMS)

## 📦 Project Overview

This project is a web-based **Employee Management System (EMS)** designed for a retro DVD rental business modeled after **Blockbuster**. The goal of the application is to provide store managers with a tool to view employee shifts, allocate resources efficiently, and maintain proper staffing across multiple store locations. For simplicity, this implementation focuses on **two store branches**.

Managers can:
- View employees and their schedules
- Add or remove employees from shifts
- See employee details such as name, age, and role


---

## ⚙️ Setup & Installation Instructions
### Step 1: Clone the Repository
```bash
git clone https://github.com/your-username/blockbuster-ems.git
cd blockbuster-ems(1)
```


### Step 2: Download MySQL and Database GUI
- Install [MySQL Server](https://dev.mysql.com/downloads/mysql/)
- Install a GUI like [DBeaver](https://dbeaver.io/) or [MySQL Workbench](https://www.mysql.com/products/workbench/)

##### Step 3: Create Database and Tables
- Create a database named (e.g., `blockbuster_ems`) on port 3306
- The database schema can be provided upon request, for this assignment that file is attached via. Canvas submission

### Step 4: Install Apache Tomcat
- Download and install [Apache Tomcat](https://tomcat.apache.org/) version 9+
- Extract and remember the destination(IntelliJ Setup)


### Step 5: Set Up IntelliJ Project (Ultimate Edition Needed)
1. Open the project in IntelliJ
2. Hover over the green "Run" arrow (top-right) and click **"Edit Configurations"**
3. Click the **+** icon → Select **Tomcat Server > Local**
4. Set the HTTP port to `8080`
5. Set the path to your **Apache Tomcat** installation from earlier
6. Click **Apply**

### Step 6: Connect Application to MySQL
- Open `DatabaseConnection.java`
- Update the username and password in the file to match the one you used to create your database

### Step 7: Run the Application
- Click the green **Run** arrow in the top-right corner
- Access the web app at: `http://localhost:8080/`

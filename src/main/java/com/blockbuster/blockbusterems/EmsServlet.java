package com.blockbuster.blockbusterems;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.blockbuster.util.DatabaseConnection;

@WebServlet("/blockbusterEMS")
public class EmsServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Handle form submissions for adding a new employee
        String action = request.getParameter("action");

        if ("addEmployee".equals(action)) {
            String firstName = request.getParameter("firstName");
            String lastName = request.getParameter("lastName");
            String email = request.getParameter("email");
            int storeId = Integer.parseInt(request.getParameter("storeId"));
            int roleId = Integer.parseInt(request.getParameter("roleId"));

            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(
                         "INSERT INTO employees (first_name, last_name, email, store_id, role_id) VALUES (?, ?, ?, ?, ?)")
            ) {

                pstmt.setString(1, firstName);
                pstmt.setString(2, lastName);
                pstmt.setString(3, email);
                pstmt.setInt(4, storeId);
                pstmt.setInt(5, roleId);

                pstmt.executeUpdate();

                // Redirect back to the employees view
                response.sendRedirect("blockbusterEMS?view=employees");
                return;
            } catch (SQLException e) {
                response.setContentType("text/html");
                PrintWriter out = response.getWriter();
                out.println("<p>Error adding employee: " + e.getMessage() + "</p>");
                e.printStackTrace(out);
            }
        }

        // If we get here, something went wrong, redirect to main page
        response.sendRedirect("blockbusterEMS");
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        // Get the 'view' parameter from the URL (e.g., /blockbusterEMS?view=employees)
        String view = request.getParameter("view");
        if (view == null) view = "employees";

        String sort = request.getParameter("sort");
        String order = request.getParameter("order");
        if (sort == null) sort = "employee_id"; // default sort
        if (order == null || (!order.equalsIgnoreCase("asc") && !order.equalsIgnoreCase("desc"))) {
            order = "asc"; // default order
        }

        out.println("<!DOCTYPE html>");
        out.println("<html lang='en'>");
        out.println("<head>");
        out.println("<title>Blockbuster EMS</title>");
        out.println("<style>");
        out.println("""
            body {
                font-family: Arial, sans-serif;
                background-color: #000033;
                color: #ffffcc;
                margin: 20px;
            }
            
            h1 {
                color: #ffcc00;
            }
            
            nav {
                display: flex;
                flex-wrap: wrap;
                gap: 10px;
                margin-bottom: 20px;
            }
            
            nav form {
                display: inline;
            }
            
            button {
                background-color: #ffcc00;
                color: #000033;
                font-weight: bold;
                border: 2px solid #ffcc00;
                padding: 10px 16px;
                border-radius: 6px;
                cursor: pointer;
                transition: background-color 0.2s ease-in-out, box-shadow 0.2s;
                font-size: 14px;
            }
            
            button:hover {
                background-color: #ffe066;
                box-shadow: 0 0 5px #ffcc00;
            }
            
            button:focus {
                outline: none;
                box-shadow: 0 0 6px 2px #ffcc00;
            }
            
            table {
                width: 100%;
                border-collapse: collapse;
                margin-top: 20px;
                background-color: #001144;
            }
            
            th, td {
                border: 1px solid #ffcc00;
                padding: 10px;
                text-align: left;
                color: #ffffff;
            }
            
            th {
                background-color: #000055;
                cursor: pointer;
            }
            
            tr:nth-child(even) {
                background-color: #000022;
            }
            
            /* Modal Styles */
            .modal {
                display: none;
                position: fixed;
                z-index: 1;
                left: 0;
                top: 0;
                width: 100%;
                height: 100%;
                overflow: auto;
                background-color: rgba(0,0,0,0.7);
            }
            
            .modal-content {
                background-color: #001144;
                margin: 10% auto;
                padding: 20px;
                border: 2px solid #ffcc00;
                width: 60%;
                max-width: 500px;
                border-radius: 8px;
                box-shadow: 0 0 20px #ffcc00;
            }
            
            .close {
                color: #ffcc00;
                float: right;
                font-size: 28px;
                font-weight: bold;
                cursor: pointer;
            }
            
            .close:hover {
                color: #ffe066;
            }
            
            .form-group {
                margin-bottom: 15px;
            }
            
            label {
                display: block;
                margin-bottom: 5px;
                color: #ffcc00;
            }
            
            input, select {
                width: 80%;
                max-width: 300px;
                padding: 8px;
                border: 1px solid #ffcc00;
                background-color: #000055;
                color: #ffffff;
                border-radius: 4px;
            }
            
            .add-btn {
                background-color: #009900;
                color: white;
                padding: 10px 20px;
                border: none;
                border-radius: 6px;
                cursor: pointer;
                float: right;
                margin-top: 10px;
            }
            
            .add-btn:hover {
                background-color: #00cc00;
                box-shadow: 0 0 5px #00cc00;
            }
            
            /* Clear floats */
            .clearfix::after {
                content: "";
                clear: both;
                display: table;
            }
            
            .action-bar {
                margin: 20px 0;
                text-align: right;
            }
        """);
        out.println("</style>");
        out.println("</head>");
        out.println("<body>");

        out.println("<h1>Blockbuster Employee Management System</h1>");

        // Navigation buttons
        out.println("""
            <nav>
                <form action="blockbusterEMS" method="get" style="display: inline;">
                    <input type="hidden" name="view" value="employees" />
                    <button type="submit">Employees</button>
                </form>
                <form action="blockbusterEMS" method="get" style="display: inline;">
                    <input type="hidden" name="view" value="roles" />
                    <button type="submit">Roles</button>
                </form>
                <form action="blockbusterEMS" method="get" style="display: inline;">
                    <input type="hidden" name="view" value="stores" />
                    <button type="submit">Stores</button>
                </form>
                <form action="blockbusterEMS" method="get" style="display: inline;">
                    <input type="hidden" name="view" value="shifts" />
                    <button type="submit">Shifts</button>
                </form>
                <form action="blockbusterEMS" method="get" style="display: inline;">
                    <input type="hidden" name="view" value="trainings" />
                    <button type="submit">Trainings</button>
                </form>
            </nav>
        """);

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            ResultSet rs;

            // This switch handles which table to view
            switch (view) {
                case "roles":
                    rs = stmt.executeQuery("SELECT * FROM roles");
                    out.println("<h3>Roles</h3>");
                    out.println("<table id='dataTable' data-sort='asc'>");
                    out.println("<tr><th onclick='sortTable(0)'>Role ID</th><th onclick='sortTable(1)'>Role Name</th></tr>");
                    while (rs.next()) {
                        out.printf("<tr><td>%d</td><td>%s</td></tr>",
                                rs.getInt("role_id"),
                                rs.getString("role_name"));
                    }
                    out.println("</table>");
                    break;

                case "stores":
                    rs = stmt.executeQuery("SELECT * FROM stores");
                    out.println("<h3>Stores</h3>");
                    out.println("<table id='dataTable' data-sort='asc'>");
                    out.println("<tr><th onclick='sortTable(0)'>Store ID</th><th onclick='sortTable(1)'>Store Name</th><th onclick='sortTable(2)'>Location</th></tr>");
                    while (rs.next()) {
                        out.printf("<tr><td>%d</td><td>%s</td><td>%s</td></tr>",
                                rs.getInt("store_id"),
                                rs.getString("store_name"),
                                rs.getString("location"));
                    }
                    out.println("</table>");
                    break;

                case "shifts":
                    rs = stmt.executeQuery("SELECT * FROM shifts");
                    out.println("<h3>Shifts</h3>");
                    out.println("<table id='dataTable' data-sort='asc'>");
                    out.println("<tr><th onclick='sortTable(0)'>Shift ID</th><th onclick='sortTable(1)'>Start Time</th><th onclick='sortTable(2)'>End Time</th><th onclick='sortTable(3)'>Employee ID</th></tr>");
                    while (rs.next()) {
                        out.printf("<tr><td>%d</td><td>%s</td><td>%s</td><td>%d</td></tr>",
                                rs.getInt("shift_id"),
                                rs.getString("start_time"),
                                rs.getString("end_time"),
                                rs.getInt("employee_id"));
                    }
                    out.println("</table>");
                    break;



                case "trainings":
                    rs = stmt.executeQuery("SELECT t.training_id, t.employee_id, e.first_name, e.last_name, t.module_name, t.completion_date " +
                            "FROM trainings t JOIN employees e ON t.employee_id = e.employee_id");
                    out.println("<h3>Training Records</h3>");
                    out.println("<table id='dataTable' data-sort='asc'>");
                    out.println("<tr>");
                    out.println("<th onclick='sortTable(0)'>Training ID</th>");
                    out.println("<th onclick='sortTable(1)'>Employee ID</th>");
                    out.println("<th onclick='sortTable(2)'>Name</th>");
                    out.println("<th onclick='sortTable(3)'>Module</th>");
                    out.println("<th onclick='sortTable(4)'>Completion Date</th>");
                    out.println("</tr>");
                    while (rs.next()) {
                        out.printf("<tr><td>%d</td><td>%d</td><td>%s %s</td><td>%s</td><td>%s</td></tr>",
                                rs.getInt("training_id"),
                                rs.getInt("employee_id"),
                                rs.getString("first_name"),
                                rs.getString("last_name"),
                                rs.getString("module_name"),
                                rs.getString("completion_date"));
                    }
                    out.println("</table>");
                    break;

                case "employees": //default
                default:
                    // Add the "New Employee" button above the table
                    out.println("<div class='action-bar'>");
                    out.println("<button id='newEmployeeBtn'>Add New Employee</button>");
                    out.println("</div>");

                    rs = stmt.executeQuery("SELECT * FROM employees ORDER BY " + sort + " " + order);
                    out.println("<h3>Employees</h3>");
                    out.println("<table id='dataTable' data-sort='asc'>");
                    out.println("<tr>");
                    out.println("<th onclick='sortTable(0)'>ID</th>");
                    out.println("<th onclick='sortTable(1)'>First Name</th>");
                    out.println("<th onclick='sortTable(2)'>Last Name</th>");
                    out.println("<th onclick='sortTable(3)'>Email</th>");
                    out.println("<th onclick='sortTable(4)'>Store ID</th>");
                    out.println("<th onclick='sortTable(5)'>Role ID</th>");
                    out.println("</tr>");
                    while (rs.next()) {
                        out.printf("<tr><td>%s</td><td>%s</td><td>%s</td><td>%s</td><td>%d</td><td>%d</td></tr>",
                                rs.getString("employee_id"),
                                rs.getString("first_name"),
                                rs.getString("last_name"),
                                rs.getString("email"),
                                rs.getInt("store_id"),
                                rs.getInt("role_id"));
                    }
                    out.println("</table>");

                    // Add New Employee Modal
                    out.println("<div id='employeeModal' class='modal'>");
                    out.println("  <div class='modal-content'>");
                    out.println("    <span class='close'>&times;</span>");
                    out.println("    <h3>Add New Employee</h3>");
                    out.println("    <form action='blockbusterEMS' method='post'>");
                    out.println("      <input type='hidden' name='action' value='addEmployee'>");

                    out.println("      <div class='form-group'>");
                    out.println("        <label for='firstName'>First Name:</label>");
                    out.println("        <input type='text' id='firstName' name='firstName' required>");
                    out.println("      </div>");

                    out.println("      <div class='form-group'>");
                    out.println("        <label for='lastName'>Last Name:</label>");
                    out.println("        <input type='text' id='lastName' name='lastName' required>");
                    out.println("      </div>");

                    out.println("      <div class='form-group'>");
                    out.println("        <label for='email'>Email:</label>");
                    out.println("        <input type='email' id='email' name='email' required>");
                    out.println("      </div>");

                    // Store dropdown
                    out.println("      <div class='form-group'>");
                    out.println("        <label for='storeId'>Store:</label>");
                    out.println("        <select id='storeId' name='storeId' required>");
                    ResultSet storeRs = stmt.executeQuery("SELECT store_id, store_name FROM stores");
                    while (storeRs.next()) {
                        out.printf("          <option value='%d'>%s</option>",
                                storeRs.getInt("store_id"),
                                storeRs.getString("store_name"));
                    }
                    out.println("        </select>");
                    out.println("      </div>");

                    // Role dropdown
                    out.println("      <div class='form-group'>");
                    out.println("        <label for='roleId'>Role:</label>");
                    out.println("        <select id='roleId' name='roleId' required>");
                    ResultSet roleRs = stmt.executeQuery("SELECT role_id, role_name FROM roles");
                    while (roleRs.next()) {
                        out.printf("          <option value='%d'>%s</option>",
                                roleRs.getInt("role_id"),
                                roleRs.getString("role_name"));
                    }
                    out.println("        </select>");
                    out.println("      </div>");

                    out.println("      <div class='clearfix'>");
                    out.println("        <button type='submit' class='add-btn'>Add Employee</button>");
                    out.println("      </div>");
                    out.println("    </form>");
                    out.println("  </div>");
                    out.println("</div>");
            }

        } catch (SQLException e) {
            out.println("<p>Error: " + e.getMessage() + "</p>");
            e.printStackTrace(out);
        }

        // Add JavaScript for sorting and modal functionality
        out.println("<script>");
        out.println("""
            function sortTable(n) {
                const table = document.getElementById("dataTable");
                const rows = Array.from(table.rows).slice(1);
                const isAsc = table.getAttribute("data-sort") !== "asc";
                
                rows.sort((a, b) => {
                    let cellA = a.cells[n].innerText.trim();
                    let cellB = b.cells[n].innerText.trim();
                    
                    const isNumeric = !isNaN(cellA) && !isNaN(cellB);
                    
                    if (isNumeric) {
                        return (parseFloat(cellA) - parseFloat(cellB)) * (isAsc ? 1 : -1);
                    } else {
                        return cellA.localeCompare(cellB) * (isAsc ? 1 : -1);
                    }
                });
                
                rows.forEach(row => table.appendChild(row));
                table.setAttribute("data-sort", isAsc ? "asc" : "desc");
            }
            
            // Modal JavaScript
            document.addEventListener('DOMContentLoaded', function() {
                // Get the modal
                var modal = document.getElementById('employeeModal');
                
                // Get the button that opens the modal
                var btn = document.getElementById('newEmployeeBtn');
                
                // Get the <span> element that closes the modal
                var span = document.getElementsByClassName('close')[0];
                
                // When the user clicks the button, open the modal 
                if (btn) {
                    btn.onclick = function() {
                        modal.style.display = "block";
                    }
                }
                
                // When the user clicks on <span> (x), close the modal
                if (span) {
                    span.onclick = function() {
                        modal.style.display = "none";
                    }
                }
                
                // When the user clicks anywhere outside of the modal, close it
                window.onclick = function(event) {
                    if (event.target == modal) {
                        modal.style.display = "none";
                    }
                }
            });
        """);
        out.println("</script>");

        out.println("</body></html>");
    }

    private String toggleOrder(String currentSort, String currentOrder, String column) {
        if (column.equals(currentSort)) {
            return currentOrder.equals("asc") ? "desc" : "asc";
        }
        return "asc";
    }
}
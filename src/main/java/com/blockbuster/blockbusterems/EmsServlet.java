package com.blockbuster.blockbusterems;

/*
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
*/

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.blockbuster.util.DatabaseConnection;

@WebServlet("/blockbusterEMS")
public class EmsServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        // Get the 'view' parameter from the URL (e.g., /blockbusterEMS?view=employees)
        String view = request.getParameter("view");
        if (view == null) view = "employees"; // default view

        // Start of HTML output
        out.println("""
    <html>
    <head>
        <title>Blockbuster EMS</title>
        <style>
            body {
                font-family: Arial, sans-serif;
                background-color: #0a0a0a;
                color: #f8f8f8;
                margin: 20px;
            }
            h1 {
                background-color: #002366;
                color: #FFD700;
                padding: 10px;
                border-radius: 5px;
            }
            nav button {
                background-color: #FFD700;
                color: #002366;
                border: none;
                padding: 10px 15px;
                margin: 5px;
                border-radius: 4px;
                cursor: pointer;
                font-weight: bold;
            }
            nav button:hover {
                background-color: #ffcc00;
            }
            .bb-table {
                width: 100%;
                border-collapse: collapse;
                margin-top: 15px;
            }
            .bb-table th, .bb-table td {
                border: 1px solid #ddd;
                padding: 8px;
                text-align: left;
            }
            .bb-table th {
                background-color: #002366;
                color: #FFD700;
            }
            .bb-table tr:nth-child(even) {
                background-color: #1a1a1a;
            }
        </style>
    </head>
    <body>
    """);

        out.println("<h1>Blockbuster Employee Management System</h1>");

        // Navigation buttons (took out: /employee_management_system_war_exploded/)
        out.println("""
            <nav>
                <form action="blockbusterEMS" method="get" style="display:inline;">
                    <button type="submit" name="view" value="employees">Employees</button>
                </form>
                <form action="blockbusterEMS" method="get" style="display:inline;">
                    <button type="submit" name="view" value="roles">Roles</button>
                </form>
                <form action="blockbusterEMS" method="get" style="display:inline;">
                    <button type="submit" name="view" value="stores">Stores</button>
                </form>
                <form action="blockbusterEMS" method="get" style="display:inline;">
                    <button type="submit" name="view" value="shifts">Shifts</button>
                </form>
                <form action="blockbusterEMS" method="get" style="display:inline;">
                    <button type="submit" name="view" value="trainings">Trainings</button>
                </form>
            </nav>
            <hr>
        """);

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            ResultSet rs;

            // This switch handles which table to view
            switch (view) {
                case "roles":
                    rs = stmt.executeQuery("SELECT * FROM roles");
                    out.println("<h3>Roles</h3>");
                    out.println("<table class='bb-table'>");
                    out.println("<tr><th>Role ID</th><th>Role Name</th></tr>");
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
                    out.println("<table class='bb-table'>");
                    out.println("<tr><th>Store ID</th><th>Store Name</th><th>Location</th></tr>");
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
                    out.println("<table class='bb-table'>");
                    out.println("<tr><th>Shift ID</th><th>Start Time</th><th>End Time</th><th>Employee ID</th></tr>");
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
                    out.println("<table class='bb-table'>");
                    out.println("<tr><th>Training ID</th><th>Employee ID</th><th>Name</th><th>Module</th><th>Completion Date</th></tr>");
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

                case "employees":
                default:
                    rs = stmt.executeQuery("SELECT * FROM employees");
                    out.println("<h3>Employees</h3>");
                    out.println("<table class='bb-table'>");
                    out.println("<tr><th>ID</th><th>First Name</th><th>Last Name</th><th>Email</th><th>Store ID</th><th>Role ID</th></tr>");
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
                    break;
            }

        } catch (SQLException e) {
            out.println("<p>Error: " + e.getMessage() + "</p>");
            e.printStackTrace(out);
        }

        out.println("</body></html>");
    }
}

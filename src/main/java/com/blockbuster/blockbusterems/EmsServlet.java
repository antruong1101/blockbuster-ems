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
        out.println("<html><head><title>Blockbuster EMS</title></head><body>");
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
                    out.println("<h3>Roles</h3><ul>");
                    while (rs.next()) {
                        out.printf("<li>%d - %s</li>", rs.getInt("role_id"), rs.getString("role_name"));
                    }
                    out.println("</ul>");
                    break;

                case "stores":
                    rs = stmt.executeQuery("SELECT * FROM stores");
                    out.println("<h3>Stores</h3><ul>");
                    while (rs.next()) {
                        out.printf("<li>%d - %s (%s)</li>",
                                rs.getInt("store_id"),
                                rs.getString("store_name"),
                                rs.getString("location"));
                    }
                    out.println("</ul>");
                    break;

                case "shifts":
                    rs = stmt.executeQuery("SELECT * FROM shifts");
                    out.println("<h3>Shifts</h3><ul>");
                    while (rs.next()) {
                        out.printf("<li>Shift %d: %s to %s (Employee %d)</li>",
                                rs.getInt("shift_id"),
                                rs.getString("start_time"),
                                rs.getString("end_time"),
                                rs.getInt("employee_id"));
                    }
                    out.println("</ul>");
                    break;

                case "trainings":
                    rs = stmt.executeQuery("SELECT t.training_id, t.employee_id, e.first_name, e.last_name, t.module_name, t.completion_date " +
                            "FROM trainings t JOIN employees e ON t.employee_id = e.employee_id");
                    out.println("<h3>Training Records</h3><ul>");
                    while (rs.next()) {
                        out.printf("<li>Employee %d - %s %s: Completed '%s' on %s</li>",
                                rs.getInt("employee_id"),
                                rs.getString("first_name"),
                                rs.getString("last_name"),
                                rs.getString("module_name"),
                                rs.getString("completion_date"));
                    }
                    out.println("</ul>");
                    break;

                case "employees":
                default:
                    rs = stmt.executeQuery("SELECT * FROM employees");
                    out.println("<h3>Employees</h3><ul>");
                    while (rs.next()) {
                        out.printf("<li><strong>%s</strong>: %s %s (%s), Store: %d, Role: %d</li>",
                                rs.getString("employee_id"),
                                rs.getString("first_name"),
                                rs.getString("last_name"),
                                rs.getString("email"),
                                rs.getInt("store_id"),
                                rs.getInt("role_id"));
                    }
                    out.println("</ul>");
                    break;
            }

        } catch (SQLException e) {
            out.println("<p>Error: " + e.getMessage() + "</p>");
            e.printStackTrace(out);
        }

        out.println("</body></html>");
    }
}

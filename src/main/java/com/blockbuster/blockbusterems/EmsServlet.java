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
        if (view == null) view = "employees";

        String sort = request.getParameter("sort");
        String order = request.getParameter("order");
        if (sort == null) sort = "employee_id"; // default sort
        if (order == null || (!order.equalsIgnoreCase("asc") && !order.equalsIgnoreCase("desc"))) {
            order = "asc"; // default order
        }


        out.println("<h1>Blockbuster Employee Management System</h1>");

        // Navigation buttons (took out: /employee_management_system_war_exploded/)
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


        out.println("""
                <head>
                    <title>Blockbuster EMS</title>
                    <style>
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
                
                                                .sort-arrow {
                                                                    font-size: 0.8em;
                                                                    margin-left: 4px;
                                                }
                
                                            </style>
                
                    <script>
                                                                  function sortTable(columnIndex, clickedHeader) {
                                                                      const table = document.getElementById("dataTable");
                                                                      const rows = Array.from(table.rows).slice(1);
                                                                      const currentSort = table.getAttribute("data-sort-column");
                                                                      let currentOrder = table.getAttribute("data-sort-order") || "asc";
                                                                      const isSameColumn = currentSort == columnIndex;
                                                                      const newOrder = isSameColumn && currentOrder === "asc" ? "desc" : "asc";
        
                                                                      rows.sort((a, b) => {
                                                                          let cellA = a.cells[columnIndex].innerText.trim();
                                                                          let cellB = b.cells[columnIndex].innerText.trim();
                                                                          const isNumeric = !isNaN(cellA) && !isNaN(cellB);
        
                                                                          if (isNumeric) {
                                                                              return (parseFloat(cellA) - parseFloat(cellB)) * (newOrder === "asc" ? 1 : -1);
                                                                          } else {
                                                                              return cellA.localeCompare(cellB) * (newOrder === "asc" ? 1 : -1);
                                                                          }
                                                                      });
        
                                                                      rows.forEach(row => table.appendChild(row));
                                                                      table.setAttribute("data-sort-column", columnIndex);
                                                                      table.setAttribute("data-sort-order", newOrder);
        
                                                                      // Reset arrows
                                                                      document.querySelectorAll(".sort-arrow").forEach(el => el.textContent = "");
                                                                      if (clickedHeader) {
                                                                      clickedHeader.querySelector(".sort-arrow").innerHTML = newOrder === "asc" ? " &uarr;" : " &darr;";
                
                                                                                                                                            clickedHeader.querySelector(".sort-arrow").innerHTML = newOrder === "asc" ? " &uarr;" : " &darr;";                                                                                                                         }
                                                                  }
                                                              </script>
                
                
                </head>
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
                    out.println("<tr>\n" +
                            "    <th onclick=\"sortTable(0, this)\" data-column=\"0\">Role ID <span class=\"sort-arrow\"></span></th>\n" +
                            "    <th onclick=\"sortTable(1, this)\" data-column=\"1\">Role Name <span class=\"sort-arrow\"></span></th>\n" +
                            "</tr>\n");
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
                    out.println("<tr>\n" +
                            "    <th onclick=\"sortTable(0, this)\" data-column=\"0\">Store ID <span class=\"sort-arrow\"></span></th>\n" +
                            "    <th onclick=\"sortTable(1, this)\" data-column=\"1\">Store Name <span class=\"sort-arrow\"></span></th>\n" +
                            "    <th onclick=\"sortTable(2, this)\" data-column=\"2\">Location <span class=\"sort-arrow\"></span></th>\n" +
                            "</tr>\n");
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
                    out.println("<tr>\n" +
                            "    <th onclick=\"sortTable(0, this)\">Shift ID <span class=\"sort-arrow\"></span></th>\n" +
                            "    <th onclick=\"sortTable(1, this)\">Start Time <span class=\"sort-arrow\"></span></th>\n" +
                            "    <th onclick=\"sortTable(2, this)\">End Time <span class=\"sort-arrow\"></span></th>\n" +
                            "    <th onclick=\"sortTable(3, this)\">Employee ID <span class=\"sort-arrow\"></span></th>\n" +
                            "</tr>\n");
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
                    out.println("<tr>\n" +
                            "    <th onclick=\"sortTable(0, this)\" data-column=\"0\">Training ID <span class=\"sort-arrow\"></span></th>\n" +
                            "    <th onclick=\"sortTable(1, this)\" data-column=\"1\">Employee ID <span class=\"sort-arrow\"></span></th>\n" +
                            "    <th onclick=\"sortTable(2, this)\" data-column=\"2\">Name <span class=\"sort-arrow\"></span></th>\n" +
                            "    <th onclick=\"sortTable(3, this)\" data-column=\"3\">Module <span class=\"sort-arrow\"></span></th>\n" +
                            "    <th onclick=\"sortTable(4, this)\" data-column=\"4\">Completion Date <span class=\"sort-arrow\"></span></th>\n" +
                            "</tr>\n");
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
                    rs = stmt.executeQuery("SELECT * FROM employees ORDER BY " + sort + " " + order);
                    out.println("<h3>Employees</h3>");
                    out.println("<table id='dataTable' data-sort='asc'>");
                    out.println("<tr>\n" +
                            "    <th onclick=\"sortTable(0, this)\" data-column=\"0\">ID <span class=\"sort-arrow\"></span></th>\n" +
                            "    <th onclick=\"sortTable(1, this)\" data-column=\"1\">First Name <span class=\"sort-arrow\"></span></th>\n" +
                            "    <th onclick=\"sortTable(2, this)\" data-column=\"2\">Last Name <span class=\"sort-arrow\"></span></th>\n" +
                            "    <th onclick=\"sortTable(3, this)\" data-column=\"3\">Email <span class=\"sort-arrow\"></span></th>\n" +
                            "    <th onclick=\"sortTable(4, this)\" data-column=\"4\">Store ID <span class=\"sort-arrow\"></span></th>\n" +
                            "    <th onclick=\"sortTable(5, this)\" data-column=\"5\">Role ID <span class=\"sort-arrow\"></span></th>\n" +
                            "</tr>\n");
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
            }

        } catch (SQLException e) {
            out.println("<p>Error: " + e.getMessage() + "</p>");
            e.printStackTrace(out);
        }

        out.println("</body></html>");
    }

    private String toggleOrder(String currentSort, String currentOrder, String column) {
        if (column.equals(currentSort)) {
            return currentOrder.equals("asc") ? "desc" : "asc";
        }
        return "asc";
    }
}

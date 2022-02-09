package ru.job4j.cinema.controller;

import com.google.gson.Gson;
import ru.job4j.cinema.model.Ticket;
import ru.job4j.cinema.store.DbStore;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

@WebServlet(value = "/hall.do")
public class HallServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        var gson = (Gson) getServletContext().getAttribute("GSON");
        Collection<Ticket> tickets = DbStore.instOf().findAllTickets();
        HashMap<Integer, List<Ticket>> hall = new HashMap<>();
        for (Ticket ticket : tickets) {
            var rowNumber = ticket.getRow();
            if (hall.containsKey(rowNumber)) {
                hall.get(rowNumber).add(ticket);
            } else {
                List<Ticket> list = new ArrayList<>();
                list.add(ticket);
                hall.put(rowNumber, list);
            }
        }
        StringBuilder sb = new StringBuilder();
        for (Integer i : hall.keySet()) {
            sb.append("<tbody><tr><th>" + i + "</th>");
            for (Ticket ticket : hall.get(i)) {
                String available = ticket.isAvailable() ? "" : "disabled= \"true";
                sb.append("\"<td><input type=\"radio\" name=\"place\" value=\"" + ticket.getRow() + ticket.getCell()
                        +"\" title=\"Ряд " + ticket.getRow() + ", Место " + ticket.getCell() + "\""
                        + available + " id=\"" + ticket.getId() + "\""
                        + "\"> Ряд " + ticket.getRow() + ", Место" + ticket.getCell() + "</td>\""
                );
            }
            sb.append("</tr></tbody>");
        }
        resp.setContentType("application/json; charset=utf-8");
        OutputStream outputStream = resp.getOutputStream();
        String json = gson.toJson(sb);
        outputStream.write(json.getBytes(StandardCharsets.UTF_8));
        outputStream.flush();

    }
}

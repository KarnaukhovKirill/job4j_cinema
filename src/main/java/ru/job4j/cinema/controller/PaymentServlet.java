package ru.job4j.cinema.controller;

import com.google.gson.Gson;
import ru.job4j.cinema.model.Account;
import ru.job4j.cinema.store.DbStore;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

@WebServlet(value = "/payment.do")
public class PaymentServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        var gson = (Gson) getServletContext().getAttribute("GSON");
        var place = req.getParameter("place");
        var id = req.getParameter("id");
        var session = req.getParameter("session");
        var username = req.getParameter("username");
        var email = req.getParameter("email");
        var phone = req.getParameter("phone");
        resp.setContentType("application/json; charset=utf-8");
        OutputStream outputStream = resp.getOutputStream();
        var store = DbStore.instOf();
        var account = store.findAccountByPhone(phone);
        if (account == null) {
            account = new Account(0, username, email, phone);
        } else {
            account = new Account(account.getId(), username, email, phone);
        }
        store.saveAccount(account);
        var ticket = store.findTicketById(Integer.parseInt(id));
        ticket.setAvailable(false);
        ticket.setAccountId(account.getId());
        store.saveTicket(ticket);
        String response = "Кресло " + place.charAt(1) + " в " + place.charAt(0) + "-ом ряду зарезервировано для "
                + account.getUsername() + ". Ждём Вас в " + session + " в нашем кинотеатре!";
        String json = gson.toJson(response);
        outputStream.write(json.getBytes(StandardCharsets.UTF_8));
        outputStream.flush();
    }
}

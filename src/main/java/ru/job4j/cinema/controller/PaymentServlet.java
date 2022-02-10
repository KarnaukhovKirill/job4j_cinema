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
        var place = req.getParameter("place");
        var id = req.getParameter("id");
        var session = req.getParameter("session");
        var username = req.getParameter("username");
        var email = req.getParameter("email");
        var phone = req.getParameter("phone");
        resp.setContentType("text/html; charset=utf-8");
        OutputStream outputStream = resp.getOutputStream();
        String response;
        var store = DbStore.instOf();
        var ticket = store.findTicketById(Integer.parseInt(id));
        if (ticket.isAvailable()) {
            var account = store.findAccountByPhone(phone);
            if (account == null) {
                account = new Account(0, username, email, phone);
            } else {
                account = new Account(account.getId(), username, email, phone);
            }
            store.saveAccount(account);
            ticket.setAvailable(false);
            ticket.setAccountId(account.getId());
            store.saveTicket(ticket);
            response = "<p>Кресло " + place.charAt(1) + " в " + place.charAt(0) + "-ом ряду зарезервировано для "
                    + account.getUsername() + ". Ждём Вас в " + session + " в нашем кинотеатре!</p>";
        } else {
            response = "<p>К сожалению, кресло " + place.charAt(1) + " в " + place.charAt(0) + "-ом ряду уже занято.</p>";
        }
        response += "<p><a href=\"/index.html\">На главную</a></p>";
        outputStream.write(response.getBytes(StandardCharsets.UTF_8));
        outputStream.flush();
    }
}

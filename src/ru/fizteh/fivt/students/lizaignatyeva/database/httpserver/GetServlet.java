package ru.fizteh.fivt.students.lizaignatyeva.database.httpserver;

import ru.fizteh.fivt.storage.structured.Storeable;
import ru.fizteh.fivt.students.lizaignatyeva.database.MyTable;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class GetServlet extends HttpServlet {
    private Database database;

    public GetServlet(Database database) {
        this.database = database;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String transactionId = req.getParameter("tid");
        if (transactionId == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "No tid provided");
            return;
        }
        String key = req.getParameter("key");
        if (key == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "No key provided");
            return;
        }
        MyTable table = database.getTransaction(transactionId);
        if (table == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "No such transaction");
            return;
        }
        String result;
        try {
            Storeable value = table.get(key);
            if (value == null) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "No such key"); // SC_NOT_FOUND ?
                return;
            }
            result = database.tableProvider.serialize(table, value);
        } catch (IllegalArgumentException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Illegal key");
            return;
        } catch (Exception e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to get");
            return;
        }
        resp.setStatus(HttpServletResponse.SC_OK);
        resp.setContentType("text/plain");
        resp.setCharacterEncoding("UTF8");
        resp.getWriter().println(result);
    }
}

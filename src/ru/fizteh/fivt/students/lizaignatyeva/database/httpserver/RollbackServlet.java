package ru.fizteh.fivt.students.lizaignatyeva.database.httpserver;

import ru.fizteh.fivt.students.lizaignatyeva.database.MyTable;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class RollbackServlet extends HttpServlet {
    private Database database;

    public RollbackServlet(Database database) {
        this.database = database;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String transactionId = req.getParameter("tid");
        if (transactionId == null) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "No tid provided");
            return;
        }
        MyTable table = database.getTransaction(transactionId);
        if (table == null) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "No such tid");
            return;
        }
        int result;
        try {
            result = table.rollback();
        } catch (Exception e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to rollback: " + e.getMessage());
            return;
        }
        database.cancelTransaction(transactionId);
        resp.setStatus(HttpServletResponse.SC_OK);
        resp.setContentType("text/plain");
        resp.setCharacterEncoding("UTF8");
        resp.getWriter().println("diff=" + result);
    }
}

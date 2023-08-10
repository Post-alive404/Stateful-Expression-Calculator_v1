package servlet;

import services.HandlerStatefulExpression;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Writer;

@WebServlet("/calc/*")
public class StatefulCalcServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        Writer writer = resp.getWriter();

        String result = HandlerStatefulExpression.calculateExpression(session);

        if(result == null) {
            String invalid = session.getAttribute("invalidArgument").toString();
            resp.setStatus(409);
            writer.write(invalid + " is invalid");
            return;

        }
        resp.setStatus(200);
        writer.write(result);


    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        BufferedReader reader = req.getReader();
        HttpSession session = req.getSession();

        if(req.getRequestURI().endsWith("/expression")){
            if(session.getAttribute("expression") != null){
                resp.setStatus(200);
            } else {
                resp.setStatus(201);
            }
            String expression = reader.readLine();

            if(isBadFormatExpression(expression)){
                resp.setStatus(400);
                return;
            }
            session.setAttribute("expression", expression);
        } else if(req.getRequestURI().startsWith("/calc/")){
            String variable = req.getRequestURI().split("/")[2];
            String value = reader.readLine();

            if(isBadFormatVariable(variable)){
                resp.setStatus(400);
                return;
            }

            if(isBadFormatValue(value)){
                resp.setStatus(403);
                return;
            }

            if (session.getAttribute(variable) != null){
                resp.setStatus(200);
            } else {
                resp.setStatus(201);
            }

            session.setAttribute(variable, value);
        }
    }



    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String variable = req.getRequestURI().split("/")[2];
        HttpSession session = req.getSession();

        if(session.getAttribute(variable) == null){
            resp.setStatus(409);
            resp.getWriter().write(variable + " is invalid");
            return;
        }

        session.setAttribute(variable, null);
        resp.setStatus(204);
    }

    private boolean isBadFormatExpression(String expression) {
        return !(expression.contains("+")||expression.contains("-")||
                expression.contains("/")||expression.contains("*"));
    }

    private boolean isBadFormatValue(String value) {
        return value == null ||
                ( (value.charAt(0) >= '0' &&
                        value.charAt(0) <= '9') || value.charAt(0) == '-')
                        && (Integer.parseInt(value) >10000 || Integer.parseInt(value) < -10000);
    }

    private boolean isBadFormatVariable(String variable){
        return variable.matches("[\\d\\s\\WA-Z]");
    }

}

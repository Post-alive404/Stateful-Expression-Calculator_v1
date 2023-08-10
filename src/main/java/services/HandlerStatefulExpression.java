package services;

import javax.servlet.http.HttpSession;


public class HandlerStatefulExpression {

    static public String calculateExpression(HttpSession session){
        String expression = session.getAttribute("expression").toString().replace(" ", "").trim();

        expression = replaceVariablesToValues(expression, session);

        if(expression != null){
            int out = EvaluateString.evaluate(expression.trim());
            return String.valueOf(out);
        }

        return null;
    }

    private static String replaceVariablesToValues(String expression, HttpSession session) {
        StringBuilder builder = new StringBuilder();
        char c;
        Object val;
        for (int i = 0; i < expression.length(); i++) {
            c = expression.charAt(i);
            if (c >= 'a' && c <= 'z') {
                val = session.getAttribute(String.valueOf(c));
                if(val == null){
                    session.setAttribute("invalidArgument", c);
                    return null;
                }

                if (val.toString().charAt(0) >= '0' && val.toString().charAt(0) <= '9'
                        || val.toString().charAt(0) <= '-') {
                    builder.append(Integer.parseInt(val.toString()));
                } else {
                    val = session.getAttribute(val.toString());
                    builder.append(Integer.parseInt((String) val));
                }
            } else {
                builder.append(c);
            }
        }

        return builder.toString();
    }
}

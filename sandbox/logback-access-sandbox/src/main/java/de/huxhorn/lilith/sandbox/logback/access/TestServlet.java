package de.huxhorn.lilith.sandbox.logback.access;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Writer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestServlet extends HttpServlet {
	private final Logger logger = LoggerFactory.getLogger(TestServlet.class);

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        process(req,  resp);
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        process(req,  resp);
    }

    private void process(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		if(logger.isInfoEnabled()) logger.info("A log message.");
        StringBuffer buf = new StringBuffer();
        buf.append("<html>\n");
        buf.append("    <body>\n");
        buf.append("        <h1>Hello world!</h1>\n");
        buf.append("    </body>\n");
        buf.append("</html>");

        Writer out = response.getWriter();
        out.write(buf.toString());
        out.flush();
    }
}

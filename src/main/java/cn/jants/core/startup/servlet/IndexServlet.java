package cn.jants.core.startup.servlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Ants框架默认首页
 *
 * @author MrShun
 * @version 1.0
 */
public class IndexServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html; charset=UTF-8");
        String html = "<title>Amts Java极速开发框架</title>" +
                "<body>" +
                "<div>" +
                "<img src=\"" + req.getContextPath() + "/ants-logo\" style=\"position:fixed;top:35%;left:0;bottom:65%;right:0;margin:auto;\"/>" +
                "</div>" +
                "<div style=\"font:normal 12px/24px Helvetica, Tahoma, Arial, sans-serif;position:fixed;left:0;bottom:10px;text-align:center;width:100%;\">&copy; 2017 Ants By 优旅家 MrShun</div>" +
                "</body>";
        PrintWriter writer = resp.getWriter();
        writer.print(html);
        writer.close();
    }

}

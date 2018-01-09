package com.ants.core.startup.servlet;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

/**
 * ants logo
 *
 * @author MrShun
 * @version 1.0
 * @date 2017-11-30
 */
public class LogoServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        InputStream in = this.getClass().getResourceAsStream("/assets/ants-logo.png");
        resp.setContentType("image/jpeg;charset=UTF-8");
        // 得到输出流
        OutputStream output = resp.getOutputStream();
        byte[] buffer = new byte[2048];
        int count;
        while ((count = in.read(buffer)) > 0) {
            output.write(buffer, 0, count);
        }
        in.close();
        output.close();
    }

}

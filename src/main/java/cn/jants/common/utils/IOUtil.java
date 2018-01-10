package cn.jants.common.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by liushun on 2016/8/10.
 */
public class IOUtil {

    /**
     * inputStream转outputStream
     *
     * @param in
     * @return
     * @throws Exception
     */
    public static ByteArrayOutputStream parse(InputStream in) throws Exception {
        ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
        int ch;
        while ((ch = in.read()) != -1) {
            swapStream.write(ch);
        }
        return swapStream;
    }

    /**
     * outputStream转inputStream
     *
     * @param out
     * @return
     * @throws Exception
     */
    public static ByteArrayInputStream parse(OutputStream out) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        baos = (ByteArrayOutputStream) out;
        ByteArrayInputStream swapStream = new ByteArrayInputStream(baos.toByteArray());
        return swapStream;
    }

    /**
     * inputStream转String
     *
     * @param in
     * @return
     * @throws Exception
     */
    public static String parseStr(InputStream in) throws Exception {
        ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
        int ch;
        while ((ch = in.read()) != -1) {
            swapStream.write(ch);
        }
        return swapStream.toString();
    }

    /**
     * OutputStream 转String
     *
     * @param out
     * @return
     * @throws Exception
     */
    public static String parseStr(OutputStream out) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        baos = (ByteArrayOutputStream) out;
        ByteArrayInputStream swapStream = new ByteArrayInputStream(baos.toByteArray());
        return swapStream.toString();
    }

    /**
     * String转inputStream
     *
     * @param in
     * @return
     * @throws Exception
     */
    public static ByteArrayInputStream parseInputStream(String in) throws Exception {
        ByteArrayInputStream input = new ByteArrayInputStream(in.getBytes());
        return input;
    }

    /**
     * String 转outputStream
     *
     * @param in
     * @return
     * @throws Exception
     */
    public static ByteArrayOutputStream parseOutputStream(String in) throws Exception {
        return parse(parseInputStream(in));
    }
}

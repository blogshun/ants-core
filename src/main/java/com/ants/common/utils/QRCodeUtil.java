package com.ants.common.utils;


import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Hashtable;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 二维码生成器 需要导入 jar
 * <dependency>
 * <groupId>com.google.zxing</groupId>
 * <artifactId>core</artifactId>
 * </dependency>
 * 生成web链接二维码
 * 生成本地二维码图片, 可以根据需求定制二维码
 *
 * @author MrShun
 * @version 1.0
 * @Date 2017/11/13
 */
public class QRCodeUtil {

    private static final Logger LOG = LoggerFactory.getLogger(QRCodeUtil.class);

    private static final String CHARSET = "utf-8";

    private static final String FORMAT_NAME = "JPG";
    /**
     * LOGO宽度
     */
    private static final int WIDTH = 60;
    /**
     * LOGO高度
     */
    private static final int HEIGHT = 60;

    /**
     * 二维码尺寸
     */
    private static AtomicInteger CODE_SIZE = new AtomicInteger(300);

    /**
     * 二维码图形的颜色
     */
    private static AtomicInteger CODE_COLOR = new AtomicInteger(0xFF000000);
    /**
     * 背景色
     */
    private static AtomicInteger BG_COLOR = new AtomicInteger(0xFFFFFFFF);

    /**
     * 设置属性基于线程安全
     */
    public static void setBitMatrix(Integer codeSize, Integer codeColor, Integer bgColor) {
        CODE_SIZE.set(codeSize);
        CODE_COLOR.set(codeColor);
        if (bgColor != null) {
            BG_COLOR.set(bgColor);
        }
    }

    public static void setBitMatrix(Integer codeSize, Integer codeColor) {
        setBitMatrix(codeSize, codeColor);
    }

    private static BufferedImage createImage(String content, String imgPath,
                                             boolean needCompress) throws Exception {
        Hashtable<EncodeHintType, Object> hints = new Hashtable<>();
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        hints.put(EncodeHintType.CHARACTER_SET, CHARSET);
        hints.put(EncodeHintType.MARGIN, 1);
        BitMatrix bitMatrix = new MultiFormatWriter().encode(content,
                BarcodeFormat.QR_CODE, CODE_SIZE.get(), CODE_SIZE.get(), hints);
        int width = bitMatrix.getWidth();
        int height = bitMatrix.getHeight();
        BufferedImage image = new BufferedImage(width, height,
                BufferedImage.TYPE_INT_RGB);
        System.out.println(String.format("width:%s, height:%s", width, height));
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int rgb = new Color(x * 10 / 12, y * 10 / 12, 1).getRGB();
                image.setRGB(x, y, bitMatrix.get(x, y) ? rgb : BG_COLOR.get());
            }
        }
        if (imgPath == null || "".equals(imgPath)) {
            return image;
        }
        // 插入图片
        insertImage(image, imgPath, needCompress);
        return image;
    }

    /**
     * 插入LOGO
     *
     * @param source       二维码图片
     * @param imgPath      LOGO图片地址
     * @param needCompress 是否压缩
     * @throws Exception
     */
    private static void insertImage(BufferedImage source, String imgPath,
                                    boolean needCompress) throws Exception {
        File file = new File(imgPath);
        if (!file.exists()) {
            System.err.println("" + imgPath + "   该文件不存在！");
            return;
        }
        Image src = ImageIO.read(new File(imgPath));
        int width = src.getWidth(null);
        int height = src.getHeight(null);
        // 压缩LOGO
        if (needCompress) {
            if (width > WIDTH) {
                width = WIDTH;
            }
            if (height > HEIGHT) {
                height = HEIGHT;
            }
            Image image = src.getScaledInstance(width, height,
                    Image.SCALE_SMOOTH);
            BufferedImage tag = new BufferedImage(width, height,
                    BufferedImage.TYPE_INT_RGB);
            Graphics g = tag.getGraphics();
            // 绘制缩小后的图
            g.drawImage(image, 0, 0, null);
            g.dispose();
            src = image;
        }
        // 插入LOGO
        Graphics2D graph = source.createGraphics();
        int x = (CODE_SIZE.get() - width) / 2;
        int y = (CODE_SIZE.get() - height) / 2;
        graph.drawImage(src, x, y, width, height, null);
        Shape shape = new RoundRectangle2D.Float(x, y, width, width, 6, 6);
        graph.setStroke(new BasicStroke(3f));
        graph.draw(shape);
        graph.dispose();
    }

    /**
     * 当文件夹不存在时，mkdirs会自动创建多层目录，区别于mkdir．(mkdir如果父目录不存在则会抛出异常)
     *
     * @param destPath 存放目录
     * @author lanyuan
     * Email: mmm333zzz520@163.com
     * @date 2013-12-11 上午10:16:36
     */
    private static void mkdirs(String destPath) {
        File file = new File(destPath);
        //当文件夹不存在时，mkdirs会自动创建多层目录，区别于mkdir．(mkdir如果父目录不存在则会抛出异常)
        if (!file.exists() && !file.isDirectory()) {
            file.mkdirs();
        }
    }

    /**
     * 生成二维码到本地(内嵌LOGO)
     *
     * @param content      内容
     * @param imgPath      LOGO地址
     * @param destFile     目标文件
     * @param needCompress 是否压缩LOGO
     */
    public static void writeToFile(String content, String imgPath, String destFile,
                                   boolean needCompress) {
        try {
            BufferedImage image = createImage(content, imgPath,
                    needCompress);
            int n = destFile.lastIndexOf(File.separator);
            String destPath = destFile.substring(0, (n == -1 ? destFile.lastIndexOf("/") : n));
            mkdirs(destPath);
            ImageIO.write(image, FORMAT_NAME, new File(destFile));
        } catch (Exception e) {
            e.printStackTrace();
            LOG.error("生成二维码到文件异常 " + e.getMessage());
        }
    }

    public static void writeToFile(String content, String destFile) {
        writeToFile(content, null, destFile, false);
    }

    /**
     * 图片流二维码
     *
     * @param content      内容
     * @param imgPath      logo图片地址
     * @param needCompress 是否压缩LOGO
     * @param out
     */
    public static void writeToOutputStream(String content, String imgPath, boolean needCompress, OutputStream out) {
        try {
            BufferedImage image = createImage(content, imgPath,
                    needCompress);
            ImageIO.write(image, FORMAT_NAME, out);
        } catch (IOException e) {
            LOG.error("Could not write an image of format " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void writeToOutputStream(String content, OutputStream out) {
        writeToOutputStream(content, null, false, out);
    }

    public static InputStream getInputStream(String content) {
        FileOutputStream fos = null;
        File file = null;
        try {
            //创建临时文件
            file = File.createTempFile("temp", ".code");
            LOG.info("临时文件所在的本地路径 > {}", file.getCanonicalPath());
            fos = new FileOutputStream(file);
            //这里处理业务逻辑
            writeToOutputStream(content, fos);
            return new FileInputStream(file.getCanonicalPath());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                //关闭临时文件
                fos.flush();
                fos.close();
                file.deleteOnExit();//程序退出时删除临时文件
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static void main(String[] args) {
        writeToFile("这临时文件所在的本地路临时文件所在的本地路临时文件所在的本地路是", "e:/aa.jpg", "e:/code/tes.png", true);
    }
}

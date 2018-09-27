package cn.jants.plugin.sqlmap;

import cn.jants.common.enums.StartMode;
import cn.jants.common.utils.PathUtil;
import cn.jants.core.context.AppConstant;
import cn.jants.core.ext.Plugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author MrShun
 * @version 1.0
 */
public class SqlMapPlugin implements Plugin {

    private static final Logger logger = LoggerFactory.getLogger(SqlMapPlugin.class);

    private String dirPath;

    private static DocumentBuilder documentBuilder;

    static {//调用jdk自己的xml解析，不想多加依赖。
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            documentBuilder = dbf.newDocumentBuilder();
        } catch (ParserConfigurationException ex) {
            throw new RuntimeException("I don`t know .", ex);
        }
    }

    /**
     * 通过构造赋值存放sql文件目录
     *
     * @param dirPath
     */
    public SqlMapPlugin(String dirPath) {
        this.dirPath = dirPath;
    }


    @Override
    public boolean start() throws IOException, SAXException {
        SqlParser.clear();
        logger.debug("初始化[XML SQL] 文件 ...");
        if (PathUtil.isJarMode()) {
            AppConstant.START_MODE = StartMode.JAR;
        } else {
            File dirFile = new File(PathUtil.getClassPath().concat(dirPath));
            if (dirFile.isDirectory()) {
                File[] files = dirFile.listFiles(new FileFilter() {
                    //过滤文件取xml文件
                    @Override
                    public boolean accept(File pathname) {
                        return pathname.getName().endsWith(".xml");
                    }
                });
                for (File file : files) {
                    //迭代解析sql xml
                    Document document = documentBuilder.parse(file);
                    SqlParser.parse(document);
                    logger.debug("读取 {} 成功 ...", file.getPath());
                }
                logger.debug("[XML SQL] 初始化完成 ...");
            }
        }
        return true;
    }

    /**
     * jar中读取流转document
     * @param in
     */
    public static void parse(InputStream in) {
        try {
            Document document = documentBuilder.parse(in);
            SqlParser.parse(document);
            in.close();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean destroy() {
        SqlParser.clear();
        return true;
    }
}

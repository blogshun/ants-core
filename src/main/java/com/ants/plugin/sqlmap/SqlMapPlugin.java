package com.ants.plugin.sqlmap;

import com.ants.common.enums.StartMode;
import com.ants.common.utils.PathUtil;
import com.ants.core.context.AppConstant;
import com.ants.core.ext.Plugin;
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
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * @author MrShun
 * @version 1.0
 * @date 2017-05-20
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
        SqlXmlParser.clear();
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
                    SqlXmlParser.parse(document);
                    logger.debug("读取 {} 成功 ...", file.getPath());
                }
                logger.debug("[XML SQL] 初始化完成 ...");
            }
        }
        return true;
    }

    @Override
    public boolean destroy() {
        SqlXmlParser.clear();
        return true;
    }
}

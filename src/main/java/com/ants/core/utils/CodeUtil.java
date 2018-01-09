package com.ants.core.utils;

import com.ants.plugin.orm.Table;
import com.ants.plugin.orm.TableBean;
import com.ants.plugin.orm.TableMapper;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;

import javax.lang.model.element.Modifier;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author MrShun
 * @version 1.0
 */
public class CodeUtil {

    private static final String letter = "abcdefghijklmnopqrstuvwxyz";

    private static final String num = "1234567890";

    private static List<String> alias = new ArrayList<>();

    /**
     * 生成Mapper静态对象
     *
     * @param sqlPath   sql文件
     * @param targetDir 目标目录
     */
    public static void genStaticMapper(String sqlPath, String targetDir) {

    }


    /**
     * 生成静态字段
     *
     * @param packages        扫描包目录
     * @param targetPkg       文件生成目录 "com.ants.auth.generate"
     * @param targetDirectory 目标生成java目录
     */
    public static void genStaticFiled(String[] packages, String targetPkg, String targetDirectory) {
        List<Class<?>> scanClass = ScanUtil.findScanClass(packages, Table.class);
        for (Class<?> scanCls : scanClass) {
            TableBean tableBean = TableMapper.findTableBean(scanCls);
            System.out.println(tableBean.getTable());
            javaStaticFiledPoet(tableBean, scanCls, targetPkg, targetDirectory);
        }
    }

    private static void javaStaticFiledPoet(TableBean tableBean, Class cls, String targetPkg, String targetDirectory) {
        String className = "Q".concat(cls.getSimpleName());
        String tableName = tableBean.getTable();
        //生成类型
        TypeSpec.Builder builder = TypeSpec.classBuilder(className)
                .addModifiers(Modifier.PUBLIC);
        String aliasStr = recursion();
        FieldSpec tableFieldSpec = FieldSpec.builder(String.class, "TABLE", Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                .initializer("$S", tableName.concat(" ").concat(aliasStr))
                .build();
        builder.addField(tableFieldSpec);
        for (String filed : tableBean.getFields()) {

            FieldSpec fieldSpec = FieldSpec.builder(String.class, filed.toUpperCase(), Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                    .initializer("$S", filed)
                    .build();

            builder.addField(fieldSpec);

            FieldSpec defaultFieldSpec = FieldSpec.builder(String.class, "_".concat(filed.toUpperCase()), Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                    .initializer("$S", aliasStr.concat(".").concat(filed))
                    .build();

            builder.addField(defaultFieldSpec);
        }
        TypeSpec hellworld = builder.build();
        //构建Java源文件
        JavaFile javaFile = JavaFile.builder(targetPkg, hellworld).build();

        //5. 输出java源文件到文件系统
        try {
            System.out.println(javaFile);
            File dir = new File(targetDirectory);
            System.out.println(dir.getPath());
            if (!dir.exists()) {
                dir.mkdirs();
            }
            //JavaFile.write(), 参数为源码生成目录(源码的classpath目录)
            javaFile.writeTo(dir);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String recursion() {
        String aliasStr = randomStr();
        if (alias.contains(aliasStr)) {
            recursion();
        } else {
            alias.add(aliasStr);
        }
        return aliasStr;
    }

    private static String randomStr() {
        Random random = new Random();
        int i = random.nextInt(25);
        int j = random.nextInt(9);
        return "" + letter.toCharArray()[i] + num.toCharArray()[j];
    }
}

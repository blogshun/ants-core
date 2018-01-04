package com.ants.core.utils;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * 文件夹操作工具栏
 *
 * @author MrShun
 * @version 1.0
 * Date 2017-04-26
 */
public class FileUtil {

    /**
     * 读取文件可以带缓冲区读取大文件，默认不带缓冲
     *
     * @param fileName 文件名称包含路径
     * @param isBuff   是否缓冲 true:缓冲 false:不缓冲
     * @return
     */
    public static String read(String fileName, boolean isBuff) {
        StringBuffer data = new StringBuffer();
        BufferedReader in = null;
        try {
            if (isBuff) {
                in = new BufferedReader(new FileReader(fileName), 5 * 1024 * 1024);
            } else {
                {
                    in = new BufferedReader(new FileReader(fileName));
                }
            }
            String line = "";
            while ((line = in.readLine()) != null) {
                data.append(line);
            }
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data.toString();
    }

    public static String read(String fileName) {
        return read(fileName, false);
    }

    public static String read(InputStream inputStream, String encoding) {
        try {
            if (inputStream == null) {
                return null;
            }
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, encoding));
            String line;
            StringBuffer sb = new StringBuffer();
            while ((line = br.readLine()) != null) {
                sb.append(line + "\n");
            }
            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 文件移动 默认覆盖文件
     *
     * @param oldFile 旧文件地址
     * @param newFile 新文件地址
     * @param isCover 是否覆盖 true:是 false:否
     * @return
     */
    public static boolean move(String oldFile, String newFile, boolean isCover) {
        File file = new File(oldFile);
        if (file.exists()) {
            File neile = new File(newFile);
            if (isCover) {
                file.renameTo(neile);
            } else {
                if (neile.exists()) {
                    System.out.println(newFile + " > 文件已经存在!");
                } else {
                    file.renameTo(neile);
                }
            }
            return true;
        }
        return false;
    }

    public static boolean move(String oldFile, String newFile) {
        return move(oldFile, newFile, true);
    }

    /**
     * 下载文件到本地
     *
     * @param urlString 被下载的文件地址
     * @param filename  本地文件名
     */
    public static boolean download(String urlString, String filename) {
        try {
            // 构造URL
            URL url = new URL(urlString);
            // 打开连接
            URLConnection con = url.openConnection();
            // 输入流
            InputStream is = con.getInputStream();
            // 1K的数据缓冲
            byte[] bs = new byte[1024];
            // 读取到的数据长度
            int len;
            File file = new File(filename.substring(0, filename.lastIndexOf("/")));
            if (!file.exists()) {
                file.mkdirs();
            }
            // 输出的文件流
            OutputStream os = new FileOutputStream(filename);
            // 开始读取
            while ((len = is.read(bs)) != -1) {
                os.write(bs, 0, len);
            }
            // 完毕，关闭所有链接
            os.close();
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static void download(String path, HttpServletResponse response) {
        download(path, null, response);
    }

    public static void download(String path, String newName, HttpServletResponse response) {
        try {
            // path是指欲下载的文件的路径。
            File file = new File(path);
            // 取得文件名。
            String filename = file.getName();
            // 取得文件的后缀名。
            String ext = filename.substring(filename.lastIndexOf(".") + 1);

            // 以流的形式下载文件。
            InputStream fis = new BufferedInputStream(new FileInputStream(path));
            byte[] buffer = new byte[fis.available()];
            fis.read(buffer);
            fis.close();
            // 清空response
            response.reset();
            // 设置response的Header
            response.addHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(newName == null ? filename : newName, response.getCharacterEncoding()));
            response.addHeader("Content-Length", "" + file.length());

            OutputStream toClient = new BufferedOutputStream(response.getOutputStream());
            response.setContentType("application/octet-stream");
            toClient.write(buffer);
            toClient.flush();
            toClient.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 文件复制
     *
     * @param currentFile 当前文件
     * @param targetFile  目标文件
     * @return
     */
    public static boolean copyFile(String currentFile, String targetFile) {
        FileInputStream in = null;
        try {
            in = new FileInputStream(currentFile);
            FileOutputStream out = new FileOutputStream(create(targetFile));
            int c;
            byte[] buffer = new byte[1024];
            while ((c = in.read(buffer)) != -1) {
                for (int i = 0; i < c; i++) {
                    out.write(buffer[i]);
                }
            }
            in.close();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static File create(String filePath) {
        File file = new File(filePath);
        if (file.isDirectory()) {
            if (!file.exists()) {
                file.mkdirs();
            }
        } else {
            if (!file.exists()) {
                String path = file.getPath();
                path = path.substring(0, path.lastIndexOf("\\"));
                new File(path).mkdirs();
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return file;
    }

    public static void copyFolder(String currentFolder, String targetFolder) {
        List<File> list = new ArrayList<>();
        iterator(currentFolder, true, "", list);
        for (File f : list) {
            String currentFile = f.getPath();
            String targetFile = targetFolder + currentFile.replace(currentFolder, "");
            System.out.println(currentFile + "|" + targetFile);
            copyFile(currentFile, targetFile);
        }
    }

    /**
     * 写入文件
     *
     * @param fileName 文件名称
     * @param content  内容
     * @param isAppend 是否追加 true:追加 false:不追加
     * @return
     */
    public static boolean write(String fileName, String content, boolean isAppend) {
        BufferedWriter out = null;
        try {
            //追加的方法
            out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileName, isAppend)));
            out.write(content);
            out.write("\r\n");
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    /**
     * 基于流写文件
     *
     * @param inputStream 流
     * @param filePath    文件存储路径
     * @return
     */
    public static boolean write(InputStream inputStream, String filePath) {
        boolean check = false;
        try {
            OutputStream out = new FileOutputStream(filePath);
            byte[] buffer = new byte[1024];
            int length = -1;
            while ((length = inputStream.read(buffer)) != -1) {
                out.write(buffer, 0, length);
            }
            inputStream.close();
            out.close();
            check = true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return check;
    }

    public static boolean write(String fileName, String content) {
        return write(fileName, content, false);
    }

    /**
     * 文件重命名 必须在相同路径下面
     *
     * @param path    文件路径
     * @param oldName 旧文件名称
     * @param newName 新文件名称
     * @return
     */
    public static boolean rename(String path, String oldName, String newName) {
        File file = new File(path + File.separator + oldName);
        if (file.exists()) {
            file.renameTo(new File(path + File.separator + newName));
            return true;
        }
        return false;
    }


    /**
     * 根据类型获取下面文件
     *
     * @param filePath 文件夹路径
     * @param isLoop   是否循环遍历
     * @param type     文件类型
     * @param fileList 接收集合
     */
    public static void iterator(String filePath, Boolean isLoop, String type, List<File> fileList) {
        File dir = new File(filePath);
        // 该文件目录下文件全部放入数组
        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    if (isLoop) {
                        iterator(file.getAbsolutePath(), isLoop, type, fileList);
                    }
                } else {
                    if (file.getAbsolutePath().indexOf(type) != -1) {
                        fileList.add(file);
                    }
                }
            }

        }
    }


    /**
     * 删除文件或文件夹下面所有文件
     *
     * @param filePath 文件夹路径
     */
    public static void delete(String filePath) {
        File file = new File(filePath);
        if (file != null && file.exists()) {
            if (file.isFile()) {
                file.delete();
            } else if (file.isDirectory()) {
                File[] files = file.listFiles();
                for (int i = 0; i < files.length; i++) {
                    delete(files[i].getPath());
                }
            }
            file.delete();
        }
    }


    /**
     * 文件夹不存在则创建
     *
     * @param fileDir
     */
    public static void dirExists(String fileDir) {
        File file = new File(fileDir);
        if (!file.exists()) {
            file.mkdirs();
        }
    }
}

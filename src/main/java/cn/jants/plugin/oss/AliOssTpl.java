package cn.jants.plugin.oss;

import cn.jants.common.bean.Log;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.ObjectMetadata;
import com.aliyun.oss.model.PutObjectResult;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author MrShun
 * @version 1.0
 */
public class AliOssTpl {

    private OSSClient client;

    private String bucketName;

    private String url;

    public AliOssTpl(OSSClient client, String url, String bucketName) {
        this.client = client;
        this.bucketName = bucketName;
        this.url = url;
    }

    public OSSClient getClient() {
        return client;
    }

    /**
     * 上传本地文件到OSS
     *
     * @param file     文件对象
     * @param diskName oss文件夹目录
     * @return
     */
    public OssResult uploadFile2OSS(File file, String diskName) {
        return uploadFile2OSS(file, null, diskName);
    }


    /**
     * 本地文件重命名上传到OSS
     *
     * @param file     本地文件
     * @param rename   重命名文件
     * @param diskName oss文件夹目录
     * @return
     */
    public OssResult uploadFile2OSS(File file, String rename, String diskName) {
        try {
            Long fileSize = file.length();
            String fileName = file.getName();
            InputStream is = new FileInputStream(file);
            //创建上传Object的Metadata
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(is.available());
            metadata.setCacheControl("no-cache");
            metadata.setHeader("Pragma", "no-cache");
            metadata.setContentEncoding("utf-8");
            metadata.setContentType(getContentType(fileName));
            metadata.setContentDisposition("filename/filesize=" + fileName + "/" + fileSize + "Byte.");
            //上传文件
            String urlStr = url.concat("/").concat(diskName).concat(rename);
            PutObjectResult putResult = client.putObject(bucketName, diskName + (rename == null ? fileName : rename), is, metadata);
            //解析结果
            return new OssResult(true, "上传成功 > ".concat(file.getName().concat(" !")), urlStr, putResult.getETag());
        } catch (Exception e) {
            Log.error("上传阿里云OSS服务器异常." + e.getMessage(), e);
        }
        return new OssResult(false, "上传失败 > ".concat(file.getName().concat(" !")), null, null);
    }

    /**
     * 网络URL上传传到oss
     *
     * @param url      图片地址
     * @param fileName 文件名称
     * @param diskName oss上面的目录
     * @return
     */
    public OssResult uploadUrl2OSS(String url, String fileName, String diskName) {
        try {
            URL reqUrl = new URL(url);
            HttpURLConnection httpURLConnection = (HttpURLConnection) reqUrl.openConnection();
            // 设置网络连接超时时间
            httpURLConnection.setConnectTimeout(3000);
            // 设置应用程序要从网络连接读取数据
            httpURLConnection.setDoInput(true);
            httpURLConnection.setRequestMethod("GET");
            int responseCode = httpURLConnection.getResponseCode();
            if (responseCode == 200) {
                // 从服务器返回一个输入流
                InputStream inputStream = httpURLConnection.getInputStream();
                try {
                    int fileSize = httpURLConnection.getContentLength();
                    //创建上传Object的Metadata
                    ObjectMetadata metadata = new ObjectMetadata();
                    metadata.setContentLength(fileSize);
                    metadata.setCacheControl("no-cache");
                    metadata.setHeader("Pragma", "no-cache");
                    metadata.setContentEncoding("utf-8");
                    metadata.setContentType(getContentType(fileName));
                    metadata.setContentDisposition("filename/filesize=" + fileName + "/" + fileSize + "Byte.");
                    //上传文件
                    PutObjectResult putResult = client.putObject(bucketName, diskName + fileName, inputStream, metadata);
                    //解析结果
                    String urlStr = url.concat("/").concat(diskName).concat(fileName);
                    return new OssResult(true, "上传成功 > ".concat(fileName.concat(" !")), urlStr, putResult.getETag());
                } catch (Exception e) {
                    Log.error("上传阿里云OSS服务器异常." + e.getMessage(), e);
                }
                return new OssResult(false, "上传失败 > ".concat(fileName.concat(" !")), null, null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 网络上面图片流上传到OSS
     *
     * @param is       文件流
     * @param fileSize 文件大小
     * @param fileName 文件名名称
     * @param diskName 文件目录
     * @return
     */
    public OssResult uploadStream2OSS(InputStream is, Long fileSize, String fileName, String diskName) {
        try {
            //创建上传Object的Metadata
            if (fileSize == null) {
                fileSize = Long.valueOf(is.available() + "");
            }
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(fileSize);
            metadata.setCacheControl("no-cache");
            metadata.setHeader("Pragma", "no-cache");
            metadata.setContentEncoding("utf-8");
            metadata.setContentType(getContentType(fileName));
            metadata.setContentDisposition("filename/filesize=" + fileName + "/" + fileSize + "Byte.");
            //上传文件
            PutObjectResult putResult = client.putObject(bucketName, diskName + fileName, is, metadata);
            //解析结果
            String urlStr = url.concat("/").concat(diskName).concat(fileName);
            return new OssResult(true, "上传成功 > ".concat(fileName.concat(" !")), urlStr, putResult.getETag());
        } catch (Exception e) {
            Log.error("上传阿里云OSS服务器异常." + e.getMessage(), e);
        }
        return new OssResult(false, "上传失败 > ".concat(fileName.concat(" !")), null, null);
    }

    /**
     * 本地文件流上传到oss
     *
     * @param is
     * @param fileName
     * @param diskName
     * @return
     */
    public OssResult uploadStream2OSS(InputStream is, String fileName, String diskName) {
        return uploadStream2OSS(is, null, fileName, diskName);
    }

    /**
     * 删除文件
     *
     * @param objectName 文件对象
     * @return
     */
    public OssResult delete(String objectName) {
        boolean exist = client.doesObjectExist(bucketName, objectName);
        if (exist) {
            client.deleteObject(this.bucketName == null ? bucketName : this.bucketName, objectName);
            return new OssResult(true, "文件删除成功 > ".concat(objectName));
        } else {
            return new OssResult(false, "文件不存在, 删除失败!");
        }
    }

    /**
     * 通过文件名判断并获取OSS服务文件上传时文件的contentType
     *
     * @param fileName 文件名
     * @return 文件的contentType
     */
    public String getContentType(String fileName) {
        Path path = Paths.get(fileName);
        String contentType = null;
        try {
            contentType = Files.probeContentType(path);
        } catch (IOException e) {
            e.printStackTrace();
            Log.debug("File content type is : " + contentType);
        }
        return contentType;
    }
}

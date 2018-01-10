package cn.jants.plugin.oss;

import cn.jants.common.bean.Log;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.Bucket;
import com.aliyun.oss.model.OSSObject;
import com.aliyun.oss.model.ObjectMetadata;
import com.aliyun.oss.model.PutObjectResult;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
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
     * 新建Bucket  --Bucket权限:私有
     *
     * @param bucketName bucket名称
     * @return true 新建Bucket成功
     */
    public boolean createBucket(String bucketName) {
        Bucket bucket = client.createBucket(bucketName);
        return bucketName.equals(bucket.getName());
    }

    /**
     * 删除Bucket
     *
     * @param bucketName bucket名称
     */
    public void deleteBucket(String bucketName) {
        client.deleteBucket(bucketName);
        Log.info("删除" + bucketName + "Bucket成功");
    }

    /**
     * 本地文件上传到OSS
     *
     * @param file       本地文件
     * @param rename     重命名文件
     * @param bucketName
     * @param diskName   oss文件夹目录
     * @return
     */
    public OssResult uploadFile2OSS(File file, String rename, String bucketName, String diskName) {
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
            PutObjectResult putResult = client.putObject(this.bucketName == null ? bucketName : this.bucketName, diskName + (rename == null ? fileName : rename), is, metadata);
            //解析结果
            return new OssResult(true, "上传成功 > ".concat(file.getName().concat(" !")), urlStr, putResult.getETag());
        } catch (Exception e) {
            Log.error("上传阿里云OSS服务器异常." + e.getMessage(), e);
        }
        return new OssResult(false, "上传失败 > ".concat(file.getName().concat(" !")), null, null);
    }

    public OssResult uploadFile2OSS(File file, String bucketName, String diskName) {
        return uploadFile2OSS(file, null, bucketName, diskName);
    }

    public OssResult uploadFile2OSS(File file, String diskName) {
        return uploadFile2OSS(file, null, bucketName, diskName);
    }

    /**
     * 流上传到OSS
     *
     * @param is         文件流
     * @param fileName   文件名称
     * @param bucketName
     * @param diskName   文件目录
     * @return
     */
    public OssResult uploadStream2OSS(InputStream is, String fileName, String bucketName, String diskName) {
        try {
            Integer fileSize = is.available();
            //创建上传Object的Metadata
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(is.available());
            metadata.setCacheControl("no-cache");
            metadata.setHeader("Pragma", "no-cache");
            metadata.setContentEncoding("utf-8");
            metadata.setContentType(getContentType(fileName));
            metadata.setContentDisposition("filename/filesize=" + fileName + "/" + fileSize + "Byte.");
            //上传文件
            PutObjectResult putResult = client.putObject(this.bucketName == null ? bucketName : this.bucketName, diskName + fileName, is, metadata);
            //解析结果
            String urlStr = url.concat("/").concat(diskName).concat(fileName);
            return new OssResult(true, "上传成功 > ".concat(fileName.concat(" !")), urlStr, putResult.getETag());
        } catch (Exception e) {
            Log.error("上传阿里云OSS服务器异常." + e.getMessage(), e);
        }
        return new OssResult(false, "上传失败 > ".concat(fileName.concat(" !")), null, null);
    }

    public OssResult uploadStream2OSS(InputStream is, String fileName, String diskName) {
        return uploadStream2OSS(is, fileName, null, diskName);
    }

    /**
     * 根据key获取OSS服务器上的文件输入流
     *
     * @param bucketName bucket名称
     * @param diskName   文件路径
     * @param key        Bucket下的文件的路径名+文件名
     */
    public InputStream getOSS2InputStream(String bucketName, String diskName, String key) {
        OSSObject ossObj = client.getObject(this.bucketName == null ? bucketName : this.bucketName, diskName + key);
        return ossObj.getObjectContent();
    }

    public InputStream getOSS2InputStream(String diskName, String key) {
        return getOSS2InputStream(null, diskName, key);
    }

    /**
     * 删除文件
     *
     * @param bucketName
     * @param objectName 文件对象
     * @return
     */
    public OssResult delete(String bucketName, String objectName) {
        boolean exist = client.doesObjectExist(this.bucketName == null ? bucketName : this.bucketName, objectName);
        if (exist) {
            client.deleteObject(this.bucketName == null ? bucketName : this.bucketName, objectName);
            return new OssResult(true, "文件删除成功 > ".concat(objectName));
        } else {
            return new OssResult(false, "文件不存在, 删除失败!");
        }
    }

    public OssResult delete(String objectName) {
        return delete(null, objectName);
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

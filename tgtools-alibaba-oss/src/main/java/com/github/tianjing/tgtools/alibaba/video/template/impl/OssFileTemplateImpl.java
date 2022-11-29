package com.github.tianjing.tgtools.alibaba.video.template.impl;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.OSSObject;
import com.aliyun.oss.model.OSSObjectSummary;
import com.aliyun.oss.model.ObjectListing;
import com.github.tianjing.tgtools.alibaba.video.exception.TgtoolsOssException;
import com.github.tianjing.tgtools.alibaba.video.template.OssFileTemplate;

import java.io.*;
import java.util.List;
import java.util.UUID;

public class OssFileTemplateImpl implements OssFileTemplate {

    protected OSS ossClient;
    protected String bucketName;

    public OSS getOssClient() {
        return ossClient;
    }

    public void setOssClient(OSS ossClient) {
        this.ossClient = ossClient;
    }

    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    @Override
    public InputStream downloadStream(String pName) throws TgtoolsOssException {
        File vFile = downloadFile(pName);
        try {
            return new FileInputStream(vFile);
        } catch (FileNotFoundException e) {
            throw new TgtoolsOssException("downloadStream 出错，下载的临时文件不存在。 异常：" + e.toString(), e);
        }
    }

    @Override
    public byte[] downloadByteArray(String pName) throws TgtoolsOssException {
        File vFile = downloadFile(pName);
        return readFileToByte(vFile);
    }

    @Override
    public File downloadFile(String pName) throws TgtoolsOssException {
        OSSObject vOSSObject = null;

        try {
            String vTempName = UUID.randomUUID().toString().toUpperCase();
            vOSSObject = ossClient.getObject(bucketName, pName);
            File vFile = File.createTempFile(vTempName, null);
            writeFile(vFile, vOSSObject.getObjectContent());
            return vFile;
        } catch (OSSException oe) {
            throw new TgtoolsOssException("downloadStream 出错，oss 异常：" + oe.toString(), oe);
        } catch (Throwable ce) {
            throw new TgtoolsOssException("downloadStream 出错，异常：" + ce.toString(), ce);
        } finally {
            if (null != vOSSObject) {
                try {
                    vOSSObject.close();
                } catch (IOException e) {
                }
            }
        }
    }

    @Override
    public void uploadStream(String pName, InputStream pInputStream) throws TgtoolsOssException {
        try {
            // 创建PutObject请求。
            ossClient.putObject(bucketName, pName, pInputStream);
        } catch (OSSException oe) {
            throw new TgtoolsOssException("uploadStream 出错，oss 异常：" + oe.toString(), oe);
        } catch (ClientException ce) {
            throw new TgtoolsOssException("uploadStream 出错，client 异常：" + ce.toString(), ce);
        } catch (Throwable ce) {
            throw new TgtoolsOssException("uploadStream 出错，异常：" + ce.toString(), ce);
        }

    }

    @Override
    public void uploadByteArray(String pName, byte[] pData) throws TgtoolsOssException {
        uploadStream(pName, new ByteArrayInputStream(pData));
    }

    @Override
    public void uploadByteFile(String pName, File pFile) throws TgtoolsOssException {
        try {
            uploadStream(pName, new FileInputStream(pFile));
        } catch (FileNotFoundException e) {
            throw new TgtoolsOssException("uploadByteFile 出错，文件不存在。 异常：" + e.toString(), e);
        }
    }

    @Override
    public void delete(String pName) throws TgtoolsOssException {
        try {
            // 删除文件或目录。如果要删除目录，目录必须为空。
            ossClient.deleteObject(bucketName, pName);
        } catch (OSSException oe) {
            throw new TgtoolsOssException("delete 出错，oss 异常：" + oe.toString(), oe);
        } catch (ClientException ce) {
            throw new TgtoolsOssException("delete 出错，client 异常：" + ce.toString(), ce);
        } catch (Throwable ce) {
            throw new TgtoolsOssException("delete 出错，异常：" + ce.toString(), ce);
        }
    }

    @Override
    public void rename(String pSourceName, String pTargetName) throws TgtoolsOssException {
        try {
            ossClient.copyObject(bucketName, pSourceName, bucketName, pTargetName);
            ossClient.deleteObject(bucketName, pSourceName);
        } catch (OSSException oe) {
            throw new TgtoolsOssException("rename 出错，oss 异常：" + oe.toString(), oe);
        } catch (ClientException ce) {
            throw new TgtoolsOssException("rename 出错，client 异常：" + ce.toString(), ce);
        } catch (Throwable ce) {
            throw new TgtoolsOssException("rename 出错，异常：" + ce.toString(), ce);
        }
    }

    @Override
    public boolean exists(String pName) throws TgtoolsOssException {
        try {
            return ossClient.doesObjectExist(bucketName, pName);
        } catch (OSSException oe) {
            throw new TgtoolsOssException("rename 出错，oss 异常：" + oe.toString(), oe);
        } catch (ClientException ce) {
            throw new TgtoolsOssException("rename 出错，client 异常：" + ce.toString(), ce);
        } catch (Throwable ce) {
            throw new TgtoolsOssException("rename 出错，异常：" + ce.toString(), ce);
        }
    }

    @Override
    public List<OSSObjectSummary> list(String pKeyPrefix) throws TgtoolsOssException {
        try {
            // 列举文件。如果不设置keyPrefix，则列举存储空间下的所有文件。如果设置keyPrefix，则列举包含指定前缀的文件。
            ObjectListing objectListing = ossClient.listObjects(bucketName, pKeyPrefix);
            return objectListing.getObjectSummaries();
        } catch (OSSException oe) {
            throw new TgtoolsOssException("list 出错，oss 异常：" + oe.toString(), oe);
        } catch (ClientException ce) {
            throw new TgtoolsOssException("list 出错，client 异常：" + ce.toString(), ce);
        } catch (Throwable ce) {
            throw new TgtoolsOssException("list 出错，异常：" + ce.toString(), ce);
        }
    }


    /**
     * 将流写入到文件 (写入完成后关闭InputStream)
     *
     * @param pFile 文件
     * @param pData 内容
     * @throws TgtoolsOssException
     */
    private static void writeFile(File pFile, InputStream pData) throws TgtoolsOssException {
        FileOutputStream fop = null;
        try {
            File file = pFile;
            fop = new FileOutputStream(file);
            byte[] data = new byte[10 * 1024];
            int length = 0;
            while ((length = pData.read(data)) > 0) {
                fop.write(data, 0, length);
            }
        } catch (Exception e) {
            throw new TgtoolsOssException("文件写入失败:" + pFile.toString(), e);
        } finally {
            try {
                fop.close();
            } catch (IOException e) {
            }
            try {
                pData.close();
            } catch (IOException e) {
            }
        }
    }

    /**
     * 读文件
     *
     * @param pFile 文件
     * @throws TgtoolsOssException
     */
    public static byte[] readFileToByte(File pFile) throws TgtoolsOssException {
        File f = pFile;
        if (!f.exists()) {
            throw new TgtoolsOssException("文件未找到：" + pFile.toString());
        }

        ByteArrayOutputStream bos = new ByteArrayOutputStream((int) f.length());
        BufferedInputStream in = null;
        try {
            in = new BufferedInputStream(new FileInputStream(f));
            int bufSize = 1024;
            byte[] buffer = new byte[bufSize];
            int len = 0;
            while (-1 != (len = in.read(buffer, 0, bufSize))) {
                bos.write(buffer, 0, len);
            }
            return bos.toByteArray();
        } catch (Exception e) {
            throw new TgtoolsOssException("文件读取失败:" + pFile.toString(), e);
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                bos.close();
            } catch (Exception ee) {
            }
        }

    }
}

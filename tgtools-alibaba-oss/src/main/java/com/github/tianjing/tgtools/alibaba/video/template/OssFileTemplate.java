package com.github.tianjing.tgtools.alibaba.video.template;

import com.aliyun.oss.model.OSSObjectSummary;
import com.github.tianjing.tgtools.alibaba.video.exception.TgtoolsOssException;

import java.io.File;
import java.io.InputStream;
import java.util.List;

public interface OssFileTemplate {

    InputStream downloadStream(String pName) throws TgtoolsOssException;

    byte[] downloadByteArray(String pName) throws TgtoolsOssException;

    File downloadFile(String pName) throws TgtoolsOssException;


    void uploadStream(String pName, InputStream pInputStream) throws TgtoolsOssException;

    void uploadByteArray(String pName, byte[] pData) throws TgtoolsOssException;

    void uploadByteFile(String pName, File pFile) throws TgtoolsOssException;


    void delete(String pName) throws TgtoolsOssException;


    void rename(String pSourceName,String pTargetName) throws TgtoolsOssException;

    boolean exists(String pName) throws TgtoolsOssException;

    List<OSSObjectSummary> list(String pKeyPrefix) throws TgtoolsOssException;
}

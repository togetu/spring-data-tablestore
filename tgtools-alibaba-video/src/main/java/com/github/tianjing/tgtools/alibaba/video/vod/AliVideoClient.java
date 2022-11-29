package com.github.tianjing.tgtools.alibaba.video.vod;

import com.aliyun.vod.upload.impl.UploadVideoImpl;
import com.aliyun.vod.upload.req.UploadFileStreamRequest;
import com.aliyun.vod.upload.req.UploadStreamRequest;
import com.aliyun.vod.upload.resp.UploadFileStreamResponse;
import com.aliyun.vod.upload.resp.UploadStreamResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.vod.model.v20170321.*;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.tianjing.tgtools.alibaba.video.bean.PageInfo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import tgtools.exceptions.APPErrorException;

import java.io.InputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class AliVideoClient {
    private static Logger log = LoggerFactory.getLogger(AliVideoClient.class);

    AliVideoConfig config;

    DefaultAcsClient vodClient;

    public void init(AliVideoConfig pConfig) {
        config = pConfig;
        DefaultProfile profile = DefaultProfile.getProfile(config.getRegionId(), config.getAccessKey(), config.getAccessSecret());
        vodClient = new DefaultAcsClient(profile);
    }

    public SearchMediaResponse.Media.Video getByVideoId(String pVideoId) throws APPErrorException {
        SearchMediaRequest mediaRequest = new SearchMediaRequest();
        mediaRequest.setFields(StringUtils.join(Arrays.asList("Title",//标题
                "CoverURL",//封面
                "Status",//状态
                "CateName",//分类
                "CreationTime",//创建时间
                "Duration",//持续时间-视频时长
                "Size",//文件大小
                "Tags"//标签
        ), ","));
        mediaRequest.setSearchType("video");

        String match = "VideoId = '" + pVideoId + "'";

        mediaRequest.setMatch(match);
        try {

            SearchMediaResponse response = vodClient.getAcsResponse(mediaRequest);
            int size = response.getMediaList().size();
            List<SearchMediaResponse.Media.Video> videoList = response.getMediaList().stream().map(o -> o.getVideo()).collect(Collectors.toList());
            if (videoList.size() > 0) {
                return videoList.get(0);
            }
            return null;
        } catch (Exception e) {
            log.error("vod listPage 错误！" + e.getMessage(), e);
            throw new APPErrorException("vod listPage 错误！" + e.getMessage(), e);
        }

    }

    public List<SearchMediaResponse.Media.Video> listByVideoIds(List<String> pVideoIds) throws APPErrorException {
        if (CollectionUtils.isEmpty(pVideoIds)) {
            return new ArrayList<>();
        }
        SearchMediaRequest mediaRequest = new SearchMediaRequest();
        mediaRequest.setFields(StringUtils.join(Arrays.asList("Title",//标题
                "CoverURL",//封面
                "Status",//状态
                "CateName",//分类
                "CreationTime",//创建时间
                "Duration",//持续时间-视频时长
                "Size",//文件大小
                "Tags"//标签
        ), ","));
        mediaRequest.setSearchType("video");

        String match = " VideoId in ( ";
        for (int i = 0; i < pVideoIds.size(); i++) {
            if (StringUtils.isEmpty(pVideoIds.get(i))) {
                continue;
            }
            match += "'" + pVideoIds.get(i) + "'";
            if (i < pVideoIds.size() - 1) {
                match += " , ";
            }
        }
        match += " ) ";
        mediaRequest.setMatch(match);
        try {

            SearchMediaResponse response = vodClient.getAcsResponse(mediaRequest);
            int size = response.getMediaList().size();
            return response.getMediaList().stream().map(o -> o.getVideo()).collect(Collectors.toList());

        } catch (Exception e) {
            log.error("vod listPage 错误！" + e.getMessage(), e);
            throw new APPErrorException("vod listPage 错误！" + e.getMessage(), e);
        }

    }


    public PageInfo<SearchMediaResponse.Media.Video> listPage(String pTitle, String pTags, List<Long> pCateIdList, int pPageIndex, int pPageSize) throws APPErrorException {

        SearchMediaRequest mediaRequest = new SearchMediaRequest();
        mediaRequest.setFields(StringUtils.join(Arrays.asList("Title",//标题
                "CoverURL",//封面
                "Status",//状态
                "CateName",//分类
                "CreationTime",//创建时间
                "Duration",//持续时间-视频时长
                "Size",//文件大小
                "Tags",//标签
                "PlayInfoList"
        ), ","));
        mediaRequest.setPageNo((int) pPageIndex);
        mediaRequest.setPageSize((int) pPageSize);
        mediaRequest.setSearchType("video");
        mediaRequest.setSortBy("CreateTime:Desc");

        String match = "";
        if (StringUtils.isNotEmpty(pTitle)) {
            match += "Title = '" + pTitle + "'  and Status in ('Normal') ";
        }


        if (pTags != null) {
            if (!"".equals(match)) {
                match += " and ";
            }
            match += " Tags in (" + pTags + ")";
        }

        if (pCateIdList != null) {
            if (!CollectionUtils.isEmpty(pCateIdList) && StringUtils.isNotEmpty(match)) {
                match += " and ";
            }
            match += getCateSearchFilter(pCateIdList);
        }

        if (StringUtils.isNotEmpty(match)) {
            mediaRequest.setMatch(match);
        }
        try {
            PageInfo vPage = new PageInfo();
            SearchMediaResponse response = vodClient.getAcsResponse(mediaRequest);
            List<SearchMediaResponse.Media.Video> videoList = response.getMediaList().stream().map(o -> o.getVideo()).collect(Collectors.toList());
            vPage.setCurrent(pPageIndex);
            vPage.setSize(pPageSize);
            vPage.setRecords(videoList);
            vPage.setTotal(response.getTotal());
            return vPage;
        } catch (Exception e) {
            log.error("vod listPage 错误！" + e.getMessage(), e);
            throw new APPErrorException("vod listPage 错误！" + e.getMessage(), e);
        }
    }

    /**
     * 获取源文件信息
     */
    public GetMezzanineInfoResponse.Mezzanine getMezzanineInfo(String pVideoId) throws APPErrorException {
        GetMezzanineInfoRequest infoRequest = new GetMezzanineInfoRequest();
        infoRequest.setVideoId(pVideoId);
        try {
            GetMezzanineInfoResponse response = vodClient.getAcsResponse(infoRequest);
            return response.getMezzanine();

        } catch (Exception e) {
            throw new APPErrorException("getVideo 出错！" + e.getMessage(), e);
        }
    }


    public String getCateSearchFilter(List<Long> pCateIdList) {
        String vSql = StringUtils.EMPTY;
        boolean vHasOrCondition = false;
        if (pCateIdList.size() > 0) {
            for (int i = 0; i < pCateIdList.size(); i++) {
                vSql += "CateId = " + pCateIdList.get(i) + "";
                if (i < (pCateIdList.size() - 1)) {
                    vSql += " or ";
                    vHasOrCondition = true;
                }
            }
            if (vHasOrCondition) {
                return " ( " + vSql + " ) ";
            }
            return vSql;
        }
        return vSql;
    }

    public GetVideoInfoResponse.Video getVideoInfo(String pVideoId) throws APPErrorException {
        //Assert.checkNonNull(pVideoId, "getVideo 参数 不可为null");

        GetVideoInfoRequest infoRequest = new GetVideoInfoRequest();
        infoRequest.setVideoId(pVideoId);
        try {
            GetVideoInfoResponse response = vodClient.getAcsResponse(infoRequest);
            GetVideoInfoResponse.Video vVideo = response.getVideo();
            return vVideo;
        } catch (Exception e) {
            throw new APPErrorException("getVideo 出错！" + e.getMessage(), e);
        }
    }

    /**
     * 获取视频播放信息
     *
     * @param pVideoId
     * @param definition
     * @param tryPlay
     * @return
     * @throws APPErrorException
     */
    public GetPlayInfoResponse getPlayVideoInfo(String pVideoId, Definition definition, boolean tryPlay, long tryPlayTime) throws APPErrorException {
        GetPlayInfoRequest request = new GetPlayInfoRequest();
        request.setOutputType("oss");
        request.setVideoId(pVideoId);
        request.setAuthTimeout(60L * 60);
        if (definition != null) {
            request.setDefinition(definition.getValue());
        }
        PlayConfig vPlayConfig = new PlayConfig();
        //试看时间
        if (tryPlay) {
            if (tryPlayTime < 1) {
                tryPlayTime = 30L;
            }
            vPlayConfig.setPreviewTime(tryPlayTime);
        }
        request.setPlayConfig(vPlayConfig.toString());
        log.info("play config: {}", request.getPlayConfig());
        try {
            return vodClient.getAcsResponse(request);
        } catch (Exception e) {
            throw new APPErrorException("getVideo 出错！" + e.getMessage(), e);
        }

    }

    public UpdateVideoInfoResponse updateVideoInfo(String pVideoId, String pTitle) throws Exception {

        Assert.hasText(pVideoId, "videoId 不可为空");
        Assert.hasText(pTitle, "title 不可为空");

        UpdateVideoInfoRequest request = new UpdateVideoInfoRequest();
        request.setVideoId(pVideoId);
        request.setTitle(pTitle);
        //request.setDescription("new Description");
        //request.setTags("new Tag1,new Tag2");
        return vodClient.getAcsResponse(request);

    }

    //---------------------------------------------------------------------------------
    public void audit(AuditContent auditContentList) throws ClientException {
        audit(new ArrayList() {{
            add(auditContentList);
        }});
    }

    public void audit(List<AuditContent> auditContentList) throws ClientException {
        CreateAuditRequest request = new CreateAuditRequest();
        String str = tgtools.util.JsonParseHelper.parseToJson(auditContentList, false);
        // JSONObject.toJSONString(auditContentList);
        log.info("str: {}", str);
        request.setAuditContent(str);
        vodClient.getAcsResponse(request);
    }

    /**
     * 流式上传接口
     *
     * @param title
     * @param inputStream
     */
    public UploadStreamResponse uploadStream(String title, Long pCateId, String pFileName, String pTemplateGroupId, InputStream inputStream) throws APPErrorException {
        UploadStreamRequest request = new UploadStreamRequest(config.getAccessKey(), config.getAccessSecret(), title, pFileName, inputStream);
        /* 是否使用默认水印（可选），指定模板组ID时，根据模板组配置确定是否使用默认水印*/
        //request.setShowWaterMark(true);
        /* 自定义消息回调设置 */
        //request.setUserData(""{\"Extend\":{\"test\":\"www\",\"localId\":\"xxxx\"},\"MessageCallback\":{\"CallbackURL\":\"http://demo.example.com\"}}"");
        /* 视频分类ID（可选） */
        request.setCateId(pCateId);
        /* 视频标签,多个用逗号分隔（可选） */
        //request.setTags("标签1,标签2");
        /* 视频描述（可选）*/
        //request.setDescription("视频描述");
        /* 封面图片（可选）*/
        //request.setCoverURL("http://cover.example.com/image_01.jpg");
        /* 模板组ID（可选）*/
        request.setTemplateGroupId(pTemplateGroupId);
        /* 工作流ID（可选）*/
        //request.setWorkflowId("d4430d07361f0*be1339577859b0****");
        /* 存储区域（可选）*/
        //request.setStorageLocation("in-201703232118266-5sejd****.oss-cn-shanghai.aliyuncs.com");
        /* 开启默认上传进度回调 */
        // request.setPrintProgress(true);
        /* 设置自定义上传进度回调（必须继承 VoDProgressListener） */
        /*默认关闭。如果开启了这个功能，上传过程中服务端会在日志中返回上传详情。如果不需要接收此消息，需关闭此功能*/
        // request.setProgressListener(new PutObjectProgressListener());
        /* 设置应用ID*/
        //request.setAppId("app-100****");
        /* 点播服务接入点 */
        //request.setApiRegionId("cn-shanghai");
        /* ECS部署区域*/
        // request.setEcsRegionId("cn-shanghai");
        UploadVideoImpl uploader = new UploadVideoImpl();
        UploadStreamResponse response = uploader.uploadStream(request);
        if (response.isSuccess()) {
            return response;
        } else { //如果设置回调URL无效，不影响视频上传，可以返回VideoId同时会返回错误码。其他情况上传失败时，VideoId为空，此时需要根据返回错误码分析具体错误原因
            throw new APPErrorException("上传失败！" + tgtools.util.JsonParseHelper.parseToJson(response, false));
        }
    }

    public UploadFileStreamResponse uploadFileStream(String title, Long pCateId, String fileName) throws APPErrorException {
        UploadFileStreamRequest request = new UploadFileStreamRequest(config.getAccessKey(), config.getAccessSecret(), title, fileName);
        /* 是否使用默认水印（可选），指定模板组ID时，根据模板组配置确定是否使用默认水印*/
        //request.setShowWaterMark(true);
        /* 自定义消息回调设置 */
        //request.setUserData(""{\"Extend\":{\"test\":\"www\",\"localId\":\"xxxx\"},\"MessageCallback\":{\"CallbackURL\":\"http://demo.example.com\"}}"");
        /* 视频分类ID（可选）*/
        request.setCateId(pCateId);
        /* 视频标签,多个用逗号分隔（可选） */
        //request.setTags("标签1,标签2");
        /* 视频描述（可选）*/
        //request.setDescription("视频描述");
        /* 封面图片（可选）*/
        //request.setCoverURL("http://cover.example.com/image_01.jpg");
        /* 模板组ID（可选）*/
        //request.setTemplateGroupId("8c4792cbc8694e7084fd5330e56****");
        /* 工作流ID（可选）*/
        //request.setWorkflowId("d4430d07361f0*be1339577859b0****");
        /* 存储区域（可选）*/
        //request.setStorageLocation("in-201703232118266-5sejd****.oss-cn-shanghai.aliyuncs.com");
        /* 开启默认上传进度回调 */
        //request.setPrintProgress(true);
        /* 设置自定义上传进度回调（必须继承 VoDProgressListener）*/
        /*默认关闭。如果开启了这个功能，上传过程中服务端会在日志中返回上传详情。如果不需要接收此消息，需关闭此功能*/
        //request.setProgressListener(new PutObjectProgressListener());
        /* 设置应用ID*/
        //request.setAppId("app-100****");
        /* 点播服务接入点 */
        //request.setApiRegionId("cn-shanghai");
        /* ECS部署区域*/
        // request.setEcsRegionId("cn-shanghai");
        UploadVideoImpl uploader = new UploadVideoImpl();
        UploadFileStreamResponse response = uploader.uploadFileStream(request);
        if (response.isSuccess()) {
            return response;
        } else {
            /* 如果设置回调URL无效，不影响视频上传，可以返回VideoId同时会返回错误码。其他情况上传失败时，VideoId为空，此时需要根据返回错误码分析具体错误原因 */
            throw new APPErrorException("上传失败！" + tgtools.util.JsonParseHelper.parseToJson(response, false));
        }
    }

    /**
     * URL批量拉取上传
     *
     * @param pUrl http://video_01.mp4
     * @return UploadMediaByURLResponse URL批量拉取上传响应数据
     * @throws Exception
     */
    public UploadMediaByURLResponse uploadMediaByURL(String pTitle, String pUrl, String pCateId, String pTemplateGroupId) throws Exception {
        UploadMediaByURLRequest request = new UploadMediaByURLRequest();
        String encodeUrl = URLEncoder.encode(pUrl, "UTF-8");
        //视频源文件URL
        request.setUploadURLs(encodeUrl);

        //上传视频元数据信息
        ObjectNode uploadMetadata = tgtools.util.JsonParseHelper.createObjectNode();
        //需要上传的视频源文件URL，与UploadURLs里的URL匹配才能生效
        uploadMetadata.put("SourceUrl", encodeUrl);
        //视频标题
        uploadMetadata.put("Title", pTitle);
        uploadMetadata.put("CateId", pCateId);
        uploadMetadata.put("TemplateGroupId", pTemplateGroupId);

        ArrayNode uploadMetadataList = tgtools.util.JsonParseHelper.createArrayNode();
        uploadMetadataList.add(uploadMetadata);
        request.setUploadMetadatas(uploadMetadataList.toString());

        //UserData，用户自定义设置参数，用户需要单独回调URL及数据透传时设置(非必须)
//        JSONObject userData = new JSONObject();

//        //UserData回调部分设置
//        //消息回调设置，指定时以此为准，否则以全局设置的事件通知为准
//        JSONObject messageCallback = new JSONObject();
//        //设置回调地址
//        messageCallback.put("CallbackURL", "http://192.168.0.0/16");
//        //设置回调类型，默认为http
//        messageCallback.put("CallbackType", "http");
//        userData.put("MessageCallback", messageCallback.toJSONString());

//        JSONObject extend = new JSONObject();
//        extend.put("MyId", "user-defined-id");
//        userData.put("Extend", extend.toJSONString());

        //request.setUserData(userData.toJSONString());

        return vodClient.getAcsResponse(request);
    }

    public void updateCate(Long pCateId, String pCateName) throws ClientException {
        UpdateCategoryRequest request = new UpdateCategoryRequest();
        request.setCateId(pCateId);
        request.setCateName(pCateName);
        vodClient.getAcsResponse(request);
    }

    //    public void remove(Long pCateId) throws ClientException {
//        DeleteCategoryRequest request = new DeleteCategoryRequest();
//        request.setCateId(pCateId);
//        vodClient.getAcsResponse(request);
//    }
    public DeleteVideoResponse deleteVideo(String pVideoId) throws Exception {
        DeleteVideoRequest request = new DeleteVideoRequest();
        //支持传入多个视频ID，多个用逗号分隔 "VideoId1,VideoId2"
        request.setVideoIds(pVideoId);
        return vodClient.getAcsResponse(request);
    }

    /**
     * 刷新音/视频上传凭证
     *
     * @param pVideoId 视频id
     * @return RefreshUploadVideoResponse 刷新音/视频上传凭证响应数据
     * @throws Exception
     */
    public RefreshUploadVideoResponse refreshUploadVideo(String pVideoId) throws Exception {
        RefreshUploadVideoRequest request = new RefreshUploadVideoRequest();
        //音频或视频ID
        request.setVideoId(pVideoId);
        return vodClient.getAcsResponse(request);
    }

}

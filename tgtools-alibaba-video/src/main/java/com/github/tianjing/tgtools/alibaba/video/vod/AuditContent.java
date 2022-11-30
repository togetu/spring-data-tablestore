package com.github.tianjing.tgtools.alibaba.video.vod;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @author
 * @date 2022-03-04
 */
@Data
public class AuditContent {

    /**
     * 视频ID
     */
    @JsonProperty("VideoId")
    String videoId;
    /**
     * 视频审核状态
     */
    @JsonProperty("Status")
    Status status;
    /**
     * 若审核状态为屏蔽时，需给出屏蔽的理由。最长支持128字节。
     */
    @JsonProperty("Reason")
    String reason;
    /**
     * 审核备注。最长支持512字节。
     */
    @JsonProperty("Comment")
    String comment;


    public enum Status {
        Blocked("屏蔽"),
        Normal("正常"),
        ;
        String text;

        Status(String text) {
            this.text = text;
        }
    }


}

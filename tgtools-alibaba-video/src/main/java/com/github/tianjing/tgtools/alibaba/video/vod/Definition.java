package com.github.tianjing.tgtools.alibaba.video.vod;

import lombok.Getter;

/**
 * 清晰
 *
 * @author
 * @date 2022-01-24
 */
@Getter
public enum Definition {

    FD("流畅", "FD"),
    LD("标清", "LD"),
    SD("高清", "SD"),
    HD("超清", "HD"),
    OD("原画", "OD"),
    K2("2K", "2K"),
    K4("4K", "4K"),
    SQ("普通音质", "SQ"),
    HQ("高音质", "HQ"),
    AUTO("自适应码率", "AUTO"),


    ;

    String text;
    String value;

    Definition(String text, String value) {
        this.text = text;
        this.value = value;
    }
}

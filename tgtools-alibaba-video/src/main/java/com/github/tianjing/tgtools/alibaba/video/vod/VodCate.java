package com.github.tianjing.tgtools.alibaba.video.vod;

import com.aliyuncs.vod.model.v20170321.GetCategoriesResponse;
import lombok.Data;

import java.util.List;

@Data
public class VodCate extends GetCategoriesResponse.Category {

    List<VodCate> children;
}

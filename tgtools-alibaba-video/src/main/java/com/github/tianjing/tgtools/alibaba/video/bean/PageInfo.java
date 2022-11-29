package com.github.tianjing.tgtools.alibaba.video.bean;

import lombok.Data;

import java.util.Collection;

@Data
public class PageInfo<T> {
    protected Collection<T> records;
    protected long total;
    protected int size;
    protected int current;
}

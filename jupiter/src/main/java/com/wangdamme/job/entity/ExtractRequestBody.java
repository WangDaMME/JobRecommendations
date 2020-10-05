package com.wangdamme.job.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;


// 给 monkeylearn client用的 准备请求
public class ExtractRequestBody {

    public List<String> data;  // 发给 Monkeylearn _ api 的新信息

    @JsonProperty("max_keywords") // 有underscore 和 返回结果match
    public int maxKeywords;

    public ExtractRequestBody(List<String> data, int maxKeywords)
    {
        this.data = data;
        this.maxKeywords = maxKeywords;
    }
}

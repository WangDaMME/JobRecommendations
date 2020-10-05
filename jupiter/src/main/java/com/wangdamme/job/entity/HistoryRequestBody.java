package com.wangdamme.job.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

//history 就是favorite
public class HistoryRequestBody {
    @JsonProperty("user_id")
    public String userId;

    public Item favorite;
}

package com.hotbitmapgg.ohmybilibili.network.api;

import com.hotbitmapgg.ohmybilibili.entity.bangumi.BangumiDetailsRecommend;

import retrofit2.http.GET;
import rx.Observable;

/**
 * Created by hcc on 2016/10/2 16:48
 * 100332338@qq.com
 * <p>
 * 番剧详情里的番剧推荐接口
 * http://bangumi.bilibili.com/api/bangumi_recommend?access_key=f5bd4e793b82fba5aaf5b91fb549910a&actionKey=appkey
 * &appkey=27eb53fc9058f8c3&build=3470&cursor=0
 * &device=phone&mobi_app=iphone&pagesize=10&platform=ios&sign=56329a5709c401d4d7c0237f64f7943f&ts=1469613558
 */

public interface BangumiDetailsRecommendService
{

    @GET("api/bangumi_recommend?access_key=f5bd4e793b82fba5aaf5b91fb549910a&actionKey=appkey&appkey=27eb53fc9058f8c3&build=3470&cursor=0&device=phone&mobi_app=iphone&pagesize=10&platform=ios&sign=56329a5709c401d4d7c0237f64f7943f&ts=1469613558")
    Observable<BangumiDetailsRecommend> getBangumiDetailsRecommended();
}

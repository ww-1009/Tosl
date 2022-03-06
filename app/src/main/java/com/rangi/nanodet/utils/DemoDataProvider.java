/*
 * Copyright (C) 2020 xuexiangjys(xuexiangjys@163.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.rangi.nanodet.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.rangi.nanodet.R;
import com.rangi.nanodet.adapter.entity.NewInfo;
import com.xuexiang.xaop.annotation.MemoryCache;
import com.xuexiang.xui.adapter.simple.AdapterItem;
import com.xuexiang.xui.utils.ResUtils;
import com.xuexiang.xui.widget.banner.widget.banner.BannerItem;

import java.util.ArrayList;
import java.util.List;

/**
 * 演示数据
 *
 * @author xuexiang
 * @since 2018/11/23 下午5:52
 */
public class DemoDataProvider {

    public static String[] titles = new String[]{
            "伪装者:胡歌演绎'痞子特工'",
            "无心法师:生死离别!月牙遭虐杀",
            "花千骨:尊上沦为花千骨",
            "综艺饭:胖轩偷看夏天洗澡掀波澜",
            "碟中谍4:阿汤哥高塔命悬一线,超越不可能",
    };

    public static String[] urls = new String[]{//640*360 360/640=0.5625
            "https://img2.baidu.com/it/u=2679003800,2371836913&fm=253&fmt=auto&app=138&f=JPEG?w=750&h=500",//伪装者:胡歌演绎"痞子特工"
            "https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fi2.hdslb.com%2Fbfs%2Farchive%2Fbb98b5f49aa4791fc725f96e94bd05efd5c8a20a.png&refer=http%3A%2F%2Fi2.hdslb.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=jpeg?sec=1648561847&t=fc83fc8bd0fc13f701740cdfd1e6dd5c",
            "https://img1.baidu.com/it/u=3687861932,4090911923&fm=253&fmt=auto&app=138&f=JPEG?w=500&h=257",//花千骨:尊上沦为花千骨

    };

    @MemoryCache
    public static List<BannerItem> getBannerList() {
        List<BannerItem> list = new ArrayList<>();
        for (int i = 0; i < urls.length; i++) {
            BannerItem item = new BannerItem();
            item.imgUrl = urls[i];
            item.title = titles[i];

            list.add(item);
        }
        return list;
    }

    /**
     * 用于占位的空信息
     *
     * @return
     */
    @MemoryCache
    public static List<NewInfo> getDemoNewInfos() {
        List<NewInfo> list = new ArrayList<>();
        list.add(new NewInfo("视频", "付款")
                .setSummary("获取更多咨询，欢迎点击关注公众号:【我的Android开源之旅】，里面有一整套X-Library系列文章视频介绍！\n")
                .setDetailUrl("https://media.spreadthesign.com/video/mp4/35/379805.mp4")
                .setImageId(R.drawable.img_pay));

        list.add(new NewInfo("视频", "什么")
                .setSummary("涵盖绝大部分的UI组件：TextView、Button、EditText、ImageView、Spinner、Picker、Dialog、PopupWindow、ProgressBar、LoadingView、StateLayout、FlowLayout、Switch、Actionbar、TabBar、Banner、GuideView、BadgeView、MarqueeView、WebView、SearchView等一系列的组件和丰富多彩的样式主题。\n")
                .setDetailUrl("https://media.spreadthesign.com/video/mp4/35/393898.mp4")
                .setImageId(R.drawable.img_what));

        list.add(new NewInfo("视频", "为什么")
                .setSummary("XUpdate 一个轻量级、高可用性的Android版本更新框架。本框架借鉴了AppUpdate中的部分思想和UI界面，将版本更新中的各部分环节抽离出来，形成了如下几个部分：")
                .setDetailUrl("https://media.spreadthesign.com/video/mp4/35/472469.mp4")
                .setImageId(R.drawable.img_why));

        list.add(new NewInfo("视频", "今天")
                .setSummary("一个功能强悍的网络请求库，使用RxJava2 + Retrofit2 + OKHttp组合进行封装。还不赶紧点击使用说明文档，体验一下吧！")
                .setDetailUrl("https://media.spreadthesign.com/video/mp4/35/358133.mp4")
                .setImageId(R.drawable.img_today));

        list.add(new NewInfo("视频", "兄弟")
                .setSummary("其实Android系统的启动最主要的内容无非是init、Zygote、SystemServer这三个进程的启动，他们一起构成的铁三角是Android系统的基础。")
                .setDetailUrl("https://media.spreadthesign.com/video/mp4/35/367254.mp4")
                .setImageId(R.drawable.img_bro));
        return list;
    }

    @MemoryCache
    public static List<NewInfo> getDemoTrendingInfos() {
        List<NewInfo> list = new ArrayList<>();
        list.add(new NewInfo("添加联系人")
                .setImageId(R.drawable.add_linkman));

        list.add(new NewInfo("新的朋友")
                .setImageId(R.drawable.new_linkman));

        list.add(new NewInfo("小王")
                .setImageId(R.drawable.touxiang1));

        list.add(new NewInfo("小张")
                .setImageId(R.drawable.touxiang2));

        list.add(new NewInfo("小吴")
                .setImageId(R.drawable.touxiang3));
        return list;
    }

    public static List<AdapterItem> getGridItems(Context context) {
        return getGridItems(context, R.array.grid_titles_entry, R.array.grid_icons_entry);
    }


    private static List<AdapterItem> getGridItems(Context context, int titleArrayId, int iconArrayId) {
        List<AdapterItem> list = new ArrayList<>();
        String[] titles = ResUtils.getStringArray(titleArrayId);
        Drawable[] icons = ResUtils.getDrawableArray(context, iconArrayId);
        for (int i = 0; i < titles.length; i++) {
            list.add(new AdapterItem(titles[i], icons[i]));
        }
        return list;
    }

    /**
     * 用于占位的空信息
     *
     * @return
     */
    @MemoryCache
    public static List<NewInfo> getEmptyNewInfo() {
        List<NewInfo> list = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            list.add(new NewInfo());
        }
        return list;
    }

}

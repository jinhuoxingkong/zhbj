package com.iigt.myapplication.impl;

import android.app.Activity;
import android.text.TextUtils;
import android.view.View;

import com.google.gson.Gson;
import com.iigt.myapplication.activity.MainActivity;
import com.iigt.myapplication.base.BaseMenuDetailPager;
import com.iigt.myapplication.base.BasePager;
import com.iigt.myapplication.domin.NewsMenu;
import com.iigt.myapplication.fragment.LeftMenuFragment;
import com.iigt.myapplication.global.GlobalConstants;
import com.iigt.myapplication.menu.InteractMenuDetailPager;
import com.iigt.myapplication.menu.NewsMenuDetailPager;
import com.iigt.myapplication.menu.PhotosMenuDetailPager;
import com.iigt.myapplication.menu.TopicMenuDetailPager;
import com.iigt.myapplication.utils.CacheUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import java.util.ArrayList;

/**
 * Created by zhouheng on 2017/2/17.
 */

public class NewsCenterPager extends BasePager {

    private NewsMenu mNewsData;
    private ArrayList<BaseMenuDetailPager> mMenuDetailPagers;// 菜单详情页集合

    public NewsCenterPager(Activity activity) {
        super(activity);
    }

    @Override
    public void initData() {
        System.out.println("新闻初始化啦...");

        // 要给帧布局填充布局对象
//        TextView view = new TextView(mActivity);
//        view.setText("新闻");
//        view.setTextColor(Color.RED);
//        view.setTextSize(22);
//        view.setGravity(Gravity.CENTER);
//
//        flContent.addView(view);

        // 修改页面标题
        tvTitle.setText("新闻");

        // 显示菜单按钮
        btnMenu.setVisibility(View.VISIBLE);

        // 先判断有没有缓存,如果有的话,就加载缓存
        String cache = CacheUtils.getCache(mActivity, GlobalConstants.CATEGORY_URL);
        if (!TextUtils.isEmpty(cache)) {
            System.out.println("发现缓存啦...");
            processData(cache);
        }


        // 请求服务器,获取数据
        // 开源框架: XUtils
        getDataFromServer();
    }

    public void getDataFromServer() {
        HttpUtils utils = new HttpUtils();
        utils.send(HttpRequest.HttpMethod.GET, GlobalConstants.CATEGORY_URL,
                new RequestCallBack<String>() {
                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        //请求成功
                        String result = responseInfo.result;
                        System.out.println("返回服务器的结果："+result);

                        //解析数据
                        processData(result);

                        //写缓存
                        CacheUtils.setCache(mActivity, GlobalConstants.CATEGORY_URL, result);
                    }

                    @Override
                    public void onFailure(HttpException error, String msg) {
                        error.printStackTrace();
                        System.out.println("结果请求失败");
                    }
                });
    }

    //定义方法来解析数据
    protected void processData(String json) {
        Gson gson = new Gson();
        mNewsData = gson.fromJson(json, NewsMenu.class);
        System.out.println("解析结果:" + mNewsData);

        //获取侧边栏对象
        MainActivity mainUI = (MainActivity) mActivity;
        LeftMenuFragment fragment = mainUI.getLeftMenuFragment();

        //给侧边栏设置数据
        fragment.setMenuData(mNewsData.data);

        //初始化4个菜单详情页
        mMenuDetailPagers = new ArrayList<BaseMenuDetailPager>();
        mMenuDetailPagers.add(new NewsMenuDetailPager(mActivity, mNewsData.data.get(0).children));
        mMenuDetailPagers.add(new TopicMenuDetailPager(mActivity));
        mMenuDetailPagers.add(new PhotosMenuDetailPager(mActivity));
        mMenuDetailPagers.add(new InteractMenuDetailPager(mActivity));

        setCurrentDetailPager(0);
    }

    // 设置菜单详情页
    public void setCurrentDetailPager(int position) {
        // 重新给frameLayout添加内容
        BaseMenuDetailPager pager = mMenuDetailPagers.get(position);// 获取当前应该显示的页面
        View view = pager.mRootView;// 当前页面的布局

        // 清除之前旧的布局
        // 帧布局也就是红色的标题栏下面的布局
        flContent.removeAllViews();

        flContent.addView(view);// 给帧布局添加布局

        // 初始化页面数据
        pager.initData();

        // 更新标题
        tvTitle.setText(mNewsData.data.get(position).title);
    }
}

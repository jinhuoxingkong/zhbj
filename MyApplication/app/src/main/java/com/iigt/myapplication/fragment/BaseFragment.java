package com.iigt.myapplication.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by zhouheng on 2017/2/16.
 */

public abstract class BaseFragment extends Fragment {

    public Activity mActivity;

    //Fragment的创建
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //获取当前Fragment所依赖的Activity
        mActivity = getActivity();
    }

    //初始化Fragment的布局
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = initView();
        return view;
    }

    //Fragment所依赖的Activity的onCreate方法执行结束之后
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initData();
    }

    // 初始化布局, 必须由子类实现
    public abstract View initView();

    // 初始化数据, 必须由子类实现
    public abstract void initData();
}

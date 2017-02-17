package com.iigt.myapplication.base;

import android.app.Activity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import com.iigt.myapplication.R;
import com.iigt.myapplication.activity.MainActivity;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

/**
 * Created by zhouheng on 2017/2/16.
 */

public class BasePager {

    public Activity mActivity;
    public View mRootView;// 当前页面的布局对象

    public TextView tvTitle;
    public ImageButton btnMenu;
    public FrameLayout flContent;// 空的帧布局对象, 要动态添加布局

    public BasePager(Activity activity){
        mActivity = activity;
        mRootView = initView();
    }

    //初始化布局
    private View initView() {
        View view = View.inflate(mActivity, R.layout.base_pager, null);

        tvTitle = (TextView) view.findViewById(R.id.tv_title);
        btnMenu = (ImageButton) view.findViewById(R.id.btn_menu);
        flContent = (FrameLayout) view.findViewById(R.id.fl_content);

        btnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggle();
            }
        });
        return view;
    }

    protected void toggle() {
        MainActivity mainUI = (MainActivity) mActivity;
        SlidingMenu slidingMenu = new SlidingMenu(mainUI);
        slidingMenu.toggle();// 如果当前状态是开, 调用后就关; 反之亦然
    }

    // 初始化数据
    public void initData() {

    }
}

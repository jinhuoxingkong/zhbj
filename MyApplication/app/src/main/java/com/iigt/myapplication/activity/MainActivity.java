package com.iigt.myapplication.activity;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;

import com.iigt.myapplication.R;
import com.iigt.myapplication.fragment.ContentFragment;
import com.iigt.myapplication.fragment.LeftMenuFragment;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;

public class MainActivity extends SlidingFragmentActivity {

    private static final String TAG_LEFT_MENU = "TAG_LEFT_MENU";
    private static final String TAG_CONTENT = "TAG_CONTENT";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        // configure the SlidingMenu
//        SlidingMenu menu = new SlidingMenu(this);
//        menu.setMode(SlidingMenu.LEFT);
//        // 设置触摸屏幕的模式
//        menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
//        menu.setShadowWidthRes(R.dimen.shadow_width);
////        menu.setShadowDrawable(R.drawable.shadow);
//
//        // 设置滑动菜单视图的宽度
//        menu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
//        // 设置渐入渐出效果的值
//        menu.setFadeDegree(0.35f);
//
//        menu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
//        //为侧滑菜单设置布局
//        menu.setMenu(R.layout.layout_left_menu);

        setBehindContentView(R.layout.layout_left_menu);
        SlidingMenu slidingMenu = getSlidingMenu();
        slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);// 全屏触摸
        slidingMenu.setBehindOffset(200);// 屏幕预留200像素宽度

        initFragment();
    }

    //初始化Fragment
    private void initFragment() {
        FragmentManager fm = getFragmentManager();
        //开始事物
        FragmentTransaction transaction = fm.beginTransaction();

        //将Fragment填充到布局里面
        transaction.replace(R.id.fl_left_menu, new LeftMenuFragment(), TAG_LEFT_MENU);
        transaction.replace(R.id.fl_main, new ContentFragment(), TAG_CONTENT);

        transaction.commit();
    }


}

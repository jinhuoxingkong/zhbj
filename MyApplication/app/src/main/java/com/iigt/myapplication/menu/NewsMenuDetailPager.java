package com.iigt.myapplication.menu;

import android.app.Activity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import com.iigt.myapplication.R;
import com.iigt.myapplication.activity.MainActivity;
import com.iigt.myapplication.base.BaseMenuDetailPager;
import com.iigt.myapplication.domin.NewsMenu;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.viewpagerindicator.TabPageIndicator;

import java.util.ArrayList;

/**
 * 菜单详情页-新闻
 * 
 * @author Kevin
 * @date 2015-10-18
 */
public class NewsMenuDetailPager extends BaseMenuDetailPager implements ViewPager.OnPageChangeListener{

    //通过注入的方式，和findViewById是一个道理
    @ViewInject(R.id.vp_news_menu_detail)
    private ViewPager mViewPager;

    @ViewInject(R.id.indicator)
    private TabPageIndicator mIndicator;

    private ArrayList<NewsMenu.NewsTabData> mTabData;// 页签网络数据
    private ArrayList<TabDetailPager> mPagers;// 页签页面集合
    private boolean slidingMenuEnable;

    public NewsMenuDetailPager(Activity activity, ArrayList<NewsMenu.NewsTabData> children) {
		super(activity);
        mTabData = children;
	}

	@Override
	public View initView() {
//		TextView view = new TextView(mActivity);
//		view.setText("菜单详情页-新闻");
//		view.setTextColor(Color.RED);
//		view.setTextSize(22);
//		view.setGravity(Gravity.CENTER);

        View view = View.inflate(mActivity, R.layout.pager_news_menu_detail, null);
        ViewUtils.inject(this, view);

        return view;
	}

    // 初始化数据
    public void initData() {
        // 初始化页签
        mPagers = new ArrayList<TabDetailPager>();
        for (int i = 0; i < mTabData.size(); i++){
            TabDetailPager pager = new TabDetailPager(mActivity, mTabData.get(i));
            mPagers.add(pager);
        }

        //这里的mViewPager是findViewById得到的
        mViewPager.setAdapter(new NewsMenuDetailAdapter());

        // 将viewpager和指示器绑定在一起.注意:必须在viewpager设置完数据之后再绑定
        mIndicator.setViewPager(mViewPager);

        mIndicator.setOnPageChangeListener(this);// 此处必须给指示器设置页面监听,不能设置给viewpager

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        if (position == 0) {
            // 开启侧边栏
            setSlidingMenuEnable(true);
        } else {
            // 禁用侧边栏
            setSlidingMenuEnable(false);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }



    // 开启或者禁用侧边栏
    public void setSlidingMenuEnable(boolean slidingMenuEnable) {
        //获取侧边栏的对象
        MainActivity mainUI = (MainActivity) mActivity;
        SlidingMenu slidingMenu = mainUI.getSlidingMenu();

        if (slidingMenuEnable) {
            slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        } else {
            slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
        }
    }

    class NewsMenuDetailAdapter extends PagerAdapter{

        // 指定指示器的标题
        @Override
        public CharSequence getPageTitle(int position) {
            NewsMenu.NewsTabData data = mTabData.get(position);
            return data.title;
        }

        @Override
        public int getCount() {
            return mPagers.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            TabDetailPager pager = mPagers.get(position);
            View view = pager.mRootView;
            container.addView(view);
            pager.initData();

            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }


    //使用注解的方式来设置点击事件
    @OnClick(R.id.btn_next)
    public void nextPage(View view) {
        // 跳到下个页面
        int currentItem = mViewPager.getCurrentItem();
        currentItem++;
        mViewPager.setCurrentItem(currentItem);
    }

}

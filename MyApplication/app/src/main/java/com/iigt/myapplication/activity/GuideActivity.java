package com.iigt.myapplication.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.iigt.myapplication.R;
import com.iigt.myapplication.utils.PrefUtils;

import java.util.ArrayList;

/**
 * Created by zhouheng on 2017/2/14.
 */

public class GuideActivity extends Activity {

    private ViewPager vp_guide;
    private LinearLayout ll_container;
    private ImageView iv_red_point;
    private Button bt_start;

    private ArrayList<ImageView> mImageViewList; // imageView集合

    //新手引导图片的id数组
    private int[] mImageIds = new int[]{R.drawable.guide_1, R.drawable.guide_2, R.drawable.guide_3};

    // 小红点移动距离
    private int mPointDis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initUI();

        initData();

        //设置数据
        vp_guide.setAdapter(new GuideAdapter());
        vp_guide.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            //当页面滑动过程中进行调用
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                System.out.print("当前位置："+position+" 移动偏移百分比"+positionOffset);

                //计算小红点当前的左边距
                int leftMargin = (int)(mPointDis * positionOffset) + (position * mPointDis);

                //获取小红点的布局参数
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) iv_red_point.getLayoutParams();

                //修改左边距
                params.leftMargin = leftMargin;

                //重新设置布局参数
                iv_red_point.setLayoutParams(params);
            }

            @Override
            public void onPageSelected(int position) {
                //某个页面被选中，到达最后一个页面时，显示开始体验按钮
                if (position == mImageViewList.size() - 1) {
                    bt_start.setVisibility(View.VISIBLE);
                } else {
                    bt_start.setVisibility(View.INVISIBLE);
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {
                //当页面状态发生变化的时候调用
            }
        });

        //计算两个小灰点之间的距离
        //要等到Layout绘制完之后才可以进行距离的测量，也就是Activity创建完之后才能画东西
        //这里监听的是Layout方法执行完，也就是位置已经确定好了
        //Mesure()->Layout()->Draw()这是
        iv_red_point.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        iv_red_point.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                        mPointDis = ll_container.getChildAt(1).getLeft()
                                - ll_container.getChildAt(0).getLeft();
                    }
                }
        );

        bt_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PrefUtils.setBoolean(getApplication(), "is_first_enter", false);

                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
            }
        });

    }


    private void initUI() {
        //去掉标题
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_guide);

        vp_guide = (ViewPager) findViewById(R.id.vp_guide);
        ll_container = (LinearLayout) findViewById(R.id.ll_container);
        iv_red_point = (ImageView) findViewById(R.id.iv_red_point);
        bt_start = (Button) findViewById(R.id.bt_start);

    }

    private void initData() {
        mImageViewList = new ArrayList<>();
        for (int i = 0; i < mImageIds.length; i++) {
            ImageView view = new ImageView(this);
            view.setBackgroundResource(mImageIds[i]);
            mImageViewList.add(view);

            //初始化小圆点
            ImageView point = new ImageView(this);
            point.setBackgroundResource(R.drawable.shape_point_gray);

            //初始化布局参数，父控件是谁，就声明谁的布局参数
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );

            if (i > 0) {
                params.leftMargin = 10;
            }
            //先设置参数，再装进容器
            point.setLayoutParams(params);
            ll_container.addView(point);
        }
    }


    //新建一个数据的适配器
    class GuideAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return mImageViewList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        //初始化item布局
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ImageView view = mImageViewList.get(position);
            container.addView(view);
            return view;
        }

        //销毁item布局
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }
}

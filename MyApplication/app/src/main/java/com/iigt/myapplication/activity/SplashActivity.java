package com.iigt.myapplication.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.LinearLayout;

import com.iigt.myapplication.R;
import com.iigt.myapplication.utils.PrefUtils;

/**
 * Created by zhouheng on 2017/2/13.
 */

public class SplashActivity extends Activity{

    private LinearLayout ll_root;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        ll_root = (LinearLayout) findViewById(R.id.ll_Root);

        //定义一个旋转动画
        RotateAnimation animRoatate = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        animRoatate.setDuration(1000);
        animRoatate.setFillAfter(true);

        //定义一个缩放动画
        ScaleAnimation animScale = new ScaleAnimation(0, 1, 0, 1, Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        animScale.setDuration(1000);
        animScale.setFillAfter(true);

        //定义一个渐变动画
        AlphaAnimation animAlpha = new AlphaAnimation(0, 1);
        animAlpha.setDuration(1000);
        animAlpha.setFillAfter(true);

        //定义一个动画集合
        AnimationSet animationSet = new AnimationSet(true);
        animationSet.addAnimation(animRoatate);
        animationSet.addAnimation(animScale);
        animationSet.addAnimation(animAlpha);

        ll_root.startAnimation(animationSet);

        //定义监听器，动画结束后跳转页面
        animationSet.setAnimationListener(new Animation.AnimationListener() {

            private Intent intent;

            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                //是否是第一次进入应用，是进入新手引导界面，否进入主页面
                boolean isFirstEnter = PrefUtils.getBoolean(SplashActivity.this, "is_first_enter", true);
                if (isFirstEnter){
                    //进入新手引导界面
                    intent = new Intent(getApplicationContext(), GuideActivity.class);
                }else {
                    //进入主页面
                    intent = new Intent(getApplicationContext(), MainActivity.class);
                }
                startActivity(intent);

                finish();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }
}

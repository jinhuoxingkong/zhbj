package com.iigt.myapplication.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.iigt.myapplication.R;

import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * Created by zhouheng on 2017/2/22.
 */

public class PullToRefreshListView extends ListView implements AbsListView.OnScrollListener {

    private static final int STATE_PULL_TO_REFRESH = 1;
    private static final int STATE_RELEASE_TO_REFRESH = 2;
    private static final int STATE_REFRESHING = 3;

    private int mCurrentState = STATE_PULL_TO_REFRESH;// 当前刷新状态

    private ImageView iv_arrow;
    private ProgressBar pb_loading;
    private TextView tv_title;
    private TextView tv_time;
    private View mHeaderView;
    private int mHeaderViewHeight;
    private int startY;
    private RotateAnimation animUp;
    private RotateAnimation animDown;
    private OnRefreshListener mListener;
    private View mFooterView;
    private int mFooterViewHeight;

    public PullToRefreshListView(Context context) {
        super(context);
        initHeaderView();
        initFooterView();
    }

    public PullToRefreshListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initHeaderView();
        initFooterView();
    }

    public PullToRefreshListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initHeaderView();
        initFooterView();
    }

    //初始化头布局
    public void initHeaderView(){
        mHeaderView = View.inflate(getContext(), R.layout.pull_to_refresh_header, null);

        //将该头布局加入到ListView中
        this.addHeaderView(mHeaderView);

        iv_arrow = (ImageView) mHeaderView.findViewById(R.id.iv_arrow);
        pb_loading = (ProgressBar) mHeaderView.findViewById(R.id.pb_loading);
        tv_title = (TextView) mHeaderView.findViewById(R.id.tv_title);
        tv_time = (TextView) mHeaderView.findViewById(R.id.tv_time);

        //隐藏头布局
        mHeaderView.measure(0, 0);
        mHeaderViewHeight = mHeaderView.getMeasuredHeight();
        mHeaderView.setPadding(0, -mHeaderViewHeight, 0, 0);

        initAnim();
        setCurrentTime();
    }

    //初始化脚布局
    public void initFooterView(){
        mFooterView = View.inflate(getContext(), R.layout.pull_to_refresh_footer, null);
        this.addFooterView(mFooterView);

        mFooterView.measure(0, 0);
        mFooterViewHeight = mFooterView.getMeasuredHeight();

        mFooterView.setPadding(0, -mFooterViewHeight, 0, 0);

        this.setOnScrollListener(this);// 滑动监听
    }

    private void setCurrentTime() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time = format.format(new Date());

        tv_time.setText(time);
    }

    //重写ListView中的onTouchEvent方法
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startY = (int) ev.getY();
                break;

            case MotionEvent.ACTION_MOVE:
                if (startY == -1) { //当用户按住头条新闻的viewpager进行下拉时,ACTION_DOWN会被viewpager消费掉,
                    // 导致startY没有赋值,此处需要重新获取一下
                    startY = (int) ev.getY();
                }

                if (mCurrentState == STATE_REFRESHING) {
                    // 如果是正在刷新, 跳出循环
                    break;
                }

                int endY = (int) ev.getY();
                int dy = endY - startY;

                int firstVisiblePosition = getFirstVisiblePosition();// 当前显示的第一个item的位置

                // 必须下拉,并且当前显示的是第一个item
                if (dy > 0 && firstVisiblePosition == 0) {
                    int padding = dy - mHeaderViewHeight;// 计算当前下拉控件的padding值
                    mHeaderView.setPadding(0, padding, 0, 0);

                    if (padding > 0 && mCurrentState != STATE_RELEASE_TO_REFRESH) {
                        // 改为松开刷新
                        mCurrentState = STATE_RELEASE_TO_REFRESH;
                        refreshState();
                    } else if (padding < 0
                            && mCurrentState != STATE_PULL_TO_REFRESH) {
                        // 改为下拉刷新
                        mCurrentState = STATE_PULL_TO_REFRESH;
                        refreshState();
                    }

                    return true;
                }

                break;

            case MotionEvent.ACTION_UP:
                startY = -1;

                if (mCurrentState == STATE_RELEASE_TO_REFRESH) {
                    mCurrentState = STATE_REFRESHING;
                    refreshState();

                    // 完整展示头布局
                    mHeaderView.setPadding(0, 0, 0, 0);

                    // 4. 进行回调
                    if (mListener != null) {
                        mListener.onRefresh();
                    }

                } else if (mCurrentState == STATE_PULL_TO_REFRESH) {
                    // 隐藏头布局
                    mHeaderView.setPadding(0, -mHeaderViewHeight, 0, 0);
                }

                break;

            default:
                break;
        }

        return super.onTouchEvent(ev);
    }


    //初始化箭头的动画
    private void initAnim() {
        animUp = new RotateAnimation(0, -180, Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        animUp.setDuration(200);
        animUp.setFillAfter(true);

        animDown = new RotateAnimation(-180, 0, Animation.RELATIVE_TO_SELF,
                0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animDown.setDuration(200);
        animDown.setFillAfter(true);
    }

    //进行下拉刷新的操作
    private void refreshState() {
        switch (mCurrentState){
            case STATE_PULL_TO_REFRESH:
                tv_title.setText("下拉刷新");
                pb_loading.setVisibility(View.INVISIBLE);
                iv_arrow.setVisibility(View.VISIBLE);
                iv_arrow.startAnimation(animDown);

                break;
            case STATE_RELEASE_TO_REFRESH:
                tv_title.setText("松开刷新");
                pb_loading.setVisibility(View.INVISIBLE);
                iv_arrow.setVisibility(View.VISIBLE);
                iv_arrow.setAnimation(animUp);

                break;
            case STATE_REFRESHING:
                tv_title.setText("正在刷新");
                iv_arrow.clearAnimation();// 清除箭头动画,否则无法隐藏
                pb_loading.setVisibility(View.VISIBLE);
                iv_arrow.setVisibility(View.INVISIBLE);
                break;
            default:
                break;
        }
    }


    //刷新结束，收起控件
    public void onRefreshComplete(boolean success) {
        if(!isLoadMore) {
            mHeaderView.setPadding(0, -mHeaderViewHeight, 0, 0);

            mCurrentState = STATE_PULL_TO_REFRESH;
            tv_title.setText("下拉刷新");
            pb_loading.setVisibility(View.INVISIBLE);
            iv_arrow.setVisibility(View.VISIBLE);

            if (success) {// 只有刷新成功之后才更新时间
                setCurrentTime();
            }
        }else {
            //是加载更多的状态的话就收起脚布局
            mFooterView.setPadding(0, -mFooterViewHeight, 0, 0);//隐藏布局
            isLoadMore = false;
        }
    }



    public void setOnRefreshListener(OnRefreshListener listener) {
        mListener = listener;
    }


    private boolean isLoadMore;// 标记是否正在加载更多


    //当滑动的状态发生变换
    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if(scrollState == SCROLL_STATE_IDLE){
            int lastVisiblePosition = getLastVisiblePosition();

            if(lastVisiblePosition == getCount()-1 && !isLoadMore){
                isLoadMore = true;

                mFooterView.setPadding(0, 0, 0, 0);// 显示加载更多的布局
                setSelection(getCount() - 1);//将listview显示在最后一个item上,

                //通知主界面加载下一页数据
                if(mListener!=null) {
                    mListener.onLoadMore();
                }
            }
        }
    }

    //滑动过程的回调
    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

    }

    /**
     * 1. 下拉刷新的回调接口
     *
     * @author Kevin
     * @date 2015-10-21
     */
    public interface OnRefreshListener {
        public void onRefresh();
        public void onLoadMore();
    }
}

package com.iigt.myapplication.menu;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.iigt.myapplication.R;
import com.iigt.myapplication.activity.NewsDetailActivity;
import com.iigt.myapplication.base.BaseMenuDetailPager;
import com.iigt.myapplication.domin.NewsMenu;
import com.iigt.myapplication.domin.NewsTabBean;
import com.iigt.myapplication.global.GlobalConstants;
import com.iigt.myapplication.utils.CacheUtils;
import com.iigt.myapplication.utils.PrefUtils;
import com.iigt.myapplication.view.PullToRefreshListView;
import com.iigt.myapplication.view.TopNewsViewPager;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.viewpagerindicator.CirclePageIndicator;

import java.util.ArrayList;

/**
 * Created by zhouheng on 2017/2/21.
 */

public class TabDetailPager extends BaseMenuDetailPager {

    private NewsMenu.NewsTabData mTabData;// 单个页签的网络数据
    // private TextView view;

    @ViewInject(R.id.vp_top_news)
    private TopNewsViewPager mViewPager;

    @ViewInject(R.id.indicator)
    private CirclePageIndicator mIndicator;

    @ViewInject(R.id.tv_title)
    private TextView tvTitle;

    @ViewInject(R.id.lv_list)
    private PullToRefreshListView lvList;

    private String mUrl;
    private String mMoreUrl;// 下一页数据链接

    private ArrayList<NewsTabBean.TopNews> mTopNews;
    private ArrayList<NewsTabBean.NewsData> mNewsList;

    private NewsAdapter mNewsAdapter;

    private Handler mHandler;

    public TabDetailPager(Activity activity, NewsMenu.NewsTabData newsTabData) {
        super(activity);
        mTabData = newsTabData;

        mUrl = GlobalConstants.SERVER_URL + mTabData.url;
    }


    @Override
    public View initView() {

        View view = View.inflate(mActivity, R.layout.pager_tab_detail, null);
        ViewUtils.inject(this, view);

        // 给listview添加头布局
        View mHeaderView = View.inflate(mActivity, R.layout.list_item_header,
                null);
        ViewUtils.inject(this, mHeaderView);// 此处必须将头布局也注入
        lvList.addHeaderView(mHeaderView);

        lvList.setOnRefreshListener(new PullToRefreshListView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getDataFromServer();
            }

            @Override
            public void onLoadMore() {
                if(mMoreUrl != null){
                    getMoreDataFromServer();
                }else{
                    Toast.makeText(mActivity, "没有更多数据了", Toast.LENGTH_SHORT)
                            .show();
                }

                // 收起下拉刷新控件
                lvList.onRefreshComplete(true);
            }
        });

        lvList.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int headerViewsCount = lvList.getHeaderViewsCount();// 获取头布局数量
                position = position - headerViewsCount;// 需要减去头布局的占位
                System.out.println("第" + position + "个被点击了");

                NewsTabBean.NewsData news = mNewsList.get(position);


                //将被点击的条目换成灰色的
                String readIds = PrefUtils.getString(mActivity, "read_ids", "");
                if(!readIds.contains(news.id+" ")){
                    readIds = readIds + news.id + ",";// 1101,1102,
                    PrefUtils.setString(mActivity, "read_ids", readIds);
                }

                // 要将被点击的item的文字颜色改为灰色, 局部刷新, view对象就是当前被点击的对象
                TextView tvTitle = (TextView) view.findViewById(R.id.tv_title);
                tvTitle.setTextColor(Color.GRAY);

                //跳转到详情页
                Intent intent = new Intent(mActivity, NewsDetailActivity.class);
                intent.putExtra("url", news.url);
                mActivity.startActivity(intent);
            }
        });
        return view;
    }

    @Override
    public void initData() {
//        view.setText(mTabData.title);

        //首先要加载缓存
        String cache = CacheUtils.getCache(mActivity, mUrl);
        if(!TextUtils.isEmpty(cache)){
            processData(cache, false);
        }
        getDataFromServer();
    }

    public void getDataFromServer() {
        HttpUtils utils = new HttpUtils();
        utils.send(HttpRequest.HttpMethod.GET, mUrl, new RequestCallBack<String>() {

            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                System.out.println("请求TopNews数据成功-----");
                String result = responseInfo.result;
                processData(result, false);

                CacheUtils.setCache(mActivity, mUrl, result);

                lvList.onRefreshComplete(true);
            }

            @Override
            public void onFailure(HttpException error, String msg) {
                // 请求失败
                error.printStackTrace();
                Toast.makeText(mActivity, msg, Toast.LENGTH_SHORT).show();
            }
        });

    }

    //加载下一页的数据
    protected void getMoreDataFromServer() {
        HttpUtils utils = new HttpUtils();
        utils.send(HttpRequest.HttpMethod.GET, mMoreUrl, new RequestCallBack<String>() {

            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                String result = responseInfo.result;
                processData(result, true);

                // 收起下拉刷新控件
                lvList.onRefreshComplete(true);
            }

            @Override
            public void onFailure(HttpException error, String msg) {
                // 请求失败
                error.printStackTrace();
                Toast.makeText(mActivity, msg, Toast.LENGTH_SHORT).show();

                // 收起下拉刷新控件
                lvList.onRefreshComplete(false);
            }
        });
    }

    protected void processData(String result, boolean isMore) {
        System.out.println("进行数据的解析-----");
        Gson gson = new Gson();
        NewsTabBean newsTabBean = gson.fromJson(result, NewsTabBean.class);


        //得到moreUrl的链接
        String moreUrl = newsTabBean.data.more;
        if (!TextUtils.isEmpty(moreUrl)) {
            mMoreUrl = GlobalConstants.SERVER_URL + moreUrl;
        } else {
            mMoreUrl = null;
        }

        //判断要不加载更多的数据
        if(!isMore) {
            //不是加载更多数据

            //获取头条新闻的数据
            mTopNews = newsTabBean.data.topnews;

            if (mTopNews != null) {
                mViewPager.setAdapter(new TopNewsAdapter());
                mIndicator.setViewPager(mViewPager);
                mIndicator.setSnap(true);// 快照方式展示

                //监听viewPager改变的事件
                //当有indicator的事件，我们就将事件设置给indicator
                mIndicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                    }

                    @Override
                    public void onPageSelected(int position) {
                        //更新头条新闻的标题
                        NewsTabBean.TopNews topNews = mTopNews.get(position);
                        tvTitle.setText(topNews.title);
                    }

                    @Override
                    public void onPageScrollStateChanged(int state) {

                    }
                });

                tvTitle.setText(mTopNews.get(0).title);
                mIndicator.onPageSelected(0);
            }

            // 更新列表新闻的数据
            mNewsList = newsTabBean.data.news;

            if (mNewsList != null) {
                mNewsAdapter = new NewsAdapter();
                lvList.setAdapter(mNewsAdapter);
            }

            //使用Handler进行自动轮播
            if(mHandler == null){
                mHandler = new Handler(){
                    @Override
                    public void handleMessage(Message msg) {
                        int currentItem = mViewPager.getCurrentItem();
                        currentItem++;

                        if (currentItem > mTopNews.size() - 1) {
                            currentItem = 0;// 如果已经到了最后一个页面,跳到第一页
                        }

                        mViewPager.setCurrentItem(currentItem);
                        mHandler.sendEmptyMessageDelayed(0, 3000);// 继续发送延时3秒的消息,形成内循环
                    };
                };
                // 保证启动自动轮播逻辑只执行一次
                mHandler.sendEmptyMessageDelayed(0, 3000);// 发送延时3秒的消息

                mViewPager.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        switch (event.getAction()){
                            case MotionEvent.ACTION_DOWN:
                                mHandler.removeCallbacksAndMessages(null);
                                break;
                            case MotionEvent.ACTION_CANCEL:// 取消事件,
                                mHandler.sendEmptyMessageDelayed(0, 3000);
                                break;
                            case MotionEvent.ACTION_UP:
                                System.out.println("ACTION_UP");
                                // 启动广告
                                mHandler.sendEmptyMessageDelayed(0, 3000);
                                break;

                            default:
                                break;
                        }
                        return false;
                    }
                });
            }
        }else{
            // 需要加载更多数据
            ArrayList<NewsTabBean.NewsData> moreNews = newsTabBean.data.news;
            mNewsList.addAll(moreNews);// 将数据追加在原来的集合中
            // 刷新listview
            mNewsAdapter.notifyDataSetChanged();
        }

    }

    // 头条新闻数据适配器
    class TopNewsAdapter extends PagerAdapter {

        private BitmapUtils mBitmapUtils;

        public TopNewsAdapter() {
            mBitmapUtils = new BitmapUtils(mActivity);
            mBitmapUtils
                    .configDefaultLoadingImage(R.drawable.topnews_item_default);// 设置加载中的默认图片
        }

        @Override
        public int getCount() {
            return mTopNews.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ImageView view = new ImageView(mActivity);
            // view.setImageResource(R.drawable.topnews_item_default);
            view.setScaleType(ImageView.ScaleType.FIT_XY);// 设置图片缩放方式, 宽高填充父控件

            String imageUrl = mTopNews.get(position).topimage;// 图片下载链接

            // 下载图片-将图片设置给imageview-避免内存溢出-缓存
            // BitmapUtils-XUtils
            mBitmapUtils.display(view, imageUrl);

            container.addView(view);

            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }

    //列表新闻数据适配器
    class NewsAdapter extends BaseAdapter {

        private BitmapUtils mBitmapUtils;
        private ViewHolder holder;

        public NewsAdapter() {
            mBitmapUtils = new BitmapUtils(mActivity);
            mBitmapUtils.configDefaultLoadingImage(R.drawable.news_pic_default);
        }

        @Override
        public int getCount() {
            return mNewsList.size();
        }

        @Override
        public NewsTabBean.NewsData getItem(int position) {
            return mNewsList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if(convertView == null){
                convertView = View.inflate(mActivity, R.layout.list_item_news, null);

                holder = new ViewHolder();

                holder.ivIcon = (ImageView) convertView.findViewById(R.id.iv_icon);
                holder.tvTitle = (TextView) convertView.findViewById(R.id.tv_title);
                holder.tvDate = (TextView) convertView.findViewById(R.id.tv_date);

                convertView.setTag(holder);
            }else {
                holder = (ViewHolder) convertView.getTag();
            }

            NewsTabBean.NewsData news = getItem(position);
            holder.tvTitle.setText(news.title);
            holder.tvDate.setText(news.pubdate);



            // 根据本地记录来标记已读未读
            String readIds = PrefUtils.getString(mActivity, "read_ids", "");
            if (readIds.contains(news.id + "")) {
                holder.tvTitle.setTextColor(Color.GRAY);
            } else {
                holder.tvTitle.setTextColor(Color.BLACK);
            }

            mBitmapUtils.display(holder.ivIcon, news.listimage);

            return convertView;
        }

    }

    class ViewHolder {
        public ImageView ivIcon;
        public TextView tvTitle;
        public TextView tvDate;
    }

}

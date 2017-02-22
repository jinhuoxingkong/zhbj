package com.iigt.myapplication.domin;

import java.util.ArrayList;

/**
 * Created by zhouheng on 2017/2/18.
 */

public class NewsMenu {

    public int retcode;
    public ArrayList<Integer> extend;
    public ArrayList<NewsMenuData> data;

    public class NewsMenuData{
        public int id;
        public String title;
        public int type;

        public ArrayList<NewsTabData> children;

        @Override
        public String toString() {
            return "NewsMenuData [title=" + title + ", children=" + children
                    + "]";
        }
    }

    //页签的对象
    public class NewsTabData {
        public int id;
        public String title;
        public int type;
        public String url;

        @Override
        public String toString() {
            return "NewsTabData [title=" + title + "]";
        }
    }

    public String toString() {
        return "NewsMenu [data=" + data + "]";
    }

}

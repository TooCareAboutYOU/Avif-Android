package zs.plugin.android.model;

import com.google.gson.Gson;

import java.io.Serializable;

/**
 * @author zhangshuai@attrsense.com
 * @date 2022/11/29 18:51
 * @description
 */
public class DingDingNewsBean implements Serializable {
    public String msgtype;
    public TextBean text;
    public LinkBean link;
    public PhotoBean photo;
    public MarkdownBean markdown;
    public ActionCardBean actionCard;
    public FeedCardBean feedCard;
    public AtBean at;

    /**
     * 文本
     * 消息类型，此时固定为：text。
     */
    public static class TextBean implements Serializable {
        public String content;

        @Override
        public String toString() {
            return new Gson().toJson(this);
        }
    }

    /**
     * 链接
     * 消息类型，此时固定为：link。
     */
    public static class LinkBean implements Serializable {
        public String text;
        public String title;
        public String picUrl;
        public String messageUrl;

        @Override
        public String toString() {
            return new Gson().toJson(this);
        }
    }


    /**
     * 图片
     */
    public static class PhotoBean implements Serializable {
        public String photoURL;

        @Override
        public String toString() {
            return new Gson().toJson(this);
        }
    }

    /**
     * Markdown
     * 消息类型，此时固定为：markdown。
     */
    public static class MarkdownBean implements Serializable {
        public String title;    //首屏会话透出的展示内容。
        public String text;     //Markdown格式的消息内容。

        @Override
        public String toString() {
            return new Gson().toJson(this);
        }
    }

    /**
     * 整体跳转
     * 消息类型，此时固定为：actionCard。
     */
    public static class ActionCardBean implements Serializable {
        public String title;            //首屏会话透出的展示内容。
        public String text;             //markdown格式的消息内容。
        //0：按钮竖直排列 / 1：按钮横向排列
        public int btnOrientation = 0;
        //样式一
        public String singleTitle;      //单个按钮的标题。
        public String singleURL;        //单个按钮的跳转链接。
        //样式二
        public BtnsBean[] btns;         //按钮。


        public static class BtnsBean implements Serializable {
            public String title;        //按钮标题。
            public String actionURL;    //点击按钮触发的URL。

            @Override
            public String toString() {
                return new Gson().toJson(this);
            }
        }

        @Override
        public String toString() {
            return new Gson().toJson(this);
        }
    }

    /**
     * FeedCard类型
     * 此消息类型为固定feedCard。
     */
    public static class FeedCardBean implements Serializable {
        public FeedCardLinksBean[] links;

        public static class FeedCardLinksBean implements Serializable {
            public String title;        //单条信息文本。
            public String messageURL;   //单条信息跳转链接
            public String picURL;       //单条信息后面图片的URL

            @Override
            public String toString() {
                return new Gson().toJson(this);
            }
        }

        @Override
        public String toString() {
            return new Gson().toJson(this);
        }
    }

    /**
     * 用户相关
     */
    public static class AtBean implements Serializable {
        public String[] atMobiles;          //被@人的手机号。说明 消息内容text内要带上"@手机号"，跟atMobiles参数结合使用，才有@效果，如上示例。
        public String[] atUserIds;          //被@人的用户userid
        public boolean isAtAll = false;     //@所有人是true，否则为false

        @Override
        public String toString() {
            return new Gson().toJson(this);
        }
    }

    /**
     * ---------------------------------------------------------------------------------------------
     * 填充消息模板
     * ---------------------------------------------------------------------------------------------
     *
     * @description：
     */
    public MsgParamMapBean msg_param_map;
    public MsgMediaIdParamMapBean msg_media_id_param_map;

    public static class MsgParamMapBean implements Serializable {
        public String type_name;

        @Override
        public String toString() {
            return new Gson().toJson(this);
        }
    }

    public static class MsgMediaIdParamMapBean implements Serializable {
        public String img_media_id;

        @Override
        public String toString() {
            return new Gson().toJson(this);
        }
    }


    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
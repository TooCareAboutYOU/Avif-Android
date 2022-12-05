package zs.plugin.android.model;

import com.google.gson.Gson;

import java.io.Serializable;

/**
 * @author zhangshuai@attrsense.com
 * @date 2022/12/5 10:58
 */
public class PluginConfigBean implements Serializable {
    /**
     * (必填)执行gradle目标Task的名称
     */
//    public String[] executeTaskName;
//      "executeTaskName": [
//              "assembleRelease",
//              "assembleDebug"
//              ],

    public PgyConfigBean pgyConfig;
    public DdConfigBean ddConfig;
    public DingDingBean ddContent;

    public static class PgyConfigBean implements Serializable {
        //(必填)蒲公英服务地址
        public String pgyBaseUrl;
        //(必填)蒲公英账号生成的APP KEY
        public String pgyAppKey;
        //(必填) API Key
        public String pgyApiKey;
        //(必填) 需要上传的应用类型，如果是iOS类型请传ios或ipa，如果是Android类型请传android或apk
        public String pgyBuildType;
        //(选填) 是否使用海外加速上传，值为：1 使用海外加速上传，2 国内加速上传；留空根据 IP 自动判断海外加速或国内加速
        public int pgyOversea;
        //(选填)应用安装方式，值为(1,2,3，默认为1 公开安装)。1：公开安装，2：密码安装，3：邀请安装
        public int pgyBuildInstallType;
        //(选填) 设置App安装密码，密码为空时默认公开安装
        public String pgyBuildPassword;
        //(选填) 应用介绍，如没有介绍请传空字符串，或不传。
        public String pgyBuildDescription;
        //(选填) 版本更新描述，请传空字符串，或不传。
        public String pgyBuildUpdateDescription;
        //(选填)是否设置安装有效期，值为：1 设置有效时间， 2 长期有效，如果不填写不修改上一次的设置
        public int pgyBuildInstallDate;
        //(选填)安装有效期开始时间，字符串型，如：2018-01-01
        public String pgyBuildInstallStartDate;
        //(选填)安装有效期结束时间，字符串型，如：2018-12-31
        public String pgyBuildInstallEndDate;
        //(选填)所需更新指定的渠道短链接，渠道短链接须为已创建成功的，并且只可指定一个渠道，字符串型，如：abcd
        public String pgyBuildChannelShortcut;
        //(必填)本地Apk文件的本地路径
        public String apkOutputPath;
        //(选填)apk文件的原始名
        public String apkName;

        @Override
        public String toString() {
            return new Gson().toJson(this);
        }
    }

    public static class DdConfigBean implements Serializable {
        public String ddWebSecret;
        public String ddWebHookUrl;

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
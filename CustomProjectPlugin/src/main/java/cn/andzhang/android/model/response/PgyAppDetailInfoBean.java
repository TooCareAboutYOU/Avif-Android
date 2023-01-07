package cn.andzhang.android.model.response;

import com.google.gson.Gson;

import java.io.Serializable;

/**
 * @author zhangshuai@attrsense.com
 * @date 2022/11/29 22:14
 * 应用详情返回参数	        类型	    说明
 * buildKey	                String	Build Key是唯一标识应用的索引ID
 * buildType	            Integer	应用类型（1:iOS; 2:Android）
 * buildIsFirst	            Integer	是否是第一个App（1:是; 2:否）
 * buildIsLastest	        Integer	是否是最新版（1:是; 2:否）
 * buildFileSize	        Integer	App 文件大小
 * buildName	            String	应用名称
 * iconUrl	                String	应用图标地址
 * buildVersion	            String	版本号, 默认为1.0 (是应用向用户宣传时候用到的标识，例如：1.1、8.2.1等。)
 * buildVersionNo	        String	上传包的版本编号，默认为1 (即编译的版本号，一般来说，编译一次会变动一次这个版本号, 在 Android 上叫 Version Code。对于 iOS 来说，是字符串类型；对于 Android 来说是一个整数。例如：1001，28等。)
 * buildBuildVersion        Integer	蒲公英生成的用于区分历史版本的build号
 * buildIdentifier	        String	应用程序包名，iOS为BundleId，Android为包名
 * buildIcon	            String	应用的Icon图标key，访问地址为 https://www.pgyer.com/image/view/app_icons/[应用的Icon图标key]
 * buildDescription	        String	应用介绍
 * buildUpdateDescription	String	应用更新说明
 * buildScreenShots	        String	应用截图的key，获取地址为 https://www.pgyer.com/image/view/app_screenshots/[应用截图的key]
 * buildScreenshotsUrl	    array	应用截图地址
 * buildShortcutUrl	        String	应用短链接
 * buildCates	            String	应用分类，多个分类用 “,” 号隔开
 * buildQRCodeURL	        String	应用二维码地址
 * buildCreated	            String	应用上传时间
 * buildUpdated	            String	应用更新时间
 * otherApps	            array	历史版本
 * otherAppsCount	        int	    历史版本数量
 */
public class PgyAppDetailInfoBean implements Serializable {
    public String buildKey;
    public Integer buildType;
    public Integer buildIsFirst;
    public Integer buildIsLastest;
    public Integer buildFileSize;
    public String buildName;
    public String iconUrl;
    public String buildVersion;
    public String buildVersionNo;
    public Integer buildBuildVersion;
    public String buildIdentifier;
    public String buildIcon;
    public String buildDescription;
    public String buildUpdateDescription;
    public String buildScreenShots;
    public String[] buildScreenshotsUrl;
    public String buildShortcutUrl;
    public String buildCates;
    public String buildQRCodeURL;
    public String buildCreated;
    public String buildUpdated;
    public OtherAppsBean[] otherApps;
    public int otherAppsCount;

    /**
     * 历史版本返回参数        类型	说明
     * buildKey	            String	Build Key是唯一标识应用的索引ID
     * buildName	        String	应用名称
     * buildVersion	        String	版本号, 默认为1.0 (是应用向用户宣传时候用到的标识，例如：1.1、8.2.1等。)
     * buildBuildVersion	Integer	蒲公英生成的用于区分历史版本的build号
     * buildCreated	        String	应用上传时间
     * buildUpdated	        String	应用更新时间
     */
    public static class OtherAppsBean implements Serializable {
        public String buildKey;
        public String buildName;
        public String buildVersion;
        public Integer buildBuildVersion;
        public String buildCreated;
        public String buildUpdated;

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
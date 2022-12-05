package zs.plugin.android.http;

import org.apache.commons.codec.binary.Base64;
import org.gradle.api.Project;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import zs.plugin.android.api.ApiService;
import zs.plugin.android.model.BaseResponseBean;
import zs.plugin.android.model.DingDingBean;
import zs.plugin.android.model.GetAppDetailInfoBean;
import zs.plugin.android.model.GetUploadTokenBean;

/**
 * @author zhangshuai@attrsense.com
 * @date 2022/11/28 18:45
 * @description
 */
public class HttpRequestCopy {

    private static Project mProject;
    //蒲公英配置
    private static String pgyBaseUrl;
    private static String pgyApikey;
    private static String pgyAppKey;
    private static String pgyDescription;
    private static String pgyUpdateDescription;
    //钉钉配置
    private static Long mTimestamp;
    private static String dd_webSecret;
    private static String dd_webHook;
    private static String dd_apkPath;

    private HttpLoggingInterceptor mInterceptor = new HttpLoggingInterceptor(s -> {
        System.out.println("日志：" + s);
        if (s.contains("204")) {
            System.out.println("上传成功！！");
            getAppDetailInfo();
        }
    });
    private OkHttpClient mOkhttpClient;
    private Retrofit mRetrofit;
    private ApiService mApiService;
    private GetUploadTokenBean mUploadToken;

    private HttpRequestCopy() {
        mInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        mOkhttpClient = new OkHttpClient.Builder().addInterceptor(mInterceptor).build();
        mRetrofit = new Retrofit.Builder()
                .baseUrl(pgyBaseUrl)
                .client(mOkhttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        mApiService = mRetrofit.create(ApiService.class);
    }

    public static HttpRequestCopy getInstance() {
        return HttpRequestHolder.INSTANCE;
    }

    private static class HttpRequestHolder {
        private static final HttpRequestCopy INSTANCE = new HttpRequestCopy();
    }

    public static void init(Project project) {
        mProject = project;
        try {
            Properties properties = new Properties();
            File file = new File(project.getRootDir().getAbsolutePath() + "/gradle.properties");
            FileInputStream inputStream = new FileInputStream(file);
            properties.load(inputStream);

            pgyBaseUrl = properties.getProperty("PGY_BASE_URL");
            pgyApikey = properties.getProperty("PGY_API_KEY");
            pgyAppKey = properties.getProperty("PGY_APP_KEY");
            pgyDescription = properties.getProperty("PGY_DESCRIPTION");
            pgyUpdateDescription = properties.getProperty("PGY_UPDATE_DESCRIPTION");

            dd_webSecret = properties.getProperty("DD_WEB_SECRET");
            dd_webHook = properties.getProperty("DD_WEB_HOOK_URL");
            dd_apkPath = properties.getProperty("DD_APK_PATH");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取token
     */
    public void getUploadKey() {
        Map<String, Object> map = new HashMap<String, Object>(4) {
            {
                put("_api_key", pgyApikey);
                put("buildType", "apk");
                put("oversea", 2);
                put("buildInstallType", 1);

//                put("buildPassword", "");
                put("buildDescription", pgyDescription);
                put("buildUpdateDescription", pgyUpdateDescription);
                put("buildInstallDate", 2);
//                put("buildInstallStartDate", "");
//                put("buildInstallEndDate", "");
//                put("buildChannelShortcut", "");
            }
        };
        Call<BaseResponseBean<GetUploadTokenBean>> callBack = mApiService.getUploadToken(map);
        try {
            Response<BaseResponseBean<GetUploadTokenBean>> result = callBack.execute();
            System.out.println(">>>>>>>>>> HttpRequest.getUploadKey：" + result.body());
            if (result.body() != null) {
                mUploadToken = result.body().data;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 上传apk文件
     */
    public void uploadApk() {
        System.out.println("运行了uploadApk：" + mUploadToken);
        if (mUploadToken != null) {
            try {
                File file = new File(mProject.getRootDir() + "/app/release/app-release.apk");
                if (file.exists()) {
                    System.out.println("文件路径：" + file.getAbsolutePath());
                    RequestBody fileBody = RequestBody.create(file, MediaType.parse("multipart/form-data"));

                    Call<BaseResponseBean<Object>> callBack = mApiService.uploadApk(
                            mUploadToken.endpoint,
                            RequestBody.create(mUploadToken.key, MediaType.parse("text/plain")),
                            RequestBody.create(mUploadToken.params.signature, MediaType.parse("text/plain")),
                            RequestBody.create(mUploadToken.params.xToken, MediaType.parse("text/plain")),
                            RequestBody.create("debug_v1.0_1.apk", MediaType.parse("text/plain")),
                            MultipartBody.Part.createFormData("file", file.getName(), fileBody)
                    );
                    Response<BaseResponseBean<Object>> result = callBack.execute();
                    System.out.println(">>>>>>>>>> HttpRequest.uploadApk：" + result.body());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void getAppDetailInfo() {
        Map<String, Object> map = new HashMap<String, Object>(3) {
            {
                put("_api_key", pgyApikey);
                put("appKey", pgyAppKey);
                put("buildKey", "");
            }
        };
        Call<BaseResponseBean<GetAppDetailInfoBean>> callBack = mApiService.getAppDetailInfo(map);
        try {
            Response<BaseResponseBean<GetAppDetailInfoBean>> result = callBack.execute();

            System.out.println(">>>>>>>>>> HttpRequest.getAppDetailInfo：" + result.body().data);

            if (result.body() != null && result.body().data != null) {
                GetAppDetailInfoBean data = result.body().data;

//                StringBuilder sb = new StringBuilder();
//                sb.append("应用名称：").append(data.buildName).append("\n");
//                sb.append("更新版本：").append(data.buildVersion).append("\n");
//                sb.append("更新时间：").append(data.buildUpdated).append("\n");
//                sb.append("应用介绍：").append(data.buildDescription).append("\n");
//                sb.append("版本描述：").append(data.buildUpdateDescription).append("\n");


//                postLinkToDD(data.buildName, sb.toString(), data.buildQRCodeURL, data.buildShortcutUrl);

//                postActionCardToDD(data.buildName,sb.toString(),"应用详情",data.buildQRCodeURL);

                String text = "### " + data.buildName +
                        "\n*   更新版本：" + data.buildVersion +
                        "\n*   更新时间：" + data.buildUpdated +
//                        "应用介绍：" + getString(data.buildDescription) +
//                        "版本描述：" + getString(data.buildUpdateDescription) +
                        "\n![screenshot](" + data.buildQRCodeURL + ")" +
                        "\n##### @18874703156";
                postMarkDownToDD(data.buildName, text);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * ---------------------------------------------------------------------------------------------
     * 钉钉操作相关
     * ---------------------------------------------------------------------------------------------
     */

    private String getString(String str) {
        try {
            return new String(str.getBytes("UTF-8"), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "";
    }

    private String getSign() {
        String sign = null;
        try {
            mTimestamp = System.currentTimeMillis();
            String stringToSign = mTimestamp + "\n" + dd_webSecret;
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(dd_webSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] signData = mac.doFinal(stringToSign.getBytes(StandardCharsets.UTF_8));
            sign = URLEncoder.encode(new String(Base64.encodeBase64(signData)), "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sign;
    }

    /**
     * 拼接webhook
     */
    private String getWebHook() {
        String sign = getSign();
        return dd_webHook += "&timestamp=" + mTimestamp + "&sign=" + sign;
    }

    /**
     * ---------------------------------------------------------------------------------------------
     * 消息方式：文本、链接、图片、markdown、跳转
     * ---------------------------------------------------------------------------------------------
     */

    /**
     * 发送文本到钉钉
     */
    public void postTextToDD() {
        DingDingBean bean = new DingDingBean();
        bean.msgtype = "text";
        DingDingBean.TextBean textBean = new DingDingBean.TextBean();
        textBean.content = "发布：我是Android Apk动态发布的测试消息！";
        bean.text = textBean;
        System.out.println("提交数据：" + bean);
        try {
            Response<BaseResponseBean<Object>> result = mApiService.postToDD(getWebHook(), bean).execute();
            System.out.println(">>>>>>>>>> HttpRequest.postTextToDD：" + result.body());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送链接到钉钉
     */
    public void postLinkToDD(String title, String text, String picUrl, String messageUrl) {
        DingDingBean bean = new DingDingBean();
        bean.msgtype = "link";
        DingDingBean.LinkBean linkBean = new DingDingBean.LinkBean();
        linkBean.title = "发布：" + title;
        linkBean.text = text;
        linkBean.picUrl = picUrl;
        linkBean.messageUrl = messageUrl;
        bean.link = linkBean;

        System.out.println("提交数据：" + bean);
        try {
            Response<BaseResponseBean<Object>> result = mApiService.postToDD(getWebHook(), bean).execute();
            System.out.println(">>>>>>>>>> HttpRequest.postLinkToDD：" + result.body());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送图片到钉钉
     *
     * @param picUrl 二维码
     */
    public void postPhotoToDD(String picUrl) {
        DingDingBean bean = new DingDingBean();
        bean.msgtype = "photo";
        DingDingBean.PhotoBean photoBean = new DingDingBean.PhotoBean();
        photoBean.photoURL = picUrl;
        bean.photo = photoBean;

        System.out.println("提交数据：" + bean);
        try {
            Response<BaseResponseBean<Object>> result = mApiService.postToDD(getWebHook(), bean).execute();
            System.out.println(">>>>>>>>>> HttpRequest.postPhotoToDD：" + result.body());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送Markdown到钉钉
     */
    public void postMarkDownToDD(String title, String text) {
//        DingDingBean bean = new DingDingBean();
//        bean.msgtype = "markdown";
//        DingDingBean.MarkdownBean markdownBean = new DingDingBean.MarkdownBean();
//        markdownBean.title = "发布：" + title;
//        markdownBean.text = text;
//        markdownBean.atMobiles = new String[]{"18874703156"};
//        bean.markdown = markdownBean;
//
//        System.out.println("提交数据：" + bean);
//        try {
//            Response<BaseResponseBean<Object>> result = mApiService.postToDD(getWebHook(), bean).execute();
//            System.out.println(">>>>>>>>>> HttpRequest.postMarkDownToDD：" + result.body());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    /**
     * 发送整体跳转到钉钉
     */
    public void postActionCardToDD(String title, String text, String singleTitle, String singleURL) {
        DingDingBean bean = new DingDingBean();
        bean.msgtype = "actionCard";
        DingDingBean.ActionCardBean cardBean = new DingDingBean.ActionCardBean();
        cardBean.title = "发布：" + title;
        cardBean.text = text;
        cardBean.singleTitle = singleTitle;
        cardBean.singleURL = singleURL;
        bean.actionCard = cardBean;

        System.out.println("提交数据：" + bean);
        try {
            Response<BaseResponseBean<Object>> result = mApiService.postToDD(getWebHook(), bean).execute();
            System.out.println(">>>>>>>>>> HttpRequest.postActionCardToDD：" + result.body());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
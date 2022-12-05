package zs.plugin.android.http;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import org.apache.commons.codec.binary.Base64;
import org.gradle.api.Project;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

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
import zs.plugin.android.model.GetAppDetailInfoBean;
import zs.plugin.android.model.GetUploadTokenBean;
import zs.plugin.android.model.PluginConfigBean;

/**
 * @author zhangshuai@attrsense.com
 * @date 2022/11/28 18:45
 * @description
 */
public class HttpRequest {

    private static Project mProject;
    private static Gson gson;
    public static PluginConfigBean mData;
    private static Long mTimestamp;
    private static boolean isUpload = false;

    private HttpLoggingInterceptor mInterceptor = new HttpLoggingInterceptor(s -> {
        printLog("日志：" + s);
        if (s.contains("204")) {
            printLog("上传成功！！");
            isUpload = true;
        }
    });
    private OkHttpClient mOkhttpClient;
    private Retrofit mRetrofit;
    private ApiService mApiService;
    private GetUploadTokenBean mUploadToken;

    private HttpRequest() {
        mInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        mOkhttpClient = new OkHttpClient.Builder().addInterceptor(mInterceptor).build();
        mRetrofit = new Retrofit.Builder()
                .baseUrl(mData.pgyConfig.pgyBaseUrl)
                .client(mOkhttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        mApiService = mRetrofit.create(ApiService.class);
    }

    public static HttpRequest getInstance() {
        return HttpRequestHolder.INSTANCE;
    }

    private static class HttpRequestHolder {
        private static final HttpRequest INSTANCE = new HttpRequest();
    }

    public static void init(Project project) {
        mProject = project;
        gson = new Gson();
        File file = new File(project.getRootDir().getAbsolutePath() + "/releaseApk.json");
        try {
            JsonReader jsonReader = new JsonReader(new FileReader(file));
            mData = gson.fromJson(jsonReader, PluginConfigBean.class);

            //拼接webhook
            getSign();
            printLog("输出：" + mData);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static String getSign() {
        String sign = null;
        try {
            mTimestamp = System.currentTimeMillis();
            String stringToSign = mTimestamp + "\n" + mData.ddConfig.ddWebSecret;
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(mData.ddConfig.ddWebSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] signData = mac.doFinal(stringToSign.getBytes(StandardCharsets.UTF_8));
            sign = URLEncoder.encode(new String(Base64.encodeBase64(signData)), "UTF-8");
            mData.ddConfig.ddWebHookUrl += "&timestamp=" + mTimestamp + "&sign=" + sign;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sign;
    }

    /**
     * 获取token
     */
    public void getUploadToken() {
        Map<String, Object> map = new HashMap<String, Object>(11) {
            {
                put("_api_key", mData.pgyConfig.pgyApiKey);
                put("buildType", mData.pgyConfig.pgyBuildType);
                put("oversea", mData.pgyConfig.pgyOversea);
                put("buildInstallType", mData.pgyConfig.pgyBuildInstallType);
                put("buildPassword", mData.pgyConfig.pgyBuildPassword);
                put("buildDescription", mData.pgyConfig.pgyBuildDescription);
                put("buildUpdateDescription", mData.pgyConfig.pgyBuildUpdateDescription);
                put("buildInstallDate", mData.pgyConfig.pgyBuildInstallDate);
                put("buildInstallStartDate", mData.pgyConfig.pgyBuildInstallStartDate);
                put("buildInstallEndDate", mData.pgyConfig.pgyBuildInstallEndDate);
                put("buildChannelShortcut", mData.pgyConfig.pgyBuildChannelShortcut);
            }
        };
        Call<BaseResponseBean<GetUploadTokenBean>> callBack = mApiService.getUploadToken(map);
        try {
            Response<BaseResponseBean<GetUploadTokenBean>> result = callBack.execute();
            printLog(">>>>>>>>>> HttpRequest.getUploadKey：" + result.body());
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
        printLog("运行了uploadApk：" + mUploadToken);
        if (mUploadToken != null) {
            try {
                File file = new File(mData.pgyConfig.apkOutputPath);
                if (file.exists()) {
                    printLog("文件路径：" + file.getAbsolutePath());
                    RequestBody fileBody = RequestBody.create(file, MediaType.parse("multipart/form-data"));

                    Call<BaseResponseBean<Object>> callBack = mApiService.uploadApk(
                            mUploadToken.endpoint,
                            RequestBody.create(mUploadToken.key, MediaType.parse("text/plain")),
                            RequestBody.create(mUploadToken.params.signature, MediaType.parse("text/plain")),
                            RequestBody.create(mUploadToken.params.xToken, MediaType.parse("text/plain")),
                            RequestBody.create(mData.pgyConfig.apkName, MediaType.parse("text/plain")),
                            MultipartBody.Part.createFormData("file", file.getName(), fileBody)
                    );
                    Response<BaseResponseBean<Object>> result = callBack.execute();
                    printLog(">>>>>>>>>> HttpRequest.uploadApk：" + result.body());
                    if (isUpload) {
                        getAppDetailInfo();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void getAppDetailInfo() {
        Map<String, Object> map = new HashMap<String, Object>(3) {
            {
                put("_api_key", mData.pgyConfig.pgyApiKey);
                put("appKey", mData.pgyConfig.pgyAppKey);
                put("buildKey", ""); //Build Key是唯一标识应用的索引ID，可以通过 获取App所有版本取得
            }
        };
        Call<BaseResponseBean<GetAppDetailInfoBean>> callBack = mApiService.getAppDetailInfo(map);
        try {
            Response<BaseResponseBean<GetAppDetailInfoBean>> result = callBack.execute();
            printLog(">>>>>>>>>> HttpRequest.getAppDetailInfo：" + result.body().data);

            if (result.body() != null && result.body().data != null) {
                postToDD();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * ---------------------------------------------------------------------------------------------
     * 消息方式：文本、链接、图片、markdown、跳转
     * ---------------------------------------------------------------------------------------------
     */

    /**
     * 发送文本到钉钉
     */
    public void postToDD() {
        try {
            Response<BaseResponseBean<Object>> result = mApiService.postToDD(mData.ddConfig.ddWebHookUrl, mData.ddContent).execute();
            printLog(">>>>>>>>>> HttpRequest.postToDD：" + result.body());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void printLog(String msg) {
//        System.out.println(msg);
    }
}
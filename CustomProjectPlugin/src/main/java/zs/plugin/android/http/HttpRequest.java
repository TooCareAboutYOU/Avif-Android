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
import java.util.List;
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
import zs.plugin.android.model.response.BaseResponseBean;
import zs.plugin.android.model.response.FirImTokenBean;
import zs.plugin.android.model.response.FirImUploadBean;
import zs.plugin.android.model.response.PgyAppDetailInfoBean;
import zs.plugin.android.model.response.PgyTokenBean;
import zs.plugin.android.model.PluginConfigBean;

/**
 * @author zhangshuai@attrsense.com
 * @date 2022/11/28 18:45
 * @description
 */
public class HttpRequest {

    private static Project mProject;
    private static Gson mGson;
    public static PluginConfigBean mData;
    private static Long mTimestamp;
    private static boolean isUpload = false;

    private HttpLoggingInterceptor mInterceptor = new HttpLoggingInterceptor(s -> {
        printLog("日志：" + s);
        if (s.contains("<-- 201")) {
            printLog("Fir.im请求成功！！");
            isUpload = true;
        }

        if (s.contains("<-- 204")) {
            printLog("蒲公英上传成功！！");
            isUpload = true;
        }
    });
    private OkHttpClient mOkhttpClient;
    private Retrofit mRetrofit;
    private ApiService mApiService;
    private PgyTokenBean mUploadToken;
    private FirImTokenBean mFirImTokenBean;

    private HttpRequest() {
        mInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        mOkhttpClient = new OkHttpClient.Builder().addInterceptor(mInterceptor).build();
        mRetrofit = new Retrofit.Builder()
                .baseUrl(mData.isPgy ? mData.pgyConfig.pgyBaseUrl : mData.firImConfig.firBaseUrl)
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
        mGson = new Gson();
        File file = new File(project.getRootDir().getAbsolutePath() + "/releaseApk.json");
        try {
            JsonReader jsonReader = new JsonReader(new FileReader(file));
            mData = mGson.fromJson(jsonReader, PluginConfigBean.class);
            //拼接webhook
            createSign();
            printLog("输出：" + mData);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }


    /**
     * 获取蒲公英token
     */
    public void getToken() {
        if (mData.isPgy) {
            getPgyToken();
        } else {
            getFirImToken();
        }
    }

    /**
     * 上传apk文件到蒲公英
     */
    public void uploadApk() {
        if (mData.isPgy) {
            uploadApkToPgy();
        } else {
            uploadApkToFirIm();
        }
    }


    /**
     * ---------------------------------------------------------------------------------------------
     *                                          蒲公英接口区
     * ---------------------------------------------------------------------------------------------
     * @description：
     */

    /**
     * 获取蒲公英token
     */
    private void getPgyToken() {
        try {
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
            Call<BaseResponseBean<PgyTokenBean>> callBack = mApiService.getPgyToken(map);
            Response<BaseResponseBean<PgyTokenBean>> result = callBack.execute();
            printLog(">>>>>>>>>> HttpRequest.getUploadKey：" + result.body());
            if (result.body() != null) {
                mUploadToken = result.body().data;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 上传apk文件到蒲公英
     */
    private void uploadApkToPgy() {
        printLog("运行了uploadApk：" + mUploadToken);
        if (mUploadToken != null && mData.apkOutputPath != null && mData.apkOutputPath.length() > 0) {
            try {
                File file = new File(mData.apkOutputPath);
                if (file.exists()) {
                    RequestBody fileBody = RequestBody.create(file, MediaType.parse("multipart/form-data"));
                    MultipartBody.Builder builder = new MultipartBody.Builder();
                    builder.setType(MultipartBody.FORM);
                    builder.addFormDataPart("key", mUploadToken.key);
                    builder.addFormDataPart("signature", mUploadToken.params.signature);
                    builder.addFormDataPart("x-cos-security-token", mUploadToken.params.xToken);
                    builder.addFormDataPart("x-cos-meta-file-name", mData.apkName);
                    builder.addFormDataPart("file", file.getName(), fileBody);
                    List<MultipartBody.Part> parts = builder.build().parts();

                    Call<BaseResponseBean<Object>> callBack = mApiService.uploadApkToPgy(mUploadToken.endpoint, parts);
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

    /**
     * 发版后获取蒲公英版本信息
     */
    public void getAppDetailInfo() {
        try {
            Map<String, Object> map = new HashMap<String, Object>(3) {
                {
                    put("_api_key", mData.pgyConfig.pgyApiKey);
                    put("appKey", mData.pgyConfig.pgyAppKey);
                    put("buildKey", ""); //Build Key是唯一标识应用的索引ID，可以通过 获取App所有版本取得
                }
            };
            Call<BaseResponseBean<PgyAppDetailInfoBean>> callBack = mApiService.getAppDetailInfo(map);
            Response<BaseResponseBean<PgyAppDetailInfoBean>> result = callBack.execute();
            if (result.body() != null && result.body().data != null) {
                printLog(">>>>>>>>>> HttpRequest.getAppDetailInfo：" + result.body().data);
                postToDD();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * ---------------------------------------------------------------------------------------------
     *                                          Fir.im 区
     * ---------------------------------------------------------------------------------------------
     *
     * @description：
     */

    /**
     * 获取Fir.im的token
     */
    private void getFirImToken() {
        try {
            Map<String, String> map = new HashMap<String, String>(3) {
                {
                    put("type", mData.firImConfig.type);
                    put("bundle_id", mData.firImConfig.packageName);
                    put("api_token", mData.firImConfig.apiToken);
                }
            };

            Call<FirImTokenBean> call = mApiService.getFirImToken(map);
            Response<FirImTokenBean> response = call.execute();
            FirImTokenBean result = response.body();
            if (result != null) {
                mFirImTokenBean = result;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 上传apk和icon文件到Firm.im
     */
    private void uploadApkToFirIm() {
        try {
            if (!isUpload) {
                return;
            }
            if (mData.apkOutputPath != null && mData.apkOutputPath.length() > 0) {
                File apkFile = new File(mData.apkOutputPath);

                if (apkFile.exists()) {
                    RequestBody apkFileBody = RequestBody.create(apkFile, MediaType.parse("multipart/form-data"));
                    MultipartBody.Builder builder = new MultipartBody.Builder();
                    builder.setType(MultipartBody.FORM);
                    builder.addFormDataPart("key", mFirImTokenBean.cert.binary.key);
                    builder.addFormDataPart("token", mFirImTokenBean.cert.binary.token);
                    builder.addFormDataPart("x:name", mData.firImConfig.xName);
                    builder.addFormDataPart("x:version", mData.firImConfig.xVersion);
                    builder.addFormDataPart("x:build", mData.firImConfig.xBuild);
                    builder.addFormDataPart("x:changelog", mData.firImConfig.xChangeLog);
                    builder.addFormDataPart("file", apkFile.getName(), apkFileBody);
                    List<MultipartBody.Part> parts = builder.build().parts();
                    Call<FirImUploadBean> call = mApiService.uploadToFirIm(mFirImTokenBean.cert.binary.upload_url, parts);

                    Response<FirImUploadBean> response = call.execute();
                    FirImUploadBean result = response.body();
                    printLog(">>>>>>>>>> uploadApkToFirIm：" + result);

                    if (result != null && result.is_completed) {

                        postToDD();

                        if (mData.firImConfig.icon != null && mData.firImConfig.icon.length() > 0) {
                            File iconFile = new File(mData.firImConfig.icon);
                            if (iconFile.exists()) {
                                RequestBody iconFileBody = RequestBody.create(iconFile, MediaType.parse("image/*"));

                                MultipartBody.Builder iconBuilder = new MultipartBody.Builder();
                                iconBuilder.setType(MultipartBody.FORM);
                                iconBuilder.addFormDataPart("key", mFirImTokenBean.cert.icon.key);
                                iconBuilder.addFormDataPart("token", mFirImTokenBean.cert.icon.token);
                                iconBuilder.addFormDataPart("file", iconFile.getName(), iconFileBody);
                                List<MultipartBody.Part> iconParts = iconBuilder.build().parts();

                                Call<FirImUploadBean> iconCall = mApiService.uploadToFirIm(mFirImTokenBean.cert.icon.upload_url, iconParts);
                                Response<FirImUploadBean> iconResponse = iconCall.execute();
                                printLog(">>>>>>>>>> uploadIconToFirIm：" + iconResponse.body());
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * ---------------------------------------------------------------------------------------------
     *                                          钉钉区
     * ---------------------------------------------------------------------------------------------
     * @description：
     */

    /**
     * 安全配置
     */
    private static String createSign() {
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
     * 发送文本到钉钉
     * {"errcode":0,"errmsg":"ok"}
     */
    public void postToDD() {
        try {
            Response<Object> response = mApiService.postToDD(mData.ddConfig.ddWebHookUrl, mData.ddContent).execute();
            Object result = response.body();
            printLog(">>>>>>>>>> HttpRequest.postToDD：" + result);

        } catch (IOException e) {
            e.printStackTrace();
        }
        isUpload = false;
    }

    private static void printLog(String msg) {
        System.out.println(msg);
    }
}
package zs.plugin.android.api;

import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Url;
import zs.plugin.android.model.DingDingBean;
import zs.plugin.android.model.BaseResponseBean;
import zs.plugin.android.model.GetAppDetailInfoBean;
import zs.plugin.android.model.GetUploadTokenBean;

/**
 * @author zhangshuai@attrsense.com
 * @date 2022/11/28 18:44
 * @description
 */
public interface ApiService {

    /**
     * 获取上传token
     *
     * @param map
     * @return
     */
    @FormUrlEncoded
    @POST("getCOSToken")
    Call<BaseResponseBean<GetUploadTokenBean>> getUploadToken(@FieldMap Map<String, Object> map);

    /**
     * 上传apk文件
     *
     * @param url
     * @param key
     * @param signature
     * @param xToken
     * @param xName
     * @param file
     * @return
     */
    @Multipart
    @POST
    Call<BaseResponseBean<Object>> uploadApk(@Url String url,
                                             @Part("key") RequestBody key,
                                             @Part("signature") RequestBody signature,
                                             @Part("x-cos-security-token") RequestBody xToken,
                                             @Part("x-cos-meta-file-name") RequestBody xName,
                                             @Part MultipartBody.Part file);


    /**
     * 获取应用信息
     *
     * @param map 参数	    类型	     说明
     *            _api_key	String	(必填) API Key 点击获取_api_key
     *            appKey	String	(必填) 表示一个App组的唯一Key。例如，名称为'微信'的App上传了三个版本，那么这三个版本为一个App组，该参数表示这个组的Key。这个值显示在应用详情--应用概述--App Key。
     *            buildKey	String	(选填) Build Key是唯一标识应用的索引ID，可以通过 获取App所有版本取得
     * @return
     */
    @FormUrlEncoded
    @POST("view")
    Call<BaseResponseBean<GetAppDetailInfoBean>> getAppDetailInfo(@FieldMap Map<String, Object> map);


    /**
     * 发送自定义消息形式到钉钉
     *
     * @param url
     * @param body
     * @return
     */
    @POST
    Call<BaseResponseBean<Object>> postToDD(@Url String url, @Body DingDingBean body);

}

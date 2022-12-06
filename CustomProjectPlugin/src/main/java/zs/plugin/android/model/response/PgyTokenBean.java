package zs.plugin.android.model.response;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * @author zhangshuai@attrsense.com
 * @date 2022/11/28 18:43
 * @description
 */
public class PgyTokenBean implements Serializable {
    //key 上传文件存储标识唯一 key
    public String key;
    //上传文件的 URL
    public String endpoint;
    //上传文件需要的参数，包含signature、x-cos-security-token、key
    public ParamsBean params;

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

    public static class ParamsBean implements Serializable {
        public String signature;
        @SerializedName("x-cos-security-token")
        public String xToken;
        public String key;

        @Override
        public String toString() {
            return new Gson().toJson(this);
        }
    }
}
package zs.plugin.android.model.response;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * @author zhangshuai@attrsense.com
 * @date 2022/12/6 15:12
 * @description
 */
public class FirImTokenBean implements Serializable {


    public String user_system_default_download_domain;
    public String id;
    public String type;
    @SerializedName("short")
    public String shortX;
    public String download_domain;
    public Boolean download_domain_https_ready;
    public String app_user_id;
    public String storage;
    public String form_method;
    public CertBean cert;


    public static class CertBean implements Serializable {
        public IconBean icon;
        public BinaryBean binary;
        public MqcBean mqc;
        public String support;
        public String prefix;

        public static class IconBean implements Serializable {
            public String key;
            public String token;
            public String upload_url;
            public CustomHeadersBean custom_headers;

            public static class CustomHeadersBean implements Serializable {
            }


            @Override
            public String toString() {
                return new Gson().toJson(this);
            }
        }

        public static class BinaryBean implements Serializable {
            public String key;
            public String token;
            public String upload_url;
            public CustomHeadersBean custom_headers;

            public static class CustomHeadersBean implements Serializable {
            }


            @Override
            public String toString() {
                return new Gson().toJson(this);
            }
        }

        public static class MqcBean implements Serializable {
            public Integer total;
            public Integer used;
            public Boolean is_mqc_availabled;


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


    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
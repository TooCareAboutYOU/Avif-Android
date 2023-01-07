package cn.andzhang.android.model.response;

import com.google.gson.Gson;

import java.io.Serializable;

/**
 * @author zhangshuai@attrsense.com
 * @date 2022/12/6 16:22
 * @description
 */
public class FirImUploadBean implements Serializable {

    public String download_url;
    public Boolean is_completed;
    public String release_id;

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
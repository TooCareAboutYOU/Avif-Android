package zs.plugin.android.model;

import java.io.Serializable;

/**
 * @author zhangshuai@attrsense.com
 * @date 2022/11/28 18:42
 * @description
 */
public class BaseResponseBean<T> implements Serializable {
    public Integer code;
    public String message;
    public T data;

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("{");
        sb.append("\"code\":")
                .append(code);
        sb.append(",\"message\":\"")
                .append(message).append('\"');
        sb.append(",\"data\":")
                .append(data);
        sb.append('}');
        return sb.toString();
    }
}
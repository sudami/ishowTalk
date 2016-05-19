package com.example.ishow.Xutils3;

import com.example.ishow.Bean.CourseEntry;

import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.common.util.LogUtil;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by MRME on 2016-03-28.
 */
public class XHttpUtils {
    public static XHttpUtils xHttpUtils=null;
    private Callback.Cancelable cancelable;

    /**
     * 获取XHttpUtils实例
     * @return
     */
    public static XHttpUtils getInstace(){

       if (xHttpUtils==null)
           xHttpUtils =new XHttpUtils();

        return xHttpUtils;
    }

    /**
     * 从网络获取数据 get方式
     */
    public  Callback.Cancelable getValue(String url , JSONObject object, final OnHttpCallBack callBack){

        try {

            RequestParams params = new RequestParams(url+ URLEncoder.encode(object.toString(), "UTF-8"));
            LogUtil.e(params.toString());
            cancelable = x.http().get(params, new Callback.CacheCallback<String>() {
                @Override
                public boolean onCache(String result) {
                    return false;
                }

                @Override
                public void onSuccess(String result) {
                    if (callBack != null) {
                        httpCallBack = callBack;
                        httpCallBack.onSuccess(result);
                    }
                    LogUtil.e(result);
                }

                @Override
                public void onError(Throwable ex, boolean isOnCallback) {

                    if (callBack != null) {
                        httpCallBack = callBack;
                        httpCallBack.onError(ex.getMessage());
                    }
                    // LogUtil.e(ex.getMessage());

                }

                @Override
                public void onCancelled(CancelledException cex) {

                }

                @Override
                public void onFinished() {

                }
            });
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
       // LogUtil.e(params.toString());
        return cancelable;
    }

    public OnHttpCallBack httpCallBack;
    public interface OnHttpCallBack<T>{

         void onSuccess(String result);
        void onError(String errorResson);
    }
}

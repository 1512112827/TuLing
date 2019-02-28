package com.example.myapplication;

import android.util.Log;

import com.google.gson.Gson;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PostHttp {
    public static void sendRequestWithOkHttp(final String url,final String text,final Callback callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Gson gs = new Gson();
                String JsonApiKey = "ee72b35870394b48875a745155216871";//转为JSON格式的字符串
                String JsonUserId = "402374";
                try {
                    OkHttpClient client = new OkHttpClient();
                    App.PerceptionBean perceptionBean = new App.PerceptionBean();
                    App.UserInfoBean userInfoBean = new App.UserInfoBean();
                    App.PerceptionBean.InputTextBean inputTextBean = new App.PerceptionBean.InputTextBean();
                    inputTextBean.setText(text);
                    perceptionBean.setInputText(inputTextBean);

                    userInfoBean.setApiKey(JsonApiKey);
                    userInfoBean.setUserId(JsonUserId);

                    String str = "{\"perception\":"+gs.toJson(perceptionBean)+",\"userInfo\":"+gs.toJson(userInfoBean)+"}";
                    MediaType mediaType = MediaType.parse("text/json; charset=utf-8");
                    Request request = new Request.Builder()
                            .url(url)
                            .post(RequestBody.create(mediaType, str))
                            .build();
                    Response response = client.newCall(request).execute();
                    Log.d("ASD","asdasdasd");
                    if(!response.isSuccessful()){
                        Log.d("ASD", "sendRequestWithOkHttp: failed"+response.message());
                    }else{
                        callback.onResponse(response.body().string());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    interface Callback{
        void onResponse(String response);
    }
}

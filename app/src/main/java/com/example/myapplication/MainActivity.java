package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    private List<Msg> msgList = new ArrayList<Msg>();
    private EditText inputText;
    private Button send;
    private RecyclerView msgRecyclerView;
    private MsgAdapter adapter;
    private Button chat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        initMsgs(); // 初始化消息数据

        chat = (Button) findViewById(R.id.chat) ;
        inputText = (EditText) findViewById(R.id.input_text);
        send = (Button) findViewById(R.id.send);
        msgRecyclerView = (RecyclerView) findViewById(R.id.msg_recycler_view);
        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,ChatRecord.class);
                startActivity(intent);
            }
        });


        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        msgRecyclerView.setLayoutManager(layoutManager);
        adapter = new MsgAdapter(msgList);
        msgRecyclerView.setAdapter(adapter);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                String content = inputText.getText().toString();
                if (!"".equals(content)) {
                    Msg msg = new Msg(content, Msg.TYPE_SENT);
                    msgList.add(msg);
                    adapter.notifyItemInserted(msgList.size() - 1); // 当有新消息时，刷新ListView中的显示
                    msgRecyclerView.scrollToPosition(msgList.size() - 1); // 将ListView定位到最后一行
                    inputText.setText(""); // 清空输入框中的内容
                    final Gson gs = new Gson();
                    PostHttp.sendRequestWithOkHttp("http://openapi.tuling123.com/openapi/api/v2", content, new PostHttp.Callback() {
                        @Override
                        public void onResponse(String response) {
                            Log.d("ASD", "onResponse: "+response);
                            Take take = gs.fromJson(response,new TypeToken<Take>(){}.getType());
                            final String value = take.getResults().get(take.getResults().size()-1).getValues().getText();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Msg msg= new Msg(value,Msg.TYPE_RECEIVED);
                                    msgList.add(msg);
                                    adapter.notifyItemInserted(msgList.size()-1);
                                    msgRecyclerView.scrollToPosition(msgList.size() - 1);
                                }
                            });
                            save("机器人："+value);
                        }
                    });

                }
                save("我："+content+"  //  ");
            }
        });
    }

    private void initMsgs() {
        Msg msg1 = new Msg("你好呀！我叫小蜜，很高兴认识你。", Msg.TYPE_RECEIVED);
        msgList.add(msg1);
    }

    public void save(String inputText) {
        FileOutputStream out = null;
        BufferedWriter writer = null;
        try {
            out =openFileOutput("data", Context.MODE_APPEND);
            writer = new BufferedWriter(new OutputStreamWriter(out));
            writer.write(inputText);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
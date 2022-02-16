package com.rangi.nanodet.person;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.baidu.speech.EventListener;
import com.baidu.speech.EventManager;
import com.baidu.speech.EventManagerFactory;
import com.baidu.speech.asr.SpeechConstant;
import com.rangi.nanodet.BaseActivity;
import com.rangi.nanodet.MainActivity;
import com.rangi.nanodet.R;
import com.xuexiang.xui.adapter.simple.XUISimpleAdapter;
import com.xuexiang.xui.utils.DensityUtils;
import com.xuexiang.xui.utils.ResUtils;
import com.xuexiang.xui.utils.SnackbarUtils;
import com.xuexiang.xui.widget.popupwindow.popup.XUIListPopup;
import com.xuexiang.xui.widget.popupwindow.popup.XUIPopup;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class personMsg extends BaseActivity implements EventListener {

    private List<Msg> msgList = new ArrayList<>();
    private EditText input_text;
    private Button bt_send;
    private Button bt_start_recognize;
    private Button ask_place;
    private Button ask_price;
    private Button ask_do;
    private RecyclerView mRecyclerView;
    private MsgAdapter adapter;
    private XUIListPopup wherePopup;
    private XUIListPopup pricePopup;
    private XUIListPopup doPopup;
    Context context = personMsg.this;
    protected Button startBtn;//开始识别  一直不说话会自动停止，需要再次打开
    private EventManager asr;//语音识别核心库

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.msg_person);
        initNavBar(false,"聊天窗口",true);
        fast_choose();
        initView();
        initPermission();
        //初始化EventManager对象
        asr = EventManagerFactory.create(this, "asr");
        //注册自己的输出事件类
        asr.registerListener(this); //  EventListener 中 onEvent方法

        input_text = findViewById(R.id.input);
        bt_send = findViewById(R.id.send);
        mRecyclerView = findViewById(R.id.recycler_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        adapter = new MsgAdapter(msgList);
        mRecyclerView.setAdapter(adapter);
        bt_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String content = input_text.getText().toString();
                if (!"".equals(content)){
                    Msg msg = new Msg(content,Msg.TYPE_RECEIVED);
                    msgList.add(msg);
                    //当有新消息时，调用notifyItemInserted方法刷新listview中的显示
                    adapter.notifyItemInserted(msgList.size()-1);
                    //将listview定位到最后一行
                    mRecyclerView.scrollToPosition(msgList.size()-1);
                    //新消息显示在listview中 清空输入框中的内容
                    input_text.setText("");
                }
            }
        });
        bt_start_recognize = findViewById(R.id.btn_start_recognize);
        bt_start_recognize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                MainActivity.per_org=1;
                startActivityForResult(new Intent(personMsg.this, MainActivity.class), 2);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(data==null){
            return;
        }
        System.out.println("DEBUG:" + requestCode +" " + resultCode +" " + data);
        Bundle MBuddle = data.getExtras();
        String info = MBuddle .getString("date");
        System.out.println("12"+info);
        if (info!=null) {
//            input_text.setText(info);
            Msg msg = new Msg(info,Msg.TYPE_SEND);
            msgList.add(msg);
            //当有新消息时，调用notifyItemInserted方法刷新listview中的显示
            adapter.notifyItemInserted(msgList.size()-1);
            //将listview定位到最后一行
            mRecyclerView.scrollToPosition(msgList.size()-1);
        }
    }

    private void fast_choose(){
        ask_place = (Button) findViewById(R.id.ask_place);
        ask_price=(Button)findViewById(R.id.ask_price);
        ask_do = (Button) findViewById(R.id.ask_do);
        ask_place.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                askwhere();
                wherePopup.setAnimStyle(XUIPopup.ANIM_GROW_FROM_CENTER);
                wherePopup.setPreferredDirection(XUIPopup.DIRECTION_TOP);
                wherePopup.show(view);
//                ask_place.setText("隐藏列表浮层");
            }
        });
        ask_price.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                askprice();
                pricePopup.setAnimStyle(XUIPopup.ANIM_GROW_FROM_CENTER);
                pricePopup.setPreferredDirection(XUIPopup.DIRECTION_TOP);
                pricePopup.show(view);
            }
        });
        ask_do.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                howdo();
                doPopup.setAnimStyle(XUIPopup.ANIM_GROW_FROM_CENTER);
                doPopup.setPreferredDirection(XUIPopup.DIRECTION_TOP);
                doPopup.show(view);
            }
        });
    }

    /**
     * 初始化控件
     */
    private void initView() {
        startBtn = (Button) findViewById(R.id.btn_speech_transform);
        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Map<String, Object> params = new LinkedHashMap<String, Object>();
                String event = null;
                event = SpeechConstant.ASR_START;
                params.put(SpeechConstant.ACCEPT_AUDIO_VOLUME, false);
                String json = null;
                json = new JSONObject(params).toString();
                asr.send(event, json, null, 0, 0);
                SnackbarUtils.Indefinite(view, "请说话......").info()
                        .actionColor(ResUtils.getColor(R.color.xui_config_color_white))
                        .setAction("停止", v -> asr.send(SpeechConstant.ASR_STOP, null, null, 0, 0)).show();
            }
        });
    }

    private void initPermission() {
        String permissions[] = {Manifest.permission.RECORD_AUDIO,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.INTERNET,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };

        ArrayList<String> toApplyList = new ArrayList<String>();
        for (String perm : permissions) {
            if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(this, perm)) {
                toApplyList.add(perm);
            }
        }
        String tmpList[] = new String[toApplyList.size()];
        if (!toApplyList.isEmpty()) {
            ActivityCompat.requestPermissions(this, toApplyList.toArray(tmpList), 123);
        }
    }

    @Override
    public void onEvent(String name, String params, byte[] data, int offset, int length) {
        if (name.equals(SpeechConstant.CALLBACK_EVENT_ASR_PARTIAL)) {
            // 识别相关的结果都在这里
            if (params == null || params.isEmpty()) {
                return;
            }
            if (params.contains("\"final_result\"")) {
                // 一句话的最终识别结果
                String regrex = "\\[(.*?),";  //使用正则表达式抽取我们需要的内容
                Pattern pattern = Pattern.compile(regrex);
                Matcher matcher = pattern.matcher(params);
                if (matcher.find()) {
                    int a  = matcher.group(0).indexOf("[");
                    int b  = matcher.group(0).indexOf(",");
                    input_text.setText(matcher.group(0).substring(a+2,b-3));
                }
            }
        }

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        //发送取消事件
        asr.send(SpeechConstant.ASR_CANCEL, "{}", null, 0, 0);
        //退出事件管理器
        // 必须与registerListener成对出现，否则可能造成内存泄露
        asr.unregisterListener(this);
    }

    private void askwhere() {
        if (wherePopup == null) {
            String[] listItems = new String[]{
                    "请问公交站往哪走？",
                    "请问最近的公共厕所怎么走？",
                    "请问附近的医院怎么走？",
            };
            XUISimpleAdapter adapter = XUISimpleAdapter.create(context, listItems);
            wherePopup = new XUIListPopup(context, adapter);
            wherePopup.create(DensityUtils.dp2px(200), DensityUtils.dp2px(150), (adapterView, view, i, l) -> {
                input_text.setText(listItems[i]);
                wherePopup.dismiss();
            });
        }
    }

    private void askprice() {
        if (pricePopup == null) {
            String[] listItems = new String[]{
                    "请问这一斤多少钱？",
                    "请问这个可以打折吗？",
                    "请问这个东西多少钱？",
                    "可以再便宜点吗？",
            };
            XUISimpleAdapter adapter = XUISimpleAdapter.create(context, listItems);
            pricePopup = new XUIListPopup(context, adapter);
            pricePopup.create(DensityUtils.dp2px(200), DensityUtils.dp2px(150), (adapterView, view, i, l) -> {
                input_text.setText(listItems[i]);
                pricePopup.dismiss();
            });
        }
    }
    private void howdo() {
        if (doPopup == null) {
            String[] listItems = new String[]{
                    "请问我现在应该怎么做？",
                    "是不是这么做",
                    "可以帮我操作一下吗？",
            };
            XUISimpleAdapter adapter = XUISimpleAdapter.create(context, listItems);
            doPopup = new XUIListPopup(context, adapter);
            doPopup.create(DensityUtils.dp2px(200), DensityUtils.dp2px(150), (adapterView, view, i, l) -> {
                input_text.setText(listItems[i]);
                doPopup.dismiss();
            });

        }
    }
}

package com.rangi.nanodet.fragment.news.person;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import com.baidu.speech.EventListener;
import com.baidu.speech.EventManager;
import com.baidu.speech.EventManagerFactory;
import com.baidu.speech.asr.SpeechConstant;
import com.rangi.nanodet.R;
import com.rangi.nanodet.core.BaseFragment;
import com.rangi.nanodet.databinding.FragmentPersonmsgBinding;
import com.rangi.nanodet.fragment.news.RecogeFragment;
import com.xuexiang.xaop.annotation.SingleClick;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xrouter.annotation.AutoWired;
import com.xuexiang.xrouter.launcher.XRouter;
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

@Page
public class personMsg extends BaseFragment<FragmentPersonmsgBinding> implements View.OnClickListener,EventListener {
    public static final String KEY_BACK_DATA = "NULL";
    public static final String KEY_INPUT = "NULL";

    /**
     * 自动注入参数，不能是private
     */
    @AutoWired(name = KEY_BACK_DATA)
    String back_input;
    @AutoWired(name = KEY_INPUT)
    String recoge_input;


    @NonNull
    @Override
    protected FragmentPersonmsgBinding viewBindingInflate(LayoutInflater inflater, ViewGroup container) {
        return FragmentPersonmsgBinding.inflate(inflater, container, false);
    }

    @Override
    protected void initArgs() {
        // 自动注入参数必须在initArgs里进行注入
        XRouter.getInstance().inject(this);
    }


    @Override
    protected String getPageTitle() {
        String title="聊天窗口";
        return title;
    }

    private List<Msg> msgList = new ArrayList<>();
    private EditText input_text;
    private RecyclerView mRecyclerView;
    private XUIListPopup wherePopup;
    private XUIListPopup pricePopup;
    private XUIListPopup doPopup;
    protected Button startBtn;//开始识别  一直不说话会自动停止，需要再次打开
    private EventManager asr;//语音识别核心库
    MsgAdapter adapter;

    @Override
    protected void initViews() {
        input_text = findViewById(R.id.input);
        adapter = new MsgAdapter(msgList);
        mRecyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setAdapter(adapter);
        init_chat();

    }
    @Override
    protected void initListeners() {
        binding.askPlace.setOnClickListener((View.OnClickListener) this);
        binding.askPlace.setOnClickListener((View.OnClickListener) this);
        binding.askDo.setOnClickListener((View.OnClickListener) this);
        binding.send.setOnClickListener((View.OnClickListener) this);
        binding.btnHeart.setOnClickListener((View.OnClickListener) this);
        binding.btnStartRecognize.setOnClickListener((View.OnClickListener) this);
        binding.btnSpeechTransform.setOnClickListener((View.OnClickListener) this);
    }

    @SingleClick
    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.askPlace) {
            askwhere();
            wherePopup.setAnimStyle(XUIPopup.ANIM_GROW_FROM_CENTER);
            wherePopup.setPreferredDirection(XUIPopup.DIRECTION_TOP);
            wherePopup.show(view);
//                ask_place.setText("隐藏列表浮层");
        }else if (id == R.id.askPrice) {
            askprice();
            pricePopup.setAnimStyle(XUIPopup.ANIM_GROW_FROM_CENTER);
            pricePopup.setPreferredDirection(XUIPopup.DIRECTION_TOP);
            pricePopup.show(view);
        }else if (id == R.id.askDo){
            howdo();
            doPopup.setAnimStyle(XUIPopup.ANIM_GROW_FROM_CENTER);
            doPopup.setPreferredDirection(XUIPopup.DIRECTION_TOP);
            doPopup.show(view);
        }else if (id==R.id.btnSpeechTransform){
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
        }else if (id==R.id.send){
            String content = input_text.getText().toString();
                if (!"".equals(content)){
                    Msg msg = new Msg(content,Msg.TYPE_RECEIVED);
                    msgList.add(msg);
                    //当有新消息时，调用notifyItemInserted方法刷新listview中的显示
                    adapter.notifyItemInserted(msgList.size()-1);
                    //将listview定位到最后一行
                    binding.recyclerView.scrollToPosition(msgList.size()-1);
                    //新消息显示在listview中 清空输入框中的内容
                    input_text.setText("");
                }
        }else if (id==R.id.btnStartRecognize){
            openPageForResult(RecogeFragment.class, null, 200);
        }
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initPermission();
        //初始化EventManager对象
        asr = EventManagerFactory.create(getContext(), "asr");
        //注册自己的输出事件类
        asr.registerListener(this); //  EventListener 中 onEvent方法

    }

    private void init_chat(){
        if (recoge_input!=null) {
            //            input_text.setText(info);
            Msg msg = new Msg(recoge_input,Msg.TYPE_SEND);
            msgList.add(msg);
            //当有新消息时，调用notifyItemInserted方法刷新listview中的显示
            adapter.notifyItemInserted(msgList.size()-1);
            //将listview定位到最后一行
            binding.recyclerView.scrollToPosition(msgList.size()-1);
        }
    }




    private void initPermission() {
        String permissions[] = {Manifest.permission.RECORD_AUDIO,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.INTERNET,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };

        ArrayList<String> toApplyList = new ArrayList<String>();
        for (String perm : permissions) {
            if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(getContext(), perm)) {
                toApplyList.add(perm);
            }
        }
        String tmpList[] = new String[toApplyList.size()];
        if (!toApplyList.isEmpty()) {
            ActivityCompat.requestPermissions(getActivity(), toApplyList.toArray(tmpList), 123);
        }
    }

    @Override
    public void onEvent(String name, String params, byte[] data, int offset, int length) {
        System.out.println("onEvent");
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
    public void onDestroy() {
        super.onDestroy();
        System.out.println("onDestroy");
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
            XUISimpleAdapter adapter = XUISimpleAdapter.create(getContext(), listItems);
            wherePopup = new XUIListPopup(getContext(), adapter);
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
            XUISimpleAdapter adapter = XUISimpleAdapter.create(getContext(), listItems);
            pricePopup = new XUIListPopup(getContext(), adapter);
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
            XUISimpleAdapter adapter = XUISimpleAdapter.create(getContext(), listItems);
            doPopup = new XUIListPopup(getContext(), adapter);
            doPopup.create(DensityUtils.dp2px(200), DensityUtils.dp2px(150), (adapterView, view, i, l) -> {
                input_text.setText(listItems[i]);
                doPopup.dismiss();
            });

        }
    }

    @Override
    public void onFragmentResult(int requestCode, int resultCode, Intent data) {
        super.onFragmentResult(requestCode, resultCode, data);
        if(data==null){
            return;
        }
        Bundle extras = data.getExtras();
        String info = extras.getString(RecogeFragment.KEY_BACK_DATA);
        System.out.println("12"+info);
        if (info!=null) {
            Msg msg = new Msg(info, Msg.TYPE_SEND);
            msgList.add(msg);
            //当有新消息时，调用notifyItemInserted方法刷新listview中的显示
            adapter.notifyItemInserted(msgList.size() - 1);
            //将listview定位到最后一行
            mRecyclerView.scrollToPosition(msgList.size() - 1);
        }
    }
}

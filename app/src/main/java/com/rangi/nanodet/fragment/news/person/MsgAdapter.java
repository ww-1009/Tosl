package com.rangi.nanodet.fragment.news.person;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.rangi.nanodet.R;
import java.util.List;
public class MsgAdapter extends RecyclerView.Adapter<MsgAdapter.ViewHolder>{

    private List<Msg> mMsgList;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.msg_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Msg msg = mMsgList.get(position);
        //判断消息类型 如果是收到的消息 显示左边的消息布局，隐藏右边的消息布局
        if (msg.getType() == Msg.TYPE_RECEIVED){
            holder.received_layout.setVisibility(View.VISIBLE);
            holder.send_layout.setVisibility(View.GONE);
            holder.leftMsg.setText(msg.getContent());
            //判断消息类型 如果是发送的消息 显示右边的消息布局，隐藏左边的消息布局
        }else if (msg.getType() == Msg.TYPE_SEND){
            holder.send_layout.setVisibility(View.VISIBLE);
            holder.received_layout.setVisibility(View.GONE);
            holder.rightMsg.setText(msg.getContent());
        }
    }

    @Override
    public int getItemCount() {
        return mMsgList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout received_layout;
        LinearLayout send_layout;
        TextView leftMsg;
        TextView rightMsg;
        public ViewHolder(View view) {
            super(view);
            received_layout = view.findViewById(R.id.left_layout);
            send_layout =view.findViewById(R.id.right_layout);
            leftMsg =view.findViewById(R.id.received_msg);
            rightMsg =view.findViewById(R.id.send_msg);
        }
    }
    public MsgAdapter(List<Msg> msgList){
        mMsgList = msgList;
    }
}

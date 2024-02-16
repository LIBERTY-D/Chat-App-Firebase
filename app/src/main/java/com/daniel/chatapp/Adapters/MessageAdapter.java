package com.daniel.chatapp.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.daniel.chatapp.Models.Message;
import com.daniel.chatapp.R;

import java.util.List;

public class MessageAdapter  extends  RecyclerView.Adapter<MessageAdapter.MessageView>{


    private int send;
    private int recv;

    boolean status;
    String username;

    List<Message> message;

    public MessageAdapter( String username, List<Message> message) {
        this.username = username;
        this.message = message;
        send = 1;
        recv = 2;
        status =false;
    }

    @NonNull
    @Override
    public MessageView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view=null;
        if(viewType==send){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.send_chat,parent,false);
        }else if (viewType==recv){

            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.receive_chat,parent,false);


        }

        return new MessageView(view);
    };


    @Override
    public int getItemViewType(int position) {
        if (message.get(position).getFrom().equals(username)){
           status=true;

           return send;
        }else{
            status = false;
            return recv;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull MessageView holder, int position) {
               Message msg =  message.get(position);
               holder.message.setText(msg.getMessage());
    }

    @Override
    public int getItemCount() {
        return message.size();
    }

    public class MessageView extends RecyclerView.ViewHolder {

        TextView message;
        public MessageView(@NonNull View itemView) {
            super(itemView);
            if(status){
                message =  itemView.findViewById(R.id.messageSend);
            }else{
                message =  itemView.findViewById(R.id.messageRcv);
            }


        }
    }
}

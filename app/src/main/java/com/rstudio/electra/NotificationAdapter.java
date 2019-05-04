package com.rstudio.electra;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.MyViewHolder> {

    private Context context;
    private List<Request> requestList;

    private OnItemClickListener mListener;


    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView time, message, status, type;


        public MyViewHolder(View view, final OnItemClickListener listener) {
            super(view);
            status = view.findViewById(R.id.tv_statusRequestUser);
            time = view.findViewById(R.id.tv_Date_ReqLayout);
            type = view.findViewById(R.id.tv_reqType_Not);
            message = view.findViewById(R.id.tv_Description_ReqLayout);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(position);
                        }
                    }
                }
            });
        }
    }


    public NotificationAdapter(List<Request> requestList, Context context) {
        this.context = context;
        this.requestList = requestList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.notification_layout, parent, false);

        return new MyViewHolder(itemView, mListener);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Request request = requestList.get(position);
        if(request.getSenderMessage() == null)
            holder.message.setVisibility(View.GONE);
        holder.message.setText(request.getSenderMessage());
        holder.time.setText(request.getTime());
        holder.type.setText(request.getRequesttype());
        holder.status.setText(request.getStatus());
    }

    @Override
    public int getItemCount() {
        return requestList.size();
    }
}

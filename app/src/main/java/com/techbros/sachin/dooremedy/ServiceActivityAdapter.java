package com.techbros.sachin.dooremedy;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by saini on 12-Apr-17.
 */

public class ServiceActivityAdapter extends RecyclerView.Adapter<ServiceActivityAdapter.OurHolder> {

    public class OurHolder extends RecyclerView.ViewHolder {
        ImageView serviceImage;
        TextView serviceName;
        TextView serviceId;
        RelativeLayout relativeLayout;
        public OurHolder(View itemView) {
            super(itemView);
            serviceId = (TextView) itemView.findViewById(R.id.tv_service_id);
            serviceName = (TextView) itemView.findViewById(R.id.tv_service);
            serviceImage = (ImageView) itemView.findViewById(R.id.iv_img);
            relativeLayout = (RelativeLayout) itemView.findViewById(R.id.service_item);
        }
    }

    Context mContext;
    ArrayList<ServiceListItem> data;

    public ServiceActivityAdapter(ArrayList<ServiceListItem> data, Context mContext ) {
        this.data = data;
        this.mContext = mContext;
    }

    @Override
    public OurHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext)
                .inflate(R.layout.service_list,parent,false);
        return new OurHolder(v);
    }

    @Override
    public void onBindViewHolder(OurHolder holder, int position) {
        final ServiceListItem h = data.get(position);
        holder.serviceName.setText(h.getName());
        //holder.serviceId.setText(h.getId());
        Picasso.with(mContext).load(h.getImage()).into(holder.serviceImage);
        holder.relativeLayout.setOnClickListener(new RecyclerView.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(mContext,EmployeeActivity.class);
                intent.putExtra("serviceId",h.getId());
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }


}

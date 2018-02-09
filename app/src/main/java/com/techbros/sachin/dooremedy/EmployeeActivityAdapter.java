package com.techbros.sachin.dooremedy;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
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

public class EmployeeActivityAdapter extends RecyclerView.Adapter<EmployeeActivityAdapter.OurHolder> {

    public class OurHolder extends RecyclerView.ViewHolder {
        ImageView employeeImage;
        TextView employeName;
        TextView employeeDetail;
        Button hireButton;
        public OurHolder(View itemView) {
            super(itemView);
            employeName = (TextView) itemView.findViewById(R.id.tv_employee);
            employeeImage = (ImageView) itemView.findViewById(R.id.iv_empImg);
            employeeDetail = (TextView) itemView.findViewById(R.id.tv_employee_details);
            hireButton = (Button) itemView.findViewById(R.id.hireButton);
        }
    }

    Context mContext;
    ArrayList<EmployeeListItem> data;

    public EmployeeActivityAdapter(ArrayList<EmployeeListItem> data, Context mContext ) {
        this.data = data;
        this.mContext = mContext;
    }

    @Override
    public OurHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext)
                .inflate(R.layout.employee_list_row,parent,false);
        return new OurHolder(v);
    }

    @Override
    public void onBindViewHolder(OurHolder holder, int position) {
        final EmployeeListItem h = data.get(position);
        holder.employeName.setText(h.getName());
        holder.employeeDetail.setText(h.getId());
        Picasso.with(mContext).load(h.getImage()).into(holder.employeeImage);
        holder.hireButton.setOnClickListener(new RecyclerView.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(mContext,QueryActivity.class);
                intent.putExtra("employeeName",h.getName());
                intent.putExtra("employeeDetail",h.getId());
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }


}

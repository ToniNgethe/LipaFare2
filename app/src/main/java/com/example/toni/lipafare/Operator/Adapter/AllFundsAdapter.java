package com.example.toni.lipafare.Operator.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.toni.lipafare.Operator.AllFundsActivity;
import com.example.toni.lipafare.Operator.Model.AllFundsModel;
import com.example.toni.lipafare.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by toni on 6/19/17.
 */

public class AllFundsAdapter extends RecyclerView.Adapter<AllFundsAdapter.AllFundsViewHolder> {

    private List<AllFundsModel> mList = new ArrayList<>();
    private Context ctx;

    public AllFundsAdapter(Context ctx, List<AllFundsModel> mList) {
        this.mList = mList;
        this.ctx = ctx;
        notifyDataSetChanged();
    }

    @Override
    public AllFundsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new AllFundsViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.allfundsactivity_row,parent,false));
    }

    @Override
    public void onBindViewHolder(AllFundsViewHolder holder, int position) {

        if (holder != null){
            AllFundsModel m = mList.get(position);
            holder.setSits(m.getSits());
            holder.setTotal(m.getTotal());
        }

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public static class AllFundsViewHolder extends RecyclerView.ViewHolder{

        private View mView;

        public AllFundsViewHolder(View itemView) {
            super(itemView);
            this.mView = itemView;
        }

        public void setSits(int a){
            TextView textView = (TextView) mView.findViewById(R.id.tv_sits_allfunds);
            textView.setText(String.valueOf(a) + " sits");
        }

        public void setTotal(double a){
            TextView textView = (TextView) mView.findViewById(R.id.tv_total_allfunds);
            textView.setText(String.valueOf(a) + " ksh");
        }
    }
}

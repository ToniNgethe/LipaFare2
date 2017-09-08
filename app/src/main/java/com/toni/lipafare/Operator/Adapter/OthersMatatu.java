package com.toni.lipafare.Operator.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.toni.lipafare.Operator.Dialog_mat.MatatuDetails;
import com.toni.lipafare.Operator.Model.Matatu;
import com.toni.lipafare.R;

import java.util.List;

/**
 * Created by toni on 5/30/17.
 */

public class OthersMatatu extends RecyclerView.Adapter<OthersMatatu.OtherViewHolder>{
    private Context ctx;
    private List<Matatu> matList;
    private List<String> keyList;

    public OthersMatatu(Context ctx, List<Matatu> matatuList, List<String> keys) {
        this.ctx = ctx;
        this.matList = matatuList;
        this.keyList = keys;
    }

    @Override
    public OthersMatatu.OtherViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_matatus,parent, false);
        return new OtherViewHolder(ctx,mView);
    }


    @Override
    public void onBindViewHolder(OtherViewHolder holder, int position) {

        if (holder !=null){

            Matatu matatuModel = matList.get(position);
            final String key = keyList.get(position);

            Log.d("sasdsad", key);

            holder.setPlate(matatuModel.getPlate());
            holder.setMatatuImage(matatuModel.getLogo());
            holder.setName(matatuModel.getName());

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    MatatuDetails matatuDetails = new MatatuDetails(ctx, key);
                    matatuDetails.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    matatuDetails.setCanceledOnTouchOutside(false);
                    matatuDetails.show();
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return matList.size();
    }

    public static class OtherViewHolder extends RecyclerView.ViewHolder{

        private View mView;
        private Context ctx;

        public OtherViewHolder(Context ctx, View itemView) {
            super(itemView);

            this.ctx = ctx;
            mView = itemView;
        }

        public void setPlate(String plate){
            TextView textView = (TextView) mView.findViewById(R.id.tv_rowmatatu);
            textView.setText(plate);
        }
        public void setName(String nam){
            TextView textView = (TextView) mView.findViewById(R.id.tv_mataturow_name);
            textView.setText(nam);
        }


        public void setMatatuImage(final String url){

            new Handler().post(new Runnable() {
                @Override
                public void run() {

                    ImageView iv = (ImageView) mView.findViewById(R.id.iv_rowmatatu);
                    Glide.with(ctx).load(url)
                            .crossFade()
                            .thumbnail(0.5f)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .error(R.mipmap.error_network)
                            .into(iv);
                }
            });
        }
    }
}

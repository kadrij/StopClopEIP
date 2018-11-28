package com.eip.stopclopeip.Advice;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.eip.stopclopeip.R;

import java.util.ArrayList;
import java.util.List;

public class AdviceAdapter extends RecyclerView.Adapter<AdviceAdapter.AdviceViewHolder>{
    private Context mContext;
    private List<AdviceSample> AdviceList;

    public AdviceAdapter(Context mContext, List<AdviceSample> adviceList) {
        this.mContext = mContext;
        AdviceList = adviceList;
    }

    @Override
    public AdviceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.row, parent, false);
        return new AdviceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final AdviceViewHolder holder, int position) {
        final AdviceSample adviceSample = AdviceList.get(position);
        holder.mTitle.setText(adviceSample.getTitle());
        holder.mComment.setText(adviceSample.getComment());
    }

    @Override
    public int getItemCount() {
        return AdviceList.size();
    }

    public void setFilter(List<AdviceSample> adviceList) {
        AdviceList=new ArrayList<>();
        AdviceList.addAll(adviceList);
        notifyDataSetChanged();
    }

    class AdviceViewHolder extends RecyclerView.ViewHolder {
        TextView mTitle;
        TextView mComment;

        public AdviceViewHolder(View itemView) {
            super(itemView);
            mTitle = itemView.findViewById(R.id.advice_title);
            mComment = itemView.findViewById(R.id.advice_comment);
        }
    }
}

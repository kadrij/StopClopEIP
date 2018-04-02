package com.eip.stopclopeip;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class AdviceAdapter extends RecyclerView.Adapter<AdviceAdapter.AdviceViewHolder>{
    private Context mContext;
    private List<Advice> AdviceList;

    public AdviceAdapter(Context mContext, List<Advice> adviceList) {
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
    public void onBindViewHolder(AdviceViewHolder holder, int position) {
        Advice advice = AdviceList.get(position);
        holder.mTitle.setText(advice.getTitle());
        holder.mContent.setText(advice.getContent());
    }

    @Override
    public int getItemCount() {
        return AdviceList.size();
    }

    public void setFilter(List<Advice> adviceList) {
        AdviceList=new ArrayList<>();
        AdviceList.addAll(adviceList);
        notifyDataSetChanged();
    }

    class AdviceViewHolder extends RecyclerView.ViewHolder {
        TextView mTitle;
        TextView mContent;

        public AdviceViewHolder(View itemView) {
            super(itemView);
            mTitle = itemView.findViewById(R.id.title);
            mContent = itemView.findViewById(R.id.content);
        }
    }
}

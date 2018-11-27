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
        holder.mContent.setText(adviceSample.getContent());
        holder.mLikeCount.setText("" + (adviceSample.getLikes()));
        holder.mTag.setText(adviceSample.getTag());
        holder.mLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!adviceSample.isLiked()) {
                    holder.mLike.setImageResource(R.drawable.ic_thumb_up_liked_24dp);
                    holder.mLikeCount.setText("" + (adviceSample.getLikes() + 1));
                    adviceSample.setLiked(true);
                }
                else {
                    holder.mLike.setImageResource(R.drawable.ic_thumb_up_not_licked_24dp);
                    holder.mLikeCount.setText("" + (adviceSample.getLikes()));
                    adviceSample.setLiked(false);
                }
            }
        });
        if (adviceSample.isLiked())
            holder.mLike.setImageResource(R.drawable.ic_thumb_up_liked_24dp);
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
        CardView mCard;
        TextView mContent;
        TextView mLikeCount;
        TextView mTag;
        ImageView mLike;

        public AdviceViewHolder(View itemView) {
            super(itemView);
            mCard = itemView.findViewById(R.id.advice_card);
            mContent = itemView.findViewById(R.id.content);
            mLikeCount = itemView.findViewById(R.id.like_counter);
            mTag = itemView.findViewById(R.id.tag_content);
            mLike = itemView.findViewById(R.id.like_button);
        }
    }
}

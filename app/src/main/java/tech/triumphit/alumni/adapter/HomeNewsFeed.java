package tech.triumphit.alumni.adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import tech.triumphit.alumni.R;
import tech.triumphit.alumni.databinding.RowFeedsBinding;

/**
 * Created by Tushar on 10/10/2016.
 */

public class HomeNewsFeed extends BaseAdapter {

    ArrayList name, date, propic, postPic, postText, totalLike, likerProPic;
    Context context;

    public HomeNewsFeed(Context context, ArrayList name, ArrayList postPic, ArrayList date, ArrayList propic, ArrayList postText, ArrayList totalLike, ArrayList likerProPic){
        this.context = context;
        this.name = name;
        this.date = date;
        this.propic = propic;
        this.postPic = postPic;
        this.postText = postText;
        this.totalLike = totalLike;
        this.likerProPic = likerProPic;
    }

    @Override
    public int getCount() {
        return name.size();
    }

    @Override
    public Object getItem(int position) {
        return name.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Holder holder;
        if(convertView == null){
            holder = new Holder();
            convertView = holder.rowFeedsBinding.getRoot();
            convertView.setTag(holder);
        }else{
            holder = (Holder) convertView.getTag();
        }
        holder.rowFeedsBinding.textView23.setText("" + name.get(position));
        holder.rowFeedsBinding.textView25.setText("" + date.get(position));
        holder.rowFeedsBinding.textView26.setText("" + postText.get(position));
        holder.rowFeedsBinding.textView27.setText("" + totalLike.get(position));
        Glide.with(context).load("" + propic.get(position)).diskCacheStrategy(DiskCacheStrategy.ALL).into(holder.rowFeedsBinding.profileImage);
        Log.e("propic", "" + propic.get(position));
        Glide.with(context).load("" + likerProPic.get(position)).diskCacheStrategy(DiskCacheStrategy.ALL).centerCrop().placeholder(R.drawable.placeholder).into(holder.rowFeedsBinding.profileImage2);
        Glide.with(context).load("" + postPic.get(position)).centerCrop().diskCacheStrategy(DiskCacheStrategy.ALL).listener(new RequestListener<String, GlideDrawable>() {
            @Override
            public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                holder.rowFeedsBinding.imageView.setBackgroundColor(Color.BLACK);
                holder.rowFeedsBinding.textView26.setTextColor(Color.parseColor("#ffffff"));
                return false;
            }

            @Override
            public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                return false;
            }
        }).into(holder.rowFeedsBinding.imageView);
        return convertView;
    }

    class Holder{
        RowFeedsBinding rowFeedsBinding;
        Holder(){
            rowFeedsBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.row_feeds, null, true);
        }
    }
}

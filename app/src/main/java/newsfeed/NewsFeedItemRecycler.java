package newsfeed;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.administrator.renren_tarb.R;

import java.util.ArrayList;
import java.util.List;

import tool.DisplayUtil;
import view.PhotoActivity;

/**
 * Created by Administrator on 2016/4/21.
 */
public class NewsFeedItemRecycler extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<String> mList;
    private LayoutInflater mInflater;

    public NewsFeedItemRecycler(Context context, List<String> list) {
        mContext = context;
        mList = new ArrayList<>();
        mList.addAll(list);
        mInflater = LayoutInflater.from(mContext);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ItemHolder holder = new ItemHolder(mInflater.inflate(R.layout.tab_item_view, parent, false));
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final ItemHolder ivHolder = (ItemHolder) holder;
        int w = (mContext.getResources().getDisplayMetrics().widthPixels - DisplayUtil.dip2px(2) * 4 - DisplayUtil.dip2px(60)) / 3;

        Glide.with(mContext).load(mList.get(position))
                .centerCrop()
                .override(w, w).diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true).crossFade()
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .placeholder(R.drawable.group_bg_album_image).dontAnimate()
                .into(ivHolder.iv);
        ivHolder.iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, PhotoActivity.class);
                intent.putExtra("imagePath", mList.get(position));
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class ItemHolder extends RecyclerView.ViewHolder {
        ImageView iv;

        public ItemHolder(View itemView) {
            super(itemView);
            iv = (ImageView) itemView.findViewById(R.id.tab_image);
        }
    }
}
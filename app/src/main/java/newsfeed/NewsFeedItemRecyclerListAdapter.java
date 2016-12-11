package newsfeed;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.administrator.renren_tarb.R;

import java.util.ArrayList;
import java.util.List;

import bmob.ServiceHelper;
import publisher.FullyGridLayoutManager;
import tool.DisplayUtil;
import tool.ImageWorker;
import tool.StringHelper;
import view.PhotoActivity;

/**
 * Created by yapeng.tian on 2016/5/20.
 */
public class NewsFeedItemRecyclerListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<NewsFeedItem> mList;
    private LayoutInflater mInflater;

    public NewsFeedItemRecyclerListAdapter(Context context) {
        mContext = context;
        mList = new ArrayList<>();
        mInflater = LayoutInflater.from(mContext);
    }

    public void setData(List<NewsFeedItem> list) {
        mList.clear();
        mList.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ItemHolder holder = new ItemHolder(mInflater.inflate(R.layout.newfeed_item, parent, false));
        return holder;
    }

    public void addItem(NewsFeedItem item) {
        mList.add(0, item);
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder hold, final int position) {
        NewsFeedItem item = mList.get(position);
        final ItemHolder holder = (ItemHolder) hold;
        holder.time.setText(item.time);
        holder.name.setText(item.name);
        holder.url.setImageResource(R.drawable.common_default_head_black);
        holder.recyclerView.setVisibility(View.GONE);
        if (item.txt.equals("")) {
            holder.txt.setVisibility(View.GONE);
        } else {
            holder.txt.setVisibility(View.VISIBLE);
            StringHelper.parseEmotion(mContext, holder.txt, item.txt);
        }
        if (item.url != null && !item.url.equals("")) {
            final List<String> list = StringHelper.parseUrl(item.url);
                holder.recyclerView.setHasFixedSize(true);
                NewsFeedItemRecycler mAdapter = new NewsFeedItemRecycler(mContext, list);
                FullyGridLayoutManager layoutManager = new FullyGridLayoutManager(mContext, 3);
                holder.recyclerView.setLayoutManager(layoutManager);
                holder.recyclerView.setAdapter(mAdapter);
                int w = (mContext.getResources().getDisplayMetrics().widthPixels - DisplayUtil.dip2px(2) * 4 - DisplayUtil.dip2px(60)) / 3;
                int high = w * (Math.abs(list.size() - 1) / 3 + 1);
                holder.recyclerView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, high));
                holder.recyclerView.setVisibility(View.VISIBLE);
        }
        ServiceHelper.findUrlByName(mContext, item.name, holder.url);
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
        TextView name;
        TextView time;
        TextView txt;
        ImageView url;
        RecyclerView recyclerView;

        public ItemHolder(View convertView) {
            super(convertView);
            name = (TextView) convertView.findViewById(R.id.name);
            txt = (TextView) convertView.findViewById(R.id.txt);
            url = (ImageView) convertView.findViewById(R.id.head);
            time = (TextView) convertView.findViewById(R.id.time);
            recyclerView = (RecyclerView) convertView.findViewById(R.id.recycler_view);
        }
    }
}
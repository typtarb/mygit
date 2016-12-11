package newsfeed;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.administrator.renren_tarb.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import base.BaseFragment;
import de.greenrobot.event.EventBus;
import publisher.FullyGridLayoutManager;
import tool.DisplayUtil;
import tool.ImageWorker;
import tool.StringHelper;
import tool.ToolUtil;
import view.RefreshLayout;

/**
 * Created by Administrator on 2016/4/14.
 */
public class NewsFeedContentFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener,
        RefreshLayout.OnLoadListener, ImageWorker.LoadImageInterface {
    private Context mContext;
    private View news;
    private RefreshLayout swipeLayout;
    private RecyclerView recyclerList;
    private LayoutInflater mInflater;
    private NewsFeedItemRecyclerListAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
        mInflater = LayoutInflater.from(mContext);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
        doWork();

    }

    private void initView() {
        news = mInflater.inflate(R.layout.news, null);
        swipeLayout = (RefreshLayout) news.findViewById(R.id.swipe_container);
        recyclerList = (RecyclerView) news.findViewById(R.id.recycler_list);
        recyclerList.setHasFixedSize(true);
        FullyGridLayoutManager layoutManager = new FullyGridLayoutManager(mContext, 1);
        recyclerList.setLayoutManager(layoutManager);
        mAdapter = new NewsFeedItemRecyclerListAdapter(mContext);
        recyclerList.setAdapter(mAdapter);
        int high = DisplayUtil.getSreenSize(mContext, false) - DisplayUtil.getStatusBarHeight() - DisplayUtil.dip2px(50) * 2;
        rootLayout.addView(news, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, high));
        swipeLayout.setColorSchemeResources(R.color.color_bule2,
                R.color.color_bule,
                R.color.color_bule2,
                R.color.color_bule3);
        swipeLayout.setOnRefreshListener(this);
        swipeLayout.setOnLoadListener(this);
        swipeLayout.post(new Thread(new Runnable() {
            @Override
            public void run() {
                swipeLayout.setRefreshing(true);
            }
        }));
    }

    private void doWork() {
        EventBus.getDefault().register(this);
        ImageWorker.setLoadImageInterface(this);
        onRefresh();
    }

    @Override
    public void onSuccess(List<NewsFeedBmobObject> list) {
        final List<NewsFeedItem> newsFeedList = new ArrayList<>();
        newsFeedList.clear();
        for (NewsFeedBmobObject item : list) {
            NewsFeedItem itemN = new NewsFeedItem();
            itemN.txt = item.getTxt();
            itemN.time = StringHelper.parseTime(item.getTime());
            itemN.url = item.getUrl();
            itemN.type = item.getType();
            itemN.name = item.getName();
            newsFeedList.add(itemN);
        }
        mAdapter.setData(newsFeedList);
    }

    public void onEventMainThread(NewsFeedItem item) {
        item.time = "刚刚";
        item.url = StringHelper.parseUrl(item.imageList);
        mAdapter.addItem(item);
    }

    @Override
    public void onRefresh() {
        ToolUtil.checkNet(mContext);
        swipeLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                ImageWorker.getImage(mContext);
                swipeLayout.setRefreshing(false);
            }
        }, 1000);
    }

    @Override
    public void onLoad() {
    }

    @Override
    public View getMidView() {
        setTitle("新鲜事");
        return super.getMidView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
package account;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.example.administrator.renren_tarb.R;

import java.util.Collections;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;
import discover.AccountAdapter;
import publisher.FullyGridLayoutManager;
import title.TitleBar;
import title.TitleInterface;
import title.TitleUtil;
import tool.SettingManager;
import tool.ToolUtil;
import view.RefreshLayout;

/**
 * Created by yapeng.tian on 2016/5/24.
 */
public class MyAccountActivity extends Activity implements SwipeRefreshLayout.OnRefreshListener,
        RefreshLayout.OnLoadListener, TitleInterface {

    private RefreshLayout swipeLayout;
    private RecyclerView recyclerList;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.news);
        mContext = MyAccountActivity.this;
        initView();
        doWork();
    }

    private void initView() {
        swipeLayout = (RefreshLayout) findViewById(R.id.swipe_container);
        recyclerList = (RecyclerView) findViewById(R.id.recycler_list);
    }

    private void doWork() {
        TitleBar bar = (TitleBar) findViewById(R.id.title_bar);
        bar.setVisibility(View.VISIBLE);
        bar.setTitle(this);
        recyclerList.setHasFixedSize(true);
        FullyGridLayoutManager layoutManager = new FullyGridLayoutManager(mContext, 1);
        recyclerList.setLayoutManager(layoutManager);
        swipeLayout.setOnRefreshListener(this);
        swipeLayout.setOnLoadListener(this);
        swipeLayout.setColorSchemeResources(R.color.color_bule2,
                R.color.color_bule,
                R.color.color_bule2,
                R.color.color_bule3);
        swipeLayout.post(new Thread(new Runnable() {
            @Override
            public void run() {
                swipeLayout.setRefreshing(true);
            }
        }));
        onRefresh();
    }

    @Override
    public void onLoad() {

    }

    @Override
    public void onRefresh() {
        ToolUtil.checkNet(mContext);
        swipeLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                BmobQuery<AccountBmob> query = new BmobQuery<AccountBmob>();
                String name = SettingManager.getInstance().getName();
                Log.d("AccountBmob", name);
                query.addWhereMatches("personnel", "*" + name + "*");
                query.addWhereContains("personnel", name);
                query.findObjects(mContext, new FindListener<AccountBmob>() {
                    @Override
                    public void onSuccess(List<AccountBmob> list) {
                        Collections.reverse(list);
                        AccountAdapter mAdapter = new AccountAdapter(mContext, list);
                        recyclerList.setAdapter(mAdapter);
                    }

                    @Override
                    public void onError(int i, String s) {
                        Log.d("AccountBmob", "失败");
                    }
                });
                swipeLayout.setRefreshing(false);
            }
        }, 1000);
    }

    @Override
    public View getMidView() {
        return TitleUtil.addTextView(mContext, "我的账单");
    }

    @Override
    public View getLeftView() {
        View left = TitleUtil.addView(this, R.drawable.profile_2015_album_back_icon);
        left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        return left;
    }

    @Override
    public View getRightView() {
        return null;
    }

    @Override
    public void isShowView(boolean isShowTitle) {

    }
}

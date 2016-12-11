package main;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.example.administrator.renren_tarb.R;

import java.util.ArrayList;
import java.util.List;

import base.BaseFragment;
import chat.ChatSessionContentFragment;
import cn.bmob.v3.Bmob;
import discover.DiscoverMainFragment;
import interfaces.TabItemClickListener;
import login.Login;
import newsfeed.NewsFeedContentFragment;
import personal.UserGroupsFragment2015;
import publisher.GalleryActivity;
import publisher.InputPublisherActivity;
import tool.SettingManager;

public class MainActivity extends FragmentActivity implements TabItemClickListener {
    private BaseFragment news, chat, discover, user;
    private RenTabHost host;
    private FragmentTransaction transaction;
    private FragmentManager manager;
    private List<BaseFragment> containerList;
    private BaseFragment lastFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    private void init() {
        Bmob.initialize(this, getString(R.string.bmob_appKey));
        if (!SettingManager.getInstance().getIsLogin()) {
            Intent intent = new Intent(MainActivity.this, Login.class);
            startActivity(intent);
            finish();
        } else {
            containerList = new ArrayList<>();
            manager = getSupportFragmentManager();
            host = (RenTabHost) findViewById(R.id.host);
            host.setTabItemClickListener(this);
            host.setCurrentFragment(RenTabHost.TabType.FEED);
        }
    }

    @Override
    public void OnTabItemSelected(int type, Bundle args) {
        switch (type) {
            case RenTabHost.TabType.FEED:
                if (news == null) {
                    news = new NewsFeedContentFragment();
                }
                changFragment(news);
                break;
            case RenTabHost.TabType.CHAT:
                if (discover == null) {
                    discover = new DiscoverMainFragment();
                }
                changFragment(discover);
                break;
            case RenTabHost.TabType.DISCOVER:
                if (chat == null) {
                    chat = new ChatSessionContentFragment();
                }
                changFragment(chat);
                break;
            case RenTabHost.TabType.MINE:
                if (user == null) {
                    user = new UserGroupsFragment2015();
                }
                changFragment(user);
                break;
            default:
                break;
        }
    }

    private void changFragment(BaseFragment fragment) {
        transaction = manager.beginTransaction();
        boolean b = isIn(fragment);
        if (!b) {
            if (lastFragment != null) transaction.hide(lastFragment);
            transaction.add(R.id.fragment_layout, fragment);
            containerList.add(fragment);
        } else {
            for (BaseFragment container : containerList) {
                if (container == fragment) {
                    transaction.show(container);
                } else {
                    transaction.hide(container);
                }
            }
        }
        lastFragment = fragment;
        transaction.commit();
    }

    @Override
    public void OnPublisherClick() {
        GalleryActivity.show(this);
    }

    @Override
    public void OnPublisherLongClick() {
        InputPublisherActivity.showPublisher(MainActivity.this);
    }

    /**
     * fragment是否打开过
     *
     * @param fragment
     * @return
     */
    private boolean isIn(BaseFragment fragment) {
        if (containerList != null) {
            for (BaseFragment container : containerList) {
                if (container == fragment) {
                    return true;
                }
            }
        }
        return false;
    }
}
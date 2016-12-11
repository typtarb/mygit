package main;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.administrator.renren_tarb.R;

import interfaces.TabItemClickListener;
import tool.DisplayUtil;

/**
 * Created by Administrator on 2016/4/16.
 */
public class RenTabHost extends LinearLayout {

    public final int TAB_ITEM_LENGTH = 4;
    private TabItem[] tabs;
    private PublishTab publishTab;
    private Context mContext;
    private AnimationSet tabAnimSet;
    private TabItemClickListener mTabItemClickListener;
    private int mPublishTabHigh;

    public interface TabType {
        int FEED = 0;
        int DISCOVER = 1;
        int CHAT = 2;
        int MINE = 3;
    }

    public RenTabHost(Context context) {
        super(context);
        initTab(context);
    }

    public RenTabHost(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initTab(context);
    }

    public RenTabHost(Context context, AttributeSet attrs) {
        super(context, attrs);
        initTab(context);

    }

    public void initTab(Context context) {
        mContext = context;
        setOrientation(HORIZONTAL);
        mPublishTabHigh = DisplayUtil.dip2px(50);
        publishTab = new PublishTab(mContext);
        publishTab.getPublish().setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mTabItemClickListener) {
                    mTabItemClickListener.OnPublisherClick();
                }
            }
        });
        publishTab.getPublish().setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (null != mTabItemClickListener) {
                    mTabItemClickListener.OnPublisherLongClick();
                    return true;
                }
                return false;
            }
        });
        addView();
        initAnimation();
    }

    private void addView() {
        tabs = new TabItem[TAB_ITEM_LENGTH];
        boolean intoPublish = true;
        for (int i = 0; i < TAB_ITEM_LENGTH; i++) {
            if (i == 2 && intoPublish) {
                addView(publishTab.getPublish());
                intoPublish = false;
                i--;
            } else {
                tabs[i] = new TabItem(mContext);
                tabs[i].setType(i);
                initTabItem(tabs[i]);
                tabs[i].setUnselected();
                addView(tabs[i].getTabItem());
            }
        }
    }

    private void initTabItem(final TabItem item) {
        switch (item.getType()) {
            case TabType.FEED:
                item.getTabItem().setId(R.id.tab_item_feed);
                item.setImageId(R.drawable.common_btn_feed_pressdown, R.drawable.common_btn_feed_normal);
                break;
            case TabType.CHAT:
                item.getTabItem().setId(R.id.tab_item_chat);
                item.setImageId(R.drawable.common_btn_buddy_pressdown, R.drawable.common_btn_buddy_normal);
                break;
            case TabType.DISCOVER:
                item.getTabItem().setId(R.id.tab_item_discover);
                item.setImageId(R.drawable.common_btn_explore_pressdown, R.drawable.common_btn_explore_normal);
                break;
            case TabType.MINE:
                item.getTabItem().setId(R.id.tab_item_mine);
                item.setImageId(R.drawable.common_btn_profile_pressdown, R.drawable.common_btn_profile_normal);
                break;
            default:
                // TODO, how to expend this function
        }

        // 点击处理
        item.getTabItem().setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                cleanSelected();
                setSelected(item.getType());
                mTabItemClickListener.OnTabItemSelected(item.getType(), null);
            }
        });
    }

    private void setSelected(int type) {

        TabItem tabItem = findTabItemByType(type);
        if (null != tabItem) {
            tabItem.setSelected();
        }
    }

    private TabItem findTabItemByType(int type) {
        for (TabItem item : tabs) {
            if (item.getType() == type)
                return item;
        }
        return null;
    }

    private void initAnimation() {
        ScaleAnimation zoomIn, zoomOut;
        zoomIn = new ScaleAnimation(1f, 1.125f, 1f, 1.125f, Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        zoomOut = new ScaleAnimation(1.125f, 1f, 1.125f, 1f, Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        zoomIn.setDuration(100);
        zoomOut.setDuration(100);
        zoomOut.setStartOffset(100);  // 延迟一定的时间再启动动画
        tabAnimSet = new AnimationSet(true);
        tabAnimSet.addAnimation(zoomIn);
        tabAnimSet.addAnimation(zoomOut);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int measuredWidth = MeasureSpec.getSize(widthMeasureSpec);
        final int nums = tabs.length + 1;
        int tabWidth = Math.round(measuredWidth / nums);
        for (int i = 0; i < tabs.length; ++i) {
            tabs[i].getTabItem().measure(MeasureSpec.makeMeasureSpec(tabWidth, MeasureSpec.EXACTLY),
                    MeasureSpec.makeMeasureSpec(mPublishTabHigh, MeasureSpec.EXACTLY));
        }
        publishTab.getPublish().measure(MeasureSpec.makeMeasureSpec(tabWidth, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(mPublishTabHigh, MeasureSpec.EXACTLY));
        setMeasuredDimension(measuredWidth, mPublishTabHigh);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        final int width = r - l;
        int tabWidth = width / (tabs.length + 1);
        for (int i = 0; i < tabs.length; i++) {
            if (i < tabs.length / 2) {
                tabs[i].getTabItem().layout(tabWidth * i, 0
                        , tabWidth * (i + 1), mPublishTabHigh);
            } else {
                tabs[i].getTabItem().layout(tabWidth * (i + 1), 0
                        , tabWidth * (i + 2), mPublishTabHigh);
            }
        }
        publishTab.getPublish().layout(tabWidth * 2, 0, tabWidth * 3, mPublishTabHigh);
    }

    private class PublishTab {

        private Context mContext;
        private View mPublish;

        private PublishTab(Context context) {
            mContext = context;
            mPublish = View.inflate(mContext, R.layout.publish_tab_host_item, null);
        }

        public View getPublish() {
            return mPublish;
        }
    }

    private void cleanSelected() {

        for (int i = 0; i < TAB_ITEM_LENGTH; i++) {
            tabs[i].setUnselected();
        }
    }

    private class TabItem {
        private int mType;
        private Context mContext;
        private View mTabItem;
        private ImageView mImage, icon;
        protected int selectedImageResId;
        protected int unSelectedImageResId;

        public TabItem(Context context) {
            mContext = context;
            LayoutInflater mInflater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            mTabItem = (FrameLayout) mInflater.inflate(R.layout.tab_host_item, null);
            mImage = (ImageView) mTabItem.findViewById(R.id.tab_iv);
            icon = (ImageView) mTabItem.findViewById(R.id.icon_dian);
        }

        public int getType() {
            return mType;
        }

        public void setType(int mType) {
            this.mType = mType;
        }

        public void setSelected() {

            tabAnimSet.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    mImage.setImageResource(selectedImageResId);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            mImage.startAnimation(tabAnimSet);
        }

        public void setUnselected() {
            mImage.setImageResource(unSelectedImageResId);
        }

        public void setImageId(int selectedImageResId, int unSelectedImageResId) {
            this.selectedImageResId = selectedImageResId;
            this.unSelectedImageResId = unSelectedImageResId;
        }

        /**
         * 鉴于可能的情况，所以使用不同的XML
         *
         * @return
         */
        public View getTabItem() {
            return mTabItem;
        }

        public void isShowIcon(boolean show) {
            if (null != icon) {
                if (show) {
                    icon.setVisibility(View.VISIBLE);
                } else {
                    icon.setVisibility(View.GONE);
                }
            }
        }
    }

    public void setTabItemClickListener(TabItemClickListener l) {
        mTabItemClickListener = l;
    }

    public void setCurrentFragment(int type) {
        switch (type) {
            case TabType.FEED:
                tabs[0].getTabItem().callOnClick();
                break;
            case TabType.DISCOVER:
                tabs[1].getTabItem().callOnClick();
                break;
            case TabType.CHAT:
                tabs[2].getTabItem().callOnClick();
                break;
            case TabType.MINE:
                tabs[3].getTabItem().callOnClick();
                break;
            default:
                if (null != mTabItemClickListener) {
                    mTabItemClickListener.OnPublisherLongClick();
                }
                break;
        }
    }
}
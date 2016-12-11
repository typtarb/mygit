package base;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.administrator.renren_tarb.R;

import title.TitleBar;
import title.TitleInterface;
import title.TitleUtil;

/**
 * Created by Administrator on 2016/4/18.
 */
public class BaseFragment extends Fragment implements TitleInterface {

    private boolean isShowTitle = true;
    private String titleTxt = "";
    private Context mContext;
    private TitleBar mTitleBar;
    protected LinearLayout rootLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.base_fragment_layout, null);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        rootLayout = (LinearLayout) view.findViewById(R.id.main_layout);
        mTitleBar = (TitleBar) view.findViewById(R.id.title_bar);
        mTitleBar.setTitle(this);
        if (isShowTitle) {
            mTitleBar.setVisibility(View.VISIBLE);
        } else {
            mTitleBar.setVisibility(View.GONE);
        }
    }

    @Override
    public View getMidView() {
        return TitleUtil.addTextView(mContext, titleTxt);
    }

    @Override
    public View getLeftView() {
        return null;
    }

    @Override
    public View getRightView() {
        return null;
    }

    @Override
    public void isShowView(boolean isShowTitle) {
        this.isShowTitle = isShowTitle;
    }

    public String setTitle(String s) {
        if (s == null) s = "";
        titleTxt = s;
        return titleTxt;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d("BaseFragment", "执行");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
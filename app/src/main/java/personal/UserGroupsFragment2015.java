package personal;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.administrator.renren_tarb.R;

import java.util.List;

import account.AccountActivity;
import account.MyAccountActivity;
import base.BaseFragment;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;
import login.Person;
import tool.ImageWorker;
import tool.SettingManager;
import tool.ToolUtil;

/**
 * Created by Administrator on 2016/4/14.
 */
public class UserGroupsFragment2015 extends BaseFragment {

    public final String TAG = "UserGroupsFragment2015";
    private Context mContext;
    private View personLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        personLayout = LayoutInflater.from(mContext).inflate(R.layout.personal_layout, null);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        rootLayout.addView(personLayout, params);
        initView();
    }

    private void initView() {
        final TextView balanceView = (TextView) personLayout.findViewById(R.id.balance);
        TextView name = (TextView) personLayout.findViewById(R.id.name);
        name.setText(SettingManager.getInstance().getName());
        ImageView head = (ImageView) personLayout.findViewById(R.id.head);
        if (!SettingManager.getInstance().getHeadUrl().equals("")) {
            ImageWorker.displayImage(mContext, SettingManager.getInstance().getHeadUrl(), head);
        }
        personLayout.findViewById(R.id.account).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, AccountActivity.class);
                startActivity(intent);
            }
        });
        BmobQuery<Person> query = new BmobQuery<Person>();
        query.addWhereEqualTo("name", SettingManager.getInstance().getName());
        query.findObjects(mContext, new FindListener<Person>() {
            @Override
            public void onSuccess(List<Person> list) {
                Person person = list.get(0);
                String bablance = Float.toString(person.getBalance());
                balanceView.setText("余额：" + bablance);
            }

            @Override
            public void onError(int i, String s) {
                ToolUtil.toast(mContext, "服务出错：" + s);
            }
        });
        personLayout.findViewById(R.id.head_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, MyAccountActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public View getMidView() {
        setTitle("个人主页");
        return super.getMidView();
    }
}
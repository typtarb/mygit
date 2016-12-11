package account;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

import com.example.administrator.renren_tarb.R;

import bmob.ServiceHelper;
import title.TitleBar;
import title.TitleInterface;
import title.TitleUtil;
import tool.ToolUtil;

/**
 * 充值
 * Created by yapeng.tian on 2016/5/24.
 */
public class RechargeActivity extends Activity implements View.OnClickListener, TitleInterface {

    private Activity mContext;
    private EditText recharge, name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recharge_layout);
        mContext = RechargeActivity.this;

        TitleBar bar = (TitleBar) findViewById(R.id.title_bar);
        bar.setTitle(this);

        recharge = (EditText) findViewById(R.id.recharge);
        name = (EditText) findViewById(R.id.name);
        name.setOnTouchListener(
                new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        if (event.getAction() == MotionEvent.ACTION_UP) {
                            Intent intent = new Intent(mContext, PersonListActivity.class);
                            intent.putExtra("type", PersonListActivity.GET_COST_PERSON);
                            startActivityForResult(intent, 1);
                        }
                        return true;
                    }
                }
        );
    }

    @Override
    public void onClick(View v) {
        final String recharge_string = recharge.getText().toString();
        final String name_string = name.getText().toString();
        switch (v.getId()) {
            case R.id.recharge_ok:
                if (recharge_string.equals("") || name_string.equals("")) {
                    ToolUtil.toast(mContext, "请补全信息");
                } else {
                    if (recharge_string.endsWith(".") || recharge_string.startsWith(".")) {
                        ToolUtil.toast(mContext, "消费金额数字不合法");
                        return;
                    }
                    ServiceHelper.recharge(mContext, name_string, recharge_string);
                }
        }
    }

    @Override
    public View getMidView() {
        return null;
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            String s = data.getStringExtra("person");
            if (resultCode == 7) {
                name.setText(s);
            }
        }
    }
}

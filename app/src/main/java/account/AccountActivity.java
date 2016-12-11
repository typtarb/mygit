package account;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.example.administrator.renren_tarb.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import login.Person;
import title.TitleBar;
import title.TitleInterface;
import title.TitleUtil;
import tool.ToolUtil;

/**
 * Created by yapeng.tian on 2016/5/21.
 */
public class AccountActivity extends Activity implements View.OnClickListener, TitleInterface {

    private Activity mContext;
    private EditText cost, restaurant, personnel, coster;
    private LinearLayout layout;
    public static final int ACCOUNT_TYPE = 0;
    public static final int NEWMAN_TYPE = 1;
    public static final int RECHARGE_TYPE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account);

        mContext = AccountActivity.this;

        TitleBar bar = (TitleBar) findViewById(R.id.title_bar);
        bar.setTitle(this);
        layout = (LinearLayout) findViewById(R.id.layout);
        cost = (EditText) findViewById(R.id.cost);
        restaurant = (EditText) findViewById(R.id.restaurant);
        personnel = (EditText) findViewById(R.id.personnel);
        personnel.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    Intent intent = new Intent(mContext, PersonListActivity.class);
                    intent.putExtra("type", PersonListActivity.GET_PERSONAL);
                    startActivityForResult(intent, 1);
                }
                return false;
            }
        });
        coster = (EditText) findViewById(R.id.coster);
        coster.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    Intent intent = new Intent(mContext, PersonListActivity.class);
                    intent.putExtra("type", 6);
                    startActivityForResult(intent, 1);
                }
                return false;
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.account_ok:
                ToolUtil.showProgressDialog(mContext);
                layout.setClickable(false);
                layout.setBackgroundColor(getResources().getColor(R.color.emptyview_color));
                upAccount();
                break;
        }
    }

    private void upAccount() {
        final String costs, restaurants, personnels, costers;
        costs = cost.getText().toString();
        restaurants = restaurant.getText().toString();
        personnels = personnel.getText().toString();
        costers = coster.getText().toString();
        if (costs.equals("") || restaurants.equals("") || personnels.equals("") || costers.equals("")) {
            ToolUtil.toast(mContext, "请补全信息");
            return;
        }
        if (costs.endsWith(".") || costs.startsWith(".")) {
            ToolUtil.toast(mContext, "消费金额数字不合法");
            return;
        }

        final String[] array = personnels.split(",");
        float spend = Float.valueOf(costs);
        final float nong_min = spend / (array.length);
        final float di_zhu = spend - nong_min;


        AccountBmob item = new AccountBmob();
        item.setCost(Float.valueOf(costs));
        item.setCoster(costers);
        item.setType(ACCOUNT_TYPE);
        item.setPersonnel(personnels);
        item.setRestaurant(restaurants);
        item.save(mContext, new SaveListener() {
            @Override
            public void onSuccess() {
                BmobQuery<Person> query = new BmobQuery<Person>();
                query.addWhereContainedIn("name", Arrays.asList(array));
                query.findObjects(mContext, new FindListener<Person>() {
                    @Override
                    public void onSuccess(List<Person> list) {

                        List<BmobObject> persons = new ArrayList<BmobObject>();
                        for (Person person : list) {
                            if (person.getName().equals(costers)) {
                                person.setBalance(person.getBalance() + di_zhu);
                            } else {
                                person.setBalance(person.getBalance() - nong_min);
                            }
                            persons.add(person);
                        }

                        new BmobObject().updateBatch(mContext, persons, new UpdateListener() {
                            @Override
                            public void onSuccess() {
                                // TODO Auto-generated method stub
                                ToolUtil.toast(mContext, "发布成功");
                                ToolUtil.dismissProgressDialog();
                                layout.setClickable(true);
                                layout.setBackgroundColor(getResources().getColor(R.color.white));
                            }

                            @Override
                            public void onFailure(int code, String msg) {
                                // TODO Auto-generated method stub
                                ToolUtil.toast(mContext, "发布失败:" + msg);
                                ToolUtil.dismissProgressDialog();
                                layout.setClickable(true);
                            }
                        });
                    }

                    @Override
                    public void onError(int i, String s) {
                        ToolUtil.toast(mContext, "发布失败：" + s);
                        ToolUtil.dismissProgressDialog();
                        layout.setClickable(true);
                    }
                });
            }

            @Override
            public void onFailure(int i, String s) {
                ToolUtil.toast(mContext, "发布失败：" + s);
                ToolUtil.dismissProgressDialog();
                layout.setClickable(true);
            }
        });
    }

    @Override
    public View getMidView() {
        return TitleUtil.addTextView(this, "账单报销");
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
        View right = TitleUtil.addView(mContext, R.drawable.newsfeed_icon_title_addfriend);
        right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AccountActivity.this, RechargeActivity.class);
                startActivity(intent);
            }
        });
        return right;
    }

    @Override
    public void isShowView(boolean isShowTitle) {
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            String s = data.getStringExtra("person");
            if (resultCode == 2) {
                personnel.setText(s);
            } else if (resultCode == 7) {
                coster.setText(s);
            }
        }
    }
}

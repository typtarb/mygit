package login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.example.administrator.renren_tarb.R;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;
import main.MainActivity;
import tool.SettingManager;
import tool.ToolUtil;
import tool.Var;

/**
 * Created by yapeng.tian on 2016/5/18.
 */
public class Login extends Activity implements View.OnClickListener {

    public static final int REGISTERED = 1;
    public static final int REGISTERED_OK = 2;
    public static final int REGISTERED_ERROR = 3;
    private EditText name, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        name = (EditText) findViewById(R.id.name);
        password = (EditText) findViewById(R.id.password);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.registered:
                Intent intent = new Intent(Login.this, Registered.class);
                startActivityForResult(intent, REGISTERED);
                break;
            case R.id.login:
                checkAccount();
                break;
            default:
                break;
        }
    }

    private boolean checkAccount() {
        final String names = name.getText().toString();
        String passwords = password.getText().toString();
        if (names.equals("") || passwords.equals("")) {
            ToolUtil.toast(Login.this, "请完善登陆信息");
        } else {
            BmobQuery<Person> query = new BmobQuery<Person>();
            query.addWhereEqualTo("name", names).addWhereEqualTo("password", passwords);
            query.findObjects(Login.this, new FindListener<Person>() {
                @Override
                public void onSuccess(List<Person> list) {
                    Log.d("Login", "查询成功：共" + list.size() + "条数据");
                    if (list.size() > 0) {
                        Var.name = names;
                        Var.head_url = list.get(0).getHeadUrl();
                        SettingManager.getInstance().setName(Var.name);
                        SettingManager.getInstance().setHeadUrl(Var.head_url);
                        SettingManager.getInstance().setIsLogin(true);
                        ToolUtil.toast(Login.this, "登陆成功");
                        Intent intent = new Intent(Login.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        ToolUtil.toast(Login.this, "查无此人,请注册或输入正确的信息");
                    }
                }

                @Override
                public void onError(int i, String s) {
                    ToolUtil.toast(Login.this, "查询失败：" + s);
                }
            });
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REGISTERED) {
            if (resultCode == REGISTERED_OK) {
                Intent intent = new Intent(Login.this, MainActivity.class);
                startActivity(intent);
                finish();
            } else if (resultCode == REGISTERED_ERROR) {
                ToolUtil.toast(Login.this, "注册失败");
            }
        }
    }
}

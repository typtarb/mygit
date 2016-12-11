package login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.administrator.renren_tarb.R;

import java.util.ArrayList;

import bmob.ServiceHelper;
import publisher.GalleryActivity;
import publisher.GalleryItem;
import tool.ActionType;
import tool.ImageWorker;
import tool.ToolUtil;

/**
 * Created by yapeng.tian on 2016/5/18.
 */
public class Registered extends Activity implements View.OnClickListener {

    private EditText name;
    private EditText password;
    private EditText password_ag;
    private PersonItem personItem;
    private ImageView head, back;
    private Button registered_ok;
    private Activity mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = Registered.this;
        setContentView(R.layout.registered);
        initView();
    }

    private void initView() {
        personItem = new PersonItem();
        back = (ImageView) findViewById(R.id.back);
        back.setOnClickListener(this);
        head = (ImageView) findViewById(R.id.head);
        head.setOnClickListener(this);
        name = (EditText) findViewById(R.id.name);
        password = (EditText) findViewById(R.id.password);
        password_ag = (EditText) findViewById(R.id.password_ag);
        registered_ok = (Button) findViewById(R.id.registered_ok);
        registered_ok.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.registered_ok:
                if (checkRegistered()) {
                    ToolUtil.showProgressDialog(mContext);
                    ServiceHelper.Registered(mContext, personItem, ActionType.REGISTERED);
                }
                break;
            case R.id.head:
                getHead();
                break;
            default:
                break;
        }
    }

    private boolean checkRegistered() {
        boolean check = false;
        personItem.name = name.getText().toString();
        personItem.password = password.getText().toString();
        String passwords_ag = password_ag.getText().toString();
        if (personItem.name.equals("") || personItem.password.equals("") || passwords_ag.equals("")) {
            ToolUtil.toast(Registered.this, "请完善资料");
        } else if (!personItem.password.equals(passwords_ag)) {
            ToolUtil.toast(Registered.this, "两次密码输入不相同");
        } else {
            check = true;
        }
        return check;
    }

    private void getHead() {
        GalleryActivity.showRegisteredForResult(Registered.this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ActionType.REGISTERED_FOR_HEAD_IMAGE) {
            ArrayList<GalleryItem> addPhoto = data.getExtras().getParcelableArrayList("list");
            if (addPhoto.size() == 1) {
                ImageWorker.displayImage(Registered.this, addPhoto.get(0).image_path, head);
                personItem.headUrl = addPhoto.get(0).image_path;
            } else {
                personItem.headUrl = null;
            }
        }
    }
}

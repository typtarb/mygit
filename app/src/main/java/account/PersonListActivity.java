package account;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.administrator.renren_tarb.R;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;
import login.Person;
import login.PersonItem;
import title.TitleBar;
import title.TitleInterface;
import title.TitleUtil;
import tool.ImageWorker;
import tool.ToolUtil;

/**
 * Created by yapeng.tian on 2016/5/21.
 */
public class PersonListActivity extends Activity implements TitleInterface {

    public static int GET_PERSONAL = 5;
    public static int GET_COST_PERSON = 6;
    private Context mContext;
    private ListView listView;
    private PersonAdapter adapter;
    private List<PersonItem> selectedList;
    private int type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = PersonListActivity.this;
        setContentView(R.layout.person_list);
        selectedList = new ArrayList<>();
        type = getIntent().getIntExtra("type", 5);
        listView = (ListView) findViewById(R.id.list);
        TitleBar bar = (TitleBar) findViewById(R.id.title_bar);
        bar.setTitle(this);
        getPerson();
    }

    private void getPerson() {
        BmobQuery<Person> query = new BmobQuery<Person>();
        query.order("objectid");
        query.findObjects(mContext, new FindListener<Person>() {
            @Override
            public void onSuccess(List<Person> list) {
                final List<PersonItem> personList = new ArrayList<>();
                for (Person item : list) {
                    PersonItem itemP = new PersonItem();
                    itemP.name = item.getName();
                    itemP.headUrl = item.getHeadUrl();
                    personList.add(itemP);
                }
                adapter = new PersonAdapter(personList);
                listView.setAdapter(adapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        PersonItem item = personList.get(position);
                        if (item.isSelected) {
                            int i = getPosition(selectedList, item.name);
                            selectedList.remove(i);
                            item.isSelected = false;
                            view.setBackgroundColor(getResources().getColor(R.color.white));
                        } else {
                            if (type == 6 && selectedList.size() > 0) {
                                ToolUtil.toast(mContext, "你已经选择了以为付款人，\n若要更改请先将其取消!");
                                return;
                            }
                            selectedList.add(item);
                            item.isSelected = true;
                            view.setBackgroundColor(getResources().getColor(R.color.content_at_friends_color));
                        }
                    }
                });
            }

            @Override
            public void onError(int i, String s) {
                Log.d("tianyapeng", "查询失败" + s);
            }
        });
    }

    private int getPosition(List<PersonItem> list, String name) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).name.equals(name)) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public View getMidView() {
        return TitleUtil.addTextView(this, "全部人员");
    }

    @Override
    public View getLeftView() {
        View left = TitleUtil.addView(this, R.drawable.profile_2015_album_back_icon);
        left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, AccountActivity.class);
                setResult(3, intent);
                finish();
            }
        });
        return left;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Intent intent = new Intent(mContext, AccountActivity.class);
        setResult(3, intent);
        finish();
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public View getRightView() {
        View right = TitleUtil.addView(this, R.drawable.common_title_done);
        right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StringBuilder s = new StringBuilder("");
                for (int i = 0; i < selectedList.size(); i++) {
                    if (i == selectedList.size() - 1) {
                        s.append(selectedList.get(i).name);
                    } else {
                        s.append(selectedList.get(i).name + "、");
                    }
                }
                Intent intent = new Intent(mContext, AccountActivity.class);
                intent.putExtra("person", s.toString());
                if (type == 5) {
                    setResult(2, intent);
                } else if (type == 6) {
                    setResult(7, intent);
                }
                finish();
            }
        });
        return right;
    }

    @Override
    public void isShowView(boolean isShowTitle) {

    }

    public class PersonAdapter extends BaseAdapter {

        private List<PersonItem> personList = null;
        private LayoutInflater mInflater;

        public PersonAdapter(List<PersonItem> list) {
            if (personList == null) {
                personList = new ArrayList<>();
            }
            personList.clear();
            personList.addAll(list);
            mInflater = LayoutInflater.from(mContext);
        }

        @Override
        public int getCount() {
            return personList.size();
        }

        @Override
        public Object getItem(int position) {
            return personList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            PersonHolder holder = null;
            PersonItem item = (PersonItem) getItem(position);
            if (convertView == null) {
                holder = new PersonHolder();
                convertView = mInflater.inflate(R.layout.person_item, null);
                holder.head = (ImageView) convertView.findViewById(R.id.head);
                holder.name = (TextView) convertView.findViewById(R.id.name);
                convertView.setTag(holder);
            } else {
                holder = (PersonHolder) convertView.getTag();
            }
            ImageWorker.displayImage(mContext, item.headUrl, holder.head);
            holder.name.setText(item.name);
            convertView.setBackgroundColor(getResources().getColor(R.color.white));
            return convertView;
        }

        final class PersonHolder {
            public ImageView head;
            public TextView name;
        }
    }
}

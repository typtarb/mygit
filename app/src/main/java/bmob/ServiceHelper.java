package bmob;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.ImageView;

import java.io.File;
import java.util.List;

import account.AccountActivity;
import account.AccountBmob;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadBatchListener;
import cn.bmob.v3.listener.UploadFileListener;
import login.Login;
import login.Person;
import login.PersonItem;
import main.MainActivity;
import newsfeed.NewsFeedBmobObject;
import newsfeed.NewsFeedItem;
import newsfeed.NewsFeedType;
import tool.ActionType;
import tool.ImageWorker;
import tool.SettingManager;
import tool.ToolUtil;
import tool.Var;

/**
 * Created by Administrator on 2016/4/14.
 */
public class ServiceHelper {

    private static SaveListener saveListener;

    public static void setSaveListener(SaveListener mListener) {
        saveListener = mListener;
    }

    /**
     * 上传新鲜事
     *
     * @param context
     * @param path
     */
    public static void upNewsFeed(final Context context, NewsFeedItem path) {
        if (path.imageList.size() == 0) {
            NewsFeedBmobObject item = new NewsFeedBmobObject();
            item.setName(path.name);
            item.setTxt(path.txt);
            item.setTime(System.currentTimeMillis());
            item.setType(NewsFeedType.TXT);
            item.save(context, saveListener);
        } else if (path.imageList.size() == 1) {
            final BmobFile bmobFile = new BmobFile(new File(path.imageList.get(0)));
            bmobFile.uploadblock(context, new MyUploadFileListener(context, path, bmobFile));  // 单图
        } else {
            int size = path.imageList.size();
            String filePaths[] = new String[size];
            for (int i = 0; i < size; i++) {
                filePaths[i] = path.imageList.get(i);
            }
            Bmob.uploadBatch(context, filePaths, new MyUploadBatchListener(context, path, size));   // 多图
        }
    }

    /**
     * 注册
     *
     * @param mContext
     * @param personItem
     */
    public static void Registered(final Context mContext, final PersonItem personItem, final int type) {
        findPersonByName(mContext, personItem, type);
    }

    private static void findPersonByName(final Context mContext, final PersonItem personItem, final int type) {
        BmobQuery<Person> query = new BmobQuery<Person>();
        query.addWhereEqualTo("name", personItem.name);
        query.findObjects(mContext, new FindListener<Person>() {
            @Override
            public void onSuccess(List<Person> list) {
                if (type == ActionType.REGISTERED) {
                    if (list.size() > 0) {
                        ToolUtil.toast(mContext, "此用户名已被占用,请另用它名！");
                    } else {
                        findCheckPerson(mContext, personItem, type);
                    }
                } else if (type == ActionType.RECHARGE) {
                    float balance = list.get(0).getBalance();
                    balance += Float.valueOf(personItem.recharge);
                    updateAccount(mContext, personItem.name, personItem.recharge, balance, list.get(0).getObjectId());
                }
            }

            @Override
            public void onError(int i, String s) {
                error(mContext, s);
            }
        });
    }

    private static void updateAccount(final Context mContext, final String name, final String recharge, float balance, String id) {
        Person p2 = new Person();
        p2.setName(name);
        p2.setBalance(balance);
        p2.update(mContext, id, new UpdateListener() {
            @Override
            public void onSuccess() {
                // TODO Auto-generated method stub
                publisherRecharge(mContext, name, recharge);
            }

            @Override
            public void onFailure(int code, String msg) {
                // TODO Auto-generated method stub
                ToolUtil.toast(mContext, "充值失败:" + msg);
            }
        });
    }

    /**
     * 发布充值
     *
     * @param mContext
     * @param name
     * @param recharge
     */
    private static void publisherRecharge(final Context mContext, String name, String recharge) {
        AccountBmob item = new AccountBmob();
        item.setCost(Float.valueOf(recharge));
        item.setCoster(name);
        item.setType(AccountActivity.RECHARGE_TYPE);
        item.save(mContext, new SaveListener() {
            @Override
            public void onSuccess() {
                ToolUtil.toast(mContext, "充值成功");
            }

            @Override
            public void onFailure(int i, String s) {
                ToolUtil.toast(mContext, "充值失败:" + s);
            }
        });
    }

    private static void findCheckPerson(final Context mContext, final PersonItem personItem, final int type) {
        BmobQuery<Person> query = new BmobQuery<Person>();
        query.addWhereEqualTo("name", personItem.name).addWhereEqualTo("password", personItem.password);
        query.findObjects(mContext, new FindListener<Person>() {
            @Override
            public void onSuccess(List<Person> list) {
                if (list.size() > 0) {
                    ToolUtil.toast(mContext, "此账号已注册请直接登陆");
                } else {
                    goRegistered(mContext, personItem, type);
                }
            }

            @Override
            public void onError(int i, String s) {
                error(mContext, s);
            }
        });
    }

    private static void goRegistered(final Context context, final PersonItem person, final int type) {
        if (person.headUrl == null) {
            upPersonNullUrl(context, person, type);
        } else {
            upPerson(context, person, type);
        }
    }

    // 注册无图片
    private static void upPersonNullUrl(final Context context, final PersonItem person, final int type) {
        Person personBmob = new Person();
        personBmob.setPassword(person.password);
        personBmob.setName(person.name);
        personBmob.save(context, new SaveListener() {
            @Override
            public void onSuccess() {
                RegisteredSuccess(context, person, type);
            }

            @Override
            public void onFailure(int i, String s) {
                error(context, s);
            }
        });
    }

    // 注册存在图片
    private static void upPerson(final Context context, final PersonItem person, final int type) {
        final BmobFile bmobFile = new BmobFile(new File(person.headUrl));
        bmobFile.uploadblock(context, new UploadFileListener() {
            @Override
            public void onSuccess() {
                Person personBmob = new Person();
                personBmob.setPassword(person.password);
                personBmob.setName(person.name);
                personBmob.setHead(bmobFile);
                personBmob.setHeadUrl(bmobFile.getFileUrl(context));
                personBmob.save(context, new SaveListener() {
                    @Override
                    public void onSuccess() {
                        RegisteredSuccess(context, person, type);
                    }

                    @Override
                    public void onFailure(int i, String s) {
                        error(context, s);
                    }
                });
            }

            @Override
            public void onFailure(int i, String s) {
                error(context, s);
            }
        });
    }

    public static void RegisteredSuccess(final Context context, final PersonItem item, final int type) {
        ToolUtil.dismissProgressDialog();
        upAccountRegistered(context, item, type);
    }

    private static void upAccountRegistered(final Context context, final PersonItem item, final int type) {
        AccountBmob account = new AccountBmob();
        account.setCoster(item.name);
        account.setType(AccountActivity.NEWMAN_TYPE);
        account.save(context, new SaveListener() {
            @Override
            public void onSuccess() {
                ToolUtil.toast(context, "注册成功");
                Var.name = item.name;
                Var.head_url = item.headUrl;
                SettingManager.getInstance().setName(Var.name);
                SettingManager.getInstance().setHeadUrl(Var.head_url);
                SettingManager.getInstance().setIsLogin(true);
                Intent intent = new Intent(context, MainActivity.class);
                ((Activity) context).setResult(Login.REGISTERED_OK, intent);
                ((Activity) context).finish();
            }

            @Override
            public void onFailure(int i, String s) {
            }
        });
    }

    /**
     * 充值
     *
     * @param mContext
     * @param name_string
     * @param recharge_string
     */
    public static void recharge(final Context mContext, final String name_string, final String recharge_string) {
        findPersonByName(mContext, new PersonItem(name_string, recharge_string), ActionType.RECHARGE);
    }

    final static class MyUploadFileListener extends UploadFileListener {

        private Context context;
        private BmobFile bmobFile;
        private NewsFeedItem path;

        MyUploadFileListener(Context context, NewsFeedItem path, BmobFile bmobFile) {
            this.bmobFile = bmobFile;
            this.context = context;
            this.path = path;
        }

        @Override
        public void onSuccess() {

            NewsFeedBmobObject item = new NewsFeedBmobObject();
            item.setImage(bmobFile);
            item.setName(path.name);
            item.setTxt(path.txt);
            item.setTime(System.currentTimeMillis());
            item.setUrl(bmobFile.getFileUrl(context));
            item.setType(NewsFeedType.SINGLE_IMAGE);
            item.save(context, saveListener);
        }

        @Override
        public void onFailure(int i, String s) {
            ToolUtil.dismissProgressDialog();
            ToolUtil.toast(context, "发布失败：" + s);
        }

        @Override
        public void onProgress(Integer value) {
            Log.d("Progress", "value = " + value);
        }
    }

    static class MyUploadBatchListener implements UploadBatchListener {

        private Context context;
        private NewsFeedItem path;
        private int size;

        MyUploadBatchListener(Context context, NewsFeedItem path, int size) {
            this.context = context;
            this.path = path;
            this.size = size;
        }

        @Override
        public void onSuccess(List<BmobFile> list, List<String> list1) {
            if (list.size() == size) {
                final NewsFeedBmobObject item = new NewsFeedBmobObject();
                StringBuilder url = new StringBuilder("");
                int length = list1.size();
                for (int i = 0; i < length; i++) {
                    if (i == length - 1) {
                        url.append(list1.get(i) + "");
                    } else {
                        url.append(list1.get(i) + ",");
                    }
                }
                item.setUrl(url.toString());
                item.setName(path.name);
                item.setTxt(path.txt);
                item.setTime(System.currentTimeMillis());
                item.setType(NewsFeedType.MULTI_IMAGE);
                item.save(context, saveListener);
            }
        }

        @Override
        public void onProgress(int i, int i1, int i2, int i3) {
        }

        @Override
        public void onError(int i, String s) {
            ToolUtil.dismissProgressDialog();
            ToolUtil.toast(context, "发布失败：" + s);
        }
    }

    public static void findUrlByName(final Context context, String name, final ImageView v) {
        BmobQuery<Person> query = new BmobQuery<Person>();
        query.addWhereEqualTo("name", name);
        query.findObjects(context, new FindListener<Person>() {
            @Override
            public void onSuccess(List<Person> list) {
                ImageWorker.displayImage(context, list.get(0).getHeadUrl(), v);
            }

            @Override
            public void onError(int i, String s) {
                ToolUtil.toast(context, "服务错误");
            }
        });
    }

    private static void error(Context context, String msg) {
        ToolUtil.toast(context, msg);
        ToolUtil.dismissProgressDialog();
    }
}
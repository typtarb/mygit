package tool;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;

import com.example.administrator.renren_tarb.R;

import main.RenApplication;

/**
 * Created by yapeng.tian on 2016/5/18.
 */
public class SettingManager {
    private Context mContext;
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;

    private static class SingletonCreator {
        private static final SettingManager instance = new SettingManager();
    }

    public static SettingManager getInstance() {
        return SingletonCreator.instance;
    }

    private SettingManager() {
        init(RenApplication.getContext());
    }

    public void init(Context context) {
        if (context != null) {
            mContext = context.getApplicationContext();
        }
        int mode = (Build.VERSION.SDK_INT > Build.VERSION_CODES.FROYO) ? Context.MODE_MULTI_PROCESS : Context.MODE_PRIVATE;
        mSharedPreferences = mContext.getSharedPreferences("setting", mode);
        mEditor = mSharedPreferences.edit();
    }

    public boolean getIsLogin() {
        return mSharedPreferences.getBoolean(mContext.getString(R.string.login), false);
    }

    public void setIsLogin(boolean isNeedShow) {
        mEditor.putBoolean(mContext.getString(R.string.login), isNeedShow);
        mEditor.commit();
    }

    public void setName(String name) {
        mEditor.putString("name", name);
        mEditor.commit();
    }

    public String getName() {
        return mSharedPreferences.getString("name", "");
    }

    public void setHeadUrl(String url) {
        mEditor.putString("head_url", url);
        mEditor.commit();
    }

    public boolean getIsFirstIn() {
        return mSharedPreferences.getBoolean("first_in", true);
    }

    public void setIsFirstIn(boolean isNeedShow) {
        mEditor.putBoolean("first_in", isNeedShow);
        mEditor.commit();
    }

    public String getHeadUrl() {
        return mSharedPreferences.getString("head_url", "");
    }
}

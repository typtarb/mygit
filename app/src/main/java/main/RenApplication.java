package main;

import android.app.Application;
import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import publisher.EmotionHelper;

/**
 * Created by Administrator on 2016/4/14.
 */
public class RenApplication extends Application {
    private static Context mContext;
    public static List<Integer> EmotionList = new ArrayList<>();

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        for (int i = 0; i < EmotionHelper.array.length; i++) {
            EmotionList.add(EmotionHelper.array[i]);
        }
    }

    public static Context getContext() {
        return mContext;
    }
}

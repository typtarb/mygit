package tool;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.administrator.renren_tarb.R;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import account.AccountBmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;
import newsfeed.NewsFeedBmobObject;
import publisher.GalleryItem;

public class ImageWorker {
    private static LoadImageInterface loadImageInterface;
    private static LoadAccountInterface loadAccountInterface;

    public static List<GalleryItem> loadImageLocal(Context context) {
        ExecutorService executor = Executors.newCachedThreadPool();
        Task task = new Task(context);
        Future<List<GalleryItem>> futureTask = executor.submit(task);
        executor.shutdown();
        List<GalleryItem> list = null;
        try {
            list = futureTask.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static List<GalleryItem> loadImage(Context context) {

        List<GalleryItem> imageList = new ArrayList<GalleryItem>();
        ContentResolver cr = context.getContentResolver();
        String[] projection = {MediaStore.Images.Media._ID, MediaStore.Images.Media.DATA, MediaStore.Images.Media
                .DATE_TAKEN, MediaStore.Images.Media.BUCKET_ID};
        Cursor imageCursor = null;
        try {
            imageCursor = cr.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, null, null, null);
            int i = 0;
            while (imageCursor != null && imageCursor.moveToNext()) {

                int Column_imgId = imageCursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID);
                int Column_absPath = imageCursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                int Column_buckId = imageCursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_ID);

                int imgId = imageCursor.getInt(Column_imgId);
                String buckId = imageCursor.getString(Column_buckId);
                String imgPath = imageCursor.getString(Column_absPath);

                if (TextUtils.isEmpty(imgPath) || imgPath.toLowerCase().endsWith(".gif")) {
                    continue;
                }
                File file = new File(imgPath);
                if (!file.exists()) {
                    continue;
                }

                GalleryItem item = new GalleryItem();
                item.image_id = imgId;
                if (buckId != null) {
                    item.albumId = buckId;
                }
                item.isShow = true;
                item.image_path = imgPath;
                item.image_position = i;
                ++i;
                imageList.add(item);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (imageCursor != null && !imageCursor.isClosed()) {
                imageCursor.close();
            }
        }
        return imageList;
    }

    public static void displayImage(Context context, String url, final ImageView view) {
        int w = (context.getResources().getDisplayMetrics().widthPixels - DisplayUtil.dip2px(2) * 4) / 3;
        Glide.with(context).load(url).
                centerCrop()
                .crossFade()
                .override(w, w)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.group_bg_album_image)
                .into(view);
    }

    public static void getImage(Context mContext) {

        BmobQuery<NewsFeedBmobObject> query = new BmobQuery<NewsFeedBmobObject>();
        query.order("objectid");
        query.findObjects(mContext, new FindListener<NewsFeedBmobObject>() {
            @Override
            public void onSuccess(List<NewsFeedBmobObject> list) {
                Collections.sort(list, new Comparator<NewsFeedBmobObject>() {
                    @Override
                    public int compare(NewsFeedBmobObject lhs, NewsFeedBmobObject rhs) {
                        return listCompare(lhs.getTime(), rhs.getTime());
                    }
                });
                loadImageInterface.onSuccess(list);
            }

            @Override
            public void onError(int i, String s) {
                Log.d("tianyapeng", "查询失败" + s);
            }
        });
    }

    private static final int listCompare(long x, long y) {
        if (x == y) {
            return 0;
        } else if (x > y) {
            return -1;
        } else {
            return 1;
        }
    }

    public static void getAcconut(Context mContext) {

        BmobQuery<AccountBmob> query = new BmobQuery<AccountBmob>();
        query.findObjects(mContext, new FindListener<AccountBmob>() {
            @Override
            public void onSuccess(List<AccountBmob> list) {
                Collections.reverse(list);
                loadAccountInterface.onSuccess(list);
            }

            @Override
            public void onError(int i, String s) {
                Log.d("tianyapeng", "查询失败" + s);
            }
        });
    }

    public static void setLoadImageInterface(LoadImageInterface loadImageInterface) {
        ImageWorker.loadImageInterface = loadImageInterface;
    }

    public static void setLoadAccountInterface(LoadAccountInterface loadAccountInterface) {
        ImageWorker.loadAccountInterface = loadAccountInterface;
    }

    public interface LoadImageInterface {
        void onSuccess(List<NewsFeedBmobObject> list);
    }

    public interface LoadAccountInterface {
        void onSuccess(List<AccountBmob> list);
    }

    static class Task implements Callable<List<GalleryItem>> {
        private Context mContext;

        public Task(Context context) {
            mContext = context;
        }

        @Override
        public List<GalleryItem> call() throws Exception {
            return ImageWorker.loadImage(mContext);
        }
    }
}
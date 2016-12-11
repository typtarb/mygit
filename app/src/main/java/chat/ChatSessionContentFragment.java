package chat;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.achartengine.ChartFactory;
import org.achartengine.model.CategorySeries;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;

import java.util.ArrayList;

import base.BaseFragment;

/**
 * Created by Administrator on 2016/4/14.
 */
public class ChatSessionContentFragment extends BaseFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public View getMidView() {
        setTitle("大数据");
        return super.getMidView();
    }

    private static ArrayList<Pie> pieL = new ArrayList<Pie>();

    public static View getPieChartView(Context context, ArrayList<Pie> pie) {
        pieL = pie;
        double[] values = new double[pieL.size()];
        for (int i = 0; i < pieL.size(); i++) {
            values[i] = pieL.get(i).getValue();
        }
        int[] colors = new int[]{Color.parseColor("#DCD900"), Color.parseColor("#1E8C04"), Color.parseColor("#23BA00"),
                Color.parseColor("#90BA00")};
        DefaultRenderer renderer = buildCategoryRenderer(colors);
        renderer.setZoomButtonsVisible(true);
        renderer.setZoomEnabled(true);
        renderer.setChartTitleTextSize(20);
        renderer.setLabelsColor(Color.BLACK);
        renderer.setLegendHeight(30);
        renderer.setFitLegend(true);
        renderer.setShowLegend(true);
        renderer.setClickEnabled(true);
        return ChartFactory.getPieChartView(context, buildCategoryDataset("", values), renderer);
    }

    protected static CategorySeries buildCategoryDataset(String title,
                                                         double[] values) {
        String[] s = new String[]{"增加 ", "下降", "基本不变", "不变"};
        CategorySeries series = new CategorySeries(title);
        int k = 0;
        for (int i = 0; i < pieL.size(); i++) {
            series.add(s[i], pieL.get(i).getValue());
        }
        return series;
    }

    protected static DefaultRenderer buildCategoryRenderer(int[] colors) {
        DefaultRenderer renderer = new DefaultRenderer();
        renderer.setLabelsTextSize(15);
        renderer.setLegendTextSize(15);
        renderer.setMargins(new int[]{20, 30, 15, 0});
        for (int color : colors) {
            SimpleSeriesRenderer r = new SimpleSeriesRenderer();
            r.setColor(color);
            renderer.addSeriesRenderer(r);
        }
        return renderer;
    }
}

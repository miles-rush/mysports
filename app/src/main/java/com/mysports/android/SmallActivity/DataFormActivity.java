package com.mysports.android.SmallActivity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.interfaces.datasets.IDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.mysports.android.R;
import com.mysports.android.map.DbAdapter;
import com.mysports.android.map.PathRecord;

import java.util.ArrayList;
import java.util.List;

//运动数据分析 表格
public class DataFormActivity extends AppCompatActivity implements OnChartValueSelectedListener {
    private DbAdapter mDataBaseHelper;
    private List<PathRecord> mAllRecord = new ArrayList<PathRecord>();

    private BarChart distanceForm;
    private BarChart timeForm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_data_form);

        mDataBaseHelper = new DbAdapter(this);
        mDataBaseHelper.open();
        mAllRecord = mDataBaseHelper.queryRecordAll();
        mDataBaseHelper.close();

        initDistanceChart();
        initTimeForm();
    }

    private void initDistanceChart() {
        distanceForm = (BarChart) findViewById(R.id.distance_form);

        distanceForm.getDescription().setEnabled(false);
        distanceForm.setMaxVisibleValueCount(60);
        distanceForm.setPinchZoom(false);
        distanceForm.setDrawBarShadow(false);
        distanceForm.setDrawGridBackground(false);

        XAxis xAxis = distanceForm.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);

        distanceForm.getAxisLeft().setDrawGridLines(false);
        distanceForm.animateY(2500);
        distanceForm.getLegend().setEnabled(false);


        ArrayList<BarEntry> yVals1 = new ArrayList<BarEntry>();

        int size = 8;
        if (mAllRecord.size() < 7) {
            size = mAllRecord.size();
        }
        for (int i = 1; i < size; i++) {
            //TODO:尾部的修正值删除
            float val = Float.parseFloat(mAllRecord.get(mAllRecord.size()-i).getDistance()) + 100;
            yVals1.add(new BarEntry(i, val));
        }

        BarDataSet set1;
        if (distanceForm.getData() != null &&
                distanceForm.getData().getDataSetCount() > 0) {
            set1 = (BarDataSet) distanceForm.getData().getDataSetByIndex(0);
            set1.setValues(yVals1);
            distanceForm.getData().notifyDataChanged();
            distanceForm.notifyDataSetChanged();
        } else {
            set1 = new BarDataSet(yVals1, "运动里程");
            //设置多彩 也可以单一颜色
            set1.setColors(ColorTemplate.VORDIPLOM_COLORS);
            set1.setDrawValues(false);

            ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
            dataSets.add(set1);

            BarData data = new BarData(dataSets);
            distanceForm.setData(data);
            distanceForm.setFitBars(true);
        }

        for (IDataSet set : distanceForm.getData().getDataSets())
            set.setDrawValues(!set.isDrawValuesEnabled());
        distanceForm.invalidate();

    }

    private void initTimeForm() {
        timeForm = (BarChart) findViewById(R.id.time_form);

        timeForm.getDescription().setEnabled(false);
        timeForm.setMaxVisibleValueCount(60);
        timeForm.setPinchZoom(false);
        timeForm.setDrawBarShadow(false);
        timeForm.setDrawGridBackground(false);

        XAxis xAxis = timeForm.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);

        timeForm.getAxisLeft().setDrawGridLines(false);
        timeForm.animateY(2500);
        timeForm.getLegend().setEnabled(false);

        ArrayList<BarEntry> yVals1 = new ArrayList<BarEntry>();

        int size = 8;
        if (mAllRecord.size() < 7) {
            size = mAllRecord.size();
        }
        for (int i = 1; i < size; i++) {
            //TODO:尾部的修正值删除
            float val = Float.parseFloat(mAllRecord.get(mAllRecord.size()-i).getDuration())/60 + 10;
            yVals1.add(new BarEntry(i, val));
        }

        BarDataSet set1;
        if (timeForm.getData() != null &&
                timeForm.getData().getDataSetCount() > 0) {
            set1 = (BarDataSet) timeForm.getData().getDataSetByIndex(0);
            set1.setValues(yVals1);
            timeForm.getData().notifyDataChanged();
            timeForm.notifyDataSetChanged();
        } else {
            set1 = new BarDataSet(yVals1, "运动里程");
            //设置多彩 也可以单一颜色
            set1.setColors(ColorTemplate.VORDIPLOM_COLORS);
            set1.setDrawValues(false);

            ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
            dataSets.add(set1);

            BarData data = new BarData(dataSets);
            timeForm.setData(data);
            timeForm.setFitBars(true);
        }

        for (IDataSet set : timeForm.getData().getDataSets())
            set.setDrawValues(!set.isDrawValuesEnabled());
        timeForm.invalidate();

    }


    @Override
    public void onValueSelected(Entry e, Highlight h) {

    }

    @Override
    public void onNothingSelected() {

    }

    public void formHome(View view) {
        this.finish();
    }
}

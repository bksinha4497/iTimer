package com.example.ankit.locationmonitor;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DownloadManager;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.location.Address;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Toast;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Calendar;

public class GraphActivity extends AppCompatActivity {

   // BarChart barChart;

    PieChart pieChart;
    public Button btn_date;
    int year_x,month_x,day_x;
    static final int Dialog_id=0;

    public void showDialogOnDateClick()
    {
        btn_date=(Button)findViewById(R.id.button12);
        btn_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog(Dialog_id);
            }
        });

    }

    @Override
    protected Dialog onCreateDialog(int id){
        if(id==Dialog_id)
            return new DatePickerDialog(this,dpickerListner ,year_x,month_x,day_x);
        return null;

    }

    private DatePickerDialog.OnDateSetListener dpickerListner=new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            year_x = year;
            month_x = monthOfYear + 1;
            day_x = dayOfMonth;
            setDatedMarker();
        }
    };
    private void setDatedMarker(){
        String date = String.format("%02d", day_x)+"."+String.format("%02d", month_x)+"."+String.format("%04d", year_x);
        Toast.makeText(GraphActivity.this,date,Toast.LENGTH_SHORT).show();
        addGraphs(date);

    }
    private void addGraphs(String date ){

        DatabaseHelper db9 = new DatabaseHelper(this);
        //String date1="";
        ArrayList<DatabaseHelper.MyObj> myObjs = db9.normalizeData(this, date);

        LatLng locations = null;

        int j=20;
        ArrayList<PieEntry> yValues=new ArrayList<>();
        for (int i = 0; i < myObjs.size(); i++) {
            String a1=myObjs.get(i).loc1;


            yValues.add(new PieEntry(j,a1));

        }
        // graph start
        pieChart= (PieChart) findViewById(R.id.piechart);
        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.setExtraOffsets(5,10,5,5);

        pieChart.setDragDecelerationFrictionCoef(0.99f);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColor(Color.WHITE);
        pieChart.setTransparentCircleRadius(61f);



        pieChart.animateY(1000, Easing.EasingOption.EaseInOutCubic);

        PieDataSet dataSet=new PieDataSet(yValues,"Localities");
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);
        dataSet.setColors(ColorTemplate.JOYFUL_COLORS);

        PieData data=new PieData(dataSet);
        data.setValueTextSize(10f);
        data.setValueTextColor(Color.YELLOW);

        pieChart.setData(data);

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);

        final Calendar cal=Calendar.getInstance();
        year_x=cal.get(Calendar.YEAR);
        month_x=cal.get(Calendar.MONTH);
        day_x=cal.get(Calendar.DAY_OF_MONTH);

        showDialogOnDateClick();





    }


}


      /*  barChart = (BarChart) findViewById(R.id.barchart);
        barChart.setDrawBarShadow(false);
        barChart.setDrawValueAboveBar(true);
        barChart.setMaxVisibleValueCount(50);
        barChart.setPinchZoom(true);
        barChart.setDrawGridBackground(true);

        ArrayList<BarEntry> barEntries = new ArrayList<>();
        barEntries.add(new BarEntry(1, 20f));
        barEntries.add(new BarEntry(2, 20f));
        barEntries.add(new BarEntry(3, 20f));
        barEntries.add(new BarEntry(4, 20f));


        BarDataSet barDataSet = new BarDataSet(barEntries, "Daily Data");
        barDataSet.setColors(ColorTemplate.COLORFUL_COLORS);

        BarData data = new BarData(barDataSet);
        data.setBarWidth(0.9f);
        barChart.setData(data);

        String[] months = new String[]{"Jan", "FEb", "Mar", "Apr", "May", "jun"};
        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new MyXAxisValueFormatter(months));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1);
        xAxis.setCenterAxisLabels(true);
        xAxis.setAxisMinimum(1);

        String[] time = new String[]{"a", "b", "c", "d", "e", "f"};
        YAxis yAxis=barChart.getAxisLeft();
        yAxis.setValueFormatter(new MyYAxisValueFormatter(months));
        yAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        yAxis.setGranularity(1);
        yAxis.setCenterAxisLabels(true);
        yAxis.setAxisMinimum(1);


    }

    public class MyXAxisValueFormatter implements IAxisValueFormatter {

        private String[] mvalues;

        public MyXAxisValueFormatter(String[] values) {
            this.mvalues = values;

        }

        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            return mvalues[(int) value];
        }
    }

    public class MyYAxisValueFormatter implements IAxisValueFormatter {

        private String[] mvalues;

        public MyYAxisValueFormatter(String[] values) {
            this.mvalues = values;

        }

        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            return mvalues[(int) value];
        }
    }
}

*/


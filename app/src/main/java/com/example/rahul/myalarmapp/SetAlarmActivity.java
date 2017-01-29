package com.example.rahul.myalarmapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TimePicker;

import java.util.ArrayList;
import java.util.List;

public class SetAlarmActivity extends AppCompatActivity {
    private TimePicker timePicker;
    private Spinner ringBeforeSpinner;
    private Spinner intervalSpinner;
    private int ringBeforeData[] = {10, 15, 30, 45, 60};
    private int intervalData[] = {1, 2, 5, 10, 15, 20};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_alarm);

        timePicker = (TimePicker) findViewById(R.id.timePicker);
        timePicker.setIs24HourView(true);

        List<String> list1 = new ArrayList<String>();
        for (int a : ringBeforeData) {
           list1.add(a + " mins");
        }
        ArrayAdapter<String> dataAdapter1= new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, list1);
        dataAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        ringBeforeSpinner = (Spinner) findViewById(R.id.ringBeforeSpinner);
        ringBeforeSpinner.setAdapter(dataAdapter1);
        ringBeforeSpinner.setVerticalScrollbarPosition(1);

        List<String> list2 = new ArrayList<String>();
        for (int a : intervalData) {
            list2.add(a + " mins");
        }
        ArrayAdapter<String> dataAdapter2 = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, list2);
        dataAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        intervalSpinner = (Spinner) findViewById(R.id.intervalSpinner);
        intervalSpinner.setAdapter(dataAdapter2);
        intervalSpinner.setVerticalScrollbarPosition(1);
    }

    public void doneClicked(View view) {
        int finalHour = timePicker.getHour();
        int finalMin = timePicker.getMinute();
        int beforeMin = ringBeforeData[ringBeforeSpinner.getSelectedItemPosition()];
        int intervalMin = intervalData[intervalSpinner.getSelectedItemPosition()];

        getIntent().putExtra("FinalHour", finalHour);
        getIntent().putExtra("FinalMin", finalMin);
        getIntent().putExtra("BeforeMin", beforeMin);
        getIntent().putExtra("IntervalMin", intervalMin);

        setResult(RESULT_OK, getIntent());
        finish();
    }
}

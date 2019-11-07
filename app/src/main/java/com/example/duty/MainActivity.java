package com.example.duty;

import android.annotation.SuppressLint;

import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;


import com.example.duty.base.BaseActivity;
import com.haibin.calendarview.Calendar;
import com.haibin.calendarview.CalendarLayout;
import com.haibin.calendarview.CalendarView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends BaseActivity implements
        CalendarView.OnCalendarSelectListener,
        CalendarView.OnMonthChangeListener,
        CalendarView.OnYearChangeListener{

    final String FLAG_DATE = "2019-11-04";

    final String duties[] = {"值班", "半休", "正修", "白班"};

    final int colors[] = {0xFF40db25, 0xFFe69138, 0xFFaacc44, 0xFF4a4bd2};

    TextView mTextMonthDay;

    TextView mTextYear;

    TextView mTextSchedule;

    TextView mTextCurrentDay;

    TextView mTextLunar;

    CalendarView mCalendarView;

    RelativeLayout mRelativeTool;

    CalendarLayout mCalendarLayout;

    private int mYear;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void initView() {
        setStatusBarDarkMode();
        mTextMonthDay = findViewById(R.id.tv_month_day);
        mTextYear = findViewById(R.id.tv_year);
        mTextLunar = findViewById(R.id.tv_lunar);

        mRelativeTool = findViewById(R.id.rl_tool);
        mCalendarView = findViewById(R.id.calendarView);
        mTextCurrentDay = findViewById(R.id.tv_current_day);
        mTextSchedule = findViewById(R.id.tv_schedule);
        mTextMonthDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mCalendarLayout.isExpand()) {
                    mCalendarLayout.expand();
                    return;
                }
                mCalendarView.showYearSelectLayout(mYear);
                mTextLunar.setVisibility(View.GONE);
                mTextYear.setVisibility(View.GONE);
                mTextMonthDay.setText(String.valueOf(mYear));
            }
        });

        findViewById(R.id.fl_current).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = new Calendar();

                Date date = new Date();
                java.util.Calendar originCalendar = java.util.Calendar.getInstance();
                originCalendar.setTime(date);
                int year = originCalendar.get(java.util.Calendar.YEAR);
                int month = originCalendar.get(java.util.Calendar.MONTH);
                int day = originCalendar.get(java.util.Calendar.DAY_OF_MONTH);

                mCalendarView.scrollToCalendar(year, month+1, day);
            }
        });

        mCalendarLayout = findViewById(R.id.calendarLayout);
        mTextYear.setText(String.valueOf(mCalendarView.getCurYear()));
        mYear = mCalendarView.getCurYear();
        mTextMonthDay.setText(mCalendarView.getCurMonth() + "月" + mCalendarView.getCurDay() + "日");
        mTextLunar.setText("今日");
        mTextCurrentDay.setText(String.valueOf(mCalendarView.getCurDay()));

        // Listeners
        mCalendarView.setOnCalendarSelectListener(this);
        mCalendarView.setOnMonthChangeListener(this);
        mCalendarView.setOnYearChangeListener(this);
    }

    @SuppressWarnings("unused")
    @Override
    protected void initData() {
        Map<String, Calendar> map = new HashMap<>();
        Date flagDate = getFlagDate();
        int n = 0;
        do {
            java.util.Calendar calendar = java.util.Calendar.getInstance();
            calendar.setTime(flagDate);
            int year = calendar.get(java.util.Calendar.YEAR);
            int month = calendar.get(java.util.Calendar.MONTH);
            int day = calendar.get(java.util.Calendar.DAY_OF_MONTH);
            int key = n % 4;

            Calendar cal = getSchemeCalendar(year, month + 1, day, colors[key], duties[key]);
            map.put(cal.toString(), cal);

            flagDate = new Date(flagDate.getTime() + 24*3600*1000);
        } while (n++ < 1000);

        mCalendarView.setSchemeDate(map);
    }


    protected Date getFlagDate()
    {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date flagDate = null;
        try {
            flagDate = formatter.parse(FLAG_DATE);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return flagDate;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onCalendarSelect(Calendar calendar, boolean isClick) {
        mTextLunar.setVisibility(View.VISIBLE);
        mTextYear.setVisibility(View.VISIBLE);
        mTextMonthDay.setText(calendar.getMonth() + "月" + calendar.getDay() + "日");
        mTextYear.setText(String.valueOf(calendar.getYear()));
        mTextLunar.setText(calendar.getLunar());
        mYear = calendar.getYear();
        mTextSchedule.setText(calendar.getScheme());
    }

    @Override
    public void onCalendarOutOfRange(Calendar calendar) {
//        Toast.makeText(this, String.format("%s : OutOfRange", calendar), Toast.LENGTH_SHORT).show();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onMonthChange(int year, int month) {
        Calendar calendar = mCalendarView.getSelectedCalendar();
        mTextLunar.setVisibility(View.VISIBLE);
        mTextYear.setVisibility(View.VISIBLE);
        mTextMonthDay.setText(calendar.getMonth() + "月" + calendar.getDay() + "日");
        mTextYear.setText(String.valueOf(calendar.getYear()));
        mTextLunar.setText(calendar.getLunar());
        mYear = calendar.getYear();
    }

    @Override
    public void onYearChange(int year) {
        mTextMonthDay.setText(String.valueOf(year));
//        Log.e("onYearChange", " 年份变化 " + year);
    }

    private Calendar getSchemeCalendar(int year, int month, int day, int color, String text) {
        Calendar calendar = new Calendar();
        calendar.setYear(year);
        calendar.setMonth(month);
        calendar.setDay(day);
        calendar.setSchemeColor(color);//如果单独标记颜色、则会使用这个颜色
        calendar.setScheme(text);
        return calendar;
    }
}



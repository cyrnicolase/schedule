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

    final String FLAG_DATE = "2019-12-02";

    final String Test_DATE = "2020-06-31";  // 实际上是2020-05-31 ,因为Java month要+1

    final String duties[] = {"值班", "夜修", "正修", "正修", "白班", "门诊"};

    final int colors[] = {0xFF40db25, 0xFFe69138, 0xFFaacc44, 0xFFaacc44, 0xFFcda1af, 0xFF22acaf};

    TextView mTextMonthDay;

    TextView mTextYear;

    TextView mTextSchedule;

    TextView mTextCurrentDay;

    TextView mTextLunar;

    TextView mTextTest;

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
        mTextTest = findViewById(R.id.tv_test);
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
                mTextSchedule.setVisibility(View.GONE);
                mTextMonthDay.setText(String.valueOf(mYear));
            }
        });

        findViewById(R.id.fl_current).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Calendar calendar = new Calendar();

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

        // Test day
        Calendar calendar = mCalendarView.getSelectedCalendar();
        mTextTest.setText(String.valueOf(getTestDateCount(calendar)));
        rePlan(calendar);

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
            int key = n % 6;

            Calendar cal = getSchemeCalendar(year, month + 1, day, colors[key], duties[key]);
            map.put(cal.toString(), cal);

            flagDate = new Date(flagDate.getTime() + 24*3600*1000);
        } while (n++ < 1000);

        mCalendarView.setSchemeDate(map);
    }


    protected Date getFlagDate()
    {
        return parseDate(FLAG_DATE);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onCalendarSelect(Calendar calendar, boolean isClick) {
        rePlan(calendar);
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
        rePlan(calendar);
    }

    protected void rePlan(Calendar calendar)
    {
        mTextLunar.setVisibility(View.VISIBLE);
        mTextYear.setVisibility(View.VISIBLE);
        mTextSchedule.setVisibility(View.VISIBLE);
        mTextMonthDay.setText(calendar.getMonth() + "月" + calendar.getDay() + "日");
        mTextYear.setText(String.valueOf(calendar.getYear()));
        mTextLunar.setText(calendar.getLunar());
        mYear = calendar.getYear();
        mTextTest.setText(String.valueOf(getTestDateCount(calendar)));
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
        calendar.setSchemeColor(color); //如果单独标记颜色、则会使用这个颜色
        calendar.setScheme(text);

        return calendar;
    }

    private int getTestDateCount(Calendar calendar) {
        int year = calendar.getYear();
        int month = calendar.getMonth();
        int day = calendar.getDay();

        String current = String.valueOf(year) +"-"+ String.valueOf(month + 1) +"-"+ String.valueOf(day);
        Date currentDate = parseDate(current);
        Date targetDate = parseDate(Test_DATE);

        long d = (targetDate.getTime() - currentDate.getTime()) / 1000 / 3600 / 24;

        return (int) d;

    }

    private Date parseDate(String strDate)
    {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date outputDate = null;
        try {
            outputDate = formatter.parse(strDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return outputDate;
    }
}



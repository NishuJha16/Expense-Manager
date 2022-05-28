package com.example.expensemanager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class ChooseAnalyticsActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private CardView dailyCardView, weeklyCardView, monthlyCardView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_analytics);

        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Choose Analytics");
        dailyCardView=findViewById(R.id.dailyCardView);
        weeklyCardView=findViewById(R.id.weeklyCardView);
        monthlyCardView=findViewById(R.id.monthlyCardView);

        dailyCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ChooseAnalyticsActivity.this, DailyAnalyticsActivity.class));
            }
        });

        weeklyCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ChooseAnalyticsActivity.this, WeeklyAnalyticsActivity.class));
            }
        });

        monthlyCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ChooseAnalyticsActivity.this, MonthlyAnalyticsActivity.class));
            }
        });

    }
}
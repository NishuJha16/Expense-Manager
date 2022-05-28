package com.example.expensemanager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Pie;
import com.anychart.enums.Align;
import com.anychart.enums.LegendLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.joda.time.DateTime;
import org.joda.time.Months;
import org.joda.time.MutableDateTime;
import org.joda.time.Weeks;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.TimerTask;

public class MonthlyAnalyticsActivity extends AppCompatActivity {

    private Toolbar toolbar;

    private FirebaseAuth mAuth;
    private String onlineUserId = "";
    private DatabaseReference expenseRef, personalRef;

    private TextView totalSpentAmountTv, analyticsTransportAmount, analyticsFoodAmount, analyticsHouseAmount,analyticsSummaryAmount, analyticsEntertainmentAmount, analyticsEducationAmount, analyticsCharityAmount, analyticsApparelAmount, analyticsHealthAmount, analyticsPersonalAmount, analyticsOtherAmount;
    private TextView progressRatioTransport,progressRatioFood,progressRatioHouse,progressRatioEntertainment,progressRatioEducation,progressRatioCharity,progressRatioApparel,progressRatioHealth,progressRatioPersonal,progressRatioOther,progressRatioOverall;

    private ImageView statusTransport, statusFood, statusHouse, statusEntertainment, statusEducation, statusCharity, statusApparel, statusHealth, statusPersonal, statusOther, statusOverall;

    private CardView layoutTransport,layoutFood,layoutHouse,layoutEntertainment,layoutEducation,layoutCharity,layoutApparel,layoutHealth,layoutPersonal,layoutOther,layoutOverall;
    private AnyChartView anyChartView;

    private ProgressBar progressBar;

    private LinearLayout noSpentLayout,statusTab,linearLayoutData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monthly_analytics);

        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Monthly Analytics");

        mAuth = FirebaseAuth.getInstance();
        onlineUserId = mAuth.getCurrentUser().getUid();
        MutableDateTime epoch =new MutableDateTime();
        epoch.setDate(0);
        DateTime now = new DateTime();
        Months months=Months.monthsBetween(epoch,now);
        expenseRef= FirebaseDatabase.getInstance().getReference("expenses").child(onlineUserId);
        personalRef= FirebaseDatabase.getInstance().getReference("personal").child(onlineUserId);

        anyChartView=findViewById(R.id.anyChartView);

        noSpentLayout=findViewById(R.id.noSpentLayout);
        statusTab=findViewById(R.id.statusTab);
        linearLayoutData=findViewById(R.id.linearLayoutData);
        linearLayoutData.setVisibility(View.GONE);

        progressBar = findViewById(R.id.progressBar);

        totalSpentAmountTv=findViewById(R.id.totalSpentAmount);
        analyticsTransportAmount=findViewById(R.id.analyticsTransportAmount);
        analyticsFoodAmount=findViewById(R.id.analyticsFoodAmount);
        analyticsHouseAmount=findViewById(R.id.analyticsHouseAmount);
        analyticsEntertainmentAmount=findViewById(R.id.analyticsEntertainmentAmount);
        analyticsEducationAmount=findViewById(R.id.analyticsEducationAmount);
        analyticsCharityAmount=findViewById(R.id.analyticsCharityAmount);
        analyticsApparelAmount=findViewById(R.id.analyticsApparelAmount);
        analyticsHealthAmount=findViewById(R.id.analyticsHealthAmount);
        analyticsPersonalAmount=findViewById(R.id.analyticsPersonalAmount);
        analyticsOtherAmount=findViewById(R.id.analyticsOtherAmount);
        analyticsSummaryAmount=findViewById(R.id.analyticsSummaryAmount);


        progressRatioTransport=findViewById(R.id.progressRatioTransport);
        progressRatioFood=findViewById(R.id.progressRatioFood);
        progressRatioHouse=findViewById(R.id.progressRatioHouse);
        progressRatioEntertainment=findViewById(R.id.progressRatioEntertainment);
        progressRatioEducation=findViewById(R.id.progressRatioEducation);
        progressRatioCharity=findViewById(R.id.progressRatioCharity);
        progressRatioApparel=findViewById(R.id.progressRatioApparel);
        progressRatioHealth=findViewById(R.id.progressRatioHealth);
        progressRatioPersonal=findViewById(R.id.progressRatioPersonal);
        progressRatioOther=findViewById(R.id.progressRatioOther);
        progressRatioOverall=findViewById(R.id.progressRatioOverall);

        statusTransport=findViewById(R.id.statusTransport);
        statusFood=findViewById(R.id.statusFood);
        statusHouse=findViewById(R.id.statusHome);
        statusEntertainment=findViewById(R.id.statusEntertainment);
        statusEducation=findViewById(R.id.statusEducation);
        statusCharity=findViewById(R.id.statusCharity);
        statusApparel=findViewById(R.id.statusApparel);
        statusHealth=findViewById(R.id.statusHealth);
        statusPersonal=findViewById(R.id.statusPersonal);
        statusOther=findViewById(R.id.statusOther);
        statusOverall=findViewById(R.id.statusOverall);

        layoutTransport=findViewById(R.id.layoutTransport);
        layoutFood=findViewById(R.id.layoutFood);
        layoutHouse=findViewById(R.id.layoutHouse);
        layoutEntertainment=findViewById(R.id.layoutEntertainment);
        layoutEducation=findViewById(R.id.layoutEducation);
        layoutCharity=findViewById(R.id.layoutCharity);
        layoutApparel=findViewById(R.id.layoutApparel);
        layoutHealth=findViewById(R.id.layoutHealth);
        layoutPersonal=findViewById(R.id.layoutPersonal);
        layoutOther=findViewById(R.id.layoutOther);
        layoutOverall=findViewById(R.id.layoutTotal);

        progressBar.setVisibility(View.VISIBLE);

        getTotalTransportExpense();
        getTotalFoodExpense();
        getTotalHouseExpense();
        getTotalEntertainmentExpense();
        getTotalEducationExpense();
        getTotalCharityExpense();
        getTotalApparelExpense();
        getTotalHealthExpense();
        getTotalPersonalExpense();
        getTotalOtherExpense();

        getTotalMonthSpending();

        new java.util.Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                loadGraph();
                setStatusAndImageResource();

            }
        },2000);

        final Handler handler=new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.GONE);
                linearLayoutData.setVisibility(View.VISIBLE);
            }
        },2000);
    }
    private void setStatusAndImageResource () {
        personalRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    int transportTotal;
                    if(snapshot.hasChild("monthlyTransport")){
                        transportTotal=Integer.parseInt(snapshot.child("monthlyTransport").getValue().toString());
                    } else {
                        transportTotal=0;
                    }

                    int foodTotal;
                    if(snapshot.hasChild("monthlyFood")){
                        foodTotal=Integer.parseInt(snapshot.child("monthlyFood").getValue().toString());
                    } else {
                        foodTotal=0;
                    }

                    int houseTotal;
                    if(snapshot.hasChild("monthlyHouse")){
                        houseTotal=Integer.parseInt(snapshot.child("monthlyHouse").getValue().toString());
                    } else {
                        houseTotal=0;
                    }

                    int entertainmentTotal;
                    if(snapshot.hasChild("monthlyEntertainment")){
                        entertainmentTotal=Integer.parseInt(snapshot.child("monthlyEntertainment").getValue().toString());
                    } else {
                        entertainmentTotal=0;
                    }

                    int educationTotal;
                    if(snapshot.hasChild("monthlyEducation")){
                        educationTotal=Integer.parseInt(snapshot.child("monthlyEducation").getValue().toString());
                    } else {
                        educationTotal=0;
                    }

                    int healthTotal;
                    if(snapshot.hasChild("monthlyHealth")){
                        healthTotal=Integer.parseInt(snapshot.child("monthlyHealth").getValue().toString());
                    } else {
                        healthTotal=0;
                    }

                    int charityTotal;
                    if(snapshot.hasChild("monthlyCharity")){
                        charityTotal=Integer.parseInt(snapshot.child("monthlyCharity").getValue().toString());
                    } else {
                        charityTotal=0;
                    }

                    int personalTotal;
                    if(snapshot.hasChild("monthlyPersonal")){
                        personalTotal=Integer.parseInt(snapshot.child("monthlyPersonal").getValue().toString());
                    } else {
                        personalTotal=0;
                    }

                    int apparelTotal;
                    if(snapshot.hasChild("monthlyApparel")){
                        apparelTotal=Integer.parseInt(snapshot.child("monthlyApparel").getValue().toString());
                    } else {
                        apparelTotal=0;
                    }

                    int otherTotal;
                    if(snapshot.hasChild("monthlyOther")){
                        otherTotal=Integer.parseInt(snapshot.child("monthlyOther").getValue().toString());
                    } else {
                        otherTotal=0;
                    }
                    int todayTotal;
                    if(snapshot.hasChild("month")){
                        todayTotal=Integer.parseInt(snapshot.child("month").getValue().toString());
                    } else {
                        todayTotal=0;
                    }

                    //GETTING RATIOS

                    float transportTotalRatio;
                    if(snapshot.hasChild("monthTransportRatio")){
                        transportTotalRatio=Float.parseFloat(snapshot.child("monthTransportRatio").getValue().toString());
                    } else {
                        transportTotalRatio=0;
                    }

                    float foodTotalRatio;
                    if(snapshot.hasChild("monthFoodRatio")){
                        foodTotalRatio=Float.parseFloat(snapshot.child("monthFoodRatio").getValue().toString());
                    } else {
                        foodTotalRatio=0;
                    }

                    float houseTotalRatio;
                    if(snapshot.hasChild("monthHouseRatio")){
                        houseTotalRatio=Float.parseFloat(snapshot.child("monthHouseRatio").getValue().toString());
                    } else {
                        houseTotalRatio=0;
                    }

                    float entertainmentTotalRatio;
                    if(snapshot.hasChild("monthEntertainmentRatio")){
                        entertainmentTotalRatio=Float.parseFloat(snapshot.child("monthEntertainmentRatio").getValue().toString());
                    } else {
                        entertainmentTotalRatio=0;
                    }

                    float educationTotalRatio;
                    if(snapshot.hasChild("monthEducationRatio")){
                        educationTotalRatio=Float.parseFloat(snapshot.child("monthEducationRatio").getValue().toString());
                    } else {
                        educationTotalRatio=0;
                    }

                    float healthTotalRatio;
                    if(snapshot.hasChild("monthHealthRatio")){
                        healthTotalRatio=Float.parseFloat(snapshot.child("monthHealthRatio").getValue().toString());
                    } else {
                        healthTotalRatio=0;
                    }

                    float charityTotalRatio;
                    if(snapshot.hasChild("monthCharityRatio")){
                        charityTotalRatio=Float.parseFloat(snapshot.child("monthCharityRatio").getValue().toString());
                    } else {
                        charityTotalRatio=0;
                    }

                    float personalTotalRatio;
                    if(snapshot.hasChild("monthPersonalRatio")){
                        personalTotalRatio=Float.parseFloat(snapshot.child("monthPersonalRatio").getValue().toString());
                    } else {
                        personalTotalRatio=0;
                    }

                    float apparelTotalRatio;
                    if(snapshot.hasChild("monthApparelRatio")){
                        apparelTotalRatio=Float.parseFloat(snapshot.child("monthApparelRatio").getValue().toString());
                    } else {
                        apparelTotalRatio=0;
                    }

                    float otherTotalRatio;
                    if(snapshot.hasChild("monthOtherRatio")){
                        otherTotalRatio=Float.parseFloat(snapshot.child("monthOtherRatio").getValue().toString());
                    } else {
                        otherTotalRatio=0;
                    }

                    float dailyTotalRatio;
                    if(snapshot.hasChild("budget")){
                        dailyTotalRatio=Float.parseFloat(Objects.requireNonNull(snapshot.child("budget").getValue()).toString());
                    } else {
                        dailyTotalRatio=0;
                    }

                    DecimalFormat df = new DecimalFormat("#.##");

                    float dailyPercent = (todayTotal/dailyTotalRatio)*100;
                    if(dailyPercent<50){
                        progressRatioOverall.setText(df.format(dailyPercent)+"% used of ₹"+df.format(dailyTotalRatio));
                        statusOverall.setImageResource(R.drawable.ic_flag_green);
                    } else if(dailyPercent>=50 && dailyPercent<100){
                        progressRatioOverall.setText(df.format(dailyPercent)+"% used of ₹"+df.format(dailyTotalRatio));
                        statusOverall.setImageResource(R.drawable.ic_flag_yellow);
                    } else {
                        progressRatioOverall.setText("Budget Exceeded");
                        statusOverall.setImageResource(R.drawable.ic_flag_red);
                    }

                    float transportPercent = (transportTotal/transportTotalRatio)*100;
                    if(transportPercent<50){
                        progressRatioTransport.setText(df.format(transportPercent)+"% used of ₹"+df.format(transportTotalRatio));
                        statusTransport.setImageResource(R.drawable.ic_flag_green);
                    } else if(transportPercent>=50 && transportPercent<100){
                        progressRatioTransport.setText(df.format(transportPercent)+"% used of ₹"+df.format(transportTotalRatio));
                        statusTransport.setImageResource(R.drawable.ic_flag_yellow);
                    } else {
                        progressRatioTransport.setText("Budget Exceeded");
                        statusTransport.setImageResource(R.drawable.ic_flag_red);
                    }

                    float apparelPercent = (apparelTotal/apparelTotalRatio)*100;
                    if(apparelPercent<50){
                        progressRatioApparel.setText(df.format(apparelPercent)+"% used of ₹"+df.format(apparelTotalRatio));
                        statusApparel.setImageResource(R.drawable.ic_flag_green);
                    } else if(apparelPercent>=50 && apparelPercent<100){
                        progressRatioApparel.setText(df.format(df.format(apparelPercent))+"% used of ₹"+df.format(apparelTotalRatio));
                        statusApparel.setImageResource(R.drawable.ic_flag_yellow);
                    } else {
                        progressRatioApparel.setText("Budget Exceeded");
                        statusApparel.setImageResource(R.drawable.ic_flag_red);
                    }

                    float foodPercent = (foodTotal/foodTotalRatio)*100;
                    if(foodPercent<50){
                        progressRatioFood.setText(df.format(foodPercent)+"% used of ₹"+df.format(foodTotalRatio));
                        statusFood.setImageResource(R.drawable.ic_flag_green);
                    } else if(foodPercent>=50 && foodPercent<100){
                        progressRatioFood.setText(df.format(foodPercent)+"% used of ₹"+df.format(foodTotalRatio));
                        statusFood.setImageResource(R.drawable.ic_flag_yellow);
                    } else {
                        progressRatioFood.setText("Budget Exceeded");
                        statusFood.setImageResource(R.drawable.ic_flag_red);
                    }

                    float healthPercent = (healthTotal/healthTotalRatio)*100;
                    if(healthPercent<50){
                        progressRatioHealth.setText(df.format(healthPercent)+"% used of ₹"+df.format(healthTotalRatio));
                        statusHealth.setImageResource(R.drawable.ic_flag_green);
                    } else if(healthPercent>=50 && healthPercent<100){
                        progressRatioHealth.setText(df.format(healthPercent)+"% used of ₹"+df.format(healthTotalRatio));
                        statusHealth.setImageResource(R.drawable.ic_flag_yellow);
                    } else {
                        progressRatioHealth.setText("Budget Exceeded");
                        statusHealth.setImageResource(R.drawable.ic_flag_red);
                    }

                    float housePercent = (houseTotal/houseTotalRatio)*100;
                    if(housePercent<50){
                        progressRatioHouse.setText(df.format(housePercent)+"% used of ₹"+df.format(houseTotalRatio));
                        statusHouse.setImageResource(R.drawable.ic_flag_green);
                    } else if(housePercent>=50 && housePercent<100){
                        progressRatioHouse.setText(df.format(housePercent)+"% used of ₹"+df.format(houseTotalRatio));
                        statusHouse.setImageResource(R.drawable.ic_flag_yellow);
                    } else {
                        progressRatioHouse.setText("Budget Exceeded");
                        statusHouse.setImageResource(R.drawable.ic_flag_red);
                    }

                    float entertainmentPercent = (entertainmentTotal/entertainmentTotalRatio)*100;
                    if(entertainmentPercent<50){
                        progressRatioEntertainment.setText(df.format(entertainmentPercent)+"% used of ₹"+df.format(entertainmentTotalRatio));
                        statusEntertainment.setImageResource(R.drawable.ic_flag_green);
                    } else if(entertainmentPercent>=50 && entertainmentPercent<100){
                        progressRatioEntertainment.setText(df.format(entertainmentPercent)+"% used of ₹"+df.format(entertainmentTotalRatio));
                        statusEntertainment.setImageResource(R.drawable.ic_flag_yellow);
                    } else {
                        progressRatioEntertainment.setText("Budget Exceeded");
                        statusEntertainment.setImageResource(R.drawable.ic_flag_red);
                    }

                    float educationPercent = (educationTotal/educationTotalRatio)*100;
                    if(educationPercent<50){
                        progressRatioEducation.setText(df.format(educationPercent)+"% used of ₹"+df.format(educationTotalRatio));
                        statusEducation.setImageResource(R.drawable.ic_flag_green);
                    } else if(educationPercent>=50 && educationPercent<100){
                        progressRatioEducation.setText(df.format(educationPercent)+"% used of ₹"+df.format(educationTotalRatio));
                        statusEducation.setImageResource(R.drawable.ic_flag_yellow);
                    } else {
                        progressRatioEducation.setText("Budget Exceeded");
                        statusEducation.setImageResource(R.drawable.ic_flag_red);
                    }

                    float charityPercent = (charityTotal/charityTotalRatio)*100;
                    if(charityPercent<50){
                        progressRatioCharity.setText(df.format(charityPercent)+"% used of ₹"+df.format(charityTotalRatio));
                        statusCharity.setImageResource(R.drawable.ic_flag_green);
                    } else if(charityPercent>=50 && charityPercent<100){
                        progressRatioCharity.setText(df.format(charityPercent)+"% used of ₹"+df.format(charityTotalRatio));
                        statusCharity.setImageResource(R.drawable.ic_flag_yellow);
                    } else {
                        progressRatioCharity.setText("Budget Exceeded");
                        statusCharity.setImageResource(R.drawable.ic_flag_red);
                    }

                    float personalPercent = (personalTotal/personalTotalRatio)*100;
                    if(personalPercent<50){
                        progressRatioPersonal.setText(df.format(personalPercent)+"% used of ₹"+df.format(personalTotalRatio));
                        statusPersonal.setImageResource(R.drawable.ic_flag_green);
                    } else if(transportPercent>=50 && dailyPercent<100){
                        progressRatioPersonal.setText(df.format(personalPercent)+"% used of ₹"+df.format(personalTotalRatio));
                        statusPersonal.setImageResource(R.drawable.ic_flag_yellow);
                    } else {
                        progressRatioPersonal.setText("Budget Exceeded");
                        statusPersonal.setImageResource(R.drawable.ic_flag_red);
                    }

                    float otherPercent = (otherTotal/otherTotalRatio)*100;
                    if(otherPercent<50){
                        progressRatioOther.setText(df.format(otherPercent)+"% used of ₹"+df.format(otherTotalRatio));
                        statusOther.setImageResource(R.drawable.ic_flag_green);
                    } else if(otherPercent>=50 && otherPercent<100){
                        progressRatioOther.setText(df.format(otherPercent)+"% used of ₹"+df.format(otherTotalRatio));
                        statusOther.setImageResource(R.drawable.ic_flag_yellow);
                    } else {
                        progressRatioOther.setText("Budget Exceeded");
                        statusOther.setImageResource(R.drawable.ic_flag_red);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private  void loadGraph() {
        personalRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    int transportTotal;
                    if(snapshot.hasChild("monthlyTransport")){
                        transportTotal=Integer.parseInt(snapshot.child("monthlyTransport").getValue().toString());
                    } else {
                        transportTotal=0;
                    }

                    int foodTotal;
                    if(snapshot.hasChild("monthlyFood")){
                        foodTotal=Integer.parseInt(snapshot.child("monthlyFood").getValue().toString());
                    } else {
                        foodTotal=0;
                    }

                    int houseTotal;
                    if(snapshot.hasChild("monthlyHouse")){
                        houseTotal=Integer.parseInt(snapshot.child("monthlyHouse").getValue().toString());
                    } else {
                        houseTotal=0;
                    }

                    int entertainmentTotal;
                    if(snapshot.hasChild("monthlyEntertainment")){
                        entertainmentTotal=Integer.parseInt(snapshot.child("monthlyEntertainment").getValue().toString());
                    } else {
                        entertainmentTotal=0;
                    }

                    int educationTotal;
                    if(snapshot.hasChild("monthlyEducation")){
                        educationTotal=Integer.parseInt(snapshot.child("monthlyEducation").getValue().toString());
                    } else {
                        educationTotal=0;
                    }

                    int healthTotal;
                    if(snapshot.hasChild("monthlyHealth")){
                        healthTotal=Integer.parseInt(snapshot.child("monthlyHealth").getValue().toString());
                    } else {
                        healthTotal=0;
                    }

                    int charityTotal;
                    if(snapshot.hasChild("monthlyCharity")){
                        charityTotal=Integer.parseInt(snapshot.child("monthlyCharity").getValue().toString());
                    } else {
                        charityTotal=0;
                    }

                    int personalTotal;
                    if(snapshot.hasChild("monthlyPersonal")){
                        personalTotal=Integer.parseInt(snapshot.child("monthlyPersonal").getValue().toString());
                    } else {
                        personalTotal=0;
                    }

                    int apparelTotal;
                    if(snapshot.hasChild("monthlyApparel")){
                        apparelTotal=Integer.parseInt(snapshot.child("monthlyApparel").getValue().toString());
                    } else {
                        apparelTotal=0;
                    }

                    int otherTotal;
                    if(snapshot.hasChild("monthlyOther")){
                        otherTotal=Integer.parseInt(snapshot.child("monthlyOther").getValue().toString());
                    } else {
                        otherTotal=0;
                    }

                    Pie pie = AnyChart.pie();
                    List<DataEntry> data= new ArrayList<>();
                    data.add(new ValueDataEntry("Transport",transportTotal));
                    data.add(new ValueDataEntry("Food",foodTotal));
                    data.add(new ValueDataEntry("House",houseTotal));
                    data.add(new ValueDataEntry("Entertainment",entertainmentTotal));
                    data.add(new ValueDataEntry("Education",educationTotal));
                    data.add(new ValueDataEntry("Charity",charityTotal));
                    data.add(new ValueDataEntry("Health",healthTotal));
                    data.add(new ValueDataEntry("Apparel",apparelTotal));
                    data.add(new ValueDataEntry("Personal",personalTotal));
                    data.add(new ValueDataEntry("Other",otherTotal));

                    pie.data(data);
                    pie.title("Monthly Analytics");
                    pie.labels().position("outside");
                    pie.legend().title().enabled(true);
                    pie.legend().title()
                            .text("Items spent on")
                            .padding(0d,0d,10d,0d);

                    pie.legend()
                            .position("center-bottom")
                            .itemsLayout(LegendLayout.HORIZONTAL)
                            .align(Align.CENTER);
                    anyChartView.setChart(pie);

                } else {
                    Toast.makeText(MonthlyAnalyticsActivity.this, "Child does not exists",Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getTotalMonthSpending() {
        String date =new SimpleDateFormat("dd-MM-yyy", Locale.getDefault()).format(new Date());
        MutableDateTime epoch =new MutableDateTime();
        epoch.setDate(0);
        DateTime now = new DateTime();
        Weeks weeks=Weeks.weeksBetween(epoch,now);
        Months months=Months.monthsBetween(epoch,now);
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("expenses").child(onlineUserId);
        Query query= reference.orderByChild("month").equalTo(months.getMonths());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists() && snapshot.getChildrenCount()>0) {
                    noSpentLayout.setVisibility(View.GONE);
                    statusTab.setVisibility(View.VISIBLE);
                    int totalAmount = 0;
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Map<String, Object> map = (Map<String, Object>) ds.getValue();
                        Object total = map.get("amount");
                        int pTotal = Integer.parseInt(String.valueOf(total));
                        totalAmount += pTotal;
                    }
                    analyticsSummaryAmount.setText("Total Spent Amount: ₹" + String.valueOf(totalAmount));
                    totalSpentAmountTv.setText("Total Spending: ₹"+ String.valueOf(totalAmount));
                } else {
                    noSpentLayout.setVisibility(View.VISIBLE);
                    statusTab.setVisibility(View.GONE);
                    anyChartView.setVisibility(View.GONE);
                    layoutOverall.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void getTotalOtherExpense() {
        String date =new SimpleDateFormat("dd-MM-yyy", Locale.getDefault()).format(new Date());
        MutableDateTime epoch =new MutableDateTime();
        epoch.setDate(0);
        DateTime now = new DateTime();
        Months months=Months.monthsBetween(epoch,now);
        String itemMonth = "Other"+months.getMonths();
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("expenses").child(onlineUserId);
        Query query= reference.orderByChild("itemMonth").equalTo(itemMonth);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    int totalAmount = 0;
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Map<String, Object> map = (Map<String, Object>) ds.getValue();
                        Object total = map.get("amount");
                        int pTotal = Integer.parseInt(String.valueOf(total));
                        totalAmount += pTotal;
                        Log.i("total",String.valueOf(pTotal));
                        analyticsOtherAmount.setText("Spent: ₹" + String.valueOf(totalAmount));
                    }
                    personalRef.child("monthlyOther").setValue(totalAmount);
                } else {
                    layoutOther.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getTotalPersonalExpense() {
        String date =new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        MutableDateTime epoch =new MutableDateTime();
        epoch.setDate(0);
        DateTime now = new DateTime();
        Months months=Months.monthsBetween(epoch,now);
        String itemMonth = "Personal"+months.getMonths();
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("expenses").child(onlineUserId);
        Query query= reference.orderByChild("itemMonth").equalTo(itemMonth);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    int totalAmount = 0;
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Map<String, Object> map = (Map<String, Object>) ds.getValue();
                        Object total = map.get("amount");
                        int pTotal = Integer.parseInt(String.valueOf(total));
                        totalAmount += pTotal;
                        analyticsPersonalAmount.setText("Spent: ₹" + String.valueOf(totalAmount));
                    }
                    personalRef.child("monthlyPersonal").setValue(totalAmount);
                } else {
                    layoutPersonal.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getTotalHealthExpense() {
        String date =new SimpleDateFormat("dd-MM-yyy", Locale.getDefault()).format(new Date());
        MutableDateTime epoch =new MutableDateTime();
        epoch.setDate(0);
        DateTime now = new DateTime();
        Months months=Months.monthsBetween(epoch,now);
        String itemMonth = "Health"+months.getMonths();
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("expenses").child(onlineUserId);
        Query query= reference.orderByChild("itemMonth").equalTo(itemMonth);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    int totalAmount = 0;
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Map<String, Object> map = (Map<String, Object>) ds.getValue();
                        Object total = map.get("amount");
                        int pTotal = Integer.parseInt(String.valueOf(total));
                        totalAmount += pTotal;
                        analyticsHealthAmount.setText("Spent: ₹" + String.valueOf(totalAmount));
                    }
                    personalRef.child("monthlyHealth").setValue(totalAmount);
                } else {
                    layoutHealth.setVisibility(View.GONE);
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getTotalApparelExpense() {
        String date =new SimpleDateFormat("dd-MM-yyy", Locale.getDefault()).format(new Date());
        MutableDateTime epoch =new MutableDateTime();
        epoch.setDate(0);
        DateTime now = new DateTime();
        Months months=Months.monthsBetween(epoch,now);
        String itemMonth = "Apparel"+months.getMonths();
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("expenses").child(onlineUserId);
        Query query= reference.orderByChild("itemMonth").equalTo(itemMonth);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    int totalAmount = 0;
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Map<String, Object> map = (Map<String, Object>) ds.getValue();
                        Object total = map.get("amount");
                        int pTotal = Integer.parseInt(String.valueOf(total));
                        totalAmount += pTotal;
                        analyticsApparelAmount.setText("Spent: ₹" + String.valueOf(totalAmount));
                    }
                    personalRef.child("monthlyApparel").setValue(totalAmount);
                } else {
                    layoutApparel.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getTotalCharityExpense() {
        String date =new SimpleDateFormat("dd-MM-yyy", Locale.getDefault()).format(new Date());
        MutableDateTime epoch =new MutableDateTime();
        epoch.setDate(0);
        DateTime now = new DateTime();
        Months months=Months.monthsBetween(epoch,now);
        String itemMonth = "Charity"+months.getMonths();
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("expenses").child(onlineUserId);
        Query query= reference.orderByChild("itemMonth").equalTo(itemMonth);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    int totalAmount = 0;
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Map<String, Object> map = (Map<String, Object>) ds.getValue();
                        Object total = map.get("amount");
                        int pTotal = Integer.parseInt(String.valueOf(total));
                        totalAmount += pTotal;
                        analyticsCharityAmount.setText("Spent: ₹" + String.valueOf(totalAmount));
                    }
                    personalRef.child("monthlyCharity").setValue(totalAmount);
                } else {
                    layoutCharity.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getTotalEducationExpense() {
        String date =new SimpleDateFormat("dd-MM-yyy", Locale.getDefault()).format(new Date());
        MutableDateTime epoch =new MutableDateTime();
        epoch.setDate(0);
        DateTime now = new DateTime();
        Months months=Months.monthsBetween(epoch,now);
        String itemMonth = "Education"+months.getMonths();
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("expenses").child(onlineUserId);
        Query query= reference.orderByChild("itemMonth").equalTo(itemMonth);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    int totalAmount = 0;
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Map<String, Object> map = (Map<String, Object>) ds.getValue();
                        Object total = map.get("amount");
                        int pTotal = Integer.parseInt(String.valueOf(total));
                        totalAmount += pTotal;
                        analyticsEducationAmount.setText("Spent: ₹" + String.valueOf(totalAmount));
                    }
                    personalRef.child("monthlyEducation").setValue(totalAmount);
                } else {
                    layoutEducation.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getTotalEntertainmentExpense() {
        String date =new SimpleDateFormat("dd-MM-yyy", Locale.getDefault()).format(new Date());
        MutableDateTime epoch =new MutableDateTime();
        epoch.setDate(0);
        DateTime now = new DateTime();
        Months months=Months.monthsBetween(epoch,now);
        String itemMonth = "Entertainment"+months.getMonths();
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("expenses").child(onlineUserId);
        Query query= reference.orderByChild("itemMonth").equalTo(itemMonth);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    int totalAmount = 0;
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Map<String, Object> map = (Map<String, Object>) ds.getValue();
                        Object total = map.get("amount");
                        int pTotal = Integer.parseInt(String.valueOf(total));
                        totalAmount += pTotal;
                        analyticsEntertainmentAmount.setText("Spent: ₹" + String.valueOf(totalAmount));
                    }
                    personalRef.child("monthlyEntertainment").setValue(totalAmount);
                } else {
                    layoutEntertainment.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getTotalHouseExpense() {
        String date =new SimpleDateFormat("dd-MM-yyy", Locale.getDefault()).format(new Date());
        MutableDateTime epoch =new MutableDateTime();
        epoch.setDate(0);
        DateTime now = new DateTime();
        Months months=Months.monthsBetween(epoch,now);
        String itemMonth = "House"+months.getMonths();
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("expenses").child(onlineUserId);
        Query query= reference.orderByChild("itemMonth").equalTo(itemMonth);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    int totalAmount = 0;
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Map<String, Object> map = (Map<String, Object>) ds.getValue();
                        Object total = map.get("amount");
                        int pTotal = Integer.parseInt(String.valueOf(total));
                        totalAmount += pTotal;
                        analyticsHouseAmount.setText("Spent: ₹" + String.valueOf(totalAmount));
                    }
                    personalRef.child("monthlyHouse").setValue(totalAmount);
                } else {
                    layoutHouse.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getTotalFoodExpense() {
        String date =new SimpleDateFormat("dd-MM-yyy", Locale.getDefault()).format(new Date());
        MutableDateTime epoch =new MutableDateTime();
        epoch.setDate(0);
        DateTime now = new DateTime();
        Months months=Months.monthsBetween(epoch,now);
        String itemMonth = "Food"+months.getMonths();
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("expenses").child(onlineUserId);
        Query query= reference.orderByChild("itemMonth").equalTo(itemMonth);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    int totalAmount = 0;
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Map<String, Object> map = (Map<String, Object>) ds.getValue();
                        Object total = map.get("amount");
                        int pTotal = Integer.parseInt(String.valueOf(total));
                        totalAmount += pTotal;
                        analyticsFoodAmount.setText("Spent: ₹" + String.valueOf(totalAmount));
                    }
                    personalRef.child("monthlyFood").setValue(totalAmount);
                } else {
                    layoutFood.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getTotalTransportExpense() {
        String date =new SimpleDateFormat("dd-MM-yyy", Locale.getDefault()).format(new Date());
        MutableDateTime epoch =new MutableDateTime();
        epoch.setDate(0);
        DateTime now = new DateTime();
        Months months=Months.monthsBetween(epoch,now);
        String itemMonth = "Transport"+months.getMonths();
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("expenses").child(onlineUserId);
        Query query= reference.orderByChild("itemMonth").equalTo(itemMonth);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    int totalAmount = 0;
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Map<String, Object> map = (Map<String, Object>) ds.getValue();
                        Object total = map.get("amount");
                        int pTotal = Integer.parseInt(String.valueOf(total));
                        totalAmount += pTotal;
                        analyticsTransportAmount.setText("Spent: ₹" + String.valueOf(totalAmount));
                    }
                    personalRef.child("monthlyTransport").setValue(totalAmount);
                } else {
                    layoutTransport.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
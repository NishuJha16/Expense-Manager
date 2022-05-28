package com.example.expensemanager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.TimePicker;
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

import java.text.SimpleDateFormat;
import java.time.Month;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.TimerTask;


public class MainActivity extends AppCompatActivity {

    private CardView budgetCardView;
    private CardView todayCardView;
    private Toolbar toolbar;
    private CardView weekCardView;
    private CardView monthCardView;
    private CardView analyticsCardView, historyCardView,anyChartViewLayout;

    private TextView budgetTv, todayTv, monthTv, weekTv, savingsTv;
    private AnyChartView anyChartView;

    private FirebaseAuth mAuth;
    private String onlineUserId = "";
    private DatabaseReference expenseRef, personalRef, budgetRef;
    private ImageView cancelAlertSuccess;
    private CardView cardViewAlertSuccess;
    private TableLayout tableLayout;

    private int totalAmountMonth = 0;
    private int totalAmountBudget = 0;
    private int totalAmountBudgetC = 0;
    private int totalAmountBudgetD = 0;
    private Pie pie;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        budgetCardView=findViewById(R.id.budgetCardView);
        todayCardView=findViewById(R.id.todayCardView);
        weekCardView=findViewById(R.id.weekCardView);
        monthCardView=findViewById(R.id.monthCardView);
        analyticsCardView=findViewById(R.id.analyticsCardView);
        historyCardView=findViewById(R.id.historyCardView);

        anyChartView=findViewById(R.id.anyChartView);
        anyChartViewLayout=findViewById(R.id.anyChartViewLayout);

        tableLayout=findViewById(R.id.table);

        budgetTv=findViewById(R.id.budgetTv);
        todayTv=findViewById(R.id.todayTv);
        monthTv=findViewById(R.id.monthTv);
        weekTv=findViewById(R.id.weekTv);
        savingsTv=findViewById(R.id.savingsTv);
        cardViewAlertSuccess=findViewById(R.id.cardViewSuccess);
        cancelAlertSuccess=findViewById(R.id.cancelAlertSuccess);

        mAuth = FirebaseAuth.getInstance();
        onlineUserId = mAuth.getCurrentUser().getUid();
        expenseRef= FirebaseDatabase.getInstance().getReference("expenses").child(onlineUserId);
        personalRef= FirebaseDatabase.getInstance().getReference("personal").child(onlineUserId);
        budgetRef= FirebaseDatabase.getInstance().getReference().child(mAuth.getCurrentUser().getUid()).child("budget");

        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Home");

        ProgressDialog progressDialog=new ProgressDialog(MainActivity.this);
        progressDialog.setMessage("Loading..");
        progressDialog.show();

        cancelAlertSuccess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cardViewAlertSuccess.setVisibility(View.GONE);
            }
        });


        budgetCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,BudgetActivity.class));
            }
        });
        todayCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,TodaySpendingActivity.class));
            }
        });
        weekCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    Intent intent=new Intent(MainActivity.this,WeekSpendingActivity.class);
                    intent.putExtra("type","week");
                startActivity(intent);
            }
        });

        monthCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MainActivity.this,WeekSpendingActivity.class);
                intent.putExtra("type","month");
                startActivity(intent);
            }
        });

        analyticsCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MainActivity.this,ChooseAnalyticsActivity.class);
                startActivity(intent);
            }
        });

        historyCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MainActivity.this,HistoryActivity.class);
                startActivity(intent);
            }
        });

        budgetRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists() && snapshot.getChildrenCount()>0){
                    for(DataSnapshot ds: snapshot.getChildren()){
                        Map<String, Object> map= (Map<String, Object>)ds.getValue();
                        Object total = map.get("amount");
                        int pTotal = Integer.parseInt(String.valueOf(total));
                        totalAmountBudgetD += pTotal;
                    }
                    totalAmountBudgetC = totalAmountBudgetD;
                    anyChartViewLayout.setVisibility(View.VISIBLE);
                    tableLayout.setVisibility(View.VISIBLE);
                    cardViewAlertSuccess.setVisibility(View.GONE);
                    progressDialog.dismiss();
                } else {
                    personalRef.child("budget").setValue(0);
                    Toast.makeText(MainActivity.this,"Please set a Budget",Toast.LENGTH_LONG).show();
                    anyChartViewLayout.setVisibility(View.GONE);
                    tableLayout.setVisibility(View.GONE);
                    cardViewAlertSuccess.setVisibility(View.VISIBLE);
                    progressDialog.dismiss();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        getBudgetAmount();
        getTodaySpentAmount();
        getWeekSpentAmount();
        getMonthSpentAmount();
        getSavings();
        NotificationChannel();
        //myAlarm();

        new java.util.Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                loadGraph();

            }
        },2000);

    }

    private  void loadGraph() {
        personalRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    int saving;
                    if(snapshot.hasChild("saving")){
                        saving=Integer.parseInt(snapshot.child("saving").getValue().toString());
                    } else {
                        saving=0;
                    }

                    int spent;
                    if(snapshot.hasChild("month")){
                        spent=Integer.parseInt(snapshot.child("month").getValue().toString());
                    } else {
                        spent=0;
                    }

                    pie = AnyChart.pie();
                    List<DataEntry> data= new ArrayList<>();
                    Log.i("savings",String.valueOf(saving));
                    data.add(new ValueDataEntry("Savings",saving));
                    data.add(new ValueDataEntry("Spent",spent));

                    pie.data(data);
                    pie.title("Month Stats");
                    pie.labels().position("outside");
                    pie.legend().title().enabled(false);
                    pie.legend().title()
                            .text("")
                            .padding(0d,0d,10d,0d);

                    pie.legend()
                            .position("center-bottom")
                            .itemsLayout(LegendLayout.HORIZONTAL)
                            .align(Align.CENTER);
                    anyChartView.setChart(pie);

                } else {
                    Toast.makeText(MainActivity.this, "Child does not exists",Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getSavings() {
        personalRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    int budget;
                    if(snapshot.hasChild("budget")){
                        budget= Integer.parseInt(snapshot.child("budget").getValue().toString());

                    } else {
                        budget=0;
                    }

                    int monthSpending;
                    if(snapshot.hasChild("month")){
                        monthSpending= Integer.parseInt(Objects.requireNonNull(snapshot.child("month").getValue().toString()));
                    } else {
                        monthSpending=0;
                    }

                    int savings = budget - monthSpending;
                    savingsTv.setText("₹"+ savings);
                    personalRef.child("saving").setValue(savings);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getTodaySpentAmount() {
        String date =new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        MutableDateTime epoch =new MutableDateTime();
        epoch.setDate(0);
        DateTime now = new DateTime();
        Weeks weeks=Weeks.weeksBetween(epoch,now);
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("expenses").child(onlineUserId);
        Query query= reference.orderByChild("date").equalTo(date);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int totalAmount = 0;
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Map<String, Object> map = (Map<String, Object>) ds.getValue();
                    Object total = map.get("amount");
                    int pTotal = Integer.parseInt(String.valueOf(total));
                    totalAmount += pTotal;
                    todayTv.setText("₹" + String.valueOf(totalAmount));
                }
                personalRef.child("today").setValue(totalAmount);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getMonthSpentAmount() {
        String date =new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        MutableDateTime epoch =new MutableDateTime();
        epoch.setDate(0);
        DateTime now = new DateTime();
        Months month= Months.monthsBetween(epoch,now);
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("expenses").child(onlineUserId);
        Query query= reference.orderByChild("month").equalTo(month.getMonths());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int totalAmount = 0;
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Map<String, Object> map = (Map<String, Object>) ds.getValue();
                    Object total = map.get("amount");
                    int pTotal = Integer.parseInt(String.valueOf(total));
                    totalAmount += pTotal;
                    monthTv.setText("₹" + String.valueOf(totalAmount));
                }
                personalRef.child("month").setValue(totalAmount);
                totalAmountMonth = totalAmount;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getWeekSpentAmount() {
        String date =new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        MutableDateTime epoch =new MutableDateTime();
        epoch.setDate(0);
        DateTime now = new DateTime();
        Weeks weeks=Weeks.weeksBetween(epoch,now);
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("expenses").child(onlineUserId);
        Query query= reference.orderByChild("week").equalTo(weeks.getWeeks());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int totalAmount = 0;
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Map<String, Object> map = (Map<String, Object>) ds.getValue();
                    Object total = map.get("amount");
                    int pTotal = Integer.parseInt(String.valueOf(total));
                    totalAmount += pTotal;
                    weekTv.setText("₹" + String.valueOf(totalAmount));
                }
                personalRef.child("week").setValue(totalAmount);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getBudgetAmount() {
        budgetRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists() && snapshot.getChildrenCount()>0){
                    totalAmountBudget=0;
                    for(DataSnapshot ds: snapshot.getChildren()){
                        Map<String, Object> map= (Map<String, Object>)ds.getValue();
                        Object total = map.get("amount");
                        int pTotal = Integer.parseInt(String.valueOf(total));
                        totalAmountBudget += pTotal;
                        Log.i("Budget", String.valueOf(totalAmountBudget));
                        budgetTv.setText("₹"+ String.valueOf(totalAmountBudget) );

                    }
                    personalRef.child("budget").setValue(totalAmountBudget);
                } else {
                    totalAmountBudget=0;
                    budgetTv.setText("₹"+ String.valueOf(totalAmountBudget) );
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater=getMenuInflater();
        menuInflater.inflate(R.menu.menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()== R.id.account) {
            Intent intent=new Intent(MainActivity.this, AccountActivity.class);
            startActivity(intent);
        }
        if (item.getItemId() == R.id.notification) {
            final Calendar c = Calendar.getInstance();
            int mHour = c.get(Calendar.HOUR_OF_DAY);
            int mMinute = c.get(Calendar.MINUTE);

            TimePickerDialog tpd = new TimePickerDialog(MainActivity.this, //same Activity Context like before
                    new TimePickerDialog.OnTimeSetListener() {

                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay,
                                              int minute) {
                            Log.i("time",hourOfDay+":"+minute);
                            personalRef.child("notificationHour").setValue(hourOfDay);
                            personalRef.child("notificationMinute").setValue(minute);
                            myAlarm(hourOfDay,minute);
                        }
                    }, mHour, mMinute, false);
            tpd.setMessage("Please select a time to get Reminder!!");
            tpd.show();

        }
        if(item.getItemId()==R.id.logout){
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("Expense Manager")
                    .setMessage("Are you sure you want to logout?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                       @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            FirebaseAuth.getInstance().signOut();
                            Intent intent=new Intent(MainActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    })
                    .setNegativeButton("No",null)
                    .show();
        }
        return super.onOptionsItemSelected(item);
    }

    public void myAlarm(int hour, int minute) {

        Calendar calendar = Calendar.getInstance();

//        personalRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if(snapshot.exists() && snapshot.hasChild("notificationHour") && snapshot.hasChild("notificationMinute")){
//                    calendar.set(Calendar.HOUR_OF_DAY, (Integer) snapshot.child("notificationHour").getValue());
//                    calendar.set(Calendar.MINUTE, (Integer) snapshot.child("notificationMinute").getValue());
//                    Log.i("hour",snapshot.child("notificationMinute").getValue().toString());
//                } else {
//                    calendar.set(Calendar.HOUR_OF_DAY, 22);
//                    calendar.set(Calendar.MINUTE, 0);
//                    Log.i("hour","22");
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);

        if (calendar.getTime().compareTo(new Date()) < 0)
            calendar.add(Calendar.DAY_OF_MONTH, 1);

        Intent intent = new Intent(getApplicationContext(), NotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),pendingIntent);
        }

    }

    private void NotificationChannel() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            CharSequence name="Expense Manager";
            String description="Reminder!! Please set your budget";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel= new NotificationChannel("Notification",name,importance);
            channel.setDescription(description);

            NotificationManager notificationManager=getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
package com.example.expensemanager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class WeekSpendingActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TextView totalWeekAmount;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private ProgressDialog loader;

    private WeekSpendingAdapter weekSpendingAdapter;
    private List<Data> myDataList;

    private FirebaseAuth mAuth;
    private String onlineUserId="";
    private DatabaseReference expenseRef;

    private String type="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_week_spending);

        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        progressBar=findViewById(R.id.progressBar);
        totalWeekAmount=findViewById(R.id.totalWeekAmount);
        recyclerView=findViewById(R.id.recyclerView);
        loader= new ProgressDialog(this);

        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        mAuth=FirebaseAuth.getInstance();
        onlineUserId=mAuth.getCurrentUser().getUid();
        expenseRef= FirebaseDatabase.getInstance().getReference("expenses").child(onlineUserId);

        myDataList=new ArrayList<>();
        weekSpendingAdapter = new WeekSpendingAdapter(WeekSpendingActivity.this, myDataList);
        recyclerView.setAdapter(weekSpendingAdapter);

        if(getIntent().getExtras()!= null){
            type= getIntent().getStringExtra("type");
            if(type.equals("week")){
                getSupportActionBar().setTitle("Week's Spending");
                readWeekSpendingItems();
            } else if(type.equals("month")){
                getSupportActionBar().setTitle("Month's Spending");
                readMonthSpendingItems();
            }
        }


    }

    private void readMonthSpendingItems() {
        MutableDateTime epoch =new MutableDateTime();
        epoch.setDate(0);
        DateTime now = new DateTime();
        Months months= Months.monthsBetween(epoch,now);
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("expenses").child(onlineUserId);
        Query query= reference.orderByChild("month").equalTo(months.getMonths());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                myDataList.clear();
                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    Data data= dataSnapshot.getValue(Data.class);
                    myDataList.add(data);
                }
                weekSpendingAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);

                int totalAmount=0;
                for(DataSnapshot ds:snapshot.getChildren()){
                    Map<String,Object> map=(Map<String,Object>)ds.getValue();
                    Object total = map.get("amount");
                    int pTotal= Integer.parseInt(String.valueOf(total));
                    totalAmount += pTotal;

                    totalWeekAmount.setText("Total Month's Spending: ₹"+totalAmount);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void readWeekSpendingItems() {
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
                myDataList.clear();
                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    Data data= dataSnapshot.getValue(Data.class);
                    myDataList.add(data);
                }
                weekSpendingAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);

                int totalAmount=0;
                for(DataSnapshot ds:snapshot.getChildren()){
                    Map<String,Object> map=(Map<String,Object>)ds.getValue();
                    Object total = map.get("amount");
                    int pTotal= Integer.parseInt(String.valueOf(total));
                    totalAmount += pTotal;

                    totalWeekAmount.setText("Total Week's Spending: ₹"+totalAmount);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


}
package com.example.expensemanager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

public class HistoryActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener{

    private RecyclerView recyclerView;
    private Toolbar toolbar;

    private FirebaseAuth mAuth;
    private ProgressDialog loader;
    private ProgressBar progressBar;
    private String onlineUserId="";
    private DatabaseReference expenseRef;

    private TextView historyTotalAmountSpent;

    private TodayItemsAdapter todayItemsAdapter;
    private List<Data> myDataList;
    private Button search;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Today's Spending");

        recyclerView=findViewById(R.id.recyclerView);
        progressBar= findViewById(R.id.progressBar);
        search=findViewById(R.id.search);
        imageView=findViewById(R.id.imageView);

        historyTotalAmountSpent=findViewById(R.id.historyTotalAmountSpent);

        mAuth=FirebaseAuth.getInstance();
        onlineUserId=mAuth.getCurrentUser().getUid();
        expenseRef= FirebaseDatabase.getInstance().getReference("expenses").child(onlineUserId);

        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        myDataList=new ArrayList<>();
        todayItemsAdapter=new TodayItemsAdapter(HistoryActivity.this,myDataList);
        recyclerView.setAdapter(todayItemsAdapter);

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog();
            }
        });
    }

    private void showDatePickerDialog() {
        DatePickerDialog datePickerDialog= new DatePickerDialog(
                this,
                this,
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
        imageView.setVisibility(View.GONE);
        myDataList.clear();
        int months = month+1;
        String date= (dayOfMonth<9?"0":"")+dayOfMonth+"-"+(months<9?"0":"")+months+"-"+year;
        Log.i("date",date);

        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("expenses").child(onlineUserId);
        Query query= reference.orderByChild("date").equalTo(date);
        Log.i("query", String.valueOf(query));
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.i("snapshot", String.valueOf(snapshot));
                if(snapshot.exists() && snapshot.getChildrenCount()>0) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Data data = ds.getValue(Data.class);
                        myDataList.add(data);
                    }
                    todayItemsAdapter.notifyDataSetChanged();
                    recyclerView.setVisibility(View.VISIBLE);


                    int totalAmount = 0;
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Map<String, Object> map = (Map<String, Object>) ds.getValue();
                        Object total = map.get("amount");
                        int pTotal = Integer.parseInt(String.valueOf(total));
                        totalAmount += pTotal;
                        if (totalAmount > 0) {
                            historyTotalAmountSpent.setVisibility(View.VISIBLE);
                            historyTotalAmountSpent.setText("Date: "+date+"\nYou spent: ₹" + totalAmount);
                        }
                    }
                } else {
                    historyTotalAmountSpent.setVisibility(View.VISIBLE);
                    historyTotalAmountSpent.setText("You have not spent money on "+date);
                    recyclerView.setVisibility(View.GONE);
                }
                }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(HistoryActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }
}
package com.example.expensemanager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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

public class TodaySpendingActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TextView totalSpentAmountTv;
    private RecyclerView recyclerView;
    private FloatingActionButton fab;
    private ProgressBar progressBar;
    private ProgressDialog loader;

    private FirebaseAuth mAuth;
    private String onlineUserId="";
    private DatabaseReference expenseRef;

    private TodayItemsAdapter todayItemsAdapter;
    private List<Data> myDataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_today_spending);

        fab= findViewById(R.id.fab);
        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Today's Spending");

        progressBar=findViewById(R.id.progressBar);
        totalSpentAmountTv=findViewById(R.id.totalSpentAmount);
        recyclerView=findViewById(R.id.recyclerView);
        loader= new ProgressDialog(this);

        mAuth=FirebaseAuth.getInstance();
        onlineUserId=mAuth.getCurrentUser().getUid();
        expenseRef= FirebaseDatabase.getInstance().getReference("expenses").child(onlineUserId);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addItemSpentOn();
            }
        });

        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        myDataList= new ArrayList<>();
        todayItemsAdapter= new TodayItemsAdapter(TodaySpendingActivity.this,myDataList);
        recyclerView.setAdapter(todayItemsAdapter);

        readItems();

    }

    private void readItems() {
        String date =new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("expenses").child(onlineUserId);
        Query query= reference.orderByChild("date").equalTo(date);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                myDataList.clear();
                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    Data data= dataSnapshot.getValue(Data.class);
                    myDataList.add(data);
                    Log.i("data", String.valueOf(data));
                }
                todayItemsAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);

                int totalAmount=0;
                for(DataSnapshot ds:snapshot.getChildren()){
                    Map<String,Object> map=(Map<String,Object>)ds.getValue();
                    Object total = map.get("amount");
                    int pTotal= Integer.parseInt(String.valueOf(total));
                    totalAmount += pTotal;

                    totalSpentAmountTv.setText("Total Day's Spending: ₹"+totalAmount);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void addItemSpentOn(){
        AlertDialog.Builder myDialog = new AlertDialog.Builder(this);
        LayoutInflater inflater= LayoutInflater.from(this);
        View myView = inflater.inflate(R.layout.input_layout, null);
        myDialog.setView(myView);

        final AlertDialog dialog = myDialog.create();
        dialog.setCancelable(true);

        final Spinner itemSpinner = myView.findViewById(R.id.itemsSpinner);
        final EditText amount= myView.findViewById(R.id.amount);
        final EditText note= myView.findViewById(R.id.note);
        final Button cancel =  myView.findViewById(R.id.cancel);
        final Button save =  myView.findViewById(R.id.save);

        note.setVisibility(View.VISIBLE);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String spentAmount = amount.getText().toString();
                String spentItem = itemSpinner.getSelectedItem().toString();
                String notes=note.getText().toString();
                if(TextUtils.isEmpty(spentAmount))
                {
                    amount.setError("Amount is required");
                    return;
                }
                if(TextUtils.isEmpty(notes))
                {
                    note.setError("Note is required");
                    return;
                }
                if(spentItem.equals("Select item")){
                    Toast.makeText(TodaySpendingActivity.this, "Select a valid Item", Toast.LENGTH_SHORT).show();
                }

                else {
                    loader.setMessage("Adding a Budget Item");
                    loader.setCanceledOnTouchOutside(false);
                    loader.show();

                    String id = expenseRef.push().getKey();
                    String date =new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());

                    MutableDateTime epoch =new MutableDateTime();
                    epoch.setDate(0);
                    DateTime now = new DateTime();
                    Weeks weeks=Weeks.weeksBetween(epoch,now);
                    Months months= Months.monthsBetween(epoch,now);
                    String itemDay = spentItem+date;
                    String itemWeek = spentItem+weeks.getWeeks();
                    String itemMonth = spentItem+months.getMonths();


                    Data data =  new Data(spentItem, date, id,itemDay,itemWeek,itemMonth, notes, Integer.parseInt(spentAmount), months.getMonths(),weeks.getWeeks());
                    expenseRef.child(id).setValue(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(TodaySpendingActivity.this, "Expense Added Successfully", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(TodaySpendingActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                            }
                            loader.dismiss();
                        }
                    });

                }
                dialog.dismiss();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }
}
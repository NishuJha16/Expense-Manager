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
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.anychart.AnyChartView;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
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
import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

public class BudgetActivity extends AppCompatActivity {

    private FloatingActionButton fab;
    private DatabaseReference budgetRef,personalRef;
    private TextView totalBudgetAmountTv;
    private RecyclerView recyclerView;
    private AnyChartView anyChartView;
    private Toolbar toolbar;
    private LinearLayout noSpentLayout;

    private FirebaseAuth mAuth;
    private ProgressDialog loader;
    private ProgressBar progressBar;

    private String post_key = "";
    private String item="";
    private int amount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_budget);
        fab= findViewById(R.id.fab);
        totalBudgetAmountTv=findViewById(R.id.totalBudgetAmountTv);
        recyclerView=findViewById(R.id.recyclerView);
        progressBar=findViewById(R.id.progressBar);
        toolbar=findViewById(R.id.toolbar);
        anyChartView=findViewById(R.id.anyChartView);
        noSpentLayout=findViewById(R.id.noSpentLayout);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Budget");


        mAuth= FirebaseAuth.getInstance();
        personalRef =FirebaseDatabase.getInstance().getReference("personal").child(mAuth.getCurrentUser().getUid());
        budgetRef= FirebaseDatabase.getInstance().getReference().child(mAuth.getCurrentUser().getUid()).child("budget");
        loader= new ProgressDialog(this);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addItem();
            }
        });
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        budgetRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && snapshot.getChildrenCount() > 0) {
                    int totalAmount = 0;

                    for (DataSnapshot snap : snapshot.getChildren()) {
                        Data data = snap.getValue(Data.class);
                        totalAmount += data.getAmount();
                        String sTotal = String.valueOf("Month budget: ₹" + totalAmount);
                        totalBudgetAmountTv.setText(sTotal);
                        if(totalAmount==0){
                            progressBar.setVisibility(View.GONE);
                            noSpentLayout.setVisibility(View.VISIBLE);
                        }
                    }
                    float weeklyBudget = totalAmount / (float)4;
                    float dailyBudget = totalAmount / (float)30;
                    personalRef.child("budget").setValue(totalAmount);
                    personalRef.child("weeklyBudget").setValue(weeklyBudget);
                    personalRef.child("dailyBudget").setValue(dailyBudget);
                } else {
                    personalRef.child("budget").setValue(0);
                    personalRef.child("weeklyBudget").setValue(0);
                    personalRef.child("dailyBudget").setValue(0);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        getMonthTransportBudgetRatios();
        getMonthFoodBudgetRatios();
        getMonthHouseBudgetRatios();
        getMonthEntertainmentBudgetRatios();
        getMonthEducationBudgetRatios();
        getMonthCharityBudgetRatios();
        getMonthApparelBudgetRatios();
        getMonthHealthBudgetRatios();
        getMonthPersonalBudgetRatios();
        getMonthOtherBudgetRatios();

    }

    private void getMonthOtherBudgetRatios() {
        Query query = budgetRef.orderByChild("item").equalTo("Other");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    int pTotal = 0;
                    for(DataSnapshot ds: snapshot.getChildren()){
                        Map<String, Object> map= (Map<String, Object>)ds.getValue();
                        Object total = map.get("amount");
                        pTotal += Integer.parseInt(String.valueOf(total));
                    }
                    float dayOtherRatio = pTotal/(float)30;
                    float weekOtherRatio = pTotal/(float)4;
                    float monthOtherRatio = pTotal;

                    personalRef.child("dayOtherRatio").setValue(dayOtherRatio);
                    personalRef.child("weekOtherRatio").setValue(weekOtherRatio);
                    personalRef.child("monthOtherRatio").setValue(monthOtherRatio);
                    //Log.i("otherRatio",String.valueOf(dayOtherRatio));
                } else {
                    personalRef.child("dayOtherRatio").setValue(0);
                    personalRef.child("weekOtherRatio").setValue(0);
                    personalRef.child("monthOtherRatio").setValue(0);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getMonthPersonalBudgetRatios() {
        Query query = budgetRef.orderByChild("item").equalTo("Personal");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    int pTotal = 0;
                    for(DataSnapshot ds: snapshot.getChildren()){
                        Map<String, Object> map= (Map<String, Object>)ds.getValue();
                        Object total = map.get("amount");
                        pTotal += Integer.parseInt(String.valueOf(total));
                    }
                    float dayPersonalRatio = pTotal/(float)30;
                    float weekPersonalRatio = pTotal/(float)4;
                    float monthPersonalRatio = pTotal;

                    personalRef.child("dayPersonalRatio").setValue(dayPersonalRatio);
                    personalRef.child("weekPersonalRatio").setValue(weekPersonalRatio);
                    personalRef.child("monthPersonalRatio").setValue(monthPersonalRatio);

                } else {
                    personalRef.child("dayPersonalRatio").setValue(0);
                    personalRef.child("weekPersonalRatio").setValue(0);
                    personalRef.child("monthPersonalRatio").setValue(0);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getMonthHealthBudgetRatios() {
        Query query = budgetRef.orderByChild("item").equalTo("Health");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    int pTotal = 0;
                    for(DataSnapshot ds: snapshot.getChildren()){
                        Map<String, Object> map= (Map<String, Object>)ds.getValue();
                        Object total = map.get("amount");
                        pTotal += Integer.parseInt(String.valueOf(total));
                    }
                    float dayHealthRatio = pTotal/(float)30;
                    float weekHealthRatio = pTotal/(float)4;
                    float monthHealthRatio = pTotal;

                    personalRef.child("dayHealthRatio").setValue(dayHealthRatio);
                    personalRef.child("weekHealthRatio").setValue(weekHealthRatio);
                    personalRef.child("monthHealthRatio").setValue(monthHealthRatio);

                } else {
                    personalRef.child("dayHealthRatio").setValue(0);
                    personalRef.child("weekHealthRatio").setValue(0);
                    personalRef.child("monthHealthRatio").setValue(0);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getMonthApparelBudgetRatios() {
        Query query = budgetRef.orderByChild("item").equalTo("Apparel");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    int pTotal = 0;
                    for(DataSnapshot ds: snapshot.getChildren()){
                        Map<String, Object> map= (Map<String, Object>)ds.getValue();
                        Object total = map.get("amount");
                        pTotal += Integer.parseInt(String.valueOf(total));
                    }
                    float dayApparelRatio = pTotal/(float)30;
                    float weekApparelRatio = pTotal/(float)4;
                    float monthApparelRatio = pTotal;

                    personalRef.child("dayApparelRatio").setValue(dayApparelRatio);
                    personalRef.child("weekApparelRatio").setValue(weekApparelRatio);
                    personalRef.child("monthApparelRatio").setValue(monthApparelRatio);

                } else {
                    personalRef.child("dayApparelRatio").setValue(0);
                    personalRef.child("weekApparelRatio").setValue(0);
                    personalRef.child("monthApparelRatio").setValue(0);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getMonthCharityBudgetRatios() {
        Query query = budgetRef.orderByChild("item").equalTo("Charity");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    int pTotal = 0;
                    for(DataSnapshot ds: snapshot.getChildren()){
                        Map<String, Object> map= (Map<String, Object>)ds.getValue();
                        Object total = map.get("amount");
                        pTotal += Integer.parseInt(String.valueOf(total));
                    }
                    float dayCharityRatio = pTotal/(float)30;
                    float weekCharityRatio = pTotal/(float)4;
                    float monthCharityRatio = pTotal;

                    personalRef.child("dayCharityRatio").setValue(dayCharityRatio);
                    personalRef.child("weekCharityRatio").setValue(weekCharityRatio);
                    personalRef.child("monthCharityRatio").setValue(monthCharityRatio);

                } else {
                    personalRef.child("dayCharityRatio").setValue(0);
                    personalRef.child("weekCharityRatio").setValue(0);
                    personalRef.child("monthCharityRatio").setValue(0);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getMonthEducationBudgetRatios() {
        Query query = budgetRef.orderByChild("item").equalTo("Education");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    int pTotal = 0;
                    for(DataSnapshot ds: snapshot.getChildren()){
                        Map<String, Object> map= (Map<String, Object>)ds.getValue();
                        Object total = map.get("amount");
                        pTotal += Integer.parseInt(String.valueOf(total));
                    }
                    float dayEducationRatio = pTotal/(float)30;
                    float weekEducationRatio = pTotal/(float)4;
                    float monthEducationRatio = pTotal;

                    personalRef.child("dayEducationRatio").setValue(dayEducationRatio);
                    personalRef.child("weekEducationRatio").setValue(weekEducationRatio);
                    personalRef.child("monthEducationRatio").setValue(monthEducationRatio);

                } else {
                    personalRef.child("dayEducationRatio").setValue(0);
                    personalRef.child("weekEducationRatio").setValue(0);
                    personalRef.child("monthEducationRatio").setValue(0);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getMonthEntertainmentBudgetRatios() {
        Query query = budgetRef.orderByChild("item").equalTo("Entertainment");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    int pTotal = 0;
                    for(DataSnapshot ds: snapshot.getChildren()){
                        Map<String, Object> map= (Map<String, Object>)ds.getValue();
                        Object total = map.get("amount");
                        pTotal += Integer.parseInt(String.valueOf(total));
                    }
                    float dayEntertainmentRatio = pTotal/(float)30;
                    float weekEntertainmentRatio = pTotal/(float)4;
                    float monthEntertainmentRatio = pTotal;

                    personalRef.child("dayEntertainmentRatio").setValue(dayEntertainmentRatio);
                    personalRef.child("weekEntertainmentRatio").setValue(weekEntertainmentRatio);
                    personalRef.child("monthEntertainmentRatio").setValue(monthEntertainmentRatio);

                } else {
                    personalRef.child("dayEntertainmentRatio").setValue(0);
                    personalRef.child("weekEntertainmentRatio").setValue(0);
                    personalRef.child("monthEntertainmentRatio").setValue(0);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getMonthHouseBudgetRatios() {
        Query query = budgetRef.orderByChild("item").equalTo("House");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    int pTotal = 0;
                    for(DataSnapshot ds: snapshot.getChildren()){
                        Map<String, Object> map= (Map<String, Object>)ds.getValue();
                        Object total = map.get("amount");
                        pTotal += Integer.parseInt(String.valueOf(total));
                    }
                    float dayHouseRatio = pTotal/(float)30;
                    float weekHouseRatio = pTotal/(float)4;
                    float monthHouseRatio = pTotal;

                    personalRef.child("dayHouseRatio").setValue(dayHouseRatio);
                    personalRef.child("weekHouseRatio").setValue(weekHouseRatio);
                    personalRef.child("monthHouseRatio").setValue(monthHouseRatio);

                } else {
                    personalRef.child("dayHouseRatio").setValue(0);
                    personalRef.child("weekHouseRatio").setValue(0);
                    personalRef.child("monthHouseRatio").setValue(0);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getMonthFoodBudgetRatios() {
        Query query = budgetRef.orderByChild("item").equalTo("Food");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    int pTotal = 0;
                    for(DataSnapshot ds: snapshot.getChildren()){
                        Map<String, Object> map= (Map<String, Object>)ds.getValue();
                        Object total = map.get("amount");
                        pTotal += Integer.parseInt(String.valueOf(total));
                    }
                    float dayFoodRatio = pTotal/(float)30;
                    float weekFoodRatio = pTotal/(float)4;
                    float monthFoodRatio = pTotal;

                    personalRef.child("dayFoodRatio").setValue(dayFoodRatio);
                    personalRef.child("weekFoodRatio").setValue(weekFoodRatio);
                    personalRef.child("monthFoodRatio").setValue(monthFoodRatio);

                } else {
                    personalRef.child("dayFoodRatio").setValue(0);
                    personalRef.child("weekFoodRatio").setValue(0);
                    personalRef.child("monthFoodRatio").setValue(0);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getMonthTransportBudgetRatios() {
        Query query = budgetRef.orderByChild("item").equalTo("Transport");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    int pTotal = 0;
                    for(DataSnapshot ds: snapshot.getChildren()){
                        Map<String, Object> map= (Map<String, Object>)ds.getValue();
                        Object total = map.get("amount");
                        pTotal += Integer.parseInt(String.valueOf(total));
                    }
                    float dayTransportRatio = pTotal/(float)30;
                    float weekTransportRatio = pTotal/(float)4;
                    float monthTransportRatio = pTotal;

                    personalRef.child("dayTransportRatio").setValue(dayTransportRatio);
                    personalRef.child("weekTransportRatio").setValue(weekTransportRatio);
                    personalRef.child("monthTransportRatio").setValue(monthTransportRatio);

                } else {
                    personalRef.child("dayTransportRatio").setValue(0);
                    personalRef.child("weekTransportRatio").setValue(0);
                    personalRef.child("monthTransportRatio").setValue(0);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void addItem(){
        AlertDialog.Builder myDialog = new AlertDialog.Builder(this);
        LayoutInflater inflater= LayoutInflater.from(this);
        View myView = inflater.inflate(R.layout.input_layout, null);
        myDialog.setView(myView);

        final AlertDialog dialog = myDialog.create();
        dialog.setCancelable(true);

        final Spinner itemSpinner = myView.findViewById(R.id.itemsSpinner);
        final EditText amount= myView.findViewById(R.id.amount);
        final Button cancel =  myView.findViewById(R.id.cancel);
        final Button save =  myView.findViewById(R.id.save);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String budgetAmount = amount.getText().toString();
                String budgetItem = itemSpinner.getSelectedItem().toString();
                if(TextUtils.isEmpty(budgetAmount))
                {
                    amount.setError("Amount is required");
                    return;
                }
                if(budgetItem.equals("Select item")){
                    Toast.makeText(BudgetActivity.this, "Select a valid Item", Toast.LENGTH_SHORT).show();
                }
                else {
                    loader.setMessage("Adding a Budget Item");
                    loader.setCanceledOnTouchOutside(false);
                    loader.show();

                    String id = budgetRef.push().getKey();
                    String date =new SimpleDateFormat("dd-MM-yyy", Locale.getDefault()).format(new Date());

                    MutableDateTime epoch =new MutableDateTime();
                    epoch.setDate(0);
                    DateTime now = new DateTime();
                    Weeks weeks=Weeks.weeksBetween(epoch,now);
                    Months months= Months.monthsBetween(epoch,now);

                    String itemDay = budgetItem+date;
                    String itemWeek = budgetItem+weeks.getWeeks();
                    String itemMonth = budgetItem+months.getMonths();

                    Data data =  new Data(budgetItem, date, id, itemDay,itemWeek,itemMonth,null, Integer.parseInt(budgetAmount), months.getMonths(),weeks.getWeeks());
                    budgetRef.child(budgetItem).setValue(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(BudgetActivity.this, "Budget Item Added Successfully", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(BudgetActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
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

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Data> options = new FirebaseRecyclerOptions.Builder<Data>()
        .setQuery(budgetRef, Data.class).build();

        FirebaseRecyclerAdapter<Data, MyViewHolder> adapter= new FirebaseRecyclerAdapter<Data, MyViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull MyViewHolder holder, int position, @NonNull Data model) {
                holder.setItemAmount("₹"+model.getAmount() );
                holder.setItemDate("Date: "+model.getDate() );
                holder.setItemName(""+model.getItem() );
                holder.notes.setVisibility(View.GONE);

                switch(model.getItem()){
                    case "Transport":
                        holder.imageView.setImageResource(R.drawable.transport);
                        break;
                    case "Food":
                        holder.imageView.setImageResource(R.drawable.food);
                        break;
                    case "House":
                        holder.imageView.setImageResource(R.drawable.home);
                        break;
                    case "Entertainment":
                        holder.imageView.setImageResource(R.drawable.entertainment);
                        break;
                    case "Education":
                        holder.imageView.setImageResource(R.drawable.education);
                        break;
                    case "Charity":
                        holder.imageView.setImageResource(R.drawable.charity);
                        break;
                    case "Apparel":
                        holder.imageView.setImageResource(R.drawable.apparel);
                        break;
                    case "Health":
                        holder.imageView.setImageResource(R.drawable.health);
                        break;
                    case "Personal":
                        holder.imageView.setImageResource(R.drawable.personal);
                        break;
                    case "Other":
                        holder.imageView.setImageResource(R.drawable.other);
                        break;
                }

                holder.view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        post_key=getRef(holder.getAbsoluteAdapterPosition()).getKey();
                        item=model.getItem();
                        amount= model.getAmount();
                        updateData();
                    }
                });
                progressBar.setVisibility(View.GONE);
            }

            @NonNull
            @Override
            public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.retrieve_layout,parent,false);
                return new MyViewHolder(view);
            }
        };

        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        View view;
        public ImageView imageView;
        public TextView notes;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            view=itemView;
            imageView=itemView.findViewById(R.id.imageView);
            notes= itemView.findViewById(R.id.note);
        }

        public void setItemName(String itemName){
            TextView item = itemView.findViewById(R.id.item);
            item.setText(itemName);
        }
        public void setItemAmount(String itemAmount){
            TextView item = itemView.findViewById(R.id.amount);
            item.setText(itemAmount);
        }
        public void setItemDate(String amount){
            TextView item = itemView.findViewById(R.id.date);
            item.setText(amount);
        }

    }

    private void updateData(){
        AlertDialog.Builder myDialog = new AlertDialog.Builder(this);
        LayoutInflater inflater= LayoutInflater.from(this);
        View view= inflater.inflate(R.layout.update_layout, null);

        myDialog.setView(view);
        final AlertDialog dialog=myDialog.create();

        final TextView mItem =view.findViewById(R.id.itemName);
        final EditText mAmount = view.findViewById(R.id.amount);
        final EditText mNotes = view.findViewById(R.id.note);

        mNotes.setVisibility(View.GONE);
        mItem.setText(item);
        mAmount.setText(String.valueOf(amount));
        mAmount.setSelection(String.valueOf(amount).length());

        Button deleteBtn = view.findViewById(R.id.btnDelete);
        Button updateBtn = view.findViewById(R.id.btnUpdate);

        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                amount = Integer.parseInt(mAmount.getText().toString());
                String id = budgetRef.push().getKey();
                String date =new SimpleDateFormat("dd-MM-yyy", Locale.getDefault()).format(new Date());

                MutableDateTime epoch =new MutableDateTime();
                epoch.setDate(0);
                DateTime now = new DateTime();
                Months months= Months.monthsBetween(epoch,now);
                Weeks weeks=Weeks.weeksBetween(epoch,now);

                String itemDay = item+date;
                String itemWeek = item+weeks.getWeeks();
                String itemMonth = item+months.getMonths();


                Data data =  new Data(item, date, post_key,itemDay,itemWeek,itemMonth, null, amount, months.getMonths(),weeks.getWeeks());
                budgetRef.child(post_key).setValue(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(BudgetActivity.this, "Updated Successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(BudgetActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                dialog.dismiss();
            }
        });

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                budgetRef.child(post_key).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(BudgetActivity.this, "Deleted Successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(BudgetActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                dialog.dismiss();
            }
        });

        dialog.show();
    }
}
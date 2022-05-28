package com.example.expensemanager;

public class Data {

    String item,date,id,itemDay, itemWeek, itemMonth,notes;
    int amount, month,week;

    public Data() {}

    public Data(String item, String date, String id, String itemDay, String itemWeek, String itemMonth, String notes, int amount, int month, int week) {
        this.item = item;
        this.date = date;
        this.id = id;
        this.itemDay = itemDay;
        this.itemWeek = itemWeek;
        this.itemMonth = itemMonth;
        this.notes = notes;
        this.amount = amount;
        this.month = month;
        this.week = week;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getItemDay() {
        return itemDay;
    }

    public void setItemDay(String itemDay) {
        this.itemDay = itemDay;
    }

    public String getItemWeek() {
        return itemWeek;
    }

    public void setItemWeek(String itemWeek) {
        this.itemWeek = itemWeek;
    }

    public String getItemMonth() {
        return itemMonth;
    }

    public void setItemMonth(String itemMonth) {
        this.itemMonth = itemMonth;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getWeek() {
        return week;
    }

    public void setWeek(int week) {
        this.week = week;
    }
}

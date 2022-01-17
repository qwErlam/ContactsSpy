package com.example.contactsspy;

import android.util.Log;

import java.util.List;

public class ContactInfo {
    public String name;
    public List<String> numbers;

    public ContactInfo(String name){
        this.name = name;
    }

    public void printToLog(){
        Log.d("myLogs", "----------------------------------------------------");
        Log.d("myLogs", "Contact name: " + this.name + "with numbers: ");
        for(String number : numbers)
            Log.d("myLogs", "\t\t" + number);
    };
}

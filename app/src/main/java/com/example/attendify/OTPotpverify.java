package com.example.attendify;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.chaos.view.PinView;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONObject;

import java.text.DateFormat;
import java.util.Calendar;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class OTPotpverify extends AppCompatActivity {

    private PinView pinView;
    private Button proceed;
    private HashMap<String, Object> studentMap;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    public static String lectureInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_o_t_potpverify);
        getSupportActionBar().hide();
        studentMap = new HashMap<>();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Students");
        loadDatabaseFromFirebase();
        //studentMap = loadMap();
        /*if (studentMap.size() == 0) {
            Log.v("CSVCode","IN");
            CSVParser csvParser = new CSVParser();
            studentMap = csvParser.readCSV(this);
            databaseReference.updateChildren(studentMap);
            saveMap(studentMap);
        }*/

        pinView = findViewById(R.id.pin_view);
        proceed = findViewById(R.id.proceed);
        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar c = Calendar.getInstance();
                int mnts = c.get(Calendar.MINUTE);
                int a;
                if (mnts <= 5) {
                    a = 1;
                } else {
                    a = 0;
                }
                //A is a variable to check weather you are late or not
                if (a == 1) {
                    String rollNo = pinView.getText().toString();
                    Log.v("OTPotpverify", "Roll Number is" + rollNo);
                    if (performCheck(rollNo)) {
                        Log.v("OTPotpverify", "Roll Number Found");
                        Map<String, Object> lectureAttended = new HashMap<>();
                        lectureAttended.put("lectureAttended", 1);
                        databaseReference.child(rollNo).child(lectureInfo).updateChildren(lectureAttended);
                        proceedToMarkAttendance(rollNo);

                    } else {
                        Log.v("OTPotpverify", "Roll Number Not Found");
                        Toast.makeText(getApplicationContext(), "Please enter a valid Roll Number", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    startActivity(new Intent(OTPotpverify.this, failerror.class));
                }
            }
        });
    }

    private void loadDatabaseFromFirebase() {
        HashMap<String, Object> hashMap = new HashMap<>();
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot d : snapshot.getChildren()) {
                    Log.v("Firebase",snapshot.toString());
                    HashMap<String, Object> tempHashMap = (HashMap<String, Object>) d.getValue();
                    String rollNo = (String) tempHashMap.get("rollNo");
                    String name = (String) tempHashMap.get("name");
                    String phoneNumber = (String) tempHashMap.get("phoneNumber");
                    Student student = new Student(rollNo, name, phoneNumber);
                    hashMap.put(rollNo, student);
                }
                studentMap = hashMap;
                Log.v("studentMap", String.valueOf(studentMap.size()));
                Log.v("HashMap", String.valueOf(hashMap.size()));
                startOperations();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void startOperations()
    {
        Calendar c = Calendar.getInstance();
        int hours = c.get(Calendar.HOUR_OF_DAY);
        Log.v("Hour", String.valueOf(hours));
        if (hours >= 8 && hours <= 17) {
            lectureInfo = "Lecture - " + hours + ":00 - " + c.get(Calendar.DAY_OF_MONTH) + "-" + c.get(Calendar.MONTH) + "-" + c.get(Calendar.YEAR);
            Log.v("lectureInfo",lectureInfo);
            DatabaseReference student;
            Log.v("studentMap before loop", String.valueOf(studentMap.size()));
            for (String rollNo : studentMap.keySet()) {
                Log.v("Loop","In");
                student = databaseReference.child(rollNo);
                DatabaseReference lectureReference = student.child(lectureInfo);
                Log.v("lectureKey",lectureReference.getKey());
                lectureReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.getChildrenCount() == 0) {
                            Log.v("Snapshot",String.valueOf(snapshot.getChildrenCount()));
                            HashMap<String, Integer> lectureAttended = new HashMap<>();
                            lectureAttended.put("lectureAttended", 0);
                            lectureReference.setValue(lectureAttended);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        }
    }
    private boolean performCheck(String rollNo) {
        for (String checkString : studentMap.keySet()) {
            if (checkString.equals(rollNo))
                return true;
        }
        return false;
    }

    private void proceedToMarkAttendance(String rollNo) {
        Student student = (Student) studentMap.get(rollNo);
        String phoneNumber = student.getPhoneNumber();
        Intent intent = new Intent(OTPotpverify.this, OTPFinalHaiYE.class);
        intent.putExtra("rollNo", rollNo);
        intent.putExtra("phoneNumber", phoneNumber);
        startActivity(intent);
        pinView.setText("");
    }

    private void saveMap(HashMap<String, Object> inputMap) {
        SharedPreferences pSharedPref = getApplicationContext().getSharedPreferences("students", Context.MODE_PRIVATE);
        if (pSharedPref != null) {
            JSONObject jsonObject = new JSONObject(inputMap);
            String jsonString = jsonObject.toString();
            SharedPreferences.Editor editor = pSharedPref.edit();
            editor.remove("rollNo").commit();
            editor.putString("rollNo", jsonString);
            editor.commit();
        }
    }

    private HashMap<String, Object> loadMap() {
        HashMap<String, Object> outputMap = new HashMap<>();
        SharedPreferences pSharedPref = getApplicationContext().getSharedPreferences("students", Context.MODE_PRIVATE);
        try {
            if (pSharedPref != null) {
                String jsonString = pSharedPref.getString("rollNo", (new JSONObject()).toString());
                JSONObject jsonObject = new JSONObject(jsonString);
                Iterator<String> keysItr = jsonObject.keys();
                while (keysItr.hasNext()) {
                    String key = keysItr.next();
                    Student value = (Student) jsonObject.get(key);
                    outputMap.put(key, value);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return outputMap;
    }


}
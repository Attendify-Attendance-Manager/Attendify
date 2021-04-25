package com.example.attendify;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;

public class CSVParser {
    public HashMap<String,Object> readCSV(Context context)
    {
        HashMap<String,Object> studentMap = new HashMap<>();
        InputStream is = context.getResources().openRawResource(R.raw.students_test);
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(is, Charset.forName("UTF-8")));
        String line = "";

        try {
            while ((line = reader.readLine()) != null) {
                // Split the line into different tokens (using the comma as a separator).
                String[] tokens = line.split(",");

                // Read the data and store it in the WellData POJO.
                Student student = new Student();
                student.setRollNo(tokens[0]);
                student.setName(tokens[1]);
                student.setPhoneNumber(tokens[2]);

                Log.d("MainActivity" ,"Just Created " + student.getRollNo()+" "+student.getName()+" "+student.getPhoneNumber());
                studentMap.put(student.getRollNo(),student);
            }
        } catch (IOException e1) {
            Log.e("CSVParser", "Error" + line, e1);
            e1.printStackTrace();
        }
        return studentMap;
    }
}

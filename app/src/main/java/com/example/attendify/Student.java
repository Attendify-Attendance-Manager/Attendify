package com.example.attendify;

public class Student {
    private String rollNo;
    private String name;
    private String phoneNumber;

    public Student()
    {

    }
    public Student(String rollNo, String name, String phoneNumber) {
        this.rollNo = rollNo;
        this.name = name;
        this.phoneNumber = phoneNumber;
    }

    public String getRollNo() {
        return rollNo;
    }

    public void setRollNo(String rollNo) {
        this.rollNo = rollNo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
